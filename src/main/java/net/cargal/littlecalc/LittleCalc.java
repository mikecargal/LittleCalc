package net.cargal.littlecalc;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class LittleCalc {
    public static void main(String... args) throws IOException {
        new LittleCalc().run(CharStreams.fromFileName("./little.ltl"));
    }

    public void run(CharStream charStream) {
        var lexer = new LittleCalcLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new LittleCalcParser(tokenStream);
        var listener = new LittleCalcSemanticValidationListener();

        var calcIn = parser.calcIn();
        if (parser.getNumberOfSyntaxErrors() == 0) {
            ParseTreeWalker.DEFAULT.walk(listener, calcIn);
            if (!listener.hasErrors()) {
                new LittleCalcExecutionVisitor().visit(calcIn);
            }
        }
    }
}