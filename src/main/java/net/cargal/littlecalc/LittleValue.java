package net.cargal.littlecalc;

import java.util.function.Supplier;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public abstract class LittleValue implements Comparable<LittleValue> {

    protected int line;
    protected int column;

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    protected LittleValue(int line, int column) {
        this.line = line;
        this.column = column;
    }

    static LittleValue numberValue(Double dv, ParserRuleContext ctx) {
        Token tk = ctx.getStart();
        return new LVNumber(dv, tk.getLine(), tk.getCharPositionInLine());
    }

    static LittleValue stringValue(String sv, ParserRuleContext ctx) {
        Token tk = ctx.getStart();
        return new LVString(sv, tk.getLine(), tk.getCharPositionInLine());
    }

    static LittleValue booleanValue(boolean bv, ParserRuleContext ctx) {
        Token tk = ctx.getStart();
        return new LVBoolean(Boolean.valueOf(bv), tk.getLine(), tk.getCharPositionInLine());
    }

    static LittleValue numberValue(Double dv, int line, int column) {
        return new LVNumber(dv, line, column);
    }

    static LittleValue stringValue(String sv, int line, int column) {
        return new LVString(sv, line, column);
    }

    static LittleValue booleanValue(boolean bv, int line, int column) {
        return new LVBoolean(Boolean.valueOf(bv), line, column);
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

    public abstract LVType type();

    public String toString() {
        return getValueObject().toString();
    }

    public boolean evalCompare(LVComparableOp op, LittleValue rhs) {
        assertion(canCompareTo(rhs), () -> "Cannot compare " + this.type() + " to " + rhs.type());
        return switch (op) {
            case LT:
                yield compareTo(rhs) < 0;
            case LE:
                yield compareTo(rhs) <= 0;
            case GT:
                yield compareTo(rhs) > 0;
            case GE:
                yield compareTo(rhs) >= 0;
        };
    }

    public boolean evalEquality(LVEquatableOp op, LittleValue rhs) {
        return ((op == LVEquatableOp.EQ) ? equals(rhs) : !equals(rhs));
    }

    private void assertion(boolean condition, Supplier<String> messageSupplier) {
        if (!condition) {
            throw new LittleCalcRuntimeException(messageSupplier.get(), line, column);
        }
    }

    private boolean canCompareTo(LittleValue other) {
        return this.type().canCompareTo(other.type());
    }

}
