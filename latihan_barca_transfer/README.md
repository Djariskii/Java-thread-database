| | |
|---|---|
| **Nama** | Zarizky Meidyansyah |
| **NIM** | F1D02310099 |

# Simulasi Bursa Transfer dengan Thread dan JDBC

Tugas ini adalah implementasi gabungan GUI, Thread, dan Database (JDBC) dalam Java.

## deskripsi

Program ini mensimulasikan "race condition" di bursa transfer. Dua klub (dijalankan di **Thread** terpisah) mencoba merekrut pemain yang sama (**Shared Resource**) dari database secara bersamaan.

- **GUI (Swing):** Menyediakan antarmuka untuk memulai simulasi dan melihat log *real-time*.
- **Runnable:** Setiap "tugas transfer" oleh klub dibungkus dalam *class* yang `implements Runnable`.
- **Thread:** Setiap tugas dijalankan di *thread* terpisah agar tidak membuat GUI macet/freeze.
- **JDBC:** Status pemain (seperti `status` dan `dikontrak_oleh`) dibaca dan ditulis ke database MySQL.
- **Synchronized:** *Method* `rekrutPemain()` pada *class* `Pemain` di-`synchronized` untuk mencegah *race condition*, memastikan hanya satu klub yang berhasil.

## Skenario

1.  GUI menampilkan status pemain "Lamine Yamal" (ID: LY27) yang diambil dari database. Status awal: "Tersedia".
2.  Pengguna mengklik tombol "Rekrut oleh PSG" dan "Rekrut oleh Man City" secara hampir bersamaan.
3.  Ini akan memulai dua *thread* (Thread-PSG dan Thread-City) yang berlomba memanggil *method* `pemain.rekrutPemain(...)`.
4.  Berkat `synchronized`, hanya satu *thread* yang akan berhasil mengubah status pemain.
5.  *Thread* yang kalah akan gagal karena status pemain sudah bukan "Tersedia" lagi.
6.  GUI akan menampilkan log proses dan hasil akhir status pemain, yang juga tersimpan di database.

## Persiapan Database

### 1. Jalankan XAMPP

Pastikan MySQL di XAMPP sudah berjalan.

### 2. Buat Database dan Tabel

## Buka phpMyAdmin atau MySQL command line, lalu jalankan query berikut:

```sql
-- 1. Buat database baru (jika belum ada)
CREATE DATABASE db_barca;

-- 2. Gunakan database
USE db_barca;

-- 3. Buat tabel pemain
CREATE TABLE pemain (
    id_pemain VARCHAR(10) PRIMARY KEY,
    nama_pemain VARCHAR(100),
    status VARCHAR(50),
    dikontrak_oleh VARCHAR(100)
);

-- 4. Insert data Lamine Yamal
INSERT INTO pemain (id_pemain, nama_pemain, status, dikontrak_oleh)
VALUES ('LY27', 'Lamine Yamal', 'Tersedia', 'Barcelona');

-- 5. (Opsional) Cek datanya
SELECT * FROM pemain;