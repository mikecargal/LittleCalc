package net.cargal.littlecalc;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContextWithAltNum;
import org.antlr.v4.runtime.atn.ATN;

public class LCAltNumCtx extends RuleContextWithAltNum {
    public LCAltNumCtx() {
        altNum = ATN.INVALID_ALT_NUMBER;
    }

    public LCAltNumCtx(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    @Override
    public void setAltNumber(int altNum) {
        super.setAltNumber(altNum);
        // System.out.println(this.getClass().getSimpleName() + ":" + altNum);
    }
}
