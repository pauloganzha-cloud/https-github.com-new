package org.jdesktop.lg3d.wg;

import java.util.*;
import java.util.concurrent.*;

/**
 * WindowManager - manages all windows in the system.
 */
public class WindowManager {

    private static WindowManager instance;

    private final ConcurrentHashMap<Long, Window3D> windows;
    private final List<WindowManagerListener> listeners;
    private Window3D activeWindow;
    private Window3D focusedWindow;
    private long nextWindowId = 1;

    private boolean running = false;
    private Thread eventThread;

    private WindowManager() {
        windows = new ConcurrentHashMap<>();
        listeners = new ArrayList<>();
    }

    public static WindowManager getInstance() {
        if (instance == null) {
            instance = new WindowManager();
        }
        return instance;
    }

    public void start() {
        if (running) return;
        running = true;
        eventThread = new Thread(this::eventLoop, "WindowManager");
        eventThread.setDaemon(true);
        eventThread.start();
        System.out.println("[WindowManager] Started");
    }

    public void stop() {
        running = false;
        if (eventThread != null) {
            eventThread.interrupt();
        }
        System.out.println("[WindowManager] Stopped");
    }

    private void eventLoop() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                processWindowEvents();
                Thread.sleep(16);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void processWindowEvents() {
        for (Window3D win : windows.values()) {
        }
    }

    public Window3D createWindow() {
        return createWindow("Window-" + nextWindowId++);
    }

    public Window3D createWindow(String title) {
        Window3D window = new Window3D(title);
        window.setTitle(title);
        window.setWindowId(nextWindowId++);

        WindowDecorator decorator = new WindowDecorator(window);
        window.setDecorator(decorator);

        windows.put(window.getId(), window);

        fireWindowCreated(window);
        System.out.println("[WindowManager] Created window: " + title);

        return window;
    }

    public void destroyWindow(Window3D window) {
        if (window == null) return;

        if (focusedWindow == window) {
            focusWindow(null);
        }
        if (activeWindow == window) {
            activeWindow = null;
        }

        windows.remove(window.getId());
        window.destroy();

        fireWindowDestroyed(window);
        System.out.println("[WindowManager] Destroyed window: " + window.getTitle());
    }

    public void focusWindow(Window3D window) {
        if (focusedWindow != null && focusedWindow != window) {
            focusedWindow.setFocused(false);
        }

        focusedWindow = window;

        if (window != null) {
            window.setFocused(true);
            bringToFront(window);
        }

        fireWindowFocused(window);
    }

    public void bringToFront(Window3D window) {
        if (activeWindow != null && activeWindow != window) {
            activeWindow.setActive(false);
        }

        activeWindow = window;
        window.setActive(true);
        window.raise();

        fireWindowActivated(window);
    }

    public void sendToBack(Window3D window) {
        window.lower();
        if (activeWindow == window) {
            activeWindow = null;
        }
    }

    public Window3D getWindow(long windowId) {
        return windows.get(windowId);
    }

    public Window3D getFocusedWindow() {
        return focusedWindow;
    }

    public Window3D getActiveWindow() {
        return activeWindow;
    }

    public Collection<Window3D> getAllWindows() {
        return windows.values();
    }

    public List<Window3D> getOrderedWindows() {
        List<Window3D> list = new ArrayList<>(windows.values());
        Collections.sort(list, (w1, w2) -> {
            if (w1.isActive() && !w2.isActive()) return -1;
            if (!w1.isActive() && w2.isActive()) return 1;
            return 0;
        });
        return list;
    }

    public void tileWindows(TileDirection direction) {
        List<Window3D> visible = new ArrayList<>();
        for (Window3D w : windows.values()) {
            if (w.isVisible()) visible.add(w);
        }

        if (visible.isEmpty()) return;

        int count = visible.size();
        int cols = (int) Math.ceil(Math.sqrt(count));
        int rows = (int) Math.ceil((double) count / cols);

        int screenWidth = 1920;
        int screenHeight = 1080;
        int windowWidth = screenWidth / cols;
        int windowHeight = screenHeight / rows;

        for (int i = 0; i < visible.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            Window3D w = visible.get(i);
            w.setPosition(col * windowWidth, row * windowHeight);
            w.setSize(windowWidth - 10, windowHeight - 30);
        }
    }

    public void cascadeWindows() {
        List<Window3D> visible = new ArrayList<>();
        for (Window3D w : windows.values()) {
            if (w.isVisible()) visible.add(w);
        }

        int offset = 30;
        int startX = 50;
        int startY = 50;
        int width = 800;
        int height = 600;

        for (int i = 0; i < visible.size(); i++) {
            Window3D w = visible.get(i);
            w.setPosition(startX + i * offset, startY + i * offset);
            w.setSize(width, height);
        }
    }

    public void minimizeAll() {
        for (Window3D w : windows.values()) {
            w.setMinimized(true);
        }
    }

