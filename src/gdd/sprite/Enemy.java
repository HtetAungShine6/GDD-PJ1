package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends Sprite {

    protected List<Bomb> bombs;
    protected int bombTimer = 0; // Timer for bomb shooting frequency
    protected int bombInterval = 120; // Default: 2 seconds at 60 FPS (120 frames)

    public Enemy(int x, int y) {
        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bombs = new ArrayList<>();
        bombs.add(new Bomb(x, y));

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
        return bombs.isEmpty() ? null : bombs.get(0);
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }

    // Method to remove destroyed bombs
    public void cleanupDestroyedBombs() {
        bombs.removeIf(Bomb::isDestroyed);
    }

    // Timer-based bomb shooting method
    public void updateBombTimer() {
        bombTimer++;
    }

    public boolean canShootBomb() {
        return bombTimer >= bombInterval;
    }

    public void resetBombTimer() {
        bombTimer = 0;
    }

    public void setBombInterval(int intervalInSeconds) {
        // Convert seconds to frames (60 FPS)
        this.bombInterval = intervalInSeconds * 60;
    }

    @Override
    public void act() {
        // bomb.act();
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'act'");
    }
}
