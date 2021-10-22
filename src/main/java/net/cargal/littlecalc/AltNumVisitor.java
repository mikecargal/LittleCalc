package net.cargal.littlecalc;

import net.cargal.littlecalc.LittleCalcParser.AddSubExprContext;

public class AltNumVisitor extends LittleCalcBaseListener {
    @Override
    public void enterAddSubExpr(AddSubExprContext ctx) {
        super.enterAddSubExpr(ctx);
        ctx.setAltNumber(7);
    }
}
