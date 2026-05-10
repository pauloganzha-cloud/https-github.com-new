package org.jdesktop.lg3d.utils;

import java.util.*;

/**
 * Utility classes for LG3D.
 */

/**
 * Vector math utilities.
 */
public class Vector3f {

    public float x, y, z;

    public Vector3f() {
        this(0, 0, 0);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vector3f other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize() {
        float len = length();
        if (len > 0) {
            x /= len;
            y /= len;
            z /= len;
        }
    }

    public float dot(Vector3f other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public void cross(Vector3f a, Vector3f b) {
        x = a.y * b.z - a.z * b.y;
        y = a.z * b.x - a.x * b.z;
        z = a.x * b.y - a.y * b.x;
    }

    public void add(Vector3f other) {
        x += other.x;
        y += other.y;
        z += other.z;
    }

    public void sub(Vector3f other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;
    }

    public void scale(float s) {
        x *= s;
        y *= s;
        z *= s;
    }

    public Vector3f clone() {
        return new Vector3f(x, y, z);
    }

    public static final Vector3f ZERO = new Vector3f(0, 0, 0);
    public static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
    public static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
    public static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);
}

/**
 * Color utilities.
 */
class Color4f {

    public float r, g, b, a;

    public Color4f() {
        this(0, 0, 0, 1);
    }

    public Color4f(float r, float g, float b) {
        this(r, g, b, 1);
    }

    public Color4f(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public static final Color4f BLACK = new Color4f(0, 0, 0, 1);
    public static final Color4f WHITE = new Color4f(1, 1, 1, 1);
    public static final Color4f RED = new Color4f(1, 0, 0, 1);
    public static final Color4f GREEN = new Color4f(0, 1, 0, 1);
    public static final Color4f BLUE = new Color4f(0, 0, 1, 1);
    public static final Color4f TRANSPARENT = new Color4f(0, 0, 0, 0);
}

/**
 * Animation utilities.
 */
class AnimationUtils {

    public interface Interpolator {
        float interpolate(float t);
    }

    public static final Interpolator LINEAR = t -> t;
    public static final Interpolator EASE_IN = t -> t * t;
    public static final Interpolator EASE_OUT = t -> t * (2 - t);
    public static final Interpolator EASE_IN_OUT = t -> t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}

/**
 * Timing utilities.
 */
class Timer {

    private long startTime;
    private long pauseTime;
    private boolean paused = false;

    public void start() {
        startTime = System.currentTimeMillis();
        paused = false;
    }

    public void pause() {
        if (!paused) {
            pauseTime = System.currentTimeMillis();
            paused = true;
        }
    }

    public void resume() {
        if (paused) {
            startTime += System.currentTimeMillis() - pauseTime;
            paused = false;
        }
    }

    public long elapsed() {
        long now = System.currentTimeMillis();
        if (paused) {
            return pauseTime - startTime;
        }
        return now - startTime;
    }

    public void reset() {
        start();
    }
}

/**
 * Resource loader utility.
 */
class ResourceUtils {

    private static final Map<String, byte[]> cache = new HashMap<>();

    public static byte[] loadResource(String name) {
        return cache.computeIfAbsent(name, key -> {
            try {
                var stream = ResourceUtils.class.getResourceAsStream(key);
                if (stream == null) return null;
                return stream.readAllBytes();
            } catch (Exception e) {
                return null;
            }
        });
    }

    public static String loadTextResource(String name) {
        byte[] data = loadResource(name);
        return data == null ? null : new String(data);
    }
}