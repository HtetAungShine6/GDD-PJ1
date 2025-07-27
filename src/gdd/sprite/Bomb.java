package gdd.sprite;

import javax.swing.ImageIcon;

public class Bomb extends Sprite {

    private boolean destroyed;

    public Bomb(int x, int y) {
        initBomb(x, y);
    }

    private void initBomb(int x, int y) {
        setDestroyed(true);
        this.x = x;
        this.y = y;

        var bombImg = "src/images/bomb.png";
        var ii = new ImageIcon(bombImg);
        var scaledImage = ii.getImage().getScaledInstance(10, 20, java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        // setImage(ii.getImage());
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void act() {
        if (!destroyed) {
            y += 2; // Doubled from 1 to 2
        }
    }
}