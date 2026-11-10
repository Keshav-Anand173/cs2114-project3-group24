package model;

/**
 * A single party waiting to be seated: a name and a number of guests.
 * The model never prints anything — invalid input results in an
 * IllegalArgumentException that the controller is responsible for catching.
 */
public class Party {

    public static final int MAX_PARTY_SIZE = 20;

    private String name;
    private int numberOfGuests;

    public Party(String name, int numberOfGuests) {
        validateName(name);
        validateSize(numberOfGuests);
        this.name = name;
        this.numberOfGuests = numberOfGuests;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        validateSize(numberOfGuests);
        this.numberOfGuests = numberOfGuests;
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Party name cannot be blank.");
        }
    }

    private static void validateSize(int numberOfGuests) {
        if (numberOfGuests < 1 || numberOfGuests > MAX_PARTY_SIZE) {
            throw new IllegalArgumentException(
                    "Party size must be between 1 and " + MAX_PARTY_SIZE + ".");
        }
    }

    @Override
    public String toString() {
        return name + " (" + numberOfGuests + ")";
    }
}
