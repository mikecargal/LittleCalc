package net.cargal.littlecalc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

class LittleValueTest {
    private static final double D1_0 = 1.0;
    private static final double D2_0 = 2.0;
    private static final double D3_0 = 3.0;

    private static final String AAA = "AAA";
    private static final String BBB = "BBB";
    private static final String CCC = "CCC";

    private final int LINE = 5;
    private final int COLUMN = 20;
    ParserRuleContext mockContext;

    LittleValue lv1;
    LittleValue lv2;
    LittleValue lv2Dup;
    LittleValue lv3;
    LittleValue lvA;
    LittleValue lvB;
    LittleValue lvBDup;
    LittleValue lvC;
    LittleValue lvTrue;
    LittleValue lvTrueDup;
    LittleValue lvFalse;
    LittleValue lvFalseDup;

    @BeforeEach
    public void before() {
        mockContext = new ParserRuleContext();
        var token = new CommonToken(1);
        token.setLine(LINE);
        token.setCharPositionInLine(COLUMN);
        mockContext.start = token;

        lv1 = LittleValue.numberValue(D1_0, mockContext);
        lv2 = LittleValue.numberValue(D2_0, mockContext);
        lv2Dup = LittleValue.numberValue(D2_0, mockContext);
        lv3 = LittleValue.numberValue(D3_0, mockContext);
        lvA = LittleValue.stringValue(AAA, mockContext);
        lvB = LittleValue.stringValue(BBB, mockContext);
        lvBDup = LittleValue.stringValue(BBB, mockContext);
        lvC = LittleValue.stringValue(CCC, mockContext);
        lvTrue = LittleValue.booleanValue(true, mockContext);
        lvTrueDup = LittleValue.booleanValue(true, mockContext);
        lvFalse = LittleValue.booleanValue(false, mockContext);
        lvFalseDup = LittleValue.booleanValue(false, mockContext);
    }

    @Test
    void createsString() {
        assertTrue(lvA.isString());
        assertFalse(lvA.isNumber());
        assertFalse(lvA.isBoolean());
        assertEquals(AAA, lvA.string());
        assertEquals(LINE, lvA.getLine());
        assertEquals(COLUMN, lvA.getColumn());
        assertEquals(AAA, lvA.toString());
        assertThrows(LittleCalcRuntimeException.class, () -> {
            lvA.bool();
        });
        assertThrows(LittleCalcRuntimeException.class, () -> {
            lvA.number();
        });
    }

    @Test
    void createsNumber() {
        assertTrue(lv1.isNumber());
        assertFalse(lv1.isString());
        assertFalse(lv1.isBoolean());
        assertEquals(D1_0, lv1.number());
        assertEquals(LINE, lv1.getLine());
        assertEquals(COLUMN, lv1.getColumn());
        assertEquals("1.0", lv1.toString());
        assertThrows(LittleCalcRuntimeException.class, () -> {
            lv1.bool();
        });
        assertThrows(LittleCalcRuntimeException.class, () -> {
            lv1.string();
        });
    }

    @Test
    void createsBooleanTrue() {
        assertTrue(lvTrue.isBoolean());
        assertFalse(lvTrue.isString());
        assertFalse(lvTrue.isNumber());
        assertEquals(true, lvTrue.bool());
        assertEquals(LINE, lvTrue.getLine());
        assertEquals(COLUMN, lvTrue.getColumn());
        assertEquals("true", lvTrue.toString());
        assertThrows(LittleCalcRuntimeException.class, () -> {
            lvTrue.string();
        });
        assertThrows(LittleCalcRuntimeException.class, () -> {
            lvTrue.number();
        });
    }

    @Test
    void createsBooleanFalse() {
        assertTrue(lvFalse.isBoolean());
        assertFalse(lvFalse.isString());
        assertFalse(lvFalse.isNumber());
        assertEquals(false, lvFalse.bool());
        assertEquals(LINE, lvFalse.getLine());
        assertEquals(COLUMN, lvFalse.getColumn());
        assertEquals("false", lvFalse.toString());
        assertThrows(LittleCalcRuntimeException.class, () -> {
            lvFalse.string();
        });
        assertThrows(LittleCalcRuntimeException.class, () -> {
            lvFalse.number();
        });
    }

