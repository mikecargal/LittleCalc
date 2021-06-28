package net.cargal.littlecalc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrAndOutNormalized;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class REPLTest {

    private LittleCalcRepl repl;
    private String capturedOutput;

    @BeforeEach
    void before() {
        repl = new LittleCalcRepl();
    }

    @Test
    void testLineReader() throws IOException {
        var lineReader = repl.getLineReader(inputStream("quit\n"));
        assertEquals("quit", lineReader.readLine());
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
        verifyRun(prep(source, expected));
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
        verifyRun(prep(source, expected));
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
        verifyRun(prep(source, expected));
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
        verifyRun(prep(source, expected));
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
        verifyRun(prep(source, expected));
    }

    @Test
    void testTracing() throws Exception {
        var source = """
                trace = true
                trace = false
                """;
        // TODO: why do we see "lIn" instead of "enter replIn"
        // Must be something to do with catpuring output, get correct results in actual
        // REPL
        var expected = """
                trace = true
                Tracing On
                trace = false
                lIn
                enter   stmt
                consume
                consume
                enter   expr
                consume
                exit    expr
                exit    stmt
                consume
                exit    replIn
                Tracing Off
                """;
        verifyRun(prep(source, expected));
    }

    private Pair<String, String> prep(String source, String expected) {
        var resSource = source + "quit\n";
        var resExpected = resSource + expected + """
                quit
                Exiting...
                """;
        return new Pair<>(resSource, resExpected);
    }

    private InputStream inputStream(String inputString) {
        return new ByteArrayInputStream(inputString.getBytes());
    }

    private void verifyRun(Pair<String, String> pair) throws Exception {
        var source = pair.a;
        var expected = pair.b;
        capturedOutput = tapSystemErrAndOutNormalized(() -> {
            repl.run(inputStream(source));
        });
        assertMatchedOutput(expected);
    }

    private void assertMatchedOutput(String expected) {
        var expectedLines = expected.split("\n");
        var outputLines = capturedOutput.split("[\n\r]+");

        if (expectedLines.length != outputLines.length) {
            System.out.println(expected);
            System.out.println(capturedOutput);
        }

        for (int i = 0; i < expectedLines.length; i++) {
            assertThat(outputLines[i], containsString(expectedLines[i]));
        }
        assertEquals(expectedLines.length, outputLines.length);
    }
}
