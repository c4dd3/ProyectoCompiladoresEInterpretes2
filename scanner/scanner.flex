/* ================================== Sección 1: User code ================================== */
package scanner;

/* ===== Corte de sección ===== */
%%

/* ================================== Sección 2: Opciones + Macros ================================== */
%class Scanner
%unicode
%public
%line
%column
%ignorecase
%type int


/* --- Básicos --- */
DIGITO            = [0-9]                               // dígito
LETRA             = [A-Za-z]                            // letra (mayúscula o minúscula)

/* --- Identificadores y espacios --- */
IDENTIFICADOR     = {LETRA}({LETRA}|{DIGITO}){0,126}    // identificador: letra seguida de letras/dígitos, hasta 127 caracteres
ESPACIOS          = [ \t\r\n]+                          // espacios en blanco (espacio, tab, retorno de carro, nueva línea)

/* Identificador inválido (más de 127 caracteres) */
IDENTIFICADOR_INVALIDO = {LETRA}({LETRA}|{DIGITO}){127}({LETRA}|{DIGITO})+    // identificador inválido: más de 127 caracteres

/* --- Comentarios (no anidados) --- */
COM_LLAVES        = \{[^}]*\}                   /*  { ... }      */
COM_PAREST        = \(\*([^*]|\*+[^)])*\*+\)    /*  (* ... *)    */

/* --- Literales numéricos --- */
HEX               = 0[xX][0-9a-fA-F]+           // hexadecimal (0xFF, 0X1A, ...)
OCT               = 0[0-7]+                     // octal (0, 07, 077, ...)
ENTERO_DEC        = [1-9][0-9]*|0               // decimal (0, 1, 2, ..., sin ceros a la izquierda)
REAL_BASICO       = {DIGITO}+\.{DIGITO}+        // parte entera y decimal (1.0, 0.5, etc.)
EXP               = [eE][+-]?{DIGITO}+          // exprentación científica (E10, e-5, etc.)
REAL              = {REAL_BASICO}({EXP})?       // real con parte entera y decimal, opcionalmente con exponente (3.0, 0.5E10, 1.5e-4, etc.)

/* Reales inválidos (punto líder o punto cola) */
ERROR_REAL_PUNTO_LIDER = \.{DIGITO}+({EXP})?    // punto líder (ej: .5, .123E10, etc.)
ERROR_REAL_PUNTO_COLA  = {DIGITO}+\.({EXP})?    // punto cola (ej: 5., 123.E10, etc.)

/* --- Errores de literales numéricos --- */
ID_TAIL        = {LETRA}({LETRA}|{DIGITO})*         // cola de identificador (letras/dígitos)
NUM_DEC_E      = {ENTERO_DEC}({EXP})?               // número decimal con posible exponente (123, 0, 456E10, 789e-5, etc.)
NUM_ANY        = ({HEX}|{OCT}|{REAL}|{NUM_DEC_E})   // cualquier número (hex, octal, real, decimal con exponente)
ERROR_NUM_SEGUIDO_TEXTO = {NUM_ANY}{ID_TAIL}        // número seguido de texto (ej: 123abc, 0x1G, 0778, 3.14pi, etc.)

/* Cualquier símbolo NO permitido dentro de un identificador (¡aquí cae ‘!’) */
SIMBOLO_ILEGAL_EN_ID = [^A-Za-z0-9 \t\r\n\+\-\*\/,;\(\)\[\]:\.\^<>=]                    // símbolo ilegal en ID (cualquier cosa que no sea letra, dígito, espacio o símbolo permitido)
ID_CON_SIMBOLO_ILEGAL = {LETRA}({LETRA}|{DIGITO})* ({SIMBOLO_ILEGAL_EN_ID}{ID_TAIL})+   // identificador con símbolo ilegal (ej: var!, id$, nombre@, etc.)

/* --- Literales de texto --- */
STRING            = \"([^\"\n])*\"              // string entre comillas dobles (1 sola línea)
CHAR              = \'([^\'\n])\'               // carácter entre comillas simples (ej: 'A', '9', etc.)

/* Errores de literales sin cierre */
STRING_INCOMP = \"[^\"\r\n]*                    // string sin cierre (hasta fin de línea o retorno de carro)
CHAR_INCOMP   = \'[^\'\r\n]*                    // char sin cierre (hasta fin de línea o retorno de carro)

/* --- Palabras que funcionan como OPERADORES (reportarlas como OPERADOR) --- */
OPER_PALABRA      = AND|OR|NOT|DIV|MOD|IN|SHL|SHR

/* --- Otras reservadas --- */
RESERVADAS = ABSOLUTE|ARRAY|ASM|BEGIN|CASE|CONST|CONSTRUCTOR|DESTRUCTOR|EXTERNAL|DO|DOWNTO|ELSE|END|FILE|FOR|FORWARD|FUNCTION|GOTO|IF|IMPLEMENTATION|INLINE|INTERFACE|INTERRUPT|LABEL|NIL|OBJECT|OF|PACKED|PRIVATE|PROCEDURE|RECORD|REPEAT|SET|STRING|THEN|TO|TYPE|UNIT|UNTIL|USES|VAR|VIRTUAL|WHILE|WITH|XOR|AND|OR|NOT|DIV|MOD|IN|SHL|SHR|PROGRAM|READ|WRITE|INT|CHAR|REAL

