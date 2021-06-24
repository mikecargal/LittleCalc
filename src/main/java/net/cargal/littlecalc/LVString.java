package net.cargal.littlecalc;

import org.antlr.v4.runtime.ParserRuleContext;

public class LVString extends LittleValue {
    private String value;

    public LVString(String value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    @Override
    protected Object getValueObject() {
        return value;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public String string() {
        return value;
    }

    @Override
    public LVType type() {
        return LVStringType.INSTANCE;
    }

    @Override
    public int compareTo(LittleValue o) {
        return string().compareTo(o.string());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LVString))
            return false;
        return string() == ((LittleValue) obj).string();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
