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

        // Load direction-specific images
        ImageIcon rightIcon = new ImageIcon("src/images/alienType2_r.png");
        ImageIcon leftIcon = new ImageIcon("src/images/alienType2_l.png");

        int rightScaledWidth = (int) (rightIcon.getIconWidth() * SCALE_FACTOR * 0.7);
        int rightScaledHeight = (int) (rightIcon.getIconHeight() * SCALE_FACTOR * 0.7);

        int leftScaledWidth = (int) (leftIcon.getIconWidth() * SCALE_FACTOR * 0.7);
        int leftScaledHeight = (int) (leftIcon.getIconHeight() * SCALE_FACTOR * 0.7);

        rightFacingImage = rightIcon.getImage().getScaledInstance(
                rightScaledWidth, rightScaledHeight, java.awt.Image.SCALE_SMOOTH);
        leftFacingImage = leftIcon.getImage().getScaledInstance(
                leftScaledWidth, leftScaledHeight, java.awt.Image.SCALE_SMOOTH);

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
        if (this.x <= BORDER_LEFT || this.x >= BOARD_WIDTH - BORDER_RIGHT + 25) {
            facingRight = !facingRight;
            setImage(facingRight ? rightFacingImage : leftFacingImage);
        }

        // Handle bomb dropping
        if (canShootBomb()) {
            Bomb newBomb = createBomb();
            if (newBomb != null) {
                newBomb.setDestroyed(false);
                addBomb(newBomb);
                resetBombTimer();
            }
        }
    }

    public Bomb createBomb() {
        int bombStartX = this.x + getImageWidth() / 2;
        int bombStartY = this.y + getImageHeight();

        Bomb bomb = new Bomb(bombStartX, bombStartY);

        return bomb;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    // Handle Alien2's diagonal bomb movement
    public void moveBombs() {
        for (Bomb bomb : getBombs()) {
            if (!bomb.isDestroyed()) {
                // Diagonal movement for Alien2 bombs
                bomb.setY(bomb.getY() + 2);
                if (isFacingRight()) {
                    bomb.setX(bomb.getX() + 1); // Move right
                } else {
                    bomb.setX(bomb.getX() - 1); // Move left
                }

                // Check if bomb is out of bounds
                if (bomb.getY() > BOARD_HEIGHT + 50 ||
                        bomb.getX() < -50 || bomb.getX() > BOARD_WIDTH + 50) {
                    bomb.setDestroyed(true);
                }
            }
        }
    }

}
