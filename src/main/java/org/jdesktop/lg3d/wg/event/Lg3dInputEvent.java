package org.jdesktop.lg3d.wg.event;

import java.util.*;
import org.jdesktop.lg3d.wg.*;

/**
 * Input event system for LG3D.
 */
public class InputEvent {

    public enum Type {
        KEY_PRESSED,
        KEY_RELEASED,
        KEY_TYPED,
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_CLICKED,
        MOUSE_MOVED,
        MOUSE_DRAGGED,
        MOUSE_WHEEL
    }

    private final Type type;
    private final long timestamp;
    private Component3D source;
    private boolean consumed = false;

    public InputEvent(Type type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public Type getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Component3D getSource() {
        return source;
    }

    public void setSource(Component3D source) {
        this.source = source;
    }

    public void consume() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }
}

/**
 * Keyboard event.
 */
class KeyEvent extends InputEvent {

    public static final int VK_UNDEFINED = 0;
    public static final int VK_A = 65;
    public static final int VK_ENTER = 10;
    public static final int VK_ESCAPE = 27;
    public static final int VK_SPACE = 32;

    private final int keyCode;
    private final char keyChar;

    public KeyEvent(Type type, int keyCode, char keyChar) {
        super(type);
        this.keyCode = keyCode;
        this.keyChar = keyChar;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public char getKeyChar() {
        return keyChar;
    }

    public String getKeyText() {
        return KeyEvent.class.getName() + "[keyCode=" + keyCode + "]";
    }
}

/**
 * Mouse event.
 */
class MouseEvent extends InputEvent {

    private final int x, y;
    private final int clickCount;
    private final int button;
    private final int modifiers;

    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_MIDDLE = 2;
    public static final int BUTTON_RIGHT = 3;

    public static final int NOBUTTON = 0;

    public MouseEvent(Type type, int x, int y, int button, int clickCount) {
        super(type);
        this.x = x;
        this.y = y;
        this.button = button;
        this.clickCount = clickCount;
        this.modifiers = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getClickCount() {
        return clickCount;
    }

    public int getButton() {
        return button;
    }

    public int getModifiers() {
        return modifiers;
    }

    public Point getPoint() {
        return new Point(x, y);
    }
}

/**
 * Event listener interface.
 */
interface Lg3dEventListener extends EventListener {

    void handleInputEvent(InputEvent event);
}

/**
 * Event adapter - convenience class.
 */
class Lg3dEventAdapter implements Lg3dEventListener {

    @Override
    public void handleInputEvent(InputEvent event) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }
}