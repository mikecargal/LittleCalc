package net.cargal.littlecalc;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

import net.cargal.littlecalc.LittleCalcParser.ReplInContext;

public class LittleCalcRepl {
    private static final String INITIAL_PROMPT = "> ";
    private static final String CONTINUE_PROMPT = "| ";
    private static final String INPUT_CONTINUE_SUFFIX = "\\";

    private LittleCalcLexer lexer;
    private CommonTokenStream tokenStream;
    private LittleCalcParser parser;
    private LittleCalcREPLVisitor executionVisitor;
    private LittleReplErrorListener replErrListener;
    private LittleCalcSemanticValidationListener listener;

    public static void main(String... args) throws IOException {
        new LittleCalcRepl().run(System.in);
    }

    void run(InputStream inputStream) throws IOException {
        var lineReader = getLineReader(inputStream);
        initParser();
        var parseString = "";
        while (true) {
            var input = getInput(lineReader, parseString);
            if (input.trim().equals("quit"))
                break;
            parseString = process(parseString + input);
        }
        System.out.println("Exiting...");
    }

    LineReader getLineReader(InputStream inputStream) throws IOException {
        var terminal = TerminalBuilder.builder() //
                .streams(inputStream, System.out) //
             //   .jna(true) //
                .build();
        return LineReaderBuilder.builder() //
                .terminal(terminal).option(Option.DISABLE_EVENT_EXPANSION, true) //
                .build();
    }

    String getInput(LineReader lineReader, String existingSrc) {
        return lineReader.readLine(existingSrc.isEmpty() ? INITIAL_PROMPT : CONTINUE_PROMPT);
    }

    private void initParser() {
        lexer = new LittleCalcLexer(CharStreams.fromString(""));
        tokenStream = new CommonTokenStream(lexer);
        parser = new LittleCalcParser(tokenStream);
        listener = new LittleCalcSemanticValidationListener();
        executionVisitor = new LittleCalcREPLVisitor(parser);

        replErrListener = new LittleReplErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(replErrListener);
    }

    private String process(String source) {
        if (source.trim().endsWith(INPUT_CONTINUE_SUFFIX)) {
            return source.substring(0, source.lastIndexOf(INPUT_CONTINUE_SUFFIX));
        }
        var result = source;
        var replTree = parseInput(source);
        if (replErrListener.completeInput()) {
            result = "";
            if (parser.getNumberOfSyntaxErrors() == 0) {
                ParseTreeWalker.DEFAULT.walk(listener, replTree);
                if (!listener.hasErrors()) {
                    executionVisitor.visit(replTree);
                }
            }
        }
        replErrListener.reset();
        listener.reset();
        return result;
    }

    private ReplInContext parseInput(String source) {
        lexer.setInputStream(CharStreams.fromString(source));
        tokenStream.setTokenSource(lexer);
        parser.setTokenStream(tokenStream);
        parser.setTrace(executionVisitor.isTracing());
        return parser.replIn();
    }

}