package net.cargal.littlecalc;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.cargal.littlecalc.LittleCalcParser.AddSubExprContext;
import net.cargal.littlecalc.LittleCalcParser.AndExprContext;
import net.cargal.littlecalc.LittleCalcParser.CompareExprContext;
import net.cargal.littlecalc.LittleCalcParser.ExpExprContext;
import net.cargal.littlecalc.LittleCalcParser.FalseExprContext;
import net.cargal.littlecalc.LittleCalcParser.IDExprContext;
import net.cargal.littlecalc.LittleCalcParser.MulDivExprContext;
import net.cargal.littlecalc.LittleCalcParser.NegationExprContext;
import net.cargal.littlecalc.LittleCalcParser.NumberExprContext;
import net.cargal.littlecalc.LittleCalcParser.OrExprContext;
import net.cargal.littlecalc.LittleCalcParser.StringExprContext;
import net.cargal.littlecalc.LittleCalcParser.TernaryExprContext;
import net.cargal.littlecalc.LittleCalcParser.TrueExprContext;
import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class LittleCalcExprVisitor extends LittleCalcBaseVisitor<LittleValue> {
    private SymbolTable variables;

    public LittleCalcExprVisitor(SymbolTable variables) {
        this.variables = variables;
    }

    @Override
    protected LittleValue aggregateResult(LittleValue aggregate, LittleValue nextResult) {
        if (aggregate != null)
            return aggregate;
        return nextResult;
    }

    @Override
    public LittleValue visitIDExpr(IDExprContext ctx) {
        LittleValue idVal = variables.get(ctx.ID().getText());
        assertion(idVal != null, ctx.ID().getText() + " has not been assigned a value", ctx);
        return idVal;
    }

    @Override
    public LittleValue visitExpExpr(ExpExprContext ctx) {
        return LittleValue.numberValue(Math.pow(number(ctx.base), number(ctx.exp)), ctx);
    }

    @Override
    public LittleValue visitMulDivExpr(MulDivExprContext ctx) {
        var res = ctx.op.getType() == LittleCalcLexer.MUL //
                ? number(ctx.lhs) * number(ctx.rhs) //
                : number(ctx.lhs) / number(ctx.rhs);
        return LittleValue.numberValue(res, ctx);
    }

    @Override
    public LittleValue visitAddSubExpr(AddSubExprContext ctx) {
        var res = ctx.op.getType() == LittleCalcLexer.ADD //
                ? number(ctx.lhs) + number(ctx.rhs) //
                : number(ctx.lhs) - number(ctx.rhs);
        return LittleValue.numberValue(res, ctx);
    }

    @Override
    public LittleValue visitTernaryExpr(TernaryExprContext ctx) {
        return bool(ctx.cond) ? visit(ctx.tv) : visit(ctx.fv);
    }

    @Override
    public LittleValue visitCompareExpr(CompareExprContext ctx) {
        return LittleValue.booleanValue( //
                visit(ctx.lhs).evalCompare(LVComparableOp.fromToken(ctx.op), visit(ctx.lhs)), //
                ctx);
    }

    @Override
    public LittleValue visitNegationExpr(NegationExprContext ctx) {
        return LittleValue.booleanValue(!bool(ctx.expr()), ctx);
    }

    @Override
    public LittleValue visitAndExpr(AndExprContext ctx) {
        return LittleValue.booleanValue(bool(ctx.lhs) && bool(ctx.rhs), ctx);
    }

    @Override
    public LittleValue visitOrExpr(OrExprContext ctx) {
        return LittleValue.booleanValue(bool(ctx.lhs) || bool(ctx.rhs), ctx);
    }

    @Override
    public LittleValue visitNumberExpr(NumberExprContext ctx) {
        return LittleValue.numberValue(doubleFromToken(ctx.NUMBER()), ctx);
    }

    @Override
    public LittleValue visitTrueExpr(TrueExprContext ctx) {
        return LittleValue.booleanValue(true, ctx);
    }

    @Override
    public LittleValue visitFalseExpr(FalseExprContext ctx) {
        return LittleValue.booleanValue(false, ctx);
    }

    @Override
    public LittleValue visitStringExpr(StringExprContext ctx) {
        return LittleValue.stringValue(stringFromToken(ctx.STRING()), ctx);
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

    private Double number(ParserRuleContext ctx) {
        return visit(ctx).number();
    }

    private boolean bool(ParserRuleContext ctx) {
        return visit(ctx).bool();
    }

}
