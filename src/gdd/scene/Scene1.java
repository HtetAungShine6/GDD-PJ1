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
import gdd.sprite.Rock;
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
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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
    private int backgroundY = 0;
    private double backgroundScrollSpeed = 0.6;
    private double backgroundPosition = 0.0;

    private int direction = -1; // Enemy movement direction
    private boolean inGame = true;
    private String message = "Game Over";
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;

    private boolean bossComing = false;
    private boolean bossSpawned = false;
    private boolean showBossAlert = false;
    private int bossAlertFramesLeft = 0;
    private boolean bossDefeated = false;

    public Scene1(Game game) {
        initBoard();
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
            String line = br.readLine();

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
        loadSpawnDetailsFromCSV("src/map/scene1_spawn.csv");
        background = new ImageIcon("src/images/stage1Background.png").getImage();
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start(); // Start game loop

        gameInit();
        initAudio();
    }

    public void stop() {
        timer.stop(); // Stop game loop
        try {
            if (audioPlayer != null) {
                audioPlayer.stop(); // Stop background music
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

    private void drawAliens(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);

                // Draw boss rocks if this is a boss
                if (enemy instanceof Boss boss) {
                    for (Rock rock : boss.getRocks()) {
                        if (rock.isVisible()) {
                            g.drawImage(rock.getImage(), rock.getX(), rock.getY(), this);
                        }
                    }
                }
            }
            if (enemy.isDying()) {
                enemy.die(); // Clean up dying enemies
            }
        }
    }

    private void drawPowerUps(Graphics g) {
        for (PowerUp p : powerups) {

            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }
            if (p.isDying()) {
                p.die(); // Clean up collected power-ups
            }
        }
    }

    private void drawPlayer(Graphics g) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
        if (player.isDying()) {
            player.die();
            boolean explosionDone = true;
            for (Explosion ex : explosions) {
                if (ex.isVisible()) {
                    explosionDone = false;
                    break;
                }
            }

            if (explosionDone) {
                if (audioPlayer != null) {
                    audioPlayer.stop();
                }
                AudioPlayer.SoundUtils.playSoundOnce("src/audio/gameOver.wav");
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
            for (Bomb bomb : e.getBombs()) {
                if (!bomb.isDestroyed()) {
                    g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
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

        try {
            doDrawing(g);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawBossHealthBar(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy instanceof Boss boss && boss.isVisible()) {
                int maxHealth = 100; // change if you set different health
                int currentHealth = boss.getHealth();
                int barWidth = Math.min(BOARD_WIDTH - 100, maxHealth * 2);
                int barHeight = 15;
                int x = (BOARD_WIDTH - barWidth) / 2;
                int y = 30;

                // Background
                g.setColor(Color.GRAY);
                g.fillRect(x, y, barWidth, barHeight);

                // Health
                g.setColor(Color.RED);
                g.fillRect(x, y, (int) ((currentHealth / (double) maxHealth) * barWidth), barHeight);

                // Border
                g.setColor(Color.WHITE);
                g.drawRect(x, y, barWidth, barHeight);
            }
        }
    }

    private void doDrawing(Graphics g) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (bossSpawned) {
            // Draw boss background with scrolling
            g.drawImage(bossBackground, 0, bossBackgroundY, d.width, d.height, this);
            g.drawImage(bossBackground, 0, bossBackgroundY - d.height, d.width, d.height, this);
        } else {
            // Draw normal background with scrolling
            g.drawImage(background, 0, backgroundY, d.width, d.height, this);
            g.drawImage(background, 0, backgroundY - d.height, d.width, d.height, this);
        }

        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 90, 60);
        g.setColor(Color.green);
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 90, 80);
        g.drawString("Speed: " + player.getSpeed(), 90, 100);
        g.drawString("Multishot Level: " + player.getMultishotLevel(), 90, 120);

        if (inGame && !bossDefeated) {
            drawExplosions(g);
            drawPowerUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
            drawBossHealthBar(g);
        } else if (inGame && bossDefeated) {
            inGame = false;
            timer.stop();
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
            AudioPlayer.SoundUtils.playSoundOnce("src/audio/victory.wav");
            message = "🎉 Victory! You defeated the Boss!";
            gameVictory(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        if (showBossAlert && bossAlertFramesLeft > 0) {
            g.setColor(Color.RED);
            g.setFont(new Font("Helvetica", Font.BOLD, 48));
            String alertText = "⚠️ BOSS IS COMING! ⚠️";
            int stringWidth = g.getFontMetrics().stringWidth(alertText);
            g.drawString(alertText, (BOARD_WIDTH - stringWidth) / 2, BOARD_HEIGHT / 2);
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

    private void gameVictory(Graphics g) {

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

    private void changeMusic(String path) {
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
            audioPlayer = new AudioPlayer(path);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Failed to change music: " + e.getMessage());
        }
    }

    private Image bossBackground = new ImageIcon("src/images/bossFightBackground.png").getImage();
    private int bossBackgroundY = 0;
    private double bossBackgroundPosition = 0.0;

    private void spawnBossAfterAlert() {
        bossSpawned = true;
        Boss boss = new Boss(BOARD_WIDTH / 2 - 50, 50);
        enemies.add(boss);
        background = bossBackground;
        bossBackgroundY = 0;
        bossBackgroundPosition = 0.0;
        changeMusic("src/audio/bossFight_sound.wav");
    }

    private void update() {
        // Update scrolling background
        backgroundPosition += backgroundScrollSpeed;
        backgroundY = (int) backgroundPosition;
        if (backgroundY >= d.height) {
            backgroundY = backgroundY - d.height;
            backgroundPosition = backgroundPosition - d.height;
        }

        // Update boss background scrolling
        if (bossSpawned) {
            bossBackgroundPosition += backgroundScrollSpeed;
            bossBackgroundY = (int) bossBackgroundPosition;
            if (bossBackgroundY >= d.height) {
                bossBackgroundY = bossBackgroundY - d.height;
                bossBackgroundPosition = bossBackgroundPosition - d.height;
            }
        }

        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            switch (sd.type) {
                case "Alien1":
                    Enemy alien1 = new Alien1(sd.x, sd.y);
                    enemies.add(alien1);
                    break;
                case "Alien2":
                    Enemy alien2 = new Alien2(sd.x, sd.y);
                    enemies.add(alien2);
                    break;
                case "Boss":
                    // Boss spawning from CSV
                    if (!bossSpawned) {
                        showBossAlert = true;
                        bossAlertFramesLeft = 180; // Show alert for 3 seconds
                        bossComing = true;
                    }
                    break;
                case "PowerUp-SpeedUp":
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                case "PowerUp-Multishot":
                    PowerUp multiShot = new MultiShot(sd.x, sd.y);
                    powerups.add(multiShot);
                    break;
                default:
                    System.out.println("Unknown entity type: " + sd.type);
                    break;
            }
        }

        // Handle boss alert and spawning
        if (showBossAlert && bossAlertFramesLeft > 0) {
            bossAlertFramesLeft--;
            if (bossAlertFramesLeft == 0) {
                showBossAlert = false;
                if (bossComing && !bossSpawned) {
                    spawnBossAfterAlert();
                }
            }
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
        List<Enemy> enemiesToRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);

                // Remove enemies that have gone off the bottom of the screen
                if (enemy.getY() > BOARD_HEIGHT + 50) {
                    enemy.die();
                    enemiesToRemove.add(enemy);
                }
            } else {
                if (enemy.isDying()) {
                    boolean allBombsDestroyed = true;
                    for (Bomb bomb : enemy.getBombs()) {
                        if (!bomb.isDestroyed()) {
                            allBombsDestroyed = false;
                            break;
                        }
                    }
                    if (allBombsDestroyed) {
                        enemiesToRemove.add(enemy);
                    }
                }
            }
        }
        enemies.removeAll(enemiesToRemove);

        // Update enemy bombs
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.updateBombTimer();
            }
        } // Update all bombs from all enemies
        for (Enemy enemy : enemies) {
            if (enemy instanceof Alien2) {
                // Handle Alien2 bomb movement
                ((Alien2) enemy).moveBombs();
            } else {
                // Handle normal bomb movement
                for (Bomb bomb : enemy.getBombs()) {
                    if (!bomb.isDestroyed()) {
                        bomb.setY(bomb.getY() + 2);

                        if (bomb.getY() > BOARD_HEIGHT + 50 ||
                                bomb.getX() < -50 || bomb.getX() > BOARD_WIDTH + 50) {
                            bomb.setDestroyed(true);
                        }
                    }
                }
            }
        }

        // Handle collision detection for all bombs from all enemies
        for (Enemy enemy : enemies) {
            for (Bomb bomb : enemy.getBombs()) {
                if (!bomb.isDestroyed()) {
                    // Check collision with player
                    int bombX = bomb.getX();
                    int bombY = bomb.getY();
                    int playerX = player.getX();
                    int playerY = player.getY();

                    if (player.isVisible() && !bomb.isDestroyed()
                            && bombX + BOMB_WIDTH >= playerX
                            && bombX <= (playerX + PLAYER_WIDTH)
                            && bombY + BOMB_HEIGHT >= playerY
                            && bombY <= (playerY + PLAYER_HEIGHT)) {
                        explosions.add(new Explosion(player.getX(), player.getY(), true));
                        player.setDying(true);
                        bomb.setDestroyed(true);
                    }
                }
            }
            // Clean up destroyed bombs
            enemy.cleanupDestroyedBombs();

            // Check rock collisions with player (for Boss)
            if (enemy instanceof Boss boss) {
                for (Rock rock : boss.getRocks()) {
                    if (rock.isVisible() && player.isVisible()) {
                        int rockX = rock.getX();
                        int rockY = rock.getY();
                        int playerX = player.getX();
                        int playerY = player.getY();

                        // Check collision using rock and player dimensions
                        if (rockX + rock.getImageWidth() >= playerX
                                && rockX <= (playerX + player.getImageWidth())
                                && rockY + rock.getImageHeight() >= playerY
                                && rockY <= (playerY + player.getImageHeight())) {
                            explosions.add(new Explosion(player.getX(), player.getY(), true));
                            player.setDying(true);
                            rock.setVisible(false); // Rock disappears on collision
                        }
                    }
                }
            }
        }
        List<Shot> shotsToRemove = new ArrayList<>();

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
                        enemyWidth += 20;
                        enemyHeight += 20;
                    }

                    if (shotX >= enemyX && shotX <= enemyX + enemyWidth
                            && shotY >= enemyY && shotY <= enemyY + enemyHeight) {

                        if (enemy instanceof Boss) {
                            Boss boss = (Boss) enemy;
                            boss.takeHit();
                            score += 100;
                            AudioPlayer.SoundUtils.playSoundOnce("src/audio/explosion.wav");
                            explosions.add(new Explosion(enemyX, enemyHeight, false));
                            if (boss.isDead()) {
                                boss.setDying(true);
                                bossDefeated = true;
                                timer.stop();
                            }
                        } else {
                            enemy.setDying(true);
                            score += 100;
                            AudioPlayer.SoundUtils.playSoundOnce("src/audio/explosion.wav");
                            explosions.add(new Explosion(enemyX, enemyY, false));
                        }

                        shot.die();
                        shotsToRemove.add(shot);
                    }
                }

                // Check shot collision with boss rocks
                for (Enemy enemy : enemies) {
                    if (enemy instanceof Boss boss && boss.isVisible()) {
                        for (Rock rock : boss.getRocks()) {
                            if (rock.isVisible() && shot.isVisible()) {
                                int rockX = rock.getX();
                                int rockY = rock.getY();

                                if (shotX >= rockX && shotX <= rockX + rock.getImageWidth()
                                        && shotY >= rockY && shotY <= rockY + rock.getImageHeight()) {
                                    // Rock destroyed by shot
                                    rock.setVisible(false);
                                    explosions.add(new Explosion(rockX, rockY, false));
                                    score += 10; // Small score for destroying rocks
                                    shot.die();
                                    shotsToRemove.add(shot);
                                    break; // Exit rock loop since shot is destroyed
                                }
                            }
                        }
                        if (!shot.isVisible())
                            break; // Exit enemy loop if shot was destroyed
                    }
                }

                int y = shot.getY();
                y -= 20;

                if (y < -50) {
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
        // Keyboard input handler for player controls
        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Scene2.keyPressed: " + e.getKeyCode());
            player.keyPressed(e);
            int y = player.getY();
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                // Handle shooting when spacebar is pressed
                AudioPlayer.SoundUtils.playSoundOnce("src/audio/laser.wav");

                int multishotLevel = player.getMultishotLevel();
                int centerX = player.getX() + PLAYER_WIDTH / 2;

                int spacing = 10;
                int totalWidth = spacing * (multishotLevel - 1); // Total width of shot spread
                int startX = centerX - totalWidth / 2;

                // Create multiple shots based on multishot level
                for (int i = 0; i < multishotLevel; i++) {
                    int shotX = startX + i * spacing;
                    shots.add(new Shot(shotX, y));
                }
            }
        }
    }
}
