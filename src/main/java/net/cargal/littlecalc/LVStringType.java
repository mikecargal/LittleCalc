package net.cargal.littlecalc;

public class LVStringType extends LVType {
    public static final LVStringType INSTANCE = new LVStringType();

    private LVStringType() {
    }

    @Override
    boolean canEquateTo(LVType other) {
        return other instanceof LVStringType;
    }

    @Override
    boolean canCompareTo(LVType other) {
        return other instanceof LVStringType;
    }

}