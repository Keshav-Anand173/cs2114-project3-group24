package model;

/**
 * A single restaurant table: an ID and whether it is currently available.
 */
public class Table {

    private final int tableId;
    private boolean available;

    public Table(int tableId) {
        if (tableId < 1) {
            throw new IllegalArgumentException("Table ID must be 1 or greater.");
        }
        this.tableId = tableId;
        this.available = true;
    }

    public int getTableId() {
        return tableId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void occupy() {
        if (!available) {
            throw new IllegalStateException("Table " + tableId + " is already occupied.");
        }
        available = false;
    }

    public void free() {
        if (available) {
            throw new IllegalStateException("Table " + tableId + " is already open.");
        }
        available = true;
    }
}
