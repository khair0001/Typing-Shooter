# Typing-Shooter
## Kelompok 20 Project PBO
---
### Anggota Kelompok
- AHMAD MUSLIHUL KHAIR (F1D02310001)
- ZAHRATU SYITA (F1D02310148)
- MUHAMMAD RIDHO AIDIL FURQON (F1D02310127)
### Pendamping
- AGNA
---
## Deskripsi Project
**Typing Shooter** adalah game untuk mempertahankan garis depan dengan kecepatan dan akurasi mengetik. Setiap musuh yang menyerang dari kanan memiliki kata target yang harus diketik, huruf pertama akan mengunci target, dan setiap huruf yang benar berikutnya akan menembakkan proyektil hingga seluruh kata selesai dan musuh hancur. Kegagalan mengetik tepat waktu memungkinkan musuh mencapai sisi kiri, mengurangi HP pemain.

## Struktur Project

```
TypingCommando/
├── assets/
│   ├── audio/
│   │   └── music/          # Musik dan efek suara permainan
│   ├── images/
│   │   ├── backgrounds/    # Gambar latar belakang
│   │   ├── characters/     # Sprite karakter pemain
│   │   ├── effects/        # Efek visual
│   │   ├── enemies/        # Sprite musuh
│   │   └── ui/             # Elemen antarmuka pengguna
│   └── soal/               # File teks berisi tantangan mengetik
├── bin/                    # File .class hasil kompilasi
├── lib/                    # Library eksternal
└── src/
    └── main/               # Kode sumber
        ├── AssetPath.java  # Path ke aset game
        ├── AudioManager.java # Mengelola audio game
        ├── BasePanel.java  # Panel dasar untuk layar game
        ├── Bullet.java     # Logika peluru
        ├── DatabaseManager.java # Mengelola penyimpanan data
        ├── Enemy.java      # Perilaku dan properti musuh
        ├── GamePanel.java  # Panel utama permainan
        ├── InputUsernamePanel.java # Input nama pemain
        ├── LeaderboardPanel.java   # Peringkat skor tertinggi
        ├── MainApp.java    # Titik masuk aplikasi
        ├── Player.java     # Logika karakter pemain
        ├── gameOver.java   # Layar game over
        ├── halamanAwal.java # Layar awal permainan
        └── menuAwal.java   # Menu utama
```

## Cara Menjalankan

1. Pastikan Java Development Kit (JDK) sudah terinstall
2. Buka terminal/command prompt di direktori project
3. buat folder bin dengan 
  ``` bash
  mkdir bin
  ```
4. Kompilasi kode sumber:
   ```bash
   javac -d bin -cp "lib/*" src/main/*.java
   ```
5. Jalankan game:
   ```bash
   java -cp "bin;lib/*" MainApp
   ```

**Catatan:** Pastikan MySQL Server berjalan dan database sudah disiapkan sesuai konfigurasi di `DatabaseManager.java`

## Fitur

- Permainan berbasis mengetik
- Berbagai tipe musuh
- Pencatatan skor
- Sistem peringkat
- Umpan balik audio dan visual
- Antarmuka yang responsif
