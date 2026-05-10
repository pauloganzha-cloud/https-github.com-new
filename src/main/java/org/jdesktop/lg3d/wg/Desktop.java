package org.jdesktop.lg3d.wg;

import java.util.*;
import org.jdesktop.lg3d.sg.*;

/**
 * Desktop - the main desktop area with icons and widgets.
 */
public class Desktop extends Component3D {

    private static Desktop instance;

    private final Map<String, DesktopIcon> icons;
    private final List<DesktopWidget> widgets;
    private Container3D iconContainer;
    private Container3D widgetContainer;
    private float width = 20f;
    private float height = 15f;
    private float iconSpacing = 1.2f;

    private Desktop() {
        super("Desktop");
        icons = new LinkedHashMap<>();
        widgets = new ArrayList<>();
        init();
    }

    public static Desktop getInstance() {
        if (instance == null) {
            instance = new Desktop();
        }
        return instance;
    }

    private void init() {
        iconContainer = new Container3D("IconContainer");
        iconContainer.setSize(width, height);
        addChild(iconContainer);

        widgetContainer = new Container3D("WidgetContainer");
        widgetContainer.setSize(width, height);
        addChild(widgetContainer);
    }

    public void addIcon(DesktopIcon icon) {
        icons.put(icon.getId(), icon);
        repositionIcon(icon);
        iconContainer.addChild(icon);
    }

    public void removeIcon(String iconId) {
        DesktopIcon icon = icons.remove(iconId);
        if (icon != null) {
            iconContainer.removeChild(icon);
        }
    }

    public DesktopIcon getIcon(String iconId) {
        return icons.get(iconId);
    }

    public Collection<DesktopIcon> getAllIcons() {
        return icons.values();
    }

    private void repositionIcon(DesktopIcon icon) {
        int index = icons.size() - 1;
        int col = index % 5;
        int row = index / 5;

        float x = -width / 2 + 1.0f + col * iconSpacing;
        float y = height / 2 - 1.5f - row * iconSpacing;
        icon.setTranslation(x, y, 0);
    }

    public void addWidget(DesktopWidget widget) {
        widgets.add(widget);
        widgetContainer.addChild(widget);
    }

    public void removeWidget(DesktopWidget widget) {
        widgets.remove(widget);
        widgetContainer.removeChild(widget);
    }

    public List<DesktopWidget> getWidgets() {
        return widgets;
    }

    public void setGridSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void arrangeIcons() {
        int i = 0;
        for (DesktopIcon icon : icons.values()) {
            int col = i % 5;
            int row = i / 5;

            float x = -width / 2 + 1.0f + col * iconSpacing;
            float y = height / 2 - 1.5f - row * iconSpacing;
            icon.setTranslation(x, y, 0);
            i++;
        }
    }

    public void refreshDesktop() {
        arrangeIcons();
    }
}

/**
 * DesktopIcon - icon on the desktop.
 */
class DesktopIcon extends Component3D {

    private final String id;
    private String name;
    private String iconPath;
    private String action;
    private boolean selected = false;

    public DesktopIcon(String id, String name, String iconPath) {
        super("DesktopIcon-" + id);
        this.id = id;
        this.name = name;
        this.iconPath = iconPath;
        initComponents();
    }

    private void initComponents() {
        setSize(1.0f, 1.2f);

        Component3D iconImage = new Component3D("IconImage");
        iconImage.setSize(0.8f, 0.8f);
        iconImage.setTranslation(0, 0.15f, 0.02f);
        addChild(iconImage);

        Label3D label = new Label3D(name);
        label.setTranslation(0, -0.35f, 0.02f);
        label.setFontSize(0.12f);
        addChild(label);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void execute() {
        System.out.println("[DesktopIcon] Executing: " + action);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setHighlighted(selected);
    }

    public boolean isSelected() {
        return selected;
    }
}

/**
 * DesktopWidget - interactive widget on the desktop.
 */
class DesktopWidget extends Component3D {

