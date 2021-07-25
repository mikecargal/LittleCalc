package net.cargal.littlecalc;

public class LVNumber extends LittleValue {
    private final Double value;

    public LVNumber(Double value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    @Override
    protected Object getValueObject() {
        return value;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public Double number() {
        return value;
    }

    @Override
    public LVType type() {
        return LVNumberType.INSTANCE;
    }

    @Override
    public int compareTo(LittleValue o) {
        return number().compareTo(o.number());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LVNumber))
            return false;
        return number().equals(((LittleValue) obj).number());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
