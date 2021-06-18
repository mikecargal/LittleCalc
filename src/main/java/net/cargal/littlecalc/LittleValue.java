package net.cargal.littlecalc;

import java.util.stream.IntStream;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public abstract class LittleValue implements Comparable<LittleValue> {
    int[] compareOps = { LittleCalcLexer.EQ, //
            LittleCalcLexer.NE, //
            LittleCalcLexer.LT, //
            LittleCalcLexer.LE, //
            LittleCalcLexer.GT, //
            LittleCalcLexer.GE };

    private int line;
    private int column;

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    protected LittleValue(ParserRuleContext ctx) {
        Token tk = ctx.getStart();
        line = tk.getLine();
        column = tk.getCharPositionInLine();
    }

    static LittleValue numberValue(Double dv, ParserRuleContext ctx) {
        return new LVNumber(dv, ctx);
    }

    static LittleValue stringValue(String sv, ParserRuleContext ctx) {
        return new LVString(sv, ctx);

    }

    static LittleValue booleanValue(boolean bv, ParserRuleContext ctx) {
        return new LVBoolean(Boolean.valueOf(bv), ctx);
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    protected abstract Object getValueObject();

    public boolean bool() {
        throw new LittleCalcRuntimeException("value is not boolean (" + getValueObject() + ")", line, column);
    }

    public Double number() {
        throw new LittleCalcRuntimeException("value is not a Number (" + getValueObject() + ")", line, column);
    }

    public String string() {
        throw new LittleCalcRuntimeException("value is not a String (" + getValueObject() + ")", line, column);
    }

    public abstract String type();

    public String toString() {
        return getValueObject().toString();
    }

    public boolean evalCompare(int compareOp, LittleValue rhs) {
        if (compareOp == LittleCalcLexer.EQ)
            return this.equals(rhs);
        if (compareOp == LittleCalcLexer.NE)
            return !this.equals(rhs);

        assertion(this.getClass().equals(rhs.getClass()), "Cannot compare " + type() + " to " + rhs.type());

        assertion(validCompareForType(compareOp), "Comparison operator ("
                + LittleCalcLexer.VOCABULARY.getDisplayName(compareOp) + ") is not valid for " + type() + " values");
        switch (compareOp) {
            case LittleCalcLexer.LT:
                return compareTo(rhs) < 0;
            case LittleCalcLexer.LE:
                return compareTo(rhs) <= 0;
            case LittleCalcLexer.GT:
                return compareTo(rhs) > 0;
            case LittleCalcLexer.GE:
                return compareTo(rhs) >= 0;
            default:
                throw new LittleCalcRuntimeException("Unhandled compareOp = " + compareOp, line, column);
        }
    }

    protected boolean validCompareForType(int compareOp) {
        return IntStream.of(compareOps).anyMatch(c -> c == compareOp);
    }

    private void assertion(boolean condition, String message) {
        if (!condition) {
            throw new LittleCalcRuntimeException(message, line, column);
        }
    }

}
