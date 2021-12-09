package junit.extensions.eclipse.quick.mock.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

    private static Activator plugin;

    public Activator() {
        plugin = this;
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    public static Activator getDefault() {
        return Activator.plugin;
    }

}
