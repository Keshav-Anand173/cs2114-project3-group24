package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PartyTest {

    @Test
    void constructor_normalCase() {
        Party party = new Party("Smith", 4);
        assertEquals("Smith", party.getName());
        assertEquals(4, party.getNumberOfGuests());
    }

    @Test
    void constructor_badInput_throws() {
        assertThrows(IllegalArgumentException.class, () -> new Party("", 4));
        assertThrows(IllegalArgumentException.class, () -> new Party("Smith", 0));
        assertThrows(IllegalArgumentException.class, () -> new Party("Smith", -2));
        assertThrows(IllegalArgumentException.class, () -> new Party("Smith", 500));
    }

    @Test
    void setName_normalCase() {
        Party party = new Party("Smith", 4);
        party.setName("Lee");
        assertEquals("Lee", party.getName());
    }

    @Test
    void setName_blank_throwsAndNameUnchanged() {
        Party party = new Party("Smith", 4);
        assertThrows(IllegalArgumentException.class, () -> party.setName(" "));
        assertEquals("Smith", party.getName());
    }

    @Test
    void setNumberOfGuests_normalCase() {
        Party party = new Party("Smith", 4);
        party.setNumberOfGuests(6);
        assertEquals(6, party.getNumberOfGuests());
    }

    @Test
    void setNumberOfGuests_zero_throwsAndSizeUnchanged() {
        Party party = new Party("Smith", 4);
        assertThrows(IllegalArgumentException.class, () -> party.setNumberOfGuests(0));
        assertEquals(4, party.getNumberOfGuests());
    }
}
