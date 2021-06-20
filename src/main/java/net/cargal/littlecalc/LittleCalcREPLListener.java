package net.cargal.littlecalc;

import org.antlr.v4.runtime.Parser;

import net.cargal.littlecalc.LittleCalcParser.AssignmentStmtContext;

public class LittleCalcREPLListener extends LittleCalcInterpListener {
    private Parser parser;
    private boolean tracing = false;

    public LittleCalcREPLListener(Parser parser) {
        this.parser = parser;
    }

    @Override
    public void exitReplExpr(LittleCalcParser.ReplExprContext ctx) {
        System.out.println(stack.pop());
    }

    @Override
    public void exitAssignmentStmt(AssignmentStmtContext ctx) {
        super.exitAssignmentStmt(ctx);
        processCommand(ctx.ID().getText());
    }

    private void processCommand(String cmd) {
        if ("trace".equalsIgnoreCase(cmd)) {
            tracing = getVar(cmd).bool();
            System.out.println("Tracing " + (tracing ? "On" : "Off"));
            parser.setTrace(getVar(cmd).bool());
        }
    }

    public boolean isTracing() {
        return tracing;
    }
}
