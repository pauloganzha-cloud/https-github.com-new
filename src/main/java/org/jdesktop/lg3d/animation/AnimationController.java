package org.jdesktop.lg3d.animation;

import java.util.*;
import java.util.concurrent.*;

/**
 * AnimationController - manages all animations in the scene.
 */
public class AnimationController {

    private static AnimationController instance;

    private final ConcurrentHashMap<String, Animation> animations;
    private final ConcurrentHashMap<String, Timeline> timelines;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService workerPool;

    private boolean enabled = true;
    private float timeScale = 1.0f;

    private AnimationController() {
        animations = new ConcurrentHashMap<>();
        timelines = new ConcurrentHashMap<>();

        scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "Animation-Scheduler");
            t.setDaemon(true);
            return t;
        });

        workerPool = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r, "Animation-Worker");
            t.setDaemon(true);
            return t;
        });
    }

    public static AnimationController getInstance() {
        if (instance == null) {
            instance = new AnimationController();
        }
        return instance;
    }

    public void start() {
        scheduler.submit(this::updateLoop);
    }

    public void stop() {
        scheduler.shutdown();
        workerPool.shutdown();
        animations.clear();
        timelines.clear();
    }

    private void updateLoop() {
        while (!scheduler.isShutdown()) {
            try {
                long startTime = System.nanoTime();

                for (Animation anim : animations.values()) {
                    if (anim.isRunning()) {
                        anim.update();
                    }
                }

                for (Timeline timeline : timelines.values()) {
                    timeline.update();
                }

                long elapsed = System.nanoTime() - startTime;
                long frameTime = (long) (16_666_666 * timeScale);
                long sleepTime = frameTime - elapsed;

                if (sleepTime > 0) {
                    Thread.sleep(sleepTime / 1_000_000);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void playAnimation(Animation anim) {
        if (!enabled) return;
        animations.put(anim.getName(), anim);
        anim.start();
    }

    public void playAnimation(String name) {
        Animation anim = animations.get(name);
        if (anim != null) {
            anim.start();
        }
    }

    public void stopAnimation(String name) {
        Animation anim = animations.remove(name);
        if (anim != null) {
            anim.stop();
        }
    }

    public void pauseAnimation(String name) {
        Animation anim = animations.get(name);
        if (anim != null) {
            anim.pause();
        }
    }

    public void resumeAnimation(String name) {
        Animation anim = animations.get(name);
        if (anim != null) {
            anim.resume();
        }
    }

    public Animation getAnimation(String name) {
        return animations.get(name);
    }

    public boolean hasAnimation(String name) {
        return animations.containsKey(name);
    }

    public void stopAll() {
        for (Animation anim : animations.values()) {
            anim.stop();
        }
        animations.clear();
    }

    public void createTimeline(String name, Timeline timeline) {
        timelines.put(name, timeline);
    }

    public Timeline getTimeline(String name) {
        return timelines.get(name);
    }

    public void playTimeline(String name) {
        Timeline timeline = timelines.get(name);
        if (timeline != null) {
            timeline.play();
        }
    }

    public void setTimeScale(float scale) {
        this.timeScale = Math.max(0.1f, Math.min(5.0f, scale));
    }

    public float getTimeScale() {
        return timeScale;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Collection<Animation> getActiveAnimations() {
        return animations.values();
    }

    public int getActiveAnimationCount() {
        return animations.size();
    }
}

/**
 * Timeline - sequence of animations.
 */
class Timeline {

    private final String name;
    private final List<TimelineAction> actions;
    private int currentAction = 0;
    private boolean playing = false;
    private boolean looping = false;
    private long elapsed = 0;

    public Timeline(String name) {
        this.name = name;
        this.actions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Timeline addAction(float time, Animation anim) {
        actions.add(new TimelineAction(time, anim));
        return this;
    }

    public Timeline addAction(float time, Runnable action) {
        actions.add(new TimelineAction(time, action));
        return this;
    }

    public Timeline setLooping(boolean loop) {
        this.looping = loop;
        return this;
    }

    public void play() {
        playing = true;
        currentAction = 0;
        elapsed = 0;

        if (!actions.isEmpty()) {
            actions.get(0).play();
        }
    }

    public void stop() {
        playing = false;
        currentAction = 0;
        elapsed = 0;

        for (TimelineAction action : actions) {
            action.stop();
        }
    }

    public void pause() {
        playing = false;
    }

    public void resume() {
        if (!actions.isEmpty()) {
            playing = true;
        }
    }

    public void update() {
        if (!playing || actions.isEmpty()) return;

        elapsed += 16;

        TimelineAction current = actions.get(currentAction);
        if (current.isFinished()) {
            currentAction++;
            if (currentAction >= actions.size()) {
                if (looping) {
                    stop();
                    play();
                } else {
                    playing = false;
                }
            } else {
                actions.get(currentAction).play();
            }
        }
    }

    public boolean isPlaying() {
        return playing;
    }
}

class TimelineAction {

    private final float time;
    private final Animation animation;
    private final Runnable runnable;
    private boolean started = false;
    private boolean finished = false;

    public TimelineAction(float time, Animation anim) {
        this.time = time;
        this.animation = anim;
        this.runnable = null;
    }

    public TimelineAction(float time, Runnable run) {
        this.time = time;
        this.animation = null;
        this.runnable = run;
    }

    public float getTime() {
        return time;
    }

    public void play() {
        if (!started) {
            started = true;
            if (animation != null) {
                animation.start();
            } else if (runnable != null) {
                runnable.run();
                finished = true;
            }
        }
    }

    public void stop() {
        started = false;
        finished = false;
        if (animation != null) {
            animation.stop();
        }
    }

    public boolean isFinished() {
        if (finished) return true;
        if (animation != null) {
            return !animation.isRunning();
        }
        return false;
    }
}

/**
 * AnimationBuilder - fluent API for creating animations.
 */
class AnimationBuilder {

    private Component3D target;
    private String name;
    private long duration = 1000;
    private Interpolator interpolator = Interpolator.LINEAR;
    private float startX, startY, startZ;
    private float endX, endY, endZ;
    private float startScale = 1, endScale = 1;
    private float startRotation, endRotation;
    private float axisX, axisY, axisZ = 1;
    private Runnable onComplete;

    public AnimationBuilder(Component3D target) {
        this.target = target;
    }

    public AnimationBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AnimationBuilder duration(long ms) {
        this.duration = ms;
        return this;
    }

    public AnimationBuilder easeIn() {
        this.interpolator = Interpolator.EASE_IN;
        return this;
    }

    public AnimationBuilder easeOut() {
        this.interpolator = Interpolator.EASE_OUT;
        return this;
    }

    public AnimationBuilder easeInOut() {
        this.interpolator = Interpolator.EASE_IN_OUT;
        return this;
    }

    public AnimationBuilder bounce() {
        this.interpolator = Interpolator.BOUNCE;
        return this;
    }

    public AnimationBuilder elastic() {
        this.interpolator = Interpolator.ELASTIC;
        return this;
    }

    public AnimationBuilder to(float x, float y, float z) {
        this.endX = x;
        this.endY = y;
        this.endZ = z;
        return this;
    }

    public AnimationBuilder scale(float to) {
        this.endScale = to;
        return this;
    }

    public AnimationBuilder rotate(float angle, float x, float y, float z) {
        this.endRotation = angle;
        this.axisX = x;
        this.axisY = y;
        this.axisZ = z;
        return this;
    }

    public AnimationBuilder onComplete(Runnable callback) {
        this.onComplete = callback;
        return this;
    }

    public Animation build() {
        String animName = name != null ? name : "anim-" + target.getName();

        if (endX != 0 || endY != 0 || endZ != 0) {
            PositionAnimation anim = new PositionAnimation(target, endX, endY, endZ, duration);
            anim = new PositionAnimation(target, 0, 0, 0, endX, endY, endZ, duration);
            return anim;
        } else if (endScale != 1) {
            ScaleAnimation anim = new ScaleAnimation(target, endScale, duration);
            return anim;
        } else if (endRotation != 0) {
            RotationAnimation anim = new RotationAnimation(target, 0, endRotation, axisX, axisY, axisZ, duration);
            return anim;
        }

        return new Animation(animName, duration, interpolator) {
            @Override
            protected void onUpdate(float t) {
            }
        };
    }
}

/**
 * Animation presets for common effects.
 */
class AnimationPresets {

    public static Animation fadeIn(Component3D target, long duration) {
        return new Animation("fadeIn-" + target.getName(), duration, Interpolator.LINEAR) {
            @Override
            protected void onUpdate(float t) {
            }
        };
    }

    public static Animation fadeOut(Component3D target, long duration) {
        return new Animation("fadeOut-" + target.getName(), duration, Interpolator.LINEAR) {
            @Override
            protected void onUpdate(float t) {
            }
        };
    }

    public static Animation slideIn(Component3D target, String direction, long duration) {
        return new PositionAnimation(target, 0, 0, 0, 0, 0, 5, duration);
    }

    public static Animation slideOut(Component3D target, String direction, long duration) {
        float z = "left".equals(direction) ? -10 : ("right".equals(direction) ? 10 : 0);
        return new PositionAnimation(target, 0, 0, 0, 0, 0, z, duration);
    }

    public static Animation pulse(Component3D target, long duration) {
        return new Animation("pulse-" + target.getName(), duration, Interpolator.EASE_IN_OUT) {
            @Override
            protected void onUpdate(float t) {
                float scale = 1 + (float) Math.sin(t * Math.PI * 2) * 0.1f;
                target.setScale(scale, scale, scale);
            }
        };
    }

    public static Animation shake(Component3D target, long duration) {
        return new Animation("shake-" + target.getName(), duration, Interpolator.LINEAR) {
            @Override
            protected void onUpdate(float t) {
                float offset = (float) Math.sin(t * 20) * 0.1f * (1 - t);
            }
        };
    }

    public static Animation spin(Component3D target, long duration) {
        return new RotationAnimation(target, 0, 360, 0, 1, 0, duration);
    }
}