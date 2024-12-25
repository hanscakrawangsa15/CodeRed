import java.util.*;
import javax.sound.sampled.*;
import java.io.File;

public class Main {
    private static InvestigationGraph investigationGraph;
    private static TimelineSearch timelineSearch;
    private static GraphMap graphMap;
    private static Map<String, Location> locations;
    private static GameState gameState;
    private static Scanner scanner;

    public static void main(String[] args) {
        initializeGame();
        playGame();
    }

    private static void initializeGame() {
        investigationGraph = new InvestigationGraph();
        timelineSearch = new TimelineSearch();
        graphMap = new GraphMap();
        locations = new HashMap<>();
        gameState = new GameState();
        scanner = new Scanner(System.in);
        initializeInvestigationGraph();
        initializeGraph();
        initializeLocations();

        Sound.playSound("welcome.wav", () -> {
            new Thread(() -> Sound.playSound("ready.wav")).start();
        });
        System.out.println("=== CODE RED: Murder Mystery ===");
        System.out.println("Code Red adalah permainan riddle interaktif yang akan membawa kamu ke dalam misteri menegangkan di kampus ITS\n"+
        "Kamu akan dibawa masuk ke dunia penuh misteri, di mana tugas utamamu adalah mengungkap siapa dalang di balik pembunuhan tragis.\n" +
        "Siapkah kamu memulai petualangan ini?");

        while (true) {
            try {
                System.out.print("\nKetik 'siap' untuk memulai game: ");
                String input = scanner.nextLine().trim().toLowerCase();
                if (!input.equals("siap")) {
                    Sound.stopSound();
                    throw new GameInputException("Sayang sekali kamu bukan orang yang tepat. Kamu harus ketik 'siap' untuk memulai game.");
                }
                break;
            } catch (GameInputException e) {
                System.out.println(e.getMessage());
                continue;
            }
        }
        Sound.stopSound();
        System.out.println("\nGame dimulai! Selamat menyelidiki kasus ini.");
        displayIntroduction();
    }
    private static void initializeInvestigationGraph() {
        // Inisialisasi lokasi dengan bukti dan skor keandalan
        investigationGraph.addLocation("Sistem Informasi", "Tali dan bercak darah", 9);
        investigationGraph.addLocation("Dr. Angka", "Rekaman CCTV", 8);
        investigationGraph.addLocation("Kedokteran", "Sarung tangan Latex", 7);
        investigationGraph.addLocation("Research Center", "Dokumen Penelitian Terlarang", 9);
        investigationGraph.addLocation("Perpustakaan", "Sobekan buku", 6);
        investigationGraph.addLocation("Teknik Kelautan", "Pipa air", 5);
        investigationGraph.addLocation("Teknik Geomatika", "Saluran Air Bocor", 6);
        investigationGraph.addLocation("Teknik Informatika", "Ketapel", 7);

        // Menghubungkan lokasi berdasarkan tata letak kampus
        investigationGraph.connectLocations("Sistem Informasi", "Dr. Angka");
        investigationGraph.connectLocations("Sistem Informasi", "Kedokteran");
        investigationGraph.connectLocations("Sistem Informasi", "Research Center");
        investigationGraph.connectLocations("Sistem Informasi", "Perpustakaan");
        investigationGraph.connectLocations("Kedokteran", "Teknik Kelautan");
        investigationGraph.connectLocations("Kedokteran", "Teknik Geomatika");
        investigationGraph.connectLocations("Teknik Geomatika", "Teknik Informatika");
        investigationGraph.connectLocations("Teknik Kelautan", "Teknik Informatika");
        investigationGraph.connectLocations("Dr. Angka", "Perpustakaan");
        investigationGraph.connectLocations("Perpustakaan", "Research Center");
        investigationGraph.connectLocations("Research Center", "Teknik Informatika");

        // Inisialisasi rumor awal di lokasi-lokasi utama
        investigationGraph.propagateRumors("Sistem Informasi", "Joko terlihat panik sebelum kejadian", 3);
        investigationGraph.propagateRumors("Research Center", "Ada dokumen penelitian yang hilang", 4);
        investigationGraph.propagateRumors("Dr. Angka", "Terdengar suara pertengkaran malam itu", 2);
    }

