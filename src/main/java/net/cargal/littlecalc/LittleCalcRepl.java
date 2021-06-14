package net.cargal.littlecalc;

import java.util.Scanner;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class LittleCalcRepl {
    public static void main(String... args) {
        var keyboard = new Scanner(System.in);
        var charStream = CharStreams.fromString("");
        var lexer = new LittleCalcLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new LittleCalcParser(tokenStream);
        var listener = new LittleCalcInterpListener();
        parser.removeErrorListeners();
        var replErrListener = new LittleReplErrorListener();
        parser.addErrorListener(replErrListener);
        var input = "";
        while (true) {
            System.out.print("\n> ");
            input += keyboard.nextLine();
            if (input.equals("quit")) {
                break;
            }
            if (input.endsWith("\\")) {
                input = input.substring(0, input.length() - 1);
            } else {
                charStream = CharStreams.fromString(input);
                lexer.setInputStream(charStream);
                tokenStream.setTokenSource(lexer);
                parser.setTokenStream(tokenStream);
                var replTree = parser.replIn();
                if (replErrListener.canProcessReplInput()) {
                    ParseTreeWalker.DEFAULT.walk(listener, replTree);
                }
                if (!replErrListener.incompleteInput()) {
                    input = "";
                }
                replErrListener.reset();
                listener.reset();
            }
        }
        System.out.println("Exiting...");
        keyboard.close();
    }
}