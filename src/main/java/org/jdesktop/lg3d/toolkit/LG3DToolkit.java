package org.jdesktop.lg3d.toolkit;

import java.util.*;
import org.jdesktop.lg3d.wg.*;
import org.jdesktop.lg3d.sg.*;

/**
 * LG3D Toolkit - Provides UI component creation and management.
 */
public class LG3DToolkit {

    private static LG3DToolkit instance;
    private final Map<String, Component3D> components;
    private DisplayServer displayServer;

    private LG3DToolkit() {
        components = new HashMap<>();
    }

    public static LG3DToolkit getInstance() {
        if (instance == null) {
            instance = new LG3DToolkit();
        }
        return instance;
    }

    public void initialize() {
        displayServer = DisplayServer.getInstance();
    }

    public Component3D createComponent(String name) {
        Component3D comp = new Component3D(name);
        components.put(name, comp);
        return comp;
    }

    public Window3D createWindow(String title) {
        Window3D window = new Window3D(title);
        window.setTitle(title);
        return window;
    }

    public void addToScene(Component3D component) {
        if (displayServer != null) {
        }
    }

    public void removeFromScene(Component3D component) {
    }

    public Component3D findComponent(String name) {
        return components.get(name);
    }

    public Collection<Component3D> getAllComponents() {
        return components.values();
    }
}

/**
 * ComponentFactory - creates standard UI components.
 */
class ComponentFactory {

    public static Component3D createLabel(String text) {
        Component3D label = new Component3D("Label:" + text);
        return label;
    }

    public static Component3D createButton(String text) {
        Component3D button = new Component3D("Button:" + text);
        return button;
    }

    public static Component3D createPanel() {
        return new Component3D("Panel");
    }

    public static Component3D createImage(String imagePath) {
        Component3D image = new Component3D("Image:" + imagePath);
        return image;
    }
}

/**
 * Layout manager interface for 3D layouts.
 */
interface LayoutManager3D {

    void layoutContainer(Container3D parent);

    void addLayoutComponent(Component3D comp, Object constraints);

    void removeLayoutComponent(Component3D comp);

    Dimension3D preferredLayoutSize(Container3D parent);

    Dimension3D minimumLayoutSize(Container3D parent);
}

/**
 * 3D Dimension.
 */
class Dimension3D {
    public float width, height, depth;

    public Dimension3D() {
        this(0, 0, 0);
    }

    public Dimension3D(float w, float h) {
        this(w, h, 0);
    }

    public Dimension3D(float w, float h, float d) {
        this.width = w;
        this.height = h;
        this.depth = d;
    }
}

/**
 * Container3D - can hold child components.
 */
class Container3D extends Component3D {

    private LayoutManager3D layoutManager;
    private List<Component3D> managedChildren;

    public Container3D() {
        super();
        managedChildren = new ArrayList<>();
    }

    public Container3D(String name) {
        super(name);
        managedChildren = new ArrayList<>();
    }

    public void setLayout(LayoutManager3D layout) {
        this.layoutManager = layout;
    }

    public LayoutManager3D getLayout() {
        return layoutManager;
    }

    public void add(Component3D comp) {
        add(comp, null);
    }

    public void add(Component3D comp, Object constraints) {
        managedChildren.add(comp);
        addChild(comp);
        if (layoutManager != null) {
            layoutManager.addLayoutComponent(comp, constraints);
        }
    }

    public void remove(Component3D comp) {
        managedChildren.remove(comp);
        removeChild(comp);
        if (layoutManager != null) {
            layoutManager.removeLayoutComponent(comp);
        }
    }

    public List<Component3D> getManagedChildren() {
        return managedChildren;
    }

    public void doLayout() {
        if (layoutManager != null) {
            layoutManager.layoutContainer(this);
        }
    }
}

/**
 * BorderLayout3D - positions components in 5 regions.
 */
class BorderLayout3D implements LayoutManager3D {

    public static final String NORTH = "North";
    public static final String SOUTH = "South";
    public static final String EAST = "East";
    public static final String WEST = "West";
    public static final String CENTER = "Center";

    private Component3D north, south, east, west, center;

    @Override
    public void layoutContainer(Container3D parent) {
        float w = parent.getWidth();
        float h = parent.getHeight();

        if (north != null) north.setSize(w, 0.1f);
        if (south != null) south.setSize(w, 0.1f);
        if (east != null) east.setSize(0.1f, h);
        if (west != null) west.setSize(0.1f, h);
    }

    @Override
    public void addLayoutComponent(Component3D comp, Object constraints) {
        if (constraints == null || CENTER.equals(constraints)) {
            center = comp;
        } else if (NORTH.equals(constraints)) {
            north = comp;
        } else if (SOUTH.equals(constraints)) {
            south = comp;
        } else if (EAST.equals(constraints)) {
            east = comp;
        } else if (WEST.equals(constraints)) {
            west = comp;
        }
    }

    @Override
    public void removeLayoutComponent(Component3D comp) {
        if (comp == north) north = null;
        else if (comp == south) south = null;
        else if (comp == east) east = null;
        else if (comp == west) west = null;
        else if (comp == center) center = null;
    }

    @Override
    public Dimension3D preferredLayoutSize(Container3D parent) {
        return new Dimension3D(parent.getWidth(), parent.getHeight());
    }

    @Override
    public Dimension3D minimumLayoutSize(Container3D parent) {
        return new Dimension3D(100, 50);
    }
}

/**
 * FlowLayout3D - simple left-to-right layout.
 */
class FlowLayout3D implements LayoutManager3D {

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    private int align = LEFT;
    private float hgap = 0.1f;
    private float vgap = 0.1f;

    public FlowLayout3D() {
    }

    public FlowLayout3D(int align) {
        this.align = align;
    }

    @Override
    public void layoutContainer(Container3D parent) {
        List<Component3D> children = parent.getManagedChildren();
        float x = 0;
        float y = 0;
        float rowHeight = 0;

        for (Component3D child : children) {
            float cw = child.getWidth();
            float ch = child.getHeight();

            if (x + cw > parent.getWidth() && x > 0) {
                x = 0;
                y += rowHeight + vgap;
                rowHeight = 0;
            }

            child.setLocation(x, y, 0);
            x += cw + hgap;
            rowHeight = Math.max(rowHeight, ch);
        }
    }

    @Override
    public void addLayoutComponent(Component3D comp, Object constraints) {
    }

    @Override
    public void removeLayoutComponent(Component3D comp) {
    }

    @Override
    public Dimension3D preferredLayoutSize(Container3D parent) {
        return new Dimension3D(200, 100);
    }

    @Override
    public Dimension3D minimumLayoutSize(Container3D parent) {
        return new Dimension3D(100, 50);
    }
}