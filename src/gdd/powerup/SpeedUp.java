package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


public class SpeedUp extends PowerUp {

    public SpeedUp(int x, int y) {
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_POWERUP_SPEEDUP);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() ,
                ii.getIconHeight() ,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act() {
        this.y += 2;
        this.x += 1;  // Move to the right (horizontal speed)
        // bounce off screen edges
        if (x < 0 || x > BOARD_WIDTH - getImage().getWidth(null)) {
            x = Math.max(0, Math.min(x, BOARD_WIDTH - getImage().getWidth(null)));
        }
    }

    public void upgrade(Player player) {
        // Upgrade the player with speed boost
        player.setSpeed(player.getSpeed() + 6); // Increase player's speed by 4
        this.die(); // Remove the power-up after use
    }

}
