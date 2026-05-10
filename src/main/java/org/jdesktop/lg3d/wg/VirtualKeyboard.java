package org.jdesktop.lg3d.wg;

import java.util.*;
import org.jdesktop.lg3d.wg.components.*;
import org.jdesktop.lg3d.sg.*;

/**
 * VirtualKeyboard - 3D virtual keyboard.
 */
public class VirtualKeyboard extends Component3D {

    private static VirtualKeyboard instance;

    private boolean visible = false;
    private KeyboardLayout layout = KeyboardLayout.US;
    private boolean capsLock = false;
    private boolean shift = false;

    private Container3D keyboardContainer;
    private Key3D[][] keys;
    private VirtualKeyboardListener listener;

    private static final int ROWS = 5;
    private static final int[] COLS = {13, 13, 12, 10, 7};

    public static final String[] LAYOUT_US = {
        "`1234567890-=",
        "qwertyuiop[]\\",
        "asdfghjkl;'",
        "zxcvbnm,./",
        "   space    "
    };

    public static final String[] LAYOUT_US_SHIFT = {
        "~!@#$%^&*()_+",
        "QWERTYUIOP{}|",
        "ASDFGHJKL:\"",
        "ZXCVBNM<>?",
        "   SPACE    "
    };

    private VirtualKeyboard() {
        super("VirtualKeyboard");
        keys = new Key3D[ROWS][];
        initKeyboard();
    }

    public static VirtualKeyboard getInstance() {
        if (instance == null) {
            instance = new VirtualKeyboard();
        }
        return instance;
    }

    private void initKeyboard() {
        keyboardContainer = new Container3D("KeyboardContainer");
        keyboardContainer.setSize(8.0f, 2.5f);
        addChild(keyboardContainer);

        createKeys();
    }

    private void createKeys() {
        float startY = 1.0f;
        float keyWidth = 0.5f;
        float keyHeight = 0.4f;
        float spacing = 0.05f;

        String[] layout = LAYOUT_US;
        if (shift || capsLock) {
            layout = LAYOUT_US_SHIFT;
        }

        for (int row = 0; row < ROWS; row++) {
            String rowStr = layout[row];
            keys[row] = new Key3D[rowStr.length()];

            float rowWidth = rowStr.length() * (keyWidth + spacing);
            float startX = -rowWidth / 2 + keyWidth / 2;

            for (int col = 0; col < rowStr.length(); col++) {
                char c = rowStr.charAt(col);
                Key3D key = new Key3D(String.valueOf(c));
                key.setSize(keyWidth - 0.02f, keyHeight - 0.02f);
                key.setTranslation(startX + col * (keyWidth + spacing), startY - row * (keyHeight + spacing), 0.05f);

                key.addListener(new Button3DListener() {
                    @Override
                    public void buttonPressed(Button3D button) {
                        keyPressed(c);
                    }
                });

                keys[row][col] = key;
                keyboardContainer.addChild(key);
            }
        }
    }

    private void keyPressed(char c) {
        if (c == ' ') {
            if (listener != null) listener.keyTyped(' ');
            return;
        }

        char keyChar = c;
        if (!shift && !capsLock && Character.isUpperCase(c)) {
            keyChar = Character.toLowerCase(c);
        }

        if (listener != null) {
            listener.keyTyped(keyChar);
        }

        if (shift) {
            shift = false;
            refreshKeys();
        }
    }

    private void refreshKeys() {
        keyboardContainer.getChildren().clear();
        createKeys();
    }

    public void show() {
        setVisible(true);
        visible = true;
    }

    public void hide() {
        setVisible(false);
        visible = false;
    }

    public void toggle() {
        if (visible) hide();
        else show();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setCapsLock(boolean enabled) {
        capsLock = enabled;
        refreshKeys();
    }

    public void toggleCapsLock() {
        setCapsLock(!capsLock);
    }

    public void pressShift() {
        shift = true;
        refreshKeys();
    }

    public void releaseShift() {
        shift = false;
        refreshKeys();
    }

    public void setKeyboardListener(VirtualKeyboardListener listener) {
        this.listener = listener;
    }

    public void setLayout(KeyboardLayout layout) {
        this.layout = layout;
        refreshKeys();
    }

    public enum KeyboardLayout {
        US, UK, DE, FR, ES
    }
}

interface VirtualKeyboardListener {
    void keyTyped(char key);
    void keyPressed(String key);
    void keyReleased(String key);
}

/**
 * Key - individual keyboard key.
 */
class Key3D extends Component3D {

    private String keyChar;
    private boolean isModifier = false;
    private Key3DListener listener;

    public Key3D(String keyChar) {
        super("Key-" + keyChar);
        this.keyChar = keyChar;

        if (keyChar.equals("   ")) {
            isModifier = true;
            setSize(3.0f, 0.4f);
        } else {
            setSize(0.5f, 0.4f);
        }
    }

    public String getKeyChar() {
        return keyChar;
    }

    public void setKeyListener(Key3DListener listener) {
        this.listener = listener;
    }

    public boolean isModifier() {
        return isModifier;
    }
}

interface Key3DListener {
    void keyActivated(Key3D key);
}