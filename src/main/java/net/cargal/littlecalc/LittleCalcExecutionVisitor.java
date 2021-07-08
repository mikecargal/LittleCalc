package net.cargal.littlecalc;

import java.util.Optional;

import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.tinylog.Logger;

import net.cargal.littlecalc.LittleCalcParser.AssignmentStmtContext;
import net.cargal.littlecalc.LittleCalcParser.EqualityExprContext;
import net.cargal.littlecalc.LittleCalcParser.GuiStmtContext;
import net.cargal.littlecalc.LittleCalcParser.PrintStmtContext;
import net.cargal.littlecalc.LittleCalcParser.PrintVarsContext;
import net.cargal.littlecalc.LittleCalcParser.ReplExprContext;
import net.cargal.littlecalc.LittleCalcParser.SimplifyStmtContext;
import net.cargal.littlecalc.LittleCalcParser.TreeStmtContext;

public class LittleCalcExecutionVisitor extends LittleCalcBaseVisitor<Void> {
    protected SymbolTable<LittleValue> variables = new SymbolTable<>();
    protected LittleCalcExprVisitor exprVisitor;
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
        rewriter = new TokenStreamRewriter(parser.getInputStream());
    }

    public LittleCalcExecutionVisitor() {
        exprVisitor = new LittleCalcExprVisitor(variables);
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
    public Void visitReplExpr(ReplExprContext ctx) {
        System.out.println(exprVisitor.visit(ctx.expr()));
        return null;
    }

    @Override
    public Void visitGuiStmt(GuiStmtContext ctx) {
        if (parser != null) {
            if (ctx.expr() != null) {
                Trees.inspect(ctx.expr(), parser);
            } else {
                Trees.inspect(ctx.stmts(), parser);
            }
        }
        return null;
    }

    @Override
    public Void visitTreeStmt(TreeStmtContext ctx) {
        if (parser != null) {
            if (ctx.expr() != null) {
                System.out.println(ctx.expr().toStringTree(parser));
            } else {
                System.out.println(ctx.stmts().toStringTree(parser));
            }
        }
        return null;
    }

    @Override
    public Void visitSimplifyStmt(SimplifyStmtContext ctx) {
        var contentInterval = new Interval( //
                ctx.O_CURLY().getSymbol().getTokenIndex() + 1, //
                ctx.C_CURLY().getSymbol().getTokenIndex() - 1);
        var pName = "simplify"+ctx.getStart().getTokenIndex();

        eqTrue(ctx, pName);
        neTrue(ctx, pName);
        eqFalse(ctx, pName);
        neFalse(ctx, pName);
        plus0(ctx, pName);
        times1(ctx, pName);

        System.out.println(rewriter.getText(pName,contentInterval));
        return null;
    }

    private void eqTrue(SimplifyStmtContext ctx,String pName) {
        var pattern = parser.compileParseTreePattern("<expr> == <TRUE>", LittleCalcParser.RULE_expr);
        var matches = pattern.findAll(ctx, "//expr");
        for (var match : matches) {
            var matchCtx = (EqualityExprContext) (match.getTree());
            rewriter.delete(pName,matchCtx.op, matchCtx.rhs.getStop());
        }
    }

    private void neTrue(SimplifyStmtContext ctx,String pName) {
        var pattern = parser.compileParseTreePattern("<expr> != <TRUE>", LittleCalcParser.RULE_expr);
        var matches = pattern.findAll(ctx, "//expr");
        for (var match : matches) {
            var matchCtx = (EqualityExprContext) (match.getTree());
            rewriter.insertBefore(pName, matchCtx.lhs.start, "!");
            rewriter.delete(pName,matchCtx.op, matchCtx.rhs.getStop());
        }
    }

    private void eqFalse(SimplifyStmtContext ctx,String pName) {
        var pattern = parser.compileParseTreePattern("<expr> == <FALSE>", LittleCalcParser.RULE_expr);
        var matches = pattern.findAll(ctx, "//expr");
        for (var match : matches) {
            var matchCtx = (EqualityExprContext) (match.getTree());
            rewriter.insertBefore(pName, matchCtx.lhs.start, "!");
            rewriter.delete(pName,matchCtx.op, matchCtx.rhs.getStop());
        }
    }

    private void neFalse(SimplifyStmtContext ctx,String pName) {
        var pattern = parser.compileParseTreePattern("<expr> != <FALSE>", LittleCalcParser.RULE_expr);
        var matches = pattern.findAll(ctx, "//expr");
        for (var match : matches) {
            var matchCtx = (EqualityExprContext) (match.getTree());
            rewriter.delete(pName,matchCtx.op, matchCtx.rhs.getStop());
        }
    }

    private void plus0(SimplifyStmtContext ctx,String pName) {
        var pattern = parser.compileParseTreePattern("<expr> + 0", LittleCalcParser.RULE_expr);
        var matches = pattern.findAll(ctx, "//expr");
        for (var match : matches) {
            var matchCtx = (EqualityExprContext) (match.getTree());
            rewriter.delete(pName,matchCtx.op, matchCtx.rhs.getStop());
        }
    }

    private void times1(SimplifyStmtContext ctx,String pName) {
        var pattern = parser.compileParseTreePattern("<expr> * 1", LittleCalcParser.RULE_expr);
        var matches = pattern.findAll(ctx, "//expr");
        for (var match : matches) {
            var matchCtx = (EqualityExprContext) (match.getTree());
            rewriter.delete(pName,matchCtx.op, matchCtx.rhs.getStop());
        }
    }
    @Override
    public Void visitPrintVars(PrintVarsContext ctx) {
        dumpVariables();
        return null;
    }

    public void dumpVariables() {
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
