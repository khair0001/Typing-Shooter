// File: Bullet.java
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Bullet {
    public int x, y;
    private final int SPEED = 50;
    public final Enemy target;

    public Bullet(int x, int y, Enemy target) {
        this.x = x;
        this.y = y;
        this.target = target;
    }

    public void update() {
        x += SPEED;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 20, 5);
    }

    public void draw(Graphics2D g, BufferedImage asset) {
        g.drawImage(asset, x, y, 20, 5, null);
    }
}