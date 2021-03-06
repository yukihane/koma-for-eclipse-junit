package junit.extensions.eclipse.quick.internal;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {

    private static final String BUNDLE_NAME = "junit.extensions.eclipse.quick.internal.messages"; //$NON-NLS-1$
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String getString(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (final MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getString(final String key, final Object[] args) {
        return MessageFormat.format(RESOURCE_BUNDLE.getString(key), args);
    }

    public static String getString(final String key, final Object arg0) {
        return MessageFormat.format(RESOURCE_BUNDLE.getString(key), new Object[] { arg0 });
    }
}
