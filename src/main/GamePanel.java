import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // --- ASSETS DAN STATE GAME ---
    private BufferedImage bgImage;
    private BufferedImage heartImage;
    private BufferedImage bulletImage;
    private BufferedImage airStrikeExplosionImage; // Asset ledakan ulti

    private Player player;
    private List<Enemy> enemies;
    private List<Bullet> bullets;

    private int score = 0;
    private int lives = 10;
    private int ultiMeter = 0;
    private final int MAX_ULTI = 100;
    private final int PLAYER_ULTI_COST = MAX_ULTI; // Biaya aktivasi ulti

    private Timer gameTimer;
    private final int DELAY = 16; // ~60 FPS
    private long lastSpawnTime = System.currentTimeMillis();
    private final long BASE_SPAWN_INTERVAL = 3000;
    private int spawnCountModifier = 0;

    private String activeTargetWord = null;
    private Enemy activeTarget = null;
    private StringBuilder typedBuffer = new StringBuilder();

    // --- DATABASE KATA ---
    private final String[] wordDatabase = { "java", "gui", "thread", "musuh", "typing", "shooter", "asset",
            "aplikasi" };
    private Random rand = new Random();

    // --- PATH ASET (SILAHKAN GANTI PATH FILE INI) ---
    private static final String PATH_BG = "assets/images/backgrounds/gamebg.png";
    private static final String PATH_HEART = "assets/images/characters/life.png";
    private static final String PATH_BULLET = "assets/images/effects/bullet.png";
    private static final String PATH_ULTI_EXPLOSION = "assets/images/characters/ulti.png";

    // Aset Player
    private static final String PATH_PLAYER_IDLE = "assets/images/characters/diam.png";
    private static final String PATH_PLAYER_SHOOT = "assets/images/characters/tembak.png";
    private static final String PATH_PLAYER_ULTI = "assets/images/characters/ulti.png";

    // Aset Musuh (Enemies)
    private static final String PATH_ENEMY_LEFT = "assets/images/enemies/jalanKiri.png";
    private static final String PATH_ENEMY_RIGHT = "assets/images/enemies/jalanKanan.png";
    private static final String PATH_ENEMY_NORMAL = "assets/images/enemies/tertembak.png";
    private static final String PATH_ENEMY_EXPLODE = "assets/images/enemies/meledak.png";

    // --- UTILITY LOAD IMAGES ---
    private BufferedImage safeLoadImage(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return ImageIO.read(file);
            } else {
                System.err.println("Image not found: " + path);
                return new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + path);
            return new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public GamePanel() {
        setLayout(new BorderLayout());
        setFocusable(true);

        loadAssets();
        initializeGameObjects();
        setupInputListener();

        // Initialize game timer
        gameTimer = new Timer(DELAY, this);
        gameTimer.start();
    }

    private void loadAssets() {
        bgImage = safeLoadImage(PATH_BG);
        heartImage = safeLoadImage(PATH_HEART);
        bulletImage = safeLoadImage(PATH_BULLET);
        airStrikeExplosionImage = safeLoadImage(PATH_ULTI_EXPLOSION);

        BufferedImage playerIdle = safeLoadImage(PATH_PLAYER_IDLE);
        BufferedImage playerShoot = safeLoadImage(PATH_PLAYER_SHOOT);
        BufferedImage playerUlti = safeLoadImage(PATH_PLAYER_ULTI);
        player = new Player(playerIdle, playerShoot, playerUlti);
    }

    private void initializeGameObjects() {
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        // Posisi awal Player (disesuaikan dengan screenshot: Kiri bawah)
        player.x = 20;
        player.y = 580;
    }

    // --- GAME LOOP & LOGIC ---
    @Override
    public void actionPerformed(ActionEvent e) {
        updateGameLogic();
        repaint();
    }

    private void updateGameLogic() {
        checkSpawnCycle();
        updateEnemies();
        updateBullets();
        checkCollisions();

        // Cek Game Over (Jika lives <= 0)
        if (lives <= 0) {
            gameTimer.stop();
            System.out.println("GAME OVER! Score: " + score);
            // Panggil navigasi ke panel Game Over di sini: mainApp.showPanel("gameOver");
        }
    }

    private void checkSpawnCycle() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastSpawnTime >= BASE_SPAWN_INTERVAL) {

            if (rand.nextDouble() < 0.3) {
                spawnCountModifier = 1;
            }

            int totalSpawn = 1 + spawnCountModifier;

            for (int i = 0; i < totalSpawn; i++) {
                String word = wordDatabase[rand.nextInt(wordDatabase.length)];
                // Spawn musuh di Kanan Layar (x=1080)
                enemies.add(new Enemy(word, 1080 + rand.nextInt(300), 580 + rand.nextInt(20)));
            }

            spawnCountModifier = 0;
            lastSpawnTime = currentTime;
        }
    }

    private void updateEnemies() {
        List<Enemy> toRemove = new ArrayList<>();
        // Garis Pertahanan di KIRI Commando (misal: x=100, dekat Player)
        final int DEFENSE_LINE_X = player.x + 20;
        long currentTime = System.currentTimeMillis();

        for (Enemy enemy : enemies) {
            enemy.update();

            // Serangan Langsung: Musuh mencapai garis pertahanan
            if (enemy.x <= DEFENSE_LINE_X) {

                // --- LOGIKA PELEDAKAN DIRI BARU ---
                if (!enemy.isPendingRemoval) { // Pastikan hanya diproses sekali
                    lives = Math.max(0, lives - 1); // HP berkurang 1
                    enemy.startRemovalProcess(); // Musuh mulai meledak dan delay
                    if (activeTarget == enemy) {
                        resetTypingTarget();
                    }
                }
                // ----------------------------------
            }
            if (enemy.isPendingRemoval && currentTime - enemy.removalTime >= enemy.REMOVAL_DELAY) {
                toRemove.add(enemy);
            }
        }
        enemies.removeAll(toRemove);
    }

    private void updateBullets() {
        bullets.removeIf(b -> {
            b.update();

            // KONDISI PENGHAPUSAN BARU: Target peluru sudah selesai diketik/meledak
            if (b.target.isPendingRemoval) {
                // Peluru harus berhenti di lokasi target saat ini dan hilang
                return true;
            }

            // KONDISI LAMA: Peluru keluar dari batas kanan layar
            return b.x > getWidth();
        });
    }

    // --- Modifikasi checkCollisions() di GamePanel.java ---
    private void checkCollisions() {
        // Peluru akan dihapus jika kondisi berikut terpenuhi:
        bullets.removeIf(bullet -> {
            // Cek apakah peluru sudah mengenai target aktif (jika ada)
            if (activeTarget != null) {
                if (activeTarget.getBounds().intersects(bullet.getBounds())) {
                    // Peluru mengenai musuh yang sedang ditandai

                    // // Memicu efek visual hit pada musuh (seperti yang dibuat sebelumnya)
                    // activeTarget.triggerHitVisual();

                    // Peluru ini harus hilang
                    return true;
                }
            }

            // JIKA TIDAK mengenai target aktif: peluru tetap ada (return false)
            return false;
        });
    }

    // --- INPUT & TYPING LOGIC ---
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

    private void handleTyping(char key) {
        if (activeTarget == null) {
            for (Enemy enemy : enemies) {
                if (enemy.word.charAt(0) == key) {
                    activeTarget = enemy;
                    activeTargetWord = enemy.word;
                    typedBuffer.append(key);
                    fireBullet(activeTarget);
                    break;
                }
            }
        } else {
            // Logika Peluru per Huruf (Setelah Penguncian)
            int nextCharIndex = typedBuffer.length();
            if (nextCharIndex < activeTargetWord.length()) {
                if (activeTargetWord.charAt(nextCharIndex) == key) {
                    typedBuffer.append(key);
                    fireBullet(activeTarget);

                    // Cek Penghancuran
                    if (typedBuffer.length() == activeTargetWord.length()) {
                        killActiveTarget();
                    }
                }
                // Jika salah, tidak ada efek
            }
        }
    }

    private void fireBullet(Enemy target) {
        // Peluru menembak dari Player ke posisi Musuh yang dikunci
        bullets.add(new Bullet(player.x + 80, target.y + (target.height / 2), target)); // Tembak ke tengah vertikal
                                                                                        // musuh
        player.setState(Player.SHOOTING);
        Timer resetTimer = new Timer(100, evt -> player.setState(Player.IDLE));
        resetTimer.setRepeats(false);
        resetTimer.start();
    }

    private void killActiveTarget() {
        if (activeTarget != null) {
            // 1. Tambah skor dan ulti meter
            score += 10;
            ultiMeter = Math.min(MAX_ULTI, ultiMeter + 100);

            // 2. Memicu proses penghilangan (musuh meledak dan delay dimulai)
            activeTarget.startRemovalProcess();

            // 3. Reset target pengetikan
            resetTypingTarget();

            // PENTING: JANGAN PANGGIL enemies.remove(activeTarget) DI SINI
        }
    }

    private void resetTypingTarget() {
        activeTarget = null;
        activeTargetWord = null;
        typedBuffer.setLength(0);
    }

    // --- Modifikasi activateUlti() di GamePanel.java ---
    private void activateUlti() {
        if (ultiMeter >= PLAYER_ULTI_COST) {

            int enemiesDestroyed = enemies.size();
            int scoreGained = enemiesDestroyed * 10; // 10 poin per musuh
            score += scoreGained;
            // 1. Set Player ke state ULTI
            player.setState(Player.ULTI);

            // 2. Memicu efek ledakan pada SEMUA musuh
            for (Enemy enemy : enemies) {
                enemy.isExploding = true; // Langsung tampilkan aset meledak
            }

            // 3. Gunakan Timer untuk menunda penghapusan musuh dan reset player state
            Timer ultiTimer = new Timer(1000, evt -> {
                enemies.clear(); // Hancurkan SEMUA musuh
                player.setState(Player.IDLE); // Player kembali ke IDLE
                resetTypingTarget();
            });

            ultiTimer.setRepeats(false);
            ultiTimer.start();

            // 4. Reset Ulti Meter
            ultiMeter = 0;
            System.out.println("Serangan Udara Masif AKTIF!");
        }
    }

    // --- RENDERING (MELUKIS) ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Lukis Latar Belakang (Skala penuh)
        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }

        // 2. Lukis Player
        player.draw(g2d);

        // 3. Lukis Peluru
        for (Bullet bullet : bullets) {
            bullet.draw(g2d, bulletImage);
        }

        // 4. Lukis Musuh dan Kata
        for (Enemy enemy : enemies) {
            enemy.draw(g2d, typedBuffer.toString(), activeTarget == enemy);
        }

        // 5. Lukis HUD (Manual di sini karena sudah di paintComponent)
        drawHUD(g2d);
    }

    private void drawHUD(Graphics2D g2d) {
        // Ini adalah fallback rendering HUD, aslinya HUD berada di BorderLayout.NORTH
        // Kita hanya render Time, Score, dan Lives di sini jika tidak menggunakan
        // BorderLayout

        g2d.setColor(new Color(0, 0, 0, 180)); // Background HUD semi-transparan
        g2d.fillRect(0, 0, getWidth(), 50);

        // Time & Score
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2d.drawString("Time : 05", getWidth() - 300, 30);
        g2d.drawString("Score : " + score, getWidth() - 150, 30);

        // Lives
        int heartX = 10;
        for (int i = 0; i < lives; i++) {
            g2d.drawImage(heartImage, heartX, 10, 30, 30, this);
            heartX += 35;
        }

        // Ulti Meter
        g2d.setColor(Color.GRAY);
        g2d.fillRect(10, 55, MAX_ULTI * 3, 10);
        g2d.setColor(Color.CYAN);
        g2d.fillRect(10, 55, ultiMeter * 3, 10);
    }

    // --- INNER CLASSES (Aset dan Model) ---

    class Player {
        public static final int IDLE = 0, SHOOTING = 1, ULTI = 2;
        public int x, y, state = IDLE;
        private final int width = 100, height = 100;
        private BufferedImage idle, shoot, ulti;

        public Player(BufferedImage i, BufferedImage s, BufferedImage u) {
            this.idle = i;
            this.shoot = s;
            this.ulti = u;
        }

        public void setState(int state) {
            this.state = state;
        }

        public void draw(Graphics2D g) {
            BufferedImage currentAsset = idle;
            if (state == SHOOTING)
                currentAsset = shoot;
            else if (state == ULTI)
                currentAsset = ulti;
            g.drawImage(currentAsset, x, y, width, height, null);
        }
    }

    class Enemy {
        public String word;
        public int x, y;
        public boolean isExploding = false;
        private final int SPEED = 2;
        private final int width = 100, height = 100;
        public boolean isPendingRemoval = false;
        private long removalTime = 0;
        private final long REMOVAL_DELAY = 500;

        // --- Tambahkan ke class Enemy ---
        // State animasi
        private int animationFrame = 0;
        private final int MAX_FRAMES = 3; // Ada 3 frame berjalan: Kanan, Normal, Kiri
        private long lastFrameTime = 0;
        private final long FRAME_DELAY = 150; // 150ms per frame (sesuaikan kecepatan jalan)

        // Aset musuh
        private BufferedImage walkLeft = safeLoadImage(PATH_ENEMY_LEFT);
        private BufferedImage walkRight = safeLoadImage(PATH_ENEMY_RIGHT);
        private BufferedImage normal = safeLoadImage(PATH_ENEMY_NORMAL);
        private BufferedImage explode = safeLoadImage(PATH_ENEMY_EXPLODE);

        public Enemy(String word, int x, int y) {
            this.word = word.toLowerCase();
            this.x = x;
            this.y = y;
        }

        public void update() {
            if (!isPendingRemoval && !isExploding) {
                x -= SPEED;
            }

            if (!isExploding && !isPendingRemoval) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastFrameTime > FRAME_DELAY) {
                    animationFrame = (animationFrame + 1) % MAX_FRAMES;
                    lastFrameTime = currentTime;
                }
            }

            // Logika animasi berjalan
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime > FRAME_DELAY) {
                // Pindah ke frame berikutnya: 0 -> 1 -> 2 -> 0 ...
                animationFrame = (animationFrame + 1) % MAX_FRAMES;
                lastFrameTime = currentTime;
            }

        }

        public void startRemovalProcess() {
            this.isPendingRemoval = true;
            this.removalTime = System.currentTimeMillis();
            this.isExploding = true; // Langsung masuk ke visual ledakan
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        public void draw(Graphics2D g, String typed, boolean isActive) {
            BufferedImage currentAsset;
            // Dimensi asli
            int drawWidth = width;
            int drawHeight = height;
            int drawX = x;
            int drawY = y;

            if (isExploding || isPendingRemoval) {
                currentAsset = explode; // Prioritas: Aset meledak
                drawWidth = width * 2;
                drawHeight = height * 2;

                // Menyesuaikan posisi X dan Y agar ledakan tetap berpusat
                // Kita geser ke kiri sebesar setengah dari ukuran penambahan (yaitu 0.5 *
                // width)
                drawX = x - (width / 2);
                drawY = y - (height / 2);
            } else {
                // Logika animasi berjalan:
                switch (animationFrame) {
                    case 0: // Kaki Kanan Maju
                        currentAsset = walkRight;
                        break;
                    case 1: // Posisi Normal
                        currentAsset = normal;
                        break;
                    case 2: // Kaki Kiri Maju
                        currentAsset = walkLeft;
                        break;
                    case 3: // Posisi Normal (Lanjutan)
                    default:
                        currentAsset = normal;
                        break;
                }
            }

            g.drawImage(currentAsset, drawX, drawY, drawWidth, drawHeight, null);

            // --- Lukis Kata (di atas musuh) ---
            // (Kode yang sama untuk melukis teks target di sini)
            g.setFont(new Font("Arial", Font.BOLD, 18));
            int textX = x + 10;
            int textY = y - 10;

            if (isExploding) {
                g.setColor(Color.RED); // Set warna teks menjadi merah saat meledak

                // Lukis Background Kata (opsional, bisa dihilangkan jika tidak cocok dengan
                // efek ledakan)
                String fullWord = word;
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(fullWord);
                g.setColor(new Color(255, 0, 0, 100)); // Background merah semi-transparan
                g.fillRoundRect(textX - 5, textY - 20, textWidth + 10, 30, 10, 10);

                g.setColor(Color.RED); // Pastikan teks tetap merah setelah background
                g.drawString(word, textX, textY); // Tampilkan seluruh kata
            } else if (isActive) {
                // Lukis Background Kata
                String fullWord = word;
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(fullWord);
                g.setColor(new Color(100, 100, 100, 180)); // Semi-transparent gray
                g.fillRoundRect(textX - 5, textY - 20, textWidth + 10, 30, 10, 10);

                // Warna Hijau untuk bagian yang sudah diketik
                g.setColor(Color.GREEN);
                g.drawString(typed, textX, textY);

                // Warna Putih untuk sisa kata
                String remainder = word.substring(typed.length());
                g.setColor(Color.WHITE);
                g.drawString(remainder, textX + fm.stringWidth(typed), textY);
            } else {
                g.setColor(Color.WHITE);
                g.drawString(word, textX, textY);
            }
        }
    }

    class Bullet {
        public int x, y;
        private final int SPEED = 50;
        public final Enemy target;

        public Bullet(int x, int y, Enemy target) {
            this.x = x;
            this.y = y;
            this.target = target;
        }

        public void update() {
            // Peluru bergerak lurus secara horizontal menuju musuh
            x += SPEED;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, 20, 5);
        }

        public void draw(Graphics2D g, BufferedImage asset) {
            g.drawImage(asset, x, y, 20, 5, null);
        }
    }
}