package scanner;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public final class TokenCollector { // Define una clase final que no puede ser extendida

    /* ====== Tipos auxiliares ====== */

    private static final class TokenBucket {    // Clase interna para agrupar tokens iguales
        final String tipo;                      // Tipo de token, por ejemplo "IDENTIFICADOR"
        String displayLexema;                   // Primera forma escrita del token (para mostrar)
        final TreeMap<Integer, Integer> lineCounts = new TreeMap<>(); // Mapea línea a cantidad de ocurrencias

        TokenBucket(String tipo, String displayLexema) {    // Constructor
            this.tipo = tipo;                               // Inicializa el tipo
            this.displayLexema = displayLexema;             // Inicializa la forma escrita
        }

        void addAtLine(int linea) {                         // Método para agregar una ocurrencia en una línea
            lineCounts.merge(linea, 1, Integer::sum); // Suma 1 a la cuenta de esa línea
        }
    }

    private static final class LexError {   // Clase interna para representar errores léxicos
        final String codigo;                // Código del error, por ejemplo "ERROR_LEXICO"
        final String lexema;                // Lexema que causó el error
        final int linea;                    // Línea donde ocurrió el error

        LexError(String codigo, String lexema, int linea) {     // Constructor
            this.codigo = codigo; // Inicializa el código
            this.lexema = lexema; // Inicializa el lexema
            this.linea = linea;   // Inicializa la línea
        }
    }

    /* ====== Estado ====== */

    // key = tipo + "|" + lexemaNormalizado (en minúsculas si unifyCase es true)
    private static final TreeMap<String, TokenBucket> TOKENS = new TreeMap<>();     // Mapa de tokens agrupados
    private static final List<LexError> ERRORES = new ArrayList<>();                // Lista de errores léxicos

    private static final Locale LOCALE = Locale.ROOT;   // Locale para normalizar el case

    private static boolean unifyCase = true;            // Indica si se normaliza el case de los lexemas

    private TokenCollector() { /* no instancias */ }    // Constructor privado para evitar instanciación

    /* ====== API para el scanner ====== */

    /** Agregar un token válido. */
    public static void add(String tipo, String lexema, int linea) {                 // Método para agregar un token
        if (lexema == null) return;                                                 // Si el lexema es nulo, no hace nada
        final String norm = unifyCase ? lexema.toLowerCase(LOCALE) : lexema;        // Normaliza el lexema si corresponde
        final String key = tipo + "|" + norm;                                       // Crea la clave para el mapa

        TokenBucket bucket = TOKENS.get(key);               // Busca si ya existe ese token
        if (bucket == null) {                               // Si no existe
            bucket = new TokenBucket(tipo, lexema);         // Crea un nuevo TokenBucket con la forma escrita original
            TOKENS.put(key, bucket);                        // Lo agrega al mapa de TOKENS
        }
        bucket.addAtLine(linea);                            // Agrega la línea al conteo de ocurrencias del token
    }

    /** Registrar un error léxico. */
    public static void addError(String codigo, String lexema, int linea) {
        ERRORES.add(new LexError(codigo, lexema, linea));
    }

    /** Limpiar todo. */
    public static void reset() {
        TOKENS.clear();
        ERRORES.clear();
    }

    /** Configurar si se unifica por mayúsculas/minúsculas. Por defecto: true. */
    public static void setUnifyCase(boolean value) {
        unifyCase = value;
    }

    /* ====== Salida ====== */

    public static void printResults() {
        // 1) Errores
        System.out.println("=== ERRORES LEXICOS ===");
        if (ERRORES.isEmpty()) {
            System.out.println("(ninguno)");
        } else {
            for (LexError e : ERRORES) {
                System.out.println("[" + e.codigo + "] '" + e.lexema + "' en línea " + e.linea);
            }
        }

        // 2) Tokens
        System.out.println("\n=== TOKENS ENCONTRADOS ===");
        if (TOKENS.isEmpty()) {
            System.out.println("(ninguno)");
            return;
        }

        // Reorganizamos para imprimir agrupado por tipo y luego por lexema mostrado
        Map<String, List<TokenBucket>> porTipo = new TreeMap<>();
        for (TokenBucket b : TOKENS.values()) {
            porTipo.computeIfAbsent(b.tipo, k -> new ArrayList<>()).add(b);
        }
        for (List<TokenBucket> lista : porTipo.values()) {
            // ordenar por displayLexema (case-insensitive pero estable)
            lista.sort(Comparator.comparing(tb -> tb.displayLexema.toLowerCase(LOCALE)));
        }

        // Encabezado al estilo del enunciado
        System.out.printf("%-20s %-18s %s%n", "Token", "Tipo de Token", "Línea(s)");

        for (Map.Entry<String, List<TokenBucket>> entry : porTipo.entrySet()) {
            for (TokenBucket b : entry.getValue()) {
                String lineas = formatLineCounts(b.lineCounts);
                System.out.printf("%-20s %-18s %s%n", b.displayLexema, b.tipo, lineas);
            }
        }
    }

    /* ====== Utilitarios ====== */

    private static String formatLineCounts(TreeMap<Integer, Integer> counts) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            int line = e.getKey();
            int c = e.getValue();
            if (c == 1) sb.append(line);
            else sb.append(line).append('(').append(c).append(')');
            sb.append(", ");
        }
        if (sb.length() >= 2) sb.setLength(sb.length() - 2); // quitar ", "
        return sb.toString();
    }


}
