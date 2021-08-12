package net.cargal.littlecalc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class SymbolTable<T> {
    private final Map<String, T> symbols = new HashMap<>();
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<SymbolTable<T>> parent;

    public SymbolTable() {
        this(null);
    }

    public SymbolTable(SymbolTable<T> parent) {
        this.parent = Optional.ofNullable(parent);
    }

    public Optional<T> get(String id) {
        var res = symbols.get(id);
        if (res != null)
            return Optional.of(res);
        if (parent.isPresent())
            return parent.get().get(id);
        return Optional.empty();
    }

    public void put(String key, T value) {
        symbols.put(key, value);
    }

    public Stream<String> keyStream() {
        return Stream.concat( //
                symbols.keySet().stream(), //
                (parent.isPresent()) ? parent.get().keyStream() : Stream.empty());
    }

    public Optional<SymbolTable<T>> parent() {
        return parent;
    }
}