    @Test
    void testNumberComparesLT() {
        assertTrue(lv1.evalCompare(LittleCalcLexer.LT, lv2));
        assertFalse(lv2.evalCompare(LittleCalcLexer.LT, lv1));
    }

    @Test
    void testNumberComparesLE() {
        assertTrue(lv1.evalCompare(LittleCalcLexer.LE, lv2));
        assertTrue(lv2.evalCompare(LittleCalcLexer.LE, lv2Dup));
    }

    @Test
    void testNumberComparesEQ() {
        assertTrue(lv2.evalCompare(LittleCalcLexer.EQ, lv2Dup));
        assertFalse(lv3.evalCompare(LittleCalcLexer.EQ, lv2));
    }

    @Test
    void testNumberComparesNE() {
        assertTrue(lv1.evalCompare(LittleCalcLexer.NE, lv2));
        assertFalse(lv2.evalCompare(LittleCalcLexer.NE, lv2Dup));
    }

    @Test
    void testNumberComparesGE() {
        assertTrue(lv3.evalCompare(LittleCalcLexer.GE, lv2));
        assertTrue(lv2.evalCompare(LittleCalcLexer.GE, lv2Dup));
        assertFalse(lv1.evalCompare(LittleCalcLexer.GE, lv2));
    }

    @Test
    void testNumberComparesGT() {
        assertTrue(lv3.evalCompare(LittleCalcLexer.GT, lv2));
        assertFalse(lv2.evalCompare(LittleCalcLexer.GT, lv2Dup));
    }

    @Test
    void testNumberEqual() {

        assertEquals(lv2, lv2Dup);
        assertNotEquals(lv1, lv2);
        assertNotEquals(lvA, lv2);
    }

    @Test
    void testNumberhash() {
        assertEquals(lv2.hashCode(), lv2Dup.hashCode());
        assertNotEquals(lv1.hashCode(), lv2.hashCode());
    }