    private static void initializeGraph() {
        // Menambahkan lokasi berdasarkan hubungan
        graphMap.addEdge("Sistem Informasi", "Dr. Angka");
        graphMap.addEdge("Sistem Informasi", "Kedokteran");
        graphMap.addEdge("Sistem Informasi", "Research Center");
        graphMap.addEdge("Sistem Informasi", "Perpustakaan");
        graphMap.addEdge("Kedokteran", "Teknik Kelautan");
        graphMap.addEdge("Kedokteran", "Teknik Geomatika");
        graphMap.addEdge("Teknik Geomatika", "Teknik Informatika");
        graphMap.addEdge("Teknik Kelautan", "Teknik Informatika");
        graphMap.addEdge("Dr. Angka", "Perpustakaan");
        graphMap.addEdge("Perpustakaan", "Research Center");
        graphMap.addEdge("Research Center", "Teknik Informatika");
    }

    private static void displayIntroduction() {
        // menambahkan cerita
        System.out.println("\n" +//
        "Pada suatu hari di Departemen Sistem Informasi, sebuah tragedi tak terduga terjadi.\n" + //
        "Joko, seorang mahasiswa Sistem Informasi ditemukan tewas di gudang departemen dalam \n" + //
        "keadaan mengenaskan dimana tubuh korban tergantung dengan kepala yang terikat tali.");
        System.out.println("\n"+//
        "Joko bukan mahasiswa biasa. Dia adalah seorang mahasiswa yang sangat pintar, ambisius, dan terkenal baik\n" + //
        "oleh teman-temannya. Karena kepintarannya, Joko menjadi orang yang terlibat dalam sebuah penelitian \n" +//
        "rahasia yang diindikasi penelitian terlarang. Sebelum kejadian, Joko dilanda kegelisahan karena dokumen penelitian \n" +//
        "yang seharusnya ia bawa hilang secara misterius. Ia juga menerima sebuah panggilan misterius. \n"+//
        "Banyak orang yang memiliki keterkaitan dengan Joko, namun sebuah surat petunjuk ada didekatnya\n"+//
        "'Hanya aku sang pemenang' \n"+//
        "Apa yang sebenarnya terjadi malam itu? Apakah ini kecelakaan, bunuh diri, atau sebuah pembunuhan yang direncanakan?");
        System.out.print("\n"+//
        "Sebagai seorang detektif, kamu ditugaskan untuk mengungkap apa yang sebenarnya terjadi. \n"+//
        "Setiap lokasi di kampus ini menyimpan petunjuk dan banyaknya pihak yang terlibat sebelum kematian Joko.\n"+//
        "Tugasmu adalah menghubungkan potongan-potongan ini, menyusun fakta dari tiap lokasi dan kesaksian, \n" + //
        "dan menemukan dalang di balik tragedi ini.");
        Sound.playSoundLoop("intro3.wav");

    }

    private static void playGame() {
        // memulai game
        while (!gameState.isGameOver()) {
            System.out.println();
            displayCurrentLocation();
            displayMenu();
            processChoice();
        }
        scanner.close();
    }

    private static void displayCurrentLocation() {
        //mendapatkan lokasi saat ini
        System.out.println("\nLokasi saat ini: " + gameState.getCurrentLocation());
        if (!gameState.hasVisitedLocation(gameState.getCurrentLocation())) {
            System.out.println("Ini adalah lokasi yang sedang kamu kunjungi.");
        }
    }

    private static void displayMenu() {
        //menampilkan pilihan
        System.out.println("\nApa yang ingin kamu lakukan?");
        System.out.println("1. Introgasi saksi");
        System.out.println("2. Lihat barang bukti");
        System.out.println("3. Review clue");
        System.out.println("4. Pindah ke tempat lain");
        System.out.println("5. Analisis Rumor (BFS)");
        System.out.println("6. Cari Jalur Investigasi");
        System.out.println("7. Analisis Timeline");
        System.out.println("8. Tebak pembunuh");
        System.out.println("9. Exit game");
    }

