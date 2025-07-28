package gdd.powerup;

import static gdd.Global.*;
import gdd.AudioPlayer;
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
        this.y += 2; // Move downward
    }

    @Override
    public void upgrade(Player player) {
        player.upgradeMultishot();
        AudioPlayer.SoundUtils.playSoundOnce("src/audio/levelup.wav");
        this.die();
    }
}