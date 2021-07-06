package net.cargal.littlecalc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LCTestBase {
    protected void assertMatchedOutput(String expected,String capturedOutput) {
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
