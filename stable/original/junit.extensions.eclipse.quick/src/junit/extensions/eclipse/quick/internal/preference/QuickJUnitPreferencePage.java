package junit.extensions.eclipse.quick.internal.preference;

import junit.extensions.eclipse.quick.NamingRules;
import junit.extensions.eclipse.quick.internal.QuickJUnitPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class QuickJUnitPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private NamingRules namingRules;
    private NamingRulesPreference namingRulesPreference;

    public QuickJUnitPreferencePage() {
        setPreferenceStore(QuickJUnitPlugin.getDefault().getPreferenceStore());
    }

    @Override
    public void init(final IWorkbench workbench) {
        final IPreferenceStore store = getPreferenceStore();
        namingRules = new NamingRules(store);
        namingRulesPreference = new NamingRulesPreference(this);
    }

    @Override
    protected Control createContents(final Composite parent) {
        // TODO implements source folder feature.
        //	    Composite sourceFolderComposite = new Composite(parent, SWT.NULL);
        //	    GridLayout sfLayout = new GridLayout(2, false);
        //        sfLayout.marginHeight = 0;
        //        sfLayout.marginWidth = 0;
        //	    sourceFolderComposite.setLayout(sfLayout);
        //	    GridData sfGData = new GridData(GridData.FILL_HORIZONTAL);
        //	    sourceFolderComposite.setLayoutData(sfGData);
        //	    Label label = new Label(sourceFolderComposite, SWT.NONE);
        //	    label.setText("testCase source folder;");
        //	    Text text = new Text(sourceFolderComposite, SWT.BORDER);
        //	    GridData textGData = new GridData(GridData.FILL_HORIZONTAL);
        //	    text.setLayoutData(textGData);
        //	    text.setText("${project}/src");

        final Composite composite = new Composite(parent, SWT.NULL);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        final GridData data = new GridData(GridData.FILL_BOTH);
        composite.setLayoutData(data);

        namingRulesPreference.create(namingRules.get(), composite);
        Dialog.applyDialogFont(composite);
        return composite;
    }

    GridData getButtonGridData(final Button button) {
        final GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        final int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        gd.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
        return gd;
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        namingRulesPreference.setValue(namingRules.getDefault());
    }

    @Override
    public boolean performOk() {
        namingRules.set(namingRulesPreference.getValue());
        return true;
    }
}
