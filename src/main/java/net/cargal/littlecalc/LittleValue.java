package net.cargal.littlecalc;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.tinylog.Logger;

public class LittleValue {
    public enum ValueType {
        NUMBER, STRING, BOOLEAN
    }

    private ValueType vType;
    private Object value;
    private int line;
    private int column;

    private LittleValue(ValueType vType, Object value, int line, int column) {
        this.vType = vType;
        this.value = value;
        this.line = line;
        this.column = column;
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

    static LittleValue boolValue(boolean bv, ParserRuleContext ctx) {
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
        switch (type()) {
            case NUMBER:
                switch (compareOp) {
                    case LittleCalcLexer.LT:
                        Logger.debug("comparing " + number() + LittleCalcLexer.VOCABULARY.getDisplayName(compareOp)
                                + rhs.number() + "=" + (number() < rhs.number()));
                        return number() < rhs.number();
                    case LittleCalcLexer.LE:
                        return number() <= rhs.number();
                    case LittleCalcLexer.EQ:
                        return number() == rhs.number();
                    case LittleCalcLexer.NE:
                        return number() != rhs.number();
                    case LittleCalcLexer.GT:
                        return number() > rhs.number();
                    case LittleCalcLexer.GE:
                        return number() >= rhs.number();
                    default:
                        throw new LittleCalcRuntimeException("Unrecognized compare operator ("
                                + LittleCalcLexer.VOCABULARY.getDisplayName(compareOp) + ")", line, column);
                }
            case STRING:
                switch (compareOp) {
                    case LittleCalcLexer.LT:
                        return string().compareTo(rhs.string()) < 0;
                    case LittleCalcLexer.LE:
                        return string().compareTo(rhs.string()) <= 0;
                    case LittleCalcLexer.EQ:
                        return string().compareTo(rhs.string()) == 0;
                    case LittleCalcLexer.NE:
                        return string().compareTo(rhs.string()) != 0;
                    case LittleCalcLexer.GT:
                        return string().compareTo(rhs.string()) > 0;
                    case LittleCalcLexer.GE:
                        return string().compareTo(rhs.string()) >= 0;
                    default:
                        throw new LittleCalcRuntimeException("Unrecognized compare operator ("
                                + LittleCalcLexer.VOCABULARY.getDisplayName(compareOp) + ")", line, column);
                }
            case BOOLEAN:
                switch (compareOp) {
                    case LittleCalcLexer.EQ:
                        return string().compareTo(rhs.string()) == 0;
                    case LittleCalcLexer.NE:
                        return string().compareTo(rhs.string()) != 0;
                    default:
                        throw new LittleCalcRuntimeException(
                                "Cannot compare booleans with " + LittleCalcLexer.VOCABULARY.getDisplayName(compareOp),
                                line, column);
                }
            default:
                throw new LittleCalcRuntimeException("Unrecognized value type (" + type() + ")", line, column);
        }
    }

    private void assertion(boolean condition, String message) {
        if (!condition) {
            throw new LittleCalcRuntimeException(message, line, column);
        }
    }

}
