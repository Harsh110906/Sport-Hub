package com.example.sporthub;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.sporthub.models.Event;
import com.example.sporthub.models.FitnessRecord;
import com.example.sporthub.models.Match;
import com.example.sporthub.models.Team;
import com.example.sporthub.models.Tournament;
import com.example.sporthub.models.User;
import com.example.sporthub.models.WorkoutPlan;
import com.example.sporthub.utils.SportHubValidator;

public class NavigationAndRoleRegressionTest {

    @Test
    public void testModelsInitializationAndGettersSetters() {
        // User model
        User user = new User("uid1", "Test User", "test@example.com", "Player", "Soccer");
        assertEquals("uid1", user.getFirebaseUid());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Player", user.getRole());
        assertEquals("Soccer", user.getFavoriteSport());

        // Tournament model
        Tournament t = new Tournament();
        t.setId(1);
        t.setName("Cup");
        t.setSport("Football");
        t.setStatus("Upcoming");
        assertEquals(1, t.getId());
        assertEquals("Cup", t.getName());
        assertEquals("Football", t.getSport());
        assertEquals("Upcoming", t.getStatus());

        // Team model
        Team team = new Team();
        team.setId(2);
        team.setName("Tigers");
        team.setSport("Football");
        assertEquals(2, team.getId());
        assertEquals("Tigers", team.getName());

        // Match model
        Match match = new Match();
        match.setId(3);
        match.setTeamAId(1);
        match.setTeamBId(2);
        match.setScoreA(2);
        match.setScoreB(1);
        assertEquals(3, match.getId());
        assertEquals(Integer.valueOf(2), match.getScoreA());
        assertEquals(Integer.valueOf(1), match.getScoreB());

        // Event model
        Event event = new Event();
        event.setId(4);
        event.setName("Marathon");
        assertEquals(4, event.getId());
        assertEquals("Marathon", event.getName());

        // FitnessRecord model
        FitnessRecord fr = new FitnessRecord();
        fr.setId(5);
        fr.setActivityType("Running");
        fr.setDurationMinutes(30);
        assertEquals(5, fr.getId());
        assertEquals(30, fr.getDurationMinutes());

        // WorkoutPlan model
        WorkoutPlan wp = new WorkoutPlan();
        wp.setId(6);
        wp.setTitle("Leg Day");
        wp.setStatus("Planned");
        assertEquals(6, wp.getId());
        assertEquals("Leg Day", wp.getTitle());
        assertEquals("Planned", wp.getStatus());
    }

    @Test
    public void testRoleAppropriateFeatureRestrictionsAndNavigationRules() {
        // Verify Player navigation rules: Player cannot register tournaments if completed or already registered
        assertTrue("Upcoming and not registered allows registration", SportHubValidator.canRegisterForTournament("Upcoming", false));
        assertFalse("Completed tournament prohibits registration", SportHubValidator.canRegisterForTournament("Completed", false));
        assertFalse("Already registered prohibits duplicate registration", SportHubValidator.canRegisterForTournament("Upcoming", true));

        // Verify match team validation rules (prevent playing against oneself)
        assertFalse("Same team ID is invalid for match", SportHubValidator.areTeamsValidForMatch(5, 5));
        assertFalse("Unselected team ID (-1) is invalid", SportHubValidator.areTeamsValidForMatch(-1, 2));
        assertTrue("Different valid team IDs are valid for match", SportHubValidator.areTeamsValidForMatch(1, 2));
    }
}
