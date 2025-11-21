import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class gameOver extends JPanel {
    private BufferedImage bgImage;
    private BufferedImage gameoverImage;
    private BufferedImage retryImage;
    private BufferedImage backToMenuImage;

    private JLabel gameoverLabel;
    private JLabel retryLabel;
    private JLabel backToMenuLabel;

    public gameOver() {
        // Gunakan GridBagLayout
        setLayout(new GridBagLayout());
        setFocusable(true);

        // Load semua images
        loadImages();

        // Setup GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();

        // START GAME Title
        gameoverLabel = createImageLabel(gameoverImage, 640, 150);
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 40, 0); // Margin bawah 40px
        add(gameoverLabel, gbc);

        // 2. MULAI button
        retryLabel = createImageLabel(retryImage, 550, 90);
        addClickListener(retryLabel, "Retry");
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0); // Sedikit margin antar menu
        add(retryLabel, gbc);

        // 3. BACK TO MENU button
        backToMenuLabel = createImageLabel(backToMenuImage, 550, 90);
        addClickListener(backToMenuLabel, "Back To Menu");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(backToMenuLabel, gbc);

    }

    private void loadImages() {
        try {
            // Menggunakan safeLoadImage untuk penanganan error
            bgImage = safeLoadImage("assets/images/backgrounds/gameoverbg.png");
            gameoverImage = safeLoadImage("assets/images/ui/gameover.png");
            retryImage = safeLoadImage("assets/images/ui/retry.png");
            backToMenuImage = safeLoadImage("assets/images/ui/backmenu.png");
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
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

                if ("Retry".equals(action)) {
                    // Logika navigasi Retry
                } else if ("Back To Menu".equals(action)) {
                    // Logika navigasi Back To Menu
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

        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(40, 30, 20));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}