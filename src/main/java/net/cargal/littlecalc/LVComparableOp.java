package net.cargal.littlecalc;

import org.antlr.v4.runtime.Token;

import net.cargal.littlecalc.exceptions.LittleCalcImplementationException;

public enum LVComparableOp {
    LT("<"), LE("<="), GT(">"), GE(">=");

    private final String text;

    LVComparableOp(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static LVComparableOp fromToken(Token token) {
        return fromTokenType(token.getType());
    }

    public static LVComparableOp fromTokenType(int tokenType) {
        return switch (tokenType) {
            case LittleCalcLexer.LT -> LT;
            case LittleCalcLexer.LE -> LE;
            case LittleCalcLexer.GT -> GT;
            case LittleCalcLexer.GE -> GE;
            default -> throw new LittleCalcImplementationException("Invalid Comparable Op (" + tokenType + ")");
        };
    }
}
