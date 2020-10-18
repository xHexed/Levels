package com.grassminevn.levels.jskills;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Helper class for working with a single team.
 */
public class Team extends HashMap<IPlayer, Rating> implements ITeam {

    /** Generated UID for serialization **/
    private static final long serialVersionUID = -5274158841312594600L;

    /**
     * Constructs a new team.
     */
    public Team() { }

    /**
     * Constructs a Team and populates it with the specified player.
     * 
     * @param player The player to add.
     * @param rating The rating of the player.
     */
    public Team(final IPlayer player, final Rating rating) { addPlayer(player, rating); }

    /**
     * Adds the player to the team.
     * 
     * @param player The player to add.
     * @param rating The rating of the player
     * @return The instance of the team (for chaining convenience).
     */
    public Team addPlayer(final IPlayer player, final Rating rating) {
        put(player, rating);
        return this;
    }

    /**
     * Concatenates multiple teams into a list of teams.
     * 
     * @param teams The teams to concatenate together.
     * @return A sequence of teams.
     */
    public static Collection<ITeam> concat(final ITeam... teams) {
        final List<ITeam> teamslist = new ArrayList<ITeam>();
        for (final ITeam team : teams) teamslist.add(team);
        return teamslist;
    }
}