    public void restoreAll() {
        for (Window3D w : windows.values()) {
            w.setMinimized(false);
        }
    }

    public void closeAll() {
        for (Window3D w : new ArrayList<>(windows.values())) {
            destroyWindow(w);
        }
    }

    public void addListener(WindowManagerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WindowManagerListener listener) {
        listeners.remove(listener);
    }

    private void fireWindowCreated(Window3D window) {
        for (WindowManagerListener l : listeners) {
            l.windowCreated(window);
        }
    }

    private void fireWindowDestroyed(Window3D window) {
        for (WindowManagerListener l : listeners) {
            l.windowDestroyed(window);
        }
    }

    private void fireWindowFocused(Window3D window) {
        for (WindowManagerListener l : listeners) {
            l.windowFocused(window);
        }
    }

    private void fireWindowActivated(Window3D window) {
        for (WindowManagerListener l : listeners) {
            l.windowActivated(window);
        }
    }

    public enum TileDirection {
        HORIZONTAL, VERTICAL, GRID
    }
}

interface WindowManagerListener {
    void windowCreated(Window3D window);
    void windowDestroyed(Window3D window);
    void windowFocused(Window3D window);
    void windowActivated(Window3D window);
}

/**
 * WindowDecorator - adds window chrome/decorations.
 */
class WindowDecorator {

    private final Window3D window;
    private Component3D titleBar;
    private Component3D closeButton;
    private Component3D minimizeButton;
    private Component3D maximizeButton;
    private Component3D contentPane;

    private float titleBarHeight = 0.25f;
    private float buttonSize = 0.2f;

    public WindowDecorator(Window3D window) {
        this.window = window;
        createDecorations();
    }

    private void createDecorations() {
        Component3D container = new Container3D("WindowDecorations");

        titleBar = new Component3D("TitleBar");
        titleBar.setSize(window.getWidth(), titleBarHeight);
        titleBar.setTranslation(0, window.getHeight() / 2 - titleBarHeight / 2, 0);
        container.addChild(titleBar);

        closeButton = new Component3D("CloseButton");
        closeButton.setSize(buttonSize, buttonSize);
        closeButton.setTranslation(window.getWidth() / 2 - buttonSize - 0.05f,
                                   window.getHeight() / 2 - titleBarHeight / 2, 0.05f);
        container.addChild(closeButton);

        minimizeButton = new Component3D("MinimizeButton");
        minimizeButton.setSize(buttonSize, buttonSize);
        minimizeButton.setTranslation(window.getWidth() / 2 - buttonSize * 2 - 0.15f,
                                      window.getHeight() / 2 - titleBarHeight / 2, 0.05f);
        container.addChild(minimizeButton);

        maximizeButton = new Component3D("MaximizeButton");
        maximizeButton.setSize(buttonSize, buttonSize);
        maximizeButton.setTranslation(window.getWidth() / 2 - buttonSize * 3 - 0.25f,
                                       window.getHeight() / 2 - titleBarHeight / 2, 0.05f);
        container.addChild(maximizeButton);

        contentPane = new Container3D("ContentPane");
        contentPane.setSize(window.getWidth(), window.getHeight() - titleBarHeight - 0.1f);
        contentPane.setTranslation(0, -titleBarHeight / 2 - 0.05f, 0);
        container.addChild(contentPane);
    }

    public void setContent(Component3D component) {
        contentPane.addChild(component);
    }

    public Component3D getContentPane() {
        return contentPane;
    }
}

/**
 * Window position/size helper.
 */
class WindowPositioner {

    public static final int SNAP_MARGIN = 20;

    public static boolean isSnappedLeft(Window3D window) {
        return window.getX() <= SNAP_MARGIN;
    }

    public static boolean isSnappedRight(Window3D window, int screenWidth) {
        return window.getX() + window.getWidth() >= screenWidth - SNAP_MARGIN;
    }

    public static boolean isSnappedTop(Window3D window) {
        return window.getY() <= SNAP_MARGIN;
    }

    public static boolean isSnappedBottom(Window3D window, int screenHeight) {
        return window.getY() + window.getHeight() >= screenHeight - SNAP_MARGIN;
    }

    public static void snapToEdge(Window3D window, int screenWidth, int screenHeight) {
        if (isSnappedLeft(window)) {
            window.setPosition(0, window.getY());
            window.setSize(screenWidth / 2, window.getHeight());
        } else if (isSnappedRight(window, screenWidth)) {
            window.setPosition(screenWidth / 2, window.getY());
            window.setSize(screenWidth / 2, window.getHeight());
        }

        if (isSnappedTop(window)) {
            window.setPosition(window.getX(), 0);
            if (isSnappedLeft(window) || isSnappedRight(window, screenWidth)) {
                window.setSize(screenWidth, screenHeight / 2);
            }
        }
    }
}