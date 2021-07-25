package net.cargal.littlecalc;

import org.antlr.v4.runtime.Token;

import net.cargal.littlecalc.exceptions.LittleCalcImplementationException;

public enum LVEquatableOp {
    EQ("=="), NE("!=");

    private final String text;

    LVEquatableOp(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static LVEquatableOp fromToken(Token token) {
        return fromTokenType(token.getType());
    }

    public static LVEquatableOp fromTokenType(int tokenType) {
        return switch (tokenType) {
            case LittleCalcLexer.EQ -> EQ;
            case LittleCalcLexer.NE -> NE;
            default -> throw new LittleCalcImplementationException("Invalid Equatable Op (" + tokenType + ")");
        };
    }
}
