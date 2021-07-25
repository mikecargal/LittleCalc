package net.cargal.littlecalc;

import java.util.function.Supplier;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.cargal.littlecalc.LittleCalcParser.AddSubExprContext;
import net.cargal.littlecalc.LittleCalcParser.AndExprContext;
import net.cargal.littlecalc.LittleCalcParser.CompareExprContext;
import net.cargal.littlecalc.LittleCalcParser.EqualityExprContext;
import net.cargal.littlecalc.LittleCalcParser.ExpExprContext;
import net.cargal.littlecalc.LittleCalcParser.FalseExprContext;
import net.cargal.littlecalc.LittleCalcParser.IDExprContext;
import net.cargal.littlecalc.LittleCalcParser.MulDivExprContext;
import net.cargal.littlecalc.LittleCalcParser.NegationExprContext;
import net.cargal.littlecalc.LittleCalcParser.NumberExprContext;
import net.cargal.littlecalc.LittleCalcParser.OrExprContext;
import net.cargal.littlecalc.LittleCalcParser.ParenExprContext;
import net.cargal.littlecalc.LittleCalcParser.StringExprContext;
import net.cargal.littlecalc.LittleCalcParser.TernaryExprContext;
import net.cargal.littlecalc.LittleCalcParser.TrueExprContext;
import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class LittleCalcExprVisitor extends LittleCalcBaseVisitor<LittleValue> {
    private final SymbolTable<LittleValue> variables;

    public LittleCalcExprVisitor(SymbolTable<LittleValue> variables) {
        this.variables = variables;
    }

    @Override
    public LittleValue visitIDExpr(IDExprContext ctx) {
        var idVal = variables.get(ctx.ID().getText());
        assertion(idVal.isPresent(), () -> ctx.ID().getText() + " has not been assigned a value", ctx);
        return idVal.get();
    }

    @Override
    public LittleValue visitExpExpr(ExpExprContext ctx) {
        return lvNumber(Math.pow(number(ctx.base), number(ctx.exp)), ctx);
    }

    @Override
    public LittleValue visitParenExpr(ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public LittleValue visitMulDivExpr(MulDivExprContext ctx) {
        var res = ctx.op.getType() == LittleCalcLexer.MUL //
                ? number(ctx.lhs) * number(ctx.rhs) //
                : number(ctx.lhs) / number(ctx.rhs);
        return lvNumber(res, ctx);
    }

    @Override
    public LittleValue visitAddSubExpr(AddSubExprContext ctx) {
        var res = ctx.op.getType() == LittleCalcLexer.ADD //
                ? number(ctx.lhs) + number(ctx.rhs) //
                : number(ctx.lhs) - number(ctx.rhs);
        return lvNumber(res, ctx);
    }

    @Override
    public LittleValue visitTernaryExpr(TernaryExprContext ctx) {
        return bool(ctx.cond) ? visit(ctx.tv) : visit(ctx.fv);
    }

    @Override
    public LittleValue visitCompareExpr(CompareExprContext ctx) {
        var res = visit(ctx.lhs).evalCompare(LVComparableOp.fromToken(ctx.op), visit(ctx.rhs));
        return lvBool(res, ctx);
    }

    @Override
    public LittleValue visitEqualityExpr(EqualityExprContext ctx) {
        var res = visit(ctx.lhs).evalEquality(LVEquatableOp.fromToken(ctx.op), visit(ctx.rhs));
        return lvBool(res, ctx);
    }

    @Override
    public LittleValue visitNegationExpr(NegationExprContext ctx) {
        return lvBool(!bool(ctx.expr()), ctx);
    }

    @Override
    public LittleValue visitAndExpr(AndExprContext ctx) {
        return lvBool(bool(ctx.lhs) && bool(ctx.rhs), ctx);
    }

    @Override
    public LittleValue visitOrExpr(OrExprContext ctx) {
        return lvBool(bool(ctx.lhs) || bool(ctx.rhs), ctx);
    }

    @Override
    public LittleValue visitNumberExpr(NumberExprContext ctx) {
        return lvNumber(doubleFromToken(ctx.NUMBER()), ctx);
    }

    @Override
    public LittleValue visitTrueExpr(TrueExprContext ctx) {
        return lvBool(true, ctx);
    }

    @Override
    public LittleValue visitFalseExpr(FalseExprContext ctx) {
        return lvBool(false, ctx);
    }

    @Override
    public LittleValue visitStringExpr(StringExprContext ctx) {
        return LittleValue.stringValue(stringFromToken(ctx.STRING()), ctx);
    }

    private void assertion(boolean condition, Supplier<String> messageSupplier, ParserRuleContext ctx) {
        if (!condition) {
            Token tk = ctx.getStart();
            throw new LittleCalcRuntimeException(messageSupplier.get(), tk.getLine(), tk.getCharPositionInLine());
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

    private LittleValue lvNumber(double dv, ParserRuleContext ctx) {
        return LittleValue.numberValue(dv, ctx);
    }

    private LittleValue lvBool(boolean bv, ParserRuleContext ctx) {
        return LittleValue.booleanValue(bv, ctx);
    }

}
