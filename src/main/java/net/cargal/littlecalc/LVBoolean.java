package net.cargal.littlecalc;

import org.antlr.v4.runtime.ParserRuleContext;

public class LVBoolean extends LittleValue {
    private Boolean value;

    public LVBoolean(Boolean value, int line, int column) {
        super(line, column);
        this.value = value;
    }

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
    public String type() {
        return "BOOLEAN";
    }

    @Override
    public int compareTo(LittleValue o) {
        return 1; //
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
