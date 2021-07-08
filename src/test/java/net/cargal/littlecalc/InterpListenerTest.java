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
import org.junit.jupiter.api.Test;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class InterpListenerTest extends LCTestBase {

    private LittleCalcSemanticValidationListener listener;
    private String capturedOutput;
    private LittleCalcParser parser;
    private LittleCalcExecutionVisitor visitor;
    private ParserRuleContext parseTree;

    private void interpret(Function<LittleCalcParser, ParserRuleContext> parseRule, String source) {
        var charStream = CharStreams.fromString(source);
        var lexer = new LittleCalcLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        parser = new LittleCalcParser(tokenStream);
        try {
            capturedOutput = tapSystemErrAndOutNormalized(() -> {
                parseTree = parseRule.apply(parser);
                if ((parser.getNumberOfSyntaxErrors() == 0)) {
                    listener = new LittleCalcSemanticValidationListener();
                    ParseTreeWalker.DEFAULT.walk(listener, parseTree);
                    if (!listener.hasErrors()) {
                        visitor = new LittleCalcExecutionVisitor();
                        visitor.visit(parseTree);
                    }
                }
            });
        } catch (LittleCalcRuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            Assertions.fail(ex.getMessage(), ex);
        }
    }

    @Test
    void testNumberAssignment() {
        interpret(LittleCalcParser::calcIn, "a=1.0");
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(1.0, visitor.getVar("a").get().number());
    }

    @Test
    void testBooleanAssignment() {
        interpret(LittleCalcParser::calcIn, """
                a=true
                b=false
                c = 3 < 4
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertTrue(visitor.getVar("a").get().bool());
        assertFalse(visitor.getVar("b").get().bool());
        assertTrue(visitor.getVar("c").get().bool());
    }

    @Test
    void testStringAssignment() {
        interpret(LittleCalcParser::calcIn, """
                a='Mike'
                b="Chris"
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals("Mike", visitor.getVar("a").get().string());
        assertEquals("Chris", visitor.getVar("b").get().string());
    }

    @Test
    void testParenExpr() throws Exception {
        interpret(LittleCalcParser::calcIn, """
                x = 8 * (5 + 6)
                """);
        ;
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(88.0, visitor.getVar("x").get().number());
    }

    @Test
    void testAddSub() {
        interpret(LittleCalcParser::calcIn, """
                a=8+9
                b=9-8
                c=9-8+7
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(17.0, visitor.getVar("a").get().number());
        assertEquals(1.0, visitor.getVar("b").get().number());
        assertEquals(8.0, visitor.getVar("c").get().number());
    }

    @Test
    void testMulDiv() {
        interpret(LittleCalcParser::calcIn, """
                a=8*9
                b=9/3
                c=9/3*7
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(72.0, visitor.getVar("a").get().number());
        assertEquals(3.0, visitor.getVar("b").get().number());
        assertEquals(21.0, visitor.getVar("c").get().number());
    }

    @Test
    void testExp() {
        interpret(LittleCalcParser::calcIn, """
                a=2^2
                b=25^0.5
                c=2^4^0.5
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(4.0, visitor.getVar("a").get().number());
        assertEquals(5.0, visitor.getVar("b").get().number());
        assertEquals(4.0, visitor.getVar("c").get().number());
    }

    @Test
    void testTernary() {
        interpret(LittleCalcParser::calcIn, """
                a= true ? 2 : 3
                b= false ? 2 : 3
                c= 2<3 ? "yes" : "no"
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals(2.0, visitor.getVar("a").get().number());
        assertEquals(3.0, visitor.getVar("b").get().number());
        assertEquals("yes", visitor.getVar("c").get().string());
    }

    @Test
    void testBadTernary() {
        interpret(LittleCalcParser::calcIn, """
                a= true ? 2 : "3"
                """);
        assertEquals("line:1 col:4 -- true and false branches must share the same type (STRING,NUMBER)",
                capturedOutput.trim());
    }

    @Test
    void testNotNumber() {
        interpret(LittleCalcParser::calcIn, """
                a =  2 + "3"
                """);
        assertEquals("line:1 col:10 -- \"3\" is not numeric", capturedOutput.trim());
    }

    @Test
    void testNotBoolean() {
        interpret(LittleCalcParser::calcIn, """
                a = true && 3
                """);
        assertEquals("line:1 col:13 -- 3 is not boolean", capturedOutput.trim());
    }

    @Test
    void testBadCompare() {
        interpret(LittleCalcParser::calcIn, """
                a = 1 < "3"
                """);
        assertEquals("line:1 col:5 -- can not compare NUMBER to STRING", capturedOutput.trim());
    }

    @Test
    void testreset() {
        interpret(LittleCalcParser::calcIn, """
                a = 1 < "3"
                """);
        assertTrue(listener.hasErrors());
        listener.reset();
        assertFalse(listener.hasErrors());
    }

    @Test
    void testPrint() {
        interpret(LittleCalcParser::calcIn, """
                a = true
                Print "The test worked = " a
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertEquals("The test worked = true", capturedOutput.trim());

    }

    @Test
    void testVars() {
        interpret(LittleCalcParser::calcIn, """
                a = "Test"
                b = 0.5
                c = b < 2
                vars
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        var expected = """
                \ta : Test
                \tb : 0.5
                \tc : true
                """;
        assertEquals(expected, capturedOutput);
    }

    @Test
    void testNoValue() throws Exception {
        interpret(LittleCalcParser::calcIn, """
                a = b
                """);
        assertEquals("line:1 col:5 -- b has not been assigned a value", capturedOutput.trim());

        var ex = assertThrows(LittleCalcRuntimeException.class, () -> {
            new LittleCalcExecutionVisitor().visit(parseTree);
        });
        assertEquals("line:1 col:5 -- b has not been assigned a value", ex.getMessage().trim());
    }

    @Test
    void testEquatlity() {
        interpret(LittleCalcParser::calcIn, """
                a = 2 == 2
                b = 2 != 2
                c = 3 != 4
                d = 3 == 4
                """);
        assertTrue(visitor.getVar("a").get().bool());
        assertFalse(visitor.getVar("b").get().bool());
        assertTrue(visitor.getVar("c").get().bool());
        assertFalse(visitor.getVar("d").get().bool());
    }

    @Test
    void testAnd() {
        interpret(LittleCalcParser::calcIn, """
                a= true && false
                b= false && true
                c = true && true
                d = false && false
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertFalse(visitor.getVar("a").get().bool());
        assertFalse(visitor.getVar("b").get().bool());
        assertTrue(visitor.getVar("c").get().bool());
        assertFalse(visitor.getVar("d").get().bool());
    }

    @Test
    void testOr() {
        interpret(LittleCalcParser::calcIn, """
                a= true || false
                b= false || true
                c = true || true
                d = false || false
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertTrue(visitor.getVar("a").get().bool());
        assertTrue(visitor.getVar("b").get().bool());
        assertTrue(visitor.getVar("c").get().bool());
        assertFalse(visitor.getVar("d").get().bool());
    }

    @Test
    void TestNot() {
        interpret(LittleCalcParser::calcIn, """
                a= !true
                b= !false
                """);
        assertEquals(0, parser.getNumberOfSyntaxErrors());
        assertFalse(visitor.getVar("a").get().bool());
        assertTrue(visitor.getVar("b").get().bool());

    }

    @Test
    void TestParseError() {
        interpret(LittleCalcParser::calcIn, """
                mike = 10
                print 8 * 9 ^ / (mike / 6)
                """);
        var expected = """
                line 2:14 extraneous input '/' expecting 
                """;
        assertEquals(1, parser.getNumberOfSyntaxErrors());
        assertMatchedOutput(expected, capturedOutput);
    }

    // TODO: Unit tests to verify precedence
}
