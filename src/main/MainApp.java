import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainApp {
    private JFrame frame;
    // Gunakan nama class yang benar (huruf besar di awal)
    private halamanAwal halamanAwal;
    private menuAwal menuAwal;
    private GamePanel gamePanel; 
    private gameOver gameOver;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    public MainApp() {
        // 1. Create main frame
        frame = new JFrame("Typing Shooter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 2. Set frame size
        frame.setSize(1080, 720);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        
        // 3. Create main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // --- Perbaikan: Inisialisasi Panel di dalam try-catch ---
        try {
            // Inisialisasi semua panel
            // Catatan: Jika halamanAwal/menuAwal memerlukan MainApp sebagai parameter, sesuaikan di sini
            halamanAwal = new halamanAwal(); 
            menuAwal = new menuAwal();
            gamePanel = new GamePanel(); 
            gameOver = new gameOver();
            
            // Tambahkan semua panel ke mainPanel dengan nama unik
            mainPanel.add(halamanAwal, "halamanAwal");
            mainPanel.add(menuAwal, "menuAwal");
            mainPanel.add(gamePanel, "gamePanel");
            mainPanel.add(gameOver, "gameOver");
            
        } catch (Exception e) {
            // Tangani kesalahan pemuatan gambar
            System.err.println("FATAL ERROR: Gagal memuat aset gambar. Aplikasi dihentikan.");
            JOptionPane.showMessageDialog(null, 
                "Gagal memuat aset gambar. Pastikan file ada.", 
                "Kesalahan Fatal", JOptionPane.ERROR_MESSAGE);
            System.exit(1); 
            return;
        }
        // --------------------------------------------------------
        
        // 6. Add main panel to frame
        frame.add(mainPanel); // <--- PERBAIKAN: Hapus komentar
        
        // 7. Show halamanAwal first
        showPanel("gamePanel"); // <--- PERBAIKAN: Tampilkan panel awal
        
        // 8. Make frame visible
        frame.setVisible(true);
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
        
        // Request focus to the panel being shown
        switch(panelName) {
            case "halamanAwal":
                 halamanAwal.requestFocusInWindow();
                 break;
            case "menuAwal":
                 menuAwal.requestFocusInWindow();
                 break;
            case "gamePanel":
                gamePanel.requestFocusInWindow();
                break;
            case "gameOver":
                gameOver.requestFocusInWindow();
                break;
            default:
                System.err.println("Panel name not recognized: " + panelName);
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