package net.cargal.littlecalc;

public class LVUnknownType extends LVType {
    public static final LVUnknownType INSTANCE = new LVUnknownType();

    private LVUnknownType() {
    }

    @Override
    boolean canCompareTo(LVType other) {
        return other instanceof LVNumberType;
    }

}
