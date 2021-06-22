package net.cargal.littlecalc;

import org.antlr.v4.runtime.Parser;
import org.tinylog.Logger;

import net.cargal.littlecalc.LittleCalcParser.AssignmentStmtContext;
import net.cargal.littlecalc.LittleCalcParser.PrintStmtContext;
import net.cargal.littlecalc.LittleCalcParser.PrintVarsContext;

public class LittleCalcInterpVisitor extends LittleCalcBaseVisitor<Void> {
    protected SymbolTable variables = new SymbolTable();
    protected LittleCalcExprVisitor exprVisitor;
    protected Parser parser;

    public LittleCalcInterpVisitor() {
        exprVisitor = new LittleCalcExprVisitor(variables);
    }

    @Override
    public Void visitAssignmentStmt(AssignmentStmtContext ctx) {
        super.visitAssignmentStmt(ctx);
        var val = exprVisitor.visit(ctx.expr());
        var id = ctx.ID().getText();
        variables.put(id, val);
        Logger.debug("assignment to " + id);
        return null;
    }

    @Override
    public Void visitPrintStmt(PrintStmtContext ctx) {
        for (LittleCalcParser.ExprContext exprCtx : ctx.expr()) {
            System.out.print(exprVisitor.visit(exprCtx));
        }
        System.out.println();
        return null;
    }

    @Override
    public Void visitPrintVars(PrintVarsContext ctx) {
        dumpVariables();
        return null;
    }

    public void dumpVariables() {
        variables.keyStream().forEach(key -> System.out.println("\t" + key + " : " + variables.get(key)));
    }

    public LittleValue getVar(String varID) {
        return variables.get(varID);
    }

}
