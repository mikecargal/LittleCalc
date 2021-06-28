package net.cargal.littlecalc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrAndOutNormalized;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestREPL {

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
                quit
                """;
        capturedOutput = tapSystemErrAndOutNormalized(() -> {
            repl.run(inputStream(source));
        });
        var expected = """
                7 + 8
                quit
                7 + 8
                15.0
                quit
                Exiting...
                """;
                assertMatchedOutput(expected);
    }

    @Test
    void testREPLSessionwithContinuation() throws Exception {
        var source = """
                7 + 
                8
                3 \\
                + 4
                quit
                """;
        capturedOutput = tapSystemErrAndOutNormalized(() -> {
            repl.run(inputStream(source));
        });
        var expected = """
                7 + 
                8
                3 \\
                + 4
                quit
                7 + 
                8
                15.0
                3 \\
                + 4
                7
                quit
                Exiting...
                """;
                System.out.println(capturedOutput);
                assertMatchedOutput(expected);
    }


    private InputStream inputStream(String inputString) {
        return new ByteArrayInputStream(inputString.getBytes());
    }

    private void assertMatchedOutput(String expected) {
        var expectedLines = expected.split("\n");
        var outputLines = capturedOutput.split("[\n\r]+");

        assertEquals(expectedLines.length, outputLines.length);
        for (int i = 0; i < expectedLines.length; i++) {
            assertThat(outputLines[i], containsString(expectedLines[i]));
        }
    }
}
