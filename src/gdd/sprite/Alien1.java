package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;

public class Alien1 extends Enemy {

    // private Bomb bomb;
    private Image[] animationFrames;
    private int currentFrame = 0;
    private int frameDelay = 16; // Controls how fast it changes frame
    private int frameCount = 0; // Counts how many act() calls passed

    // Movement timing
    private int movementTimer = 0;
    private int movementInterval = 3; // Move every 3 frames for slower movement

    public Alien1(int x, int y) {
        super(x, y);
        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        animationFrames = new Image[5];

        for (int i = 0; i < 5; i++) {

            String filePath = i == 0 ? "src/images/alienType1.png" : "src/images/alienType1-" + i + ".png";
            ImageIcon ii = new ImageIcon(filePath);

            int scaledWidth = (int) (ii.getIconWidth() * SCALE_FACTOR);
            int scaledHeight = (int) (ii.getIconHeight() * SCALE_FACTOR);

            animationFrames[i] = ii.getImage().getScaledInstance(
                    scaledWidth,
                    scaledHeight,
                    java.awt.Image.SCALE_SMOOTH);
        }

        setImage(animationFrames[0]);
    }

    @Override
    public void act(int direction) {
        // Increment movement timer
        movementTimer++;

        // Move every 3 frames for slower movement
        if (movementTimer >= movementInterval) {
            movementTimer = 0; // Reset timer
            // Type 1: Move straight downward
            this.y++;
        }

        // Animate
        frameCount++;
        if (frameCount >= frameDelay) {
            frameCount = 0;
            currentFrame = (currentFrame + 1) % animationFrames.length;
            setImage(animationFrames[currentFrame]);
        }

        // Bomb moves straight down when active
        if (bomb != null && !bomb.isDestroyed()) {
            bomb.act();
        }
    }

    @Override
    public Bomb getBomb() {
        return bomb;
    }
}
