grammar LittleCalc
    ;
import LittleCalcLexerRules; // would not work
//options { tokenVocab=LittleCalcLexer; }

replIn: stmt? EOF # replStmt | expr? EOF # replExpr;
calcIn: stmts EOF;

stmts: stmt*;
stmt
    : ID '=' expr                     # AssignmentStmt
    | PRINT expr*                     # PrintStmt
    | VARS                            # printVars
    | TREE '{' (expr | stmts) '}'     # treeStmt
    | GUI '{' (expr | stmts) '}'      # guiStmt
    | SIMPLIFY '{' (expr | stmts) '}' # simplifyStmt
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
