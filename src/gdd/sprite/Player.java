package gdd.sprite;

import static gdd.Global.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int height;
    private int currentSpeed = 2;
    private int multishotLevel = 1;
    private int shotLevel = 1;
    private Image[] multishotImages;

    private Rectangle bounds = new Rectangle(175, 135, 17, 32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        multishotImages = new Image[4]; // For 4 levels

        for (int i = 0; i < multishotImages.length; i++) {
            var ii = new ImageIcon("src/images/player" + (i + 1) + ".png");
            var scaled = ii.getImage().getScaledInstance(
                    ii.getIconWidth() * SCALE_FACTOR,
                    ii.getIconHeight() * SCALE_FACTOR,
                    java.awt.Image.SCALE_SMOOTH);
            multishotImages[i] = scaled;
        }
        setImage(multishotImages[0]);

        // Initialize width and height based on the scaled image
        width = multishotImages[0].getWidth(null);
        height = multishotImages[0].getHeight(null);

        setX(START_X);
        setY(START_Y);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    public int getMultishotLevel() {
        return multishotLevel;
    }

    public int getShotLevel() {
        return this.shotLevel;
    }

    private void setMultishotLevel(int level) {
        if (level >= 1 && level <= 4) {
            multishotLevel = level;
        }
    }

    public boolean upgradeMultishot() {
        if (multishotLevel < 4) {
            multishotLevel++;
            setImage(multishotImages[multishotLevel - 1]);
            return true;
        }
        return false;
    }

    public void act() {
        x += dx;
        y += dy;

        // Left boundary check - use the same boundary as aliens
        if (x <= BORDER_LEFT) {
            x = BORDER_LEFT;
        }

        // Right boundary check - use the same boundary as aliens, accounting for player
        // width
        if (x >= BOARD_WIDTH - BORDER_RIGHT - width) {
            x = BOARD_WIDTH - BORDER_RIGHT - width;
        }

        // Top boundary check
        if (y <= BORDER_TOP) {
            y = BORDER_TOP;
        }

        // Bottom boundary check - accounting for player height
        if (y >= BOARD_HEIGHT - BORDER_BOTTOM - height) {
            y = BOARD_HEIGHT - BORDER_BOTTOM - height;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }

        if (key == KeyEvent.VK_UP) {
            dy = -currentSpeed;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_UP) {
            dy = 0;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = 0;
        }
    }
}
