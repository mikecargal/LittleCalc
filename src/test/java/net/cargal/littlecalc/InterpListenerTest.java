package net.cargal.littlecalc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

public class InterpListenerTest {
    private class TestListener extends LittleCalcInterpListener {

        private List<String> output = Arrays.asList("");

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

    private class TestErrListener extends LittleReplErrorListener {
        private List<String> errors = new ArrayList<>();

        @Override
        protected void reportError(String str) {
            errors.add(str);
        }
    }

    private TestListener listener;
    private TestErrListener errListener;

    private void interpret(String source) {
        var charStream = CharStreams.fromString(source);
        var lexer = new LittleCalcLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new LittleCalcParser(tokenStream);
        listener = new TestListener();
        parser.addParseListener(listener);
        parser.removeErrorListeners();
        errListener = new TestErrListener();
        parser.addErrorListener(errListener);
        parser.stmts();
    }

    @Test
    public void testNumberAssignment() {
        interpret("a=1.0");
        assertEquals(1.0, listener.getVar("a").number());
    }

}
