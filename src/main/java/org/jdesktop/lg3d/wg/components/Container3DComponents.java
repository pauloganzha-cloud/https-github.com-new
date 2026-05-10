package org.jdesktop.lg3d.wg.components;

import org.jdesktop.lg3d.wg.*;
import org.jdesktop.lg3d.sg.*;
import java.util.*;

/**
 * 3D ScrollPane - scrollable container.
 */
class ScrollPane3D extends Container3D {

    private Component3D viewport;
    private Component3D content;
    private ScrollBar3D hScroll;
    private ScrollBar3D vScroll;

    private float viewWidth = 3.0f;
    private float viewHeight = 2.0f;

    public ScrollPane3D() {
        super("ScrollPane3D");
        initComponents();
    }

    private void initComponents() {
        setSize(viewWidth, viewHeight);

        viewport = new Component3D("Viewport");
        viewport.setSize(viewWidth, viewHeight);
        addChild(viewport);

        content = new Container3D("ScrollContent");
        content.setSize(viewWidth, viewHeight);
        content.setTranslation(0, 0, 0);
        viewport.addChild(content);

        hScroll = new ScrollBar3D(ScrollBar3D.Orientation.HORIZONTAL);
        hScroll.setSize(viewWidth, 0.2f);
        hScroll.setTranslation(0, -viewHeight / 2 - 0.15f, 0.05f);
        addChild(hScroll);

        vScroll = new ScrollBar3D(ScrollBar3D.Orientation.VERTICAL);
        vScroll.setSize(0.2f, viewHeight);
        vScroll.setTranslation(viewWidth / 2 + 0.15f, 0, 0.05f);
        addChild(vScroll);
    }

    public Container3D getContentPane() {
        return content;
    }

    public void setViewSize(float w, float h) {
        this.viewWidth = w;
        this.viewHeight = h;
        viewport.setSize(w, h);
    }
}

/**
 * 3D List component.
 */
class List3D extends Container3D {

    private List<String> items;
    private int selectedIndex = -1;
    private List3DListener listener;
    private float itemHeight = 0.4f;
    private float listWidth = 2.5f;

    private ScrollPane3D scrollPane;
    private Container3D itemContainer;

    public List3D() {
        super("List3D");
        items = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setSize(listWidth, 2.0f);

        scrollPane = new ScrollPane3D();
        scrollPane.setSize(listWidth, getHeight());
        addChild(scrollPane);

        itemContainer = scrollPane.getContentPane();
    }

    public void addItem(String item) {
        items.add(item);
        updateItems();
    }

    public void removeItem(String item) {
        items.remove(item);
        updateItems();
    }

    public void clear() {
        items.clear();
        selectedIndex = -1;
        updateItems();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < items.size()) {
            selectedIndex = index;
            if (listener != null) {
                listener.selectionChanged(this, index);
            }
        }
    }

    public String getSelectedItem() {
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            return items.get(selectedIndex);
        }
        return null;
    }

    private void updateItems() {
        for (int i = 0; i < itemContainer.numChildren(); i++) {
            itemContainer.removeChild(itemContainer.getChildren().get(i));
        }

        for (int i = 0; i < items.size(); i++) {
            Component3D item = createItemComponent(items.get(i), i);
            item.setTranslation(-listWidth / 2 + 0.2f,
                               getHeight() / 2 - 0.3f - i * itemHeight,
                               0);
            itemContainer.addChild(item);
        }
    }

    private Component3D createItemComponent(String text, int index) {
        Component3D item = new Component3D("ListItem-" + index);
        item.setSize(listWidth - 0.4f, itemHeight - 0.05f);

        Label3D label = new Label3D(text);
        label.setTranslation(-listWidth / 2 + 0.3f, 0, 0.05f);
        item.addChild(label);

        return item;
    }

    public void addListener(List3DListener listener) {
        this.listener = listener;
    }
}

interface List3DListener {
    void selectionChanged(List3D list, int selectedIndex);
}

/**
 * 3D ComboBox (Dropdown) component.
 */
class ComboBox3D extends Component3D {

    private List<String> items;
    private int selectedIndex = -1;
    private boolean expanded = false;
    private ComboBox3DListener listener;

    private Button3D trigger;
    private Container3D dropdown;
    private List3D list;

    public ComboBox3D() {
        super("ComboBox3D");
        items = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setSize(2.0f, 0.5f);

        trigger = new Button3D("Select...");
        trigger.setSize(getWidth(), getHeight());
        trigger.addListener(new Button3DListener() {
            @Override
            public void buttonPressed(Button3D button) {
                toggleExpanded();
            }
        });
        addChild(trigger);

        dropdown = new Container3D("Dropdown");
        dropdown.setSize(getWidth(), 1.5f);
        dropdown.setTranslation(0, -getHeight() / 2 - 0.8f, 0);
        dropdown.setVisible(false);
        addChild(dropdown);

        list = new List3D();
        list.setSize(getWidth() - 0.1f, 1.5f);
        dropdown.addChild(list);
    }

