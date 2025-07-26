package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;

public class Explosion extends Sprite {

    private Image[] frames;       // Explosion frame images
    private int currentFrame;     // Which frame is being shown
    private int frameDelay = 12;   // How long to show each frame (in ticks)
    private int frameCounter = 0; // Counter for frame delay
    private boolean visible = true;

    public Explosion(int x, int y, boolean isPlayerExplosion) {

        initExplosion(x, y);
        loadFrames(isPlayerExplosion);
        setImage(frames[0]);
    }

    private void initExplosion(int x, int y) {

        this.x = x;
        this.y = y;
    }
    private void loadFrames(boolean isPlayerExplosion) {
        if(isPlayerExplosion){
            int numFrames = 9;
            frames = new Image[numFrames];
            for(int i = 0; i < numFrames; i++){
                String path = "src/images/player-explosion"+(i+1)+".png";
                ImageIcon icon = new ImageIcon(path);
                frames[i] = icon.getImage().getScaledInstance(
                        (int)(icon.getIconWidth() * SCALE_FACTOR * 0.3),
                        (int)(icon.getIconHeight() * SCALE_FACTOR * 0.3),
                        Image.SCALE_SMOOTH
                );
            }
        } else{
            int numFrames = 6;
            frames = new Image[numFrames];

            for (int i = 0; i < numFrames; i++) {
                String path = "src/images/explosion" + (i + 1) + ".png";

                ImageIcon icon = new ImageIcon(path);
                frames[i] = icon.getImage().getScaledInstance(
                        (int)(icon.getIconWidth() * SCALE_FACTOR * 0.2),
                        (int)(icon.getIconHeight() * SCALE_FACTOR * 0.2),
                        Image.SCALE_SMOOTH
                );
            }
        }

    }

//    private void loadFrames() {
//        int numFrames = 6;
//        frames = new Image[numFrames];
//
//        for (int i = 0; i < numFrames; i++) {
//            String path = "src/images/explosion" + (i + 1) + ".png"; // ex: explosion1.png
//            ImageIcon icon = new ImageIcon(path);
//            frames[i] = icon.getImage().getScaledInstance(
//                    (int) (icon.getIconWidth() * SCALE_FACTOR * 0.2),
//                    (int) (icon.getIconHeight() * SCALE_FACTOR * 0.2),
//                    Image.SCALE_SMOOTH);
//        }
//    }

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
