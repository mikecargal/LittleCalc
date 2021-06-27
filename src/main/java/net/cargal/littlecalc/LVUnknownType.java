package net.cargal.littlecalc;

import net.cargal.littlecalc.exceptions.LittleCalcImplementationException;

public class LVUnknownType extends LVType {
    public static final LVUnknownType INSTANCE = new LVUnknownType();

    private LVUnknownType() {
    }

    @Override
    boolean canCompareTo(LVType other) {
        throw new LittleCalcImplementationException(
                "Should never even ask if an Unknown can be compared to another type");
    }

}