    public void addItem(String item) {
        items.add(item);
        list.addItem(item);
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < items.size()) {
            selectedIndex = index;
            trigger.setText(items.get(index));
            if (listener != null) {
                listener.selectionChanged(this, index);
            }
        }
    }

    private void toggleExpanded() {
        expanded = !expanded;
        dropdown.setVisible(expanded);
    }

    public void addListener(ComboBox3DListener listener) {
        this.listener = listener;
    }
}

interface ComboBox3DListener {
    void selectionChanged(ComboBox3D comboBox, int selectedIndex);
}

/**
 * 3D TabbedPane component.
 */
class TabbedPane3D extends Container3D {

    private Map<String, Component3D> tabs;
    private Component3D selectedTab;
    private Container3D contentPanel;
    private float tabHeight = 0.4f;

    private Container3D tabBar;

    public TabbedPane3D() {
        super("TabbedPane3D");
        tabs = new LinkedHashMap<>();
        initComponents();
    }

    private void initComponents() {
        setSize(3.0f, 2.0f);

        tabBar = new Container3D("TabBar");
        tabBar.setSize(getWidth(), tabHeight);
        tabBar.setTranslation(0, getHeight() / 2 - tabHeight / 2, 0);
        addChild(tabBar);

        contentPanel = new Container3D("ContentPanel");
        contentPanel.setSize(getWidth(), getHeight() - tabHeight - 0.1f);
        contentPanel.setTranslation(0, -tabHeight / 2 - 0.05f, 0);
        addChild(contentPanel);
    }

    public void addTab(String title, Component3D content) {
        tabs.put(title, content);

        Button3D tabButton = new Button3D(title);
        tabButton.setSize(0.8f, tabHeight - 0.1f);
        tabButton.setTranslation(tabs.size() * 0.85f - getWidth() / 2 + 0.5f, 0, 0);
        tabButton.addListener(new Button3DListener() {
            @Override
            public void buttonPressed(Button3D button) {
                selectTab(title);
            }
        });
        tabBar.addChild(tabButton);

        if (selectedTab == null) {
            selectTab(title);
        }
    }

    public void selectTab(String title) {
        Component3D content = tabs.get(title);
        if (content == null) return;

        for (int i = 0; i < contentPanel.numChildren(); i++) {
            contentPanel.removeChild(contentPanel.getChildren().get(i));
        }

        contentPanel.addChild(content);
        selectedTab = content;
    }

    public Component3D getSelectedComponent() {
        return selectedTab;
    }
}

/**
 * 3D SplitPane - divider between two components.
 */
class SplitPane3D extends Container3D {

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    private Orientation orientation;
    private float dividerPosition = 0.5f;
    private Component3D leftComponent;
    private Component3D rightComponent;
    private Component3D divider;

    public SplitPane3D(Orientation orientation) {
        super("SplitPane3D");
        this.orientation = orientation;
        initComponents();
    }

    private void initComponents() {
        setSize(4.0f, 2.0f);

        leftComponent = new Container3D("Left");
        rightComponent = new Container3D("Right");
        divider = new Component3D("Divider");

        addChild(leftComponent);
        addChild(rightComponent);
        addChild(divider);

        updateLayout();
    }

    public void setLeftComponent(Component3D comp) {
        if (leftComponent.numChildren() > 0) {
            leftComponent.removeChild(leftComponent.getChildren().get(0));
        }
        leftComponent.addChild(comp);
    }

    public void setRightComponent(Component3D comp) {
        if (rightComponent.numChildren() > 0) {
            rightComponent.removeChild(rightComponent.getChildren().get(0));
        }
        rightComponent.addChild(comp);
    }

    public void setDividerPosition(float pos) {
        this.dividerPosition = Math.max(0.1f, Math.min(0.9f, pos));
        updateLayout();
    }

    private void updateLayout() {
        if (orientation == Orientation.HORIZONTAL) {
            float leftWidth = getWidth() * dividerPosition - 0.1f;
            float rightWidth = getWidth() * (1 - dividerPosition) - 0.1f;

            leftComponent.setSize(leftWidth, getHeight());
            leftComponent.setTranslation(-getWidth() / 2 + leftWidth / 2, 0, 0);

            rightComponent.setSize(rightWidth, getHeight());
            rightComponent.setTranslation(getWidth() / 2 - rightWidth / 2, 0, 0);

            divider.setSize(0.1f, getHeight() - 0.2f);
            divider.setTranslation(-getWidth() / 2 + leftWidth + 0.05f, 0, 0.05f);
        }
    }
}