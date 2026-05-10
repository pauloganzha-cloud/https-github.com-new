package org.jdesktop.lg3d.appkit;

import java.util.*;
import org.jdesktop.lg3d.wg.*;
import org.jdesktop.lg3d.sg.*;

/**
 * Application framework for LG3D.
 */
public class Application {

    private static Application instance;
    private String name;
    private Window3D mainWindow;
    private boolean running = false;
    private List<ApplicationListener> listeners;

    private Application() {
        listeners = new ArrayList<>();
    }

    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMainWindow(Window3D window) {
        this.mainWindow = window;
    }

    public Window3D getMainWindow() {
        return mainWindow;
    }

    public void start() {
        running = true;
        fireApplicationStarted();
    }

    public void stop() {
        running = false;
        fireApplicationStopped();
    }

    public boolean isRunning() {
        return running;
    }

    public void addApplicationListener(ApplicationListener listener) {
        listeners.add(listener);
    }

    public void removeApplicationListener(ApplicationListener listener) {
        listeners.remove(listener);
    }

    private void fireApplicationStarted() {
        for (ApplicationListener listener : listeners) {
            listener.applicationStarted();
        }
    }

    private void fireApplicationStopped() {
        for (ApplicationListener listener : listeners) {
            listener.applicationStopped();
        }
    }
}

/**
 * Application lifecycle listener.
 */
interface ApplicationListener {
    void applicationStarted();
    void applicationStopped();
}

/**
 * Simple application adapter.
 */
class ApplicationAdapter implements ApplicationListener {
    @Override
    public void applicationStarted() {
    }

    @Override
    public void applicationStopped() {
    }
}

/**
 * Frame3D - main application window frame.
 */
class Frame3D extends Window3D {

    private Container3D contentPane;
    private Component3D titleBar;
    private Component3D menuBar;
    private boolean decorationsEnabled = true;

    public Frame3D() {
        super();
        initFrame();
    }

    public Frame3D(String title) {
        super(title);
        initFrame();
    }

    private void initFrame() {
        contentPane = new Container3D("ContentPane");
        contentPane.setLayout(new BorderLayout3D());
        setComponent(contentPane);

        if (decorationsEnabled) {
            createDecorations();
        }
    }

    private void createDecorations() {
        titleBar = new Component3D("TitleBar");
        titleBar.setSize(getWidth(), 0.15f);
        contentPane.add(titleBar, BorderLayout3D.NORTH);
    }

    public Container3D getContentPane() {
        return contentPane;
    }

    public void setMenuBar(Component3D menu) {
        this.menuBar = menu;
        if (menu != null) {
            contentPane.add(menu, BorderLayout3D.NORTH);
        }
    }

    public Component3D getMenuBar() {
        return menuBar;
    }

    public void setDecorationsEnabled(boolean enabled) {
        this.decorationsEnabled = enabled;
    }

    public boolean isDecorationsEnabled() {
        return decorationsEnabled;
    }
}

/**
 * Menu bar component.
 */
class MenuBar3D extends Container3D {

    private List<Menu3D> menus;

    public MenuBar3D() {
        super("MenuBar");
        menus = new ArrayList<>();
        setLayout(new FlowLayout3D(FlowLayout3D.LEFT));
    }

    public void add(Menu3D menu) {
        menus.add(menu);
        add(menu);
    }

    public void remove(Menu3D menu) {
        menus.remove(menu);
        remove(menu);
    }

    public List<Menu3D> getMenus() {
        return menus;
    }
}

/**
 * Dropdown menu.
 */
class Menu3D extends Component3D {

    private List<MenuItem3D> items;
    private boolean popup = false;
    private Component3D parentMenu;

    public Menu3D(String label) {
        super("Menu:" + label);
        items = new ArrayList<>();
    }

    public void add(MenuItem3D item) {
        items.add(item);
    }

    public void remove(MenuItem3D item) {
        items.remove(item);
    }

    public List<MenuItem3D> getItems() {
        return items;
    }

    public void setPopup(boolean popup) {
        this.popup = popup;
    }

    public boolean isPopup() {
        return popup;
    }

    public void setParentMenu(Component3D parent) {
        this.parentMenu = parent;
    }

    public Component3D getParentMenu() {
        return parentMenu;
    }
}

/**
 * Menu item.
 */
class MenuItem3D extends Component3D {

    private String actionCommand;
    private boolean enabled = true;
    private Runnable action;

    public MenuItem3D(String label) {
        super("MenuItem:" + label);
        this.actionCommand = label;
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public void setActionCommand(String command) {
        this.actionCommand = command;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void doClick() {
        if (enabled && action != null) {
            action.run();
        }
    }

    public void setAction(Runnable action) {
        this.action = action;
    }
}

/**
 * Separator menu item.
 */
class Separator3D extends MenuItem3D {

    public Separator3D() {
        super("-");
        setEnabled(false);
    }
}

/**
 * Checkbox menu item.
 */
class CheckboxMenuItem3D extends MenuItem3D {

    private boolean state = false;

    public CheckboxMenuItem3D(String label) {
        super(label);
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void toggle() {
        setState(!state);
    }
}