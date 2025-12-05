import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {
    public static final int IDLE = 0, SHOOTING = 1, ULTI = 2, DEAD = 3;
    public int x, y, state = IDLE;
    private final int width = 140, height = 180;
    
    // Array untuk menyimpan frame animasi
    private BufferedImage[] idleFrames;
    private BufferedImage[] shootFrames;
    private BufferedImage[] ultiFrames;
    private BufferedImage[] deadFrames;
    
    // Variabel animasi
    private int currentFrame = 0;
    private int maxFrames = 4;
    private final int MAX_DEAD_FRAMES = 4;
    private final long FRAME_DELAY = 200;
    
    // Thread untuk animasi
    private Thread animationThread;
    private volatile boolean animating = true;
    private boolean deathAnimationComplete = false;

    public Player(BufferedImage[] idle, BufferedImage[] shoot, BufferedImage[] ulti, BufferedImage[] dead) {
        this.idleFrames = idle;
        this.shootFrames = shoot;
        this.ultiFrames = ulti;
        this.deadFrames = dead;
        
        startAnimationThread();
    }

    private void startAnimationThread() {
        if (animationThread != null && animationThread.isAlive()) {
            animating = false;
            animationThread.interrupt();
        }
        
        animating = true;
        
        animationThread = new Thread(() -> {
            while (animating) {
                try {
                    Thread.sleep(FRAME_DELAY);
                    if (state == DEAD) {
                        if (currentFrame < MAX_DEAD_FRAMES - 1) {
                            currentFrame++;
                        } else {
                            deathAnimationComplete = true;
                        }
                    } else {
                        currentFrame = (currentFrame + 1) % maxFrames;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        animationThread.start();
    }

    public void setState(int state) {
        if (this.state != state) {
            currentFrame = 0;
            deathAnimationComplete = false;
            
            if (state == DEAD) {
                maxFrames = MAX_DEAD_FRAMES;
            } else {
                maxFrames = 4;
            }
        }
        this.state = state;
        
        if (state != DEAD && !animating) {
            startAnimationThread();
        }
    }
    
    public boolean isDeathAnimationComplete() {
        return deathAnimationComplete;
    }

    public void draw(Graphics g) {
        BufferedImage currentAsset = null;
        
        if (state == IDLE) {
            currentAsset = idleFrames[currentFrame];
        } else if (state == SHOOTING) {
            currentAsset = shootFrames[currentFrame];
        } else if (state == ULTI) {
            currentAsset = ultiFrames[currentFrame];
        } else if (state == DEAD) {
            currentAsset = deadFrames[currentFrame];
        }
        
        if (currentAsset != null) {
            g.drawImage(currentAsset, x, y, width, height, null);
        }
    }
    
    public void stopAnimation() {
        animating = false;
        if (animationThread != null) {
            animationThread.interrupt();
        }
    }
    
    public void restartAnimation() {
        stopAnimation();
        currentFrame = 0;
        deathAnimationComplete = false;
        state = IDLE;
        maxFrames = 4;
        startAnimationThread();
    }
}