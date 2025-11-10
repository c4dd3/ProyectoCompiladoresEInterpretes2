package parser;

import java.io.FileReader;
import java.io.File;

import scanner.Scanner;

public class MainParser {

    public static void run(String sourcePath) throws Exception {
        SyntaxErrorCollector.reset();

        try (FileReader fr = new FileReader(new File(sourcePath))) {
            Scanner sc = new Scanner(fr);
            Parser p = new Parser(sc);
            p.parse();
        }
        System.out.println();
        SyntaxErrorCollector.print();
    }

    public static void main(String[] args) {
        try {
            String path;
            if (args != null && args.length > 0)
                path = args[0];
            else
                path = "parser/testFile.abs";  

            run(path);
        } catch (Exception e) {
            System.err.println("Error en el análisis sintáctico: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
