package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;

public class Alien2 extends Enemy {
    private Image rightFacingImage;
    private Image leftFacingImage;
    private double exactX;
    private double exactY;
    private int movementTimer = 0;
    private int movementInterval = 3;
    private boolean facingRight = true;

    public Alien2(int x, int y) {
        super(x, y);
        // Set Alien2 to shoot every 8 seconds
        setBombInterval(8);
        this.exactX = x;
        this.exactY = y;
        bombs.clear();
        bombs.add(new DirectionalBomb(x, y, 0, 1));

        // Load direction-specific images
        ImageIcon rightIcon = new ImageIcon("src/images/alienType2_r.png");
        ImageIcon leftIcon = new ImageIcon("src/images/alienType2_l.png");

        int scaledWidth = (int) (rightIcon.getIconWidth() * SCALE_FACTOR * 0.7);
        int scaledHeight = (int) (rightIcon.getIconHeight() * SCALE_FACTOR * 0.7);

        rightFacingImage = rightIcon.getImage().getScaledInstance(
                scaledWidth, scaledHeight, java.awt.Image.SCALE_SMOOTH);
        leftFacingImage = leftIcon.getImage().getScaledInstance(
                scaledWidth, scaledHeight, java.awt.Image.SCALE_SMOOTH);

        // Start with a random facing direction and set initial image
        this.facingRight = Math.random() > 0.5;
        setImage(facingRight ? rightFacingImage : leftFacingImage);
    }

    @Override
    public void act(int direction) {
        movementTimer++;

        if (movementTimer >= movementInterval) {
            movementTimer = 0;

            exactY += 1;

            // Move diagonally based on facing direction (2 pixels horizontally)
            if (facingRight) {
                exactX += 2;
            } else {
                exactX -= 2;
            }

            this.x = (int) exactX;
            this.y = (int) exactY;
        }

        // Check for direction change at boundaries and update sprite
        if (this.x <= BORDER_LEFT || this.x >= BOARD_WIDTH - BORDER_RIGHT) {
            facingRight = !facingRight;
            setImage(facingRight ? rightFacingImage : leftFacingImage);
        }
    }

    @Override
    public Bomb getBomb() {
        return super.getBomb();
    }

    public DirectionalBomb createDirectionalBomb() {
        int bombStartX = this.x + getImageWidth() / 2;
        int bombStartY = this.y + getImageHeight();
        // Set bomb direction to match alien's movement
        double bombDirectionX = facingRight ? 1.0 : -1.0;
        double bombDirectionY = 1.0;

        return new DirectionalBomb(bombStartX, bombStartY, bombDirectionX, bombDirectionY);
    }

    public void setDirectionalBomb(DirectionalBomb newBomb) {
        bombs.clear();
        bombs.add(newBomb);
    }

}
