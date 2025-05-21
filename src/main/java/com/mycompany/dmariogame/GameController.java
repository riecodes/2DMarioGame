package com.mycompany.dmariogame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class GameController {
    @FXML
    private Pane gamePane;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label livesLabel;

    private Player player;
    private List<Block> blocks = new ArrayList<>();
    private List<Coin> coins = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private Flag flag;

    private AnimationTimer gameLoop;
    private boolean leftPressed = false, rightPressed = false, jumpPressed = false;
    private final double GRAVITY = 0.6;
    private final double MOVE_SPEED = 4;
    private int score = 0;
    private int lives = 3;
    private boolean gameEnded = false;

    @FXML
    private void initialize() {
        setupLevel();
        setupInput();
        startGameLoop();
    }

    private void setupLevel() {
        player = new Player(50, 320);
        blocks.clear(); coins.clear(); enemies.clear();
        for (int i = 0; i < 20; i++) blocks.add(new Block(i * 30, 360, 30, 40));
        blocks.add(new Block(120, 260, 30, 30));
        blocks.add(new Block(150, 200, 30, 30));
        blocks.add(new Block(300, 300, 30, 30));
        coins.add(new Coin(125, 230, 10));
        coins.add(new Coin(155, 170, 10));
        coins.add(new Coin(305, 270, 10));
        enemies.add(new Enemy(250, 330));
        flag = new Flag(550, 260, 20, 100);
        score = 0;
        lives = 3;
        gameEnded = false;
        updateHUD();
    }

    private void setupInput() {
        gamePane.setFocusTraversable(true);
        gamePane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = true;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = true;
            if (e.getCode() == KeyCode.SPACE) jumpPressed = true;
        });
        gamePane.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) leftPressed = false;
            if (e.getCode() == KeyCode.RIGHT) rightPressed = false;
            if (e.getCode() == KeyCode.SPACE) jumpPressed = false;
        });
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                renderLevel();
            }
        };
        gameLoop.start();
    }

    private void update() {
        if (gameEnded) return;
        // Horizontal movement
        double dx = 0;
        if (leftPressed) dx -= MOVE_SPEED;
        if (rightPressed) dx += MOVE_SPEED;
        player.move(dx);

        // Gravity and jump
        player.setVelocityY(player.getVelocityY() + GRAVITY);
        player.setY(player.getY() + player.getVelocityY());

        // Block collision (all sides)
        boolean onGround = false;
        for (Block block : blocks) {
            // Bottom collision (standing on block)
            if (player.getX() + player.getWidth() > block.getX() && player.getX() < block.getX() + block.getWidth() &&
                player.getY() + player.getHeight() > block.getY() && player.getY() + player.getHeight() < block.getY() + 20 &&
                player.getVelocityY() >= 0) {
                player.setY(block.getY() - player.getHeight());
                player.setVelocityY(0);
                onGround = true;
            }
            // Top collision (hitting head)
            if (player.getX() + player.getWidth() > block.getX() && player.getX() < block.getX() + block.getWidth() &&
                player.getY() < block.getY() + block.getHeight() && player.getY() > block.getY() + block.getHeight() - 20 &&
                player.getVelocityY() < 0) {
                player.setY(block.getY() + block.getHeight());
                player.setVelocityY(0.5);
            }
            // Left collision
            if (player.getY() + player.getHeight() > block.getY() && player.getY() < block.getY() + block.getHeight() &&
                player.getX() + player.getWidth() > block.getX() && player.getX() < block.getX() && dx > 0) {
                player.setX(block.getX() - player.getWidth());
            }
            // Right collision
            if (player.getY() + player.getHeight() > block.getY() && player.getY() < block.getY() + block.getHeight() &&
                player.getX() < block.getX() + block.getWidth() && player.getX() + player.getWidth() > block.getX() + block.getWidth() && dx < 0) {
                player.setX(block.getX() + block.getWidth());
            }
        }
        player.setOnGround(onGround);
        if (jumpPressed) {
            player.jump();
        }

        // Coin collection
        Iterator<Coin> coinIt = coins.iterator();
        while (coinIt.hasNext()) {
            Coin coin = coinIt.next();
            if (intersects(player, coin)) {
                coinIt.remove();
                score += 10;
                updateHUD();
            }
        }

        // Enemy collision (game over)
        for (Enemy enemy : enemies) {
            if (intersects(player, enemy)) {
                lives--;
                updateHUD();
                if (lives <= 0) {
                    endGame(false);
                } else {
                    // Reset player position
                    player.setX(50);
                    player.setY(320);
                    player.setVelocityY(0);
                }
                return;
            }
        }

        // Flag collision (win)
        if (intersects(player, flag)) {
            endGame(true);
        }
    }

    private boolean intersects(Entity a, Entity b) {
        return a.getX() < b.getX() + b.getWidth() && a.getX() + a.getWidth() > b.getX() &&
               a.getY() < b.getY() + b.getHeight() && a.getY() + a.getHeight() > b.getY();
    }

    private void updateHUD() {
        scoreLabel.setText("Score: " + score);
        livesLabel.setText("Lives: " + lives);
    }

    private void endGame(boolean win) {
        gameEnded = true;
        gameLoop.stop();
        try {
            if (win) {
                App.setRoot("YouWin");
            } else {
                App.setRoot("GameOver");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderLevel() {
        gamePane.getChildren().clear();
        for (Block block : blocks) gamePane.getChildren().add(block.render());
        for (Coin coin : coins) gamePane.getChildren().add(coin.render());
        for (Enemy enemy : enemies) gamePane.getChildren().add(enemy.render());
        gamePane.getChildren().add(flag.render());
        gamePane.getChildren().add(player.render());
    }

    @FXML
    private void handleBackToMenu() throws IOException {
        if (gameLoop != null) gameLoop.stop();
        App.setRoot("MainMenu");
    }
} 