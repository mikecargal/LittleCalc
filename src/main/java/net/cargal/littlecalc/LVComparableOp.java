package net.cargal.littlecalc;

import org.antlr.v4.runtime.Token;

import net.cargal.littlecalc.exceptions.LittleCalcImplementationException;

public enum LVComparableOp {
    LT("<"), LE("<="), GT(">"), GE(">=");

    private String text;

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
        switch (tokenType) {
            case LittleCalcLexer.LT:
                return LT;
            case LittleCalcLexer.LE:
                return LE;
            case LittleCalcLexer.GT:
                return GT;
            case LittleCalcLexer.GE:
                return GE;
            default:
                throw new LittleCalcImplementationException("Invalid Comparable Op (" + tokenType + ")");
        }
    }
}
