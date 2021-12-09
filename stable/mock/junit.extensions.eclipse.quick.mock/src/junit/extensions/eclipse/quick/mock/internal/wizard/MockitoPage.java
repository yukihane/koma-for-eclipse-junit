package junit.extensions.eclipse.quick.mock.internal.wizard;

import junit.extensions.eclipse.quick.mock.internal.MockitoEntry;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class MockitoPage extends WizardPage implements IClasspathContainerPage, IClasspathContainerPageExtension {

    private final MockitoEntry entry;
    private IJavaProject project;

    public MockitoPage() {
        super("mockitoPage"); //$NON-NLS-1$
        setTitle("Mockito Library"); //$NON-NLS-1$
        setDescription(Messages.MockitoPage_AddMockitoLibrary);
        entry = new MockitoEntry();
    }

    @Override
    public boolean finish() {
        try {
            final IJavaProject[] javaProjects = new IJavaProject[] { project };
            final IClasspathContainer[] containers = { null };
            JavaCore.setClasspathContainer(entry.getContainerPath(), javaProjects, containers, null);
        } catch (final JavaModelException e) {
            return false;
        }

        return true;
    }

    @Override
    public IClasspathEntry getSelection() {
        return entry.getContainer();
    }

    @Override
    public void setSelection(final IClasspathEntry containerEntry) {
    }

    @Override
    public void createControl(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(1, false));

        final Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
            false));
        label.setText(Messages.MockitoPage_AddedLabel);
        setControl(composite);
    }

    @Override
    public void initialize(final IJavaProject project,
        final IClasspathEntry[] currentEntries) {
        this.project = project;
    }

}
