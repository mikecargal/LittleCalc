package net.cargal.littlecalc;

public class LVBooleanType extends LVType {
    public static final LVBooleanType INSTANCE = new LVBooleanType();

    private LVBooleanType() {
    }

    @Override
    boolean canEquateTo(LVType other) {
        return other instanceof LVBooleanType;
    }

    @Override
    boolean canCompareTo(LVType other) {
        return false;
    }

}