    @Test
    void testNumberComparesToOther() {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            lvA.evalCompare(LittleCalcLexer.LT, lv1);
        });

        assertEquals("line:5 col:21 -- Cannot compare STRING to NUMBER", ex.getMessage());

        ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            lvA.evalCompare(LittleCalcLexer.LT, lvTrue);
        });

        assertEquals("line:5 col:21 -- Cannot compare STRING to BOOLEAN", ex.getMessage());
    }

    @Test
    void testStringComparesLT() {
        assertTrue(lvA.evalCompare(LittleCalcLexer.LT, lvB));
        assertFalse(lvB.evalCompare(LittleCalcLexer.LT, lvA));
    }

    @Test
    void testStringComparesLE() {
        assertTrue(lvA.evalCompare(LittleCalcLexer.LE, lvB));
        assertTrue(lvB.evalCompare(LittleCalcLexer.LE, lvBDup));
        assertFalse(lvC.evalCompare(LittleCalcLexer.LE, lvB));
    }

    @Test
    void testStringComparesEQ() {
        assertTrue(lvB.evalCompare(LittleCalcLexer.EQ, lvBDup));
        assertFalse(lvC.evalCompare(LittleCalcLexer.EQ, lvB));
    }

    @Test
    void testStringComparesNE() {
        assertTrue(lvA.evalCompare(LittleCalcLexer.NE, lvB));
        assertFalse(lvB.evalCompare(LittleCalcLexer.NE, lvBDup));
    }

    @Test
    void testStringComparesGE() {
        assertTrue(lvC.evalCompare(LittleCalcLexer.GE, lvB));
        assertTrue(lvB.evalCompare(LittleCalcLexer.GE, lvBDup));
        assertFalse(lvA.evalCompare(LittleCalcLexer.GE, lvB));
    }

    @Test
    void testStringComparesGT() {
        assertTrue(lvC.evalCompare(LittleCalcLexer.GT, lvB));
        assertFalse(lvB.evalCompare(LittleCalcLexer.GT, lvBDup));
    }

    @Test
    void testStringEquals() {
        assertEquals(lvB, lvBDup);
        assertNotEquals(lvA, lvB);
        assertNotEquals(lvTrue, lvB);
    }

    @Test
    void testStringHash() {
        assertEquals(lvB.hashCode(), lvBDup.hashCode());
        assertNotEquals(lvA.hashCode(), lvB.hashCode());
    }

    @Test
    void testStringComparesToOther() {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            lvA.evalCompare(LittleCalcLexer.LT, lvTrue);
        });

        assertEquals("line:5 col:21 -- Cannot compare STRING to BOOLEAN", ex.getMessage());

        ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            lvA.evalCompare(LittleCalcLexer.LT, lv1);
        });

        assertEquals("line:5 col:21 -- Cannot compare STRING to NUMBER", ex.getMessage());
    }

    @Test
    void testBoolComparesEQ() {
        assertTrue(lvFalse.evalCompare(LittleCalcLexer.EQ, lvFalseDup));
        assertTrue(lvTrue.evalCompare(LittleCalcLexer.EQ, lvTrueDup));
        assertFalse(lvTrue.evalCompare(LittleCalcLexer.EQ, lvFalse));
    }

    @Test
    void testBoolComparesNE() {
        assertTrue(lvTrue.evalCompare(LittleCalcLexer.NE, lvFalse));
        assertTrue(lvFalse.evalCompare(LittleCalcLexer.NE, lvTrue));
        assertFalse(lvTrue.evalCompare(LittleCalcLexer.NE, lvTrueDup));
        assertFalse(lvFalse.evalCompare(LittleCalcLexer.NE, lvFalseDup));
    }

    @Test
    void testBoolFailsLT() {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            assertTrue(lvTrue.evalCompare(LittleCalcLexer.LT, lvFalse));
        });
        assertEquals("line:5 col:21 -- Comparison operator ('<') is not valid for BOOLEAN values", ex.getMessage());
    }

    @Test
    void testBoolFailsLE() {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            assertTrue(lvTrue.evalCompare(LittleCalcLexer.LE, lvFalse));
        });
        assertEquals("line:5 col:21 -- Comparison operator ('<=') is not valid for BOOLEAN values", ex.getMessage());
    }

    @Test
    void testBoolFailsGE() {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            assertTrue(lvTrue.evalCompare(LittleCalcLexer.GE, lvFalse));
        });
        assertEquals("line:5 col:21 -- Comparison operator ('>=') is not valid for BOOLEAN values", ex.getMessage());
    }

    @Test
    void testBoolFailsGT() {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            assertTrue(lvTrue.evalCompare(LittleCalcLexer.GT, lvFalse));
        });
        assertEquals("line:5 col:21 -- Comparison operator ('>') is not valid for BOOLEAN values", ex.getMessage());
    }

    @Test
    void testBoolEquals() {
        assertEquals(lvFalse, lvFalseDup);
        assertNotEquals(lvTrue, lvFalse);
        assertNotEquals(lvA, lvTrue);
    }

    @Test
    void testBoolhash() {
        assertEquals(lvTrue.hashCode(), lvTrue.hashCode());
        assertNotEquals(lvTrue.hashCode(), lvFalse.hashCode());
    }

    @Test
    void testBoolComparesToOther() {
        assertFalse(lvA.evalCompare(LittleCalcLexer.EQ, lvTrue));
    }

    @Test
    void testBadCompareOp() {
        var lv1 = LittleValue.numberValue(1.0, mockContext);
        var lv2 = LittleValue.numberValue(2.0, mockContext);
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            lv1.evalCompare(LittleCalcLexer.COMMENT, lv2);
        });
        assertEquals("line:5 col:21 -- Comparison operator (COMMENT) is not valid for NUMBER values", ex.getMessage());
    }

    @Test
    void test_EQ_NE_AcrossTypes() {
        assertTrue(lv1.evalCompare(LittleCalcLexer.NE, lvA));
        assertTrue(lv1.evalCompare(LittleCalcLexer.NE, lvTrue));
        assertFalse(lv1.evalCompare(LittleCalcLexer.EQ, lvA));
        assertFalse(lv1.evalCompare(LittleCalcLexer.EQ, lvTrue));
    }
}
