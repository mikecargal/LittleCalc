package net.cargal.littlecalc;

import net.cargal.littlecalc.exceptions.LittleCalcImplementationException;

public enum LVComparableOp {
    LT, LE, GT, GE;

    static LVComparableOp fromToken(int tokenType) {
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
                throw new LittleCalcImplementationException("invalid Comparable Op (" + tokenType + ")");
        }
    }
}
