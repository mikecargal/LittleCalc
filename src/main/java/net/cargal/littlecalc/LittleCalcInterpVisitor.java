package net.cargal.littlecalc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.tinylog.Logger;

import net.cargal.littlecalc.LittleCalcParser.AddSubExprContext;
import net.cargal.littlecalc.LittleCalcParser.AndExprContext;
import net.cargal.littlecalc.LittleCalcParser.NegationExprContext;
import net.cargal.littlecalc.LittleCalcParser.NumberExprContext;
import net.cargal.littlecalc.LittleCalcParser.OrExprContext;
import net.cargal.littlecalc.LittleCalcParser.PrintStmtContext;
import net.cargal.littlecalc.LittleCalcParser.PrintVarsContext;
import net.cargal.littlecalc.LittleCalcParser.StringExprContext;
import net.cargal.littlecalc.LittleCalcParser.TernaryExprContext;
import net.cargal.littlecalc.LittleCalcParser.TrueExprContext;
import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

import net.cargal.littlecalc.LittleCalcParser.AssignmentStmtContext;
import net.cargal.littlecalc.LittleCalcParser.CompareExprContext;
import net.cargal.littlecalc.LittleCalcParser.ExpExprContext;
import net.cargal.littlecalc.LittleCalcParser.FalseExprContext;
import net.cargal.littlecalc.LittleCalcParser.IDExprContext;
import net.cargal.littlecalc.LittleCalcParser.MulDivExprContext;

public class LittleCalcInterpVisitor extends LittleCalcBaseVisitor<Void> {
    protected Map<String, LittleValue> variables = new HashMap<>();
    protected Deque<LittleValue> stack = new ArrayDeque<>();
    protected boolean debugging;
    protected Parser parser;

    @Override
    public Void visitAssignmentStmt(AssignmentStmtContext ctx) {
        super.visitAssignmentStmt(ctx);
        debugEval(ctx);
        var val = stack.pop();
        var id = ctx.ID().getText();
        variables.put(id, val);
        Logger.debug("assignment to " + id);
        return null;
    }

