package junit.extensions.eclipse.quick.internal.launch;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.extensions.eclipse.quick.internal.ExtensionSupport;
import junit.extensions.eclipse.quick.internal.QuickJUnitPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.junit.launcher.ITestKind;
import org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfigurationConstants;
import org.eclipse.jdt.internal.junit.launcher.TestKindRegistry;
import org.eclipse.jdt.internal.junit.ui.JUnitMessages;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * Copied from super class.
 */
public class QuickJUnitLaunchShortcut extends JUnitLaunchShortcut {

    private static final String EMPTY_STRING = "";

    @Override
    public void launch(final ISelection selection, final String mode) {
        if (selection instanceof IStructuredSelection) {
            launch(((IStructuredSelection) selection).toArray(), mode);
        } else {
            showNoTestsFoundDialog();
        }
    }

    private void launch(final Object[] elements, final String mode) {
        try {
            IJavaElement elementToLaunch = null;

            if (elements.length == 1) {
                Object selected = elements[0];
                if (!(selected instanceof IJavaElement) && selected instanceof IAdaptable) {
                    selected = ((IAdaptable) selected).getAdapter(IJavaElement.class);
                }
                if (selected instanceof IJavaElement) {
                    final IJavaElement element = (IJavaElement) selected;
                    switch (element.getElementType()) {
                        case IJavaElement.JAVA_PROJECT:
                        case IJavaElement.PACKAGE_FRAGMENT_ROOT:
                        case IJavaElement.PACKAGE_FRAGMENT:
                        case IJavaElement.TYPE:
                        case IJavaElement.METHOD:
                            elementToLaunch = element;
                            break;
                        case IJavaElement.CLASS_FILE:
                            elementToLaunch = ((IClassFile) element).getType();
                            break;
                        case IJavaElement.COMPILATION_UNIT:
                            elementToLaunch = findTypeToLaunch((ICompilationUnit) element, mode);
                            break;
                    }
                }
            }
            if (elementToLaunch == null) {
                showNoTestsFoundDialog();
                return;
            }
            performLaunch(elementToLaunch, mode);
        } catch (final InterruptedException e) {
            // OK, silently move on
        } catch (final CoreException e) {
            getPlugin().logSystemError(e, this);
        } catch (final InvocationTargetException e) {
            getPlugin().logSystemError(e, this);
        }
    }

    private void performLaunch(final IJavaElement element, final String mode)
        throws InterruptedException, CoreException {
        final ILaunchConfigurationWorkingCopy temporary = createLaunchConfiguration(element);
        ILaunchConfiguration config = findExistingLaunchConfiguration(temporary, mode);
        if (config == null) {
            // no existing found: create a new one
            final ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(temporary.getType());
            ILaunchConfiguration qjDefault;
            for (int i = 0; i < configs.length; i++) {
                if (ExtensionSupport.QUICK_JUNIT_DEFAULT.equals(configs[i].getName())) {
                    qjDefault = configs[i];
                    final Map attributes = qjDefault.getAttributes();
                    setDefaultAttributes(temporary, attributes);
                }
            }
            config = temporary.doSave();
        }
        DebugUITools.launch(config, mode);
    }

    private static final Set KEY_SET = new HashSet();
    static {
        KEY_SET.add(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS);
    }

    private void setDefaultAttributes(
        final ILaunchConfigurationWorkingCopy temporary, final Map attributes) {
        final Iterator itr = attributes.keySet().iterator();
        for (Object obj = null; itr.hasNext();) {
            obj = itr.next();
            final String key = (String) obj;
            boolean contains = false;
            try {
                contains = attributes.containsKey(key);
            } catch (final NullPointerException e) {
                continue;
            }
            if (contains && KEY_SET.contains(key)) {
                final Object object = attributes.get(key);
                if (object != null) {
                    final String value = object.toString();
                    temporary.setAttribute(key, value);
                }
            }
        }
    }

    private ILaunchConfiguration findExistingLaunchConfiguration(final ILaunchConfigurationWorkingCopy temporary,
        final String mode)
        throws InterruptedException, CoreException {
        final List candidateConfigs = findExistingLaunchConfigurations(temporary);

        // If there are no existing configs associated with the IType, create
        // one.
        // If there is exactly one config associated with the IType, return it.
        // Otherwise, if there is more than one config associated with the
        // IType, prompt the
        // user to choose one.
        final int candidateCount = candidateConfigs.size();
        if (candidateCount == 0) {
            return null;
        } else if (candidateCount == 1) {
            return (ILaunchConfiguration) candidateConfigs.get(0);
        } else {
            // Prompt the user to choose a config. A null result means the user
            // cancelled the dialog, in which case this method returns null,
            // since cancelling the dialog should also cancel launching
            // anything.
            final ILaunchConfiguration config = chooseConfiguration(candidateConfigs, mode);
            if (config != null) {
                return config;
            }
        }
        return null;
    }

