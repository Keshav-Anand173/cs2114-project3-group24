package controller;

import model.Party;
import model.Waitlist;
import view.View;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Coordinates restaurant operations: reads and validates user input, calls
 * the model, and tells the view what to display. Holds no restaurant data
 * of its own.
 */
public class Controller {

    private final View view;
    private final Scanner input;
    private Waitlist waitlist;

    public static void main(String[] args) {
        View view = new View();
        Scanner input = new Scanner(System.in);
        Controller controller = new Controller(view, input);
        controller.start();
    }

    public Controller(View view, Scanner input) {
        this.view = view;
        this.input = input;
    }

    public void start() {
        waitlist = null;
        while (waitlist == null) {
            int candidate = readInt("Enter the number of tables in the restaurant: ");
            try {
                waitlist = new Waitlist(candidate);
            } catch (IllegalArgumentException e) {
                view.showError(e.getMessage());
            }
        }

        boolean running = true;
        while (running) {
            view.displayMenu();
            int choice = readInt("Choose an option (1-5): ");
            switch (choice) {
                case 1:
                    addParty();
                    break;
                case 2:
                    seatNextParty();
                    break;
                case 3:
                    freeTable();
                    break;
                case 4:
                    showStatus();
                    break;
                case 5:
                    running = false;
                    view.showMessage("Goodbye.");
                    break;
                default:
                    view.showError("Please choose a number from 1 to 5.");
            }
        }
    }

    public void addParty() {
        view.showPrompt("Party name: ");
        String name = input.nextLine();
        int size = readInt("Number of guests: ");
        try {
            Party party = new Party(name, size);
            waitlist.enqueue(party);
            view.showMessage(party.getName() + " has been added to the waitlist.");
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    public void seatNextParty() {
        if (waitlist.isEmpty()) {
            view.showMessage("The waitlist is empty. No one to seat.");
            return;
        }
        if (!waitlist.hasOpenTable()) {
            view.showMessage("No tables are open right now.");
            return;
        }
        Party next = waitlist.peek();
        try {
            int tableId = waitlist.seatNextParty();
            view.showSeated(next.getName(), tableId);
        } catch (NoSuchElementException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    public void freeTable() {
        int tableId = readInt("Table ID to free: ");
        try {
            waitlist.freeTable(tableId);
            view.showMessage("Table " + tableId + " is now open.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showError(e.getMessage());
        }
    }

    public void showStatus() {
        view.displayWaitlist(waitlist.toArray());
        view.displayTables(waitlist.getTableStatuses());
    }

    /** Loops until the user enters an integer (bad-input case 2). */
    private int readInt(String prompt) {
        while (true) {
            view.showPrompt(prompt);
            String line = input.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                view.showError("Please enter a whole number.");
            }
        }
    }
}
