package junit.extensions.eclipse.quick.notifications;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "junit.extensions.eclipse.quick.notifications"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        JFaceResources.getColorRegistry().put("TEST_FAILED", new RGB(255, 0, 0));
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        JFaceResources.getColorRegistry().get("TEST_FAILED").dispose();
        super.stop(context);
    }

    @Override
    protected void initializeImageRegistry(final ImageRegistry reg) {
        reg.put(ImageDesc.ERROR.name(), ImageDesc.ERROR.getIamgeDescriptor());
        reg.put(ImageDesc.FAILURE.name(), ImageDesc.FAILURE.getIamgeDescriptor());
        reg.put(ImageDesc.OK.name(), ImageDesc.OK.getIamgeDescriptor());
        reg.put(ImageDesc.ICON.name(), ImageDesc.ICON.getIamgeDescriptor());
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

}
