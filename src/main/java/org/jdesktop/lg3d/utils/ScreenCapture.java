package org.jdesktop.lg3d.utils;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.nio.*;
import java.util.*;

/**
 * ScreenCapture - screenshot and recording functionality.
 */
public class ScreenCapture {

    private static ScreenCapture instance;

    private int captureWidth = 1920;
    private int captureHeight = 1080;
    private String saveDirectory = System.getProperty("user.home") + "/Pictures";

    private final List<CaptureListener> listeners;
    private boolean capturing = false;

    private ScreenCapture() {
        listeners = new ArrayList<>();
    }

    public static ScreenCapture getInstance() {
        if (instance == null) {
            instance = new ScreenCapture();
        }
        return instance;
    }

    public void setCaptureSize(int width, int height) {
        this.captureWidth = width;
        this.captureHeight = height;
    }

    public void setSaveDirectory(String directory) {
        this.saveDirectory = directory;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public BufferedImage captureScreen() {
        try {
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            return robot.createScreenCapture(screenRect);
        } catch (Exception e) {
            System.err.println("Failed to capture screen: " + e.getMessage());
            return null;
        }
    }

    public BufferedImage captureArea(int x, int y, int width, int height) {
        try {
            Robot robot = new Robot();
            Rectangle rect = new Rectangle(x, y, width, height);
            return robot.createScreenCapture(rect);
        } catch (Exception e) {
            System.err.println("Failed to capture area: " + e.getMessage());
            return null;
        }
    }

    public String saveScreenshot() {
        return saveScreenshot("screenshot");
    }

    public String saveScreenshot(String baseName) {
        BufferedImage image = captureScreen();
        if (image == null) return null;

        String filename = generateFilename(baseName, "png");
        String fullPath = saveDirectory + "/" + filename;

        try {
            File file = new File(saveDirectory);
            if (!file.exists()) {
                file.mkdirs();
            }

            javax.imageio.ImageIO.write(image, "png", new File(fullPath));
            System.out.println("[ScreenCapture] Saved: " + fullPath);

            fireScreenshotTaken(fullPath);
            return fullPath;
        } catch (Exception e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
            return null;
        }
    }

    public String captureAndSave() {
        return saveScreenshot();
    }

    public void captureWithDelay(int delayMs) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
                saveScreenshot();
            } catch (InterruptedException e) {
            }
        }).start();
    }

    public void startCapture() {
        capturing = true;
        System.out.println("[ScreenCapture] Capture started");
    }

    public void stopCapture() {
        capturing = false;
        System.out.println("[ScreenCapture] Capture stopped");
    }

    public boolean isCapturing() {
        return capturing;
    }

    public void addListener(CaptureListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CaptureListener listener) {
        listeners.remove(listener);
    }

    private void fireScreenshotTaken(String filename) {
        for (CaptureListener listener : listeners) {
            listener.screenshotTaken(filename);
        }
    }

    private String generateFilename(String baseName, String extension) {
        long timestamp = System.currentTimeMillis();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss");
        String time = sdf.format(new Date(timestamp));
        return baseName + "_" + time + "." + extension;
    }

    public void captureRegion(int x, int y, int width, int height, String filename) {
        BufferedImage image = captureArea(x, y, width, height);
        if (image == null) return;

        String fullPath = saveDirectory + "/" + filename;
        try {
            File file = new File(saveDirectory);
            if (!file.exists()) {
                file.mkdirs();
            }
            javax.imageio.ImageIO.write(image, "png", new File(fullPath));
            System.out.println("[ScreenCapture] Region saved: " + fullPath);
        } catch (Exception e) {
            System.err.println("Failed to save region: " + e.getMessage());
        }
    }

    public int[] getScreenSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new int[]{screenSize.width, screenSize.height};
    }
}

interface CaptureListener {
    void screenshotTaken(String filename);
}

/**
 * ScreenRecorder - records screen to video.
 */
class ScreenRecorder {

    private boolean recording = false;
    private String outputFile;
    private int fps = 30;
    private long startTime;
    private List<BufferedImage> frames;

    public void startRecording(String filename) {
        if (recording) return;

        this.outputFile = filename;
        this.frames = new ArrayList<>();
        this.startTime = System.currentTimeMillis();
        this.recording = true;

        System.out.println("[ScreenRecorder] Recording started: " + filename);

        new Thread(() -> {
            while (recording) {
                BufferedImage frame = ScreenCapture.getInstance().captureScreen();
                if (frame != null) {
                    frames.add(frame);
                }
                try {
                    Thread.sleep(1000 / fps);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public void stopRecording() {
        if (!recording) return;

        recording = false;
        System.out.println("[ScreenRecorder] Recording stopped. Frames: " + frames.size());

        saveVideo();
    }

    private void saveVideo() {
        System.out.println("[ScreenRecorder] Processing " + frames.size() + " frames...");
    }

    public boolean isRecording() {
        return recording;
    }

    public int getFrameCount() {
        return frames.size();
    }

    public long getDuration() {
        return System.currentTimeMillis() - startTime;
    }
}

/**
 * GifRecorder - records to animated GIF.
 */
class GifRecorder {

    private boolean recording = false;
    private String outputFile;
    private List<BufferedImage> frames;
    private int delay = 100;

    public void startRecording(String filename) {
        if (recording) return;

        this.outputFile = filename;
        this.frames = new ArrayList<>();
        this.recording = true;

        new Thread(() -> {
            while (recording) {
                BufferedImage frame = ScreenCapture.getInstance().captureScreen();
                if (frame != null) {
                    frames.add(frame);
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();

        System.out.println("[GifRecorder] Recording started: " + filename);
    }

    public void stopRecording() {
        recording = false;
        System.out.println("[GifRecorder] Recording stopped. Frames: " + frames.size());
    }

    public void setDelay(int ms) {
        this.delay = ms;
    }
}

/**
 * Screenshot keyboard shortcut handler.
 */
class ScreenshotShortcut {

    public static final int KEY_F12 = 123;
    public static final int KEY_PRINTSCREEN = 154;

    private ScreenCapture capture;
    private boolean enabled = true;

    public ScreenshotShortcut(ScreenCapture capture) {
        this.capture = capture;
    }

    public void handleKey(int keyCode) {
        if (!enabled) return;

        if (keyCode == KEY_F12 || keyCode == KEY_PRINTSCREEN) {
            capture.saveScreenshot();
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}