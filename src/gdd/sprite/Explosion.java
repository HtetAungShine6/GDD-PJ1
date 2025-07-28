package gdd.sprite;

import static gdd.Global.*;
import java.awt.*;
import javax.swing.ImageIcon;

public class Explosion extends Sprite {

    private Image[] frames;
    private int currentFrame;
    private int frameDelay = 12;
    private int frameCounter = 0;
    private boolean visible = true;

    public Explosion(int x, int y, boolean isPlayerExplosion) {
        this.x = x; // Set explosion position
        this.y = y;
        loadFrames(isPlayerExplosion);
        setImage(frames[0]); // Set initial frame
    }

    private void loadFrames(boolean isPlayerExplosion) {
        if (isPlayerExplosion) {
            int numFrames = 9;
            frames = new Image[numFrames];
            for (int i = 0; i < numFrames; i++) {
                String path = "src/images/player-explosion" + (i + 1) + ".png";
                ImageIcon icon = new ImageIcon(path);
                frames[i] = icon.getImage().getScaledInstance(
                        (int) (icon.getIconWidth() * SCALE_FACTOR * 0.3),
                        (int) (icon.getIconHeight() * SCALE_FACTOR * 0.3),
                        Image.SCALE_SMOOTH);
            }
        } else {
            int numFrames = 6;
            frames = new Image[numFrames];

            for (int i = 0; i < numFrames; i++) {
                String path = "src/images/explosion" + (i + 1) + ".png";

                ImageIcon icon = new ImageIcon(path);
                frames[i] = icon.getImage().getScaledInstance(
                        (int) (icon.getIconWidth() * SCALE_FACTOR * 0.2),
                        (int) (icon.getIconHeight() * SCALE_FACTOR * 0.2),
                        Image.SCALE_SMOOTH);
            }
        }

    }

    @Override
    public void act() {
        visibleCountDown();
    }

    public void visibleCountDown() {
        frameCounter++;
        if (frameCounter >= frameDelay) {
            frameCounter = 0;
            currentFrame++;

            if (currentFrame < frames.length) {
                setImage(frames[currentFrame]);
            } else {
                visible = false; // Animation done
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }
}
