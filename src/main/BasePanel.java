import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public abstract class BasePanel extends JPanel {
    protected MainApp mainApp;

    public BasePanel() {
        setOpaque(false);
    }

    public void setMainApp(MainApp app) {
        this.mainApp = app;
    }

    protected void navigateTo(String panelName) {
        if (mainApp != null) {
            mainApp.showPanel(panelName);
        } else {
            System.err.println("MainApp belum di-set di BasePanel.");
        }
    }

    protected BufferedImage safeLoadImage(String path) {
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

    protected JLabel createImageLabel(final BufferedImage image, final int width, final int height) {
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
                    g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        label.setPreferredSize(new Dimension(width, height));
        label.setMinimumSize(new Dimension(width, height));
        label.setMaximumSize(new Dimension(width, height));
        label.setOpaque(false);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return label;
    }

    protected void addClickListener(final JLabel label, final String action, final String targetPanel) {
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(action + " clicked!");
                if ("Exit".equals(action)) {
                    System.exit(0);
                } else if (targetPanel != null) {
                    navigateTo(targetPanel);
                }
            }
        });
    }

    protected void addHoverClickListener(final JLabel label, final BufferedImage normalImage,
            final BufferedImage hoverImage, final String action, final String targetPanel) {

        // --- 1. HOVER LISTENER ---
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Ganti gambar ke hoverImage saat kursor masuk
                if (hoverImage != null) {
                    // Gunakan SwingUtilities.invokeLater karena ini event UI
                    SwingUtilities.invokeLater(() -> {
                        label.setIcon(new ImageIcon(hoverImage));
                        label.repaint();
                    });
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Kembalikan ke normalImage saat kursor keluar
                if (normalImage != null) {
                    SwingUtilities.invokeLater(() -> {
                        label.setIcon(new ImageIcon(normalImage));
                        label.repaint();
                    });
                }
            }

            // --- 2. CLICK LISTENER ---
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(action + " clicked!");
                if ("Exit".equals(action)) {
                    System.exit(0);
                } else if (targetPanel != null) {
                    navigateTo(targetPanel);
                }
            }
        });

        // Set ikon awal (diperlukan karena createImageLabel hanya mengatur
        // paintComponent)
        // Catatan: Anda mungkin perlu sedikit memodifikasi createImageLabel agar ia
        // juga menerima dan mengatur Icon/Image di awal.
        // Untuk sederhana, kita akan set Icon di sini:
        label.setIcon(new ImageIcon(normalImage));
    }

    public void setupEscapeKeyListener() {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Cek apakah tombol yang ditekan adalah ESCAPE
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });
    }

    protected DatabaseManager getDatabaseManager() {
    if (mainApp != null) {
        return mainApp.getDatabaseManager();
    }
    System.err.println("MainApp not set in BasePanel");
    return null;
}
}