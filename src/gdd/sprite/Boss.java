package gdd.sprite;

import static gdd.Global.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;

public class Boss extends Enemy {

    private int speed = 2;
    private Image[] animationFrames;
    private int currentFrame = 0;
    private int frameDelay = 20;
    private int frameCount = 0;
    private int direction = 1;
    private int health = 100;
    private int bombDropDelay = 100;
    private int bombDropTimer = 0;
    private Random random = new Random();
    private List<Rock> rocks = new ArrayList<>();
    private int rockDropTimer = 0;
    private int rockDropDelay = 100;

    public List<Rock> getRocks() {
        return rocks;
    }

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

        this.rocks = new ArrayList<>();
        bombs.clear(); // Remove the default bomb from Enemy constructor

        bombDropDelay = 30 + random.nextInt(91); // Start with random delay (0.5-2 seconds)
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

        // Drop bombs at random intervals
        bombDropTimer++;
        if (bombDropTimer >= bombDropDelay && bombs.size() < 5) {
            // Allow up to 5 bombs on screen
            int offsetX = random.nextInt(41) - 20; // Random offset between -20 and +20 pixels
            int bombX = this.x + getImageWidth() / 2 - BOMB_WIDTH / 2 + offsetX;
            int bombY = this.y + getImageHeight();
            bombs.add(new Bomb(bombX, bombY));
            bombDropTimer = 0;
            bombDropDelay = 30 + random.nextInt(91); // Random between 30-120 frames
        }
        // Drop rocks
        rockDropTimer++;
        if (rockDropTimer >= rockDropDelay) {
            if (rocks.size() < 9) { // allow more total on screen
                int rocksToDrop = 3;
                for (int i = 0; i < rocksToDrop; i++) {
                    int offsetX = (int) (Math.random() * getImageWidth()) - getImageWidth() / 2;
                    int rockX = this.x + getImageWidth() / 2 + offsetX;
                    int rockY = this.y + getImageHeight();
                    rocks.add(new Rock(rockX, rockY));
                }
            }
            rockDropTimer = 0;
        }

        List<Rock> toRemove = new ArrayList<>();
        for (Rock r : rocks) {
            if (r.isVisible()) {
                r.act();
            } else {
                toRemove.add(r);
            }
        }
        rocks.removeAll(toRemove);

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

    public int getHealth() {
        return health;
    }
}
