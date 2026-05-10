package org.jdesktop.lg3d.wg;

import java.util.*;
import org.jdesktop.lg3d.wg.components.*;
import org.jdesktop.lg3d.sg.*;

/**
 * Taskbar - system taskbar with window buttons.
 */
public class Taskbar extends Component3D {

    private static Taskbar instance;

    private float width = 10.0f;
    private float height = 0.8f;
    private float positionY = -7.5f;

    private Container3D taskbarContainer;
    private List<TaskbarButton> buttons;
    private Clock3D clock;
    private SystemTray tray;

    private Taskbar() {
        super("Taskbar");
        buttons = new ArrayList<>();
        init();
    }

    public static Taskbar getInstance() {
        if (instance == null) {
            instance = new Taskbar();
        }
        return instance;
    }

    private void init() {
        setSize(width, height);
        setPosition(0, positionY, 0);

        taskbarContainer = new Container3D("TaskbarContainer");
        taskbarContainer.setSize(width - 0.2f, height - 0.1f);
        addChild(taskbarContainer);

        clock = new Clock3D();
        clock.setSize(1.5f, 0.5f);
        clock.setTranslation(width / 2 - 1.0f, 0, 0.1f);
        taskbarContainer.addChild(clock);

        tray = new SystemTray();
        tray.setSize(3.0f, height - 0.2f);
        tray.setTranslation(-width / 2 + 1.8f, 0, 0.1f);
        taskbarContainer.addChild(tray);
    }

    public void addWindow(Window3D window) {
        TaskbarButton button = new TaskbarButton(window);
        float x = -width / 2 + 3.5f + buttons.size() * 1.2f;
        button.setTranslation(x, 0, 0.1f);
        buttons.add(button);
        taskbarContainer.addChild(button);
    }

    public void removeWindow(Window3D window) {
        TaskbarButton toRemove = null;
        for (TaskbarButton btn : buttons) {
            if (btn.getWindow() == window) {
                toRemove = btn;
                break;
            }
        }

        if (toRemove != null) {
            buttons.remove(toRemove);
            taskbarContainer.removeChild(toRemove);
            reorderButtons();
        }
    }

    private void reorderButtons() {
        float startX = -width / 2 + 3.5f;
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setTranslation(startX + i * 1.2f, 0, 0.1f);
        }
    }

    public void updateWindowStates() {
        for (TaskbarButton btn : buttons) {
            btn.updateState();
        }
    }

    public void show() {
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }

    public void toggle() {
        setVisible(!isVisible());
    }

    public void addTrayIcon(TrayIcon icon) {
        tray.addIcon(icon);
    }
}

/**
 * Taskbar button for a window.
 */
class TaskbarButton extends Component3D {

    private final Window3D window;
    private boolean active = false;
    private boolean minimized = false;

    private Component3D icon;
    private Label3D label;

    public TaskbarButton(Window3D window) {
        super("TaskbarButton-" + window.getTitle());
        this.window = window;
        initComponents();
    }

    private void initComponents() {
        setSize(1.0f, 0.6f);

        icon = new Component3D("WindowIcon");
        icon.setSize(0.4f, 0.4f);
        icon.setTranslation(-0.35f, 0, 0);
        addChild(icon);

        label = new Label3D(truncate(window.getTitle(), 8));
        label.setTranslation(0.1f, 0, 0.02f);
        label.setFontSize(0.1f);
        addChild(label);

        addListener(new Button3DListener() {
            @Override
            public void buttonPressed(Button3D button) {
                if (window.isMinimized()) {
                    window.setMinimized(false);
                    WindowManager.getInstance().focusWindow(window);
                } else if (WindowManager.getInstance().getFocusedWindow() == window) {
                    window.setMinimized(true);
                } else {
                    WindowManager.getInstance().focusWindow(window);
                }
            }
        });
    }

    private String truncate(String s, int max) {
        if (s.length() <= max) return s;
        return s.substring(0, max - 2) + "..";
    }

    public Window3D getWindow() {
        return window;
    }

    public void updateState() {
        active = (WindowManager.getInstance().getActiveWindow() == window);
        minimized = window.isMinimized();

        if (minimized) {
            setOpacity(0.6f);
        } else if (active) {
            setOpacity(1.0f);
        } else {
            setOpacity(0.8f);
        }
    }
}

/**
 * Clock - system clock display.
 */
class Clock3D extends Component3D {

