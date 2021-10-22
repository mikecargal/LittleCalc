package net.cargal.littlecalc;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;

public class LCStateTraceLexer extends LittleCalcLexer {

    public LCStateTraceLexer(CharStream input) {
        super(input);
    }

    @Override
    public Token nextToken() {
        var t = super.nextToken();
        System.out.println(t);
        return t;
    }
}
