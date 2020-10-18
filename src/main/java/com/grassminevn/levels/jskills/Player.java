package com.grassminevn.levels.jskills;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player who has a {@link Rating}.
 */
public class Player implements IPlayer, SupportPartialPlay, SupportPartialUpdate {

    // = 100% play time
    private static final double DefaultPartialPlayPercentage = 1.0;

    // = receive 100% update
    private static final double DefaultPartialUpdatePercentage = 1.0;

    // The identifier for the player, such as a name.
    private final UUID id;

    /**
     * Indicates the percent of the time the player should be weighted where 0.0
     * indicates the player didn't play and 1.0 indicates the player played 100%
     * of the time.
     */
    private final double partialPlayPercentage;

    /**
     * Indicated how much of a skill update a player should receive where 0.0
     * represents no update and 1.0 represents 100% of the update.
     */
    private final double partialUpdatePercentage;

    /**
     * Constructs a player.
     * 
     * @param id The identifier for the player, such as a name.
     */
    public Player(final UUID id) {
        this(id, DefaultPartialPlayPercentage, DefaultPartialUpdatePercentage);
    }

    /**
     * Constructs a player.
     * 
     * @param id The identifier for the player, such as a name.
     * @param partialPlayPercentage The weight percentage to give this player when calculating a new rank.
     */
    public Player(final UUID id, final double partialPlayPercentage) {
        this(id, partialPlayPercentage, DefaultPartialUpdatePercentage);
    }

    /**
     * Constructs a player.
     * 
     * @param id The identifier for the player, such as a name.
     * @param partialPlayPercentage The weight percentage to give this player when calculating a new rank.
     * @param partialUpdatePercentage Indicates how much of a skill update a player should receive
     *                                where 0 represents no update and 1.0 represents 100% of the update.
     */
    public Player(final UUID id, final double partialPlayPercentage, final double partialUpdatePercentage) {
        // If they don't want to give a player an id, that's ok...
        Guard.argumentInRangeInclusive(partialPlayPercentage, 0, 1.0, "partialPlayPercentage");
        Guard.argumentInRangeInclusive(partialUpdatePercentage, 0, 1.0, "partialUpdatePercentage");
        this.id = id;
        this.partialPlayPercentage = partialPlayPercentage;
        this.partialUpdatePercentage = partialUpdatePercentage;
    }

    public UUID getId() {
        return id;
    }

    public double getPartialPlayPercentage() {
        return partialPlayPercentage;
    }

    public double getPartialUpdatePercentage() {
        return partialUpdatePercentage;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Player player = (Player) o;

        if (Double.compare(player.partialPlayPercentage, partialPlayPercentage) != 0) return false;
        if (Double.compare(player.partialUpdatePercentage, partialUpdatePercentage) != 0) return false;
        return Objects.equals(id, player.id);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        temp = Double.doubleToLongBits(partialPlayPercentage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(partialUpdatePercentage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override public String toString() {
        return id != null ? id.toString() : super.toString();
    }
}