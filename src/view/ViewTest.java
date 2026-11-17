package view;

import model.Party;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/** Captures System.out so the View's exact output can be checked. */
class ViewTest {

    private static final String NL = System.lineSeparator();

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;
    private View view;

    @BeforeEach
    void redirectStdout() {
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        view = new View();
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    private String output() {
        System.out.flush();
        return captured.toString();
    }

    @Test
    void displayMenu_listsAllFiveOptions() {
        view.displayMenu();
        String out = output();
        assertTrue(out.contains("=== Restaurant Waitlist ==="));
        assertTrue(out.contains("1. Add a party to the waitlist"));
        assertTrue(out.contains("2. Seat the next party"));
        assertTrue(out.contains("3. Free a table"));
        assertTrue(out.contains("4. Show waitlist and table status"));
        assertTrue(out.contains("5. Quit"));
    }

    @Test
    void showPrompt_printsWithoutTrailingNewline() {
        view.showPrompt("Party name: ");
        assertEquals("Party name: ", output());
    }

    @Test
    void displayWaitlist_empty_showsEmptyMarker() {
        view.displayWaitlist(new Party[0]);
        assertEquals("--- Current Waitlist ---" + NL + "(empty)" + NL, output());
    }

    @Test
    void displayWaitlist_numbersPartiesInOrder() {
        view.displayWaitlist(new Party[] { new Party("Smith", 4), new Party("Lee", 2) });
        assertEquals("--- Current Waitlist ---" + NL
                + "1. Smith (4 guests)" + NL
                + "2. Lee (2 guests)" + NL, output());
    }

    @Test
    void displayTables_mixedStatuses_showsEachAndOpenCount() {
        view.displayTables(new boolean[] { false, true, true });
        assertEquals("--- Table Status ---" + NL
                + "Table 1: occupied" + NL
                + "Table 2: open" + NL
                + "Table 3: open" + NL
                + "2 of 3 tables open." + NL, output());
    }

    @Test
    void displayTables_allOccupied_zeroOpen() {
        view.displayTables(new boolean[] { false, false });
        assertTrue(output().endsWith("0 of 2 tables open." + NL));
    }

    @Test
    void showSeated_printsNameAndTable() {
        view.showSeated("Smith", 3);
        assertEquals("Smith has been seated at table 3." + NL, output());
    }

    @Test
    void showMessage_printsMessageAsIs() {
        view.showMessage("Hello");
        assertEquals("Hello" + NL, output());
    }

    @Test
    void showError_prefixesWithError() {
        view.showError("Something went wrong.");
        assertEquals("Error: Something went wrong." + NL, output());
    }
}
