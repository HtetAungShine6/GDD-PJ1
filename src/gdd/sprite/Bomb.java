package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Bomb extends Sprite {

    private boolean destroyed;

    public Bomb(int x, int y) {
        this.destroyed = false;
        this.x = x;
        this.y = y;

        var bombImg = "src/images/bomb.png";
        var ii = new ImageIcon(bombImg);
        var scaledImage = ii.getImage().getScaledInstance(BOMB_WIDTH, BOMB_HEIGHT, java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void act() {
        // Movement is handled in Scene1
    }
}