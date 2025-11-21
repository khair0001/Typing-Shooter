import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class menuAwal extends JPanel {
    private BufferedImage bgImage;
    private BufferedImage startGameImage;
    private BufferedImage mulaiImage;
    private BufferedImage leaderboardImage;
    private BufferedImage exitImage;
    
    private JLabel startGameLabel;
    private JLabel mulaiLabel;
    private JLabel leaderboardLabel;
    private JLabel exitLabel;
    
    public menuAwal() {
        // Gunakan GridBagLayout
        setLayout(new GridBagLayout());
        setFocusable(true);
        
        // Load semua images
        loadImages();
        
        // Setup GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        
        // START GAME Title
        startGameLabel = createImageLabel(startGameImage, 640, 150);
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(100, 0, 40, 0); // Margin bawah 40px
        add(startGameLabel, gbc);
        
        // 2. MULAI button
        mulaiLabel = createImageLabel(mulaiImage, 550, 90);
        addClickListener(mulaiLabel, "Mulai");
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0); // Sedikit margin antar menu
        add(mulaiLabel, gbc);
        
        // 3. LEADERBOARD button
        leaderboardLabel = createImageLabel(leaderboardImage, 550, 90);
        addClickListener(leaderboardLabel, "Leaderboard");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(leaderboardLabel, gbc);
        
        // 4. EXIT button
        exitLabel = createImageLabel(exitImage, 550, 90);
        addClickListener(exitLabel, "Exit");
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 100, 0);
        add(exitLabel, gbc);
    }
    
    private void loadImages() {
    try {
        // Menggunakan safeLoadImage untuk penanganan error
        bgImage = safeLoadImage("assets/images/backgrounds/menuawal.png");
        startGameImage = safeLoadImage("assets/images/ui/start.png");
        mulaiImage = safeLoadImage("assets/images/ui/mulai.png");
        leaderboardImage = safeLoadImage("assets/images/ui/leaderboard.png");
        exitImage = safeLoadImage("assets/images/ui/exit.png");
    } catch (Exception e) {
        System.err.println("Error loading images: " + e.getMessage());
        // e.printStackTrace(); // Optional: untuk debugging
    }
}

private BufferedImage safeLoadImage(String path) {
    try {
        File file = new File(path);
        if (file.exists()) {
            return ImageIO.read(file);
        } else {
            System.err.println("Image not found: " + path);
            return new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
        }
    } catch (IOException e) {
        System.err.println("Error loading image: " + path);
        return new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
    }
}
    
    private JLabel createImageLabel(final BufferedImage image, final int width, final int height) {
        JLabel label = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                                         RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                                         RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                         RenderingHints.VALUE_ANTIALIAS_ON);
                    // Gambar diskalakan agar mengisi seluruh area JLabel
                    g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        // Atur ukuran label
        label.setPreferredSize(new Dimension(width, height));
        label.setMinimumSize(new Dimension(width, height));
        label.setMaximumSize(new Dimension(width, height));
        label.setOpaque(false);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Beri kursor tangan
        return label;
    }
    
    private void addClickListener(final JLabel label, final String action) {
        label.addMouseListener(new MouseAdapter() {
            // Hapus originalWidth dan originalHeight karena tidak digunakan lagi
            
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(action + " clicked!");
                
                if ("Mulai".equals(action)) {
                    // Logika navigasi Mulai
                } else if ("Leaderboard".equals(action)) {
                    // Logika navigasi Leaderboard
                } else if ("Exit".equals(action)) {
                    // Keluar dari aplikasi
                    System.exit(0);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                // KODE UNTUK MENGECILKAN UKURAN DIHAPUS (Tidak ada efek tekan)
                // Hanya menyisakan fungsi kosong
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // KODE UNTUK MENGEMBALIKAN UKURAN DIHAPUS (Tidak ada efek lepas)
                // Hanya menyisakan fungsi kosong
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw background
        if (bgImage != null) {
            // Gambar diskalakan agar mengisi seluruh area panel
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(40, 30, 20));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}