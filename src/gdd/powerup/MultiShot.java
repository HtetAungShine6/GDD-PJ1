// src/gdd/powerup/Multishot.java
package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class MultiShot extends PowerUp {

    public MultiShot(int x, int y) {
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_POWERUP_MULTISHOT);
        int scaledWidth = (int) (ii.getIconWidth() * SCALE_FACTOR * 0.3);
        int scaledHeight = (int) (ii.getIconHeight() * SCALE_FACTOR * 0.3);
        var scaledImage = ii.getImage().getScaledInstance(
                scaledWidth,
                scaledHeight,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    @Override
    public void act() {
        this.y += 2; // Move down
    }

    @Override
    public void upgrade(Player player) {
        if (player.upgradeMultishot()) {
            // optionally play upgrade sound
        } else {
            // optionally play “max level” sound or show visual feedback
        }
        this.die();
    }
}