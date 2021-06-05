package net.cargal.littlecalc;

import java.util.Scanner;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class LittleCalcRepl {
    public static void main(String... args) {
        var keyboard = new Scanner(System.in);
        CharStream charStream = CharStreams.fromString("");
        var lexer = new LittleCalcLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new LittleCalcParser(tokenStream);
        var listener = new LittleCalcInterpListener();
        parser.addParseListener(listener);
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
                parser.replIn();
                input = "";
            }
        }
        System.out.println("Exiting...");
        keyboard.close();
    }
}