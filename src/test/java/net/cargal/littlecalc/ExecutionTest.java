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

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ExecutionTest extends LCTestBase {

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
                        visitor = new LittleCalcExecutionVisitor(parser);
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
    void testParenExpr() {
        interpret(LittleCalcParser::calcIn, """
                x = 8 * (5 + 6)
                """);
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
    void testReset() {
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
    void testNoValue() {
        interpret(LittleCalcParser::calcIn, """
                a = b
                """);
        assertEquals("line:1 col:5 -- b has not been assigned a value", capturedOutput.trim());

        var ex = assertThrows(LittleCalcRuntimeException.class, //
                () -> new LittleCalcExecutionVisitor().visit(parseTree));
        assertEquals("line:1 col:5 -- b has not been assigned a value", ex.getMessage().trim());
    }

    @Test
    void testEquality() {
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
    void testParseError() {
        interpret(LittleCalcParser::calcIn, """
                mike = 10
                print 8 * 9 ^ / (mike / 6)
                """);
        var expected = """
                no viable alternative at input '8 * 9 ^ /
                extraneous input '/' expecting
                """;
        assertEquals(2, parser.getNumberOfSyntaxErrors());
        assertMatchedOutput(expected, capturedOutput);
    }

    @Test
    void testEQTrueRefactor() {
        interpret(LittleCalcParser::replIn, """
                    refactor { a == true }
                """);
        assertMatchedOutput(" a ", capturedOutput);
    }

    @Test
    void testNETrueRefactor() {
        interpret(LittleCalcParser::replIn, """
                    refactor { a != true }
                """);
        assertMatchedOutput(" !a ", capturedOutput);
    }

    @Test
    void testEQFalseRefactor() {
        interpret(LittleCalcParser::replIn, """
                refactor { a == false }
                """);
        assertMatchedOutput(" !a ", capturedOutput);
    }

    @Test
    void testNEFalseRefactor() {
        interpret(LittleCalcParser::replIn, """
                refactor { a != false }
                """);
        assertMatchedOutput(" a ", capturedOutput);
    }

    @Test
    void testPlus0Refactor() {
        interpret(LittleCalcParser::replIn, """
                    refactor { a + 0 - 5 }
                """);
        assertMatchedOutput(" a  - 5 ", capturedOutput);
    }

    @Test
    void test0PlusRefactor() {
        interpret(LittleCalcParser::replIn, """
                    refactor { 0 + a - 5 }
                """);
        assertMatchedOutput(" a - 5 ", capturedOutput);
    }

    @Test
    void testTimes1Refactor() {
        interpret(LittleCalcParser::replIn, """
                    refactor { a * 1 - 5 }
                """);
        assertMatchedOutput(" a /* * 1 */ - 5 ", capturedOutput);
    }

    @Test
    void test1TimesRefactor() {
        interpret(LittleCalcParser::replIn, """
                    refactor { 1 * a - 5 }
                """);
        assertMatchedOutput(" a - 5 ", capturedOutput);
    }

    @Test
    void testTimes0Refactor() {
        interpret(LittleCalcParser::replIn, """
                    refactor { a * 0 - 5 }
                """);
        assertMatchedOutput(" 0 - 5 ", capturedOutput);
    }

    @Test
    void test0TimesRefactor() {
        interpret(LittleCalcParser::replIn, """
                    refactor { 0 * a - 5 }
                """);
        assertMatchedOutput(" 0 - 5 ", capturedOutput);
    }

    @Test
    void testVisistTokens() {
        interpret(LittleCalcParser::replIn, """
                    tokens { 0 * a - 5 }
                """);
        var expected = """
                [@5,13:13='0',<30>,1:13]
                [@6,14:14=' ',<34>,channel=1,1:14]
                [@7,15:15='*',<6>,1:15]
                [@8,16:16=' ',<34>,channel=1,1:16]
                [@9,17:17='a',<32>,1:17]
                [@10,18:18=' ',<34>,channel=1,1:18]
                [@11,19:19='-',<9>,1:19]
                [@12,20:20=' ',<34>,channel=1,1:20]
                [@13,21:21='5',<30>,1:21]
                """;
        assertMatchedOutput(expected, capturedOutput);
    }

}
