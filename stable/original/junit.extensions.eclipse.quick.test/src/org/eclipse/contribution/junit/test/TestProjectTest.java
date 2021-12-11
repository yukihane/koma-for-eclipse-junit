package org.eclipse.contribution.junit.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestProjectTest {

    private TestProject project;
    private IWorkspace workspace;

    @Test
    public void leaning_get_only_JavaDoc() throws Exception {
        assertTrue(project.getProject().exists());
        assertTrue(workspace.getRoot().exists(new Path("TestProject")));
        assertTrue(workspace.getRoot().exists(new Path("TestProject/src/test")));
        assertTrue(workspace.getRoot().exists(new Path("TestProject/src/test/TestClass.java")));
        final IType testClass = project.getJavaProject().findType("test.TestClass");
        assertTrue(testClass.exists());
        final IMethod method = testClass.getMethod("test_test", null);
        assertFalse(method.exists());
        final IMethod doMethod = testClass.getMethod("do_test", null);
        final IAnnotation annotation = doMethod.getAnnotation("org.junit.Test");
        assertTrue(annotation.exists());
        final int length = doMethod.getJavadocRange().getLength();
        System.out.println(doMethod.getSource().substring(0, length));
        createAST(testClass);
    }

    private void createAST(final IType testClass) throws Exception {
        final ASTParser parser = ASTParser.newParser(AST.JLS3);

        parser.setSource(testClass.getCompilationUnit());
        parser.createAST(new NullProgressMonitor());
    }

    @Before
    public void before() throws Exception {
        project = new TestProject();
        project.addJar("org.junit", "junit.jar");
        final IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
            @Override
            public void run(final IProgressMonitor monitor) throws CoreException {
                monitor.beginTask("create test project", 10);
                final IPackageFragment pack = project.createPackage("test");
                monitor.setTaskName("create TestClass");
                project.createType(pack, "TestClass.java",
                    "public class TestClass{\n" +
                        "	/**\n" +
                        "	 *	@see test.TestClass\n" +
                        "	 */\n" +
                        "	@org.junit.Test\n" +
                        "	public void do_test() throws Exception{\n" +
                        "	}\n" +
                        "}\n");
                monitor.setTaskName("create TestClass2");
                project.createType(pack, "TestClass2.java",
                    "public class TestClass2{\n" +
                        "	/**\n" +
                        "	 *	@see test.TestClass\n" +
                        "	 */\n" +
                        "	@org.junit.Test\n" +
                        "	public void do_test() throws Exception{\n" +
                        "	}\n" +
                        "}\n");

                project.getJavaProject().open(monitor);
            }
        };
        workspace = ResourcesPlugin.getWorkspace();
        workspace.run(runnable, null);
        closeIntro();
    }

    private void closeIntro() {
        IWorkbench workbench;
        try {
            workbench = PlatformUI.getWorkbench();
        } catch (final IllegalStateException e) {
            return;
        }
        final IIntroManager introManager = workbench.getIntroManager();
        final IIntroPart intro = introManager.getIntro();
        if (intro != null && introManager.isIntroStandby(intro)) {
            introManager.closeIntro(intro);
        }
    }

    @Test
    public void learning_SearchEngine() throws Exception {
        final SearchEngine engine = new SearchEngine();
        final IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
        final SearchPattern pattern = SearchPattern.createPattern("TestClass2", IJavaSearchConstants.CLASS,
            IJavaSearchConstants.DECLARATIONS, SearchPattern.R_FULL_MATCH);
        final SearchParticipant[] participants = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
        final SearchRequestor requestor = new SearchRequestor() {

            @Override
            public void acceptSearchMatch(final SearchMatch match) throws CoreException {
                final Object element = match.getElement();
                System.out.println(element.getClass().getName());
                System.out.println(element);
            }
        };
        engine.search(pattern, participants, scope, requestor, new NullProgressMonitor());
    }

    @After
    public void after() throws Exception {
        project.dispose();
    }
}
