package com.grassminevn.levels.jskills.trueskill;

import com.grassminevn.levels.jskills.numerics.MathUtils;
import com.grassminevn.levels.jskills.numerics.Range;
import com.grassminevn.levels.jskills.GameInfo;
import com.grassminevn.levels.jskills.Guard;

import com.grassminevn.levels.jskills.IPlayer;
import com.grassminevn.levels.jskills.ITeam;
import com.grassminevn.levels.jskills.PairwiseComparison;
import com.grassminevn.levels.jskills.RankSorter;
import com.grassminevn.levels.jskills.Rating;
import com.grassminevn.levels.jskills.SkillCalculator;

import java.util.*;

/**
 * Calculates the new ratings for only two players. When you only have two players, a lot of the
 * math simplifies. The main purpose of this class is to show the bare minimum of what a TrueSkill
 * implementation should have.
 */
public class TwoPlayerTrueSkillCalculator extends SkillCalculator {

    public TwoPlayerTrueSkillCalculator() {
        super(EnumSet.noneOf(SupportedOptions.class), Range.<ITeam>exactly(2),
              Range.<IPlayer>exactly(1));
    }

    @Override
    public Map<IPlayer, Rating> calculateNewRatings(final GameInfo gameInfo, final Collection<ITeam> teams,
                                                    final int... teamRanks) {
        // Basic argument checking
        Guard.argumentNotNull(gameInfo, "gameInfo");
        validateTeamCountAndPlayersCountPerTeam(teams);

        // Make sure things are in order
        final List<ITeam> teamsl = RankSorter.sort(teams, teamRanks);

        // Since we verified that each team has one player, we know the player is the first one
        final ITeam winningTeam = teamsl.get(0);
        final IPlayer winner = winningTeam.keySet().iterator().next();
        final Rating winnerPreviousRating = winningTeam.get(winner);

        final Map<IPlayer, Rating> losingTeam = teamsl.get(1);
        final IPlayer loser = losingTeam.keySet().iterator().next();
        final Rating loserPreviousRating = losingTeam.get(loser);

        final boolean wasDraw = (teamRanks[0] == teamRanks[1]);

        final Map<IPlayer, Rating> results = new HashMap<IPlayer, Rating>();
        results.put(winner, calculateNewRating(gameInfo, winnerPreviousRating, loserPreviousRating,
                                               wasDraw ? PairwiseComparison.DRAW
                                                       : PairwiseComparison.WIN));
        results.put(loser, calculateNewRating(gameInfo, loserPreviousRating, winnerPreviousRating,
                                              wasDraw ? PairwiseComparison.DRAW
                                                      : PairwiseComparison.LOSE));

        // And we're done!
        return results;
    }

    private static Rating calculateNewRating(final GameInfo gameInfo, final Rating selfRating,
                                             final Rating opponentRating, final PairwiseComparison comparison) {

        final double
                drawMargin =
                DrawMargin.GetDrawMarginFromDrawProbability(gameInfo.getDrawProbability(),
                                                            gameInfo.getBeta());

        final double c =
                Math.sqrt(
                        MathUtils.square(selfRating.getStandardDeviation())
                        +
                        MathUtils.square(opponentRating.getStandardDeviation())
                        +
                        2 * MathUtils.square(gameInfo.getBeta()));

        double winningMean = selfRating.getMean();
        double losingMean = opponentRating.getMean();

        switch (comparison) {
            case WIN:
            case DRAW: /* NOP */
                break;
            case LOSE:
                winningMean = opponentRating.getMean();
                losingMean = selfRating.getMean();
                break;
        }

        final double meanDelta = winningMean - losingMean;

        final double v;
        final double w;
        final double rankMultiplier;

        if (comparison != PairwiseComparison.DRAW) {
            // non-draw case
            v = TruncatedGaussianCorrectionFunctions.vExceedsMargin(meanDelta, drawMargin, c);
            w = TruncatedGaussianCorrectionFunctions.wExceedsMargin(meanDelta, drawMargin, c);
            rankMultiplier = comparison.multiplier;
        } else {
            v = TruncatedGaussianCorrectionFunctions.vWithinMargin(meanDelta, drawMargin, c);
            w = TruncatedGaussianCorrectionFunctions.wWithinMargin(meanDelta, drawMargin, c);
            rankMultiplier = 1;
        }

        final double
                meanMultiplier =
                (MathUtils.square(selfRating.getStandardDeviation()) + MathUtils.square(gameInfo.getDynamicsFactor()))
                / c;

        final double
                varianceWithDynamics =
                MathUtils.square(selfRating.getStandardDeviation()) + MathUtils.square(gameInfo.getDynamicsFactor());
        final double stdDevMultiplier = varianceWithDynamics / MathUtils.square(c);

        final double newMean = selfRating.getMean() + (rankMultiplier * meanMultiplier * v);
        final double newStdDev = Math.sqrt(varianceWithDynamics * (1 - w * stdDevMultiplier));

        return new Rating(newMean, newStdDev);
    }

    @Override
    public double calculateMatchQuality(final GameInfo gameInfo, final Collection<ITeam> teams) {
        Guard.argumentNotNull(gameInfo, "gameInfo");
        validateTeamCountAndPlayersCountPerTeam(teams);

        final Iterator<ITeam> teamIt = teams.iterator();

        final Rating player1Rating = teamIt.next().values().iterator().next();
        final Rating player2Rating = teamIt.next().values().iterator().next();

        // We just use equation 4.1 found on page 8 of the TrueSkill 2006 paper:
        final double betaSquared = MathUtils.square(gameInfo.getBeta());
        final double player1SigmaSquared = MathUtils.square(player1Rating.getStandardDeviation());
        final double player2SigmaSquared = MathUtils.square(player2Rating.getStandardDeviation());

        // This is the square root part of the equation:
        final double sqrtPart =
                Math.sqrt(
                        (2 * betaSquared)
                        /
                        (2 * betaSquared + player1SigmaSquared + player2SigmaSquared));

        // This is the exponent part of the equation:
        final double expPart =
                Math.exp(
                        (-1 * MathUtils.square(player1Rating.getMean() - player2Rating.getMean()))
                        /
                        (2 * (2 * betaSquared + player1SigmaSquared + player2SigmaSquared)));

        return sqrtPart * expPart;
    }
}