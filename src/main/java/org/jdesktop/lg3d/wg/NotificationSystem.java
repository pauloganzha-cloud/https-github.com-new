package org.jdesktop.lg3d.wg;

import java.util.*;
import org.jdesktop.lg3d.wg.components.*;
import org.jdesktop.lg3d.sg.*;

/**
 * NotificationSystem - system notifications and toasts.
 */
public class NotificationSystem {

    private static NotificationSystem instance;

    private final List<Notification> notifications;
    private final List<NotificationListener> listeners;
    private Component3D notificationContainer;
    private int maxVisible = 5;

    private NotificationSystem() {
        notifications = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public static NotificationSystem getInstance() {
        if (instance == null) {
            instance = new NotificationSystem();
        }
        return instance;
    }

    public void showNotification(String title, String message) {
        showNotification(title, message, NotificationType.INFO);
    }

    public void showNotification(String title, String message, NotificationType type) {
        Notification notification = new Notification(title, message, type);
        notifications.add(0, notification);

        if (notifications.size() > 20) {
            notifications.remove(notifications.size() - 1);
        }

        fireNotificationShown(notification);

        System.out.println("[Notification] " + type + ": " + title);
    }

    public void showInfo(String title, String message) {
        showNotification(title, message, NotificationType.INFO);
    }

    public void showSuccess(String title, String message) {
        showNotification(title, message, NotificationType.SUCCESS);
    }

    public void showWarning(String title, String message) {
        showNotification(title, message, NotificationType.WARNING);
    }

    public void showError(String title, String message) {
        showNotification(title, message, NotificationType.ERROR);
    }

    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public void clearNotifications() {
        notifications.clear();
        fireNotificationsCleared();
    }

    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    private void fireNotificationShown(Notification notification) {
        for (NotificationListener l : listeners) {
            l.notificationShown(notification);
        }
    }

    private void fireNotificationsCleared() {
        for (NotificationListener l : listeners) {
            l.notificationsCleared();
        }
    }

    public enum NotificationType {
        INFO, SUCCESS, WARNING, ERROR
    }
}

/**
 * Notification - a single notification.
 */
class Notification {

    public final String id;
    public final String title;
    public final String message;
    public final NotificationSystem.NotificationType type;
    public final long timestamp;
    public boolean dismissed = false;

    public Notification(String title, String message, NotificationSystem.NotificationType type) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public String getTimeAgo() {
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < 60000) return "Just now";
        if (diff < 3600000) return (diff / 60000) + " min ago";
        return (diff / 3600000) + " hours ago";
    }
}

interface NotificationListener {
    void notificationShown(Notification notification);
    void notificationsCleared();
}

/**
 * NotificationCenter - displays notifications in 3D.
 */
class NotificationCenter extends Component3D {

    private Container3D notificationList;
    private List<NotificationItem> items;
    private float width = 4.0f;
    private float height = 3.0f;

    public NotificationCenter() {
        super("NotificationCenter");
        items = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setSize(width, height);
        setVisible(false);

        notificationList = new Container3D("NotificationList");
        notificationList.setSize(width - 0.2f, height - 0.2f);
        addChild(notificationList);
    }

    public void addNotification(Notification notification) {
        NotificationItem item = new NotificationItem(notification);
        float y = height / 2 - 0.3f - items.size() * 0.6f;
        item.setTranslation(-width / 2 + 0.2f, y, 0.05f);
        items.add(item);
        notificationList.addChild(item);

        if (items.size() > 5) {
            NotificationItem removed = items.remove(0);
            notificationList.removeChild(removed);
        }
    }

    public void show() {
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }
}

/**
 * NotificationItem - displays a single notification.
 */
class NotificationItem extends Container3D {

    private final Notification notification;

    public NotificationItem(Notification notification) {
        super("NotificationItem-" + notification.id);
        this.notification = notification;
        initComponents();
    }

    private void initComponents() {
        setSize(3.6f, 0.5f);

        float[] color = getColorForType(notification.type);
        Component3D icon = new Component3D("NotificationIcon");
        icon.setSize(0.3f, 0.3f);
        icon.setTranslation(-1.5f, 0, 0);

        Appearance app = new Appearance();
        Material mat = new Material();
        mat.diffuse.set(color[0], color[1], color[2]);
        app.setMaterial(mat);
        icon.setAppearance(app);
        addChild(icon);

        Label3D titleLabel = new Label3D(notification.title);
        titleLabel.setTranslation(-1.0f, 0.1f, 0.02f);
        titleLabel.setFontSize(0.12f);
        addChild(titleLabel);

        Label3D messageLabel = new Label3D(truncate(notification.message, 30));
        messageLabel.setTranslation(-1.0f, -0.1f, 0.02f);
        messageLabel.setFontSize(0.09f);
        addChild(messageLabel);
    }

    private float[] getColorForType(NotificationSystem.NotificationType type) {
        switch (type) {
            case SUCCESS: return new float[]{0.2f, 0.7f, 0.3f};
            case WARNING: return new float[]{0.8f, 0.6f, 0.2f};
            case ERROR: return new float[]{0.8f, 0.3f, 0.3f};
            default: return new float[]{0.3f, 0.5f, 0.8f};
        }
    }

    private String truncate(String s, int max) {
        if (s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
    }
}

/**
 * Toast - temporary notification popup.
 */
class Toast {

    private String message;
    private long duration = 3000;
    private long showTime;

    public Toast(String message) {
        this.message = message;
    }

    public Toast(String message, long durationMs) {
        this.message = message;
        this.duration = durationMs;
    }

    public void show() {
        showTime = System.currentTimeMillis();
        NotificationSystem.getInstance().showInfo("LG3D", message);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - showTime > duration;
    }
}