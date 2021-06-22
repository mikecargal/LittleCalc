package net.cargal.littlecalc;

public abstract class LVType {

    abstract boolean canEquateTo(LVType other);

    abstract boolean canCompareTo(LVType other);
}
