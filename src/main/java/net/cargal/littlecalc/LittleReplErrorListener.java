package net.cargal.littlecalc;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class LittleReplErrorListener extends BaseErrorListener {
    private boolean errorAtEOF = false;
    private boolean hasSyntaxError = false;

    @Override
    public void syntaxError( //
            Recognizer<?, ?> recognizer, //
            Object offendingSymbol, //
            int line, //
            int charPositionInLine, //
            String msg, //
            RecognitionException e) {
        if (e != null && e.getOffendingToken().getType() == Recognizer.EOF) {
            errorAtEOF = true;
        } else {
            hasSyntaxError = true;
            reportError("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }

    protected void reportError(String str) {
        System.err.println(str);
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
