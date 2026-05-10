package org.jdesktop.lg3d.displayserver;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;

/**
 * Enhanced Demo - shows UI components and animations.
 */
public class EnhancedDemo {

    private static final float[] COLORS = {
        0.2f, 0.4f, 0.8f,
        0.8f, 0.3f, 0.2f,
        0.2f, 0.7f, 0.3f,
        0.7f, 0.2f, 0.7f,
        0.9f, 0.6f, 0.2f
    };

    public static void main(String[] args) {
        System.out.println("Starting LG3D Enhanced Demo...");
        SwingUtilities.invokeLater(EnhancedDemo::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("LG3D Modern - Enhanced Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        GLCanvas canvas = create3DCanvas();
        frame.add(canvas, BorderLayout.CENTER);

        JPanel controls = createControlPanel();
        frame.add(controls, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        final DemoRenderer renderer = new DemoRenderer();
        canvas.addGLEventListener(renderer);

        final GLCanvas c = canvas;
        new Thread(() -> {
            while (true) {
                try {
                    renderer.update();
                    c.display();
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private static GLCanvas create3DCanvas() {
        GLCapabilities caps = new GLCapabilities(null);
        caps.setDoubleBuffered(true);
        caps.setDepthBits(24);
        caps.setSampleBuffers(true);
        caps.setSamples(4);

        GLCanvas canvas = new GLCanvas(caps);
        canvas.setSize(1000, 600);
        return canvas;
    }

    private static JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(0x303040));
        panel.setPreferredSize(new Dimension(1000, 60));

        JLabel title = new JLabel("LG3D Modern - UI Components Demo");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(title);

        panel.add(Box.createHorizontalStrut(30));

        JButton addBtn = new JButton("Add Window");
        addBtn.addActionListener(e -> System.out.println("Add Window clicked"));
        panel.add(addBtn);

        JButton animBtn = new JButton("Run Animation");
        animBtn.addActionListener(e -> System.out.println("Animation triggered"));
        panel.add(animBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> System.out.println("Reset clicked"));
        panel.add(resetBtn);

        panel.add(Box.createHorizontalStrut(30));

        JCheckBox check = new JCheckBox("Show Grid");
        check.setSelected(true);
        check.setForeground(Color.WHITE);
        panel.add(check);

        return panel;
    }
}

class DemoRenderer implements GLEventListener {

    private float rotation = 0;
    private float cameraZ = -15;
    private final DemoWindow[] windows = new DemoWindow[8];

    DemoRenderer() {
        for (int i = 0; i < windows.length; i++) {
            windows[i] = new DemoWindow(
                (float) (Math.random() * 8 - 4),
                (float) (Math.random() * 6 - 3),
                (float) (Math.random() * 4 - 2),
                1.5f + (float) Math.random(),
                1.0f + (float) Math.random() * 0.5f,
                COLORS[(i * 3) % COLORS.length],
                COLORS[(i * 3 + 1) % COLORS.length],
                COLORS[(i * 3 + 2) % COLORS.length]
            );
        }
    }

    void update() {
        rotation += 0.3f;

        for (int i = 0; i < windows.length; i++) {
            DemoWindow w = windows[i];
            w.y += (float) Math.sin(rotation * 0.02 + i) * 0.005f;
            w.angle += 0.5f;
        }
    }

    @Override
    public void init(GLAutoDrawable d) {
        GL2 gl = d.getGL().getGL2();

        System.out.println("GL: " + gl.glGetString(GL.GL_VERSION));

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_NORMALIZE);

        gl.glClearColor(0.08f, 0.08f, 0.12f, 1.0f);

        float[] ambient = {0.3f, 0.3f, 0.35f, 1.0f};
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambient, 0);

        float[] diffuse = {0.9f, 0.9f, 0.9f, 1.0f};
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse, 0);

        float[] position = {5, 10, 10, 0};
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);

        gl.glEnable(GL.GL_LIGHT0);
    }

    @Override
    public void dispose(GLAutoDrawable d) {
    }

    @Override
    public void display(GLAutoDrawable d) {
        GL2 gl = d.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        double aspect = (double) d.getWidth() / d.getHeight();
        gl.glFrustum(-aspect * 5, aspect * 5, -5, 5, 1, 50);

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, cameraZ);
        gl.glRotatef(10, 1, 0, 0);

        drawFloor(gl);

        for (DemoWindow w : windows) {
            drawWindow(gl, w);
        }

        drawTitleBar(gl);
    }

    private void drawFloor(GL2 gl) {
        gl.glDisable(GL.GL_LIGHTING);

        gl.glColor3f(0.12f, 0.12f, 0.18f);
        gl.glBegin(GL.GL_QUADS);
        for (int x = -10; x < 10; x++) {
            for (int z = -10; z < 10; z++) {
                if ((x + z) % 2 == 0) {
                    gl.glVertex3f(x, -4, z);
                    gl.glVertex3f(x + 1, -4, z);
                    gl.glVertex3f(x + 1, -4, z + 1);
                    gl.glVertex3f(x, -4, z + 1);
                }
            }
        }
        gl.glEnd();

        gl.glEnable(GL.GL_LIGHTING);
    }

    private void drawWindow(GL2 gl, DemoWindow w) {
        gl.glPushMatrix();
        gl.glTranslatef(w.x, w.y, w.z);

        float angle = (float) Math.toRadians(w.angle);
        gl.glRotatef(w.angle * 0.1f, 0, 1, 0);

        float hw = w.width / 2;
        float hh = w.height / 2;

        float r = w.r, g = w.g, b = w.b;

        gl.glColor3f(r * 0.3f, g * 0.3f, b * 0.3f);
        drawBox(gl, hw, hh, 0.15f);

        gl.glColor3f(r * 0.8f, g * 0.8f, b * 0.8f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex3f(-hw + 0.05f, hh - 0.15f, 0.08f);
        gl.glVertex3f(hw - 0.05f, hh - 0.15f, 0.08f);
        gl.glVertex3f(hw - 0.05f, -hh + 0.05f, 0.08f);
        gl.glVertex3f(-hw + 0.05f, -hh + 0.05f, 0.08f);
        gl.glEnd();

        gl.glColor3f(r, g, b);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex3f(-hw + 0.1f, hh - 0.2f, 0.1f);
        gl.glVertex3f(hw - 0.1f, hh - 0.2f, 0.1f);
        gl.glVertex3f(hw - 0.1f, -hh + 0.1f, 0.1f);
        gl.glVertex3f(-hw + 0.1f, -hh + 0.1f, 0.1f);
        gl.glEnd();

        drawWindowChrome(gl, hw, hh);

        gl.glPopMatrix();
    }

    private void drawWindowChrome(GL2 gl, float hw, float hh) {
        gl.glColor3f(0.4f, 0.5f, 0.7f);

        float[] topBar = {
            -hw, hh, 0.05f,
            hw, hh, 0.05f,
            hw, hh + 0.08f, 0.05f,
            -hw, hh + 0.08f, 0.05f
        };

        gl.glBegin(GL.GL_QUADS);
        gl.glVertex3f(-hw, hh, 0.05f);
        gl.glVertex3f(hw, hh, 0.05f);
        gl.glVertex3f(hw, hh + 0.1f, 0.05f);
        gl.glVertex3f(-hw, hh + 0.1f, 0.05f);
        gl.glEnd();

        float[] closeBtn = {
            hw - 0.2f, hh + 0.02f, 0.12f,
            hw - 0.08f, hh + 0.02f, 0.12f,
            hw - 0.08f, hh + 0.07f, 0.12f,
            hw - 0.2f, hh + 0.07f, 0.12f
        };

        gl.glColor3f(0.9f, 0.3f, 0.3f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex3f(closeBtn[0], closeBtn[1], closeBtn[2]);
        gl.glVertex3f(closeBtn[3], closeBtn[4], closeBtn[5]);
        gl.glVertex3f(closeBtn[6], closeBtn[7], closeBtn[8]);
        gl.glVertex3f(closeBtn[9], closeBtn[10], closeBtn[11]);
        gl.glEnd();
    }

    private void drawBox(GL2 gl, float hw, float hh, float d) {
        gl.glBegin(GL.GL_QUADS);

        gl.glVertex3f(-hw, -hh, d);
        gl.glVertex3f(hw, -hh, d);
        gl.glVertex3f(hw, hh, d);
        gl.glVertex3f(-hw, hh, d);

        gl.glVertex3f(-hw, -hh, -d);
        gl.glVertex3f(-hw, hh, -d);
        gl.glVertex3f(hw, hh, -d);
        gl.glVertex3f(hw, -hh, -d);

        gl.glVertex3f(-hw, hh, d);
        gl.glVertex3f(hw, hh, d);
        gl.glVertex3f(hw, hh, -d);
        gl.glVertex3f(-hw, hh, -d);

        gl.glVertex3f(-hw, -hh, d);
        gl.glVertex3f(-hw, -hh, -d);
        gl.glVertex3f(hw, -hh, -d);
        gl.glVertex3f(hw, -hh, d);

        gl.glVertex3f(hw, -hh, d);
        gl.glVertex3f(hw, -hh, -d);
        gl.glVertex3f(hw, hh, -d);
        gl.glVertex3f(hw, hh, d);

        gl.glVertex3f(-hw, -hh, d);
        gl.glVertex3f(-hw, hh, d);
        gl.glVertex3f(-hw, hh, -d);
        gl.glVertex3f(-hw, -hh, -d);

        gl.glEnd();
    }

    private void drawTitleBar(GL2 gl) {
    }

    @Override
    public void reshape(GLAutoDrawable d, int x, int y, int w, int h) {
        GL2 gl = d.getGL().getGL2();
        gl.glViewport(0, 0, w, h);
    }
}

class DemoWindow {
    float x, y, z;
    float width, height;
    float r, g, b;
    float angle = 0;

    DemoWindow(float x, float y, float z, float w, float h, float r, float g, float b) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = w;
        this.height = h;
        this.r = r;
        this.g = g;
        this.b = b;
    }
}