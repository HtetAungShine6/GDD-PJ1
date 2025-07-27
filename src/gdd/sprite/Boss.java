package gdd.sprite;

import static gdd.Global.*;
import java.awt.*;
import java.util.List;
import javax.swing.ImageIcon;

public class Boss extends Enemy {

    private int speed = 2;
    private double phase;
    private Image[] animationFrames;
    private int currentFrame = 0;
    private int frameDelay = 20;
    private int frameCount = 0;
    private int direction = 1;
    private int health = 10;
    private int bombDropDelay = 100;
    private int bombDropTimer = 0;

    public Boss(int x, int y) {
        super(x, y);
        animationFrames = new Image[2];

        for (int i = 0; i < 2; i++) {

            String filePath = i == 0 ? "src/images/boss1.png" : "src/images/boss1-" + i + ".png";
            ImageIcon ii = new ImageIcon(filePath);

            int scaledWidth = (int) (ii.getIconWidth() * SCALE_FACTOR * 0.7);
            int scaledHeight = (int) (ii.getIconHeight() * SCALE_FACTOR * 0.7);

            animationFrames[i] = ii.getImage().getScaledInstance(
                    scaledWidth,
                    scaledHeight,
                    java.awt.Image.SCALE_SMOOTH);
        }

        setImage(animationFrames[0]);

        this.phase = Math.random() * Math.PI * 2;

        bombs.clear(); // Remove the default bomb
        for (int i = 0; i < 5; i++) {
            bombs.add(new Bomb(x, y));
        }
    }

    @Override
    public void act(int ignoredDirection) {
        // Move horizontally
        x += speed * direction;

        // Change direction at screen bounds (example: 0 and 800)
        if (x <= 0 || x >= 800 - getImageWidth()) {
            direction *= -1;
        }

        // Animate
        frameCount++;
        if (frameCount >= frameDelay) {
            frameCount = 0;
            currentFrame = (currentFrame + 1) % animationFrames.length;
            setImage(animationFrames[currentFrame]);
        }

        // Drop bombs
        bombDropTimer++;
        if (bombDropTimer >= bombDropDelay && bombs.size() < 3) {
            bombs.add(new Bomb(this.x + getImageWidth() / 2, this.y + getImageHeight()));
            bombDropTimer = 0;
        }

        // Update bombs
        for (Bomb b : bombs) {
            if (!b.isDestroyed()) {
                b.act();
            }
        }
    }

    public void takeHit() {
        health--;
        if (health <= 0) {
            setDying(true);
        }
    }

    @Override
    public List<Bomb> getBombs() {
        return super.getBombs();
    }

    public void hit() {
        health--;
        if (health <= 0) {
            setDestroyed(true);
        }
    }

    public boolean isDead() {
        return health <= 0;
    }
}