    private static void processChoice() {
        //pilihan yang perlu dimasukkan pemain
        int choice = getValidChoice(1, 9);
        switch (choice) {
            case 1:
                interrogateWitness();
                break;
            case 2:
                examineEvidence();
                break;
            case 3:
                reviewClues();
                break;
            case 4:
                moveLocation();
                break;
                case 5:
                analyzeRumors();
                break;
            case 6:
                findInvestigationPath();
                break;
            case 7:
                analyzeTimeline();
                break;
            case 8:
                makeAccusation();
                break;
            case 9:
                exitGame();
                break;
        }
    }

    private static int getValidChoice(int min, int max) {
        //mendapatkan pilihan pemain
        int choice;
        while (true) {
            System.out.println("Mau pilih nomor berapa, nih? (" + min + "-" + max + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Masukkan angka antara " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Masukkan angka yang valid");
            }
        }
    }

    private static void interrogateWitness() {
        //menampilkan pilihan introgasi saksi
        Location currentLocation = locations.get(gameState.getCurrentLocation());
        if (currentLocation.getWitness() != null) {
            Witness witness = currentLocation.getWitness();
            System.out.println("\n=== Introgasi Saksi === " + witness.getName() + " ===");
            System.out.println("Deskripsi karakter: " + witness.getCharacterDesc());
            System.out.println("Pernyataan: " + witness.getStatement());
            gameState.addClue(witness.getStatement(), witness.getSort());
            gameState.visitLocation(gameState.getCurrentLocation());
        } else {
            System.out.println("Tidak ada saksi di lokasi ini.");
        }
    }

    private static void examineEvidence() {
        //menampilkan pilihan menampilkan bukti
        Location currentLocation = locations.get(gameState.getCurrentLocation());
        System.out.println("\n=== Lihat Barang Bukti ===");
        System.out.println("Barang bukti: " + currentLocation.getEvidence());
    }

    private static void reviewClues() {
        //menampilkan pilihan melihat clue yang ada
        List<Sorting.ClueData> clues = gameState.getCollectedClues();
        if (clues.isEmpty()) {
            System.out.println("\nTidak ada clue yang dikumpulkan.");
            return;
        }

        // Menyortir dan mencetak petunjuk secara langsung karena sudah dalam format ClueData
        Sorting.printSortedClues(clues);
    }

    private static void moveLocation() {
        //menampilkan pilihan pindah lokasi
        List<String> availableLocations = graphMap.getNeighbors(gameState.getCurrentLocation());
        System.out.println("\n=== Lokasi Selanjutnya ===");
        for (int i = 0; i < availableLocations.size(); i++) {
            System.out.println((i + 1) + ". " + availableLocations.get(i));
        }

        System.out.print("\nPilih lokasi (1-" + availableLocations.size() + "): ");
        int choice = getValidChoice(1, availableLocations.size());
        gameState.setCurrentLocation(availableLocations.get(choice - 1));
    }

    private static void analyzeRumors() {
        //menampilkan pilihan melihat saksi
        System.out.println("\n=== Analisis Rumor di Lokasi Ini ===");
        Set<String> rumors = investigationGraph.getRumorsAtLocation(gameState.getCurrentLocation());
        if (rumors.isEmpty()) {
            System.out.println("Tidak ada rumor yang beredar di lokasi ini.");
            return;
        }

        System.out.println("Rumor yang beredar:");
        for (String rumor : rumors) {
            int reliability = investigationGraph.getReliabilityScore(gameState.getCurrentLocation());
            System.out.println("- " + rumor);
            System.out.println("  Tingkat keandalan: " + reliability + "/10");
        }
        // Menampilkan bukti yang terhubung dalam 2 langkah
        Map<String, Integer> nearbyEvidence = investigationGraph.findConnectedEvidence(gameState.getCurrentLocation(), 2);
        if (!nearbyEvidence.isEmpty()) {
            System.out.println("\nBarang bukti terkait dalam radius dekat:");
            for (Map.Entry<String, Integer> evidence : nearbyEvidence.entrySet()) {
                System.out.println("- " + evidence.getKey() + " (" + evidence.getValue() + " langkah)");
            }
        }
    }
    private static void findInvestigationPath() {
        //menampilkan pilihan lihat jalur lokasi
        System.out.println("\n=== Cari Jalur Investigasi ===");
        List<String> availableLocations = graphMap.getNeighbors(gameState.getCurrentLocation());
        System.out.println("Pilih lokasi tujuan:");
        for (int i = 0; i < availableLocations.size(); i++) {
            System.out.println((i + 1) + ". " + availableLocations.get(i));
        }

        int choice = getValidChoice(1, availableLocations.size());
        String targetLocation = availableLocations.get(choice - 1);
        List<String> path = investigationGraph.findInvestigationPath(gameState.getCurrentLocation(), targetLocation);
        if (path.isEmpty()) {
            System.out.println("Tidak dapat menemukan jalur ke lokasi tersebut.");
        } else {
            System.out.println("\nJalur investigasi teroptimal:");
            System.out.println(String.join(" -> ", path));
            System.out.println("\nRekomendasi investigasi:");
            for (String location : path) {
                System.out.println("- " + location + ": " +
                    (investigationGraph.isVisited(location) ? "Sudah diselidiki" : "Belum diselidiki"));
            }
        }
    }

    private static void makeAccusation() {
        //menampilkan pilihan tebak pembunuh
        Sound.stopSound();
        Sound.playSoundLoop("voting.wav");
        System.out.println("\n=== Tebak Pembunuh ===");
        System.out.println("WARNING: Apabila kamu salah menangkap, pembunuh akan terus berkeliaran!");
        System.out.println("\nTersangka:");

        Map<Integer, String> suspects = new LinkedHashMap<>();
        int index = 1;
        for (Location location : locations.values()) {
            Witness witness = location.getWitness();
            if (witness != null && !witness.getName().equals("Bu Ita") && !witness.getName().equals("Pak Budi")) {
                System.out.println(index + ". " + witness.getName() + " - " + witness.getCharacterDesc());
                suspects.put(index++, witness.getName());
            }
        }
        System.out.println("0. Batalkan");
        int choice = getValidChoice(0, suspects.size());

        if (choice == 0) {
            System.out.println("Pemilihan dibatalkan.");
            return;
        }
        // cek keyakinan pemain
        String accusedPerson = suspects.get(choice);
        System.out.println("\nPilihanmu adalah " + accusedPerson + ".");
        System.out.print("Apakah kamu yakin? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        //menampilkan kemenangan jika yang dijawab benar
        if (confirm.equals("y")) {
            if (accusedPerson.equals("Saka")) {
                Sound.stopSound();
                Sound.playSound("correct.wav");
                System.out.println("\n=== Case Solved! ===");
                System.out.println("Selamat! Kamu berhasil menangkap pembunuh.");
                System.out.println("\nRingkasan kejadian:");
                System.out.println("Saka membunuh Joko karena rasa iri dengan keberhasilan Joko, sehingga ada petunjuk tulisan 'Hanya aku sang pemenang'");
                System.out.println("\nPenjelasan lebih rinci");
                System.out.println("1. Argumen Saka untuk belajar bersama aneh karena Joko sedang mengalami kegelisahan");
                System.out.println("2. Pak Santoso bungkam karena takut penelitiannya terbongkar");
                System.out.println("3. Leo mencuri dokumen penelitian yang Joko bawa dan ingin mengancam untuk mengungkapkannya");
                System.out.println("4. Ari orang baik karena ingin membantu Joko menemukan dokumennya karena tahu Leo membawanya, Leo dan Ari sekelas");
                System.out.println("5. Fia mengkhawatirkan Joko dan ingin membantu, tetapi Joko menolak dan berpendapat bahwa permasalahan ini bukan masalah Fia");
                System.out.println("6. Bu Ita tidak sengaja berpapasan dengan Fia dan merupakan orang pertama yang menemukan Joko");
                System.out.println("7. Dua orang bertengkar yang terlihat di cctv adalah Ari dan Leo yang sedang meributkan dokumen penelitian");

                //delay setelah menang sebelum memulai game kembali
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //memulai game kembali
                System.out.println("\nPress Enter to return to main menu...");
                scanner.nextLine();

                gameState = new GameState();
                initializeGame();
                displayIntroduction();
                playGame();
            } else {
                //jika pemain menunjukkan pilihan yang salah
                Sound.stopSound();
                Sound.playSound("wrong.wav");
                Sound.playSoundLoop("intro3.wav");
                System.out.println("\nKamu menangkap orang yang salah");
                System.out.println("Investigasi masih terus dilakukan. Hati-hati, kamu bisa jadi orang selanjutnya.");
            }
        }
    }

    private static void exitGame() {
        //menampilkan pilihan keluar game
        System.out.println("\nThank you for playing Code Red!");
        gameState.setGameOver(true);
    }

    private static void analyzeTimeline() {
        //menampilkan pilihan analisis timeline kejadian
        while (true) {
            System.out.println("\n=== Analisis Timeline Kejadian ===");
            System.out.println("1. Cari kejadian berdasarkan waktu");
            System.out.println("2. Kembali ke menu utama");

            int choice = getValidChoice(1, 2);
            if (choice == 2) break;

            switch (choice) {
                case 1:
                    searchEventByTime();
                    break;
            }
        }
    }

    private static void searchEventByTime() {
        //menampilkan pilihan mencari waktu
        System.out.println("\nMasukkan waktu (format 24 jam, contoh 2200 untuk 22:00): ");
        int time = getValidTime();
        TimelineSearch.TimelineEvent event = timelineSearch.findEventByTime(time);
        if (event != null) {
            System.out.println("\nKejadian ditemukan:");
            System.out.println(event);
        } else {
            System.out.println("Tidak ada kejadian yang tercatat pada waktu tersebut.");
        }
    }

    private static int getValidTime() {
        //mendapatkan waktu yang valid dimasukkan oleh pemain
        while (true) {
            try {
                int time = Integer.parseInt(scanner.nextLine());
                if (time >= 0 && time <= 2359 && time % 100 < 60) {
                    return time;
                }
                System.out.println("Masukkan waktu yang valid (0000-2359)");
            } catch (NumberFormatException e) {
                System.out.println("Masukkan format waktu yang benar (contoh: 2200)");
            }
        }
    }

    private static void initializeLocations() {
        locations = new HashMap<>();

        //menginisiasi lokasi dan memasukkan saksi, bukti hingga pernyataan,
        locations.put("Kedokteran", new Location("Kedokteran",
            new Witness("Beta","Teman penelitian Joko", "Sarung tangan Latex",1,  "Semalam dia bilang ingin mengambil sesuatu di departemennya"),
            "Sarung tangan Latex yang mencurigakan"));

        locations.put("Sistem Informasi", new Location("Sistem Informasi",
            new Witness("Bu Ita","Petugas Kebersihan Kampus",  "Tali dan bercak darah", 2, "Malam itu saya melihat perempuan dari arah yang sama"),
            "Tali dan bercak darah, CCTV yang pecah"));

        locations.put("Dr. Angka", new Location("Dr. Angka",
            new Witness("Pak Budi","Satpam kampus yang mengawasi cctv", "Rekaman CCTV", 3, "Melalui CCTV, saya melihat ada dua orang sedang bertengkar"),
            "Rekaman CCTV yang menunjukkan pertengkaran"));
        locations.put("Teknik Kelautan", new Location("Teknik Kelautan",
                new Witness("Leo", "Kakak tingkat yang sangat membenci Joko","Pipa air", 4, "Aku tidak menyangka dia pergi secepat itu, padahal aku ingin mengungkapkannya"),
                "Pipa air"));
        locations.put("Teknik Geomatika", new Location("Teknik Geomatika",
                new Witness("Ari", "Kakak tingkat sekaligus teman penelitian Joko","Saluran Air Bocor", 5, "Dia sepertinya khawatir karena ada dokumen yang hilang"),
                "Saluran Air Bocor"));
        locations.put("Teknik Informatika", new Location("Saka",
                new Witness("Saka","Teman Joko yang pintar", "Ketapel", 6, "Dia orang baik dan kami sering belajar bersama"),
                "Ketapel"));
        locations.put("Perpustakaan", new Location("Fia",
                new Witness("Fia", "Sahabat baik Joko yang sudah berteman selama 10 tahun","Sobekan buku", 7, "Dia dalam bahaya dan seharusnya aku tidak meninggalkannya"),
                "Sobekan buku"));
        locations.put("Research Center", new Location("Pak Santoso",
                new Witness("Pak Santoso", "Dosen pengampu penelitian","Dokumen Penelitian Terlarang", 8, "Saya tidak ingin berkomentar apapun"),
                "Dokumen Penelitian Terlarang"));

    }

    private static class GameInputException extends Exception {
        public GameInputException(String message) {
            super(message);
        }
    }
}