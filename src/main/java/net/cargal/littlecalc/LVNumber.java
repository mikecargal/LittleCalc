package net.cargal.littlecalc;

import org.antlr.v4.runtime.ParserRuleContext;

public class LVNumber extends LittleValue {
    private Double value;

    public LVNumber(Double value, ParserRuleContext ctx) {
        super(ctx);
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
    public String type() {
        return "NUMBER";
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
