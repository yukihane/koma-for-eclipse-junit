package junit.extensions.eclipse.quick.mock.internal;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

public class MockitoEntry {

    public static final String CONTAINER_PATH = "junit.extensions.eclipse.quick.mock.MOCKITO_CONTAINER";

    public IPath getPath() {
        final Bundle bundle = Platform.getBundle("org.mockito");
        final URL entry = bundle.getEntry("mockito.jar");
        String fileURL = null;
        try {
            fileURL = URLDecoder.decode(FileLocator.toFileURL(entry).getFile(), "UTF-8");
        } catch (final IOException e) {
        }
        return new Path(fileURL);
    }

    public IClasspathEntry getContainer() {
        final IPath path = getContainerPath();
        final IClasspathEntry entry = JavaCore.newContainerEntry(path);
        return entry;
    }

    public IPath getContainerPath() {
        final IPath path = new Path(CONTAINER_PATH);
        return path;
    }

}
