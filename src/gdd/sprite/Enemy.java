package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Enemy extends Sprite {

    // private Bomb bomb;
    protected Bomb bomb;

    public Enemy(int x, int y) {
        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ENEMY);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act(int direction) {

        this.x += direction;
    }

    public Bomb getBomb() {
        return bomb;
    }
    
    @Override
    public void act() {
        // bomb.act();
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'act'");
    }
}
