package net.cargal.littlecalc;

import java.io.IOException;
import java.util.Scanner;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
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
    private static String prompt;
    private static CodePointCharStream charStream;
    private static LittleCalcLexer lexer;
    private static CommonTokenStream tokenStream;
    private static LittleCalcParser parser;
    private static LittleCalcREPLVisitor executionVisitor;
    private static LittleReplErrorListener replErrListener;
    private static LittleCalcSemanticValidationListener listener;

    public static void main(String... args) throws IOException {
        prompt = INITIAL_PROMPT;
        Terminal terminal = TerminalBuilder.terminal();
        LineReader lineReader = LineReaderBuilder.builder() //
                .terminal(terminal) //
                .option(Option.DISABLE_EVENT_EXPANSION, true) //
                .build();

        charStream = CharStreams.fromString("");
        lexer = new LittleCalcLexer(charStream);
        tokenStream = new CommonTokenStream(lexer);
        parser = new LittleCalcParser(tokenStream);
        listener = new LittleCalcSemanticValidationListener();
        executionVisitor = new LittleCalcREPLVisitor(parser);
        parser.removeErrorListeners();
        replErrListener = new LittleReplErrorListener();
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
                input = process(input);

            }
        }
        System.out.println("Exiting...");

    }

    private static String process(String input) {
        charStream = CharStreams.fromString(input);
        lexer.setInputStream(charStream);
        tokenStream.setTokenSource(lexer);
        parser.setTokenStream(tokenStream);
        parser.setTrace(executionVisitor.isTracing());
        var replTree = parser.replIn();
        if ((!replErrListener.incompleteInput()) && //
                (parser.getNumberOfSyntaxErrors() == 0)) {
            ParseTreeWalker.DEFAULT.walk(listener, replTree);
            if (!listener.hasErrors()) {
                executionVisitor.visit(replTree);
            }
        }
        if (replErrListener.incompleteInput()) {
            prompt = CONTINUE_PROMPT;
        } else {
            prompt = INITIAL_PROMPT;
            input = "";
        }
        replErrListener.reset();
        listener.reset();
        return input;
    }
}