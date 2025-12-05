import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy {
    // Properti dasar
    public String word;
    public int x, y;
    public final int width = 100, height = 100;
    
    // Status musuh
    public boolean isExploding = false;
    public boolean isPendingRemoval = false;
    private int speed;
    
    // Animasi walking
    private int animationFrame = 0;
    private final int MAX_WALK_FRAMES = 7;
    private final long WALK_FRAME_DELAY = 100;
    
    // mati
    private int explosionFrame = 0;
    private final int MAX_EXPLODE_FRAMES = 5;
    private final long EXPLODE_DELAY = 300;
    
    // Asset gambar
    private final BufferedImage[] walkFrames;
    private final BufferedImage[] explodeFrames;
    
    // Thread animasi
    private Thread animationThread;
    private volatile boolean animating = true;

    public Enemy(String word, int x, int y, BufferedImage[] frames, BufferedImage[] expFrames, int speed) {
        this.word = word.toLowerCase();
        this.x = x;
        this.y = y;
        this.walkFrames = frames;
        this.explodeFrames = expFrames;
        this.speed = speed;
        
        startAnimationThread();
    }
    
    private void startAnimationThread() {
        animationThread = new Thread(() -> {
            while (animating) {
                try {
                    if (!isExploding) {
                        Thread.sleep(WALK_FRAME_DELAY);
                        animationFrame = (animationFrame + 1) % MAX_WALK_FRAMES;
                    } else {
                        Thread.sleep(EXPLODE_DELAY);
                        explosionFrame++;
                        
                        if (explosionFrame >= MAX_EXPLODE_FRAMES) {
                            animating = false;
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        animationThread.start();
    }
    
    public void update() {
        if (!isPendingRemoval && !isExploding) {
            x -= speed;
        }
    }

    public void startRemovalProcess() {
        this.isPendingRemoval = true;
        this.isExploding = true;
        this.explosionFrame = 0;
    }
    
    public void stopAnimation() {
        animating = false;
        if (animationThread != null) {
            animationThread.interrupt();
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isExplosionFinished() {
        return this.explosionFrame >= MAX_EXPLODE_FRAMES;
    }
    
    public void draw(Graphics2D g, String typed, boolean isActive) {
        BufferedImage currentAsset;
        
        if (isExploding) {
            currentAsset = explodeFrames[Math.min(explosionFrame, MAX_EXPLODE_FRAMES - 1)];
        } else {
            currentAsset = walkFrames[animationFrame];
        }
        
        if (currentAsset != null) {
            g.drawImage(currentAsset, x, y, width, height, null);
        }
        
        drawWord(g, typed, isActive);
    }

    private void drawWord(Graphics2D g, String typed, boolean isActive) {
        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(word);
        int centerX = this.x + (this.width / 2);
        int textX = centerX - (textWidth / 2);
        int textY = y - 10;
        
        if (isExploding) {
            g.setColor(new Color(255, 0, 0, 100));
            g.fillRoundRect(textX - 5, textY - 20, textWidth + 10, 30, 10, 10);
            g.setColor(Color.RED);
            g.drawString(word, textX, textY);
            
        } else if (isActive) {
            g.setColor(new Color(100, 100, 100, 180));
            g.fillRoundRect(textX - 5, textY - 20, textWidth + 10, 30, 10, 10);
            
            g.setColor(Color.GREEN);
            g.drawString(typed, textX, textY);
            
            String remainder = word.substring(typed.length());
            g.setColor(Color.WHITE);
            g.drawString(remainder, textX + fm.stringWidth(typed), textY);
            
        } else {
            g.setColor(Color.WHITE);
            g.drawString(word, textX, textY);
        }
    }

    public int getHeight() {
        return height;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}