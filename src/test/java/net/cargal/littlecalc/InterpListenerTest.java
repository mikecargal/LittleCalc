package net.cargal.littlecalc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class InterpListenerTest {
    private class TestListener extends LittleCalcInterpListener {

        private List<String> output = new ArrayList<>(Arrays.asList(""));

        @Override
        protected void print(Object o) {
            int lastIdx = output.size() - 1;
            output.set(lastIdx, output.get(lastIdx) + String.valueOf(o));
        }

        @Override
        protected void println(Object o) {
            print(o);
            println();
        }

        @Override
        protected void println() {
            output.add("");
        }
    }

    private class TestErrListener extends BaseErrorListener {
        private List<String> errors = new ArrayList<>();

        @Override
        public void syntaxError( //
                Recognizer<?, ?> recognizer, //
                Object offendingSymbol, //
                int line, //
                int charPositionInLine, //
                String msg, //
                RecognitionException e) {
            reportError("line " + line + ":" + charPositionInLine + " " + msg);
        }

        protected void reportError(String str) {
            errors.add(str);
        }
    }

    private TestListener listener;
    private TestErrListener errListener;

    private void interpret(Function<LittleCalcParser, ParserRuleContext> parseRule, String source) {
        var charStream = CharStreams.fromString(source);
        var lexer = new LittleCalcLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new LittleCalcParser(tokenStream);
        listener = new TestListener();
        parser.removeErrorListeners();
        errListener = new TestErrListener();
        parser.addErrorListener(errListener);
        var pt = parseRule.apply(parser);
        if (this.errListener.errors.size() == 0) {
            ParseTreeWalker.DEFAULT.walk(listener, pt);
        }
    }

    @Test
    public void testNumberAssignment() {
        interpret(LittleCalcParser::replIn, "a=1.0");
        assertEquals(1.0, listener.getVar("a").number());
    }

    @Test
    public void testBooleanAssignment() {
        interpret(LittleCalcParser::stmts, """
                a=true
                b=false
                c = 3 < 4
                """);
        assertEquals(0, errListener.errors.size());
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
        assertEquals(0, errListener.errors.size());
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
        assertEquals(0, errListener.errors.size());
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
        assertEquals(0, errListener.errors.size());
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
        assertEquals(0, errListener.errors.size());
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
        assertEquals(0, errListener.errors.size());
        assertEquals(2.0, listener.getVar("a").number());
        assertEquals(3.0, listener.getVar("b").number());
        assertEquals("yes", listener.getVar("c").string());
    }

    @Test
    public void testErrorAtEOF() {
        interpret(LittleCalcParser::replIn, """
                2 + 3 * 4 ^
                """);
        assertEquals(1, errListener.errors.size());
        assertEquals("line 2:0 mismatched input '<EOF>' expecting {'true', 'false', '(', NUMBER, STRING, ID}",
                errListener.errors.get(0));
    }

    @Test
    public void testREPLExpr() {
        interpret(LittleCalcParser::replIn, """
                2 + 3
                """);
        assertEquals(2, listener.output.size());
        assertEquals("5.0", listener.output.get(0));
    }

    @Test
    public void testPrint() {
        interpret(LittleCalcParser::stmts, """
                a = true
                Print "The test worked = " a
                """);
        assertEquals(2, listener.output.size());
        assertEquals("The test worked = true", listener.output.get(0));
    }

    @Test
    public void testVars() {
        interpret(LittleCalcParser::stmts, """
                a = "Test"
                b = 0.5
                c = b < 2
                vars
                """);
        assertEquals(4, listener.output.size());
        assertEquals("\ta : Test", listener.output.get(0));
        assertEquals("\tb : 0.5", listener.output.get(1));
        assertEquals("\tc : true", listener.output.get(2));
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
}
