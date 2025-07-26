package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Bomb;
import gdd.sprite.Boss;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene1 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;
    private int score = 0;
    private Image background;

    // private Shot shot;
    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    private int currentRow = -1;
    // TODO load this map from a file
    private int mapOffset = 0;

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;

    public Scene1(Game game) {
        this.game = game;
        initBoard();
        // gameInit();
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadSpawnDetailsFromCSV(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int frame = Integer.parseInt(parts[0]);
                String type = parts[1];
                int x = Integer.parseInt(parts[2]);
                int y = Integer.parseInt(parts[3]);
                spawnMap.put(frame, new SpawnDetails(type, x, y));
            }
        } catch (IOException e) {
            System.err.println("CSV load failed: " + e.getMessage());
        }
    }

    private void initBoard() {
        loadSpawnDetailsFromCSV("src/map/scene2_spawn.csv");
        background = new ImageIcon("src/images/lvl1.png").getImage();
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        timer.stop();
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {

        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();

        player = new Player();
    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        // Set star color to white
        g.setColor(Color.WHITE);

        // Draw multiple stars in a cluster pattern
        // Main star (larger)
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        // Smaller surrounding stars
        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        // Tiny stars for more detail
        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY - 15, 1, 1);
        g.fillOval(centerX - 5, centerY - 18, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
    }

    private void drawAliens(Graphics g) {

        for (Enemy enemy : enemies) {

            if (enemy.isVisible()) {

                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }

            if (enemy.isDying()) {

                enemy.die();
            }
        }
    }

    private void drawPowreUps(Graphics g) {

        for (PowerUp p : powerups) {

            if (p.isVisible()) {

                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }

            if (p.isDying()) {

                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
        if (player.isDying()) {
            player.die();
//            inGame = false;
            boolean explosionDone = true;
            for (Explosion ex : explosions) {
                if (ex.isVisible()) {
                    explosionDone = false;
                    break;
                }
            }

            if (explosionDone) {
                inGame = false;
            }
        }
    }

    private void drawShot(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {
        for (Enemy e : enemies) {
            if (e instanceof Boss) {
                Boss boss = (Boss) e;
                for (Bomb b : boss.getBombs()) {
                    if (!b.isDestroyed()) {
                        g.drawImage(b.getImage(), b.getX(), b.getY(), this);
                    }
                }
            } else {
                Bomb b = e.getBomb();
                if (!b.isDestroyed()) {
                    g.drawImage(b.getImage(), b.getX(), b.getY(), this);
                }
            }
        }
    }

    private void drawExplosions(Graphics g) {
        List<Explosion> toRemove = new ArrayList<>();
        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }
        explosions.removeAll(toRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.drawImage(background, 0, 0, d.width, d.height, this);
        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 90, 60);
        g.setColor(Color.green);
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 90, 80);
        g.drawString("Speed: " + player.getSpeed(), 90, 100);
        // g.drawString("Shots: " + player.getShotLevel(), 20, 70);
        g.drawString("Multishot Level: " + player.getMultishotLevel(), 90,  120);

        if (inGame) {

            // drawMap(g); // Draw background stars first
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    private void update() {
        // Check enemy spawn
        // TODO this approach can only spawn one enemy at a frame
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            // Create a new enemy based on the spawn details
            switch (sd.type) {
                case "Alien1":
                    Enemy enemy = new Alien1(sd.x, sd.y);
                    enemies.add(enemy);
                    break;
                // Add more cases for different enemy types if needed
                case "Alien2":
                    Enemy enemy2 = new Alien2(sd.x, sd.y);
                    enemies.add(enemy2);
                    break;
                case "PowerUp-SpeedUp":
                    // Handle speed up item spawn
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                case "PowerUp-Multishot":
                    // Handle multishot item spawn
                    PowerUp multiShot = new MultiShot(sd.x, sd.y);
                    powerups.add(multiShot);
                    break;
                case "Boss":
                    Boss boss = new Boss(sd.x, sd.y);
                    enemies.add(boss);
                    break;
                default:
                    System.out.println("Unknown enemy type: " + sd.type);
                    break;
            }
        }

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        // player
        player.act();

        // Power-ups
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                }
            }
        }

        // Enemies
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);
            } else {
                Bomb bomb = enemy.getBomb();
                if (!bomb.isDestroyed()) {
                    bomb.setY(bomb.getY() + 1);
                    if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                        bomb.setDestroyed(true);
                    }
                }
            }
        }

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        int hitboxPadding = 40;

        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    if (!enemy.isVisible() || !shot.isVisible()) {
                        continue;
                    }

                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();
                    int enemyWidth = enemy.getImageWidth();
                    int enemyHeight = enemy.getImageHeight();

                    if (enemy instanceof Boss) {
                        // Optional: increase hitbox size further if needed
                        enemyWidth += 20;
                        enemyHeight += 20;
                    }

                    if (shotX >= enemyX && shotX <= enemyX + enemyWidth
                            && shotY >= enemyY && shotY <= enemyY + enemyHeight) {

                        if (enemy instanceof Boss) {
                            ((Boss) enemy).takeHit();
                            score += 100;
                            explosions.add(new Explosion(enemyX, enemyHeight, false));
                        } else {
//                            var ii = new ImageIcon(IMG_EXPLOSION);
//                            enemy.setImage(ii.getImage());
                            enemy.setDying(true);
                            deaths++;
                            score += 100;
                            explosions.add(new Explosion(enemyX, enemyY, false));
                        }

                        shot.die();
                        shotsToRemove.add(shot);
                    }
                }

                int y = shot.getY();
                // y -= 4;
                y -= 20;

                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);

        // Update explosion animation frames
        for (Explosion explosion : explosions) {
            explosion.visibleCountDown();
        }

        // enemies
        for (Enemy enemy : enemies) {
            int x = enemy.getX();
            if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
                direction = -1;
                for (Enemy e2 : enemies) {
                    e2.setY(e2.getY() + GO_DOWN);
                }
            }
            if (x <= BORDER_LEFT && direction != 1) {
                direction = 1;
                for (Enemy e : enemies) {
                    e.setY(e.getY() + GO_DOWN);
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy instanceof Boss) {
                Boss boss = (Boss) enemy;
                for (Bomb bomb : boss.getBombs()) {
                    int chance = randomizer.nextInt(50); // less frequent

                    if (chance == CHANCE && bomb.isDestroyed() && boss.isVisible()) {
                        bomb.setDestroyed(false);
                        bomb.setX(boss.getX() + randomizer.nextInt(60) - 30);
                        bomb.setY(boss.getY());
                    }

                    if (!bomb.isDestroyed()) {
                        bomb.setY(bomb.getY() + 2);
                        if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                            bomb.setDestroyed(true);
                        }

                        int bombX = bomb.getX();
                        int bombY = bomb.getY();
                        int playerX = player.getX();
                        int playerY = player.getY();

                        if (player.isVisible()
                                && bombX >= playerX
                                && bombX <= (playerX + PLAYER_WIDTH)
                                && bombY >= playerY
                                && bombY <= (playerY + PLAYER_HEIGHT)) {
                            var ii = new ImageIcon(IMG_EXPLOSION);
                            player.setImage(ii.getImage());
                            player.setDying(true);
                            bomb.setDestroyed(true);
                        }
                    }
                }
            } else {
                Bomb bomb = enemy.getBomb();
                int chance = randomizer.nextInt(15);
                if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {
                    bomb.setDestroyed(false);
                    int alienCenterX = enemy.getX() + enemy.getImage().getWidth(null) / 2;
                    int alienBottomY = enemy.getY() + enemy.getImage().getHeight(null);
                    bomb.setX(alienCenterX);
                    bomb.setY(alienBottomY);
                }

                if (!bomb.isDestroyed()) {
                    bomb.setY(bomb.getY() + 1);
                    if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                        bomb.setDestroyed(true);
                    }

                    int bombX = bomb.getX();
                    int bombY = bomb.getY();
                    int playerX = player.getX();
                    int playerY = player.getY();

                    if (player.isVisible() && !bomb.isDestroyed()
                            && bombX >= playerX
                            && bombX <= (playerX + PLAYER_WIDTH)
                            && bombY >= playerY
                            && bombY <= (playerY + PLAYER_HEIGHT)) {
                        explosions.add(new Explosion(player.getX(), player.getY(), true));
                        player.setDying(true);
                        bomb.setDestroyed(true);
                    }

                    if (!bomb.isDestroyed()) {
                        bomb.setY(bomb.getY() + 1);
                        if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                            bomb.setDestroyed(true);
                        }
                    }
                }
            }
        }
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Scene2.keyPressed: " + e.getKeyCode());

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                int multishotLevel = player.getMultishotLevel();
                int centerX = player.getX() + PLAYER_WIDTH / 2;

                int spacing = 10;
                int totalWidth = spacing * (multishotLevel - 1);
                int startX = centerX - totalWidth / 2;

                for (int i = 0; i < multishotLevel; i++) {
                    int shotX = startX + i * spacing;
                    shots.add(new Shot(shotX, y));
                }
            }
        }
    }
}
