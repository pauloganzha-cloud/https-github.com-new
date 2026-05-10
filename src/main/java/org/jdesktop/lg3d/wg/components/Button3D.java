package org.jdesktop.lg3d.wg.components;

import org.jdesktop.lg3d.sg.*;
import org.jdesktop.lg3d.wg.*;

/**
 * 3D Button component.
 */
public class Button3D extends Component3D {

    private String text;
    private boolean pressed = false;
    private boolean hover = false;
    private Runnable action;
    private Button3DListener listener;

    private Component3D background;
    private Component3D highlight;
    private Label3D label;

    public Button3D() {
        this("Button");
    }

    public Button3D(String text) {
        super("Button3D");
        this.text = text;
        initComponents();
    }

    private void initComponents() {
        setSize(2.0f, 0.6f);

        background = new Component3D("ButtonBackground");
        background.setSize(getWidth(), getHeight());

        Appearance app = new Appearance();
        Material mat = new Material();
        mat.diffuse.set(0.3f, 0.4f, 0.6f);
        app.setMaterial(mat);
        background.setAppearance(app);
        addChild(background);

        highlight = new Component3D("ButtonHighlight");
        highlight.setSize(getWidth() - 0.1f, getHeight() - 0.1f);
        highlight.setTranslation(0, 0, 0.02f);
        highlight.setVisible(false);
        addChild(highlight);

        label = new Label3D(text);
        label.setTranslation(0, 0, 0.05f);
        addChild(label);
    }

    public void setText(String text) {
        this.text = text;
        if (label != null) {
            label.setText(text);
        }
    }

