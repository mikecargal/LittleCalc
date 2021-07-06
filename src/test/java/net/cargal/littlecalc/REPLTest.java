package net.cargal.littlecalc;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrAndOutNormalized;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.misc.Pair;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class REPLTest extends LCTestBase {

    private LittleCalcRepl repl;
    private String capturedOutput;

    @BeforeEach
    void before() {
        repl = new LittleCalcRepl();
    }

    @Test
    void testLineReader() throws Exception {
        tapSystemErrAndOutNormalized(() -> {
            var lineReader = repl.getLineReader(getTerminal("quit\n"));
            assertEquals("quit", lineReader.readLine());
        });
    }

    @Test
    void testREPLSession() throws Exception {
        var source = """
                7 + 8
                """;
        var expected = """
                7 + 8
                15.0
                """;
        verifyRun(source, expected);

    }

    @Test
    void testAssignment() throws Exception {
        var source = """
                x = 10
                x
                """;
        var expected = """
                x = 10
                x
                10
                """;
        verifyRun(source, expected);

    }

    @Test
    void testREPLSessionwithContinuation() throws Exception {
        var source = """
                7 +
                8
                3 \\
                + 4
                """;
        var expected = """
                7 +
                8
                15.0
                3 \\
                + 4
                7
                """;
        verifyRun(source, expected);

    }

    @Test
    void testSyntaxError() throws Exception {
        var source = """
                8 * * 9
                """;
        var expected = """
                8 * * 9
                extraneous input '*' expecting
                """;
        verifyRun(source, expected);
    }

    @Test
    void testSemanticError() throws Exception {
        var source = """
                8 * "test"
                """;
        var expected = """
                8 * "test"
                "test" is not numeric
                """;
        verifyRun(source, expected);
    }

    @Test
    void testParserTracing() throws Exception {
        var source = """
                parserTracing = true
                parserTracing = false
                9
                """;
        var expected = """
                parserTracing = true
                Parser Tracing On
                parserTracing = false
                replIn
                enter   stmt
                consume
                consume
                enter   expr
                consume
                exit    expr
                exit    stmt
                consume
                exit    replIn
                Parser Tracing Off
                9
                9.0
                """;
        verifyRun(source, expected);
    }

    @Test
    void testLexerTracing() throws Exception {
        var source = """
                lexerTracing = true
                lexerTracing = false
                9
                """;
        var expected = """
                lexerTracing = true
                Lexer Tracing On
                lexerTracing = false
                ID
                '='
                'false'
                EOF
                Lexer Tracing Off
                9
                9.0
                """;
        verifyRun(source, expected);
    }

    @Test
    void testFullTracing() throws Exception {
        var source = """
                fullTracing = true
                fullTracing = false
                9
                """;
        var expected = """
                fullTracing = true
                Full Tracing On
                fullTracing = false
                ID
                replIn
                '='
                enter   stmt
                consume
                'false'
                consume
                enter   expr
                EOF
                consume
                exit    expr
                exit    stmt
                consume
                exit    replIn
                Full Tracing Off
                9
                9.0
                """;
        verifyRun(source, expected);
    }

    private Terminal getTerminal(String inputString) throws IOException {
        var inputStream = new ByteArrayInputStream(inputString.getBytes());
        return TerminalBuilder.builder() //
                .streams(inputStream, System.out) //
                .jna(true) //
                .build();
    }

    private void verifyRun(String source, String expected) throws Exception {
        var pair = prep(source, expected);
        var preppedSource = pair.a;
        var preppedExpected = pair.b;
        capturedOutput = tapSystemErrAndOutNormalized(() -> {
            repl.run(getTerminal(preppedSource));
        });
        assertMatchedOutput(preppedExpected,capturedOutput);
    }

    private Pair<String, String> prep(String source, String expected) {
        var resSource = source + "quit\n";
        var resExpected = resSource + expected + """
                quit
                Exiting...
                """;
        return new Pair<>(resSource, resExpected);
    }


}
