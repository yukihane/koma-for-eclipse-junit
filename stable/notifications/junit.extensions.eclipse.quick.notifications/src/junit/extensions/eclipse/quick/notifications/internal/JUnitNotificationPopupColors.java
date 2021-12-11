package junit.extensions.eclipse.quick.notifications.internal;

import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jface.resource.DeviceResourceException;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Helper Class to create the colors for the {@link JUnitNotificationPopup}.
 * <p>
 * Note: Copied from FormColors of UI Forms.
 * </p>
 *
 * @author Benjamin Pasero (initial contribution from RSSOwl, see bug 177974)
 * @author Mik Kersten
 */
public class JUnitNotificationPopupColors {

    private final Display display;

    private Color titleText;

    private Color gradientBegin;

    private Color gradientEnd;

    private Color border;

    private final ResourceManager resourceManager;

    private final JUnitNotification notification;

    private Color baseColor;

    public JUnitNotificationPopupColors(final Display display, final ResourceManager resourceManager,
        final JUnitNotification notification) {
        this.display = display;
        this.resourceManager = resourceManager;
        this.notification = notification;
        createColors();
    }

    private void createColors() {
        computeBaseColor();
        createBorderColor();
        createGradientColors();
        // previously used SWT.COLOR_TITLE_INACTIVE_FOREGROUND, but too light on Windows XP
        titleText = getColor(resourceManager, getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
    }

    private void computeBaseColor() {
        final Result result = (Result) notification.getAdapter(Result.class);
        if (Result.OK.equals(result)) {
            baseColor = display.getSystemColor(SWT.COLOR_GREEN);
        } else {
            baseColor = display.getSystemColor(SWT.COLOR_RED);
        }
    }

    public Color getGradientBegin() {
        return gradientBegin;
    }

    public Color getGradientEnd() {
        return gradientEnd;
    }

    public Color getBorder() {
        return border;
    }

    public Color getTitleText() {
        return titleText;
    }

    private void createBorderColor() {
        RGB tbBorder = baseColor.getRGB();
        final RGB bg = getImpliedBackground().getRGB();

        // Group 1
        // Rule: If at least 2 of the RGB values are equal to or between 180 and
        // 255, then apply specified opacity for Group 1
        // Examples: Vista, XP Silver, Wn High Con #2
        // Keyline = TITLE_BACKGROUND @ 70% Opacity over LIST_BACKGROUND
        if (testTwoPrimaryColors(tbBorder, 179, 256)) {
            tbBorder = blend(tbBorder, bg, 70);
        } else if (testTwoPrimaryColors(tbBorder, 120, 180)) {
            tbBorder = blend(tbBorder, bg, 50);
        } else {
            tbBorder = blend(tbBorder, bg, 30);
        }

        border = getColor(resourceManager, tbBorder);
    }

    private void createGradientColors() {
        final RGB titleBg = baseColor.getRGB();
        final Color bgColor = getImpliedBackground();
        final RGB bg = bgColor.getRGB();
        RGB bottom, top;

        // Group 1
        // Rule: If at least 2 of the RGB values are equal to or between 180 and
        // 255, then apply specified opacity for Group 1
        // Examples: Vista, XP Silver, Wn High Con #2
        // Gradient Bottom = TITLE_BACKGROUND @ 30% Opacity over LIST_BACKGROUND
        // Gradient Top = TITLE BACKGROUND @ 0% Opacity over LIST_BACKGROUND
        if (testTwoPrimaryColors(titleBg, 179, 256)) {
            bottom = blend(titleBg, bg, 30);
            top = bg;
        }

        // Group 2
        // Rule: If at least 2 of the RGB values are equal to or between 121 and
        // 179, then apply specified opacity for Group 2
        // Examples: XP Olive, OSX Graphite, Linux GTK, Wn High Con Black
        // Gradient Bottom = TITLE_BACKGROUND @ 20% Opacity over LIST_BACKGROUND
        // Gradient Top = TITLE BACKGROUND @ 0% Opacity over LIST_BACKGROUND
        else if (testTwoPrimaryColors(titleBg, 120, 180)) {
            bottom = blend(titleBg, bg, 20);
            top = bg;
        }

        // Group 3
        // Rule: If at least 2 of the RGB values are equal to or between 0 and
        // 120, then apply specified opacity for Group 3
        // Examples: XP Default, Wn Classic Standard, Wn Marine, Wn Plum, OSX
        // Aqua, Wn High Con White, Wn High Con #1
        // Gradient Bottom = TITLE_BACKGROUND @ 10% Opacity over LIST_BACKGROUND
        // Gradient Top = TITLE BACKGROUND @ 0% Opacity over LIST_BACKGROUND
        else {
            bottom = blend(titleBg, bg, 10);
            top = bg;
        }

        gradientBegin = getColor(resourceManager, top);
        gradientEnd = getColor(resourceManager, bottom);
    }

    private RGB blend(final RGB c1, final RGB c2, final int ratio) {
        final int r = blend(c1.red, c2.red, ratio);
        final int g = blend(c1.green, c2.green, ratio);
        final int b = blend(c1.blue, c2.blue, ratio);
        return new RGB(r, g, b);
    }

    private int blend(final int v1, final int v2, final int ratio) {
        final int b = (ratio * v1 + (100 - ratio) * v2) / 100;
        return Math.min(255, b);
    }

    private boolean testTwoPrimaryColors(final RGB rgb, final int from, final int to) {
        int total = 0;
        if (testPrimaryColor(rgb.red, from, to)) {
            total++;
        }
        if (testPrimaryColor(rgb.green, from, to)) {
            total++;
        }
        if (testPrimaryColor(rgb.blue, from, to)) {
            total++;
        }
        return total >= 2;
    }

    private boolean testPrimaryColor(final int value, final int from, final int to) {
        return value > from && value < to;
    }

    private RGB getSystemColor(final int code) {
        return getDisplay().getSystemColor(code).getRGB();
    }

    private Color getImpliedBackground() {
        return display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    }

    private Display getDisplay() {
        return display;
    }

    private Color getColor(final ResourceManager manager, final RGB rgb) {
        try {
            return manager.createColor(rgb);
        } catch (final DeviceResourceException e) {
            return manager.getDevice().getSystemColor(SWT.COLOR_BLACK);
        }
    }
}