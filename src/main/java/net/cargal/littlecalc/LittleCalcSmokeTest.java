package net.cargal.littlecalc;

import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class LittleCalcSmokeTest {
    public static void main(String... args) throws IOException {
        var charStream = CharStreams.fromFileName("./little.ltl");
        var lexer = new LittleCalcLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new LittleCalcParser(tokenStream);
        var stmts = parser.stmts();
        System.out.println(stmts.toStringTree(parser));
    }
}