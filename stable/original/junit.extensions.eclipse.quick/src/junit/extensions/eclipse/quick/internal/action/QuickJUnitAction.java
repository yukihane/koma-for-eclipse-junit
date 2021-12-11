package junit.extensions.eclipse.quick.internal.action;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class QuickJUnitAction implements IEditorActionDelegate, IObjectActionDelegate {
    private Shell shell;
    protected IJavaElement javaElement;
    protected ITextEditor javaEditor;

    @Override
    public void setActiveEditor(final IAction action, final IEditorPart targetEditor) {
        if (!(targetEditor instanceof ITextEditor)) {
            javaEditor = null;
            return;
        }
        javaEditor = (ITextEditor) targetEditor;
        shell = javaEditor.getSite().getShell();
    }

    @Override
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
    }

    @Override
    public void selectionChanged(final IAction action, final ISelection selection) {
        if (!(selection instanceof IStructuredSelection)) {
            javaElement = null;
            return;
        }
        final Object element = ((IStructuredSelection) selection).getFirstElement();
        if (element instanceof IJavaElement)
            javaElement = (IJavaElement) element;
        else
            javaElement = null;
    }

    protected void openInformation(final IAction action, final String message) {
        MessageDialog.openInformation(shell, action.getText(), message);
    }

    protected Shell getShell() {
        return shell;
    }

    protected IJavaElement getElementOfJavaEditor() throws JavaModelException {
        final ICompilationUnit unit = getCompilationUnitOfJavaEditor();
        if (unit == null)
            return null;
        final ISelectionProvider provider = javaEditor.getSelectionProvider();
        final ISelection selection = provider.getSelection();
        if (!(selection instanceof ITextSelection))
            return null;
        final int offset = ((ITextSelection) selection).getOffset();
        final IJavaElement element = unit.getElementAt(offset);
        return element;
    }

    protected ICompilationUnit getCompilationUnitOfJavaEditor() throws JavaModelException {
        if (javaEditor == null)
            return null;
        final IEditorInput input = javaEditor.getEditorInput();
        final IJavaElement element = input.getAdapter(IJavaElement.class);
        if (element instanceof ICompilationUnit)
            return (ICompilationUnit) element;
        return null;
    }
}
