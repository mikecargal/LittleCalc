package net.cargal.littlecalc;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrAndOutNormalized;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class InterpListenerTest {

    private LittleCalcInterpListener listener;
    private LittleReplErrorListener errListener;
    private String capturedOutput;

    private void interpret(Function<LittleCalcParser, ParserRuleContext> parseRule, String source) {
        var charStream = CharStreams.fromString(source);
        var lexer = new LittleCalcLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new LittleCalcParser(tokenStream);
        listener = new LittleCalcInterpListener();
        parser.removeErrorListeners();
        errListener = new LittleReplErrorListener();
        parser.addErrorListener(errListener);
        try {
            capturedOutput = tapSystemErrAndOutNormalized(() -> {
                var pt = parseRule.apply(parser);
                if (errListener.canProcessReplInput()) {
                    ParseTreeWalker.DEFAULT.walk(listener, pt);
                }
            });
        } catch (LittleCalcRuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            Assertions.fail(ex.getMessage(), ex);
        }
    }

    @Test
    public void testNumberAssignment() {
        interpret(LittleCalcParser::replIn, "a=1.0");
        assertFalse(errListener.observedAnError());
        assertEquals(1.0, listener.getVar("a").number());
    }

    @Test
    public void testBooleanAssignment() {
        interpret(LittleCalcParser::stmts, """
                a=true
                b=false
                c = 3 < 4
                """);
        assertFalse(errListener.observedAnError());
        assertTrue(listener.getVar("a").bool());
        assertFalse(listener.getVar("b").bool());
        assertTrue(listener.getVar("c").bool());
    }

    @Test
    public void testStringAssignment() {
        interpret(LittleCalcParser::stmts, """
                a='Mike'
                b="Chris"
                """);
        assertFalse(errListener.observedAnError());
        assertEquals("Mike", listener.getVar("a").string());
        assertEquals("Chris", listener.getVar("b").string());
    }

    @Test
    public void testAddSub() {
        interpret(LittleCalcParser::stmts, """
                a=8+9
                b=9-8
                c=9-8+7
                """);
        assertFalse(errListener.observedAnError());
        assertEquals(17.0, listener.getVar("a").number());
        assertEquals(1.0, listener.getVar("b").number());
        assertEquals(8.0, listener.getVar("c").number());
    }

    @Test
    public void testMulDiv() {
        interpret(LittleCalcParser::stmts, """
                a=8*9
                b=9/3
                c=9/3*7
                """);
        assertFalse(errListener.observedAnError());
        assertEquals(72.0, listener.getVar("a").number());
        assertEquals(3.0, listener.getVar("b").number());
        assertEquals(21.0, listener.getVar("c").number());
    }

    @Test
    public void testExp() {
        interpret(LittleCalcParser::stmts, """
                a=2^2
                b=25^0.5
                c=2^4^0.5
                """);
        assertFalse(errListener.observedAnError());
        assertEquals(4.0, listener.getVar("a").number());
        assertEquals(5.0, listener.getVar("b").number());
        assertEquals(4.0, listener.getVar("c").number());
    }

    @Test
    public void testTernary() {
        interpret(LittleCalcParser::stmts, """
                a= true ? 2 : 3
                b= false ? 2 : 3
                c= 2<3 ? "yes" : "no"
                """);
        assertFalse(errListener.observedAnError());
        assertEquals(2.0, listener.getVar("a").number());
        assertEquals(3.0, listener.getVar("b").number());
        assertEquals("yes", listener.getVar("c").string());
    }

    @Test
    public void testErrorAtEOF() {
        interpret(LittleCalcParser::replIn, """
                2 + 3 * 4 ^
                """);
        assertTrue(errListener.incompleteInput());
    }

    @Test
    @Disabled("Move this over to unit testing for REPLListener")
    public void testREPLExpr() {
        interpret(LittleCalcParser::replIn, """
                2 + 3
                """);
        assertFalse(errListener.observedAnError());
        assertEquals("5.0", capturedOutput.trim());
    }

    @Test
    public void testPrint() {
        interpret(LittleCalcParser::stmts, """
                a = true
                Print "The test worked = " a
                """);
        assertFalse(errListener.observedAnError());
        assertEquals("The test worked = true", capturedOutput.trim());

    }

    @Test
    public void testVars() {
        interpret(LittleCalcParser::stmts, """
                a = "Test"
                b = 0.5
                c = b < 2
                vars
                """);
        assertFalse(errListener.observedAnError());
        var expected = """
                \ta : Test
                \tb : 0.5
                \tc : true
                """;
        assertEquals(expected, capturedOutput);
    }

    @Test
    public void testNoValue() {
        LittleCalcRuntimeException ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            interpret(LittleCalcParser::stmts, """
                    a = b
                    """);
        });

        assertEquals("line:1 col:5 -- b has not been assigned a value", ex.getMessage());
    }

    @Test
    public void testAnd() {
        interpret(LittleCalcParser::stmts, """
                a= true && false
                b= false && true
                c = true && true
                d = false && false
                """);
        assertFalse(errListener.observedAnError());
        assertFalse(listener.getVar("a").bool());
        assertFalse(listener.getVar("b").bool());
        assertTrue(listener.getVar("c").bool());
        assertFalse(listener.getVar("d").bool());
    }

    @Test
    public void testOr() {
        interpret(LittleCalcParser::stmts, """
                a= true || false
                b= false || true
                c = true || true
                d = false || false
                """);
        assertFalse(errListener.observedAnError());
        assertTrue(listener.getVar("a").bool());
        assertTrue(listener.getVar("b").bool());
        assertTrue(listener.getVar("c").bool());
        assertFalse(listener.getVar("d").bool());
    }

    @Test
    public void TestNot() {
        interpret(LittleCalcParser::stmts, """
                a= !true
                b= !false
                """);
        assertFalse(errListener.observedAnError());
        assertFalse(listener.getVar("a").bool());
        assertTrue(listener.getVar("b").bool());

    }

    // TODO: Unit tests to verify precedence
}
