package net.cargal.littlecalc;

import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class LittleReplErrorListener extends ConsoleErrorListener {
    private boolean errorAtEOF = false;
    private boolean hasSyntaxError = false;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        if (e != null && e.getOffendingToken().getType() == Recognizer.EOF) {
            errorAtEOF = true;
        } else {
            hasSyntaxError = true;
            super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
        }
    }

    public void reset() {
        errorAtEOF = false;
        hasSyntaxError = false;
    }

    public boolean incompleteInput() {
        return errorAtEOF && !hasSyntaxError;
    }

    public boolean canProcessReplInput() {
        return !hasSyntaxError && !errorAtEOF;
    }
}
