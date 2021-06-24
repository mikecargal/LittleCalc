package net.cargal.littlecalc;

public class LVNumberType extends LVType {
    public static final LVNumberType INSTANCE = new LVNumberType();

    private LVNumberType() {
    }

    @Override
    boolean canCompareTo(LVType other) {
        return other instanceof LVNumberType;
    }

    @Override
    public String toString() {
        return "NUMBER";
    }
}
