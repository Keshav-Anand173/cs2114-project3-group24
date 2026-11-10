package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    @Test
    void constructor_normalCase() {
        Table table = new Table(3);
        assertEquals(3, table.getTableId());
        assertTrue(table.isAvailable());
    }

    @Test
    void constructor_invalidId_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Table(0));
    }

    @Test
    void occupy_normalCase() {
        Table table = new Table(1);
        table.occupy();
        assertFalse(table.isAvailable());
    }

    @Test
    void occupy_alreadyOccupied_throws() {
        Table table = new Table(1);
        table.occupy();
        assertThrows(IllegalStateException.class, table::occupy);
    }

    @Test
    void free_normalCase() {
        Table table = new Table(1);
        table.occupy();
        table.free();
        assertTrue(table.isAvailable());
    }

    @Test
    void free_alreadyOpen_throws() {
        Table table = new Table(1);
        assertThrows(IllegalStateException.class, table::free);
    }
}
