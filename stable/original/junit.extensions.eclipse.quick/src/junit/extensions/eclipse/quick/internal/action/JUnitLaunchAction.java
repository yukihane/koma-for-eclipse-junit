package junit.extensions.eclipse.quick.internal.action;

import junit.extensions.eclipse.quick.JavaElements;
import junit.extensions.eclipse.quick.internal.Messages;
import junit.extensions.eclipse.quick.internal.QuickJUnitPlugin;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

public class JUnitLaunchAction extends QuickJUnitAction {
    private final String mode;
    private final ILaunchShortcut launchShortcut;

    public JUnitLaunchAction(final ILaunchShortcut launchShortcut, final String mode) {
        this.launchShortcut = launchShortcut;
        this.mode = mode;
    }

    private IJavaElement getTargetElement(final IAction action) throws JavaModelException {
        final IJavaElement element = getSelectedElement();
        if (element == null || element.getElementType() < IJavaElement.COMPILATION_UNIT)
            return element;

        final IJavaElement testableElement = JavaElements.getTestMethodOrClass(element);
        if (testableElement != null)
            return testableElement;

        final IType type = JavaElements.getPrimaryTypeOf(element);
        if (type == null)
            return element;

        openInformation(action, Messages.getString("JUnitLaunchAction.notJUnitElement")); //$NON-NLS-1$
        return null;
    }

    private IJavaElement getSelectedElement() throws JavaModelException {
        final IJavaElement element = getElementOfJavaEditor();
        return element == null ? javaElement : element;
    }

    @Override
    public void run(final IAction action) {
        try {
            final IJavaElement element = getTargetElement(action);
            if (element == null)
                return;
            final ISelection sel = new StructuredSelection(new Object[] { element });
            launchShortcut.launch(sel, mode);
        } catch (final JavaModelException e) {
            QuickJUnitPlugin.getDefault().handleSystemError(e, this);
        }
    }
}
