/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Erich Gamma (erich_gamma@ch.ibm.com) and
 * 	   Kent Beck (kent@threeriversinstitute.org)
 *******************************************************************************/
/*******************************************************************************
 * Erich Gamma、Kent Beck著 小林健一郎訳
 * Eclipseプラグイン開発（Contributing to eclipse）
 * 訳書バージョン
 * オリジナルコードは、Erich Gamma氏、Kent Beck氏による。
 *******************************************************************************/

package org.eclipse.contribution.junit.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameRequestor;
import org.eclipse.jdt.launching.JavaRuntime;

public class TestProject {
    private final IProject project;

    private final IJavaProject javaProject;

    private IPackageFragmentRoot sourceFolder;

    public TestProject() throws CoreException {
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        project = root.getProject("TestProject");
        project.create(null);
        project.open(null);
        javaProject = JavaCore.create(project);

        final IFolder binFolder = createBinFolder();

        setJavaNature();
        javaProject.setRawClasspath(new IClasspathEntry[0], null);

        createOutputFolder(binFolder);
        addSystemLibraries();
    }

    public IProject getProject() {
        return project;
    }

    public IJavaProject getJavaProject() {
        return javaProject;
    }

    public void addJar(final String plugin, final String jar) throws MalformedURLException,
        IOException, JavaModelException {
        final Path result = findFileInPlugin(plugin, jar);
        final IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        final IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
        System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
        newEntries[oldEntries.length] = JavaCore.newLibraryEntry(result, null,
            null);
        javaProject.setRawClasspath(newEntries, null);
    }

    public IPackageFragment createPackage(final String name) throws CoreException {
        if (sourceFolder == null)
            sourceFolder = createSourceFolder();
        return sourceFolder.createPackageFragment(name, false, null);
    }

    public IType createType(final IPackageFragment pack, final String cuName, final String source)
        throws JavaModelException {
        final StringBuilder buf = new StringBuilder();
        buf.append("package " + pack.getElementName() + ";\n");
        buf.append("\n");
        buf.append(source);
        final IProgressMonitor monitor = new NullProgressMonitor();
        final ICompilationUnit cu = pack.createCompilationUnit(cuName,
            buf.toString(), false, monitor);
        return cu.getTypes()[0];
    }

    public void dispose() throws CoreException {
        waitForIndexer();
        project.delete(true, true, null);
    }

    private IFolder createBinFolder() throws CoreException {
        final IFolder binFolder = project.getFolder("bin");
        binFolder.create(false, true, null);
        return binFolder;
    }

    private void setJavaNature() throws CoreException {
        final IProjectDescription description = project.getDescription();
        description.setNatureIds(new String[] { JavaCore.NATURE_ID });
        project.setDescription(description, null);
    }

    private void createOutputFolder(final IFolder binFolder)
        throws JavaModelException {
        final IPath outputLocation = binFolder.getFullPath();
        javaProject.setOutputLocation(outputLocation, null);
    }

    private IPackageFragmentRoot createSourceFolder() throws CoreException {
        final IFolder folder = project.getFolder("src");
        folder.create(false, true, null);
        final IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);

        final IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        final IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
        System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
        newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
        javaProject.setRawClasspath(newEntries, null);
        return root;
    }

    private void addSystemLibraries() throws JavaModelException {
        final IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        final IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
        System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
        newEntries[oldEntries.length] = JavaRuntime
            .getDefaultJREContainerEntry();
        javaProject.setRawClasspath(newEntries, null);
    }

    private Path findFileInPlugin(final String plugin, final String file)
        throws MalformedURLException, IOException {
        final URL pluginURL = Platform.getBundle(plugin).getEntry("/");
        final URL jarURL = new URL(pluginURL, file);
        final URL localJarURL = FileLocator.toFileURL(jarURL);
        return new Path(localJarURL.getPath());

    }

    private void waitForIndexer() throws JavaModelException {
        final TypeNameRequestor nameRequestor = new TypeNameRequestor() {
        };
        new SearchEngine().searchAllTypeNames(
            null,
            SearchPattern.R_EXACT_MATCH,
            null,
            SearchPattern.R_EXACT_MATCH,
            IJavaSearchConstants.CLASS,
            SearchEngine.createJavaSearchScope(new IJavaElement[0]),
            nameRequestor,
            IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
            null);
    }
}