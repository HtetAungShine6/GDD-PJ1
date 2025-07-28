package gdd.powerup;

import static gdd.Global.*;
import gdd.AudioPlayer;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class SpeedUp extends PowerUp {

    public SpeedUp(int x, int y) {
        super(x, y);

        ImageIcon ii = new ImageIcon(IMG_POWERUP_SPEEDUP);

        var scaledImage = ii.getImage().getScaledInstance((int) (ii.getIconWidth() * 0.05),
                (int) (ii.getIconHeight() * 0.05),
                java.awt.Image.SCALE_SMOOTH);

        setImage(scaledImage);
    }

    public void act() {
        this.y += 2;
        this.x += 1;

        // Keep the power-up within the board boundaries
        if (x < 0 || x > BOARD_WIDTH - getImage().getWidth(null)) {
            x = Math.max(0, Math.min(x, BOARD_WIDTH - getImage().getWidth(null)));
        }
    }

    public void upgrade(Player player) {
        AudioPlayer.SoundUtils.playSoundOnce("src/audio/levelup.wav");
        player.setSpeed(player.getSpeed() + 6);
        this.die();
    }

}
