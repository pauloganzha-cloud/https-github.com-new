package org.jdesktop.lg3d.utils;

import java.util.*;
import java.io.*;

/**
 * ClipboardManager - system clipboard management.
 */
public class ClipboardManager {

    private static ClipboardManager instance;

    private ClipboardData currentData;
    private final List<ClipboardData> history;
    private final int maxHistory = 20;
    private ClipboardListener listener;

    private ClipboardManager() {
        history = new ArrayList<>();
    }

    public static ClipboardManager getInstance() {
        if (instance == null) {
            instance = new ClipboardManager();
        }
        return instance;
    }

    public void setText(String text) {
        ClipboardData data = new ClipboardData(ClipboardData.Type.TEXT, text);
        setData(data);
    }

    public String getText() {
        if (currentData != null && currentData.type == ClipboardData.Type.TEXT) {
            return (String) currentData.data;
        }
        return null;
    }

    public void setImage(Object image) {
        ClipboardData data = new ClipboardData(ClipboardData.Type.IMAGE, image);
        setData(data);
    }

    public Object getImage() {
        if (currentData != null && currentData.type == ClipboardData.Type.IMAGE) {
            return currentData.data;
        }
        return null;
    }

    public void setFile(File file) {
        ClipboardData data = new ClipboardData(ClipboardData.Type.FILE, file);
        setData(data);
    }

    public File getFile() {
        if (currentData != null && currentData.type == ClipboardData.Type.FILE) {
            return (File) currentData.data;
        }
        return null;
    }

    public void setFiles(File[] files) {
        ClipboardData data = new ClipboardData(ClipboardData.Type.FILES, files);
        setData(data);
    }

    public File[] getFiles() {
        if (currentData != null && currentData.type == ClipboardData.Type.FILES) {
            return (File[]) currentData.data;
        }
        return null;
    }

    private void setData(ClipboardData data) {
        currentData = data;
        history.add(0, data);

        if (history.size() > maxHistory) {
            history.remove(history.size() - 1);
        }

        if (listener != null) {
            listener.clipboardChanged(data);
        }
    }

    public ClipboardData getData() {
        return currentData;
    }

    public boolean hasData() {
        return currentData != null;
    }

    public void clear() {
        currentData = null;
        if (listener != null) {
            listener.clipboardCleared();
        }
    }

    public List<ClipboardData> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void undo() {
        if (history.size() > 1) {
            currentData = history.get(1);
            if (listener != null) {
                listener.clipboardChanged(currentData);
            }
        }
    }

    public void setListener(ClipboardListener listener) {
        this.listener = listener;
    }
}

/**
 * Clipboard data container.
 */
class ClipboardData {

    public enum Type {
        TEXT, IMAGE, FILE, FILES, HTML
    }

    public final Type type;
    public final Object data;
    public final long timestamp;
    public final String mimeType;

    public ClipboardData(Type type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.mimeType = getMimeType(type);
    }

    private String getMimeType(Type type) {
        switch (type) {
            case TEXT: return "text/plain";
            case IMAGE: return "image/png";
            case FILE: return "application/octet-stream";
            case FILES: return "application/x-multiple-files";
            case HTML: return "text/html";
            default: return "application/octet-stream";
        }
    }

    public boolean isText() { return type == Type.TEXT; }
    public boolean isImage() { return type == Type.IMAGE; }
    public boolean isFile() { return type == Type.FILE; }
    public boolean isFiles() { return type == Type.FILES; }

    @Override
    public String toString() {
        return "ClipboardData[type=" + type + ", timestamp=" + timestamp + "]";
    }
}

interface ClipboardListener {
    void clipboardChanged(ClipboardData data);
    void clipboardCleared();
}

/**
 * Clipboard format utilities.
 */
class ClipboardFormats {

    public static final String PLAIN_TEXT = "text/plain";
    public static final String HTML = "text/html";
    public static final String URI_LIST = "text/uri-list";
    public static final String PNG = "image/png";
    public static final String JPEG = "image/jpeg";

    public static boolean isText(String mimeType) {
        return mimeType != null && mimeType.startsWith("text/");
    }

    public static boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }
}

/**
 * Drag and Drop support.
 */
class DragSource {

    private Object data;
    private Component3D dragComponent;
    private boolean dragging = false;
    private DragSourceListener listener;

    public void startDrag(Component3D component, Object data) {
        this.dragComponent = component;
        this.data = data;
        this.dragging = true;

        if (listener != null) {
            listener.dragStarted(this);
        }
    }

    public void endDrag() {
        this.dragging = false;
        this.dragComponent = null;

        if (listener != null) {
            listener.dragEnded(this);
        }
    }

    public Object getData() {
        return data;
    }

    public Component3D getDragComponent() {
        return dragComponent;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setListener(DragSourceListener listener) {
        this.listener = listener;
    }
}

interface DragSourceListener {
    void dragStarted(DragSource source);
    void dragEnded(DragSource source);
}

class DropTarget {

    private Component3D target;
    private DropTargetListener listener;
    private boolean acceptDrop = true;

    public void setTarget(Component3D component) {
        this.target = component;
    }

    public Component3D getTarget() {
        return target;
    }

    public boolean canAccept() {
        return acceptDrop && target != null;
    }

    public void acceptDrop(DragSource source) {
        if (canAccept() && listener != null) {
            listener.dropReceived(source, target);
        }
    }

    public void setAcceptDrops(boolean accept) {
        this.acceptDrop = accept;
    }

    public void setListener(DropTargetListener listener) {
        this.listener = listener;
    }
}

interface DropTargetListener {
    void dropReceived(DragSource source, Component3D target);
}

/**
 * Drag gesture recognizer - detects drag operations.
 */
class DragGestureRecognizer {

    private Component3D component;
    private DragSource dragSource;
    private int dragThreshold = 5;
    private int startX, startY;
    private boolean dragging = false;

    public void componentPressed(int x, int y) {
        startX = x;
        startY = y;
        dragging = false;
    }

    public void componentDragged(int x, int y) {
        if (dragging) return;

        int dx = Math.abs(x - startX);
        int dy = Math.abs(y - startY);

        if (dx > dragThreshold || dy > dragThreshold) {
            dragging = true;
            if (dragSource != null) {
                dragSource.startDrag(component, null);
            }
        }
    }

    public void componentReleased() {
        if (dragging && dragSource != null) {
            dragSource.endDrag();
        }
        dragging = false;
    }
}