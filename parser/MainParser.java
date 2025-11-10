package parser;

import scanner.Scanner;
import java.io.FileReader;

public class MainParser {

    public static void run() throws Exception {
        // Crear el scanner
        scanner.Scanner sc = new scanner.Scanner(new FileReader("parser/testFile.abs"));
        
        // Crear el parser
        parser.Parser p = new parser.Parser(sc);
        
        // Ejecutar el análisis sintáctico
        p.parse();
    }

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) {
            System.err.println("Error en el análisis sintáctico: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
