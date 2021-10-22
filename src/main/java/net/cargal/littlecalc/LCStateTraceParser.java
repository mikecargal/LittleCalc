package net.cargal.littlecalc;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;

public class LCStateTraceParser extends LittleCalcParser {

    public LCStateTraceParser(TokenStream input) {
        super(input);
    }

    @Override
    public void setState(int atnState) {
        System.out.println("State: " + getState() + "->" + atnState);
        super.setState(atnState);
    }

    @Override
    public void enterOuterAlt(ParserRuleContext localctx, int altNum) {
        System.out.println(localctx.getClass().getSimpleName() + ":entering alt->" + altNum);
        super.enterOuterAlt(localctx, altNum);
    }
}
