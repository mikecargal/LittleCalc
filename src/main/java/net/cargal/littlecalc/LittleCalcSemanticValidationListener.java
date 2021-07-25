package net.cargal.littlecalc;

import java.util.*;
import java.util.function.Supplier;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.tinylog.Logger;

import net.cargal.littlecalc.LittleCalcParser.AndExprContext;
import net.cargal.littlecalc.LittleCalcParser.AntlrUtilStmtContext;
import net.cargal.littlecalc.LittleCalcParser.NegationExprContext;
import net.cargal.littlecalc.LittleCalcParser.OrExprContext;
import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class LittleCalcSemanticValidationListener extends LittleCalcBaseListener {
    protected final List<String> errorMessages = new ArrayList<>();

    protected final Map<String, LVType> variables = new HashMap<>();
    protected final Deque<LVType> typeStack = new ArrayDeque<>();
    protected int utilLevel = 0;

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
        typeStack.push(Objects.requireNonNullElse(idVal, LVUnknownType.INSTANCE));
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

        assertion(lhs.canCompareTo(rhs), () -> "can not compare " + lhs + " to " + rhs, ctx);
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

    @Override
    public void enterAntlrUtilStmt(AntlrUtilStmtContext ctx) {
        utilLevel++;
    }

    @Override
    public void exitAntlrUtilStmt(AntlrUtilStmtContext ctx) {
        utilLevel--;
    }

    public void reset() {
        errorMessages.clear();
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }

    private void assertNumberType(LVType type, ParserRuleContext ctx) {
        if (skippingValidation()) return;
        assertion(type instanceof LVNumberType, () -> ctx.getText() + " is not numeric", ctx);
    }

    private void assertBooleanType(LVType type, ParserRuleContext ctx) {
        if (skippingValidation()) return;
        assertion(type instanceof LVBooleanType, () -> ctx.getText() + " is not boolean", ctx);
    }

    private void assertion(boolean condition, Supplier<String> messageSupplier, ParserRuleContext ctx) {
        if (skippingValidation()) return;
        if (!condition) {
            Token tk = ctx.getStart();
            var msg = LittleCalcRuntimeException.message(messageSupplier.get(), tk.getLine(),
                    tk.getCharPositionInLine());
            System.out.println(msg);
            errorMessages.add(msg);
        }
    }

    private boolean skippingValidation() {
        return utilLevel != 0;
    }

}
