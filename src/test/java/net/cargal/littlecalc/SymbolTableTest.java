package net.cargal.littlecalc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class SymbolTableTest {
    private static final String PARENT_AAA = "ParentAAA";
    private static final String AAA = "AAA";
    private static final int LINE = 1;
    private static final int COLUMN = 3;

    SymbolTable<LittleValue> root;
    SymbolTable<LittleValue> child;
    LittleValue lvPA;
    LittleValue lvA;
    LittleValue lvB;

    @BeforeEach
    void before() {

        lvA = LittleValue.stringValue(AAA, LINE, COLUMN);
        lvB = LittleValue.stringValue(AAA, LINE, COLUMN);
        lvPA = LittleValue.stringValue(PARENT_AAA, LINE, COLUMN);

        root = new SymbolTable<>();
        child = new SymbolTable<>(root);

        root.put(PARENT_AAA, lvPA);
        root.put(AAA, lvA);
    }

    @Test
    void testInstantiation() {
        assertEquals(Optional.empty(), root.parent());
        assertTrue(child.parent().isPresent());
        assertEquals(root, child.parent().get());
    }

    @Test
    void testDirectGet() {
        assertEquals(lvPA, root.get(PARENT_AAA).get());
        assertEquals(lvA, child.get(AAA).get());
    }

    @Test
    void testGetFromParent() {
        assertEquals(lvPA, child.get(PARENT_AAA).get());
    }

    @Test
    void testLocalOverride() {
        child.put(PARENT_AAA, lvB);
        assertEquals(lvB, child.get(PARENT_AAA).get());
    }

    @Test
    void testNotFound() {
        assertEquals(Optional.empty(), root.get("NotThere"));
        assertEquals(Optional.empty(), child.get("NotThere"));
    }

    @Test
    void testKeyStream() {
        assertEquals("AAA, ParentAAA", child.keyStream().collect(Collectors.joining(", ")));
    }

}
