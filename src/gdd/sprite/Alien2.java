package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.util.Random;

public class Alien2 extends Enemy {
    private int speed = 1;
    private double phase;
    private Image[] animationFrames;
    private int currentFrame = 0;
    private int frameDelay = 16;  // Controls how fast it changes frame
    private int frameCount = 0;  // Counts how many act() calls passed

    public Alien2(int x, int y) {
        super(x, y);
        animationFrames = new Image[4];

        for (int i = 0; i < 4; i++) {

            //2 khu pl lote htr tl 4 khu lone so 360 lel ny loc <might change cuz it looks ridiculous>
            String filePath = i == 0 ? "src/images/alienType2.png" : "src/images/alienType2-" + i + ".png";
            ImageIcon ii = new ImageIcon(filePath);

            int scaledWidth = (int)(ii.getIconWidth() * SCALE_FACTOR * 0.7);
            int scaledHeight = (int)(ii.getIconHeight() * SCALE_FACTOR * 0.7);

            animationFrames[i] = ii.getImage().getScaledInstance(
                    scaledWidth,
                    scaledHeight,
                    java.awt.Image.SCALE_SMOOTH
            );
        }

        setImage(animationFrames[0]);

        this.phase = Math.random() * Math.PI * 2;
    }

    @Override
    public void act(int direction) {
        this.y++;
        if (Math.random() < 0.05) {  // 20% chance
            this.x += direction * speed;
        }

        // Animate
        frameCount++;
        if (frameCount >= frameDelay) {
            frameCount = 0;
            currentFrame = (currentFrame + 1) % animationFrames.length;
            setImage(animationFrames[currentFrame]);
        }

        if (bomb != null && !bomb.isDestroyed()) {
            bomb.act();
        }
    }

}
