package junit.extensions.eclipse.quick.mock.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class MockitoClasspathContainerInitializer extends
    ClasspathContainerInitializer {

    public MockitoClasspathContainerInitializer() {
    }

    @Override
    public void initialize(final IPath containerPath, final IJavaProject project)
        throws CoreException {
        final IClasspathContainer container = new IClasspathContainer() {

            @Override
            public IPath getPath() {
                return new Path(MockitoEntry.CONTAINER_PATH);
            }

            @Override
            public int getKind() {
                return K_APPLICATION;
            }

            @Override
            public String getDescription() {
                return "Mockito";
            }

            @Override
            public IClasspathEntry[] getClasspathEntries() {
                final MockitoEntry entry = new MockitoEntry();
                final IClasspathEntry[] result = new IClasspathEntry[] {
                    JavaCore.newLibraryEntry(entry.getPath(), null, null)
                };
                return result;
            }
        };
        JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project },
            new IClasspathContainer[] { container }, null);
    }

}
