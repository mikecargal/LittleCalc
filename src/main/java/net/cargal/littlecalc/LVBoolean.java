package net.cargal.littlecalc;

import net.cargal.littlecalc.exceptions.LittleCalcImplementationException;
import net.cargal.littlecalc.exceptions.LittleCalcRuntimeException;

public class LVBoolean extends LittleValue {
    private Boolean value;

    public LVBoolean(Boolean value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    public static final LVBoolean TRUE = new LVBoolean(true, 0, 0);
    public static final LVBoolean FALSE = new LVBoolean(false, 0, 0);

    @Override
    protected Object getValueObject() {
        return value;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }

    @Override
    public boolean bool() {
        return value;
    }

    @Override
    public LVType type() {
        return LVBooleanType.INSTANCE;
    }

    @Override
    public int compareTo(LittleValue o) {
        throw new LittleCalcImplementationException("Should not be attempting to compare a value to Boolean");
    }

    @Override
    public boolean evalCompare(LVComparableOp op, LittleValue rhs) {
        throw new LittleCalcRuntimeException(
                "Comparison operator ('" + op.getText() + "') is not valid for BOOLEAN values", line, column);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LVBoolean))
            return false;
        return bool() == ((LittleValue) obj).bool();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
