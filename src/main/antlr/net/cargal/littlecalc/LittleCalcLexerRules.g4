lexer grammar LittleCalcLexerRules
    ;

// keywords
IF:     'if';
ELSE:   'else';
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

TRUE:  'true';
FALSE: 'false';

PRINT
    : [Pp][Rr][Ii][Nn][Tt]
    ; // one way to handle case-insenstive

VARS: V A R S; // another way to handle case insensitivity

//  ANTLR Util commands
GUI:      'gui';
TREE:     'tree';
REFACTOR: 'refactor';
TOKENS:   'tokens';

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

fragment DIGIT: [0-9];
fragment ALPHA: [a-zA-Z];
NUMBER:         (DIGIT | '_')+ ('.' (DIGIT | '_')+)?;
STRING:         '"' ('\\"' | '\\\'' | .)*? '"';
S_STRING:       '\'' ('\\"' | '\\\'' | .)*? '\'' -> type(STRING);
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