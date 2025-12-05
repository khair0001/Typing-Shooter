import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class gameOver extends BasePanel {
    private BufferedImage bgImage;
    private BufferedImage gameoverImage;
    private BufferedImage retryImage;
    private BufferedImage backToMenuImage;

    private JLabel gameoverLabel;
    private JLabel retryLabel;
    private JLabel backToMenuLabel;

    public gameOver() {
        setLayout(new GridBagLayout());
        setFocusable(true);
        setupEscapeKeyListener();

        loadImages();

        GridBagConstraints gbc = new GridBagConstraints();

        gameoverLabel = createImageLabel(gameoverImage, 640, 150);
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 40, 0);
        add(gameoverLabel, gbc);

        retryLabel = createImageLabel(retryImage, 550, 90);
        addClickListener(retryLabel, "Retry", "gamePanel");
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(retryLabel, gbc);

        backToMenuLabel = createImageLabel(backToMenuImage, 550, 90);
        addClickListener(backToMenuLabel, "Back To Menu", "menuAwal");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(backToMenuLabel, gbc);
    }

    private void loadImages() {
        try {
            bgImage = safeLoadImage(AssetPath.BG_GAMEOVER);
            gameoverImage = safeLoadImage(AssetPath.UI_GAMEOVER_TITLE);
            retryImage = safeLoadImage(AssetPath.UI_RETRY);
            backToMenuImage = safeLoadImage(AssetPath.UI_BACK_TO_MENU);
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
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