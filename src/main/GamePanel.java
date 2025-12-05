import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends BasePanel {

    private BufferedImage bgImage;
    private BufferedImage heartImage;
    private BufferedImage bulletImage;
    private BufferedImage readyImage;
    private BufferedImage goImage;
    private boolean showReady = false;
    private boolean showGo = false;

    private BufferedImage[] enemyWalkFrames;
    private BufferedImage[] enemyExplodeFrames;

    private Player player;
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private AudioManager audioManager;

    private int score = 0;
    private int lives = 3;
    private int ultiMeter = 0;
    private final int MAX_ULTI = 100;
    private final int PLAYER_ULTI_COST = MAX_ULTI;

    private String activeTargetWord = null;
    private Enemy activeTarget = null;
    private StringBuilder typedBuffer = new StringBuilder();

    private Thread timeThread;
    private volatile int gameTimeSeconds = 0;

    private Thread gameThread;
    private volatile boolean running = false;

    private final int FPS = 30;
    private final long FRAME_TIME = 1000 / FPS;

    private Thread spawnThread;
    private int spawnCountModifier = 0;

    private int difficultyLevel = 1;
    private long baseSpawnDelay = 5000;
    private long currentSpawnDelay = baseSpawnDelay;

    private DatabaseManager dbManager;
    private List<String> currentWordDatabase;
    private Random rand = new Random();

    // Back to Menu button
    private Rectangle backButtonBounds;
    private boolean backButtonHovered = false;

    public GamePanel() {
        setLayout(new BorderLayout());
        setFocusable(true);
        setupEscapeKeyListener();

        loadAssets();
        audioManager = new AudioManager();
        initializeGameObjects();
        setupInputListener();
        setupMouseListener();

        // Initialize back button bounds (kiri atas, setelah lives)
        backButtonBounds = new Rectangle(10, 10, 120, 35);
    }

    public void setDatabaseManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    private void loadAssets() {
        bgImage = safeLoadImage(AssetPath.BG_GAME);
        heartImage = safeLoadImage(AssetPath.PLAYER_LIFE_HEART);
        bulletImage = safeLoadImage(AssetPath.EFFECT_BULLET);

        BufferedImage[] playerIdleFrames = new BufferedImage[] {
                safeLoadImage(AssetPath.PLAYER_IDLE_01),
                safeLoadImage(AssetPath.PLAYER_IDLE_02),
                safeLoadImage(AssetPath.PLAYER_IDLE_03),
                safeLoadImage(AssetPath.PLAYER_IDLE_04)
        };

        BufferedImage[] playerShootFrames = new BufferedImage[] {
                safeLoadImage(AssetPath.PLAYER_SHOOT_01),
                safeLoadImage(AssetPath.PLAYER_SHOOT_02),
                safeLoadImage(AssetPath.PLAYER_SHOOT_03),
                safeLoadImage(AssetPath.PLAYER_SHOOT_04)
        };

        BufferedImage[] playerUltiFrames = new BufferedImage[] {
                safeLoadImage(AssetPath.PLAYER_ULTI_01),
                safeLoadImage(AssetPath.PLAYER_ULTI_02),
                safeLoadImage(AssetPath.PLAYER_ULTI_03),
                safeLoadImage(AssetPath.PLAYER_ULTI_04)
        };

        BufferedImage[] playerDeadFrames = new BufferedImage[] {
                safeLoadImage(AssetPath.PLAYER_DEAD_01),
                safeLoadImage(AssetPath.PLAYER_DEAD_02),
                safeLoadImage(AssetPath.PLAYER_DEAD_03),
                safeLoadImage(AssetPath.PLAYER_DEAD_04)
        };

        player = new Player(playerIdleFrames, playerShootFrames, playerUltiFrames, playerDeadFrames);

        enemyWalkFrames = new BufferedImage[] {
                safeLoadImage(AssetPath.ENEMY_WALK_01),
                safeLoadImage(AssetPath.ENEMY_WALK_02),
                safeLoadImage(AssetPath.ENEMY_WALK_03),
                safeLoadImage(AssetPath.ENEMY_WALK_04),
                safeLoadImage(AssetPath.ENEMY_WALK_05),
                safeLoadImage(AssetPath.ENEMY_WALK_06),
                safeLoadImage(AssetPath.ENEMY_WALK_07)
        };

        enemyExplodeFrames = new BufferedImage[] {
                safeLoadImage(AssetPath.ENEMY_EXPLODE_01),
                safeLoadImage(AssetPath.ENEMY_EXPLODE_02),
                safeLoadImage(AssetPath.ENEMY_EXPLODE_03),
                safeLoadImage(AssetPath.ENEMY_EXPLODE_04),
                safeLoadImage(AssetPath.ENEMY_EXPLODE_05)
        };

        readyImage = safeLoadImage(AssetPath.READY_COUNTDOWN);
        goImage = safeLoadImage(AssetPath.GO_COUNTDOWN);
    }

    private void initializeGameObjects() {
        if (enemies != null) {
            synchronized (enemies) {
                for (Enemy enemy : enemies) {
                    enemy.stopAnimation();
                }
                enemies.clear();
            }
        } else {
            enemies = new ArrayList<>();
        }

        if (bullets != null) {
            synchronized (bullets) {
                bullets.clear();
            }
        } else {
            bullets = new ArrayList<>();
        }

        player.x = 20;
        player.y = 500;

        difficultyLevel = 1;
        currentSpawnDelay = baseSpawnDelay;

        score = 0;
        lives = 3;
        ultiMeter = 0;
        gameTimeSeconds = 0;

        if (player != null) {
            player.restartAnimation();
        }

        loadWordsFromDatabase();
    }

    private void loadWordsFromDatabase() {
        if (dbManager == null) {
            currentWordDatabase = new ArrayList<>();
            return;
        }

        String kesulitan = "mudah";
        if (difficultyLevel <= 3) {
            kesulitan = "mudah";
        } else if (difficultyLevel <= 6) {
            kesulitan = "sedang";
        } else {
            kesulitan = "sulit";
        }

        currentWordDatabase = dbManager.getSoalByKesulitan(kesulitan);

        if (currentWordDatabase.isEmpty()) {
            System.err.println("No words loaded from database for difficulty: " + kesulitan);
        } else {
            System.out.println("Loaded " + currentWordDatabase.size() + " words for difficulty: " + kesulitan);
        }
    }

    public void startGame() {
        stopGameThreads();

        initializeGameObjects();
        resetTypingTarget();

        running = true;

        new Thread(this::showCountdown).start();
    }

    private void showCountdown() {
        try {
            showReady = true;
            repaint();
            Thread.sleep(1000);

            showReady = false;
            showGo = true;
            repaint();
            Thread.sleep(1000);

            showGo = false;
            repaint();
            startGameThreads();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void startTimeThread() {
        if (timeThread != null && timeThread.isAlive()) {
            timeThread.interrupt();
        }

        gameTimeSeconds = 0;

        timeThread = new Thread(() -> {
            try {
                while (running) {
                    Thread.sleep(1000);
                    if (running) {
                        gameTimeSeconds++;
                        updateDifficulty();
                        SwingUtilities.invokeLater(() -> repaint());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        timeThread.start();
    }

    private void startGameThreads() {
        running = true;
        startTimeThread();

        if (audioManager != null) {
            audioManager.playBackgroundLoop("assets/audio/music/background.wav");
        }

        gameThread = new Thread(() -> {
            while (running) {
                updateGameLogic();
                SwingUtilities.invokeLater(() -> repaint());

                try {
                    Thread.sleep(FRAME_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
        });
        gameThread.start();

        spawnThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(currentSpawnDelay);
                    if (running) {
                        spawnEnemies();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
        });
        spawnThread.start();
    }

    public void stopGameThreads() {
        running = false;

        if (gameThread != null) {
            gameThread.interrupt();
            try {
                gameThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            gameThread = null;
        }

        if (spawnThread != null) {
            spawnThread.interrupt();
            try {
                spawnThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            spawnThread = null;
        }

        if (timeThread != null) {
            timeThread.interrupt();
            try {
                timeThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            timeThread = null;
        }

        if (enemies != null) {
            synchronized (enemies) {
                for (Enemy enemy : enemies) {
                    if (enemy != null) {
                        enemy.stopAnimation();
                    }
                }
                enemies.clear();
            }
        }

        if (bullets != null) {
            synchronized (bullets) {
                bullets.clear();
            }
        }

        if (audioManager != null) {
            audioManager.stopBackground();
        }
    }

    private void updateGameLogic() {
        updateEnemies();
        updateBullets();
        checkCollisions();

        if (lives <= 0) {
            if (player.state != Player.DEAD) {
                player.setState(Player.DEAD);
            }

            if (player.isDeathAnimationComplete()) {
                stopGameThreads();
                SwingUtilities.invokeLater(() -> {
                    handleGameOver();
                });
            }
        }
    }

    private void handleGameOver() {
        if (audioManager != null) {
            audioManager.playEffect("assets/audio/music/game-over.wav");
        }

        if (dbManager != null && dbManager.isTop10Score(score)) {
            InputUsernamePanel inputPanel = (InputUsernamePanel) mainApp.getPanel("inputUsernamePanel");
            if (inputPanel != null) {
                inputPanel.setScoreData(score, dbManager);
                navigateTo("inputUsernamePanel");
            }
        } else {
            if (audioManager != null) {
                audioManager.playEffect("assets/audio/music/game-over.wav");
            }
            navigateTo("gameOver");
        }
    }

    private void updateDifficulty() {
        int newDifficultyLevel = (gameTimeSeconds / 30) + 1;

        if (newDifficultyLevel != difficultyLevel && newDifficultyLevel <= 10) {
            difficultyLevel = newDifficultyLevel;
            currentSpawnDelay = Math.max(2000, baseSpawnDelay - (difficultyLevel * 50));

            loadWordsFromDatabase();

            System.out.println("Difficulty Level: " + difficultyLevel + " | Spawn Delay: " + currentSpawnDelay + "ms");
        }
    }

    private void spawnEnemies() {
        if (currentWordDatabase == null || currentWordDatabase.isEmpty()) {
            return;
        }

        int baseSpawn = 1;
        int difficultySpawn = 0;

        if (difficultyLevel >= 3)
            difficultySpawn = 1;
        if (difficultyLevel >= 6)
            difficultySpawn = 2;
        if (difficultyLevel >= 9)
            difficultySpawn = 3;

        if (rand.nextDouble() < 0.1) {
            spawnCountModifier = 1;
        }

        int totalSpawn = baseSpawn + difficultySpawn + spawnCountModifier;
        int enemySpeed = 1 + (difficultyLevel / 3);
        enemySpeed = Math.min(enemySpeed, 5);

        synchronized (enemies) {
            for (int i = 0; i < totalSpawn; i++) {
                String word = currentWordDatabase.get(rand.nextInt(currentWordDatabase.size()));

                int yOffset = i * 30;
                int randomY = 590 + (rand.nextInt(10) - 10) + yOffset;
                randomY = Math.min(randomY, 600);

                enemies.add(new Enemy(word,
                        1000 + rand.nextInt(30) + (i * 100),
                        randomY,
                        enemyWalkFrames,
                        enemyExplodeFrames,
                        enemySpeed));
            }
        }

        spawnCountModifier = 0;
    }

    private void updateEnemies() {
        final int DEFENSE_LINE_X = player.x + 20;

        synchronized (enemies) {
            List<Enemy> toRemove = new ArrayList<>();

            for (Enemy enemy : enemies) {
                enemy.update();

                if (enemy.isExploding) {
                    if (enemy.isExplosionFinished()) {
                        enemy.stopAnimation();
                        toRemove.add(enemy);
                    }
                    continue;
                }

                if (enemy.x <= DEFENSE_LINE_X && !enemy.isPendingRemoval) {
                    lives = Math.max(0, lives - 1);
                    enemy.startRemovalProcess();

                    if (activeTarget == enemy) {
                        resetTypingTarget();
                    }
                }
            }
            enemies.removeAll(toRemove);
        }
    }

    private void updateBullets() {
        synchronized (bullets) {
            bullets.removeIf(b -> {
                b.update();
                return b.target.isPendingRemoval || b.x > getWidth();
            });
        }
    }

    private void checkCollisions() {
        synchronized (bullets) {
            bullets.removeIf(bullet -> {
                if (activeTarget != null) {
                    return activeTarget.getBounds().intersects(bullet.getBounds());
                }
                return false;
            });
        }
    }

    private void setupInputListener() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = Character.toLowerCase(e.getKeyChar());
                handleTyping(key);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    activateUlti();
                }
            }
        });
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (backButtonBounds.contains(e.getPoint())) {
                    handleBackToMenu();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean wasHovered = backButtonHovered;
                backButtonHovered = backButtonBounds.contains(e.getPoint());

                if (wasHovered != backButtonHovered) {
                    repaint();
                }

                if (backButtonHovered) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }

    private void handleBackToMenu() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin kembali ke menu?\nSemua progres akan hilang!",
                "Back to Menu",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            stopGameThreads();
            navigateTo("menuAwal");
        }
    }

    private void handleTyping(char key) {
        if (player.state == Player.DEAD) {
            return;
        }

        synchronized (enemies) {
            if (activeTarget != null) {
                if (!enemies.contains(activeTarget) || activeTarget.isPendingRemoval) {
                    resetTypingTarget();
                }
            }

            if (activeTarget == null) {
                for (Enemy enemy : enemies) {
                    if (!enemy.isPendingRemoval && enemy.word.charAt(0) == key) {
                        activeTarget = enemy;
                        activeTargetWord = enemy.word;
                        typedBuffer.append(key);
                        fireBullet(activeTarget);
                        break;
                    }
                }
            } else {
                int nextCharIndex = typedBuffer.length();
                if (nextCharIndex < activeTargetWord.length()) {
                    if (activeTargetWord.charAt(nextCharIndex) == key) {
                        typedBuffer.append(key);
                        fireBullet(activeTarget);

                        if (typedBuffer.length() == activeTargetWord.length()) {
                            killActiveTarget();
                        }
                    } else {
                        resetTypingTarget();
                    }
                }
            }
        }
    }

    private void fireBullet(Enemy target) {
        if (target == null || target.isPendingRemoval) {
            return;
        }

        synchronized (bullets) {
            bullets.add(new Bullet(player.x + 80, target.y + (target.getHeight() / 2), target));
        }

        player.setState(Player.SHOOTING);

        new Thread(() -> {
            try {
                Thread.sleep(100);
                if (player.state == Player.SHOOTING) {
                    player.setState(Player.IDLE);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void killActiveTarget() {
        if (activeTarget != null) {
            score += 10;
            ultiMeter = Math.min(MAX_ULTI, ultiMeter + 10);

            activeTarget.startRemovalProcess();

            if (audioManager != null) {
                audioManager.playEffect("assets/audio/music/musuh-mati.wav");
            }

            final Enemy enemyToRemove = activeTarget;
            new Thread(() -> {
                try {
                    Thread.sleep(1550);
                    synchronized (enemies) {
                        enemies.remove(enemyToRemove);
                        enemyToRemove.stopAnimation();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

            resetTypingTarget();
        }
    }

    private void resetTypingTarget() {
        activeTarget = null;
        activeTargetWord = null;
        typedBuffer.setLength(0);
    }

    private void activateUlti() {
        if (player.state == Player.DEAD) {
            return;
        }

        if (audioManager != null) {
            audioManager.playEffect("assets/audio/music/ulti-bom.wav");
        }

        if (ultiMeter >= PLAYER_ULTI_COST) {
            synchronized (enemies) {
                int enemiesDestroyed = enemies.size();
                int scoreGained = enemiesDestroyed * 10;
                score += scoreGained;

                player.setState(Player.ULTI);

                List<Enemy> enemiesToDestroy = new ArrayList<>(enemies);

                for (Enemy enemy : enemiesToDestroy) {
                    enemy.isExploding = true;
                    enemy.isPendingRemoval = true;
                }
            }

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    synchronized (enemies) {
                        for (Enemy enemy : enemies) {
                            enemy.stopAnimation();
                        }
                        enemies.clear();
                        if (player.state == Player.ULTI) {
                            player.setState(Player.IDLE);
                        }
                        resetTypingTarget();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

            ultiMeter = 0;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (showReady && readyImage != null) {
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f);
            g2d.setComposite(alpha);

            int imgWidth = 800;
            int imgHeight = 350;
            int x = (getWidth() - imgWidth) / 2;
            int y = (getHeight() - imgHeight) / 2;

            g2d.drawImage(readyImage, x, y, imgWidth, imgHeight, null);
            g2d.setComposite(AlphaComposite.SrcOver);
            return;
        }

        if (showGo && goImage != null) {
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f);
            g2d.setComposite(alpha);

            int imgWidth = 800;
            int imgHeight = 350;
            int x = (getWidth() - imgWidth) / 2;
            int y = (getHeight() - imgHeight) / 2;

            g2d.drawImage(goImage, x, y, imgWidth, imgHeight, null);
            g2d.setComposite(AlphaComposite.SrcOver);
            return;
        }

        player.draw(g2d);

        synchronized (bullets) {
            for (Bullet bullet : bullets) {
                bullet.draw(g2d, bulletImage);
            }
        }

        synchronized (enemies) {
            for (Enemy enemy : enemies) {
                enemy.draw(g2d, typedBuffer.toString(), activeTarget == enemy);
            }
        }

        drawHUD(g2d);
    }

    private void drawHUD(Graphics2D g2d) {
        // Top bar background (slightly taller to fit everything)
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), 60); // Reduced height since we're not using 2 rows anymore

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // === LEFT: Back to Menu Button ===
        backButtonBounds = new Rectangle(10, 12, 80, 30); // Moved to left
        if (backButtonHovered) {
            g2d.setColor(new Color(255, 100, 100, 200));
        } else {
            g2d.setColor(new Color(200, 50, 50, 180));
        }
        g2d.fillRoundRect(backButtonBounds.x, backButtonBounds.y,
                backButtonBounds.width, backButtonBounds.height, 6, 6);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(backButtonBounds.x, backButtonBounds.y,
                backButtonBounds.width, backButtonBounds.height, 6, 6);

        // Button text
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String backText = "← MENU";
        int textX = backButtonBounds.x + (backButtonBounds.width - fm.stringWidth(backText)) / 2;
        int textY = backButtonBounds.y + ((backButtonBounds.height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(backText, textX, textY);

        // === Hearts (to the right of menu button) ===
        int heartX = backButtonBounds.x + backButtonBounds.width + 20;
        int heartY = 12;
        for (int i = 0; i < lives; i++) {
            g2d.drawImage(heartImage, heartX, heartY, 30, 30, this);
            heartX += 35;
        }

        // === CENTER: Level, Score, Time (all on same line) ===
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));

        // Calculate positions to center the stats
        String levelText = "LEVEL: " + difficultyLevel;
        String scoreText = "SCORE: " + score;
        String timeString = String.format("TIME: %02d:%02d", gameTimeSeconds / 60, gameTimeSeconds % 60);

        int totalWidth = g2d.getFontMetrics().stringWidth(levelText + "   " + scoreText + "   " + timeString);
        int startX = (getWidth() - totalWidth) / 2;
        int statsY = 30;

        // Draw level, score, time with equal spacing
        g2d.drawString(levelText, startX, statsY);
        startX += g2d.getFontMetrics().stringWidth(levelText + "   ");

        g2d.drawString(scoreText, startX, statsY);
        startX += g2d.getFontMetrics().stringWidth(scoreText + "   ");

        g2d.drawString(timeString, startX, statsY);

        // === RIGHT: Ulti Meter Bar ===
        int ultiBarWidth = 150;
        int ultiBarHeight = 12;
        int ultiBarX = getWidth() - ultiBarWidth - 15;
        int ultiBarY = 25;

        // Background bar
        g2d.setColor(new Color(50, 50, 50, 200));
        g2d.fillRoundRect(ultiBarX, ultiBarY, ultiBarWidth, ultiBarHeight, 6, 6);

        // Progress bar
        int progressWidth = (int) ((ultiMeter / (float) MAX_ULTI) * ultiBarWidth);
        if (ultiMeter >= MAX_ULTI) {
            int alpha = (int) (Math.abs(Math.sin(System.currentTimeMillis() / 200.0)) * 255);
            g2d.setColor(new Color(0, 255, 255, alpha));
        } else {
            g2d.setColor(new Color(0, 200, 255));
        }
        g2d.fillRoundRect(ultiBarX, ultiBarY, progressWidth, ultiBarHeight, 6, 6);

        // Border
        g2d.setColor(Color.CYAN);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(ultiBarX, ultiBarY, ultiBarWidth, ultiBarHeight, 6, 6);

        // "ULTI" label
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.setColor(Color.WHITE);
        String ultiText = "ULTIMATE";
        int ultiTextX = ultiBarX + (ultiBarWidth - g2d.getFontMetrics().stringWidth(ultiText)) / 2;
        g2d.drawString(ultiText, ultiTextX, ultiBarY - 5);
    }

}