    @Override
    public Void visitPrintStmt(PrintStmtContext ctx) {
        super.visitPrintStmt(ctx);
        debugEval(ctx);
        int itemCount = ctx.expr().size();
        Logger.debug("printing " + itemCount + " items.");
        Deque<LittleValue> pStack = new ArrayDeque<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            pStack.push(stack.pop());
        }
        pStack.stream().forEach(System.out::print);
        System.out.println();
        return null;
    }

    @Override
    public Void visitPrintVars(PrintVarsContext ctx) {
        debugEval(ctx);
        dumpVariables();
        return null;
    }

    @Override
    public Void visitIDExpr(IDExprContext ctx) {
        debugEval(ctx);
        LittleValue idVal = variables.get(ctx.ID().getText());
        assertion(idVal != null, ctx.ID().getText() + " has not been assigned a value", ctx);
        stack.push(idVal);
        return null;
    }

    @Override
    public Void visitExpExpr(ExpExprContext ctx) {
        super.visitExpExpr(ctx);
        debugEval(ctx);
        var exp = stack.pop().number();
        var base = stack.pop().number();
        stack.push(LittleValue.numberValue(Math.pow(base, exp), ctx));
        return null;
    }

    @Override
    public Void visitMulDivExpr(MulDivExprContext ctx) {
        super.visitMulDivExpr(ctx);
        debugEval(ctx);
        var rhs = stack.pop().number();
        var lhs = stack.pop().number();
        var res = ctx.op.getType() == LittleCalcLexer.MUL ? lhs * rhs : lhs / rhs;
        stack.push(LittleValue.numberValue(res, ctx));
        return null;
    }

    @Override
    public Void visitAddSubExpr(AddSubExprContext ctx) {
        super.visitAddSubExpr(ctx);
        debugEval(ctx);
        var rhs = stack.pop().number();
        var lhs = stack.pop().number();
        var res = ctx.op.getType() == LittleCalcLexer.ADD ? lhs + rhs : lhs - rhs;
        stack.push(LittleValue.numberValue(res, ctx));
        return null;
    }

    @Override
    public Void visitTernaryExpr(TernaryExprContext ctx) {
        super.visitTernaryExpr(ctx);
        debugEval(ctx);
        LittleValue fVal = stack.pop();
        LittleValue tVal = stack.pop();
        LittleValue condition = stack.pop();
        LittleValue res = condition.bool() ? tVal : fVal;
        stack.push(res);
        return null;
    }

    @Override
    public Void visitCompareExpr(CompareExprContext ctx) {
        super.visitCompareExpr(ctx);
        debugEval(ctx);
        var rhs = stack.pop();
        var lhs = stack.pop();
        stack.push(LittleValue.booleanValue(lhs.evalCompare(ctx.op.getType(), rhs), ctx));
        return null;
    }

    @Override
    public Void visitNegationExpr(NegationExprContext ctx) {
        super.visitNegationExpr(ctx);
        debugEval(ctx);
        stack.push(LittleValue.booleanValue(!stack.pop().bool(), ctx));
        return null;
    }

    @Override
    public Void visitAndExpr(AndExprContext ctx) {
        super.visitAndExpr(ctx);
        debugEval(ctx);
        var rhs = stack.pop();
        var lhs = stack.pop();
        stack.push(LittleValue.booleanValue(lhs.bool() && rhs.bool(), ctx));
        return null;
    }

    @Override
    public Void visitOrExpr(OrExprContext ctx) {
        super.visitOrExpr(ctx);
        debugEval(ctx);
        var rhs = stack.pop();
        var lhs = stack.pop();
        stack.push(LittleValue.booleanValue(lhs.bool() || rhs.bool(), ctx));
        return null;
    }

    @Override
    public Void visitNumberExpr(NumberExprContext ctx) {
        debugEval(ctx);
        stack.push(LittleValue.numberValue(doubleFromToken(ctx.NUMBER()), ctx));
        return null;
    }

    @Override
    public Void visitTrueExpr(TrueExprContext ctx) {
        debugEval(ctx);
        stack.push(LittleValue.booleanValue(true, ctx));
        return null;
    }

    @Override
    public Void visitFalseExpr(FalseExprContext ctx) {
        debugEval(ctx);
        stack.push(LittleValue.booleanValue(false, ctx));
        return null;
    }

    @Override
    public Void visitStringExpr(StringExprContext ctx) {
        debugEval(ctx);
        stack.push(LittleValue.stringValue(stringFromToken(ctx.STRING()), ctx));
        return null;
    }

    public void dumpVariables() {
        for (Entry<String, LittleValue> entry : variables.entrySet()) {
            System.out.println("\t" + entry.getKey() + " : " + entry.getValue());
        }
    }

    public LittleValue getVar(String varID) {
        return variables.get(varID);
    }

    private void assertion(boolean condition, String message, ParserRuleContext ctx) {
        if (!condition) {
            Token tk = ctx.getStart();
            throw new LittleCalcRuntimeException(message, tk.getLine(), tk.getCharPositionInLine());
        }
    }

    private Double doubleFromToken(TerminalNode token) {
        String tokenText = token.getText();
        return Double.valueOf(tokenText.replace("_", ""));
    }

    private String stringFromToken(TerminalNode token) {
        String tokenText = token.getText();
        var trimmed = tokenText.substring(1, tokenText.length() - 1);
        return trimmed.replace("\\\"", "\"").replace("\\'", "'");
    }

    private void debugEval(ParserRuleContext ctx) {
        if (debugging) {
            System.out.println("evaluating: " + ctx.getText());
            System.out.println(ctx.toStringTree(parser));
            System.out.println(stack);
        }
    }
}
