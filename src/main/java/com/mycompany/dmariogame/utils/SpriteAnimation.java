package com.mycompany.dmariogame.utils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SpriteAnimation {
    private List<BufferedImage> frames;
    private int currentFrame;
    private int frameDelay;
    private int frameCount;
    private boolean isLooping;
    private boolean isPlaying;
    
    public SpriteAnimation(int frameDelay, boolean isLooping) {
        this.frames = new ArrayList<>();
        this.currentFrame = 0;
        this.frameDelay = frameDelay;
        this.frameCount = 0;
        this.isLooping = isLooping;
        this.isPlaying = true;
    }
    
    public void addFrame(BufferedImage frame) {
        if (frame != null) {
            frames.add(frame);
        }
    }
    
    public void update() {
        if (!isPlaying || frames.isEmpty()) return;
        
        frameCount++;
        if (frameCount >= frameDelay) {
            frameCount = 0;
            currentFrame++;
            
            if (currentFrame >= frames.size()) {
                if (isLooping) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.size() - 1;
                    isPlaying = false;
                }
            }
        }
    }
    
    public BufferedImage getCurrentFrame() {
        if (frames.isEmpty()) return null;
        return frames.get(currentFrame);
    }
    
    public void play() {
        isPlaying = true;
    }
    
    public void pause() {
        isPlaying = false;
    }
    
    public void reset() {
        currentFrame = 0;
        frameCount = 0;
        isPlaying = true;
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public int getFrameCount() {
        return frames.size();
    }
    
    public void setFrameDelay(int frameDelay) {
        this.frameDelay = frameDelay;
    }
} 