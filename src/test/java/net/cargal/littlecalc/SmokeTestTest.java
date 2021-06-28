package net.cargal.littlecalc;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrAndOutNormalized;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class SmokeTestTest {
    @Test
    void itJustRuns() throws Exception {
        var capturedOutput = tapSystemErrAndOutNormalized(() -> {
            LittleCalcSmokeTest.main();
        });
        assertNotEquals("", capturedOutput);
    }
}
