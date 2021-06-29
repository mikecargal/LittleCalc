package net.cargal.littlecalc;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.misc.Pair;

public class TracingTokenFactory extends CommonTokenFactory {
    public static final TracingTokenFactory DEFAULT = new TracingTokenFactory();

    @Override
    public CommonToken create(Pair<TokenSource, CharStream> source, int type, String text, int channel, int start,
            int stop, int line, int charPositionInLine) {

        var result = super.create(source, type, text, channel, start, stop, line, charPositionInLine);
        System.out.println(LittleCalcLexer.VOCABULARY.getDisplayName(result.getType()) + " : " + result);
        return result;
    }

    @Override
    public CommonToken create(int type, String text) {
        var result = super.create(type, text);
        System.out.println(LittleCalcLexer.VOCABULARY.getDisplayName(result.getType()) + " : " + result);
        return result;
    }

}
