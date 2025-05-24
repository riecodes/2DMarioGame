package com.mycompany.dmariogame.game;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.mycompany.dmariogame.utils.ResourceManager;
import com.mycompany.dmariogame.utils.SpriteAnimation;

public class Block extends Entity {
    private static final int BLOCK_SIZE = 32;
    private BlockType type;
    private boolean isHit;
    private SpriteAnimation questionAnimation;
    
    public enum BlockType {
        SOLID,      // Regular solid block
        BREAKABLE,  // Can be broken by hitting from below
        QUESTION,   // Question mark block that gives items
        HIDDEN      // Hidden block that appears when hit from below
    }
    
    public Block(float x, float y, BlockType type) {
        super(x, y, BLOCK_SIZE, BLOCK_SIZE);
        this.type = type;
        this.isHit = false;
    }
    
    @Override
    public void update() {
        // Blocks don't move, but we might want to add animation for question blocks
        if (type == BlockType.QUESTION && questionAnimation != null) {
            questionAnimation.update();
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        switch (type) {
            case SOLID:
                g.drawImage(ResourceManager.getSprite(ResourceManager.BLOCK_SOLID), 
                           (int)x, (int)y, width, height, null);
                break;
            case BREAKABLE:
                g.drawImage(ResourceManager.getSprite(ResourceManager.BLOCK_BREAKABLE), 
                           (int)x, (int)y, width, height, null);
                break;
            case QUESTION:
                if (questionAnimation != null && questionAnimation.isPlaying()) {
                    g.drawImage(questionAnimation.getCurrentFrame(), 
                               (int)x, (int)y, width, height, null);
                } else {
                    g.drawImage(ResourceManager.getSprite(ResourceManager.BLOCK_QUESTION), 
                               (int)x, (int)y, width, height, null);
                }
                break;
            case HIDDEN:
                // Hidden blocks are invisible until hit
                if (isHit) {
                    g.drawImage(ResourceManager.getSprite(ResourceManager.BLOCK_SOLID), 
                               (int)x, (int)y, width, height, null);
                }
                break;
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public boolean isSolid() {
        return type == BlockType.SOLID || type == BlockType.BREAKABLE;
    }
    
    public boolean isBreakable() {
        return type == BlockType.BREAKABLE;
    }
    
    public boolean isQuestion() {
        return type == BlockType.QUESTION;
    }
    
    public boolean isHidden() {
        return type == BlockType.HIDDEN;
    }
    
    public void hit() {
        if (type == BlockType.QUESTION) {
            // TODO: Spawn item or coin
            type = BlockType.SOLID;
        } else if (type == BlockType.BREAKABLE) {
            setActive(false);
        } else if (type == BlockType.HIDDEN) {
            isHit = true;
            type = BlockType.SOLID;
        }
    }
    
    public BlockType getType() {
        return type;
    }
} 