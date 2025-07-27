package gdd.sprite;

import javax.swing.*;
import java.awt.*;
import static gdd.Global.*;

public class Rock extends Sprite {
    private double dx;
    private double dy;
    private Image[] animationFrames;
    private int currentFrame = 0;
    private int frameDelay = 20;
    private int frameCount = 0;

    public Rock(int x, int y) {
        this.x = x;
        this.y = y;

        // Random angle (in radians) between -60° and +60°
        double angle = Math.toRadians(90 + (Math.random() * 90 - 45));
        double speed = 3 + Math.random() * 2; // speed range 3–5
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;

        // Load animation frames
        animationFrames = new Image[6];
        for (int i = 0; i < animationFrames.length; i++) {
            String path = "src/images/rock" + (i+1) + ".png";
            ImageIcon ii = new ImageIcon(path);
            Image scaled = ii.getImage().getScaledInstance(
                    (int)(ii.getIconWidth() * SCALE_FACTOR * 0.5),
                    (int)(ii.getIconHeight() * SCALE_FACTOR * 0.5),
                    Image.SCALE_SMOOTH
            );
            animationFrames[i] = scaled;
        }

        setImage(animationFrames[0]);
    }

    public void act() {
        x += dx;
        y += dy;

        // Animate rock
        frameCount++;
        if (frameCount >= frameDelay) {
            frameCount = 0;
            currentFrame = (currentFrame + 1) % animationFrames.length;
            setImage(animationFrames[currentFrame]);
        }

        if (x < 0 || x > BOARD_WIDTH || y > BOARD_HEIGHT) {
            visible = false;
        }
    }
}
