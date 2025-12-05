import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
// import AssetPath dan BasePanel (diasumsikan sudah tersedia)

public class menuAwal extends BasePanel {

    // Aset Gambar
    private BufferedImage bgImage;
    private BufferedImage startGameImage;
    private BufferedImage mulaiImage;
    private BufferedImage leaderboardImage;
    private BufferedImage exitImage;
    
    // Label (Gunakan CustomButtonLabel untuk komponen yang berinteraksi)
    private JLabel startGameLabel; 
    private CustomButtonLabel mulaiLabel;
    private CustomButtonLabel leaderboardLabel;
    private CustomButtonLabel exitLabel;

private class CustomButtonLabel extends JLabel {
    private BufferedImage normalImage;
    private boolean isHovered = false;
    private final int WIDTH;
    private final int HEIGHT;

    public CustomButtonLabel(BufferedImage image, int width, int height) {
        this.normalImage = image;
        this.WIDTH = width;
        this.HEIGHT = height;
        
        // Atur ukuran tetap
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));
        
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tambahkan MouseListener untuk mengelola state hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (normalImage != null) {
            Graphics2D g2d = (Graphics2D) g.create(); 
            
            // Atur Rendering Hints
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Gambar aset
            g2d.drawImage(normalImage, 0, 0, WIDTH, HEIGHT, this);
            
            if (isHovered) {
                // Efek highlight tanpa mengubah ukuran
                g2d.setColor(new Color(255, 255, 255, 100)); 
                g2d.fillRect(0, 0, WIDTH, HEIGHT);
            }
            g2d.dispose(); 
        }
    }
}
    
    public menuAwal() {
        // Setup Dasar (Menggunakan GridBagLayout)
        setLayout(new GridBagLayout());
        setFocusable(true);
        setupEscapeKeyListener(); // Dari BasePanel

        loadImages();

        // Setup GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();

        // 1. START GAME Title (TIDAK ADA HOVER)
        startGameImage = safeLoadImage(AssetPath.UI_START_TITLE);
        startGameLabel = createImageLabel(startGameImage, 640, 150);
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(100, 0, 40, 0); 
        add(startGameLabel, gbc);

        // 2. MULAI button (DENGAN HOVER)
        mulaiLabel = new CustomButtonLabel(mulaiImage, 550, 90);
        // Menggunakan addClickListener dari BasePanel
        addClickListener(mulaiLabel, "Mulai", "gamePanel"); 
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(mulaiLabel, gbc);

        // 3. Leaderboard button (DENGAN HOVER)
        leaderboardLabel = new CustomButtonLabel(leaderboardImage, 550, 90);
        addClickListener(leaderboardLabel, "Leaderboard", "leaderboardPanel"); // Asumsi ada leaderboardPanel
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(leaderboardLabel, gbc);

        // 4. Exit button (DENGAN HOVER)
        exitLabel = new CustomButtonLabel(exitImage, 550, 90);
        addClickListener(exitLabel, "Exit", null); // BasePanel.addClickListener menangani 'Exit'
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 100, 0);
        add(exitLabel, gbc);

        // Tambahkan spacer untuk mendorong konten ke atas
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        GridBagConstraints gbcSpacer = new GridBagConstraints();
        gbcSpacer.gridy = 5;
        gbcSpacer.weighty = 1.0; 
        add(spacer, gbcSpacer);
    }
    
    private void loadImages() {
        try {
            // Menggunakan safeLoadImage dari BasePanel
            bgImage = safeLoadImage(AssetPath.BG_MENU_AWAL);
            mulaiImage = safeLoadImage(AssetPath.UI_MULAI);
            leaderboardImage = safeLoadImage(AssetPath.UI_LEADERBOARD);
            exitImage = safeLoadImage(AssetPath.UI_EXIT);
        } catch (Exception e) {
            System.err.println("Error loading images in menuAwal: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Gambar background
        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(40, 30, 20));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}