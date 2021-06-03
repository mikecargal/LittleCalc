package net.cargal.littlecalc;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.tinylog.Logger;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class LittleCalcInterpListener extends LittleCalcBaseListener {
    private Map<String, LittleValue> variables = new HashMap<>();
    private Deque<LittleValue> stack = new ArrayDeque<>();

    @Override
    public void exitAssignmentStmt(LittleCalcParser.AssignmentStmtContext ctx) {
        var val = stack.pop();
        variables.put(ctx.ID().getText(), val);
        Logger.debug("assignment to " + ctx.ID().getText());
        dumpStack();
        dumpVariables();
    }

    @Override
    public void exitPrintStmt(LittleCalcParser.PrintStmtContext ctx) {
        int itemCount = ctx.expr().size();
        Logger.debug("printing " + itemCount + " items.");
        Deque<LittleValue> pStack = new ArrayDeque<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            pStack.push(stack.pop());
        }
        pStack.stream().forEach(System.out::print);
        System.out.println();
        System.out.flush();
    }

    @Override
    public void exitIDExpr(LittleCalcParser.IDExprContext ctx) {
        LittleValue idVal = variables.get(ctx.ID().getText());
        assertion(idVal != null, ctx.ID().getText() + " has not been assigned a value", ctx);
        stack.push(idVal);
        debugExpr(ctx);
    }

    @Override
    public void exitMulDivExpr(LittleCalcParser.MulDivExprContext ctx) {
        var rhs = stack.pop().number();
        var lhs = stack.pop().number();
        var res = ctx.op.getType() == LittleCalcLexer.MUL ? lhs * rhs : lhs / rhs;
        stack.push(LittleValue.numberValue(res, ctx));
        debugExpr(ctx);
    }

    @Override
    public void exitCompareExpr(LittleCalcParser.CompareExprContext ctx) {
        var rhs = stack.pop();
        var lhs = stack.pop();
        stack.push(LittleValue.boolValue(lhs.evalCompare(ctx.op.getType(), rhs), ctx));
        debugExpr(ctx);
    }

    @Override
    public void exitExpExpr(LittleCalcParser.ExpExprContext ctx) {
        var base = stack.pop().number();
        var exp = stack.pop().number();
        stack.push(LittleValue.numberValue(Math.pow(base, exp), ctx));
        debugExpr(ctx);
    }

    @Override
    public void exitAddSubExpr(LittleCalcParser.AddSubExprContext ctx) {
        var rhs = stack.pop().number();
        var lhs = stack.pop().number();
        var res = ctx.op.getType() == LittleCalcLexer.ADD ? lhs + rhs : lhs - rhs;
        stack.push(LittleValue.numberValue(res, ctx));
        debugExpr(ctx);
    }

    @Override
    public void exitTernaryExpr(LittleCalcParser.TernaryExprContext ctx) {
        LittleValue fVal = stack.pop();
        LittleValue tVal = stack.pop();
        LittleValue condition = stack.pop();
        LittleValue res = condition.bool() ? tVal : fVal;
        stack.push(res);
        // throw new LittleCalcRuntimeException("why not", 1, 1);
        dumpStack();
        debugExpr(ctx);
    }

    @Override
    public void exitNumberExpr(LittleCalcParser.NumberExprContext ctx) {
        stack.push(LittleValue.numberValue(doubleFromToken(ctx.NUMBER()), ctx));
        debugExpr(ctx);
    }

    @Override
    public void exitTrueExpr(LittleCalcParser.TrueExprContext ctx) {
        stack.push(LittleValue.boolValue(true, ctx));
        debugExpr(ctx);
    }

    @Override
    public void exitFalseExpr(LittleCalcParser.FalseExprContext ctx) {
        stack.push(LittleValue.boolValue(false, ctx));
        debugExpr(ctx);
    }

    @Override
    public void exitStringExpr(LittleCalcParser.StringExprContext ctx) {
        stack.push(LittleValue.stringValue(stringFromToken(ctx.STRING()), ctx));
        debugExpr(ctx);
    }

    private Double doubleFromToken(TerminalNode token) {
        String tokenText = token.getText();
        return Double.valueOf(tokenText.replace("_", ""));
    }

    private String stringFromToken(TerminalNode token) {
        String tokenText = token.getText();
        String trimmed = tokenText.substring(1, tokenText.length() - 1);
        return trimmed.replace("\\\"", "\"").replace("\\'", "'");
    }

    public void dumpVariables() {
        Logger.debug("Variables:");
        for (Entry<String, LittleValue> entry : variables.entrySet()) {
            Logger.debug("\t" + entry.getKey() + " : " + entry.getValue());
        }
    }

    private void debugExpr(LittleCalcParser.ExprContext expr) {
        Logger.debug(expr.getText());
        dumpStack();
    }

    private void dumpStack() {
        Logger.debug("---- Stack ----");
        stack.stream().forEach(Logger::debug);
        Logger.debug("-----------------");
    }

    private void assertion(boolean condition, String message, ParserRuleContext ctx) {
        if (!condition) {
            Token tk = ctx.getStart();
            throw new LittleCalcRuntimeException(message, tk.getLine(), tk.getCharPositionInLine());
        }
    }
}
