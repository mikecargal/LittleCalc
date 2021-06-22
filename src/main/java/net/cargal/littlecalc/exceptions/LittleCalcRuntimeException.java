package net.cargal.littlecalc.exceptions;

public class LittleCalcRuntimeException extends RuntimeException {

    public LittleCalcRuntimeException(String message, int line, int column) {
        super(message(message, line, column));
    }

    public static String message(String message, int line, int column) {
        return "line:" + line + " col:" + (column + 1) + " -- " + message;
    }

}
