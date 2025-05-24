package com.mycompany.dmariogame.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mycompany.dmariogame.App;
import com.mycompany.dmariogame.game.Block;
import com.mycompany.dmariogame.game.Coin;
import com.mycompany.dmariogame.game.Enemy;
import com.mycompany.dmariogame.game.Entity;
import com.mycompany.dmariogame.game.Flag;
import com.mycompany.dmariogame.game.Player;
import com.mycompany.dmariogame.utils.GameLoop;
import com.mycompany.dmariogame.utils.ResourceManager;

public class GamePanel extends JPanel implements ActionListener {
    private static final int TILE_SIZE = 32;
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int LEVEL_WIDTH = 3200; // 100 tiles wide
    private static final int LEVEL_NUMBER = 1; // For now, single level
    private static final int FLAG_HEIGHT = 96;
    private static final int RESPAWN_DELAY = 60; // frames to wait before respawn
    private static final int INVINCIBILITY_DURATION = 120; // frames of invincibility after respawn
    private static final int RESPAWN_X = TILE_SIZE * 2; // Fixed respawn position X
    private static final int RESPAWN_Y = SCREEN_HEIGHT - 100; // Fixed respawn position Y
    
    private BufferedImage background;
    private BufferedImage playerSprite;
    private BufferedImage blockSprite;
    private BufferedImage enemySprite;
    private BufferedImage coinSprite;
    private BufferedImage flagSprite;
    
    private BufferedImage buffer;
    private Graphics2D bufferGraphics;
    private Player player;
    private List<Entity> entities;
    private List<Block> blocks;
    private List<Enemy> enemies;
    private List<Coin> coins;
    private Flag flag;
    private GameLoop gameLoop;
    private boolean isGameRunning;
    private int cameraX = 0;
    private boolean hasWon = false;
    private int respawnTimer = 0;
    private int invincibilityTimer = 0;
    private boolean isRespawning = false;
    private boolean isGameOver = false;
    private int currentLevel = 1;
    private boolean showWinButtons = false;
    private Rectangle homeButton;
    private Rectangle nextLevelButton;
    private Rectangle exitButton;
    private boolean isGameComplete = false;
    private MainMenuPanel mainMenu; // Reference to main menu
    private JFrame frame; // Reference to main JFrame
    
    // Improve key handling by using a Set to track pressed keys
    private Set<Integer> pressedKeys = new HashSet<>();
    
    private Runnable restartCallback;
    
