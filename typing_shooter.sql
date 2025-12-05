-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 05 Des 2025 pada 15.33
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `typing_shooter`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `scores`
--

CREATE TABLE `scores` (
  `score_id` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `score` int(11) NOT NULL DEFAULT 0,
  `timestamp` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `scores`
--

INSERT INTO `scores` (`score_id`, `username`, `score`, `timestamp`) VALUES
(3, 'TypeMaster', 1200, '2024-01-15 13:20:00'),
(5, 'QuickFingers', 950, '2024-01-15 15:30:00'),
(6, 'khair', 30, '2025-11-27 17:25:14'),
(11, 'bayu', 810, '2025-12-01 15:44:59'),
(13, 'hallo', 20, '2025-12-05 19:21:41');

-- --------------------------------------------------------

--
-- Struktur dari tabel `soal`
--

CREATE TABLE `soal` (
  `soal_id` int(11) NOT NULL,
  `data` text NOT NULL,
  `kesulitan` enum('mudah','sedang','sulit') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `soal`
--

INSERT INTO `soal` (`soal_id`, `data`, `kesulitan`) VALUES
(1, 'hallo, test, pohon, buku, meja, kursi, motor, mobil, kopi, teh, awan, hujan, baju, celana, topi, tas, kaca, warna, bola, lampu, pintu, jendela, batu, tanah, rumah, kamar, dapur, taman, jalan, air, susu, roti, nasi, ayam, ikan, kuda, kucing, anjing, burung, pisang, apel, jeruk, mangga, anggur, garam, gula, bambu, kabel, tali, kertas, tinta, pena, pulpen, map, ember, sapu, kipas, jam, angka, nilai, kata, huruf, koin, uang, kado, pandai, bodoh, baik, jahat, panas, dingin, dekat, jauh, atas, bawah, kiri, kanan, toko, kantin, lorong, waktu, tumit, siku, gigi, lidah, rasa, bunyi, suara, dinding, lantai, atap, sinar', 'mudah'),
(2, 'komputer, laptop, monitor, keyboard, browser, program, jaringan, sinyal, database, algoritma, operator, perangkat, aplikasi, terminal, kompilasi, struktur, framework, grafik, statistik, keamanan, analisis, sistematik, arsitektur, metadata, dokumen, pengguna, teknologi, komunikasi, bandwidth, router, switch, prosesor, memori, penyimpanan, protokol, koneksi, password, skripsi, kampus, jurusan, materi, modul, praktikum, penelitian, laporan, konsep, metode, parameter, fungsi, variabel, deskripsi, narasi, kualitas, akselerasi, kalkulasi, kombinasi, komparasi, observasi, validasi, transmisi, kondisi, perubahan, estimasi, distribusi, koleksi, arsip, transaksi, pengelola, notifikasi, pendaftaran, registrasi, verifikasi, pembayaran, aktivitas, pengendalian, sinkronisasi, representasi, kompresi, ekspansi, integrasi, kompatibel, optimasi, perhitungan, penjadwalan, keterangan, transformasi, pengembangan, pelatihan, pengorganisasian, evaluasi, penerjemahan, pengiriman', 'sedang'),
(3, 'implementasi, konfigurasi, deklaratif, eksperimental, komputasional, otentikasi, enumeration, sinkronisasi, desentralisasi, konsistensi, prosedural, dokumentasi, simultaneous, integritas, akumulasi, rekonsiliasi, kompleksitas, demonstratif, kontekstual, kalkulatif, representatif, akselerometer, telemetri, rekonstruksi, kolaboratif, transformatif, interoperabilitas, komunikabilitas, pengoptimalan, manipulatif, sensitivitas, kompresibilitas, resolusioner, generatif, prediktif, delegatif, multidimensi, akseleratif, regeneratif, responsivitas, konektivitas, interaktifitas, korelasi, validitas, kapasitas, modularitas, stabilisasi, distribusi, konsolidasi, akselerasi, penetrasi, elektronika, fotometrik, biometrik, neuronetwork, kognitif, litigasi, implementor, simulasi, konseptualisasi, proyeksi, modalitas, autentikasi, autorisasi, kompresional, permutasi, kombinatorik, fluktuatif, kontruktif, antisipatif, intervensi, rekayasa, persepsi, kolaborasi, konsentrasi, komparatif, komposisional, derivatif, komplemen, transformatika, diagonalisasi, unifikasi, elektromagnetik, instrumentasi, pertimbangan, analitik, spekulatif, koordinatif, pemodelan, rekayasawan, eksploratif, proyeksional, persepsional, antisipatori, evaluatif, deduktif, fragmentasi, disosiasi', 'sulit');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `scores`
--
ALTER TABLE `scores`
  ADD PRIMARY KEY (`score_id`),
  ADD KEY `idx_score` (`score`),
  ADD KEY `idx_timestamp` (`timestamp`);

--
-- Indeks untuk tabel `soal`
--
ALTER TABLE `soal`
  ADD PRIMARY KEY (`soal_id`),
  ADD KEY `idx_kesulitan` (`kesulitan`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `scores`
--
ALTER TABLE `scores`
  MODIFY `score_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT untuk tabel `soal`
--
ALTER TABLE `soal`
  MODIFY `soal_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
