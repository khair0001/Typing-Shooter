import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class halamanAwal extends BasePanel {

    private BufferedImage bgImage;
    private BufferedImage logoImage;

    private JLabel logoLabel;
    private boolean showText = true;
    private Thread blinkThread;

    public halamanAwal() {
        setLayout(new GridBagLayout());
        setFocusable(true);
        setupEscapeKeyListener();

        loadImages();
        startBlinkThread();
        setupKeyListener();

        GridBagConstraints gbc = new GridBagConstraints();

        logoLabel = createImageLabel(logoImage, 800, 300);
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(300, 0, 400, 0);
        add(logoLabel, gbc);

        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        GridBagConstraints gbcSpacer = new GridBagConstraints();
        gbcSpacer.gridy = 2;
        gbcSpacer.weighty = 1.0;
        add(spacer, gbcSpacer);
    }

    private void setupKeyListener() {
        // Hapus semua key listener yang ada
        KeyListener[] listeners = getKeyListeners();
        for (KeyListener listener : listeners) {
            removeKeyListener(listener);
        }

        // Gunakan InputMap dan ActionMap untuk key bindings
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // Definisikan action untuk SPACE
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "spacePressed");
        actionMap.put("spacePressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("SPACE pressed - navigating to menuAwal");
                navigateTo("menuAwal");
            }
        });

        // Definisikan action untuk ESC
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escPressed");
        actionMap.put("escPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Pastikan panel bisa difokus
        setFocusable(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                requestFocusInWindow();
                System.out.println("halamanAwal - Focus requested");
            }
        });
    }

    private void startBlinkThread() {
        // Stop old thread if exists
        if (blinkThread != null && blinkThread.isAlive()) {
            blinkThread.interrupt();
        }

        blinkThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    showText = !showText;

                    SwingUtilities.invokeLater(() -> {
                        repaint();
                    });

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        blinkThread.start();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (blinkThread != null) {
            blinkThread.interrupt();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Request focus immediately when panel is added
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            System.out.println("halamanAwal - Focus requested");
        });
    }

    private void loadImages() {
        try {
            bgImage = safeLoadImage(AssetPath.BG_MENU_AWAL);
            logoImage = safeLoadImage(AssetPath.UI_LOGO);
        } catch (Exception e) {
            System.err.println("Error loading images in halamanAwal: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (bgImage != null) {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(new Color(40, 30, 20));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        if (showText) {
            String text = "Press SPACE to continue";
            Font font = new Font("Arial", Font.BOLD, 24);
            g2d.setFont(font);

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() - 50;

            g2d.setColor(Color.BLACK);
            g2d.drawString(text, x + 2, y + 2);

            g2d.setColor(Color.WHITE);
            g2d.drawString(text, x, y);
        }
    }
}