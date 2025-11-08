/**
 * Class Pemain - Merepresentasikan Shared Resource (data yang diperebutkan)
 * * Mirip dengan AkunBank.java, class ini berisi method 'synchronized'
 * untuk memastikan hanya satu thread yang bisa memodifikasi status pemain
 * dalam satu waktu.
 */
public class Pemain {
    // Pakai 'final' untuk data yang tidak akan berubah
    private final String idPemain;
    private final String namaPemain;
    private String status; // Ini bisa berubah
    private String dikontrakOleh; // Ini juga bisa berubah

    // Referensi ke DatabaseManager untuk update DB
    private final DatabaseManager dbManager;

    public Pemain(String idPemain, String namaPemain, String status, String dikontrakOleh, DatabaseManager dbManager) {
        this.idPemain = idPemain;
        this.namaPemain = namaPemain;
        this.status = status;
        this.dikontrakOleh = dikontrakOleh;
        this.dbManager = dbManager;
    }

    // --- Getters ---
    public String getNamaPemain() { return namaPemain; }
    public String getStatus() { return status; }
    public String getDikontrakOleh() { return dikontrakOleh; }


    /**
     * Method untuk merekrut pemain.
     * * Keyword 'synchronized' adalah KUNCI-nya. [cite: 4085-4086, 4091]
     * Ini memastikan hanya SATU thread (misal "PSG") yang bisa masuk
     * dan menjalankan kode di dalam method ini dalam satu waktu.
     * * Thread lain ("Man City") harus NUNGGU di luar sampai thread "PSG" selesai.
     */
    public synchronized boolean rekrutPemain(String namaKlub, MainApp gui) {
        // Dapatkan nama thread yang sedang berjalan
        String threadName = Thread.currentThread().getName();
        
        gui.log("[" + threadName + "] Mencoba merekrut " + namaPemain + "...");
        gui.log("  Status saat ini: " + this.status);

        // 1. Cek logika (status pemain)
        if (this.status.equals("Tersedia")) {
            try {
                // Simulasi proses negosiasi yang butuh waktu
                // Ini adalah momen krusial di mana race condition bisa terjadi
                // jika tidak ada 'synchronized'.
                gui.log("  [" + threadName + "] Sedang negosiasi... (1 detik)");
                Thread.sleep(1000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Set flag interrupt
                gui.log("  [" + threadName + "] Negosiasi terganggu!");
                return false;
            }

            // 2. Update status di memory (Objek Java)
            this.status = "Dikontrak";
            this.dikontrakOleh = namaKlub;

            // 3. Update status ke Database
            boolean updateBerhasil = dbManager.updateStatusPemain(this.idPemain, this.status, this.dikontrakOleh);

            if (updateBerhasil) {
                // Berhasil!
                gui.log("-> SUKSES: [" + threadName + "] berhasil merekrut " + namaPemain + "!");
                // Update label di GUI
                gui.updateStatusLabel(this.status + " oleh " + this.dikontrakOleh);
                return true;
            } else {
                // Gagal update DB (seharusnya tidak terjadi, tapi untuk jaga-jaga)
                gui.log("-> GAGAL: [" + threadName + "] Gagal update database!");
                // Rollback status di memory
                this.status = "Tersedia";
                this.dikontrakOleh = "Barcelona";
                return false;
            }
        } else {
            // Gagal karena pemain sudah direkrut thread lain
            gui.log("-> GAGAL: [" + threadName + "] terlambat, " + namaPemain 
                + " sudah " + this.status + " oleh " + this.dikontrakOleh);
            return false;
        }
    }

    /**
     * Method synchronized untuk RESET status (juga harus thread-safe)
     */
    public synchronized void resetStatus(MainApp gui) {
        gui.log("Mereset status " + namaPemain + " ke database...");
        
        this.status = "Tersedia";
        this.dikontrakOleh = "Barcelona";
        
        boolean updateBerhasil = dbManager.updateStatusPemain(this.idPemain, this.status, this.dikontrakOleh);
        
        if (updateBerhasil) {
            gui.log("-> SUKSES: " + namaPemain + " sekarang kembali 'Tersedia'.");
            gui.updateStatusLabel(this.status + " oleh " + this.dikontrakOleh);
        } else {
            gui.log("-> GAGAL: Gagal mereset database.");
        }
    }
}