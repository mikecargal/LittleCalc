package net.cargal.littlecalc;

import org.junit.Test;
import static org.junit.Assert.*;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.Before;

public class LittleValueTest {
    private final int LINE = 5;
    private final int COLUMN = 20;
    ParserRuleContext mockContext;

    @Before
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

        testBool = false;
        lv = LittleValue.booleanValue(testBool, mockContext);
        assertEquals(LittleValue.ValueType.BOOLEAN, lv.type());
        assertTrue(lv.isBoolean());
        assertFalse(lv.isString());
        assertFalse(lv.isNumber());
        assertEquals(testBool, lv.bool());
        assertEquals(LINE, lv.getLine());
        assertEquals(COLUMN, lv.getColumn());
    }
}
