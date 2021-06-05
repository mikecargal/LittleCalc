package net.cargal.littlecalc;

import java.io.IOException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.tinylog.Logger;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class LittleCalcInterp {
    public static void main(String... args) throws IOException {
        var charStream = CharStreams.fromFileName("./little.ltl");
        var lexer = new LittleCalcLexer(charStream);
        UnbufferedTokenStream<CommonToken> tokenStream = new UnbufferedTokenStream<>(lexer);
        var parser = new LittleCalcParser(tokenStream);
        var listener = new LittleCalcInterpListener();
        parser.addParseListener(listener);
        try {
            parser.stmts();
            listener.dumpVariables();
        } catch (LittleCalcRuntimeException lcre) {
            Logger.error(lcre.getMessage());
        }
    }
}