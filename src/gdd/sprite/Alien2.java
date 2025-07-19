package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien2 extends Enemy {
    private int speed = 2;
    private double phase;

    public Alien2(int x, int y) {
        super(x, y);

        ImageIcon ii = new ImageIcon(IMG_ENEMY2);  // Use a different image
        int scaledWidth = (int)(ii.getIconWidth() * SCALE_FACTOR * 0.5);
        int scaledHeight = (int)(ii.getIconHeight() * SCALE_FACTOR * 0.5);

        var scaledImage = ii.getImage().getScaledInstance(
                scaledWidth,
                scaledHeight,
                java.awt.Image.SCALE_SMOOTH
        );

        setImage(scaledImage);

        this.phase = Math.random() * Math.PI * 2;
    }

    @Override
    public void act(int direction) {
        this.y++;
        if (bomb != null && !bomb.isDestroyed()) {
            bomb.act();
        }
    }
}