    private String widgetId;
    private boolean enabled = true;
    private WidgetUpdateListener updateListener;

    public DesktopWidget(String id) {
        super("DesktopWidget-" + id);
        this.widgetId = id;
    }

    public String getWidgetId() {
        return widgetId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setUpdateListener(WidgetUpdateListener listener) {
        this.updateListener = listener;
    }

    public void requestUpdate() {
        if (updateListener != null) {
            updateListener.widgetUpdated(this);
        }
    }
}

/**
 * ClockWidget - shows date/time on desktop.
 */
class ClockWidget extends DesktopWidget {

    private Label3D timeLabel;
    private Label3D dateLabel;
    private Thread clockThread;

    public ClockWidget() {
        super("clock");
        initClockWidget();
    }

    private void initClockWidget() {
        setSize(2.0f, 1.0f);
        setTranslation(7.0f, 6.0f, 0);

        timeLabel = new Label3D("12:00");
        timeLabel.setTranslation(-0.7f, 0.2f, 0.05f);
        timeLabel.setFontSize(0.4f);
        addChild(timeLabel);

        dateLabel = new Label3D("Jan 1, 2026");
        dateLabel.setTranslation(-0.9f, -0.1f, 0.05f);
        dateLabel.setFontSize(0.15f);
        addChild(dateLabel);

        startClock();
    }

    private void startClock() {
        clockThread = new Thread(() -> {
            while (true) {
                updateTime();
                try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
            }
        });
        clockThread.setDaemon(true);
        clockThread.start();
    }

    private void updateTime() {
        java.util.Date now = new java.util.Date();
        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM d, yyyy");
        timeLabel.setText(timeFormat.format(now));
        dateLabel.setText(dateFormat.format(now));
    }
}

/**
 * WeatherWidget - displays weather information.
 */
class WeatherWidget extends DesktopWidget {

    private String location = "Loading...";
    private float temperature = 0;
    private String condition = "Unknown";

    public WeatherWidget() {
        super("weather");
        initWeatherWidget();
    }

    private void initWeatherWidget() {
        setSize(2.0f, 1.5f);
        setTranslation(5.0f, 5.5f, 0);

        Label3D locationLabel = new Label3D(location);
        locationLabel.setTranslation(-0.8f, 0.4f, 0.05f);
        locationLabel.setFontSize(0.15f);
        addChild(locationLabel);

        Label3D tempLabel = new Label3D("--°C");
        tempLabel.setTranslation(-0.4f, 0.1f, 0.05f);
        tempLabel.setFontSize(0.3f);
        addChild(tempLabel);
    }

    public void updateWeather(String location, float temp, String condition) {
        this.location = location;
        this.temperature = temp;
        this.condition = condition;
        requestUpdate();
    }
}

/**
 * SystemMonitorWidget - shows system stats.
 */
class SystemMonitorWidget extends DesktopWidget {

    private Label3D cpuLabel;
    private Label3D memLabel;

    public SystemMonitorWidget() {
        super("system");
        initMonitorWidget();
    }

    private void initMonitorWidget() {
        setSize(2.0f, 1.0f);
        setTranslation(-7.0f, 6.0f, 0);

        cpuLabel = new Label3D("CPU: --%");
        cpuLabel.setTranslation(-0.8f, 0.2f, 0.05f);
        cpuLabel.setFontSize(0.12f);
        addChild(cpuLabel);

        memLabel = new Label3D("Memory: --%");
        memLabel.setTranslation(-0.9f, 0f, 0.05f);
        memLabel.setFontSize(0.12f);
        addChild(memLabel);
    }

    public void updateStats(int cpuPercent, int memPercent) {
        cpuLabel.setText("CPU: " + cpuPercent + "%");
        memLabel.setText("Memory: " + memPercent + "%");
    }
}

interface WidgetUpdateListener {
    void widgetUpdated(DesktopWidget widget);
}