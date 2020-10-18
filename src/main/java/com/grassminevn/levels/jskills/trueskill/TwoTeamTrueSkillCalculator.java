package com.grassminevn.levels.jskills.trueskill;

import com.grassminevn.levels.jskills.GameInfo;
import com.grassminevn.levels.jskills.numerics.MathUtils;
import com.grassminevn.levels.jskills.numerics.Range;
import com.grassminevn.levels.jskills.Guard;
import com.grassminevn.levels.jskills.IPlayer;
import com.grassminevn.levels.jskills.ITeam;
import com.grassminevn.levels.jskills.PairwiseComparison;
import com.grassminevn.levels.jskills.RankSorter;
import com.grassminevn.levels.jskills.Rating;
import com.grassminevn.levels.jskills.SkillCalculator;

import java.util.*;
import java.util.Map.Entry;

/**
 * Calculates new ratings for only two teams where each team has 1 or more players. When you only
 * have two teams, the math is still simple: no factor graphs are used yet.
 */
public class TwoTeamTrueSkillCalculator extends SkillCalculator {

    public TwoTeamTrueSkillCalculator() {
        super(EnumSet.noneOf(SupportedOptions.class), Range.exactly(2),
              Range.atLeast(1));
    }

    @Override
    public Map<IPlayer, Rating> calculateNewRatings(final GameInfo gameInfo, final Collection<ITeam> teams,
                                                    final int... teamRanks) {

        Guard.argumentNotNull(gameInfo, "gameInfo");
        validateTeamCountAndPlayersCountPerTeam(teams);

        final List<ITeam> teamsl = RankSorter.sort(teams, teamRanks);

        final ITeam team1 = teamsl.get(0);
        final ITeam team2 = teamsl.get(1);

        final boolean wasDraw = (teamRanks[0] == teamRanks[1]);

        final HashMap<IPlayer, Rating> results = new HashMap<>();

        updatePlayerRatings(gameInfo,
                            results,
                            team1,
                            team2,
                            wasDraw ? PairwiseComparison.DRAW : PairwiseComparison.WIN);

        updatePlayerRatings(gameInfo,
                            results,
                            team2,
                            team1,
                            wasDraw ? PairwiseComparison.DRAW : PairwiseComparison.LOSE);

        return results;
    }

    private static void updatePlayerRatings(final GameInfo gameInfo,
                                            final Map<IPlayer, Rating> newPlayerRatings,
                                            final ITeam selfTeam,
                                            final ITeam otherTeam,
                                            final PairwiseComparison selfToOtherTeamComparison) {
        final double drawMargin =
                DrawMargin.GetDrawMarginFromDrawProbability(gameInfo.getDrawProbability(),
                                                            gameInfo.getBeta());
        final double betaSquared = MathUtils.square(gameInfo.getBeta());
        final double tauSquared = MathUtils.square(gameInfo.getDynamicsFactor());

        final int totalPlayers = selfTeam.size() + otherTeam.size();

        double selfMeanSum = 0;
        for (final Rating r : selfTeam.values()) {
            selfMeanSum += r.getMean();
        }
        double otherTeamMeanSum = 0;
        for (final Rating r : otherTeam.values()) {
            otherTeamMeanSum += r.getMean();
        }

        double sum = 0;
        for (final Rating r : selfTeam.values()) {
            sum += MathUtils.square(r.getStandardDeviation());
        }
        for (final Rating r : otherTeam.values()) {
            sum += MathUtils.square(r.getStandardDeviation());
        }

        final double c = Math.sqrt(sum + totalPlayers * betaSquared);

        double winningMean = selfMeanSum;
        double losingMean = otherTeamMeanSum;

        switch (selfToOtherTeamComparison) {
            case WIN:
            case DRAW: /* NOP */
                break;
            case LOSE:
                winningMean = otherTeamMeanSum;
                losingMean = selfMeanSum;
                break;
        }

        final double meanDelta = winningMean - losingMean;

        final double v;
        final double w;
        final double rankMultiplier;

        if (selfToOtherTeamComparison != PairwiseComparison.DRAW) {
            // non-draw case
            v = TruncatedGaussianCorrectionFunctions.vExceedsMargin(meanDelta, drawMargin, c);
            w = TruncatedGaussianCorrectionFunctions.wExceedsMargin(meanDelta, drawMargin, c);
            rankMultiplier = selfToOtherTeamComparison.multiplier;
        } else {
            // assume draw
            v = TruncatedGaussianCorrectionFunctions.vWithinMargin(meanDelta, drawMargin, c);
            w = TruncatedGaussianCorrectionFunctions.wWithinMargin(meanDelta, drawMargin, c);
            rankMultiplier = 1;
        }

        for (final Entry<IPlayer, Rating> teamPlayerRatingPair : selfTeam.entrySet()) {
            final Rating previousPlayerRating = teamPlayerRatingPair.getValue();

            final double
                    meanMultiplier =
                    (MathUtils.square(previousPlayerRating.getStandardDeviation()) + tauSquared) / c;
            final double
                    stdDevMultiplier =
                    (MathUtils.square(previousPlayerRating.getStandardDeviation()) + tauSquared) / MathUtils.square(c);

            final double playerMeanDelta = (rankMultiplier * meanMultiplier * v);
            final double newMean = previousPlayerRating.getMean() + playerMeanDelta;

            final double newStdDev =
                    Math.sqrt(
                            (MathUtils.square(previousPlayerRating.getStandardDeviation()) + tauSquared) * (1
                                                                                                  -
                                                                                                  w
                                                                                                  * stdDevMultiplier));

            newPlayerRatings.put(teamPlayerRatingPair.getKey(), new Rating(newMean, newStdDev));
        }
    }

    @Override
    public double calculateMatchQuality(final GameInfo gameInfo, final Collection<ITeam> teams) {

        Guard.argumentNotNull(gameInfo, "gameInfo");
        validateTeamCountAndPlayersCountPerTeam(teams);

        final Iterator<ITeam> teamsIt = teams.iterator();

        // We've verified that there's just two teams
        final Collection<Rating> team1 = teamsIt.next().values();
        final int team1Count = team1.size();

        final Collection<Rating> team2 = teamsIt.next().values();
        final int team2Count = team2.size();

        final int totalPlayers = team1Count + team2Count;

        final double betaSquared = MathUtils.square(gameInfo.getBeta());

        double team1MeanSum = 0;
        for (final Rating r : team1) {
            team1MeanSum += r.getMean();
        }
        double team1StdDevSquared = 0;
        for (final Rating r : team1) {
            team1StdDevSquared += MathUtils.square(r.getStandardDeviation());
        }

        double team2MeanSum = 0;
        for (final Rating r : team2) {
            team2MeanSum += r.getMean();
        }
        double team2SigmaSquared = 0;
        for (final Rating r : team2) {
            team2SigmaSquared += MathUtils.square(r.getStandardDeviation());
        }

        // This comes from equation 4.1 in the TrueSkill paper on page 8            
        // The equation was broken up into the part under the square root sign and 
        // the exponential part to make the code easier to read.

        final double sqrtPart
                = Math.sqrt(
                (totalPlayers * betaSquared)
                /
                (totalPlayers * betaSquared + team1StdDevSquared + team2SigmaSquared)
        );

        final double expPart
                = Math.exp(
                (-1 * MathUtils.square(team1MeanSum - team2MeanSum))
                /
                (2 * (totalPlayers * betaSquared + team1StdDevSquared + team2SigmaSquared))
        );

        return expPart * sqrtPart;
    }
}