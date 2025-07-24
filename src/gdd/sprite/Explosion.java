package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;

public class Explosion extends Sprite {

    private Image[] frames;       // Explosion frame images
    private int currentFrame;     // Which frame is being shown
    private int frameDelay = 3;   // How long to show each frame (in ticks)
    private int frameCounter = 0; // Counter for frame delay
    private boolean visible = true;

    public Explosion(int x, int y) {

        initExplosion(x, y);
        loadFrames();
        setImage(frames[0]);
    }

    private void initExplosion(int x, int y) {

        this.x = x;
        this.y = y;

//        var ii = new ImageIcon(IMG_EXPLOSION);

        // Scale the image to use the global scaling factor
//        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
//                ii.getIconHeight() * SCALE_FACTOR,
//                java.awt.Image.SCALE_SMOOTH);
//        setImage(scaledImage);
    }

    private void loadFrames() {
        int numFrames = 6;  // Change based on how many explosion images you have
        frames = new Image[numFrames];

        for (int i = 0; i < numFrames; i++) {
            String path = "src/images/explosion" + (i + 1) + ".png"; // ex: explosion1.png
            ImageIcon icon = new ImageIcon(path);
            frames[i] = icon.getImage().getScaledInstance(
                    icon.getIconWidth() * SCALE_FACTOR,
                    icon.getIconHeight() * SCALE_FACTOR,
                    Image.SCALE_SMOOTH);
        }
    }

    public void act(int direction) {
        // this.x += direction;
    }

    @Override
    public void act() {
        throw new UnsupportedOperationException("Unimplemented method 'act'");
    }

    public void visibleCountDown() {
        frameCounter++;
        if (frameCounter >= frameDelay && frames != null) {
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
