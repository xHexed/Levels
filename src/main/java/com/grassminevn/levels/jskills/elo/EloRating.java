package com.grassminevn.levels.jskills.elo;

import com.grassminevn.levels.jskills.Rating;

/**
 * An Elo rating represented by a single number (mean).
 */
public class EloRating extends Rating {
    public EloRating(final double rating) { super(rating, 0); }
}