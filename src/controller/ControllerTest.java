package controller;

import model.Party;
import org.junit.jupiter.api.Test;
import view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Drives the Controller with scripted input and a recording View, so nothing
 * is printed and every call the controller makes to the view can be checked.
 * Every script must end by choosing menu option 5 (Quit), otherwise the
 * controller would run out of input.
 */
class ControllerTest {

    /** A View that records what the controller asks it to show. */
    private static class RecordingView extends View {
        final List<String> messages = new ArrayList<>();
        final List<String> errors = new ArrayList<>();
        final List<String> prompts = new ArrayList<>();
        final List<String> seated = new ArrayList<>();
        final List<Party[]> waitlistsShown = new ArrayList<>();
        final List<boolean[]> tablesShown = new ArrayList<>();
        int menuCount = 0;

        @Override
        public void displayMenu() {
            menuCount++;
        }

        @Override
        public void showPrompt(String prompt) {
            prompts.add(prompt);
        }

        @Override
        public void displayWaitlist(Party[] parties) {
            waitlistsShown.add(parties);
        }

        @Override
        public void displayTables(boolean[] tableStatuses) {
            tablesShown.add(tableStatuses);
        }

        @Override
        public void showSeated(String name, int tableId) {
            seated.add(name + "@" + tableId);
        }

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        @Override
        public void showError(String message) {
            errors.add(message);
        }
    }

    private RecordingView run(String... lines) {
        RecordingView view = new RecordingView();
        Scanner scanner = new Scanner(String.join("\n", lines) + "\n");
        new Controller(view, scanner).start();
        return view;
    }

    private static Party[] lastWaitlist(RecordingView view) {
        return view.waitlistsShown.get(view.waitlistsShown.size() - 1);
    }

    private static boolean[] lastTables(RecordingView view) {
        return view.tablesShown.get(view.tablesShown.size() - 1);
    }

    // ----- start(): table count and main loop -----

    @Test
    void start_validTableCount_thenQuit() {
        RecordingView view = run("3", "5");
        assertTrue(view.errors.isEmpty());
        assertEquals(1, view.menuCount);
        assertTrue(view.messages.contains("Goodbye."));
    }

    @Test
    void start_badTableCount_repromptsUntilValid() {
        // not a number, then below the minimum, then valid (3 tables)
        RecordingView view = run("abc", "0", "3", "4", "5");
        assertEquals(List.of(
                "Please enter a whole number.",
                "Number of tables must be 1 or greater."), view.errors);
        assertEquals(3, lastTables(view).length);
    }

    @Test
    void start_invalidMenuChoice_showsErrorAndContinues() {
        RecordingView view = run("2", "99", "x", "5");
        assertEquals(List.of(
                "Please choose a number from 1 to 5.",
                "Please enter a whole number."), view.errors);
        assertTrue(view.messages.contains("Goodbye."));
        assertEquals(2, view.menuCount); // 99 re-shows the menu; "x" never reaches it
    }

    // ----- addParty -----

    @Test
    void addParty_normalCase() {
        RecordingView view = run("2", "1", "Smith", "4", "4", "5");
        assertTrue(view.messages.contains("Smith has been added to the waitlist."));
        assertTrue(view.errors.isEmpty());
        Party[] shown = lastWaitlist(view);
        assertEquals(1, shown.length);
        assertEquals("Smith", shown[0].getName());
        assertEquals(4, shown[0].getNumberOfGuests());
    }

    @Test
    void addParty_blankName_showsErrorAndNothingAdded() {
        RecordingView view = run("2", "1", "   ", "4", "4", "5");
        assertEquals(List.of("Party name cannot be blank."), view.errors);
        assertEquals(0, lastWaitlist(view).length);
    }

    @Test
    void addParty_sizeOutOfRange_showsErrorAndNothingAdded() {
        RecordingView view = run(
                "2",
                "1", "Smith", "0",
                "1", "Smith", "21",
                "4", "5");
        assertEquals(List.of(
                "Party size must be between 1 and 20.",
                "Party size must be between 1 and 20."), view.errors);
        assertEquals(0, lastWaitlist(view).length);
    }

