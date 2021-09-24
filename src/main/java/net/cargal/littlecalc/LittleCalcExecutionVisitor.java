package net.cargal.littlecalc;

import java.util.Optional;

import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.tinylog.Logger;

import net.cargal.littlecalc.LittleCalcParser.AddSubExprContext;
import net.cargal.littlecalc.LittleCalcParser.AssignmentStmtContext;
import net.cargal.littlecalc.LittleCalcParser.EqualityExprContext;
import net.cargal.littlecalc.LittleCalcParser.GUIUtilContext;
import net.cargal.littlecalc.LittleCalcParser.ImplicitPrintStmtContext;
import net.cargal.littlecalc.LittleCalcParser.MulDivExprContext;
import net.cargal.littlecalc.LittleCalcParser.PrintStmtContext;
import net.cargal.littlecalc.LittleCalcParser.PrintVarsContext;
import net.cargal.littlecalc.LittleCalcParser.RefactorUtilContext;
import net.cargal.littlecalc.LittleCalcParser.TokensUtilContext;
import net.cargal.littlecalc.LittleCalcParser.TreeUtilContext;

public class LittleCalcExecutionVisitor extends LittleCalcBaseVisitor<Void> {
    private static final String ANY_EXPR_XPATH = "//expr";
    private ParseTreePattern eqTruePattern;
    private ParseTreePattern eqFalsePattern;
    private ParseTreePattern neTruePattern;
    private ParseTreePattern neFalsePattern;
    private ParseTreePattern plus0PatternA;
    private ParseTreePattern plus0PatternB;
    private ParseTreePattern times1PatternA;
    private ParseTreePattern times1PatternB;
    private ParseTreePattern times0PatternA;
    private ParseTreePattern times0PatternB;
    protected final SymbolTable<LittleValue> variables = new SymbolTable<>();
    protected final LittleCalcExprVisitor exprVisitor;
    private static final String FULL_TRACING_CMD = "fullTracing";
    private static final String LEXER_TRACING_CMD = "lexerTracing";
    private static final String PARSER_TRACING_CMD = "parserTracing";
    private boolean parserTracing = false;
    private boolean lexerTracing = false;
    private Parser parser;
    private TokenStreamRewriter rewriter;

    public LittleCalcExecutionVisitor(Parser parser) {
        this();
        this.parser = parser;
        var exprRule = LittleCalcParser.RULE_expr;
        eqTruePattern = parser.compileParseTreePattern("<expr> == <TRUE>", exprRule);
        neTruePattern = parser.compileParseTreePattern("<expr> != <TRUE>", exprRule);
        eqFalsePattern = parser.compileParseTreePattern("<expr> == <FALSE>", exprRule);
        neFalsePattern = parser.compileParseTreePattern("<expr> != <FALSE>", exprRule);
        plus0PatternA = parser.compileParseTreePattern("<expr> + 0", exprRule);
        plus0PatternB = parser.compileParseTreePattern("0 + <expr>", exprRule);
        times1PatternA = parser.compileParseTreePattern("<expr> * 1", exprRule);
        times1PatternB = parser.compileParseTreePattern("1 * <expr>", exprRule);
        times0PatternA = parser.compileParseTreePattern("<expr> * 0", exprRule);
        times0PatternB = parser.compileParseTreePattern("0 * <expr>", exprRule);
        resetRefactoring();
    }

    public LittleCalcExecutionVisitor() {
        exprVisitor = new LittleCalcExprVisitor(variables);
    }

    public void resetRefactoring() {
        rewriter = new TokenStreamRewriter(parser.getInputStream());
    }

    @Override
    public Void visitAssignmentStmt(AssignmentStmtContext ctx) {
        super.visitAssignmentStmt(ctx);
        var val = exprVisitor.visit(ctx.expr());
        var id = ctx.ID().getText();
        variables.put(id, val);
        Logger.debug("assignment to " + id);
        processCommand(id);
        return null;
    }

    @Override
    public Void visitPrintStmt(PrintStmtContext ctx) {
        for (LittleCalcParser.ExprContext exprCtx : ctx.expr()) {
            System.out.print(exprVisitor.visit(exprCtx));
        }
        System.out.println();
        return null;
    }

    @Override
    public Void visitImplicitPrintStmt(ImplicitPrintStmtContext ctx) {
        System.out.println(exprVisitor.visit(ctx.expr()));
        return null;
    }

    @Override
    public Void visitGUIUtil(GUIUtilContext ctx) {
        if (parser != null) {
            var content = ctx.stmts() == null ? ctx.antlrUtil() : ctx.stmts();
            Trees.inspect(content, parser);
        }
        return null;
    }

    @Override
    public Void visitTreeUtil(TreeUtilContext ctx) {
        if (parser != null) {
            var content = ctx.stmts() == null ? ctx.antlrUtil() : ctx.stmts();
            System.out.println(content.toStringTree(parser));
        }
        return null;
    }

    @Override
    public Void visitTokensUtil(TokensUtilContext ctx) {
        TokenStream tokenStream = parser.getTokenStream();
        var content = ctx.stmts() == null ? ctx.antlrUtil() : ctx.stmts();
        Interval sourceInterval = content.getSourceInterval();
        for (int i = sourceInterval.a; i <= sourceInterval.b; i++) {
            System.out.println(tokenStream.get(i));
        }
        return null;
    }

