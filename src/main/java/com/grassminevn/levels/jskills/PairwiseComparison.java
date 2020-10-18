package com.grassminevn.levels.jskills;

import java.util.TreeMap;

/**
 * Represents a comparison between two players.
 * <p>
 * The actual values for the enum were chosen so that the also correspond to the
 * multiplier for updates to means.
 */
public enum PairwiseComparison{
    WIN(1),
    DRAW(0),
    LOSE(-1);
    
    public final int multiplier;
    
    PairwiseComparison(final int multiplier) { this.multiplier = multiplier; }

    private static final TreeMap<Integer, PairwiseComparison> revmap = new TreeMap<>();
    static { for (final PairwiseComparison pc : values())
            revmap.put(pc.multiplier, pc);
    }
    
    public static PairwiseComparison fromMultiplier(final int multiplier) {
        return revmap.get(multiplier);
    }
}