package org.jdesktop.lg3d.utils;

import java.util.*;

/**
 * Theme System - visual themes and skins.
 */
public class ThemeManager {

    private static ThemeManager instance;

    private Theme currentTheme;
    private final Map<String, Theme> themes;
    private ThemeChangeListener listener;

    private ThemeManager() {
        themes = new HashMap<>();
        loadDefaultThemes();
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    private void loadDefaultThemes() {
        themes.put("default", createDefaultTheme());
        themes.put("dark", createDarkTheme());
        themes.put("light", createLightTheme());
        themes.put("blue", createBlueTheme());
        themes.put("green", createGreenTheme());

        currentTheme = themes.get("default");
    }

    public void setTheme(String name) {
        Theme theme = themes.get(name);
        if (theme != null) {
            setTheme(theme);
        }
    }

    public void setTheme(Theme theme) {
        Theme previous = currentTheme;
        currentTheme = theme;

        if (listener != null) {
            listener.themeChanged(previous, currentTheme);
        }
    }

    public Theme getTheme() {
        return currentTheme;
    }

    public Theme getTheme(String name) {
        return themes.get(name);
    }

    public Collection<Theme> getAllThemes() {
        return themes.values();
    }

    public void registerTheme(Theme theme) {
        themes.put(theme.getName(), theme);
    }

    public void setThemeChangeListener(ThemeChangeListener listener) {
        this.listener = listener;
    }

    private Theme createDefaultTheme() {
        Theme theme = new Theme("Default");

        theme.setColor("background", new float[]{0.1f, 0.1f, 0.15f, 1.0f});
        theme.setColor("foreground", new float[]{0.9f, 0.9f, 0.9f, 1.0f});
        theme.setColor("accent", new float[]{0.3f, 0.5f, 0.8f, 1.0f});
        theme.setColor("button", new float[]{0.3f, 0.4f, 0.5f, 1.0f});
        theme.setColor("buttonHover", new float[]{0.4f, 0.5f, 0.6f, 1.0f});
        theme.setColor("buttonPressed", new float[]{0.2f, 0.3f, 0.4f, 1.0f});
        theme.setColor("window", new float[]{0.15f, 0.18f, 0.22f, 1.0f});
        theme.setColor("windowTitle", new float[]{0.25f, 0.3f, 0.4f, 1.0f});
        theme.setColor("border", new float[]{0.3f, 0.35f, 0.4f, 1.0f});
        theme.setColor("text", new float[]{0.9f, 0.9f, 0.9f, 1.0f});
        theme.setColor("textDisabled", new float[]{0.5f, 0.5f, 0.5f, 1.0f});
        theme.setColor("selection", new float[]{0.2f, 0.4f, 0.7f, 1.0f});
        theme.setColor("scrollbar", new float[]{0.25f, 0.28f, 0.32f, 1.0f});

        theme.setFloat("opacity", 1.0f);
        theme.setFloat("cornerRadius", 0.1f);
        theme.setFloat("borderWidth", 0.02f);

        return theme;
    }

    private Theme createDarkTheme() {
        Theme theme = createDefaultTheme();
        theme.setName("Dark");
        theme.setColor("background", new float[]{0.08f, 0.08f, 0.1f, 1.0f});
        theme.setColor("foreground", new float[]{0.85f, 0.85f, 0.85f, 1.0f});
        theme.setColor("window", new float[]{0.12f, 0.12f, 0.15f, 1.0f});
        theme.setColor("button", new float[]{0.2f, 0.2f, 0.25f, 1.0f});
        return theme;
    }

    private Theme createLightTheme() {
        Theme theme = createDefaultTheme();
        theme.setName("Light");
        theme.setColor("background", new float[]{0.95f, 0.95f, 0.95f, 1.0f});
        theme.setColor("foreground", new float[]{0.1f, 0.1f, 0.1f, 1.0f});
        theme.setColor("window", new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        theme.setColor("button", new float[]{0.9f, 0.9f, 0.9f, 1.0f});
        theme.setColor("text", new float[]{0.1f, 0.1f, 0.1f, 1.0f});
        return theme;
    }

    private Theme createBlueTheme() {
        Theme theme = createDefaultTheme();
        theme.setName("Blue");
        theme.setColor("accent", new float[]{0.2f, 0.5f, 0.9f, 1.0f});
        theme.setColor("selection", new float[]{0.15f, 0.4f, 0.8f, 1.0f});
        theme.setColor("button", new float[]{0.2f, 0.4f, 0.7f, 1.0f});
        return theme;
    }

    private Theme createGreenTheme() {
        Theme theme = createDefaultTheme();
        theme.setName("Green");
        theme.setColor("accent", new float[]{0.2f, 0.7f, 0.4f, 1.0f});
        theme.setColor("selection", new float[]{0.15f, 0.6f, 0.35f, 1.0f});
        theme.setColor("button", new float[]{0.2f, 0.5f, 0.3f, 1.0f});
        return theme;
    }
}

/**
 * Theme - visual configuration.
 */
class Theme {

    private String name;
    private final Map<String, float[]> colors;
    private final Map<String, Float> floats;
    private final Map<String, String> strings;

    public Theme(String name) {
        this.name = name;
        this.colors = new HashMap<>();
        this.floats = new HashMap<>();
        this.strings = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float[] getColor(String key) {
        float[] color = colors.get(key);
        return color != null ? color : new float[]{0, 0, 0, 1};
    }

    public void setColor(String key, float[] color) {
        colors.put(key, color);
    }

    public float getFloat(String key) {
        Float value = floats.get(key);
        return value != null ? value : 0f;
    }

    public void setFloat(String key, float value) {
        floats.put(key, value);
    }

    public String getString(String key) {
        return strings.get(key);
    }

    public void setString(String key, String value) {
        strings.put(key, value);
    }
}

interface ThemeChangeListener {
    void themeChanged(Theme oldTheme, Theme newTheme);
}

/**
 * Theme-aware component mixin.
 */
class ThemedComponent {

    private boolean themingEnabled = true;

    public void applyTheme(Theme theme, String componentType) {
        if (!themingEnabled) return;
    }

    public void setThemingEnabled(boolean enabled) {
        this.themingEnabled = enabled;
    }

    public boolean isThemingEnabled() {
        return themingEnabled;
    }
}

/**
 * Style sheet for component styling.
 */
class StyleSheet {

    private final Map<String, Map<String, Object>> rules;

    public StyleSheet() {
        rules = new HashMap<>();
    }

    public void addRule(String selector, Map<String, Object> properties) {
        rules.put(selector, properties);
    }

    public Map<String, Object> getRule(String selector) {
        return rules.get(selector);
    }

    public Map<String, Object> getProperties(String componentType, String state) {
        String key = componentType + ":" + state;
        return rules.get(key);
    }

    public void clear() {
        rules.clear();
    }
}

/**
 * Component states for styling.
 */
class ComponentState {

    public static final String NORMAL = "normal";
    public static final String HOVER = "hover";
    public static final String PRESSED = "pressed";
    public static final String DISABLED = "disabled";
    public static final String FOCUSED = "focused";
    public static final String SELECTED = "selected";
}