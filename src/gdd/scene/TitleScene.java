package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TitleScene extends JPanel {

    private int frame = 0;
    private Image image;
    private AudioPlayer audioPlayer;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private Game game;

    public TitleScene(Game game) {
        this.game = game;
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        initTitle();
        initAudio();
    }

    public void stop() {
        try {
            if (timer != null) {
                timer.stop();
            }

            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void initTitle() {
        var ii = new ImageIcon(IMG_TITLE);
        image = ii.getImage();

    }

    private void initAudio() {
        try {
            String filePath = "src/audio/title.wav";
            audioPlayer = new AudioPlayer(filePath);

            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error with playing sound.");
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        g.drawImage(image, 0, -80, d.width, d.height, this);

        // Blinking "Press SPACE to Start"
        if (frame % 60 < 30) {
            g.setColor(Color.red);
        } else {
            g.setColor(Color.white);
        }
        g.setFont(g.getFont().deriveFont(28f));
        String text = "Press SPACE to Start";
        int stringWidth = g.getFontMetrics().stringWidth(text);
        int x = (d.width - stringWidth) / 2;
        g.drawString(text, x, 550);

        g.setColor(Color.white);
        g.setFont(g.getFont().deriveFont(18f));
        String header = "Team Members:";
        int headerWidth = g.getFontMetrics().stringWidth(header);
        g.drawString(header, (d.width - headerWidth) / 2, 590);

        String name1 = "- Hein Naing Soe";
        String name2 = "- Htet Aung Shine";
        String name3 = "- Hsu Yee Mon";

        int spacing = 40;
        int y = 630;

        int name1Width = g.getFontMetrics().stringWidth(name1);
        int name2Width = g.getFontMetrics().stringWidth(name2);
        int name3Width = g.getFontMetrics().stringWidth(name3);

        int totalWidth = name1Width + name2Width + name3Width + spacing * 2;
        int x_for_name = (d.width - totalWidth) / 2;

        g.drawString(name1, x_for_name, y);
        g.drawString(name2, x_for_name + name1Width + spacing, y);
        g.drawString(name3, x_for_name + name1Width + name2Width + spacing * 2, y);

        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
        frame++;
    }

    private void doGameCycle() {
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

        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Title.keyPressed: " + e.getKeyCode());
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE) {
                // Load the next scene
                game.loadScene1();
            }

        }
    }
}