    public String getText() {
        return text;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
        updateAppearance();
        if (pressed && listener != null) {
            listener.buttonPressed(this);
        }
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setHover(boolean hover) {
        this.hover = hover;
        updateAppearance();
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void addListener(Button3DListener listener) {
        this.listener = listener;
    }

    public void removeListener(Button3DListener listener) {
        if (this.listener == listener) {
            this.listener = null;
        }
    }

    public void doClick() {
        setPressed(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}
        setPressed(false);

        if (action != null) {
            action.run();
        }
    }

    private void updateAppearance() {
        Material mat = background.getAppearance().getMaterial();
        if (pressed) {
            mat.diffuse.set(0.2f, 0.25f, 0.4f);
        } else if (hover) {
            mat.diffuse.set(0.4f, 0.5f, 0.7f);
            highlight.setVisible(true);
        } else {
            mat.diffuse.set(0.3f, 0.4f, 0.6f);
            highlight.setVisible(false);
        }
    }
}

interface Button3DListener {
    void buttonPressed(Button3D button);
}

/**
 * 3D Label component.
 */
class Label3D extends Component3D {

    private String text;
    private float fontSize = 0.15f;
    private java.awt.Color textColor = java.awt.Color.WHITE;

    public Label3D() {
        this("");
    }

    public Label3D(String text) {
        super("Label3D");
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float size) {
        this.fontSize = size;
    }

    public java.awt.Color getTextColor() {
        return textColor;
    }

    public void setTextColor(java.awt.Color color) {
        this.textColor = color;
    }
}

/**
 * 3D Checkbox component.
 */
class Checkbox3D extends Component3D {

    private boolean selected = false;
    private String label;
    private Checkbox3DListener listener;

    private Component3D box;
    private Component3D check;
    private Label3D textLabel;

    public Checkbox3D(String label) {
        super("Checkbox3D");
        this.label = label;
        initComponents();
    }

    private void initComponents() {
        setSize(0.6f, 0.6f);

        box = new Component3D("CheckboxBox");
        box.setSize(0.4f, 0.4f);
        addChild(box);

        check = new Component3D("CheckboxCheck");
        check.setSize(0.3f, 0.3f);
        check.setTranslation(0, 0, 0.01f);
        check.setVisible(false);
        addChild(check);

        textLabel = new Label3D(label);
        textLabel.setTranslation(0.5f, 0, 0);
        addChild(textLabel);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        check.setVisible(selected);
        if (listener != null) {
            listener.checkboxChanged(this, selected);
        }
    }

    public void toggle() {
        setSelected(!selected);
    }

    public void addListener(Checkbox3DListener listener) {
        this.listener = listener;
    }
}

interface Checkbox3DListener {
    void checkboxChanged(Checkbox3D checkbox, boolean selected);
}

/**
 * 3D Slider component.
 */
class Slider3D extends Component3D {

    private float value = 0.5f;
    private float minValue = 0;
    private float maxValue = 1;
    private boolean dragging = false;
    private Slider3DListener listener;

    private Component3D track;
    private Component3D thumb;
    private float trackWidth = 2.0f;

    public Slider3D() {
        super("Slider3D");
        initComponents();
    }

    private void initComponents() {
        setSize(trackWidth, 0.3f);

        track = new Component3D("SliderTrack");
        track.setSize(trackWidth - 0.2f, 0.1f);
        addChild(track);

        thumb = new Component3D("SliderThumb");
        thumb.setSize(0.2f, 0.25f);
        updateThumbPosition();
        addChild(thumb);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Math.max(minValue, Math.min(maxValue, value));
        updateThumbPosition();
        if (listener != null) {
            listener.sliderValueChanged(this, this.value);
        }
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    private void updateThumbPosition() {
        float normalized = (value - minValue) / (maxValue - minValue);
        float x = -trackWidth / 2 + 0.2f + normalized * (trackWidth - 0.4f);
        thumb.setTranslation(x, 0, 0.05f);
    }

    public void addListener(Slider3DListener listener) {
        this.listener = listener;
    }
}

interface Slider3DListener {
    void sliderValueChanged(Slider3D slider, float value);
}

/**
 * 3D TextField component.
 */
class TextField3D extends Component3D {

    private String text = "";
    private int maxChars = 100;
    private TextField3DListener listener;
    private boolean focused = false;

    private Component3D background;
    private Label3D displayLabel;
    private float width = 2.0f;

    public TextField3D() {
        this("");
    }

    public TextField3D(String initialText) {
        super("TextField3D");
        this.text = initialText;
        initComponents();
    }

    private void initComponents() {
        setSize(width, 0.5f);

        background = new Component3D("TextFieldBackground");
        background.setSize(width - 0.1f, getHeight() - 0.1f);
        addChild(background);

        displayLabel = new Label3D(text);
        displayLabel.setTranslation(-width / 2 + 0.15f, 0, 0.05f);
        displayLabel.setFontSize(0.12f);
        addChild(displayLabel);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text.length() <= maxChars) {
            this.text = text;
            displayLabel.setText(text);
            if (listener != null) {
                listener.textChanged(this, text);
            }
        }
    }

    public void appendText(String add) {
        setText(text + add);
    }

    public void clearText() {
        setText("");
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    public void addListener(TextField3DListener listener) {
        this.listener = listener;
    }
}

interface TextField3DListener {
    void textChanged(TextField3D field, String text);
}

/**
 * 3D ProgressBar component.
 */
class ProgressBar3D extends Component3D {

    private float value = 0;
    private float minValue = 0;
    private float maxValue = 1;
    private float width = 2.5f;
    private float height = 0.3f;

    private Component3D track;
    private Component3D bar;
    private float barPadding = 0.05f;

    public ProgressBar3D() {
        super("ProgressBar3D");
        initComponents();
    }

    private void initComponents() {
        setSize(width, height);

        track = new Component3D("ProgressTrack");
        track.setSize(width - 0.2f, height - 0.1f);
        addChild(track);

        bar = new Component3D("ProgressBar");
        bar.setSize(0, height - 0.15f);
        bar.setTranslation(-width / 2 + 0.15f, 0, 0.02f);

        Appearance app = new Appearance();
        Material mat = new Material();
        mat.diffuse.set(0.2f, 0.6f, 0.9f);
        app.setMaterial(mat);
        bar.setAppearance(app);
        addChild(bar);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Math.max(minValue, Math.min(maxValue, value));
        updateBar();
    }

    private void updateBar() {
        float normalized = (value - minValue) / (maxValue - minValue);
        float barWidth = normalized * (width - 0.2f - barPadding * 2);
        bar.setSize(barWidth, getHeight() - 0.15f);
    }
}

/**
 * 3D Panel container.
 */
class Panel3D extends Container3D {

    private boolean bordered = true;
    private float borderWidth = 0.05f;

    public Panel3D() {
        super("Panel3D");
    }

    public Panel3D(String name) {
        super(name);
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
    }

    public boolean isBordered() {
        return bordered;
    }
}

/**
 * 3D ScrollBar component.
 */
class ScrollBar3D extends Component3D {

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    private Orientation orientation = Orientation.HORIZONTAL;
    private float value = 0;
    private float minValue = 0;
    private float maxValue = 1;
    private float visibleAmount = 0.2f;
    private ScrollBar3DListener listener;

    public ScrollBar3D(Orientation orientation) {
        super("ScrollBar3D");
        this.orientation = orientation;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Math.max(minValue, Math.min(maxValue, value));
        if (listener != null) {
            listener.scrollBarValueChanged(this, this.value);
        }
    }

    public void addListener(ScrollBar3DListener listener) {
        this.listener = listener;
    }
}

interface ScrollBar3DListener {
    void scrollBarValueChanged(ScrollBar3D scrollBar, float value);
}