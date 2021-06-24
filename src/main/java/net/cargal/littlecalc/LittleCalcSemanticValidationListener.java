package net.cargal.littlecalc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.tinylog.Logger;

import net.cargal.littlecalc.LittleCalcParser.AndExprContext;
import net.cargal.littlecalc.LittleCalcParser.NegationExprContext;
import net.cargal.littlecalc.LittleCalcParser.OrExprContext;
import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class LittleCalcSemanticValidationListener extends LittleCalcBaseListener {
    protected List<String> errorMessages = new ArrayList<>();

    protected Map<String, LVType> variables = new HashMap<>();
    protected Deque<LVType> typeStack = new ArrayDeque<>();

    @Override
    public void exitAssignmentStmt(LittleCalcParser.AssignmentStmtContext ctx) {
        var val = typeStack.pop();

        var id = ctx.ID().getText();
        variables.put(id, val);
        Logger.debug("assignment to " + id);
    }

    @Override
    public void exitIDExpr(LittleCalcParser.IDExprContext ctx) {
        var idVal = variables.get(ctx.ID().getText());
        assertion(idVal != null, () -> ctx.ID().getText() + " has not been assigned a value", ctx);
        if (idVal == null) {
            typeStack.push(LVUnknownType.INSTANCE);
        } else {
            typeStack.push(idVal);
        }
    }

    @Override
    public void exitMulDivExpr(LittleCalcParser.MulDivExprContext ctx) {
        var rhs = typeStack.pop();
        var lhs = typeStack.pop();

        assertNumberType(lhs, ctx.lhs);
        assertNumberType(rhs, ctx.rhs);
        typeStack.push(LVNumberType.INSTANCE);
    }

    @Override
    public void exitCompareExpr(LittleCalcParser.CompareExprContext ctx) {
        var rhs = typeStack.pop();
        var lhs = typeStack.pop();

        assert (lhs.canCompareTo(rhs));
        typeStack.push(LVBooleanType.INSTANCE);
    }

    @Override
    public void exitNegationExpr(NegationExprContext ctx) {
        assertBooleanType(typeStack.pop(), ctx.expr());
        typeStack.push(LVBooleanType.INSTANCE);
    }

    @Override
    public void exitAndExpr(AndExprContext ctx) {
        var rhs = typeStack.pop();
        var lhs = typeStack.pop();

        assertBooleanType(lhs, ctx.lhs);
        assertBooleanType(rhs, ctx.rhs);
        typeStack.push(LVBooleanType.INSTANCE);
    }

    @Override
    public void exitOrExpr(OrExprContext ctx) {
        var rhs = typeStack.pop();
        var lhs = typeStack.pop();

        assertBooleanType(lhs, ctx.lhs);
        assertBooleanType(rhs, ctx.rhs);
        typeStack.push(LVBooleanType.INSTANCE);
    }

    @Override
    public void exitExpExpr(LittleCalcParser.ExpExprContext ctx) {
        var exp = typeStack.pop();
        var base = typeStack.pop();

        assertNumberType(base, ctx.base);
        assertNumberType(exp, ctx.exp);
        typeStack.push(LVNumberType.INSTANCE);
    }

    @Override
    public void exitAddSubExpr(LittleCalcParser.AddSubExprContext ctx) {
        var rhs = typeStack.pop();
        var lhs = typeStack.pop();

        assertNumberType(lhs, ctx.lhs);
        assertNumberType(rhs, ctx.rhs);
        typeStack.push(LVNumberType.INSTANCE);
    }

    @Override
    public void exitTernaryExpr(LittleCalcParser.TernaryExprContext ctx) {
        var tType = typeStack.pop();
        var fType = typeStack.pop();
        var cond = typeStack.pop();

        assertion(tType.equals(fType), //
                () -> "true and false branches must share the same type (" + tType + "," + fType + ")", //
                ctx);
        assertBooleanType(cond, ctx.cond);
        typeStack.push(tType);
    }

    @Override
    public void exitNumberExpr(LittleCalcParser.NumberExprContext ctx) {
        typeStack.push(LVNumberType.INSTANCE);
    }

    @Override
    public void exitTrueExpr(LittleCalcParser.TrueExprContext ctx) {
        typeStack.push(LVBooleanType.INSTANCE);
    }

    @Override
    public void exitFalseExpr(LittleCalcParser.FalseExprContext ctx) {
        typeStack.push(LVBooleanType.INSTANCE);
    }

    @Override
    public void exitStringExpr(LittleCalcParser.StringExprContext ctx) {
        typeStack.push(LVStringType.INSTANCE);
    }

    public void reset() {
        errorMessages.clear();
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }

    private void assertNumberType(LVType type, ParserRuleContext ctx) {
        assertion(type instanceof LVNumberType, () -> ctx.getText() + " is not numeric", ctx);
    }

    private void assertBooleanType(LVType type, ParserRuleContext ctx) {
        assertion(type instanceof LVBooleanType, () -> ctx.getText() + " is not boolean", ctx);
    }

    private void assertion(boolean condition, Supplier<String> messageSupplier, ParserRuleContext ctx) {
        if (!condition) {
            Token tk = ctx.getStart();
            var msg = LittleCalcRuntimeException.message(messageSupplier.get(), tk.getLine(),
                    tk.getCharPositionInLine());
            System.out.println(msg);
            errorMessages.add(msg);
        }
    }

}
