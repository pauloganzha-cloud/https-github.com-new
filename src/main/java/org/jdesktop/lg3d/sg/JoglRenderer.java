package org.jdesktop.lg3d.sg;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Interface for JOGL-based rendering.
 * Modern replacement for Java 3D's rendering pipeline.
 */
public interface JoglRenderer {

    void beginRendering();

    void endRendering();

    void setBackground(Color3f color);

    void setCamera(Transform3D transform, float fov, float aspect, float near, float far);

    void setLight(Light light);

    void drawShape(Shape3D shape);

    void drawText(String text, float x, float y, float z);

    void pushMatrix();

    void popMatrix();

    void multMatrix(Transform3D transform);

    void setMaterial(Material material);

    void setTransparency(float transparency);

    void setTexture(Texture texture);

    void enable(int capability);

    void disable(int capability);

    static final int LIGHTING = 1;
    static final int TEXTURE = 2;
    static final int BLEND = 3;
    static final int DEPTH_TEST = 4;
    static final int CULL_FACE = 5;
}

/**
 * Color representation (RGB float).
 */
class Color3f {
    public float r, g, b;

    public Color3f() {
        this(0, 0, 0);
    }

    public Color3f(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void set(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static final Color3f BLACK = new Color3f(0, 0, 0);
    public static final Color3f WHITE = new Color3f(1, 1, 1);
    public static final Color3f RED = new Color3f(1, 0, 0);
    public static final Color3f GREEN = new Color3f(0, 1, 0);
    public static final Color3f BLUE = new Color3f(0, 0, 1);
}

/**
 * Light source for the scene.
 */
class Light {
    public enum Type {
        AMBIENT, DIRECTIONAL, POINT, SPOT
    }

    public Type type;
    public Color3f color = Color3f.WHITE;
    public float[] position = new float[]{0, 0, 1, 0};
    public float[] direction = new float[]{0, 0 -1, 0};
    public float intensity = 1.0f;
    public boolean enabled = true;
    public int index = 0;
}

/**
 * Material properties for rendering.
 */
class Material {
    public Color3f ambient = Color3f.BLACK;
    public Color3f diffuse = Color3f.WHITE;
    public Color3f specular = Color3f.WHITE;
    public Color3f emissive = Color3f.BLACK;
    public float shininess = 0;
    public float transparency = 0;
}

/**
 * Texture for object surfaces.
 */
class Texture {
    public String filename;
    public IntBuffer imageData;
    public int width, height;
    public int format;
    public boolean mipmap = true;
    public int minFilter = GL.GL_LINEAR_MIPMAP_LINEAR;
    public int magFilter = GL.GL_LINEAR;
    public int wrapS = GL.GL_REPEAT;
    public int wrapT = GL.GL_REPEAT;
}

public class GL {
    public static final int GL_LIGHTING = 0x0B50;
    public static final int GL_TEXTURE_2D = 0x0CDE;
    public static final int GL_BLEND = 0x0BE2;
    public static final int GL_DEPTH_TEST = 0x0B71;
    public static final int GL_CULL_FACE = 0x0B44;

    public static final int GL_LINEAR = 0x2600;
    public static final int GL_LINEAR_MIPMAP_LINEAR = 0x2703;
    public static final int GL_REPEAT = 0x2901;

    public static final int GL_AMBIENT = 0x1200;
    public static final int GL_DIFFUSE = 0x1201;
    public static final int GL_SPECULAR = 0x1202;
    public static final int GL_EMISSION = 0x1600;
    public static final int GL_SHININESS = 0x1601;
}