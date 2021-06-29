package net.cargal.littlecalc;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrAndOutNormalized;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;
import org.junit.jupiter.api.Test;

public class TracingTokenFactoryTest {
    @Test
    void testCompleteCreate() throws Exception {
        var charStream = CharStreams.fromString("hello");
        var lexer = new LittleCalcLexer(charStream);
        var source = new Pair<TokenSource, CharStream>(lexer, charStream);

        var expectedTokenString = "[@-1,0:5='hello',<31>,1:0]";
        var capturedOutput = tapSystemErrAndOutNormalized(() -> {
            CommonToken symbol = TracingTokenFactory.DEFAULT.create(source, LittleCalcLexer.ID, "hello", 0, 0, 5, 1, 0);
            assertEquals(expectedTokenString, symbol.toString());
        });
        assertEquals("ID : " + expectedTokenString, capturedOutput.trim());
    }

    @Test
    void testSimpleCreat() throws Exception {
        var expectedTokenString = "[@-1,0:0='9',<29>,0:-1]";
        var capturedOutput = tapSystemErrAndOutNormalized(() -> {
            CommonToken symbol = TracingTokenFactory.DEFAULT.create(LittleCalcLexer.NUMBER, "9");
            assertEquals(expectedTokenString, symbol.toString());
        });
        assertEquals("NUMBER : " + expectedTokenString, capturedOutput.trim());
    }
}