    public GamePanel(MainMenuPanel mainMenu, JFrame frame, Runnable restartCallback) {
        this.mainMenu = mainMenu;
        this.frame = frame;
        this.restartCallback = restartCallback;
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();
        
        // Initialize double buffering
        buffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        bufferGraphics = buffer.createGraphics();
        
        // Enable anti-aliasing
        bufferGraphics.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        
        // Initialize buttons
        homeButton = new Rectangle(SCREEN_WIDTH/2 - 100, SCREEN_HEIGHT/2 + 50, 200, 40);
        nextLevelButton = new Rectangle(SCREEN_WIDTH/2 - 100, SCREEN_HEIGHT/2 + 100, 200, 40);
        exitButton = new Rectangle(SCREEN_WIDTH/2 - 100, SCREEN_HEIGHT/2 + 150, 200, 40);
        
        // Load sprites
        ResourceManager.preloadSprites();
        background = ResourceManager.getSprite(ResourceManager.BACKGROUND);
        playerSprite = ResourceManager.getSprite(ResourceManager.PLAYER);
        blockSprite = ResourceManager.getSprite(ResourceManager.BLOCK_SOLID);
        enemySprite = ResourceManager.getSprite(ResourceManager.ENEMY);
        coinSprite = ResourceManager.getSprite(ResourceManager.COIN);
        flagSprite = ResourceManager.getSprite(ResourceManager.FLAG);
        
        // Initialize game objects
        player = new Player(SCREEN_WIDTH / 2, SCREEN_HEIGHT - 100, LEVEL_WIDTH);
        entities = new ArrayList<>();
        blocks = new ArrayList<>();
        enemies = new ArrayList<>();
        coins = new ArrayList<>();
        
        // Add initial blocks for testing
        initializeTestLevel();
        
        // Initialize game loop
        gameLoop = new GameLoop(this);
        
        // Add key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e);
            }
        });
        
        // Add mouse listener
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
            
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
        });
    }
    
    private void initializeTestLevel() {
        entities.clear();
        blocks.clear();
        coins.clear();
        enemies.clear();
        
        // Ground
        for (int x = 0; x < LEVEL_WIDTH; x += TILE_SIZE) {
            blocks.add(new Block(x, SCREEN_HEIGHT - TILE_SIZE, Block.BlockType.SOLID));
        }
        
        // Level-specific layout
        if (currentLevel == 1) {
            // Level 1 layout (easier)
            for (int i = 10; i < 90; i += 20) {
                blocks.add(new Block(i * TILE_SIZE, SCREEN_HEIGHT - 4 * TILE_SIZE, Block.BlockType.SOLID));
                blocks.add(new Block((i + 1) * TILE_SIZE, SCREEN_HEIGHT - 4 * TILE_SIZE, Block.BlockType.SOLID));
            }
            // Question and breakable blocks
            for (int i = 15; i < 90; i += 25) {
                blocks.add(new Block(i * TILE_SIZE, SCREEN_HEIGHT - 7 * TILE_SIZE, Block.BlockType.QUESTION));
                blocks.add(new Block((i + 1) * TILE_SIZE, SCREEN_HEIGHT - 7 * TILE_SIZE, Block.BlockType.BREAKABLE));
            }
            // Coins
            for (int i = 12; i < 90; i += 10) {
                coins.add(new Coin(i * TILE_SIZE, SCREEN_HEIGHT - 8 * TILE_SIZE));
            }
            // Enemies
            int groundY = SCREEN_HEIGHT - TILE_SIZE - 32;
            enemies.add(new Enemy(20 * TILE_SIZE, groundY, 20 * TILE_SIZE, 30 * TILE_SIZE));
            enemies.add(new Enemy(50 * TILE_SIZE, groundY, 50 * TILE_SIZE, 60 * TILE_SIZE));
        } else {
            // Level 2 layout (more challenging)
            // More platforms with varying heights
            for (int i = 10; i < 90; i += 15) {
                blocks.add(new Block(i * TILE_SIZE, SCREEN_HEIGHT - 4 * TILE_SIZE, Block.BlockType.SOLID));
                blocks.add(new Block((i + 1) * TILE_SIZE, SCREEN_HEIGHT - 4 * TILE_SIZE, Block.BlockType.SOLID));
                blocks.add(new Block((i + 2) * TILE_SIZE, SCREEN_HEIGHT - 4 * TILE_SIZE, Block.BlockType.SOLID));
                
                // Add some higher platforms
                if (i % 30 == 0) {
                    blocks.add(new Block(i * TILE_SIZE, SCREEN_HEIGHT - 8 * TILE_SIZE, Block.BlockType.SOLID));
                    blocks.add(new Block((i + 1) * TILE_SIZE, SCREEN_HEIGHT - 8 * TILE_SIZE, Block.BlockType.SOLID));
                }
            }
            
            // More question blocks with rewards
            for (int i = 15; i < 90; i += 20) {
                blocks.add(new Block(i * TILE_SIZE, SCREEN_HEIGHT - 7 * TILE_SIZE, Block.BlockType.QUESTION));
                blocks.add(new Block((i + 1) * TILE_SIZE, SCREEN_HEIGHT - 7 * TILE_SIZE, Block.BlockType.QUESTION));
                // Add coins above question blocks
                coins.add(new Coin(i * TILE_SIZE, SCREEN_HEIGHT - 10 * TILE_SIZE));
            }
            
            // More coins in challenging positions
            for (int i = 12; i < 90; i += 8) {
                coins.add(new Coin(i * TILE_SIZE, SCREEN_HEIGHT - 8 * TILE_SIZE));
                // Add some coins in mid-air
                if (i % 16 == 0) {
                    coins.add(new Coin(i * TILE_SIZE, SCREEN_HEIGHT - 12 * TILE_SIZE));
                }
            }
            
            // More enemies with wider patrol ranges
            int groundY = SCREEN_HEIGHT - TILE_SIZE - 32;
            enemies.add(new Enemy(20 * TILE_SIZE, groundY, 20 * TILE_SIZE, 35 * TILE_SIZE));
            enemies.add(new Enemy(40 * TILE_SIZE, groundY, 40 * TILE_SIZE, 55 * TILE_SIZE));
            enemies.add(new Enemy(60 * TILE_SIZE, groundY, 60 * TILE_SIZE, 75 * TILE_SIZE));
            enemies.add(new Enemy(80 * TILE_SIZE, groundY, 80 * TILE_SIZE, 95 * TILE_SIZE));
        }
        
        // Flag at far right
        flag = new Flag(LEVEL_WIDTH - 2 * TILE_SIZE, SCREEN_HEIGHT - TILE_SIZE - FLAG_HEIGHT);
        
        // Add all entities
        entities.addAll(blocks);
        entities.addAll(coins);
        entities.addAll(enemies);
        entities.add(flag);
        entities.add(player);
        hasWon = false;
        showWinButtons = false;
    }
    
    private void handleKeyPress(KeyEvent e) {
        int code = e.getKeyCode();

        // Allow restart even if game is not running
        if (isGameOver && code == KeyEvent.VK_R) {
            if (restartCallback != null) restartCallback.run();
            return;
        }

        if (!isGameRunning) return;

        pressedKeys.add(code);
        updatePlayerMovement();
    }
    
    private void handleKeyRelease(KeyEvent e) {
        if (!isGameRunning) return;
        int code = e.getKeyCode();
        pressedKeys.remove(code);
        updatePlayerMovement();
    }
    
    private void updatePlayerMovement() {
        if (!isGameRunning || isRespawning) return;
        
        boolean movingLeft = pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_A);
        boolean movingRight = pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D);
        boolean jumping = pressedKeys.contains(KeyEvent.VK_SPACE) || pressedKeys.contains(KeyEvent.VK_W);
        
        player.setMovingLeft(movingLeft);
        player.setMovingRight(movingRight);
        
        if (jumping && !player.isJumping()) {
            player.jump();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameRunning) {
            update();
            repaint();
        }
    }
    
    private void update() {
        // Handle respawn
        if (isRespawning) {
            respawnTimer--;
            if (respawnTimer <= 0) {
                respawnPlayer();
            }
        }
        
        // Handle invincibility
        if (invincibilityTimer > 0) {
            invincibilityTimer--;
        }
        
        // Update all entities
        for (Entity entity : entities) {
            if (entity.isActive()) {
                entity.update();
            }
        }
        // Player ground collision
        resolveVerticalCollision(player);
        // Enemy ground collision
        for (Enemy enemy : enemies) {
            resolveVerticalCollision(enemy);
        }
        // Check for collisions
        checkCollisions();
        // Remove inactive entities
        cleanupInactiveEntities();
    }
    
    private void checkCollisions() {
        // Player-Block collisions
        for (Block block : blocks) {
            if (!block.isActive()) continue;
            
            if (player.collidesWith(block)) {
                handlePlayerBlockCollision(player, block);
            }
        }
        
        // Player-Coin collisions
        for (Coin coin : coins) {
            if (!coin.isActive()) continue;
            
            if (player.collidesWith(coin)) {
                coin.collect();
                player.addScore(coin.getValue());
            }
        }
        
        // Player-Enemy collisions
        for (Enemy enemy : enemies) {
            if (!enemy.isActive()) continue;
            
            if (player.collidesWith(enemy)) {
                handlePlayerEnemyCollision(player, enemy);
            }
        }
        
        // Player-Flag collision
        if (flag.isActive() && !flag.isReached() && player.collidesWith(flag)) {
            flag.reach();
            hasWon = true;
            showWinButtons = true;
            App.getSoundManager().playSound("win.wav");
            if (currentLevel == 2) {
                isGameComplete = true;
            }
        }
        
        // Enemy-Block collisions
        for (Enemy enemy : enemies) {
            if (!enemy.isActive()) continue;
            
            for (Block block : blocks) {
                if (!block.isActive()) continue;
                
                if (enemy.collidesWith(block)) {
                    handleEnemyBlockCollision(enemy, block);
                }
            }
        }
    }
    
    private void handlePlayerBlockCollision(Player player, Block block) {
        if (block.isSolid()) {
            // Calculate overlap
            double overlapX = Math.min(
                player.getX() + player.getWidth() - block.getX(),
                block.getX() + block.getWidth() - player.getX()
            );
            double overlapY = Math.min(
                player.getY() + player.getHeight() - block.getY(),
                block.getY() + block.getHeight() - player.getY()
            );
            
            // Resolve collision
            if (overlapX < overlapY) {
                if (player.getX() < block.getX()) {
                    player.setX(block.getX() - player.getWidth());
                } else {
                    player.setX(block.getX() + block.getWidth());
                }
                player.setVelocityX(0);
            } else {
                if (player.getY() < block.getY()) {
                    player.setY(block.getY() - player.getHeight());
                    player.setVelocityY(0);
                    
                    // Check if block should be hit
                    if (block.isQuestion() || block.isBreakable() || block.isHidden()) {
                        block.hit();
                    }
                }
            }
        }
    }
    
    private void handlePlayerEnemyCollision(Player player, Enemy enemy) {
        if (enemy.isStomped() || isRespawning || invincibilityTimer > 0) return;
        
        // Check if player is above enemy
        if (player.getY() + player.getHeight() < enemy.getY() + enemy.getHeight() / 2) {
            enemy.stomp();
            player.setVelocityY(-5); // Small bounce
            // Remove enemy after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(500); // Wait 500ms
                    enemy.setActive(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            player.loseLife();
            App.getSoundManager().playSound("death.wav");
            if (player.getLives() > 0) {
                isRespawning = true;
                respawnTimer = RESPAWN_DELAY;
                player.setX(-100);
                player.setY(-100);
            } else {
                isGameOver = true;
                stopGame();
            }
        }
    }
    
    private void handleEnemyBlockCollision(Enemy enemy, Block block) {
        if (block.isSolid()) {
            enemy.setMovingRight(!enemy.isMovingRight());
        }
    }
    
    private void cleanupInactiveEntities() {
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (!entity.isActive()) {
                iterator.remove();
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Camera follows player
        cameraX = (int) player.getX() - SCREEN_WIDTH / 2 + TILE_SIZE;
        if (cameraX < 0) cameraX = 0;
        if (cameraX > LEVEL_WIDTH - SCREEN_WIDTH) cameraX = LEVEL_WIDTH - SCREEN_WIDTH;
        
        // Draw background
        if (background != null) {
            g2d.drawImage(background, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
        }
        
        // Draw visible entities
        for (Entity entity : entities) {
            if (entity.isActive()) {
                int ex = (int) entity.getX();
                if (ex + entity.getWidth() > cameraX && ex < cameraX + SCREEN_WIDTH) {
                    g2d.translate(-cameraX, 0);
                    entity.draw(g2d);
                    g2d.translate(cameraX, 0);
                }
            }
        }
        
        // Draw UI: lives, level, and coins
        g2d.setColor(Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        g2d.drawString("Lives: " + player.getLives(), 20, 30);
        g2d.drawString("Level: " + LEVEL_NUMBER, 20, 60);
        g2d.drawString("Coins: " + player.getScore(), 20, 90);
        
        // Draw invincibility effect
        if (invincibilityTimer > 0 && invincibilityTimer % 4 < 2) {
            g2d.setColor(new Color(255, 255, 255, 128));
            g2d.fillRect((int)player.getX() - cameraX, (int)player.getY(), player.getWidth(), player.getHeight());
        }
        
        // Win overlay with buttons
        if (hasWon) {
            g2d.setColor(new Color(0,0,0,180));
            g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
            
            if (currentLevel == 2) {
                g2d.drawString("GAME COMPLETE!", SCREEN_WIDTH/2 - 200, SCREEN_HEIGHT/2);
            } else {
                g2d.drawString("LEVEL " + currentLevel + " COMPLETE!", SCREEN_WIDTH/2 - 200, SCREEN_HEIGHT/2);
            }
            
            if (showWinButtons) {
                // Draw buttons with hover effect
                g2d.setColor(Color.WHITE);
                g2d.fill(homeButton);
                g2d.fill(nextLevelButton);
                
                // Draw button borders
                g2d.setColor(Color.BLACK);
                g2d.draw(homeButton);
                g2d.draw(nextLevelButton);
                
                // Draw button text
                g2d.setColor(Color.BLACK);
                g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
                g2d.drawString("Home", homeButton.x + 80, homeButton.y + 25);
                
                if (currentLevel < 2) {
                    g2d.drawString("Next Level", nextLevelButton.x + 60, nextLevelButton.y + 25);
                } else {
                    g2d.drawString("Exit to Menu", nextLevelButton.x + 60, nextLevelButton.y + 25);
                }
            }
        }
        
        // Game over overlay
        if (isGameOver) {
            g2d.setColor(new Color(0,0,0,180));
            g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            g2d.setColor(Color.RED);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
            g2d.drawString("GAME OVER", SCREEN_WIDTH/2 - 150, SCREEN_HEIGHT/2);
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
            g2d.drawString("Press R to Restart", SCREEN_WIDTH/2 - 100, SCREEN_HEIGHT/2 + 50);
        }
    }
    
    public void startGame() {
        isGameRunning = true;
        isGameOver = false;
        gameLoop.start();
        requestFocusInWindow();
    }
    
    public void stopGame() {
        isGameRunning = false;
        gameLoop.stop();
        ResourceManager.clearCache(); // Clean up resources when game stops
    }
    
    private void resolveVerticalCollision(Entity entity) {
        boolean landed = false;
        Rectangle entityBounds = entity.getBounds();
        for (Block block : blocks) {
            if (!block.isActive()) continue;
            Rectangle blockBounds = block.getBounds();
            // Only check if entity is falling and feet are above the block
            if (entity.getVelocityY() >= 0 &&
                entityBounds.intersects(blockBounds) &&
                entity.getY() + entity.getHeight() - entity.getVelocityY() <= block.getY() + 1) {
                // Land on top of block
                entity.setY(block.getY() - entity.getHeight());
                entity.setVelocityY(0);
                landed = true;
            }
        }
        if (entity instanceof Player) {
            ((Player)entity).setJumping(!landed);
        }
        // For enemy, if not landed, reverse direction if about to walk off
        if (entity instanceof Enemy) {
            Enemy enemy = (Enemy)entity;
            // Check if block under next step
            double nextX = enemy.getX() + (enemy.isMovingRight() ? enemy.getWidth() : -1);
            double footY = enemy.getY() + enemy.getHeight() + 1;
            boolean blockBelow = false;
            for (Block block : blocks) {
                if (!block.isActive()) continue;
                Rectangle blockBounds = block.getBounds();
                if (blockBounds.contains(nextX, footY)) {
                    blockBelow = true;
                    break;
                }
            }
            if (!blockBelow && landed) {
                enemy.setMovingRight(!enemy.isMovingRight());
            }
        }
    }
    
    private void respawnPlayer() {
        // Reset player state
        player.setX(RESPAWN_X);
        player.setY(RESPAWN_Y);
        player.setVelocityX(0);
        player.setVelocityY(0);
        player.setMovingLeft(false);
        player.setMovingRight(false);
        player.setJumping(false);
        
        // Reset respawn state
        isRespawning = false;
        invincibilityTimer = INVINCIBILITY_DURATION;
    }
    
    private void handleMouseClick(int x, int y) {
        if (!showWinButtons) return;
        
        if (homeButton.contains(x, y)) {
            // Return to main menu
            stopGame();
            frame.setContentPane(mainMenu);
            frame.revalidate();
            frame.repaint();
            System.out.println("Returning to main menu");
        } else if (nextLevelButton.contains(x, y)) {
            if (currentLevel < 2) {
                currentLevel++;
                initializeTestLevel();
                // Reset player state and position
                player.setX(RESPAWN_X);
                player.setY(RESPAWN_Y);
                player.setVelocityX(0);
                player.setVelocityY(0);
                player.setMovingLeft(false);
                player.setMovingRight(false);
                player.setJumping(false);
                hasWon = false;
                showWinButtons = false;
                startGame();
                System.out.println("Starting level " + currentLevel);
            } else {
                // Game complete, return to menu
                stopGame();
                frame.setContentPane(mainMenu);
                frame.revalidate();
                frame.repaint();
                System.out.println("Game complete, returning to menu");
            }
        }
    }
} 