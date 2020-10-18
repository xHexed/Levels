package com.grassminevn.levels.jskills.elo;

public class KFactor {
    private final double value;

    public KFactor(final double exactKFactor) { value = exactKFactor; }

    public double getValueForRating(final double rating) { return value; }
}