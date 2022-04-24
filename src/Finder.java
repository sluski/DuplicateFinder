import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class Finder {

    private static MessageDigest md;
    private static Scanner scanner = new Scanner(System.in);
    private static String directoryPath;
    private static Map<String, List<File>> checksums = new HashMap<>();

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        md = MessageDigest.getInstance("MD5");
        System.out.println("Duplicate finder");
        System.out.print("Enter directory path: ");
        directoryPath = scanner.next();
        generateListOfFilesChecksum(directoryPath);
        printDuplicates();
        printMenu();
    }

    private static void printMenu() {
        System.out.println("1. Usuń kopie plików");
        System.out.println("2. Nie usuwaj");
        int choose = scanner.nextInt();
        switch (choose) {
            case 1:
                deleteDuplicates();
        }
    }

    private static void deleteDuplicates() {
        System.out.println("Removing files");
        checksums.forEach((k, v) -> {
            while (v.size() != 1) {
                System.out.println("Removing: " + v.get(0).getName());
                v.get(0).delete();
                v.remove(0);
            }
        });
        System.out.println("Duplikaty usunięte");
    }

    private static void generateListOfFilesChecksum(String folderPath) throws IOException {
        System.out.println("Generating list of files");
        File[] files = new File(folderPath).listFiles();
        try {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isFile()) {
                    md.update(Files.readAllBytes(Paths.get(file.getPath())));
                    String md5 = DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
                    if (checksums.containsKey(md5)) {
                        checksums.get(md5).add(file);
                    } else {
                        checksums.put(md5, (Arrays.asList(file)));
                    }
                }
                if ((i % 10) == 0) {
                    System.out.printf("%.2f%%\n", ((float) i / files.length) * 100);
                }

            }
            checksums.forEach((k, v) -> v.stream().sorted(Comparator.comparingInt(o -> o.getName().length())).collect(Collectors.toList()));
        } catch (
                IOException ex) {
            System.err.println("Błąd dla pliku o lokalizacji: " + folderPath);
            ex.printStackTrace();
        }

    }

    private static void printDuplicates() {
        System.out.println("Printing duplicates");
        checksums.forEach((k, v) -> {
            if (v.size() > 1) {
                v.forEach(t -> System.out.println(t.getName()));
                System.out.println("--------");
            }
        });
    }

}