    private ILaunchConfiguration chooseConfiguration(final List configList, final String mode)
        throws InterruptedException {
        final IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
        final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
        dialog.setElements(configList.toArray());
        dialog.setTitle(JUnitMessages.JUnitLaunchShortcut_message_selectConfiguration);
        if (mode.equals(ILaunchManager.DEBUG_MODE)) {
            dialog.setMessage(JUnitMessages.JUnitLaunchShortcut_message_selectDebugConfiguration);
        } else {
            dialog.setMessage(JUnitMessages.JUnitLaunchShortcut_message_selectRunConfiguration);
        }
        dialog.setMultipleSelection(false);
        final int result = dialog.open();
        if (result == Window.OK) {
            return (ILaunchConfiguration) dialog.getFirstResult();
        }
        throw new InterruptedException(); // cancelled by user
    }

    private List findExistingLaunchConfigurations(final ILaunchConfigurationWorkingCopy temporary)
        throws CoreException {
        final ILaunchConfigurationType configType = temporary.getType();

        final ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
        final String[] attributeToCompare = getAttributeNamesToCompare();

        final ArrayList candidateConfigs = new ArrayList(configs.length);
        for (int i = 0; i < configs.length; i++) {
            final ILaunchConfiguration config = configs[i];
            if (hasSameAttributes(config, temporary, attributeToCompare)) {
                candidateConfigs.add(config);
            }
        }
        return candidateConfigs;
    }

    @Override
    protected String[] getAttributeNamesToCompare() {
        return new String[] {
            IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, JUnitLaunchConfigurationConstants.ATTR_TEST_CONTAINER,
            IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
            JUnitLaunchConfigurationConstants.ATTR_TEST_METHOD_NAME
        };
    }

    private static boolean hasSameAttributes(final ILaunchConfiguration config1, final ILaunchConfiguration config2,
        final String[] attributeToCompare) {
        try {
            for (int i = 0; i < attributeToCompare.length; i++) {
                final String val1 = config1.getAttribute(attributeToCompare[i], EMPTY_STRING);
                final String val2 = config2.getAttribute(attributeToCompare[i], EMPTY_STRING);
                if (!val1.equals(val2)) {
                    return false;
                }
            }
            return true;
        } catch (final CoreException e) {
            // ignore access problems here, return false
        }
        return false;
    }

    private IType findTypeToLaunch(final ICompilationUnit cu, final String mode)
        throws InterruptedException, InvocationTargetException {
        final IType[] types = findTypesToLaunch(cu);
        if (types.length == 0) {
            return null;
        } else if (types.length > 1) {
            return chooseType(types, mode);
        }
        return types[0];
    }

    private IType chooseType(final IType[] types, final String mode) throws InterruptedException {
        final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(),
            new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_POST_QUALIFIED));
        dialog.setElements(types);
        dialog.setTitle(JUnitMessages.JUnitLaunchShortcut_dialog_title2);
        if (mode.equals(ILaunchManager.DEBUG_MODE)) {
            dialog.setMessage(JUnitMessages.JUnitLaunchShortcut_message_selectTestToDebug);
        } else {
            dialog.setMessage(JUnitMessages.JUnitLaunchShortcut_message_selectTestToRun);
        }
        dialog.setMultipleSelection(false);
        if (dialog.open() == Window.OK) {
            return (IType) dialog.getFirstResult();
        }
        throw new InterruptedException(); // cancelled by user
    }

    private IType[] findTypesToLaunch(final ICompilationUnit cu)
        throws InterruptedException, InvocationTargetException {
        final ITestKind testKind = TestKindRegistry.getContainerTestKind(cu);
        final Set result = new HashSet();
        final IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(final IProgressMonitor pm) throws InterruptedException, InvocationTargetException {
                try {
                    testKind.getFinder().findTestsInContainer(cu, result, pm);
                } catch (final CoreException e) {
                    throw new InvocationTargetException(e);
                }
            }
        };
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(false, false, runnable);
        return (IType[]) result.toArray(new IType[] {});
    }

    private void showNoTestsFoundDialog() {
        MessageDialog.openInformation(getShell(), JUnitMessages.JUnitLaunchShortcut_dialog_title,
            JUnitMessages.JUnitLaunchShortcut_message_notests);
    }

    private Shell getShell() {
        final IWorkbenchWindow workBenchWindow = getWorkbenchWindow();
        return getActiveShell(workBenchWindow);
    }

    private IWorkbenchWindow getWorkbenchWindow() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    private Shell getActiveShell(final IWorkbenchWindow workBenchWindow) {
        if (workBenchWindow == null)
            return null;
        return workBenchWindow.getShell();
    }

    private static ILaunchManager getLaunchManager() {
        return QuickJUnitPlugin.getDefault().getLaunchManager();
    }

    private QuickJUnitPlugin getPlugin() {
        return QuickJUnitPlugin.getDefault();
    }

}
