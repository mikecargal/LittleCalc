grammar LittleCalc
    ;

replIn: stmt? EOF # replStmt | expr? EOF # replExpr;

stmts: stmt* EOF;
stmt
    : ID '=' expr # AssignmentStmt
    | PRINT expr* # PrintStmt
    | VARS        # printVars
    ;
expr
    : '(' expr ')'                                   # ParenExpr
    | <assoc = right> base = expr '^' exp = expr     # ExpExpr
    | lhs = expr op = ('*' | '/') rhs = expr         # MulDivExpr
    | lhs = expr op = ('+' | '-') rhs = expr         # AddSubExpr
    | lhs = expr op = (LT | LE | GE | GT) rhs = expr # CompareExpr
    | lhs = expr op = (EQ | NE) rhs = expr           # EqualityExpr
    | cond = expr '?' tv = expr ':' fv = expr        # ternaryExpr
    | lhs = expr AND rhs = expr                      # andExpr
    | lhs = expr OR rhs = expr                       # orExpr
    | '!' expr                                       # negationExpr
    | NUMBER                                         # NumberExpr
    | TRUE                                           # TrueExpr
    | FALSE                                          # FalseExpr
    | STRING                                         # StringExpr
    | ID                                             # IDExpr
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
VARS:  'vars';
STACK: 'stack';

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
WS:             [ \t\r\n]+            -> skip;
BAD_TOKEN:      .;