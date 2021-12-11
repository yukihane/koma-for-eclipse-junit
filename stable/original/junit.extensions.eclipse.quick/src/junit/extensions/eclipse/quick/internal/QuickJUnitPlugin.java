package junit.extensions.eclipse.quick.internal;

import java.util.Dictionary;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class QuickJUnitPlugin extends AbstractUIPlugin {

    private static QuickJUnitPlugin plugin;

    public QuickJUnitPlugin() {
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

    public static QuickJUnitPlugin getDefault() {
        return plugin;
    }

    public IStatus createSystemErrorStatus(final Exception ex, final Object caller) {
        final int severity = IStatus.ERROR;

        String message;
        message = ex.getMessage();
        if (message == null)
            message = ""; //$NON-NLS-1$
        final MultiStatus errorStatus = new MultiStatus(getID(), severity, message, ex);

        final Dictionary headers = getBundle().getHeaders();

        final String providerName = "" + headers.get(Constants.BUNDLE_VENDOR);
        message = Messages.getString("QuickJUnitPlugin.systemError.providerNameLabel") + providerName; //$NON-NLS-1$
        errorStatus.add(createStatus(severity, message));

        final String pluginName = "" + headers.get(Constants.BUNDLE_NAME);
        message = Messages.getString("QuickJUnitPlugin.systemError.pluginNameLabel") + pluginName; //$NON-NLS-1$
        errorStatus.add(createStatus(severity, message));

        final String pluginId = getBundle().getSymbolicName();
        message = Messages.getString("QuickJUnitPlugin.systemError.pluginIdLabel") + pluginId; //$NON-NLS-1$
        errorStatus.add(createStatus(severity, message));

        final String version = "" + headers.get(Constants.BUNDLE_VERSION);
        message = Messages.getString("QuickJUnitPlugin.systemError.versionLabel") + version; //$NON-NLS-1$
        errorStatus.add(createStatus(severity, message));

        final Class klass = caller instanceof Class ? (Class) caller : caller.getClass();
        message = Messages.getString("QuickJUnitPlugin.systemError.classLabel") + klass.getName(); //$NON-NLS-1$
        errorStatus.add(createStatus(severity, message, IStatus.ERROR, ex));

        return errorStatus;
    }

    public IStatus createStatus(final int severity, final String message) {
        return createStatus(severity, message, 0, null);
    }

    private IStatus createStatus(final int severity, final String message, final int code, final Exception ex) {
        return new Status(severity, getID(), code, message, ex);
    }

    public void handleSystemError(final Exception ex, final Object caller) {
        final IStatus status = createSystemErrorStatus(ex, caller);
        getLog().log(status);
        ErrorDialog.openError((Shell) null, Messages.getString("QuickJUnitPlugin.systemError.dialog.title"), //$NON-NLS-1$
            Messages.getString("QuickJUnitPlugin.systemError.dialog.message"), status); //$NON-NLS-1$
    }

    public void logSystemError(final Exception ex, final Object caller) {
        final IStatus status = createSystemErrorStatus(ex, caller);
        getLog().log(status);
    }

    public void logSystemErrorMessage(final String message, final Object caller) {
        final IStatus status = createSystemErrorStatus(new QuickJUnitException(message), caller);
        getLog().log(status);
    }

    public String getID() {
        return getBundle().getSymbolicName();
    }

    public ILaunchManager getLaunchManager() {
        return DebugPlugin.getDefault().getLaunchManager();
    }

}
