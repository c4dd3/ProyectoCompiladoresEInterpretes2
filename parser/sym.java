package parser;

public final class sym {
  // Palabras reservadas
  public static final int PROGRAM=1, VAR=2, BEGIN=3, END=4, IF=5, THEN=6, ELSE=7,
      WHILE=8, DO=9, FOR=10, TO=11, FUNCTION=12, PROCEDURE=13, READ=14, WRITE=15,
      INT=16, CHAR=17, REAL=18, STRING=19;

  // Booleanos/relacionales
  public static final int AND=20, OR=21, NOT=22;
  public static final int EQ=23, GE=24, GT=25, LE=26, LT=27, NE=28;

  // Aritm./asig.
  public static final int ASSIGN=29, INCR=30, DECR=31, PLUS=32, MINUS=33, TIMES=34, DIVIDE=35;
  public static final int DIV_KW=36, MOD_KW=37;

  // Separadores
  public static final int LPAREN=38, RPAREN=39, LBRACK=40, RBRACK=41,
      COMMA=42, SEMI=43, COLON=44, DOT=45;

  // Léxicos
  public static final int IDENT=46, STRING_LIT=47, CHAR_LIT=48, INT_LIT=49, REAL_LIT=50;

  // Especial
  public static final int EOF=0;
}
