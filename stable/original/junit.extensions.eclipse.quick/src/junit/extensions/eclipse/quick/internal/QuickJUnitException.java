package junit.extensions.eclipse.quick.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class QuickJUnitException extends CoreException {
    private static final long serialVersionUID = 1L;

    public QuickJUnitException(final Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public QuickJUnitException(final IStatus status) {
        super(status);
    }

    public QuickJUnitException(final String message) {
        this(new Status(IStatus.ERROR, QuickJUnitPlugin.getDefault().getID(), 0, message, null));
    }

    public QuickJUnitException(final String message, final Throwable cause) {
        this(new Status(IStatus.ERROR, QuickJUnitPlugin.getDefault().getID(), 0, message, cause));
    }
}
