package view;

import model.Party;

/**
 * All user-facing output. The view only knows what gets displayed — it
 * never stores the waitlist or tables itself and never modifies anything.
 */
public class View {

    public void displayMenu() {
        System.out.println();
        System.out.println("=== Restaurant Waitlist ===");
        System.out.println("1. Add a party to the waitlist");
        System.out.println("2. Seat the next party");
        System.out.println("3. Free a table");
        System.out.println("4. Show waitlist and table status");
        System.out.println("5. Quit");
    }

    public void showPrompt(String prompt) {
        System.out.print(prompt);
    }

    public void displayWaitlist(Party[] parties) {
        System.out.println("--- Current Waitlist ---");
        if (parties.length == 0) {
            System.out.println("(empty)");
            return;
        }
        for (int i = 0; i < parties.length; i++) {
            Party p = parties[i];
            System.out.println((i + 1) + ". " + p.getName() + " (" + p.getNumberOfGuests() + " guests)");
        }
    }

    public void displayTables(boolean[] tableStatuses) {
        System.out.println("--- Table Status ---");
        int openCount = 0;
        for (int i = 0; i < tableStatuses.length; i++) {
            System.out.println("Table " + (i + 1) + ": " + (tableStatuses[i] ? "open" : "occupied"));
            if (tableStatuses[i]) {
                openCount++;
            }
        }
        System.out.println(openCount + " of " + tableStatuses.length + " tables open.");
    }

    public void showSeated(String name, int tableId) {
        System.out.println(name + " has been seated at table " + tableId + ".");
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String message) {
        System.out.println("Error: " + message);
    }
}
