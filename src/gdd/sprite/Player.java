package gdd.sprite;

import static gdd.Global.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
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

//        var ii = new ImageIcon(IMG_PLAYER);
//
//        // Scale the image to use the global scaling factor
//        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
//                ii.getIconHeight() * SCALE_FACTOR,
//                java.awt.Image.SCALE_SMOOTH);
//        setImage(scaledImage);

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

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - 2 * width) {
            x = BOARD_WIDTH - 2 * width;
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
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
    }
}
