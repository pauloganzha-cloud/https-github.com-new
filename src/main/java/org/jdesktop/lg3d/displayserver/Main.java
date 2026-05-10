/*
 * LG3D - Looking Glass 3D Desktop
 * Modernized version for Java 17+
 *
 * Original project by Sun Microsystems
 * Modernization by Project LG3D-Modern Team
 */
package org.jdesktop.lg3d.displayserver;

import org.jdesktop.lg3d.sg.*;
import org.jdesktop.lg3d.wg.*;

/**
 * Main entry point for the LG3D Display Server.
 */
public class Main {

    private static DisplayServer displayServer;

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  LG3D Modern - Project Looking Glass 3D Desktop");
        System.out.println("  Version: 1.0.0-modern (Java 17+)");
        System.out.println("=".repeat(60));

        printSystemInfo();

        try {
            initializeAndStart();
        } catch (Exception e) {
            System.err.println("Error starting LG3D: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printSystemInfo() {
        System.out.println("\n[System Information]");
        System.out.println("  Java Version: " + System.getProperty("java.version"));
        System.out.println("  Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("  Java Home: " + System.getProperty("java.home"));
        System.out.println("  OS Name: " + System.getProperty("os.name"));
        System.out.println("  OS Version: " + System.getProperty("os.version"));
        System.out.println("  OS Arch: " + System.getProperty("os.arch"));
        System.out.println("  Available Processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("  Max Memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB");
    }

    private static void initializeAndStart() throws Exception {
        System.out.println("\n[Initialization]");

        displayServer = DisplayServer.getInstance();
        displayServer.initialize();
        displayServer.setTargetFPS(60);

        createSampleWindow();

        System.out.println("\n[Starting Display Server]");
        displayServer.start();

        System.out.println("\n[LG3D Modern is running]");
        System.out.println("  Press Ctrl+C to stop...");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[Shutting down]");
            displayServer.stop();
            System.out.println("LG3D Modern stopped.");
        }));

        while (true) {
            Thread.sleep(1000);
        }
    }

    private static void createSampleWindow() {
        System.out.println("  Creating sample 3D window...");

        Window3D window = new Window3D("Sample Window");
        window.setTitle("LG3D Modern");
        window.setTransparency(0.1f);

        Component3D component = new Component3D("SampleComponent");
        component.setSize(2.0f, 1.5f);

        Appearance appearance = new Appearance();
        Material material = new Material();
        material.diffuse.set(0.3f, 0.5f, 0.8f);
        material.specular.set(0.8f, 0.8f, 0.8f);
        material.shininess = 64.0f;
        appearance.setMaterial(material);

        Shape3D shape = createBackgroundShape(2.0f, 1.5f);
        shape.setAppearance(appearance);
        component.addChild(shape);

        window.setComponent(component);
        window.moveTo(0, 0, 0);

        displayServer.addWindow(window);
    }

    private static Shape3D createBackgroundShape(float width, float height) {
        float hw = width / 2;
        float hh = height / 2;

        float[] coords = new float[]{
            -hw, -hh, 0,
            hw, -hh, 0,
            hw, hh, 0,
            -hw, hh, 0
        };

        float[] normals = new float[]{
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1
        };

        float[] colors = new float[]{
            0.2f, 0.3f, 0.5f, 1.0f,
            0.2f, 0.3f, 0.5f, 1.0f,
            0.2f, 0.3f, 0.5f, 1.0f,
            0.2f, 0.3f, 0.5f, 1.0f
        };

        int[] indices = new int[]{0, 1, 2, 0, 2, 3};

        TriangleArray geometry = new TriangleArray(4, 0);
        geometry.setCoordinates(coords);
        geometry.setNormals(normals);
        geometry.setColors(colors);

        Shape3D shape = new Shape3D(geometry);
        shape.setName("Background");

        return shape;
    }
}