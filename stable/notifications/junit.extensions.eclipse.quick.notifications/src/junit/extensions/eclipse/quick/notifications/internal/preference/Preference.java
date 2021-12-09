package junit.extensions.eclipse.quick.notifications.internal.preference;

import junit.extensions.eclipse.quick.notifications.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Constants for plug-in preferences
 */
public enum Preference {

    TEMPLATE;

    public String getValue() {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        return store.getString(name());
    }

    public void setValue(final String value) {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(name(), value);
    }

}
