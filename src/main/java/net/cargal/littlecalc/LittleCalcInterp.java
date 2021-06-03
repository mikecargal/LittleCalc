package net.cargal.littlecalc;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.tinylog.Logger;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class LittleCalcInterp {
    public static void main(String... args) throws IOException {
        CharStream charStream = CharStreams.fromFileName("./little.ltl");
        LittleCalcLexer lexer = new LittleCalcLexer(charStream);
        UnbufferedTokenStream<CommonToken> tokenStream = new UnbufferedTokenStream<>(lexer);
        LittleCalcParser parser = new LittleCalcParser(tokenStream);
        LittleCalcInterpListener listener = new LittleCalcInterpListener();
        parser.addParseListener(listener);
        try {
            parser.stmts();
            listener.dumpVariables();
        } catch (LittleCalcRuntimeException lcre) {
            Logger.error(lcre.getMessage());
        }
    }
}