/* --- Operadores y separadores simbólicos (poner largos primero) --- */
OPER_SIMBOLO = \*\*|\+\+|\-\-|:=|<=|>=|<>|=|<|>|\+|\-|\*|\/|,|;|\(|\)|\[|\]|:|\.|\^

/* (Opcional) Guardias de error numérico */
OCT_INVALIDO      = 0[0-7]*[89]                         // octal inválido (ej: 09, 078, etc.)
HEX_INVALIDO      = 0[xX][^0-9A-Fa-f][A-Za-z0-9]*       // hexadecimal inválido (ej: 0xG, 0x1Z, etc.)

%%
/* ================================== Sección 3: Reglas léxicas ================================== */

/* ===================== ESPACIOS & COMENTARIOS ===================== */
{ESPACIOS}                  { /* ignore */ }
{COM_LLAVES}                { /* ignore */ }
{COM_PAREST}                { /* ignore */ }

/* ===================== GUARDAS DE ERROR (ANTES DE TODO) ===================== */
/* Reales inválidos: .5, .5e3, 5., 5.e-2 */
{ERROR_REAL_PUNTO_LIDER}    { TokenCollector.addError("ERROR_LEXICO_REAL_PUNTO_LIDER", yytext(), yyline+1); }
{ERROR_REAL_PUNTO_COLA}     { TokenCollector.addError("ERROR_LEXICO_REAL_PUNTO_COLA",  yytext(), yyline+1); }

/* ===================== ERRORES DE NÚMEROS ===================== */
{OCT_INVALIDO}              { TokenCollector.addError("ERROR_LEXICO_OCTAL_INVALIDO",     yytext(), yyline+1); }
{HEX_INVALIDO}              { TokenCollector.addError("ERROR_LEXICO_HEXADECIMAL_INVALIDO", yytext(), yyline+1); }

/* ===================== NÚMEROS VÁLIDOS (ORDEN ESPECÍFICO) ===================== */
{HEX}                       { TokenCollector.add("LITERAL_HEX",    yytext(), yyline+1); }
{OCT}                       { TokenCollector.add("LITERAL_OCTAL",  yytext(), yyline+1); }
{REAL}                      { TokenCollector.add("LITERAL_REAL",   yytext(), yyline+1); }
{ENTERO_DEC}                { TokenCollector.add("LITERAL_ENTERO", yytext(), yyline+1); }

/* Número válido seguido de letras/dígitos (p. ej. 1invalido, 0x1FZ, 3.2e5foo) */
{ERROR_NUM_SEGUIDO_TEXTO}   { TokenCollector.addError("ERROR_LEXICO_NUM_SEGUIDO_POR_TEXTO", yytext(), yyline+1); }

/* ===== Literales de texto válidos ===== */
{STRING}                    { TokenCollector.add("LITERAL_STRING", yytext(), yyline+1); }
{CHAR}                      { TokenCollector.add("LITERAL_CHAR",   yytext(), yyline+1); }

/* ===== Errores de cierre de "" y '' ===== */
{STRING_INCOMP} / \r?\n     { TokenCollector.addError("ERROR_STRING_SIN_CIERRE", yytext(), yyline+1); }
{CHAR_INCOMP}   / \r?\n     { TokenCollector.addError("ERROR_CHAR_SIN_CIERRE",   yytext(), yyline+1); }

/* ===================== ERRORES DE IDENTIFICADORES ===================== */
{ID_CON_SIMBOLO_ILEGAL}     { TokenCollector.addError("ERROR_IDENTIFICADOR_SIMBOLO_ILEGAL", yytext(), yyline+1); }

/* ==================== Identificador inválido (más de 127 caracteres) ===================== */
{IDENTIFICADOR_INVALIDO}    { TokenCollector.addError("ERROR_IDENTIFICADOR_LONGITUD", yytext(), yyline+1); }

/* ===================== OPERADORES / RESERVADAS / IDENTIFICADORES ===================== */
{OPER_SIMBOLO}              { TokenCollector.add("OPERADOR", yytext(), yyline+1); }
{OPER_PALABRA}              { TokenCollector.add("OPERADOR", yytext(), yyline+1); }
{RESERVADAS}                { TokenCollector.add("PALABRA_RESERVADA", yytext(), yyline+1); }
{IDENTIFICADOR}             { TokenCollector.add("IDENTIFICADOR", yytext(), yyline+1); }

/* ===================== CATCH-ALL & EOF ===================== */
.                           { TokenCollector.addError("ERROR_LEXICO", yytext(), yyline+1); }
<<EOF>>                     { return YYEOF; }