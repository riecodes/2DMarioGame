package com.mycompany.dmariogame.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class ResourceManager {
    private static final Logger LOGGER = Logger.getLogger(ResourceManager.class.getName());
    private static final Map<String, BufferedImage> spriteCache = new HashMap<>();
    private static final String SPRITE_BASE_PATH = "/com/mycompany/dmariogame/assets/";
    
    // Sprite keys for easy access
    public static final String PLAYER = "mario";
    public static final String BLOCK_SOLID = "block";
    public static final String BLOCK_BREAKABLE = "block";
    public static final String BLOCK_QUESTION = "lucky";
    public static final String ENEMY = "enemy";
    public static final String ENEMY_STOMPED = "enemy";
    public static final String COIN = "coin";
    public static final String FLAG = "flag";
    public static final String BACKGROUND = "background";
    
    public static BufferedImage loadSprite(String path) {
        if (spriteCache.containsKey(path)) {
            return spriteCache.get(path);
        }
        String fullPath = SPRITE_BASE_PATH + path + ".png";
        try {
            InputStream is = ResourceManager.class.getResourceAsStream(fullPath);
            if (is == null) {
                LOGGER.log(Level.WARNING, "Failed to load sprite: {0} - Resource not found", fullPath);
                return null;
            }
            BufferedImage sprite = ImageIO.read(is);
            if (sprite == null) {
                LOGGER.log(Level.WARNING, "Failed to load sprite: {0} - Invalid image format", fullPath);
                return null;
            }
            spriteCache.put(path, sprite);
            LOGGER.log(Level.FINE, "Successfully loaded sprite: {0}", path);
            return sprite;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading sprite: " + fullPath, e);
            return null;
        }
    }
    
    public static void preloadSprites() {
        LOGGER.info("Preloading sprites...");
        loadSprite(PLAYER);
        loadSprite(BLOCK_SOLID);
        loadSprite(BLOCK_QUESTION);
        loadSprite(ENEMY);
        loadSprite(COIN);
        loadSprite(FLAG);
        loadSprite(BACKGROUND);
        LOGGER.info("Sprite preloading completed");
    }
    
    public static void clearCache() {
        LOGGER.info("Clearing sprite cache");
        spriteCache.clear();
    }
    
    public static boolean isSpriteLoaded(String path) {
        return spriteCache.containsKey(path);
    }
    
    public static int getCacheSize() {
        return spriteCache.size();
    }
    
    public static BufferedImage getSprite(String key) {
        return spriteCache.get(key);
    }
} 