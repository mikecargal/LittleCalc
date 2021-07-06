package net.cargal.littlecalc;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class LittleReplErrorListener extends ConsoleErrorListener {
    private boolean errorAtEOF = false;

    @Override
    public void syntaxError( //
            Recognizer<?, ?> recognizer, //
            Object offendingSymbol, //
            int line, //
            int charPositionInLine, //
            String msg, //
            RecognitionException e) {
        if (offendingSymbol instanceof Token && //
                ((Token) offendingSymbol).getType() == Recognizer.EOF) {
            errorAtEOF = true;
        } else {
            super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
        }
    }

    public void reset() {
        errorAtEOF = false;
    }

    public boolean completeInput() {
        return !errorAtEOF;
    }

}