    private Label3D timeLabel;
    private Label3D dateLabel;
    private Thread clockThread;

    public Clock3D() {
        super("Clock");
        initComponents();
        startClock();
    }

    private void initComponents() {
        timeLabel = new Label3D("12:00");
        timeLabel.setFontSize(0.2f);
        addChild(timeLabel);

        dateLabel = new Label3D("Jan 1");
        dateLabel.setTranslation(0, -0.2f, 0.01f);
        dateLabel.setFontSize(0.1f);
        addChild(dateLabel);
    }

    private void startClock() {
        clockThread = new Thread(() -> {
            while (true) {
                updateTime();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        clockThread.setDaemon(true);
        clockThread.start();
    }

    private void updateTime() {
        java.util.Date now = new java.util.Date();
        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM d");
        timeLabel.setText(timeFormat.format(now));
        dateLabel.setText(dateFormat.format(now));
    }
}

/**
 * System tray with notification icons.
 */
class SystemTray extends Container3D {

    private List<TrayIcon> icons;

    public SystemTray() {
        super("SystemTray");
        icons = new ArrayList<>();
    }

    public void addIcon(TrayIcon icon) {
        icons.add(icon);
        float x = -1.0f + icons.size() * 0.4f;
        icon.setTranslation(x, 0, 0);
        addChild(icon);
    }

    public void removeIcon(TrayIcon icon) {
        icons.remove(icon);
        removeChild(icon);
    }
}

/**
 * Tray icon - small icon in system tray.
 */
class TrayIcon extends Component3D {

    private String tooltip;
    private Runnable action;
    private TrayIconListener listener;

    public TrayIcon(String iconPath) {
        super("TrayIcon");
        setSize(0.35f, 0.35f);
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    public void click() {
        if (action != null) {
            action.run();
        }
        if (listener != null) {
            listener.iconClicked(this);
        }
    }

    public void setListener(TrayIconListener listener) {
        this.listener = listener;
    }
}

interface TrayIconListener {
    void iconClicked(TrayIcon icon);
}

/**
 * Window Switcher - Alt+Tab style window switcher.
 */
class WindowSwitcher extends Component3D {

    private boolean visible = false;
    private List<Window3D> windowList;
    private int selectedIndex = 0;
    private Component3D preview;
    private Container3D thumbnails;

    public WindowSwitcher() {
        super("WindowSwitcher");
        windowList = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setSize(8.0f, 4.0f);
        setVisible(false);

        preview = new Component3D("SwitcherPreview");
        preview.setSize(4.0f, 3.0f);
        preview.setTranslation(0, 0.3f, 0);
        addChild(preview);

        thumbnails = new Container3D("Thumbnails");
        thumbnails.setSize(7.5f, 0.8f);
        thumbnails.setTranslation(0, -1.5f, 0);
        addChild(thumbnails);
    }

    public void show() {
        windowList.clear();
        for (Window3D w : WindowManager.getInstance().getAllWindows()) {
            if (w.isVisible()) {
                windowList.add(w);
            }
        }

        if (windowList.isEmpty()) return;

        selectedIndex = 0;
        updateThumbnails();
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }

    public boolean isVisible() {
        return visible;
    }

    public void selectNext() {
        if (windowList.isEmpty()) return;
        selectedIndex = (selectedIndex + 1) % windowList.size();
        updatePreview();
    }

    public void selectPrevious() {
        if (windowList.isEmpty()) return;
        selectedIndex = (selectedIndex - 1 + windowList.size()) % windowList.size();
        updatePreview();
    }

    public Window3D getSelectedWindow() {
        if (selectedIndex >= 0 && selectedIndex < windowList.size()) {
            return windowList.get(selectedIndex);
        }
        return null;
    }

    private void updatePreview() {
        Window3D selected = getSelectedWindow();
        if (selected != null) {
            // Update preview content
        }
    }

    private void updateThumbnails() {
        thumbnails.getChildren().clear();

        float startX = -3.5f;
        for (int i = 0; i < windowList.size(); i++) {
            Component3D thumb = new Component3D("Thumbnail-" + i);
            thumb.setSize(1.0f, 0.7f);
            thumb.setTranslation(startX + i * 1.1f, 0, 0);

            if (i == selectedIndex) {
                thumb.setHighlighted(true);
            }

            thumbnails.addChild(thumb);
        }
    }

    public void toggle() {
        if (visible) {
            hide();
        } else {
            show();
        }
    }
}