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

public class LittleValueTest {
    private final int LINE = 5;
    private final int COLUMN = 20;
    ParserRuleContext mockContext;

    @BeforeEach
    public void before() {
        mockContext = new ParserRuleContext();
        var token = new CommonToken(1);
        token.setLine(LINE);
        token.setCharPositionInLine(COLUMN);
        mockContext.start = token;
    }

    @Test
    public void createsString() {
        var testString = "LittleStringValue";
        var lv = LittleValue.stringValue(testString, mockContext);
        assertEquals(LittleValue.ValueType.STRING, lv.type());
        assertTrue(lv.isString());
        assertFalse(lv.isNumber());
        assertFalse(lv.isBoolean());
        assertEquals(testString, lv.string());
        assertEquals(LINE, lv.getLine());
        assertEquals(COLUMN, lv.getColumn());
        assertEquals(testString, lv.toString());
    }

    @Test
    public void createsNumber() {
        var testValue = Double.valueOf(99.9);
        var lv = LittleValue.numberValue(testValue, mockContext);
        assertEquals(LittleValue.ValueType.NUMBER, lv.type());
        assertTrue(lv.isNumber());
        assertFalse(lv.isString());
        assertFalse(lv.isBoolean());
        assertEquals(testValue, lv.number());
        assertEquals(LINE, lv.getLine());
        assertEquals(COLUMN, lv.getColumn());
        assertEquals("99.9", lv.toString());
    }

    @Test
    public void createsBoolean() {
        var testBool = true;
        var lv = LittleValue.booleanValue(testBool, mockContext);
        assertEquals(LittleValue.ValueType.BOOLEAN, lv.type());
        assertTrue(lv.isBoolean());
        assertFalse(lv.isString());
        assertFalse(lv.isNumber());
        assertEquals(testBool, lv.bool());
        assertEquals(LINE, lv.getLine());
        assertEquals(COLUMN, lv.getColumn());
        assertEquals("true", lv.toString());

        testBool = false;
        lv = LittleValue.booleanValue(testBool, mockContext);
        assertEquals(LittleValue.ValueType.BOOLEAN, lv.type());
        assertTrue(lv.isBoolean());
        assertFalse(lv.isString());
        assertFalse(lv.isNumber());
        assertEquals(testBool, lv.bool());
        assertEquals(LINE, lv.getLine());
        assertEquals(COLUMN, lv.getColumn());
        assertEquals("false", lv.toString());
    }

    @Test
    public void testNumberCompares() {
        var lv1 = LittleValue.numberValue(1.0, mockContext);
        var lv2 = LittleValue.numberValue(2.0, mockContext);
        var lv2b = LittleValue.numberValue(2.0, mockContext);
        var lv3 = LittleValue.numberValue(3.0, mockContext);
        var lvMike = LittleValue.stringValue("Mike", mockContext);
        assertTrue(lv1.evalCompare(LittleCalcLexer.LT, lv2));
        assertFalse(lv2.evalCompare(LittleCalcLexer.LT, lv1));
        assertTrue(lv1.evalCompare(LittleCalcLexer.LE, lv2));
        assertTrue(lv2.evalCompare(LittleCalcLexer.LE, lv2b));
        assertFalse(lv3.evalCompare(LittleCalcLexer.LE, lv2));
        assertTrue(lv2.evalCompare(LittleCalcLexer.EQ, lv2b));
        assertFalse(lv3.evalCompare(LittleCalcLexer.EQ, lv2));
        assertTrue(lv1.evalCompare(LittleCalcLexer.NE, lv2));
        assertFalse(lv2.evalCompare(LittleCalcLexer.NE, lv2b));
        assertTrue(lv3.evalCompare(LittleCalcLexer.GE, lv2));
        assertTrue(lv2.evalCompare(LittleCalcLexer.GE, lv2b));
        assertFalse(lv1.evalCompare(LittleCalcLexer.GE, lv2));
        assertTrue(lv3.evalCompare(LittleCalcLexer.GT, lv2));
        assertFalse(lv2.evalCompare(LittleCalcLexer.GT, lv2b));
        assertEquals(lv2, lv2b);
        assertNotEquals(lv1, lv2);
        assertNotEquals(lvMike, lv2);
        assertFalse(lvMike.equals("Mike"));
        assertEquals(lv2.hashCode(), lv2b.hashCode());
        assertNotEquals(lv1.hashCode(), lv2.hashCode());

        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            lvMike.evalCompare(LittleCalcLexer.EQ, lv1);
        });

        assertEquals("line:5 col:21 -- Cannot compare STRING to NUMBER",ex.getMessage());
    }

    @Test
    public void testBadCompareOp() {
        var lv1 = LittleValue.numberValue(1.0, mockContext);
        var lv2 = LittleValue.numberValue(2.0, mockContext);
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            lv1.evalCompare(LittleCalcLexer.COMMENT, lv2);
        });
        assertEquals("line:5 col:21 -- Comparison operator (COMMENT) is not valid for NUMBER values",ex.getMessage());
    }
}
