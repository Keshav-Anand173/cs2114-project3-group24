package model;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class WaitlistTest {

    @Test
    void constructor_normalCase() {
        Waitlist waitlist = new Waitlist(5);
        assertEquals(5, waitlist.countOpenTables());
        assertTrue(waitlist.isEmpty());
    }

    @Test
    void constructor_badInput_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Waitlist(0));
        assertThrows(IllegalArgumentException.class, () -> new Waitlist(-1));
    }

    @Test
    void enqueue_normalCase() {
        Waitlist waitlist = new Waitlist(2);
        Party a = new Party("A", 2);
        Party b = new Party("B", 2);
        waitlist.enqueue(a);
        waitlist.enqueue(b);
        assertEquals(2, waitlist.size());
        assertSame(a, waitlist.peek());
    }

    @Test
    void enqueue_null_throws() {
        Waitlist waitlist = new Waitlist(2);
        assertThrows(IllegalArgumentException.class, () -> waitlist.enqueue(null));
    }

    @Test
    void dequeue_normalCase_fifoOrderAndFrontBackResetCorrectly() {
        Waitlist waitlist = new Waitlist(2);
        Party a = new Party("A", 2);
        Party b = new Party("B", 2);
        Party c = new Party("C", 2);
        waitlist.enqueue(a);
        waitlist.enqueue(b);
        waitlist.enqueue(c);

        assertSame(a, waitlist.dequeue());
        assertSame(b, waitlist.dequeue());
        assertSame(c, waitlist.dequeue());
        assertTrue(waitlist.isEmpty());

        // Queue fully drained to empty; a new enqueue must still work, which
        // checks that firstNode/lastNode were reset correctly.
        Party d = new Party("D", 2);
        waitlist.enqueue(d);
        assertSame(d, waitlist.peek());
        assertEquals(1, waitlist.size());
    }

    @Test
    void dequeue_empty_throws() {
        Waitlist waitlist = new Waitlist(2);
        assertThrows(NoSuchElementException.class, waitlist::dequeue);
    }

    @Test
    void peek_normalCase() {
        Waitlist waitlist = new Waitlist(2);
        Party a = new Party("A", 2);
        waitlist.enqueue(a);
        assertSame(a, waitlist.peek());
        assertEquals(1, waitlist.size());
    }

    @Test
    void peek_empty_throws() {
        Waitlist waitlist = new Waitlist(2);
        assertThrows(NoSuchElementException.class, waitlist::peek);
    }

    @Test
    void isEmpty_trueThenFalseThenTrueAgain() {
        Waitlist waitlist = new Waitlist(2);
        assertTrue(waitlist.isEmpty());
        waitlist.enqueue(new Party("A", 2));
        assertFalse(waitlist.isEmpty());
        waitlist.dequeue();
        assertTrue(waitlist.isEmpty());
    }

    @Test
    void size_normalCase() {
        Waitlist waitlist = new Waitlist(2);
        waitlist.enqueue(new Party("A", 2));
        waitlist.enqueue(new Party("B", 2));
        waitlist.enqueue(new Party("C", 2));
        assertEquals(3, waitlist.size());
    }

    @Test
    void size_empty() {
        Waitlist waitlist = new Waitlist(2);
        assertEquals(0, waitlist.size());
    }

    @Test
    void toArray_normalCase_fifoOrder() {
        Waitlist waitlist = new Waitlist(2);
        Party a = new Party("A", 2);
        Party b = new Party("B", 2);
        waitlist.enqueue(a);
        waitlist.enqueue(b);
        assertArrayEquals(new Party[] { a, b }, waitlist.toArray());
    }

    @Test
    void toArray_empty_returnsLengthZeroNotNull() {
        Waitlist waitlist = new Waitlist(2);
        Party[] result = waitlist.toArray();
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    void countOpenTablesAndHasOpenTable_normalCase() {
        Waitlist waitlist = new Waitlist(3);
        waitlist.enqueue(new Party("A", 2));
        waitlist.seatNextParty();
        assertEquals(2, waitlist.countOpenTables());
        assertTrue(waitlist.hasOpenTable());
    }

    @Test
    void countOpenTablesAndHasOpenTable_allSeated() {
        Waitlist waitlist = new Waitlist(1);
        waitlist.enqueue(new Party("A", 2));
        waitlist.seatNextParty();
        assertEquals(0, waitlist.countOpenTables());
        assertFalse(waitlist.hasOpenTable());
    }

    @Test
    void seatNextParty_normalCase() {
        Waitlist waitlist = new Waitlist(2);
        waitlist.enqueue(new Party("A", 2));
        int tableId = waitlist.seatNextParty();
        assertEquals(1, tableId);
        assertTrue(waitlist.isEmpty());
        assertFalse(waitlist.getTableStatuses()[0]);
    }

    @Test
    void seatNextParty_emptyWaitlist_throws() {
        Waitlist waitlist = new Waitlist(2);
        assertThrows(NoSuchElementException.class, waitlist::seatNextParty);
    }

    @Test
    void seatNextParty_noOpenTable_throwsAndQueueUnchanged() {
        Waitlist waitlist = new Waitlist(1);
        waitlist.enqueue(new Party("A", 2));
        waitlist.seatNextParty(); // takes the only table
        waitlist.enqueue(new Party("B", 2));

        assertThrows(IllegalStateException.class, waitlist::seatNextParty);
        assertEquals(1, waitlist.size());
        assertEquals("B", waitlist.peek().getName());
    }

    @Test
    void freeTable_normalCase() {
        Waitlist waitlist = new Waitlist(2);
        waitlist.enqueue(new Party("A", 2));
        waitlist.seatNextParty();
        int before = waitlist.countOpenTables();
        waitlist.freeTable(1);
        assertEquals(before + 1, waitlist.countOpenTables());
    }

    @Test
    void freeTable_idOutOfRange_throws() {
        Waitlist waitlist = new Waitlist(2);
        assertThrows(IllegalArgumentException.class, () -> waitlist.freeTable(0));
        assertThrows(IllegalArgumentException.class, () -> waitlist.freeTable(3));
    }

    @Test
    void freeTable_alreadyOpen_throws() {
        Waitlist waitlist = new Waitlist(2);
        assertThrows(IllegalStateException.class, () -> waitlist.freeTable(1));
    }

    @Test
    void getTableStatuses_normalCase() {
        Waitlist waitlist = new Waitlist(3);
        waitlist.enqueue(new Party("A", 2));
        waitlist.seatNextParty();
        assertArrayEquals(new boolean[] { false, true, true }, waitlist.getTableStatuses());
    }

    @Test
    void getTableStatuses_returnsCopy_modifyingItDoesNotChangeModel() {
        Waitlist waitlist = new Waitlist(2);
        boolean[] statuses = waitlist.getTableStatuses();
        statuses[0] = false;
        assertTrue(waitlist.getTableStatuses()[0]);
    }
}
