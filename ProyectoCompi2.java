import java.util.Scanner;
import parser.MainParser;
import scanner.MainScanner;

public class ProyectoCompi2 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== Analizador Léxico y Sintáctico ===");
        System.out.println("1) Probar Analizador Léxico (Scanner)");
        System.out.println("2) Probar Analizador Sintáctico (Parser)");
        System.out.print("Seleccione una opción: ");
        int opcion = input.nextInt();

        switch (opcion) {
            case 1 -> MainScanner.run();
            case 2 -> MainParser.run();
            default -> System.out.println("Opción inválida.");
        }
    }
}
