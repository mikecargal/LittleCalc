package net.cargal.littlecalc.exceptions;

public class LittleCalcRuntimeException extends RuntimeException {

    public LittleCalcRuntimeException(String message,int line, int column) {
        super("line:"+line+" col:"+ (column+1)+" -- "+message);
    }

}
