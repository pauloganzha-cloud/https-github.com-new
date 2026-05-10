package org.jdesktop.lg3d.displayserver;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

/**
 * Demo launcher - runs a simple LG3D demo.
 * Useful for quick testing without full X11 setup.
 */
public class DemoLauncher {

    public static void main(String[] args) {
        System.out.println("Starting LG3D Modern Demo...");

        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("LG3D Modern - Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);

        GLCanvas canvas = create3DCanvas();
        frame.add(canvas, BorderLayout.CENTER);

        JPanel controls = createControlPanel(canvas);
        frame.add(controls, BorderLayout.SOUTH);

        frame.setVisible(true);

        final GLCanvas c = canvas;
        canvas.addGLEventListener(new GLEventListener() {
            private float rotX = 0, rotY = 0;
            private float zoom = -8;

            @Override
            public void init(GLAutoDrawable d) {
                GL2 gl = d.getGL().getGL2();
                gl.glEnable(GL.GL_DEPTH_TEST);
                gl.glEnable(GL.GL_CULL_FACE);
                gl.glClearColor(0.1f, 0.12f, 0.18f, 1.0f);

                gl.glEnable(GL.GL_LIGHTING);
                float[] ambient = {0.3f, 0.3f, 0.3f, 1.0f};
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambient, 0);

                float[] diffuse = {0.8f, 0.8f, 0.8f, 1.0f};
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse, 0);
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
                double fov = 60.0 * Math.PI / 180.0;
                double near = 0.1;
                double far = 100.0;
                double h = near * Math.tan(fov / 2);
                double w = h * aspect;
                gl.glFrustum(-w, w, -h, h, near, far);

                gl.glMatrixMode(GL.GL_MODELVIEW);
                gl.glLoadIdentity();
                gl.glTranslatef(0, 0, (float) zoom);
                gl.glRotatef(rotX, 1, 0, 0);
                gl.glRotatef(rotY, 0, 1, 0);

                drawScene(gl);

                rotY += 0.5f;
            }

            private void drawScene(GL2 gl) {
                drawFloor(gl);

                drawWindowFrame(gl, 0, 0, 0, 0.8f, 0.6f);
                drawWindowFrame(gl, 2, 0.5f, 0, 0.6f, 0.8f);
                drawWindowFrame(gl, -1.5f, -0.5f, 0.5f, 1.0f, 0.5f);
            }

            private void drawFloor(GL2 gl) {
                gl.glDisable(GL.GL_LIGHTING);
                gl.glColor3f(0.15f, 0.15f, 0.2f);
                gl.glBegin(GL.GL_QUADS);
                gl.glVertex3f(-10, -2, -10);
                gl.glVertex3f(10, -2, -10);
                gl.glVertex3f(10, -2, 10);
                gl.glVertex3f(-10, -2, 10);
                gl.glEnd();
                gl.glEnable(GL.GL_LIGHTING);
            }

            private void drawWindowFrame(GL2 gl, float x, float y, float z, float w, float h) {
                float depth = 0.1f;

                gl.glColor3f(0.2f, 0.25f, 0.35f);

                gl.glBegin(GL.GL_QUADS);
                gl.glVertex3f(x - w, y - h, z - depth);
                gl.glVertex3f(x + w, y - h, z - depth);
                gl.glVertex3f(x + w, y + h, z - depth);
                gl.glVertex3f(x - w, y + h, z - depth);
                gl.glEnd();

                gl.glColor3f(0.1f, 0.15f, 0.25f);
                gl.glBegin(GL.GL_QUADS);
                gl.glVertex3f(x - w, y - h, z + depth);
                gl.glVertex3f(x + w, y - h, z + depth);
                gl.glVertex3f(x + w, y + h, z + depth);
                gl.glVertex3f(x - w, y + h, z + depth);
                gl.glEnd();

                float border = 0.05f;
                gl.glColor3f(0.3f, 0.5f, 0.8f);
                gl.glBegin(GL.GL_QUADS);
                gl.glVertex3f(x - w - border, y - h - border, z);
                gl.glVertex3f(x + w + border, y - h - border, z);
                gl.glVertex3f(x + w + border, y - h, z);
                gl.glVertex3f(x - w - border, y - h, z);
                gl.glEnd();
                gl.glBegin(GL.GL_QUADS);
                gl.glVertex3f(x - w - border, y + h, z);
                gl.glVertex3f(x + w + border, y + h, z);
                gl.glVertex3f(x + w + border, y + h + border, z);
                gl.glVertex3f(x - w - border, y + h + border, z);
                gl.glEnd();
            }

            @Override
            public void reshape(GLAutoDrawable d, int x, int y, int w, int h) {
                GL2 gl = d.getGL().getGL2();
                gl.glViewport(0, 0, w, h);
            }
        });

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(16);
                    canvas.display();
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
        caps.setStencilBits(8);
        caps.setSampleBuffers(true);
        caps.setSamples(4);

        GLCanvas canvas = new GLCanvas(caps);
        canvas.setSize(800, 600);

        return canvas;
    }

    private static JPanel createControlPanel(GLCanvas canvas) {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(0x303040));

        JLabel label = new JLabel("LG3D Modern - 3D Window Manager Demo");
        label.setForeground(Color.WHITE);
        panel.add(label);

        panel.add(Box.createHorizontalStrut(20));

        JButton resetBtn = new JButton("Reset View");
        resetBtn.addActionListener(e -> canvas.repaint());
        panel.add(resetBtn);

        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn);

        return panel;
    }
}