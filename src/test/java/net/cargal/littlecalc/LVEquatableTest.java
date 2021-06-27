package net.cargal.littlecalc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LVEquatableTest {
    @Test
    void testEQ() {
        assertEquals(LVEquatableOp.EQ, LVEquatableOp.fromTokenType(LittleCalcLexer.EQ));
        assertEquals("==", LVEquatableOp.EQ.getText());
    }

    @Test
    void testNE() {
        assertEquals(LVEquatableOp.NE, LVEquatableOp.fromTokenType(LittleCalcLexer.NE));
        assertEquals("!=", LVEquatableOp.NE.getText());
    }
}
