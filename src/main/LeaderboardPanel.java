import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.List;

public class LeaderboardPanel extends BasePanel {
    private BufferedImage bgImage;
    private BufferedImage backButtonImage;
    private JLabel backButtonLabel;
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    private DatabaseManager dbManager;

    public LeaderboardPanel() {
        setLayout(new BorderLayout());
        setFocusable(true);
        setupEscapeKeyListener();

        loadImages();
        initializeComponents();
    }

    private void loadImages() {
        bgImage = safeLoadImage(AssetPath.BG_GAMEOVER);
        backButtonImage = safeLoadImage(AssetPath.UI_BACK_TO_MENU);
    }

    private void initializeComponents() {
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 30));

        JLabel titleLabel = new JLabel("LEADERBOARD - TOP 10");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.YELLOW);
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel();
        tablePanel.setOpaque(false);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        // Create Table Model
        String[] columnNames = { "Rank", "Username", "Score", "Date" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setFont(new Font("Monospaced", Font.PLAIN, 16));
        leaderboardTable.setRowHeight(40);
        leaderboardTable.setBackground(new Color(30, 30, 30, 200));
        leaderboardTable.setForeground(Color.WHITE);
        leaderboardTable.setGridColor(new Color(100, 100, 100));
        leaderboardTable.setSelectionBackground(new Color(50, 50, 150));
        leaderboardTable.setSelectionForeground(Color.YELLOW);

        // Header Styling
        leaderboardTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        leaderboardTable.getTableHeader().setBackground(new Color(50, 50, 50));
        leaderboardTable.getTableHeader().setForeground(Color.CYAN);
        leaderboardTable.getTableHeader().setReorderingAllowed(false);

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < leaderboardTable.getColumnCount(); i++) {
            leaderboardTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Column widths
        leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(60); // Rank
        leaderboardTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Username
        leaderboardTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Score
        leaderboardTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Date

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));

        backButtonLabel = createImageLabel(backButtonImage, 400, 70);
        addClickListener(backButtonLabel, "Back To Menu", "menuAwal");
        buttonPanel.add(backButtonLabel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setDatabaseManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        refreshLeaderboard();
    }

    public void refreshLeaderboard() {
        // Clear existing data
        tableModel.setRowCount(0);

        if (dbManager == null) {
            dbManager = getDatabaseManager();
            if (dbManager == null) {
                System.err.println("Database Manager not set in LeaderboardPanel!");
                return;
            }
        }

        // Get top 10 scores
        List<DatabaseManager.ScoreEntry> scores = dbManager.getTop10Scores();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        int rank = 1;
        for (DatabaseManager.ScoreEntry entry : scores) {
            Object[] rowData = {
                    rank++,
                    entry.username,
                    entry.score,
                    dateFormat.format(entry.timestamp)
            };
            tableModel.addRow(rowData);
        }

        // Highlight top 3
        leaderboardTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(JLabel.CENTER);

                if (!isSelected) {
                    if (row == 0) {
                        c.setBackground(new Color(255, 215, 0, 100)); // Gold
                        c.setForeground(Color.YELLOW);
                        setFont(getFont().deriveFont(Font.BOLD, 18f));
                    } else if (row == 1) {
                        c.setBackground(new Color(192, 192, 192, 100)); // Silver
                        c.setForeground(Color.LIGHT_GRAY);
                        setFont(getFont().deriveFont(Font.BOLD, 17f));
                    } else if (row == 2) {
                        c.setBackground(new Color(205, 127, 50, 100)); // Bronze
                        c.setForeground(new Color(255, 165, 79));
                        setFont(getFont().deriveFont(Font.BOLD, 16f));
                    } else {
                        c.setBackground(new Color(30, 30, 30, 200));
                        c.setForeground(Color.WHITE);
                        setFont(getFont().deriveFont(Font.PLAIN, 16f));
                    }
                }

                return c;
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
            g2d.setColor(new Color(20, 20, 40));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Dark overlay
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void addNotify() {
        super.addNotify();
        refreshLeaderboard();
    }
}