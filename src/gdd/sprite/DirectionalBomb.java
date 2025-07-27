package gdd.sprite;

public class DirectionalBomb extends Bomb {

    private double velocityX;
    private double velocityY;
    private double exactX;
    private double exactY;

    public DirectionalBomb(int x, int y, double directionX, double directionY) {
        super(x, y);
        initDirectionalBomb(x, y, directionX, directionY);
    }

    private void initDirectionalBomb(int x, int y, double directionX, double directionY) {
        this.exactX = x;
        this.exactY = y;

        // Normalize direction vector and set speed
        double magnitude = Math.sqrt(directionX * directionX + directionY * directionY);
        if (magnitude > 0) {
            this.velocityX = (directionX / magnitude) * 2.0; // Speed of 2 pixels per frame
            this.velocityY = (directionY / magnitude) * 2.0;
        } else {
            // Default to downward movement if no direction given
            this.velocityX = 0;
            this.velocityY = 2.0;
        }
    }

    @Override
    public void act() {
        if (!isDestroyed()) {
            exactX += velocityX;
            exactY += velocityY;

            // Update integer positions for drawing
            this.x = (int) exactX;
            this.y = (int) exactY;
        }
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }
}
