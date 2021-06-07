package net.cargal.littlecalc;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.tinylog.Logger;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class LittleValue implements Comparable<LittleValue> {
    public enum ValueType {
        NUMBER, STRING, BOOLEAN
    }

    private static final Map<ValueType, Set<Integer>> validCompareTypesMap;
    static {
        validCompareTypesMap = new EnumMap<>(ValueType.class);
        validCompareTypesMap.put(ValueType.BOOLEAN, //
                new HashSet<>(Arrays.asList( //
                        LittleCalcLexer.EQ, //
                        LittleCalcLexer.NE)));
        validCompareTypesMap.put(ValueType.STRING, //
                new HashSet<>(Arrays.asList( //
                        LittleCalcLexer.EQ, //
                        LittleCalcLexer.NE, //
                        LittleCalcLexer.LT, //
                        LittleCalcLexer.LE, //
                        LittleCalcLexer.GT, //
                        LittleCalcLexer.GE)));
        validCompareTypesMap.put(ValueType.NUMBER, //
                new HashSet<>(Arrays.asList( //
                        LittleCalcLexer.EQ, //
                        LittleCalcLexer.NE, //
                        LittleCalcLexer.LT, //
                        LittleCalcLexer.LE, //
                        LittleCalcLexer.GT, //
                        LittleCalcLexer.GE)));
    }

    private ValueType vType;
    private Object value;
    private int line;
    private int column;
 
    public int getLine() {
        return line;
    }

     public int getColumn() {
        return column;
    }

    private LittleValue(ValueType vType, Object value, ParserRuleContext ctx) {
        this.vType = vType;
        this.value = value;
        Token tk = ctx.getStart();
        line = tk.getLine();
        column = tk.getCharPositionInLine();
    }

    static LittleValue numberValue(Double dv, ParserRuleContext ctx) {
        return new LittleValue(ValueType.NUMBER, dv, ctx);
    }

    static LittleValue stringValue(String sv, ParserRuleContext ctx) {
        return new LittleValue(ValueType.STRING, sv, ctx);

    }

    static LittleValue booleanValue(boolean bv, ParserRuleContext ctx) {
        return new LittleValue(ValueType.BOOLEAN, Boolean.valueOf(bv), ctx);
    }

    public boolean isBoolean() {
        return vType == ValueType.BOOLEAN;
    }

    public boolean isString() {
        return vType == ValueType.STRING;
    }

    public boolean isNumber() {
        return vType == ValueType.NUMBER;
    }

    public boolean bool() {
        assertion(isBoolean(), "value is not boolean (" + value + ")");
        return ((Boolean) value).booleanValue();
    }

    public Double number() {
        assertion(isNumber(), "value is not a Number (" + value + ")");
        return (Double) value;
    }

    public String string() {
        assertion(isString(), "value is not a String (" + value + ")");
        return (String) value;
    }

    public ValueType type() {
        return vType;
    }

    public String toString() {
        return value.toString();
    }

    public boolean evalCompare(int compareOp, LittleValue rhs) {
        Logger.debug("comparing " + number() + LittleCalcLexer.VOCABULARY.getDisplayName(compareOp) + rhs.number() + "="
                + (number() < rhs.number()));
        assertion(type() == rhs.type(), "Cannot compare " + type() + " to " + rhs.type());
        assertion(validCompareForType(compareOp),
                LittleCalcLexer.VOCABULARY.getDisplayName(compareOp) + " is not valid for " + type() + " values");
        switch (compareOp) {
            case LittleCalcLexer.LT:
                return compareTo(rhs) < 0;
            case LittleCalcLexer.LE:
                return compareTo(rhs) <= 0;
            case LittleCalcLexer.EQ:
                return compareTo(rhs) == 0;
            case LittleCalcLexer.NE:
                return compareTo(rhs) != 0;
            case LittleCalcLexer.GT:
                return compareTo(rhs) > 0;
            case LittleCalcLexer.GE:
                return compareTo(rhs) >= 0;
            default:
                throw new LittleCalcRuntimeException("Unhandled compareOp = " + compareOp, line, column);
        }
    }

    private boolean validCompareForType(int compareOp) {
        return validCompareTypesMap.get(type()).contains(compareOp);
    }

    private void assertion(boolean condition, String message) {
        if (!condition) {
            throw new LittleCalcRuntimeException(message, line, column);
        }
    }

    @Override
    public int compareTo(LittleValue o) {
        switch (type()) {
            case BOOLEAN:
                return bool() == o.bool() ? 0 : 1;
            case STRING:
                return string().compareTo(o.string());
            case NUMBER:
                return number().compareTo(o.number());
            default:
                throw new LittleCalcRuntimeException("Unrecognized value type (" + type() + ")", line, column);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LittleValue))
            return false;
        LittleValue olv = (LittleValue) o;
        switch (type()) {
            case BOOLEAN:
                return bool() == olv.bool();
            case STRING:
                return string().compareTo(olv.string()) == 0;
            case NUMBER:
                return number().compareTo(olv.number()) == 0;
            default:
                throw new LittleCalcRuntimeException("Unrecognized value type (" + type() + ")", line, column);
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
