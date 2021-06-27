package net.cargal.littlecalc;

import net.cargal.littlecalc.exceptions.LittleCalcImplementationException;

public class LVBooleanType extends LVType {
    public static final LVBooleanType INSTANCE = new LVBooleanType();

    private LVBooleanType() {
    }

    @Override
    boolean canCompareTo(LVType other) {
        throw new LittleCalcImplementationException(
                "Should never even ask if a Boolean can be compared to another type");
    }

    @Override
    public String toString() {
        return "BOOLEAN";
    }
}