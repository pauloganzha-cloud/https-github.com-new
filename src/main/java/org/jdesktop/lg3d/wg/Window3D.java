package org.jdesktop.lg3d.wg;

import org.jdesktop.lg3d.sg.*;

/**
 * Window3D - 3D window representation.
 * Core component of the Looking Glass window manager.
 */
public class Window3D extends TransformGroup {

    private String title;
    private boolean visible = true;
    private boolean focusable = true;
    private boolean focused = false;
    private float transparency = 0;
    private Window3D parentWindow;
    private Component3D component;

    public Window3D() {
        super();
    }

    public Window3D(String name) {
        super(name);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public float getTransparency() {
        return transparency;
    }

    public void setTransparency(float transparency) {
        this.transparency = Math.max(0, Math.min(1, transparency));
    }

    public Window3D getParentWindow() {
        return parentWindow;
    }

    public void setParentWindow(Window3D parent) {
        this.parentWindow = parent;
    }

    public Component3D getComponent() {
        return component;
    }

    public void setComponent(Component3D component) {
        this.component = component;
        if (component != null) {
            addChild(component);
        }
    }

    public void raise() {
    }

    public void lower() {
    }

    public void moveTo(float x, float y, float z) {
        Transform3D t = getLocalTransform();
        t.setTranslation(x, y, z);
        setLocalTransform(t);
    }

    public void resize(float width, float height) {
    }

    @Override
    public void render(JoglRenderer renderer) {
        if (visible) {
            if (transparency > 0) {
                renderer.setTransparency(transparency);
            }
            super.render(renderer);
        }
    }
}

/**
 * Component3D - base for 3D UI components.
 */
public class Component3D extends Group {

    private float width = 1.0f;
    private float height = 1.0f;
    private float depth = 0.1f;
    private Appearance appearance;
    private boolean enabled = true;

    public Component3D() {
        super();
    }

    public Component3D(String name) {
        super(name);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

/**
 * Cursor3D - 3D cursor representation.
 */
class Cursor3D extends Node {

    private int cursorType = Cursor.DEFAULT;

    public static final int DEFAULT = 0;
    public static final int CROSSHAIR = 1;
    public static final int HAND = 2;
    public static final int MOVE = 3;
    public static final int TEXT = 4;
    public static final int WAIT = 5;
    public static final int RESIZE_N = 6;
    public static final int RESIZE_NE = 7;
    public static final int RESIZE_E = 8;
    public static final int RESIZE_SE = 9;
    public static final int RESIZE_S = 10;
    public static final int RESIZE_SW = 11;
    public static final int RESIZE_W = 12;

    public Cursor3D() {
        super("Cursor");
    }

    public int getCursorType() {
        return cursorType;
    }

    public void setCursorType(int type) {
        this.cursorType = type;
    }
}