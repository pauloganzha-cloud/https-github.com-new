package org.jdesktop.lg3d.displayserver.x11;

import java.util.*;
import java.util.concurrent.*;

/**
 * X11 Display - manages connection to X server.
 */
public class X11Display {

    private static X11Display instance;
    private long displayPointer = 0;
    private boolean connected = false;
    private String displayName = ":0";
    private int screenNumber = 0;
    private long rootWindow = 0;

    private final ConcurrentHashMap<Long, X11Window> windows;
    private final BlockingQueue<X11Event> eventQueue;
    private Thread eventThread;

    private X11Display() {
        windows = new ConcurrentHashMap<>();
        eventQueue = new LinkedBlockingQueue<>();
    }

    public static X11Display getInstance() {
        if (instance == null) {
            instance = new X11Display();
        }
        return instance;
    }

    public boolean connect() {
        return connect(displayName);
    }

    public boolean connect(String displayName) {
        if (connected) {
            return true;
        }

        this.displayName = displayName;
        displayPointer = nativeConnect(displayName);

        if (displayPointer != 0) {
            connected = true;
            screenNumber = nativeGetScreen(displayPointer);
            rootWindow = nativeGetRootWindow(displayPointer, screenNumber);
            startEventLoop();
            System.out.println("[X11Display] Connected to " + displayName);
            return true;
        }

        System.err.println("[X11Display] Failed to connect to " + displayName);
        return false;
    }

    public void disconnect() {
        if (!connected) return;

        stopEventLoop();

        for (X11Window win : windows.values()) {
            win.destroy();
        }
        windows.clear();

        nativeDisconnect(displayPointer);
        displayPointer = 0;
        connected = false;

        System.out.println("[X11Display] Disconnected");
    }

    public boolean isConnected() {
        return connected;
    }

    public long getDisplayPointer() {
        return displayPointer;
    }

    public int getScreenNumber() {
        return screenNumber;
    }

    public long getRootWindow() {
        return rootWindow;
    }

    public String getDisplayName() {
        return displayName;
    }

    public X11Window createWindow(long parent, int x, int y, int width, int height) {
        if (!connected) return null;

        long windowId = nativeCreateWindow(displayPointer, parent, x, y, width, height);
        if (windowId == 0) return null;

        X11Window win = new X11Window(windowId, parent, x, y, width, height);
        windows.put(windowId, win);
        return win;
    }

    public X11Window getWindow(long windowId) {
        return windows.get(windowId);
    }

    public void removeWindow(long windowId) {
        windows.remove(windowId);
    }

    public Collection<X11Window> getWindows() {
        return windows.values();
    }

    private void startEventLoop() {
        eventThread = new Thread(this::eventLoop, "LG3D-X11Events");
        eventThread.setDaemon(true);
        eventThread.start();
    }

    private void stopEventLoop() {
        if (eventThread != null) {
            eventThread.interrupt();
            eventThread = null;
        }
    }

    private void eventLoop() {
        while (connected && !Thread.currentThread().isInterrupted()) {
            try {
                X11Event event = pollEvent();
                if (event != null) {
                    handleEvent(event);
                }
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private X11Event pollEvent() {
        if (!connected) return null;
        return nativePollEvent(displayPointer);
    }

    private void handleEvent(X11Event event) {
        if (event == null) return;

        X11Window win = windows.get(event.windowId);
        if (win != null) {
            win.handleEvent(event);
        }

        eventQueue.offer(event);
    }

    public X11Event pollEventFromQueue() {
        return eventQueue.poll();
    }

    public X11Event takeEvent() throws InterruptedException {
        return eventQueue.take();
    }

    // Native methods (will be implemented via JNI or alternative)
    private native long nativeConnect(String displayName);
    private native void nativeDisconnect(long display);
    private native int nativeGetScreen(long display);
    private native long nativeGetRootWindow(long display, int screen);
    private native long nativeCreateWindow(long display, long parent, int x, int y, int w, int h);
    private native X11Event nativePollEvent(long display);

    /**
     * Check if X11 is available on this system.
     */
    public static boolean isX11Available() {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("linux") && !os.contains("unix")) {
            return false;
        }
        String x11 = System.getenv("DISPLAY");
        return x11 != null && !x11.isEmpty();
    }

    /**
     * Get X11 server info.
     */
    public String getServerInfo() {
        if (!connected) return "Not connected";
        return "X11 Server: " + displayName + " Screen: " + screenNumber;
    }
}

/**
 * X11 Window representation.
 */
class X11Window {

    private final long windowId;
    private final long parentId;
    private int x, y;
    private int width, height;
    private String title = "";
    private boolean visible = true;
    private boolean mapped = false;
    private X11WindowListener listener;

    public X11Window(long windowId, long parentId, int x, int y, int w, int h) {
        this.windowId = windowId;
        this.parentId = parentId;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public long getWindowId() {
        return windowId;
    }

    public long getParentId() {
        return parentId;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void map() {
        mapped = true;
    }

    public void unmap() {
        mapped = false;
    }

    public boolean isMapped() {
        return mapped;
    }

    public void setListener(X11WindowListener listener) {
        this.listener = listener;
    }

    void handleEvent(X11Event event) {
        if (listener != null) {
            listener.handleEvent(this, event);
        }
    }

    public void destroy() {
        if (listener != null) {
            listener.windowDestroyed(this);
        }
    }
}

/**
 * X11 Event wrapper.
 */
class X11Event {

    public static final int KEY_PRESS = 2;
    public static final int KEY_RELEASE = 3;
    public static final int BUTTON_PRESS = 4;
    public static final int BUTTON_RELEASE = 5;
    public static final int MOTION_NOTIFY = 6;
    public static final int EXPOSE = 12;
    public static final int RESIZE = 22;
    public static final int DESTROY = 17;
    public static final int FOCUS_IN = 9;
    public static final int FOCUS_OUT = 10;

    public int type;
    public long windowId;
    public long subwindow;
    public int x, y;
    public int width, height;
    public int keyCode;
    public int keyState;
    public int button;
    public int buttonState;
    public long timestamp;

    @Override
    public String toString() {
        return "X11Event[type=" + type + ", win=" + windowId + ", x=" + x + ", y=" + y + "]";
    }
}

/**
 * X11 Window event listener.
 */
interface X11WindowListener {
    void handleEvent(X11Window window, X11Event event);
    void windowDestroyed(X11Window window);
}