    @Test
    void addParty_nonIntegerSize_repromptsUntilValid() {
        RecordingView view = run("2", "1", "Smith", "many", "3", "4", "5");
        assertEquals(List.of("Please enter a whole number."), view.errors);
        assertEquals(3, lastWaitlist(view)[0].getNumberOfGuests());
    }

    @Test
    void addParty_multipleParties_keepFifoOrder() {
        RecordingView view = run("2", "1", "A", "2", "1", "B", "3", "4", "5");
        Party[] shown = lastWaitlist(view);
        assertEquals(2, shown.length);
        assertEquals("A", shown[0].getName());
        assertEquals("B", shown[1].getName());
    }

    // ----- seatNextParty -----

    @Test
    void seatNextParty_emptyWaitlist_showsMessage() {
        RecordingView view = run("2", "2", "5");
        assertTrue(view.messages.contains("The waitlist is empty. No one to seat."));
        assertTrue(view.seated.isEmpty());
    }

    @Test
    void seatNextParty_normalCase_seatsAtLowestOpenTable() {
        RecordingView view = run(
                "2",
                "1", "A", "2",
                "1", "B", "2",
                "2",
                "2",
                "5");
        assertEquals(List.of("A@1", "B@2"), view.seated);
        assertTrue(view.errors.isEmpty());
    }

    @Test
    void seatNextParty_noOpenTable_showsMessageAndPartyStaysQueued() {
        RecordingView view = run(
                "1",
                "1", "A", "2",
                "2",              // A takes the only table
                "1", "B", "2",
                "2",              // no table left
                "4",
                "5");
        assertEquals(List.of("A@1"), view.seated);
        assertTrue(view.messages.contains("No tables are open right now."));
        Party[] shown = lastWaitlist(view);
        assertEquals(1, shown.length);
        assertEquals("B", shown[0].getName());
    }

    // ----- freeTable -----

    @Test
    void freeTable_normalCase() {
        RecordingView view = run(
                "1",
                "1", "A", "2",
                "2",
                "3", "1",
                "4",
                "5");
        assertTrue(view.messages.contains("Table 1 is now open."));
        assertTrue(view.errors.isEmpty());
        assertTrue(lastTables(view)[0]);
    }

    @Test
    void freeTable_idOutOfRange_showsError() {
        RecordingView view = run("2", "3", "5", "3", "0", "5");
        assertEquals(List.of(
                "Table ID out of range: 5",
                "Table ID out of range: 0"), view.errors);
    }

    @Test
    void freeTable_alreadyOpen_showsError() {
        RecordingView view = run("2", "3", "1", "5");
        assertEquals(List.of("Table 1 is already open."), view.errors);
    }

    @Test
    void freeTable_nonIntegerId_repromptsUntilValid() {
        RecordingView view = run("1", "3", "abc", "1", "5");
        assertEquals(List.of(
                "Please enter a whole number.",
                "Table 1 is already open."), view.errors);
    }

    @Test
    void freeTable_thenSeatNextParty_reusesFreedTable() {
        RecordingView view = run(
                "1",
                "1", "A", "2",
                "2",              // A at table 1
                "1", "B", "2",
                "3", "1",         // free table 1
                "2",              // B at table 1
                "5");
        assertEquals(List.of("A@1", "B@1"), view.seated);
    }

    // ----- showStatus -----

    @Test
    void showStatus_emptyRestaurant() {
        RecordingView view = run("2", "4", "5");
        assertEquals(0, lastWaitlist(view).length);
        assertArrayEquals(new boolean[] { true, true }, lastTables(view));
    }

    @Test
    void showStatus_reflectsWaitlistAndTables() {
        RecordingView view = run(
                "3",
                "1", "A", "2",
                "2",
                "1", "B", "5",
                "4",
                "5");
        Party[] shown = lastWaitlist(view);
        assertEquals(1, shown.length);
        assertEquals("B", shown[0].getName());
        assertArrayEquals(new boolean[] { false, true, true }, lastTables(view));
    }
}
