package org.jdesktop.lg3d.utils.audio;

import java.util.*;
import java.io.*;

/**
 * Audio System - Sound playback and spatial audio.
 */
public class AudioSystem {

    private static AudioSystem instance;
    private boolean initialized = false;
    private float masterVolume = 1.0f;
    private boolean muted = false;

    private final Map<String, Sound> soundCache;
    private final List<AudioSource> activeSources;

    private AudioSystem() {
        soundCache = new HashMap<>();
        activeSources = new ArrayList<>();
    }

    public static AudioSystem getInstance() {
        if (instance == null) {
            instance = new AudioSystem();
        }
        return instance;
    }

    public boolean initialize() {
        if (initialized) return true;

        try {
            System.out.println("[AudioSystem] Initializing...");
            initialized = true;
            System.out.println("[AudioSystem] Initialized (Java-only mode)");
            return true;
        } catch (Exception e) {
            System.err.println("[AudioSystem] Failed to initialize: " + e.getMessage());
            return false;
        }
    }

    public void shutdown() {
        for (AudioSource source : activeSources) {
            source.stop();
        }
        activeSources.clear();
        soundCache.clear();
        initialized = false;
        System.out.println("[AudioSystem] Shutdown complete");
    }

    public Sound loadSound(String name, String filePath) {
        if (soundCache.containsKey(name)) {
            return soundCache.get(name);
        }

        Sound sound = new Sound(name);
        sound.setFilePath(filePath);
        soundCache.put(name, sound);
        return sound;
    }

    public Sound loadSound(String name) {
        return loadSound(name, name);
    }

    public AudioSource playSound(Sound sound) {
        return playSound(sound, 1.0f, false);
    }

    public AudioSource playSound(Sound sound, float volume, boolean loop) {
        if (!initialized || muted) return null;

        AudioSource source = new AudioSource(sound);
        source.setVolume(volume * masterVolume);
        source.setLooping(loop);
        source.play();

        activeSources.add(source);
        return source;
    }

    public AudioSource playSound(String soundName) {
        Sound sound = soundCache.get(soundName);
        if (sound != null) {
            return playSound(sound);
        }
        return null;
    }

    public void stopAllSounds() {
        for (AudioSource source : activeSources) {
            source.stop();
        }
        activeSources.clear();
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
        for (AudioSource source : activeSources) {
            source.updateVolume();
        }
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) {
            for (AudioSource source : activeSources) {
                source.pause();
            }
        } else {
            for (AudioSource source : activeSources) {
                source.resume();
            }
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void update() {
        for (int i = activeSources.size() - 1; i >= 0; i--) {
            AudioSource source = activeSources.get(i);
            if (!source.isPlaying()) {
                activeSources.remove(i);
            }
        }
    }
}

/**
 * Sound - audio data container.
 */
class Sound {

    private String name;
    private String filePath;
    private float duration;
    private int sampleRate;
    private int channels;
    private boolean loaded = false;

    public Sound(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}

/**
 * AudioSource - playing sound instance.
 */
class AudioSource {

    private final Sound sound;
    private float volume = 1.0f;
    private float pan = 0;
    private float pitch = 1.0f;
    private boolean looping = false;
    private boolean playing = false;
    private boolean paused = false;

    private float positionX, positionY, positionZ;
    private boolean spatial = false;
    private float maxDistance = 10f;

    private long startTime;
    private long pauseTime;

    public AudioSource(Sound sound) {
        this.sound = sound;
    }

    public Sound getSound() {
        return sound;
    }

    public void play() {
        playing = true;
        startTime = System.currentTimeMillis();
        System.out.println("[AudioSource] Playing: " + sound.getName());
    }

    public void stop() {
        playing = false;
        paused = false;
    }

    public void pause() {
        if (playing && !paused) {
            paused = true;
            pauseTime = System.currentTimeMillis();
        }
    }

    public void resume() {
        if (paused) {
            paused = false;
            long pauseDuration = System.currentTimeMillis() - pauseTime;
            startTime += pauseDuration;
        }
    }

    public boolean isPlaying() {
        if (!playing || paused) return false;

        long elapsed = System.currentTimeMillis() - startTime;
        float soundDuration = sound.getDuration() * 1000;

        if (!looping && elapsed >= soundDuration) {
            playing = false;
            return false;
        }

        return true;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(1, volume));
        updateVolume();
    }

    public float getVolume() {
        return volume;
    }

    public void setPan(float pan) {
        this.pan = Math.max(-1, Math.min(1, pan));
    }

    public float getPan() {
        return pan;
    }

    public void setPitch(float pitch) {
        this.pitch = Math.max(0.5f, Math.min(2.0f, pitch));
    }

    public float getPitch() {
        return pitch;
    }

    public void setLooping(boolean loop) {
        this.looping = loop;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setPosition(float x, float y, float z) {
        this.positionX = x;
        this.positionY = y;
        this.positionZ = z;
        this.spatial = true;
    }

    public void setMaxDistance(float distance) {
        this.maxDistance = distance;
    }

    public void updateVolume() {
    }

    public float getProgress() {
        if (!playing || sound.getDuration() == 0) return 0;
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.min(1, elapsed / (sound.getDuration() * 1000));
    }
}

/**
 * AudioListener - listener position for spatial audio.
 */
class AudioListener {

    private float positionX, positionY, positionZ;
    private float forwardX, forwardY, forwardZ;
    private float upX, upY, upZ;

    public AudioListener() {
        setPosition(0, 0, 0);
        setOrientation(0, 0, -1, 0, 1, 0);
    }

    public void setPosition(float x, float y, float z) {
        this.positionX = x;
        this.positionY = y;
        this.positionZ = z;
    }

    public void setOrientation(float forwardX, float forwardY, float forwardZ,
                               float upX, float upY, float upZ) {
        this.forwardX = forwardX;
        this.forwardY = forwardY;
        this.forwardZ = forwardZ;
        this.upX = upX;
        this.upY = upY;
        this.upZ = upZ;
    }

    public float[] getPosition() {
        return new float[]{positionX, positionY, positionZ};
    }
}

/**
 * Sound effects presets.
 */
class SoundEffects {

    public static final String CLICK = "click";
    public static final String HOVER = "hover";
    public static final String OPEN = "open";
    public static final String CLOSE = "close";
    public static final String ERROR = "error";
    public static final String SUCCESS = "success";
    public static final String NOTIFICATION = "notification";

    public static void preloadAll(AudioSystem audio) {
        audio.loadSound(CLICK);
        audio.loadSound(HOVER);
        audio.loadSound(OPEN);
        audio.loadSound(CLOSE);
        audio.loadSound(ERROR);
        audio.loadSound(SUCCESS);
        audio.loadSound(NOTIFICATION);
    }

    public static void playClick(AudioSystem audio) {
        audio.playSound(CLICK);
    }

    public static void playHover(AudioSystem audio) {
        audio.playSound(HOVER);
    }
}