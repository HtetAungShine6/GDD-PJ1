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

        // Use direction values directly as velocities, doubled for faster movement
        this.velocityX = directionX;
        this.velocityY = directionY * 2.0;
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
