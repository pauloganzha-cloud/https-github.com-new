package org.jdesktop.lg3d.animation;

import java.util.*;
import java.util.concurrent.*;

/**
 * Animation system for LG3D.
 */
public class AnimationManager {

    private static AnimationManager instance;
    private final ConcurrentHashMap<String, Animation> animations;
    private final ScheduledExecutorService scheduler;
    private boolean enabled = true;

    private AnimationManager() {
        animations = new ConcurrentHashMap<>();
        scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "LG3D-Animation");
            t.setDaemon(true);
            return t;
        });
    }

    public static AnimationManager getInstance() {
        if (instance == null) {
            instance = new AnimationManager();
        }
        return instance;
    }

    public void play(Animation anim) {
        if (!enabled) return;

        animations.put(anim.getName(), anim);
        anim.start();
    }

    public void stop(String name) {
        Animation anim = animations.get(name);
        if (anim != null) {
            anim.stop();
            animations.remove(name);
        }
    }

    public void stopAll() {
        for (Animation anim : animations.values()) {
            anim.stop();
        }
        animations.clear();
    }

    public Animation getAnimation(String name) {
        return animations.get(name);
    }

    public Collection<Animation> getAnimations() {
        return animations.values();
    }

    public boolean hasAnimation(String name) {
        return animations.containsKey(name);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void dispose() {
        stopAll();
        scheduler.shutdown();
    }
}

/**
 * Base animation class.
 */
abstract class Animation {

    private final String name;
    private final long duration;
    private final Interpolator interpolator;
    private State state = State.STOPPED;
    private long startTime;
    private float progress = 0;

    public enum State {
        STOPPED, PLAYING, PAUSED
    }

    public Animation(String name, long durationMs) {
        this(name, durationMs, Interpolator.LINEAR);
    }

    public Animation(String name, long durationMs, Interpolator interpolator) {
        this.name = name;
        this.duration = durationMs;
        this.interpolator = interpolator;
    }

    public String getName() {
        return name;
    }

    public long getDuration() {
        return duration;
    }

    public float getProgress() {
        return progress;
    }

    public State getState() {
        return state;
    }

    public void start() {
        state = State.PLAYING;
        startTime = System.currentTimeMillis();
        onStarted();
    }

    public void stop() {
        state = State.STOPPED;
        progress = 0;
        onStopped();
    }

    public void pause() {
        state = State.PAUSED;
        onPaused();
    }

    public void resume() {
        state = State.PLAYING;
        onResumed();
    }

    public boolean isRunning() {
        return state == State.PLAYING;
    }

    public void update() {
        if (state != State.PLAYING) return;

        long elapsed = System.currentTimeMillis() - startTime;
        progress = Math.min(1.0f, (float) elapsed / duration);

        float t = interpolator.interpolate(progress);
        onUpdate(t);

        if (progress >= 1.0f) {
            onFinished();
            stop();
        }
    }

    protected void onStarted() {
    }

    protected void onStopped() {
    }

    protected void onPaused() {
    }

    protected void onResumed() {
    }

    protected abstract void onUpdate(float t);

    protected void onFinished() {
    }
}

/**
 * Interpolator for animation curves.
 */
interface Interpolator {
    float interpolate(float t);

    Interpolator LINEAR = t -> t;
    Interpolator EASE_IN = t -> t * t;
    Interpolator EASE_OUT = t -> 1 - (1 - t) * (1 - t);
    Interpolator EASE_IN_OUT = t -> t < 0.5 ? 2 * t * t : 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
    Interpolator BOUNCE = t -> {
        if (t < 0.5f) {
            return 8 * t * t * t * t;
        } else {
            float f = t - 1;
            return 1 - 8 * f * f * f * f;
        }
    };
    Interpolator ELASTIC = t -> (float) (t == 0 ? 0 : t == 1 ? 1 :
        Math.pow(2, -10 * t) * Math.sin((t - 0.1) * 5 * Math.PI) + 1);
}

/**
 * Animation for component position.
 */
class PositionAnimation extends Animation {

    private final Component3D target;
    private final float startX, startY, startZ;
    private final float endX, endY, endZ;

    public PositionAnimation(Component3D target, float endX, float endY, float endZ, long duration) {
        super("PositionAnim-" + target.getName(), duration, Interpolator.EASE_OUT);
        this.target = target;
        this.startX = 0;
        this.startY = 0;
        this.startZ = 0;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public PositionAnimation(Component3D target, float startX, float startY, float startZ,
                             float endX, float endY, float endZ, long duration) {
        super("PositionAnim-" + target.getName(), duration, Interpolator.EASE_OUT);
        this.target = target;
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    @Override
    protected void onUpdate(float t) {
        float x = startX + (endX - startX) * t;
        float y = startY + (endY - startY) * t;
        float z = startZ + (endZ - startZ) * t;
        target.setTranslation(x, y, z);
    }
}

/**
 * Animation for component scale.
 */
class ScaleAnimation extends Animation {

    private final Component3D target;
    private final float startScale, endScale;

    public ScaleAnimation(Component3D target, float endScale, long duration) {
        super("ScaleAnim-" + target.getName(), duration, Interpolator.EASE_OUT);
        this.target = target;
        this.startScale = 1.0f;
        this.endScale = endScale;
    }

    @Override
    protected void onUpdate(float t) {
        float scale = startScale + (endScale - startScale) * t;
        target.setScale(scale, scale, scale);
    }
}

/**
 * Animation for component rotation.
 */
class RotationAnimation extends Animation {

    private final Component3D target;
    private final float startAngle, endAngle;
    private final float axisX, axisY, axisZ;

    public RotationAnimation(Component3D target, float startAngle, float endAngle,
                             float axisX, float axisY, float axisZ, long duration) {
        super("RotationAnim-" + target.getName(), duration, Interpolator.LINEAR);
        this.target = target;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.axisX = axisX;
        this.axisY = axisY;
        this.axisZ = axisZ;
    }

    @Override
    protected void onUpdate(float t) {
        float angle = startAngle + (endAngle - startAngle) * t;
        target.setRotation(angle, axisX, axisY, axisZ);
    }
}

/**
 * Composite animation - plays multiple animations together.
 */
class CompositeAnimation extends Animation {

    private final List<Animation> children;
    private boolean playTogether = true;

    public CompositeAnimation(String name, List<Animation> animations, boolean together) {
        super(name, 0, Interpolator.LINEAR);
        this.children = animations;
        this.playTogether = together;

        long maxDuration = 0;
        for (Animation anim : animations) {
            maxDuration = Math.max(maxDuration, anim.getDuration());
        }
    }

    @Override
    public void start() {
        super.start();
        for (Animation anim : children) {
            AnimationManager.getInstance().play(anim);
        }
    }

    @Override
    public void stop() {
        super.stop();
        for (Animation anim : children) {
            AnimationManager.getInstance().stop(anim.getName());
        }
    }

    @Override
    protected void onUpdate(float t) {
    }
}