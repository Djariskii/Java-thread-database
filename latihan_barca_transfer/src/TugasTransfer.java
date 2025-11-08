/**
 * Class TugasTransfer - Merepresentasikan "Tugas" yang dijalankan oleh Thread.
 * * Class ini mengimplementasikan interface Runnable, 
 * yang mengharuskan kita membuat method run().
 * * Saat thread di-start(), method run() ini akan dieksekusi.
 */
public class TugasTransfer implements Runnable {
    
    // Variabel ini di-set saat tugas dibuat
    private final Pemain pemain;       // Resource yang akan diakses
    private final String namaKlub;     // Nama klub yang mencoba merekrut
    private final MainApp gui;         // Referensi ke GUI untuk logging

    public TugasTransfer(Pemain pemain, String namaKlub, MainApp gui) {
        this.pemain = pemain;
        this.namaKlub = namaKlub;
        this.gui = gui;
    }

    /**
     * Method run() ini adalah "jantung" dari thread.
     * Kode di sinilah yang akan dieksekusi di jalur terpisah.
     */
    @Override
    public void run() { 
        // Memberi nama pada Thread agar mudah dilacak di log
        Thread.currentThread().setName(namaKlub);
        
        // Memanggil method 'synchronized' pada objek 'pemain'
        // Di sinilah "antrian" terjadi jika 2 thread mengakses
        // objek 'pemain' yang sama secara bersamaan.
        pemain.rekrutPemain(namaKlub, gui);
    }
}