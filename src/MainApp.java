import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MainApp - GUI Utama (JFrame)
 * * Bertindak sebagai:
 * 1. Tampilan GUI (JFrame)
 * 2. Entry point (main method)
 * 3. Event Listener untuk tombol
 * 4. Pengelola Thread Pool (ExecutorService)
 */
public class MainApp extends JFrame implements ActionListener {

    // --- Komponen GUI ---
    private JLabel statusLabel;
    private JTextArea logArea;
    private JButton btnPSG, btnCity, btnReset;

    // --- Komponen Data & Thread ---
    private DatabaseManager dbManager;
    private Pemain pemain; // Shared Resource (Pemain yang diperebutkan)
    
    // Menggunakan ExecutorService (Thread Pool)
    // Ini lebih modern daripada 'new Thread()' manual
    private ExecutorService threadPool = Executors.newFixedThreadPool(2);

    /**
     * Main method - Entry point aplikasi
     */
    public static void main(String[] args) {
        // Menjalankan GUI di Event Dispatch Thread (EDT)
        // Ini adalah cara standar untuk memulai aplikasi Swing
        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }

    /**
     * Constructor - Mengatur GUI dan memuat data
     */
    public MainApp() {
        // 1. Inisialisasi Database dan Model
        dbManager = new DatabaseManager();
        pemain = dbManager.getPemain("LY27");

        if (pemain == null) {
            JOptionPane.showMessageDialog(this, 
                "Gagal memuat pemain 'LY27'. Pastikan database dan tabel sudah disiapkan.", 
                "Error Database", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // 2. Setup GUI
        setupGUI();
    }

    /**
     * Mengatur semua komponen visual GUI
     */
    private void setupGUI() {
        setTitle("Simulasi Bursa Transfer - Lamine Yamal (LY27)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel Atas (Status) ---
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        statusPanel.add(new JLabel("Status Pemain Saat Ini:"));
        statusLabel = new JLabel(pemain.getStatus() + " (oleh " + pemain.getDikontrakOleh() + ")");
        statusLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);

        // --- Panel Tengah (Log) ---
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(logArea);
        add(scrollLog, BorderLayout.CENTER);

        // --- Panel Bawah (Tombol) ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        btnPSG = new JButton("Rekrut oleh PSG");
        btnCity = new JButton("Rekrut oleh Man City");
        btnReset = new JButton("Reset Status Pemain");

        // Daftarkan listener
        btnPSG.addActionListener(this);
        btnCity.addActionListener(this);
        btnReset.addActionListener(this);
        
        // Set Action Command agar mudah diidentifikasi
        btnPSG.setActionCommand("PSG");
        btnCity.setActionCommand("Man City");
        btnReset.setActionCommand("RESET");

        buttonPanel.add(btnPSG);
        buttonPanel.add(btnCity);
        buttonPanel.add(btnReset);
        add(buttonPanel, BorderLayout.SOUTH);
        
        log("Sistem Siap. Menunggu tawaran untuk " + pemain.getNamaPemain() + "...");
    }

    /**
     * Event Handler untuk semua tombol
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        if (command.equals("RESET")) {
            // Tombol Reset
            log("\n=== MERESET STATUS PEMAIN ===");
            pemain.resetStatus(this); // 'this' adalah MainApp
        } else {
            // Tombol PSG atau Man City
            // Buat tugas baru dan kirim ke thread pool
            TugasTransfer tugas = new TugasTransfer(pemain, command, this);
            threadPool.submit(tugas);
        }
    }

    /**
     * Method (Thread-Safe) untuk menulis ke log GUI
     * Ini bisa dipanggil dari thread lain
     */
    public void log(String message) {
        // Gunakan SwingUtilities.invokeLater untuk memastikan
        // update GUI dieksekusi di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            // Auto-scroll ke bawah
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    /**
     * Method (Thread-Safe) untuk update status label
     * Ini bisa dipanggil dari thread lain
     */
    public void updateStatusLabel(String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
        });
    }
}