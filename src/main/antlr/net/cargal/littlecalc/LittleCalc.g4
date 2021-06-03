grammar LittleCalc
    ;

stmts: stmt* EOF;
stmt:  ID '=' expr # AssignmentStmt 
    | PRINT expr* # PrintStmt;
expr
    : '(' expr ')'                                             # ParenExpr
    | <assoc = right> base = expr '^' exp = expr               # ExpExpr
    | lhs = expr op = ('*' | '/') rhs = expr                   # MulDivExpr
    | lhs = expr op = ('+' | '-') rhs = expr                   # AddSubExpr
    | lhs = expr op = (LT | LE | EQ | NE | GE | GT) rhs = expr # CompareExpr
    | cond = expr '?' tv = expr ':' fv = expr                  # ternaryExpr
    | NUMBER                                                   # NumberExpr
    | TRUE                                                     # TrueExpr
    | FALSE                                                    # FalseExpr
    | STRING                                                   # StringExpr
    | ID                                                       # IDExpr
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

TRUE:  'true';
FALSE: 'false';
PRINT: 'print';

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
COMMENT:        '//' .*? ('\n' | EOF) -> skip;
WS:             [ \t\r\n]+    -> skip;
//LINE_CONT:      '\\\n' -> skip;
BAD_TOKEN: .;