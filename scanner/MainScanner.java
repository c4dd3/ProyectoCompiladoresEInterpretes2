package scanner;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

public class MainScanner {
    public static void run() {
        java.util.Scanner input = new java.util.Scanner(System.in);

        System.out.println("=== Pruebas del Analizador Léxico ===");
        int i = 1;
        for (TestFile tf : TestFile.values()) {
            System.out.printf("%d) %s - %s%n", i, tf.getFileName(), tf.getDescription());
            i++;
        }

        System.out.print("Seleccione un número de prueba: ");
        int choice = input.nextInt();
        input.nextLine();

        if (choice < 1 || choice > TestFile.values().length) {
            System.out.println("Opción inválida.");
            return;
        }

        TestFile selected = TestFile.values()[choice - 1];

        // ✅ Usa Paths.get() para formar rutas portátiles
        String archivo = Paths.get("scanner", "test", selected.getFileName()).toString();

        // Debug: muestra la ruta absoluta
        System.out.println("\nEjecutando prueba léxica: " + selected.getDescription());
        System.out.println("Archivo: " + new File(archivo).getAbsolutePath() + "\n");

        try (FileReader reader = new FileReader(archivo)) {
            Scanner scanner = new Scanner(reader);
            while (scanner.yylex() != -1) {
                // consumir tokens
            }
            TokenCollector.printResults();
        } catch (Exception e) {
            System.err.println("Error al leer el archivo o durante el análisis léxico:");
            e.printStackTrace();
        }
    }
}
