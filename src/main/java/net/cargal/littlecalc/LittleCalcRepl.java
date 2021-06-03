package net.cargal.littlecalc;

import java.io.IOException;
import java.util.Scanner;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.tinylog.Logger;

import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class LittleCalcRepl {
    public static void main(String... args) {
        Scanner keyboard = new Scanner(System.in);
        CharStream charStream = CharStreams.fromString("");
        LittleCalcLexer lexer = new LittleCalcLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        LittleCalcParser parser = new LittleCalcParser(tokenStream);
        LittleCalcInterpListener listener = new LittleCalcInterpListener();
        parser.addParseListener(listener);
        String input = "";
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
                lexer = new LittleCalcLexer(charStream);
                tokenStream = new CommonTokenStream(lexer);
                parser.setTokenStream(tokenStream);
                try {
                    parser.replIn();
                } catch (LittleCalcRuntimeException lcre) {
                    Logger.error(lcre.getMessage());
                }
                input = "";
            }
        }
        System.out.println("Exiting...");
        keyboard.close();
    }
}