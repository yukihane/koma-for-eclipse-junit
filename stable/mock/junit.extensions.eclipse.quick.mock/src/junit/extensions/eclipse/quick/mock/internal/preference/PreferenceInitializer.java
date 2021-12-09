package junit.extensions.eclipse.quick.mock.internal.preference;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Initializes the default preferences if none exist.
 *
 */
public class PreferenceInitializer implements IStartup {

    private static final String EMPTY = ""; //$NON-NLS-1$
    private static final String SEMI_COLON = ";"; //$NON-NLS-1$

    private final IPreferenceStore jdtPreferenceStore;
    static final String ENABLE_ADDITIONAL_AUTOCOMPLETE_FAVOURTES = "ENABLE_ADDITIONAL_AUTOCOMPLETE_FAVOURTES"; //$NON-NLS-1$

    /**
     * Creates a default preference initializer.
     */
    public PreferenceInitializer() {
        this(PreferenceConstants.getPreferenceStore());
    }

    /**
     * Create a preference initializer with the two preference stores.
     *
     * @param jdtPreferenceStore used by JDT.
     */
    public PreferenceInitializer(final IPreferenceStore jdtPreferenceStore) {
        this.jdtPreferenceStore = jdtPreferenceStore;
    }

    void initializeFavorites() {
        final Set<String> imports = new LinkedHashSet<>(getJDTImports());
        imports.addAll(getDefaultFavorites());
        final String join = join(imports, SEMI_COLON);
        jdtPreferenceStore.setValue(PreferenceConstants.CODEASSIST_FAVORITE_STATIC_MEMBERS, join);
        try {
            ((ScopedPreferenceStore) jdtPreferenceStore).save();
        } catch (final IOException e) {
        }
    }

    /**
     * @return the JDT favorite imports.
     */
    public Set<String> getJDTImports() {
        final String preference = jdtPreferenceStore.getString(PreferenceConstants.CODEASSIST_FAVORITE_STATIC_MEMBERS);
        if (EMPTY.equals(preference.trim())) {
            return new HashSet<>();
        }
        final String[] imports = preference.split(SEMI_COLON);
        return new LinkedHashSet<>(Arrays.asList(imports));
    }

    private LinkedHashSet<String> getDefaultFavorites() {
        final LinkedHashSet<String> orderedSet = new LinkedHashSet<>();
        orderedSet.add(importStatement("org.mockito.Matchers"));
        orderedSet.add(importStatement("org.mockito.Mockito"));
        return orderedSet;
    }

    public void propertyChange(final PropertyChangeEvent event) {
        initializeFavorites();
    }

    private String importStatement(final String clazz) {
        return clazz + ".*"; //$NON-NLS-1$;
    }

    private String join(final Collection<?> toJoin, final String delimiter) {
        if ((toJoin == null) || (toJoin.size() == 0))
            return "";
        final StringBuffer result = new StringBuffer();

        for (final Object object : toJoin) {
            result.append(object);
            result.append(delimiter);
        }

        result.lastIndexOf(delimiter);
        result.replace(result.length() - delimiter.length(), result.length(), ""); //$NON-NLS-1$
        return result.toString();
    }

    @Override
    public void earlyStartup() {
        new PreferenceInitializer().initializeFavorites();
    }

}