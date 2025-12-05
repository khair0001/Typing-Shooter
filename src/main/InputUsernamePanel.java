import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class InputUsernamePanel extends BasePanel {
    private BufferedImage bgImage;
    private BufferedImage submitButtonImage;
    private BufferedImage skipButtonImage;
    
    private JTextField usernameField;
    private CustomButtonLabel submitButton;
    private CustomButtonLabel skipButton;
    
    private JLabel titleLabel;
    private JLabel messageLabel;
    private JLabel scoreInfoLabel;
    private JLabel instructionLabel;
    private JLabel charCountLabel;
    private JLabel hintLabel;
    
    private int finalScore;
    private DatabaseManager dbManager;
    
    // Custom Button dengan hover effect
    private class CustomButtonLabel extends JLabel {
        private BufferedImage normalImage;
        private boolean isHovered = false;
        private int baseWidth;
        private int baseHeight;
        private final int HOVER_ENLARGEMENT = 10;

        public CustomButtonLabel(BufferedImage image, int width, int height) {
            this.normalImage = image;
            this.baseWidth = width;
            this.baseHeight = height;
            
            setDimensions(baseWidth, baseHeight);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    setDimensions(baseWidth + HOVER_ENLARGEMENT, baseHeight + HOVER_ENLARGEMENT);
                    if (getParent() != null) {
                        getParent().revalidate();
                    }
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    setDimensions(baseWidth, baseHeight);
                    if (getParent() != null) {
                        getParent().revalidate();
                    }
                    repaint();
                }
            });
        }
        
        public void updateSize(int width, int height) {
            this.baseWidth = width;
            this.baseHeight = height;
            setDimensions(width, height);
        }
        
        private void setDimensions(int w, int h) {
            Dimension newDim = new Dimension(w, h);
            setPreferredSize(newDim);
            setMinimumSize(newDim);
            setMaximumSize(newDim);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (normalImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
                    RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                    RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(normalImage, 0, 0, getWidth(), getHeight(), this);
                
                if (isHovered) {
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
                g2d.dispose();
            }
        }
    }
    
    public InputUsernamePanel() {
        setLayout(null);
        setFocusable(true);
        setupEscapeKeyListener();
        
        loadImages();
        initializeComponents();
        
        // Add component listener for responsive resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayout();
            }
        });
    }
    
    private void loadImages() {
        bgImage = safeLoadImage(AssetPath.BG_GAMEOVER);
        submitButtonImage = safeLoadImage(AssetPath.UI_MULAI);
        skipButtonImage = safeLoadImage(AssetPath.UI_BACK_TO_MENU);
    }
    
    private void initializeComponents() {
        // Title Label - "CONGRATULATIONS!"
        titleLabel = new JLabel("CONGRATULATIONS!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 56));
        titleLabel.setForeground(new Color(255, 215, 0));
        add(titleLabel);
        
        // Message Label - "Your score is in TOP 10!"
        messageLabel = new JLabel("Your score is in TOP 10!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 28));
        messageLabel.setForeground(Color.CYAN);
        add(messageLabel);
        
        // Score Info Label
        scoreInfoLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreInfoLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        scoreInfoLabel.setForeground(Color.YELLOW);
        add(scoreInfoLabel);
        
        // Instruction Label
        instructionLabel = new JLabel("Enter your name to save your score:", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        instructionLabel.setForeground(Color.WHITE);
        add(instructionLabel);
        
        // Username TextField dengan styling
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.BOLD, 28));
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setBackground(new Color(40, 40, 60));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.YELLOW);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.CYAN, 3),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        // Limit karakter ke 20
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (usernameField.getText().length() >= 20 && e.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        
        // Enter key untuk submit
        usernameField.addActionListener(e -> submitScore());
        add(usernameField);
        
        // Character counter label
        charCountLabel = new JLabel("(Max 20 characters)", SwingConstants.CENTER);
        charCountLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        charCountLabel.setForeground(new Color(180, 180, 180));
        add(charCountLabel);
        
        // Submit Button
        submitButton = new CustomButtonLabel(submitButtonImage, 350, 70);
        submitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                submitScore();
            }
        });
        add(submitButton);
        
        // Skip Button
        skipButton = new CustomButtonLabel(skipButtonImage, 350, 70);
        skipButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                skipToLeaderboard();
            }
        });
        add(skipButton);
        
        // Hint label
        hintLabel = new JLabel("Press ENTER to submit or ESC to skip", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        hintLabel.setForeground(new Color(150, 150, 150));
        add(hintLabel);
    }
    
    private void updateLayout() {
        int panelWidth = getWidth() > 0 ? getWidth() : 720;
        int panelHeight = getHeight() > 0 ? getHeight() : 1080;
        
        // Calculate scale factor
        double scale = Math.min(panelWidth / 720.0, panelHeight / 1080.0);
        scale = Math.min(scale, 1.0);
        
        // Scaled dimensions
        int titleFontSize = (int)(56 * scale);
        int messageFontSize = (int)(28 * scale);
        int scoreFontSize = (int)(32 * scale);
        int instructionFontSize = (int)(20 * scale);
        int fieldFontSize = (int)(28 * scale);
        int charCountFontSize = (int)(14 * scale);
        int hintFontSize = (int)(16 * scale);
        
        int fieldWidth = (int)(400 * scale);
        int fieldHeight = (int)(60 * scale);
        int buttonWidth = (int)(350 * scale);
        int buttonHeight = (int)(70 * scale);
        
        // Center X position
        int centerX = panelWidth / 2;
        
        // Y positions (responsive)
        int titleY = (int)(80 * scale);
        int messageY = (int)(160 * scale);
        int scoreY = (int)(220 * scale);
        int instructionY = (int)(300 * scale);
        int fieldY = (int)(350 * scale);
        int charCountY = (int)(415 * scale);
        int submitY = (int)(480 * scale);
        int skipY = (int)(565 * scale);
        int hintY = (int)(650 * scale);
        
        // Update fonts
        titleLabel.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        messageLabel.setFont(new Font("Arial", Font.BOLD, messageFontSize));
        scoreInfoLabel.setFont(new Font("Monospaced", Font.BOLD, scoreFontSize));
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, instructionFontSize));
        usernameField.setFont(new Font("Arial", Font.BOLD, fieldFontSize));
        charCountLabel.setFont(new Font("Arial", Font.ITALIC, charCountFontSize));
        hintLabel.setFont(new Font("Arial", Font.ITALIC, hintFontSize));
        
        // Update bounds
        int labelWidth = (int)(600 * scale);
        
        titleLabel.setBounds(centerX - labelWidth/2, titleY, labelWidth, (int)(70 * scale));
        messageLabel.setBounds(centerX - labelWidth/2, messageY, labelWidth, (int)(40 * scale));
        scoreInfoLabel.setBounds(centerX - labelWidth/2, scoreY, labelWidth, (int)(50 * scale));
        instructionLabel.setBounds(centerX - labelWidth/2, instructionY, labelWidth, (int)(30 * scale));
        usernameField.setBounds(centerX - fieldWidth/2, fieldY, fieldWidth, fieldHeight);
        charCountLabel.setBounds(centerX - fieldWidth/2, charCountY, fieldWidth, (int)(20 * scale));
        
        // Update button sizes and positions
        submitButton.updateSize(buttonWidth, buttonHeight);
        submitButton.setBounds(centerX - buttonWidth/2, submitY, buttonWidth, buttonHeight);
        
        skipButton.updateSize(buttonWidth, buttonHeight);
        skipButton.setBounds(centerX - buttonWidth/2, skipY, buttonWidth, buttonHeight);
        
        hintLabel.setBounds(centerX - labelWidth/2, hintY, labelWidth, (int)(30 * scale));
        
        revalidate();
        repaint();
    }
    
    public void setScoreData(int score, DatabaseManager dbManager) {
        this.finalScore = score;
        this.dbManager = dbManager;
        
        // Update score display (tanpa accuracy)
        scoreInfoLabel.setText(String.format("Score: %d", score));
        
        // Color based on score value
        if (score >= 1000) {
            scoreInfoLabel.setForeground(new Color(0, 255, 0)); // Green for high score
        } else if (score >= 500) {
            scoreInfoLabel.setForeground(Color.YELLOW);
        } else {
            scoreInfoLabel.setForeground(Color.ORANGE);
        }
    }
    
    private void submitScore() {
        String username = usernameField.getText().trim();
        
        // Validasi input
        if (username.isEmpty()) {
            showError("Please enter your name!");
            usernameField.requestFocusInWindow();
            return;
        }
        
        if (username.length() > 20) {
            showError("Name must be 20 characters or less!");
            usernameField.requestFocusInWindow();
            return;
        }
        
        // Validasi karakter (hanya huruf, angka, dan spasi)
        if (!username.matches("[a-zA-Z0-9 ]+")) {
            showError("Name can only contain letters, numbers, and spaces!");
            usernameField.requestFocusInWindow();
            return;
        }
        
        // Simpan ke database (tanpa accuracy)
        if (dbManager != null) {
            boolean success = dbManager.insertScore(username, finalScore);
            
            if (success) {
                // Tampilkan konfirmasi sukses
                JOptionPane.showMessageDialog(this, 
                    "Score saved successfully!\nWelcome to the leaderboard, " + username + "!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                navigateTo("leaderboardPanel");
            } else {
                showError("Failed to save score to database!");
            }
        } else {
            showError("Database connection error!");
        }
    }
    
    private void skipToLeaderboard() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to skip?\nYour score won't be saved to the leaderboard!",
            "Confirm Skip",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (choice == JOptionPane.YES_OPTION) {
            navigateTo("leaderboardPanel");
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Input Error", 
            JOptionPane.WARNING_MESSAGE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Draw background
        if (bgImage != null) {
            int panelW = getWidth();
            int panelH = getHeight();
            int imgW = bgImage.getWidth();
            int imgH = bgImage.getHeight();
            
            double scale = Math.max((double)panelW / imgW, (double)panelH / imgH);
            int scaledW = (int)(imgW * scale);
            int scaledH = (int)(imgH * scale);
            int x = (panelW - scaledW) / 2;
            int y = (panelH - scaledH) / 2;
            
            g2d.drawImage(bgImage, x, y, scaledW, scaledH, this);
        } else {
            g2d.setColor(new Color(20, 20, 40));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // Dark overlay untuk readability
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Calculate responsive frame
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        double scale = Math.min(panelWidth / 720.0, panelHeight / 1080.0);
        
        int frameX = (int)(220 * scale);
        int frameY = (int)(280 * scale);
        int frameW = (int)(640 * scale);
        int frameH = (int)(380 * scale);
        int radius = (int)(20 * scale);
        
        // Draw decorative frame around input area
        g2d.setColor(new Color(255, 215, 0, 100)); // Gold transparent
        g2d.setStroke(new BasicStroke(3));
        
        int centerX = panelWidth / 2;
        frameX = centerX - frameW / 2;
        
        g2d.drawRoundRect(frameX, frameY, frameW, frameH, radius, radius);
        
        // Draw inner glow
        g2d.setColor(new Color(0, 255, 255, 50)); // Cyan glow
        g2d.fillRoundRect(frameX + 5, frameY + 5, frameW - 10, frameH - 10, radius, radius);
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        // Reset field saat panel ditampilkan
        usernameField.setText("");
        // Update layout when shown
        updateLayout();
        // Focus ke text field setelah panel ditampilkan
        SwingUtilities.invokeLater(() -> {
            usernameField.requestFocusInWindow();
        });
    }
    
    @Override
    public void removeNotify() {
        super.removeNotify();
        // Clear data saat panel ditutup
        usernameField.setText("");
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(720, 1080);
    }
}