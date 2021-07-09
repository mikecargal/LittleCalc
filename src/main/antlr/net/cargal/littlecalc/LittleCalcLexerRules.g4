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
    // another way to handle case insensitivity
fragment A: [Aa];
//...
fragment R: [Rr];
//...
fragment S: [Ss];
//...
fragment V: [Vv];
VARS:     V A R S;

GUI:      'gui';
TREE:     'tree';
SIMPLIFY: 'simplify';

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
