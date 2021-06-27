package net.cargal.littlecalc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LVComparableTest {

    @Test
    void testLT() {
        assertEquals(LVComparableOp.LT, LVComparableOp.fromTokenType(LittleCalcLexer.LT));
        assertEquals("<", LVComparableOp.LT.getText());
    }

    @Test
    void testLE() {
        assertEquals(LVComparableOp.LE, LVComparableOp.fromTokenType(LittleCalcLexer.LE));
        assertEquals("<=", LVComparableOp.LE.getText());
    }

    @Test
    void testGT() {
        assertEquals(LVComparableOp.GT, LVComparableOp.fromTokenType(LittleCalcLexer.GT));
        assertEquals(">", LVComparableOp.GT.getText());
    }

    @Test
    void testGE() {
        assertEquals(LVComparableOp.GE, LVComparableOp.fromTokenType(LittleCalcLexer.GE));
        assertEquals(">=", LVComparableOp.GE.getText());
    }
}