    @Override
    public Void visitRefactorUtil(RefactorUtilContext ctx) {
        var contentInterval = new Interval( //
                ctx.O_CURLY().getSymbol().getTokenIndex() + 1, //
                ctx.C_CURLY().getSymbol().getTokenIndex() - 1);
        var pName = "simplify" + ctx.getStart().getTokenIndex();

        eqTrue(ctx, pName);
        neTrue(ctx, pName);
        eqFalse(ctx, pName);
        neFalse(ctx, pName);
        plus0(ctx, pName);
        times1(ctx, pName);
        times0(ctx, pName);

        System.out.println(rewriter.getText(pName, contentInterval));
        return null;
    }

    private void eqTrue(RefactorUtilContext ctx, String pName) {
        for (var match : eqTruePattern.findAll(ctx, ANY_EXPR_XPATH)) {
            var matchCtx = (EqualityExprContext) (match.getTree());
            rewriter.delete(pName, matchCtx.op, matchCtx.rhs.getStop());
        }
    }

    private void neTrue(RefactorUtilContext ctx, String pName) {
        for (var match : neTruePattern.findAll(ctx, ANY_EXPR_XPATH)) {
            var matchCtx = (EqualityExprContext) (match.getTree());
            rewriter.insertBefore(pName, matchCtx.lhs.start, "!");
            rewriter.delete(pName, matchCtx.op, matchCtx.rhs.getStop());
        }
    }

    private void eqFalse(RefactorUtilContext ctx, String pName) {
        for (var match : eqFalsePattern.findAll(ctx, ANY_EXPR_XPATH)) {
            var matchCtx = (EqualityExprContext) (match.getTree());
            rewriter.insertBefore(pName, matchCtx.lhs.start, "!");
            rewriter.delete(pName, matchCtx.op, matchCtx.rhs.getStop());
        }
    }

    private void neFalse(RefactorUtilContext ctx, String pName) {
        for (var match : neFalsePattern.findAll(ctx, ANY_EXPR_XPATH)) {
            var matchCtx = (EqualityExprContext) (match.getTree());
            rewriter.delete(pName, matchCtx.op, matchCtx.rhs.getStop());
        }
    }

    private void plus0(RefactorUtilContext ctx, String pName) {
        for (var match : plus0PatternA.findAll(ctx, ANY_EXPR_XPATH)) {
            var matchCtx = (AddSubExprContext) (match.getTree());
            rewriter.delete(pName, matchCtx.op, matchCtx.rhs.getStop());
        }
        for (var match : plus0PatternB.findAll(ctx, ANY_EXPR_XPATH)) {
            var matchCtx = (AddSubExprContext) (match.getTree());
            rewriter.delete(pName, matchCtx.lhs.getStart(), matchCtx.op);
        }
    }

    private void times1(RefactorUtilContext ctx, String pName) {
        for (var match : times1PatternA.findAll(ctx, ANY_EXPR_XPATH)) {
            var matchCtx = (MulDivExprContext) (match.getTree());
            rewriter.delete(pName, matchCtx.op, matchCtx.rhs.getStop());
        }
        for (var match : times1PatternB.findAll(ctx, ANY_EXPR_XPATH)) {
            var matchCtx = (MulDivExprContext) (match.getTree());
            rewriter.delete(pName, matchCtx.lhs.getStart(), matchCtx.op);
        }
    }


    private void times0(RefactorUtilContext ctx, String pName) {
        for (var match : times0PatternA.findAll(ctx, ANY_EXPR_XPATH)) {
            var matchCtx = (MulDivExprContext) (match.getTree());
            rewriter.replace(pName, matchCtx.lhs.getStart(), matchCtx.rhs.getStop(),0);
        }
        for (var match : times0PatternB.findAll(ctx, ANY_EXPR_XPATH)) {
            var matchCtx = (MulDivExprContext) (match.getTree());
            rewriter.replace(pName, matchCtx.rhs.getStart(), matchCtx.rhs.getStop(),0);
        }
    }

    @Override
    public Void visitPrintVars(PrintVarsContext ctx) {
        dumpVariables();
        return null;
    }

    public void dumpVariables() {
        // noinspection OptionalGetWithoutIsPresent
        variables.keyStream().forEach(key -> System.out.println("\t" + key + " : " + variables.get(key).get()));
    }

    public Optional<LittleValue> getVar(String varID) {
        return variables.get(varID);
    }

    private void processCommand(String cmd) {
        if (PARSER_TRACING_CMD.equalsIgnoreCase(cmd)) {
            parserTracingCmd();
        } else if (LEXER_TRACING_CMD.equalsIgnoreCase(cmd)) {
            lexerTracingCmd();
        } else if (FULL_TRACING_CMD.equalsIgnoreCase(cmd)) {
            fullTracingCmd();
        }
    }

    private void parserTracingCmd() {
        parserTracing = getVar(PARSER_TRACING_CMD).orElse(LVBoolean.FALSE).bool();
        System.out.println("Parser Tracing " + (parserTracing ? "On" : "Off"));
    }

    private void lexerTracingCmd() {
        lexerTracing = getVar(LEXER_TRACING_CMD).orElse(LVBoolean.FALSE).bool();
        System.out.println("Lexer Tracing " + (lexerTracing ? "On" : "Off"));
    }

    private void fullTracingCmd() {
        var fullTracing = getVar(FULL_TRACING_CMD).orElse(LVBoolean.FALSE).bool();
        parserTracing = fullTracing;
        lexerTracing = fullTracing;
        variables.put(PARSER_TRACING_CMD, parserTracing ? LVBoolean.TRUE : LVBoolean.FALSE);
        variables.put(LEXER_TRACING_CMD, lexerTracing ? LVBoolean.TRUE : LVBoolean.FALSE);
        System.out.println("Full Tracing " + (parserTracing ? "On" : "Off"));
    }

    public boolean isParserTracing() {
        return parserTracing;
    }

    public boolean isLexerTracing() {
        return lexerTracing;
    }
}
