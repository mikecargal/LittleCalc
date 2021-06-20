package net.cargal.littlecalc;

import java.io.IOException;
import java.util.Scanner;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader.Option;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class LittleCalcRepl {
    private static final String INITIAL_PROMPT = "> ";
    private static final String CONTINUE_PROMPT = "| ";

    public static void main(String... args) throws IOException {
        var prompt = INITIAL_PROMPT;
        Terminal terminal = TerminalBuilder.terminal();
        LineReader lineReader = LineReaderBuilder.builder() //
                .terminal(terminal) //
                .option(Option.DISABLE_EVENT_EXPANSION, true) //
                .build();

        var keyboard = new Scanner(System.in);
        var charStream = CharStreams.fromString("");
        var lexer = new LittleCalcLexer(charStream);
        var tokenStream = new CommonTokenStream(lexer);
        var parser = new LittleCalcParser(tokenStream);
        var listener = new LittleCalcREPLListener(parser);
        parser.removeErrorListeners();
        var replErrListener = new LittleReplErrorListener();
        parser.addErrorListener(replErrListener);

        var input = "";
        while (true) {
            input += lineReader.readLine(prompt);
            if (input.equals("quit")) {
                break;
            }
            if (lineReader.getParsedLine().line().trim().endsWith("\\")) {
                prompt = CONTINUE_PROMPT;
            } else {
                charStream = CharStreams.fromString(input);
                lexer.setInputStream(charStream);
                tokenStream.setTokenSource(lexer);
                parser.setTokenStream(tokenStream);
                parser.setTrace(listener.isTracing());
                var replTree = parser.replIn();
                if (replErrListener.canProcessReplInput()) {
                    ParseTreeWalker.DEFAULT.walk(listener, replTree);
                }
                if (replErrListener.incompleteInput()) {
                    prompt = CONTINUE_PROMPT;
                } else {
                    input = "";
                    prompt = INITIAL_PROMPT;
                }
                replErrListener.reset();
            }
        }
        System.out.println("Exiting...");
        keyboard.close();
    }
}