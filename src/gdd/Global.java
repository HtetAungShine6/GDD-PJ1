package gdd;

public class Global {
    private Global() {
        // Prevent instantiation
    }

    public static final int SCALE_FACTOR = 2; // Scaling factor for sprites
    public static final int BOARD_WIDTH = 716; // Doubled from 358
    public static final int BOARD_HEIGHT = 700; // Doubled from 350
    public static final int BORDER_RIGHT = 80; // Doubled from 30
    public static final int BORDER_LEFT = 10; // Doubled from 5
    public static final int BORDER_TOP = 50; // Top boundary for player
    public static final int BORDER_BOTTOM = 100; // Bottom boundary for player

    public static final int GROUND = 580; // Doubled from 290
    public static final int BOMB_HEIGHT = 20; // Doubled from 5
    public static final int BOMB_WIDTH = 10;

    public static final int ALIEN_HEIGHT = 24; // Doubled from 12
    public static final int ALIEN_WIDTH = 24; // Doubled from 12
    public static final int ALIEN_INIT_X = 300; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
    public static final int NUMBER_OF_ALIENS_TO_DESTROY = 24;
    public static final int CHANCE = 5;
    public static final int DELAY = 17;
    public static final int PLAYER_WIDTH = 60; // Doubled from 15
    public static final int PLAYER_HEIGHT = 40; // Doubled from 10
    public static final int ROCK_WIDTH = 40; // or your rock image width
    public static final int ROCK_HEIGHT = 40;

    // Images
    public static final String IMG_ENEMY = "src/images/alienType1.png";
    public static final String IMG_ENEMY2 = "src/images/alienType2.png";
    public static final String IMG_PLAYER = "src/images/player.png";
    public static final String IMG_SHOT = "src/images/shoot.png";
    public static final String IMG_TITLE = "src/images/title.png";

    public static final String IMG_BOSS = "src/images/boss1.png";

    public static final String IMG_POWERUP_SPEEDUP = "src/images/speedUp.png";
    public static final String IMG_POWERUP_MULTISHOT = "src/images/multishot.png";

}
