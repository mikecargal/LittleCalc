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

import net.cargal.littlecalc.exceptions.LittleCalcImplementationException;
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
        lv2 = LittleValue.numberValue(D2_0, LINE, COLUMN);
        lv2Dup = LittleValue.numberValue(D2_0, LINE, COLUMN);
        lv3 = LittleValue.numberValue(D3_0, LINE, COLUMN);
        lvA = LittleValue.stringValue(AAA, mockContext);
        lvB = LittleValue.stringValue(BBB, LINE, COLUMN);
        lvBDup = LittleValue.stringValue(BBB, LINE, COLUMN);
        lvC = LittleValue.stringValue(CCC, LINE, COLUMN);
        lvTrue = LittleValue.booleanValue(true, mockContext);
        lvTrueDup = LittleValue.booleanValue(true, LINE, COLUMN);
        lvFalse = LittleValue.booleanValue(false, LINE, COLUMN);
        lvFalseDup = LittleValue.booleanValue(false, LINE, COLUMN);
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
    void unknownType() {
        assertThrows(LittleCalcImplementationException.class, () -> {
            LVUnknownType.INSTANCE.canCompareTo(LVNumberType.INSTANCE);
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
        assertThrows(LittleCalcImplementationException.class, () -> {
            lvTrue.type().canCompareTo(lvFalse.type());
        });
        assertThrows(LittleCalcImplementationException.class, () -> {
            lvTrue.compareTo(lvFalse);
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
        assertTrue(lv1.evalCompare(LVComparableOp.LT, lv2));
        assertFalse(lv2.evalCompare(LVComparableOp.LT, lv1));
    }

    @Test
    void testNumberComparesLE() {
        assertTrue(lv1.evalCompare(LVComparableOp.LE, lv2));
        assertTrue(lv2.evalCompare(LVComparableOp.LE, lv2Dup));
    }

    @Test
    void testNumberComparesEQ() {
        assertTrue(lv2.evalEquality(LVEquatableOp.EQ, lv2Dup));
        assertFalse(lv3.evalEquality(LVEquatableOp.EQ, lv2));
    }

    @Test
    void testNumberComparesNE() {
        assertTrue(lv1.evalEquality(LVEquatableOp.NE, lv2));
        assertFalse(lv2.evalEquality(LVEquatableOp.NE, lv2Dup));
    }

    @Test
    void testNumberComparesGE() {
        assertTrue(lv3.evalCompare(LVComparableOp.GE, lv2));
        assertTrue(lv2.evalCompare(LVComparableOp.GE, lv2Dup));
        assertFalse(lv1.evalCompare(LVComparableOp.GE, lv2));
    }

    @Test
    void testNumberComparesGT() {
        assertTrue(lv3.evalCompare(LVComparableOp.GT, lv2));
        assertFalse(lv2.evalCompare(LVComparableOp.GT, lv2Dup));
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
            lvA.evalCompare(LVComparableOp.LT, lv1);
        });

        assertEquals("line:5 col:21 -- Cannot compare STRING to NUMBER", ex.getMessage());

        ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            lvA.evalCompare(LVComparableOp.LT, lvTrue);
        });

        assertEquals("line:5 col:21 -- Cannot compare STRING to BOOLEAN", ex.getMessage());
    }

    @Test
    void testStringComparesLT() {
        assertTrue(lvA.evalCompare(LVComparableOp.LT, lvB));
        assertFalse(lvB.evalCompare(LVComparableOp.LT, lvA));
    }

    @Test
    void testStringComparesLE() {
        assertTrue(lvA.evalCompare(LVComparableOp.LE, lvB));
        assertTrue(lvB.evalCompare(LVComparableOp.LE, lvBDup));
        assertFalse(lvC.evalCompare(LVComparableOp.LE, lvB));
    }

    @Test
    void testStringComparesEQ() {
        assertTrue(lvB.evalEquality(LVEquatableOp.EQ, lvBDup));
        assertFalse(lvC.evalEquality(LVEquatableOp.EQ, lvB));
    }

    @Test
    void testStringComparesNE() {
        assertTrue(lvA.evalEquality(LVEquatableOp.NE, lvB));
        assertFalse(lvB.evalEquality(LVEquatableOp.NE, lvBDup));
    }

    @Test
    void testStringComparesGE() {
        assertTrue(lvC.evalCompare(LVComparableOp.GE, lvB));
        assertTrue(lvB.evalCompare(LVComparableOp.GE, lvBDup));
        assertFalse(lvA.evalCompare(LVComparableOp.GE, lvB));
    }

    @Test
    void testStringComparesGT() {
        assertTrue(lvC.evalCompare(LVComparableOp.GT, lvB));
        assertFalse(lvB.evalCompare(LVComparableOp.GT, lvBDup));
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
            lvA.evalCompare(LVComparableOp.LT, lvTrue);
        });

        assertEquals("line:5 col:21 -- Cannot compare STRING to BOOLEAN", ex.getMessage());

        ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            lvA.evalCompare(LVComparableOp.LT, lv1);
        });

        assertEquals("line:5 col:21 -- Cannot compare STRING to NUMBER", ex.getMessage());
    }

    @Test
    void testBoolComparesEQ() {
        assertTrue(lvFalse.evalEquality(LVEquatableOp.EQ, lvFalseDup));
        assertTrue(lvTrue.evalEquality(LVEquatableOp.EQ, lvTrueDup));
        assertFalse(lvTrue.evalEquality(LVEquatableOp.EQ, lvFalse));
    }

    @Test
    void testBoolComparesNE() {
        assertTrue(lvTrue.evalEquality(LVEquatableOp.NE, lvFalse));
        assertTrue(lvFalse.evalEquality(LVEquatableOp.NE, lvTrue));
        assertFalse(lvTrue.evalEquality(LVEquatableOp.NE, lvTrueDup));
        assertFalse(lvFalse.evalEquality(LVEquatableOp.NE, lvFalseDup));
    }

    @Test
    void testBoolFailsLT() {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            assertTrue(lvTrue.evalCompare(LVComparableOp.LT, lvFalse));
        });
        assertEquals("line:5 col:21 -- Comparison operator ('<') is not valid for BOOLEAN values", ex.getMessage());
    }

    @Test
    void testBoolFailsLE() {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            assertTrue(lvTrue.evalCompare(LVComparableOp.LE, lvFalse));
        });
        assertEquals("line:5 col:21 -- Comparison operator ('<=') is not valid for BOOLEAN values", ex.getMessage());
    }

    @Test
    void testBoolFailsGE() throws Exception {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            lvTrue.evalCompare(LVComparableOp.GE, lvFalse);
        });
        assertEquals("line:5 col:21 -- Comparison operator ('>=') is not valid for BOOLEAN values", ex.getMessage());
    }

    @Test
    void testBoolFailsGT() {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            assertTrue(lvTrue.evalCompare(LVComparableOp.GT, lvFalse));
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
        assertFalse(lvA.evalEquality(LVEquatableOp.EQ, lvTrue));
    }

    @Test
    void testBadCompareOp() {
        var ex = assertThrows(LittleCalcImplementationException.class, () -> {
            LVComparableOp.fromTokenType(LittleCalcLexer.ADD);
        });
        assertEquals("Invalid Comparable Op (" + LittleCalcLexer.ADD
                + ") (this is an indication of an implementation error, not user error)", ex.getMessage());
    }

    @Test
    void testBadEquatableOp() {
        var ex = assertThrows(LittleCalcImplementationException.class, () -> {
            LVEquatableOp.fromTokenType(LittleCalcLexer.DIV);
        });
        assertEquals("Invalid Equatable Op (" + LittleCalcLexer.DIV
                + ") (this is an indication of an implementation error, not user error)", ex.getMessage());
    }

    @Test
    void test_EQ_NE_AcrossTypes() {
        assertTrue(lv1.evalEquality(LVEquatableOp.NE, lvA));
        assertTrue(lv1.evalEquality(LVEquatableOp.NE, lvTrue));
        assertFalse(lv1.evalEquality(LVEquatableOp.EQ, lvA));
        assertFalse(lv1.evalEquality(LVEquatableOp.EQ, lvTrue));
    }
}
