import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class halamanAwal extends JPanel {
    private BufferedImage bgImage;
    private BufferedImage logoImage;

    private JLabel logoLabel;
    private boolean showText = true;
    private Timer blinkTimer;
    private MainApp mainApp;

    public halamanAwal() {
        // Gunakan GridBagLayout
        setLayout(new GridBagLayout());
        setFocusable(true);

        // Set up blinking text timer
        blinkTimer = new Timer(500, e -> {
            showText = !showText;
            repaint();
        });
        blinkTimer.start();

        // Add key listener for spacebar
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && mainApp != null) {
                    mainApp.showPanel("menuAwal");
                }
            }
        });

        // Load semua images
        loadImages();

        // Setup GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();

        // START GAME Title
        logoLabel = createImageLabel(logoImage, 800, 300);
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(300, 0, 400, 0); // Margin bawah 40px
        add(logoLabel, gbc);

        // Add empty panel to push content up
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        GridBagConstraints gbcSpacer = new GridBagConstraints();
        gbcSpacer.gridy = 2;
        gbcSpacer.weighty = 1.0;
        add(spacer, gbcSpacer);
    }

    private void loadImages() {
        try {
            // Menggunakan safeLoadImage untuk penanganan error
            bgImage = safeLoadImage("assets/images/backgrounds/menuawal.png");
            logoImage = safeLoadImage("assets/images/ui/logo.png");
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(40, 30, 20));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw blinking text
        if (showText) {
            String text = "Press SPACE to continue";
            Font font = new Font("Arial", Font.BOLD, 24);
            g2d.setFont(font);

            // Calculate text position (centered at bottom)
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() - 50;

            // Draw text shadow
            g2d.setColor(Color.BLACK);
            g2d.drawString(text, x + 2, y + 2);

            // Draw main text
            g2d.setColor(Color.WHITE);
            g2d.drawString(text, x, y);
        }
    }
}