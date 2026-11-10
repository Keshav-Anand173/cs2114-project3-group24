package model;

import java.util.NoSuchElementException;

/**
 * The restaurant's waitlist: a singly linked FIFO queue of parties, plus the
 * restaurant's tables. The model never prints — it reports problems by
 * throwing exceptions, which the controller catches and passes to the view.
 */
public class Waitlist {

    /** One link in the singly linked queue. Package-private, never exposed. */
    private static final class Node {
        private final Party party;
        private Node next;

        private Node(Party party) {
            this.party = party;
        }
    }

    private Node firstNode;
    private Node lastNode;
    private int numberOfEntries;

    private final Table[] tables;

    public Waitlist(int numberOfTables) {
        if (numberOfTables < 1) {
            throw new IllegalArgumentException("Number of tables must be 1 or greater.");
        }
        this.firstNode = null;
        this.lastNode = null;
        this.numberOfEntries = 0;
        this.tables = new Table[numberOfTables];
        for (int i = 0; i < numberOfTables; i++) {
            tables[i] = new Table(i + 1);
        }
    }

    // ----- Queue operations -----

    public void enqueue(Party newParty) {
        if (newParty == null) {
            throw new IllegalArgumentException("Cannot enqueue a null party.");
        }
        Node newNode = new Node(newParty);
        if (isEmpty()) {
            firstNode = newNode;
        } else {
            lastNode.next = newNode;
        }
        lastNode = newNode;
        numberOfEntries++;
    }

    public Party dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot dequeue from an empty waitlist.");
        }
        Party front = firstNode.party;
        firstNode = firstNode.next;
        numberOfEntries--;
        if (isEmpty()) {
            // The queue just emptied out; reset the back reference too so the
            // next enqueue starts a fresh chain instead of pointing at a stale node.
            lastNode = null;
        }
        return front;
    }

    public Party peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Cannot peek an empty waitlist.");
        }
        return firstNode.party;
    }

    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    public int size() {
        return numberOfEntries;
    }

    public Party[] toArray() {
        Party[] result = new Party[numberOfEntries];
        Node current = firstNode;
        int i = 0;
        while (current != null) {
            result[i] = current.party;
            current = current.next;
            i++;
        }
        return result;
    }

    // ----- Table operations -----

    public int countOpenTables() {
        int count = 0;
        for (Table table : tables) {
            if (table.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public boolean hasOpenTable() {
        return countOpenTables() > 0;
    }

    public int seatNextParty() {
        if (isEmpty()) {
            throw new NoSuchElementException("No parties are waiting.");
        }
        Table openTable = findLowestNumberedOpenTable();
        if (openTable == null) {
            throw new IllegalStateException("No tables are open.");
        }
        dequeue();
        openTable.occupy();
        return openTable.getTableId();
    }

    public void freeTable(int tableId) {
        if (tableId < 1 || tableId > tables.length) {
            throw new IllegalArgumentException("Table ID out of range: " + tableId);
        }
        tables[tableId - 1].free();
    }

    public boolean[] getTableStatuses() {
        boolean[] statuses = new boolean[tables.length];
        for (int i = 0; i < tables.length; i++) {
            statuses[i] = tables[i].isAvailable();
        }
        return statuses;
    }

    private Table findLowestNumberedOpenTable() {
        for (Table table : tables) {
            if (table.isAvailable()) {
                return table;
            }
        }
        return null;
    }
}
