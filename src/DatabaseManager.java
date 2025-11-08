import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class DatabaseManager untuk mengatur koneksi dan query ke DB.
 * Menggunakan JDBC (Java Database Connectivity).
 */
public class DatabaseManager {

    // --- Konfigurasi Database ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_barca"; 
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private Connection connection;

    public DatabaseManager() {
        try {
            // 1. Load driver MySQL dari file .jar
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            
            // 2. Bikin koneksi ke database
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); 
            System.out.println("✓ Koneksi database berhasil!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Gagal koneksi ke database: " + e.getMessage());
        }
    }

    /**
     * Method untuk ambil data pemain dari database berdasarkan ID
     * Menggunakan PreparedStatement agar aman dari SQL Injection
     */
    public Pemain getPemain(String idPemain) {
        if (connection == null) {
            System.err.println("❌ Koneksi database belum tersedia.");
            return null;
        }
        
        // Query SQL dengan placeholder (?)
        String sql = "SELECT * FROM pemain WHERE id_pemain = ?"; 
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) { 
            
            // Isi placeholder (?) pertama dengan idPemain
            stmt.setString(1, idPemain); 
            
            // Jalankan query SELECT
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Ambil data dari setiap kolom
                    String id = rs.getString("id_pemain");
                    String nama = rs.getString("nama_pemain");
                    String status = rs.getString("status");
                    String dikontrakOleh = rs.getString("dikontrak_oleh");

                    // Bikin objek Pemain baru
                    return new Pemain(id, nama, status, dikontrakOleh, this);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error saat mengambil pemain: " + e.getMessage());
        }
        
        return null; 
    }

    /**
     * Method untuk update status pemain di database
     * Dipanggil dari method synchronized di class Pemain
     */
    public boolean updateStatusPemain(String idPemain, String statusBaru, String dibeliOleh) {
        if (connection == null) {
            System.err.println("❌ Koneksi database belum tersedia. Update dibatalkan.");
            return false;
        }
        
        String sql = "UPDATE pemain SET status = ?, dikontrak_oleh = ? WHERE id_pemain = ?"; 
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            // Isi placeholder-nya
            stmt.setString(1, statusBaru);     // Placeholder pertama: status baru
            stmt.setString(2, dibeliOleh);   // Placeholder kedua: klub baru
            stmt.setString(3, idPemain);       // Placeholder ketiga: id pemain
            
            // executeUpdate() untuk query UPDATE
            int rowsAffected = stmt.executeUpdate(); 
            
            // Jika sukses, return true
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error saat update status pemain: " + e.getMessage());
            return false;
        }
    }

    // Method buat nutup koneksi database
    public void closeConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                System.out.println("✓ Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error saat menutup koneksi: " + e.getMessage());
        }
    }
}