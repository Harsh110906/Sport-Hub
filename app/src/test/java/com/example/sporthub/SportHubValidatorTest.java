package com.example.sporthub;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.sporthub.utils.SportHubValidator;

public class SportHubValidatorTest {

    @Test
    public void testIsNotEmpty() {
        assertFalse(SportHubValidator.isNotEmpty(null));
        assertFalse(SportHubValidator.isNotEmpty(""));
        assertFalse(SportHubValidator.isNotEmpty("   "));
        assertTrue(SportHubValidator.isNotEmpty("Sport"));
        assertTrue(SportHubValidator.isNotEmpty("  Hub  "));
    }

    @Test
    public void testIsValidEmail() {
        assertFalse(SportHubValidator.isValidEmail(null));
        assertFalse(SportHubValidator.isValidEmail(""));
        assertFalse(SportHubValidator.isValidEmail("invalid-email"));
        assertFalse(SportHubValidator.isValidEmail("test.com"));
        assertFalse(SportHubValidator.isValidEmail("user@.com"));
        assertTrue(SportHubValidator.isValidEmail("user@example.com"));
        assertTrue(SportHubValidator.isValidEmail("  player.one@sport-hub.org  "));
    }

    @Test
    public void testIsValidDate() {
        assertFalse(SportHubValidator.isValidDate(null));
        assertFalse(SportHubValidator.isValidDate(""));
        assertFalse(SportHubValidator.isValidDate("2023/10/12")); // wrong separator
        assertFalse(SportHubValidator.isValidDate("12-10-2023")); // wrong order
        assertFalse(SportHubValidator.isValidDate("2023-02-30")); // impossible calendar date
        assertFalse(SportHubValidator.isValidDate("2023-13-01")); // invalid month
        assertFalse(SportHubValidator.isValidDate("2023-00-15")); // invalid month 00
        assertTrue(SportHubValidator.isValidDate("2023-10-12")); // valid date
        assertTrue(SportHubValidator.isValidDate("2024-02-29")); // leap year valid date
        assertFalse(SportHubValidator.isValidDate("2023-02-29")); // non-leap year invalid date
    }

    @Test
    public void testIsStartDateBeforeOrEqualEnd() {
        assertFalse(SportHubValidator.isStartDateBeforeOrEqualEnd("2023-10-15", "2023-10-10")); // end before start
        assertTrue(SportHubValidator.isStartDateBeforeOrEqualEnd("2023-10-10", "2023-10-15")); // start before end
        assertTrue(SportHubValidator.isStartDateBeforeOrEqualEnd("2023-10-10", "2023-10-10")); // equal dates
        assertFalse(SportHubValidator.isStartDateBeforeOrEqualEnd("invalid", "2023-10-15"));
    }

    @Test
    public void testIsValidDuration() {
        assertFalse(SportHubValidator.isValidDuration(0));
        assertFalse(SportHubValidator.isValidDuration(-10));
        assertTrue(SportHubValidator.isValidDuration(45));

        assertFalse(SportHubValidator.isValidDuration((String) null));
        assertFalse(SportHubValidator.isValidDuration(""));
        assertFalse(SportHubValidator.isValidDuration("abc"));
        assertFalse(SportHubValidator.isValidDuration("0"));
        assertFalse(SportHubValidator.isValidDuration("-5"));
        assertTrue(SportHubValidator.isValidDuration("60"));
    }

    @Test
    public void testIsValidScore() {
        assertTrue(SportHubValidator.isValidScore((Integer) null));
        assertTrue(SportHubValidator.isValidScore(0));
        assertTrue(SportHubValidator.isValidScore(5));
        assertFalse(SportHubValidator.isValidScore(-1));

        assertTrue(SportHubValidator.isValidScore((String) null));
        assertTrue(SportHubValidator.isValidScore(""));
        assertTrue(SportHubValidator.isValidScore("  "));
        assertTrue(SportHubValidator.isValidScore("0"));
        assertTrue(SportHubValidator.isValidScore("12"));
        assertFalse(SportHubValidator.isValidScore("-3"));
        assertFalse(SportHubValidator.isValidScore("xyz"));
    }

    @Test
    public void testAreTeamsValidForMatch() {
        assertFalse(SportHubValidator.areTeamsValidForMatch(-1, 2));
        assertFalse(SportHubValidator.areTeamsValidForMatch(1, -1));
        assertFalse(SportHubValidator.areTeamsValidForMatch(5, 5)); // same team
        assertTrue(SportHubValidator.areTeamsValidForMatch(1, 2)); // valid different teams
    }

    @Test
    public void testTournamentRegistrationLogic() {
        // Upcoming status & not registered -> can register
        assertTrue(SportHubValidator.canRegisterForTournament("Upcoming", false));
        // Ongoing status -> cannot register
        assertFalse(SportHubValidator.canRegisterForTournament("Ongoing", false));
        // Already registered -> cannot register again
        assertFalse(SportHubValidator.canRegisterForTournament("Upcoming", true));

        // Upcoming status & registered -> can cancel
        assertTrue(SportHubValidator.canCancelRegistration("Upcoming", true));
        // Not registered -> cannot cancel
        assertFalse(SportHubValidator.canCancelRegistration("Upcoming", false));
        // Completed status -> cannot cancel
        assertFalse(SportHubValidator.canCancelRegistration("Completed", true));
    }
}
