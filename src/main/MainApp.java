import javax.swing.*;
import java.awt.*;

public class MainApp {
    private JFrame frame;
    private halamanAwal halamanAwal;
    private menuAwal menuAwal;
    private GamePanel gamePanel;
    private gameOver gameOver;
    private InputUsernamePanel inputUsernamePanel;
    private CardLayout cardLayout;
    private LeaderboardPanel leaderboardPanel;
    private JPanel mainPanel;
    private DatabaseManager dbManager;

    public MainApp() {
        frame = new JFrame("Typing Shooter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(1080, 720);
        frame.setResizable(true);
        // frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        dbManager = new DatabaseManager();

        try {
            halamanAwal = new halamanAwal();
            menuAwal = new menuAwal();
            gamePanel = new GamePanel();
            gameOver = new gameOver();
            inputUsernamePanel = new InputUsernamePanel();
            leaderboardPanel = new LeaderboardPanel();

            halamanAwal.setMainApp(this);
            menuAwal.setMainApp(this);
            gamePanel.setMainApp(this);
            gamePanel.setDatabaseManager(dbManager);
            gameOver.setMainApp(this);
            inputUsernamePanel.setMainApp(this);
            leaderboardPanel.setMainApp(this);
            leaderboardPanel.setDatabaseManager(dbManager);

            // Add panels to main panel
            mainPanel.add(halamanAwal, "halamanAwal");
            mainPanel.add(menuAwal, "menuAwal");
            mainPanel.add(gamePanel, "gamePanel");
            mainPanel.add(gameOver, "gameOver");
            mainPanel.add(inputUsernamePanel, "inputUsernamePanel");
            mainPanel.add(leaderboardPanel, "leaderboardPanel");
        } catch (Exception e) {
            System.err.println("FATAL ERROR: Gagal memuat aset gambar. Aplikasi dihentikan.");
            JOptionPane.showMessageDialog(null,
                    "Gagal memuat aset gambar. Pastikan file ada.",
                    "Kesalahan Fatal", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }

        frame.add(mainPanel);
        showPanel("halamanAwal");
        frame.setVisible(true);
    }

    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);

        switch (panelName) {
            case "halamanAwal":
                halamanAwal.requestFocusInWindow();
                break;
            case "menuAwal":
                menuAwal.requestFocusInWindow();
                break;
            case "gamePanel":
                gamePanel.requestFocusInWindow();
                gamePanel.startGame();
                break;
            case "gameOver":
                gameOver.requestFocusInWindow();
                break;
            case "inputUsernamePanel":
                inputUsernamePanel.requestFocusInWindow();
                break;
            case "leaderboardPanel":
                leaderboardPanel.requestFocusInWindow();
                leaderboardPanel.refreshLeaderboard();
                break;
            default:
                System.err.println("Panel name not recognized: " + panelName);
        }
    }

    public JPanel getPanel(String panelName) {
        switch (panelName) {
            case "halamanAwal":
                return halamanAwal;
            case "menuAwal":
                return menuAwal;
            case "gamePanel":
                return gamePanel;
            case "gameOver":
                return gameOver;
            case "inputUsernamePanel":
                return inputUsernamePanel;
            case "leaderboardPanel":
                return leaderboardPanel;
            default:
                System.err.println("Panel not found: " + panelName);
                return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainApp();
            }
        });
    }
}