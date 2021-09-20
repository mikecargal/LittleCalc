lexer grammar LittleCalcLexerRules
    ;

// Symbol Tokens
O_PAREN: '(';
C_PAREN: ')';
O_CURLY: '{';
C_CURLY: '}';
EXP:     '^';
MUL:     '*';
DIV:     '/';
ADD:     '+';
SUB:     '-';
COLON:   ':';
QMARK:   '?';
ASSIGN: '=';
EQ:     '==';
NE:     '!=';
LT:     '<';
LE:     '<=';
GT:     '>';
GE:     '>=';
NOT:    '!';
AND:    '&&';
OR:     '||';

TRUE: T R U E;
FALSE: F A L S E;

PRINT: P R I N T; 
VARS: V A R S; 

//  ANTLR Util commands
GUI:      G U I;
TREE:     T R E E;
REFACTOR: R E F A C T O R;
TOKENS:   T O K E N S;

fragment DIGIT: [0-9];
fragment ALPHA: [a-zA-Z];
NUMBER:         (DIGIT | '_')+ ('.' (DIGIT | '_')+)?;
fragment STRING_CONTENT: ('\\"' | '\\\'' | .);
STRING:         '"' STRING_CONTENT*? '"';
S_STRING:       '\'' STRING_CONTENT*? '\'' -> type(STRING);
ID:             (ALPHA | '_') (ALPHA | DIGIT | '_')*;
COMMENT:        '//' .*? ('\n' | EOF) -> channel(HIDDEN);
WS:             [ \t\r\n]+            -> channel(HIDDEN);
BAD_TOKEN:      .;

fragment A: [Aa];
fragment B: [Bb];
fragment C: [Cc];
fragment D: [Dd];
fragment E: [Ee];
fragment F: [Ff];
fragment G: [Gg];
fragment H: [Hh];
fragment I: [Ii];
fragment J: [Jj];
fragment K: [Kk];
fragment L: [Ll];
fragment M: [Mm];
fragment N: [Nn];
fragment O: [Oo];
fragment P: [Pp];
fragment Q: [Qq];
fragment R: [Rr];
fragment S: [Ss];
fragment T: [Tt];
fragment U: [Uu];
fragment V: [Vv];
fragment W: [Ww];
fragment X: [Xx];
fragment Y: [Yy];
fragment Z: [Zz];