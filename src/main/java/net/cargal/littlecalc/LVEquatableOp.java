package net.cargal.littlecalc;

import org.antlr.v4.runtime.Token;

import net.cargal.littlecalc.exceptions.LittleCalcImplementationException;

public enum LVEquatableOp {
    EQ, NE;

    public static LVEquatableOp fromToken(Token token) {
        return fromToken(token.getType());
    }

    public static LVEquatableOp fromToken(int tokenType) {
        switch (tokenType) {
            case LittleCalcLexer.EQ:
                return EQ;
            case LittleCalcLexer.NE:
                return NE;
            default:
                throw new LittleCalcImplementationException("invalid Equatable Op (" + tokenType + ")");
        }
    }
}
