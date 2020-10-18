package com.grassminevn.levels.jskills.elo;

import com.grassminevn.levels.jskills.GameInfo;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;

public class GaussianEloCalculator extends TwoPlayerEloCalculator {
    // From the paper
    private static final KFactor StableKFactor = new KFactor(24);

    public GaussianEloCalculator() { super(StableKFactor); }
    
    @Override
    protected double getPlayerWinProbability(final GameInfo gameInfo,
                                             final double playerRating, final double opponentRating) {
        final double ratingDifference = playerRating - opponentRating;

        // See equation 1.1 in the TrueSkill paper
        return GaussianDistribution.cumulativeTo(
                        ratingDifference / (Math.sqrt(2) * gameInfo.getBeta()));
    }
}