package net.cargal.littlecalc;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SymbolTable {
    private Map<String, LittleValue> symbols = new HashMap<>();
    private SymbolTable parent;

    public SymbolTable() {
        this(null);
    }

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public LittleValue get(String id) {
        return symbols.computeIfAbsent(id, k -> (parent == null) ? null : parent.get(k));
    }

    public void put(String key, LittleValue value) {
        symbols.put(key, value);
    }

    public Stream<String> keyStream() {
        return Stream.concat( //
                symbols.keySet().stream(), //
                (parent != null) ? parent.keyStream() : Stream.empty());
    }
}
