package com.grassminevn.levels.jskills.elo;

import com.grassminevn.levels.jskills.GameInfo;

/**
 * Including ELO's scheme as a simple comparison. See
 * http://en.wikipedia.org/wiki/Elo_rating_system#Theory for more details
 */
public class FideEloCalculator extends TwoPlayerEloCalculator {

    public FideEloCalculator() { this(new FideKFactor()); }

    public FideEloCalculator(final FideKFactor kFactor) { super(kFactor); }

    @Override
    protected double getPlayerWinProbability(final GameInfo gameInfo, final double playerRating, final double opponentRating) {
        final double ratingDifference = opponentRating - playerRating;

        return 1.0 /
            (1.0 + Math.pow(10.0, ratingDifference / (2 * gameInfo.getBeta())));
    }        
}