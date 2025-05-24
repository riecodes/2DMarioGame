package com.mycompany.dmariogame.audio;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> soundClips;
    private Clip backgroundMusic;
    private boolean isMuted = false;

    private SoundManager() {
        soundClips = new HashMap<>();
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSounds() {
        try {
            // Load background music
            backgroundMusic = loadSound("bg-music.wav");
            if (backgroundMusic != null) {
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }

            // Load sound effects
            loadSoundEffect("collect.wav");
            loadSoundEffect("death.wav");
            loadSoundEffect("jump.wav");
            loadSoundEffect("win.wav");
        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }

    private Clip loadSound(String filename) {
        try {
            URL url = getClass().getResource("/com/mycompany/dmariogame/assets/" + filename);
            if (url == null) {
                System.err.println("Could not find sound file: " + filename);
                return null;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound " + filename + ": " + e.getMessage());
            return null;
        }
    }

    private void loadSoundEffect(String filename) {
        Clip clip = loadSound(filename);
        if (clip != null) {
            soundClips.put(filename, clip);
        }
    }

    public void playSound(String soundName) {
        if (isMuted) return;
        
        Clip clip = soundClips.get(soundName);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void startBackgroundMusic() {
        if (backgroundMusic != null && !isMuted) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    public void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopBackgroundMusic();
        } else {
            startBackgroundMusic();
        }
    }

    public boolean isMuted() {
        return isMuted;
    }
} 