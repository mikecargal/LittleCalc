package net.cargal.littlecalc.exceptions;

public class LittleCalcImplementationException extends RuntimeException {

    public LittleCalcImplementationException(String message) {
        super(message+" (this is an indication of an implementation error, not user error)");
    }

}
