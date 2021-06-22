package net.cargal.littlecalc;

import org.antlr.v4.runtime.Parser;

import net.cargal.littlecalc.LittleCalcParser.AssignmentStmtContext;
import net.cargal.littlecalc.LittleCalcParser.ReplExprContext;

public class LittleCalcREPLVisitor extends LittleCalcInterpVisitor {
    private boolean tracing = false;

    public LittleCalcREPLVisitor(Parser parser) {
        this.parser = parser;
    }

    @Override
    public Void visitReplExpr(ReplExprContext ctx) {
        super.visitReplExpr(ctx);
        System.out.println(stack.pop());
        return null;
    }

    @Override
    public Void visitAssignmentStmt(AssignmentStmtContext ctx) {
        super.visitAssignmentStmt(ctx);
        processCommand(ctx.ID().getText());
        return null;
    }

    private void processCommand(String cmd) {
        if ("trace".equalsIgnoreCase(cmd)) {
            tracing = getVar(cmd).bool();
            System.out.println("Tracing " + (tracing ? "On" : "Off"));
            parser.setTrace(getVar(cmd).bool());
        }
        if ("debug".equalsIgnoreCase(cmd)) {
            debugging = getVar(cmd).bool();
            System.out.println("debug " + (debugging ? "On" : "Off"));
        }
    }

    public boolean isTracing() {
        return tracing;
    }
}
