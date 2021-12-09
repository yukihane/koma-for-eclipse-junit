package junit.extensions.eclipse.quick.process.internal.preferences;

import static junit.extensions.eclipse.quick.process.internal.preferences.Preference.PROCESS;
import static junit.extensions.eclipse.quick.process.internal.preferences.Preference.TEMPLATE;

import junit.extensions.eclipse.quick.process.internal.ProcessActivator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        final IPreferenceStore store = ProcessActivator.getDefault().getPreferenceStore();
        store.setDefault(PROCESS.name(), "/usr/local/bin/growlnotify -n \"Quick JUnit\" -m ${detail} ${summary}"); //$NON-NLS-1$
        store.setDefault(TEMPLATE.name(), "${name} passed:${ok_counts} failure:${fail_counts} Total:${total_counts}"); //$NON-NLS-1$
    }

}
