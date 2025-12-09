/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Main;

import java.util.List;
import java.util.Scanner;

public class Proyekakhir {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AuthService auth = null;
        Warehouse warehouse = null;
        RiwayatService riwayatService = null;

        try {
            auth = new AuthService();
            warehouse = new Warehouse();
            riwayatService = new RiwayatService(); // service riwayat transaksi

            System.out.println("=== APLIKASI INVENTORY GUDANG ===");

            boolean appRunning = true;
            User loggedIn = null;

            // LOGIN 3X GAGAL
            int attempts = 0;
            while (loggedIn == null && attempts < 3) {
                System.out.print("Username: ");
                String u = sc.nextLine();
                System.out.print("Password: ");
                String p = sc.nextLine();

                try {
                    loggedIn = auth.login(u, p);
                    if (loggedIn == null) {
                        attempts++;
                        System.out.println("Login gagal! Sisa percobaan: " + (3 - attempts));
                    } else {
                        System.out.println("Selamat datang, " + loggedIn.getNamaLengkap());
                    }
                } catch (InventoryException e) {
                    System.out.println("Error login: " + e.getMessage());
                    return;
                }
            }

            if (loggedIn == null) {
                System.out.println("3 kali salah. Aplikasi ditutup.");
                return;
            }

            // ============ MENU UTAMA ================
            while (appRunning) {
                System.out.println("\n=== MENU GUDANG UTAMA ===");
                System.out.println("1. Manajemen Barang");
                System.out.println("2. Riwayat Stok");
                System.out.println("3. Pencarian Barang");
                System.out.println("4. Manajemen Admin");
                System.out.println("5. Logout");
                System.out.println("6. Keluar Aplikasi");
                System.out.print("Pilih (1-6): ");
                String pilihan = sc.nextLine();

                switch (pilihan) {
                    case "1":
                        manajemenBarangMenu(sc, warehouse, riwayatService);
                        break;
                    case "2":
                        riwayatStokMenu(sc, riwayatService);
                        break;
                    case "3":
                        pencarianBarangMenu(sc, warehouse);
                        break;
                    case "4":
                        manajemenAdminMenu(sc, auth);
                        break;
                    case "5":
                        System.out.println("Logout berhasil.");
                        return;
                    case "6":
                        System.out.println("Keluar aplikasi. Sampai jumpa.");
                        appRunning = false;
                        break;
                    default:
                        System.out.println("Pilihan tidak valid.");
                }
            }

        } catch (InventoryException e) {
            System.out.println("Inisialisasi gagal: " + e.getMessage());
        } finally {
            if (auth != null) auth.closeConnection();
            if (warehouse != null) warehouse.closeConnection();
            if (riwayatService != null) riwayatService.closeConnection();
            sc.close();
        }
    }

    // ---------- MANAGEMEN BARANG ----------
    private static void manajemenBarangMenu(Scanner sc, Warehouse wh, RiwayatService rw) {
        boolean loop = true;
        while (loop) {
            System.out.println("\n-- Manajemen Barang --");
            System.out.println("1. Tambah barang");
            System.out.println("2. Update barang");
            System.out.println("3. Hapus barang");
            System.out.println("4. Tampilkan semua barang");
            System.out.println("0. Kembali");
            System.out.print("Pilih: ");
            String c = sc.nextLine();
            try {
                switch (c) {
                    case "1":
                        Item newItem = new Item();
                        System.out.print("Kode (unik): "); newItem.setKode(sc.nextLine());
                        System.out.print("Nama: "); newItem.setNama(sc.nextLine());
                        System.out.print("Kategori: "); newItem.setKategori(sc.nextLine());
                        System.out.print("Stok awal: "); 
                        newItem.setStok(Integer.parseInt(sc.nextLine()));
                        System.out.print("Lokasi: "); newItem.setLokasi(sc.nextLine());

                        wh.create(newItem);

                        // simpan riwayat
                        rw.tambahRiwayat(newItem.getKode(), "MASUK", newItem.getStok(), "Input awal");
                        System.out.println("Barang berhasil ditambahkan.");
                        break;

                    case "2":
                        System.out.print("Kode barang: ");
                        String kodeUp = sc.nextLine();
                        Item itUp = wh.findById(kodeUp);
                        if (itUp == null) { System.out.println("Barang tidak ditemukan."); break; }

                        int stokLama = itUp.getStok();

                        System.out.print("Nama ("+itUp.getNama()+"): ");
                        String nn = sc.nextLine(); if (!nn.isBlank()) itUp.setNama(nn);

                        System.out.print("Kategori ("+itUp.getKategori()+"): ");
                        String kk = sc.nextLine(); if (!kk.isBlank()) itUp.setKategori(kk);

                        System.out.print("Stok baru ("+itUp.getStok()+"): ");
                        String ss = sc.nextLine(); 
                        if (!ss.isBlank()) itUp.setStok(Integer.parseInt(ss));

                        System.out.print("Lokasi ("+itUp.getLokasi()+"): ");
                        String ll = sc.nextLine(); if (!ll.isBlank()) itUp.setLokasi(ll);

                        boolean up = wh.update(itUp);

                        // simpan riwayat perubahan stok
                        int selisih = itUp.getStok() - stokLama;
                        if (selisih != 0) {
                            String jenis = selisih > 0 ? "MASUK" : "KELUAR";
                            rw.tambahRiwayat(kodeUp, jenis, Math.abs(selisih), "Perubahan stok");
                        }

                        System.out.println(up ? "Update berhasil." : "Update gagal.");
                        break;

                    case "3":
                        System.out.print("Kode barang yang akan dihapus: ");
                        String kodeDel = sc.nextLine();
                        boolean del = wh.delete(kodeDel);
                        System.out.println(del ? "Barang dihapus." : "Barang tidak ditemukan.");
                        break;

                    case "4":
                        List<Item> all = wh.findAll();
                        System.out.println("\nDAFTAR BARANG:");
                        System.out.printf("%-10s %-25s %-15s %-6s %-15s%n",
                                "KODE", "NAMA", "KATEGORI", "STOK", "LOKASI");
                        for (Item it : all) {
                            System.out.printf("%-10s %-25s %-15s %-6d %-15s%n",
                                it.getKode(), it.getNama(), it.getKategori(),
                                it.getStok(), it.getLokasi());
                        }
                        break;

                    case "0":
                        loop = false;
                        break;

                    default:
                        System.out.println("Pilihan tidak valid.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ---------- MENU RIWAYAT STOK ----------
    private static void riwayatStokMenu(Scanner sc, RiwayatService rw) {
        boolean loop = true;

        while (loop) {
            System.out.println("\n-- RIWAYAT STOK --");
            System.out.println("1. Tampilkan semua riwayat");
            System.out.println("2. Tampilkan riwayat barang tertentu");
            System.out.println("0. Kembali");
            System.out.print("Pilih: ");
            String c = sc.nextLine();

            try {
                switch (c) {
                    case "1":
                        rw.tampilkanSemuaRiwayat();
                        break;

                    case "2":
                        System.out.print("Masukkan kode barang: ");
                        String kode = sc.nextLine();
                        rw.tampilkanRiwayatBarang(kode);
                        break;

                    case "0":
                        loop = false;
                        break;

                    default:
                        System.out.println("Pilihan tidak valid.");
                }

            } catch (InventoryException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // ---------- PENCARIAN BARANG ----------
    private static void pencarianBarangMenu(Scanner sc, Warehouse wh) throws InventoryException {
        System.out.print("Masukkan keyword (nama/kategori): ");
        String kw = sc.nextLine();
        List<Item> found = wh.search(kw);
        System.out.printf("%-10s %-25s %-15s %-6s %-15s%n", 
                "KODE", "NAMA", "KATEGORI", "STOK", "LOKASI");

        for (Item it : found) {
            System.out.printf("%-10s %-25s %-15s %-6d %-15s%n",
                    it.getKode(), it.getNama(), it.getKategori(), it.getStok(), it.getLokasi());
        }

        if (found.isEmpty()) System.out.println("Tidak ada hasil.");
    }

    // ---------- MANAGEMEN ADMIN ----------
    private static void manajemenAdminMenu(Scanner sc, AuthService auth) {
        boolean loop = true;
        while (loop) {
            System.out.println("\n-- Manajemen Admin --");
            System.out.println("1. Tampilkan semua admin");
            System.out.println("2. Tambah admin");
            System.out.println("3. Hapus admin");
            System.out.println("0. Kembali");
            System.out.print("Pilih: ");
            String c = sc.nextLine();
            try {
                switch (c) {
                    case "1":
                        auth.tampilSemuaAdmin();
                        break;

                    case "2":
                        User u = new User();
                        System.out.print("Username: "); u.setUsername(sc.nextLine());
                        System.out.print("Password: "); u.setPassword(sc.nextLine());
                        System.out.print("Nama lengkap: "); u.setNamaLengkap(sc.nextLine());
                        auth.register(u);
                        break;

                    case "3":
                        System.out.print("Username admin yang akan dihapus: ");
                        String uname = sc.nextLine();
                        auth.hapusAdmin(uname);
                        break;

                    case "0":
                        loop = false;
                        break;

                    default:
                        System.out.println("Pilihan tidak valid.");
                }
            } catch (InventoryException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
