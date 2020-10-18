package com.grassminevn.levels.jskills.trueskill;

import com.grassminevn.levels.jskills.PartialPlay;
import com.grassminevn.levels.jskills.numerics.MathUtils;
import com.grassminevn.levels.jskills.GameInfo;
import com.grassminevn.levels.jskills.Guard;
import com.grassminevn.levels.jskills.IPlayer;
import com.grassminevn.levels.jskills.ITeam;
import com.grassminevn.levels.jskills.RankSorter;
import com.grassminevn.levels.jskills.Rating;
import com.grassminevn.levels.jskills.SkillCalculator;
import com.grassminevn.levels.jskills.numerics.Range;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Calculates TrueSkill using a full factor graph.
 */
public class FactorGraphTrueSkillCalculator extends SkillCalculator {

    public FactorGraphTrueSkillCalculator() {
        super(EnumSet.of(SupportedOptions.PartialPlay, SupportedOptions.PartialUpdate),
              Range.atLeast(2), Range.atLeast(1));
    }

    @Override
    public Map<IPlayer, Rating> calculateNewRatings(final GameInfo gameInfo,
                                                    final Collection<? extends ITeam> teams,
                                                    final int... teamRanks) {
        Guard.argumentNotNull(gameInfo, "gameInfo");
        validateTeamCountAndPlayersCountPerTeam(teams);

        final List<ITeam> teamsl = RankSorter.sort(teams, teamRanks);

        final TrueSkillFactorGraph factorGraph = new TrueSkillFactorGraph(gameInfo, teamsl, teamRanks);
        factorGraph.buildGraph();
        factorGraph.runSchedule();

        // TODO: use this somehow?
        final double probabilityOfOutcome = factorGraph.getProbabilityOfRanking();

        return factorGraph.getUpdatedRatings();
    }

    @Override
    public double calculateMatchQuality(final GameInfo gameInfo, final Collection<? extends ITeam> teams) {
        // We need to create the A matrix which is the player team assignments.
        final List<ITeam> teamAssignmentsList = new ArrayList<>(teams);
        final SimpleMatrix skillsMatrix = GetPlayerCovarianceMatrix(teamAssignmentsList);
        final SimpleMatrix meanVector = GetPlayerMeansVector(teamAssignmentsList);
        final SimpleMatrix meanVectorTranspose = meanVector.transpose();

        final SimpleMatrix playerTeamAssignmentsMatrix =
                CreatePlayerTeamAssignmentMatrix(teamAssignmentsList, meanVector.numRows());
        final SimpleMatrix playerTeamAssignmentsMatrixTranspose = playerTeamAssignmentsMatrix.transpose();

        final double betaSquared = MathUtils.square(gameInfo.getBeta());

        final SimpleMatrix start = meanVectorTranspose.mult(playerTeamAssignmentsMatrix);
        final SimpleMatrix aTa = playerTeamAssignmentsMatrixTranspose
                .mult(playerTeamAssignmentsMatrix).scale(betaSquared);
        final SimpleMatrix aTSA = playerTeamAssignmentsMatrixTranspose.mult(skillsMatrix)
                .mult(playerTeamAssignmentsMatrix);
        final SimpleMatrix middle = aTa.plus(aTSA);

        final SimpleMatrix middleInverse = middle.invert();

        final SimpleMatrix end = playerTeamAssignmentsMatrixTranspose.mult(meanVector);

        final SimpleMatrix expPartMatrix = start.mult(middleInverse).mult(end).scale(-0.5);
        final double expPart = expPartMatrix.determinant();

        final double sqrtPartNumerator = aTa.determinant();
        final double sqrtPartDenominator = middle.determinant();
        final double sqrtPart = sqrtPartNumerator / sqrtPartDenominator;

        return Math.exp(expPart) * Math.sqrt(sqrtPart);
    }

    private static SimpleMatrix GetPlayerMeansVector(final Collection<? extends ITeam> teamAssignmentsList) {
        // A simple list of all the player means.
        final List<Double> temp = GetPlayerMeanRatingValues(teamAssignmentsList);
        final double[] tempa = new double[temp.size()];
        for (int i = 0; i < tempa.length; i++) {
            tempa[i] = temp.get(i);
        }
        return new SimpleMatrix(new double[][]{tempa}).transpose();
    }

    /**
     * This is a square matrix whose diagonal values represent the variance (square of standard
     * deviation) of all players.
     */
    private static SimpleMatrix GetPlayerCovarianceMatrix(final Collection<? extends ITeam> teamAssignmentsList) {
        final List<Double> temp = GetPlayerVarianceRatingValues(teamAssignmentsList);
        final double[] tempa = new double[temp.size()];
        for (int i = 0; i < tempa.length; i++) {
            tempa[i] = temp.get(i);
        }
        return SimpleMatrix.diag(tempa).transpose();
    }

    /**
     * Helper function that gets a list of values for all player ratings
     *
     * TODO: Make array?
     */
    private static List<Double> GetPlayerMeanRatingValues(final Collection<? extends ITeam> teamAssignmentsList) {
        final List<Double> playerRatingValues = new ArrayList<>();
        for (final ITeam currentTeam : teamAssignmentsList) {
            for (final Rating currentRating : currentTeam.values()) {
                playerRatingValues.add(currentRating.getMean());
            }
        }

        return playerRatingValues;
    }

    /**
     * Helper function that gets a list of values for all player ratings
     *
     * TODO Make array?
     */
    private static List<Double> GetPlayerVarianceRatingValues(
            final Collection<? extends ITeam> teamAssignmentsList) {
        final List<Double> playerRatingValues = new ArrayList<>();
        for (final ITeam currentTeam : teamAssignmentsList) {
            for (final Rating currentRating : currentTeam.values()) {
                playerRatingValues.add(currentRating.getVariance());
            }
        }

        return playerRatingValues;
    }

    /**
     * The team assignment matrix is often referred to as the "A" matrix. It's a matrix whose rows
     * represent the players and the columns represent teams. At Matrix[row, column] represents that
     * player[row] is on team[col] Positive values represent an assignment and a negative value
     * means that we subtract the value of the next team since we're dealing with pairs. This means
     * that this matrix always has teams - 1 columns. The only other tricky thing is that values
     * represent the play percentage. <p> For example, consider a 3 team game where team1 is just
     * player1, team 2 is player 2 and player 3, and team3 is just player 4. Furthermore, player 2
     * and player 3 on team 2 played 25% and 75% of the time (e.g. partial play), the A matrix would
     * be:
     * <p><pre>
     * A = this 4x2 matrix:
     * |  1.00  0.00 |
     * | -0.25  0.25 |
     * | -0.75  0.75 |
     * |  0.00 -1.00 |
     * </pre>
     */
    private static SimpleMatrix CreatePlayerTeamAssignmentMatrix(final List<? extends ITeam> teamAssignmentsList,
                                                                 final int totalPlayers) {

        final List<List<Double>> playerAssignments = new ArrayList<>();
        int totalPreviousPlayers = 0;

        for (int i = 0; i < teamAssignmentsList.size() - 1; i++) {
            final ITeam currentTeam = teamAssignmentsList.get(i);

            // Need to add in 0's for all the previous players, since they're not
            // on this team
            final List<Double> currentRowValues = new ArrayList<>();
            for (int j = 0; j < totalPreviousPlayers; j++) {
                currentRowValues.add(0.);
            }
            playerAssignments.add(currentRowValues);

            for (final IPlayer player : currentTeam.keySet()) {
                currentRowValues.add(PartialPlay.getPartialPlayPercentage(player));
                // indicates the player is on the team
                totalPreviousPlayers++;
            }

            final ITeam nextTeam = teamAssignmentsList.get(i + 1);
            for (final IPlayer nextTeamPlayer : nextTeam.keySet()) {
                // Add a -1 * playing time to represent the difference
                currentRowValues.add(-1 * PartialPlay.getPartialPlayPercentage(nextTeamPlayer));
            }
        }

        final SimpleMatrix
                playerTeamAssignmentsMatrix =
                new SimpleMatrix(totalPlayers, teamAssignmentsList.size() - 1);
        for (int i = 0; i < playerAssignments.size(); i++) {
            for (int j = 0; j < playerAssignments.get(i).size(); j++) {
                playerTeamAssignmentsMatrix.set(j, i, playerAssignments.get(i).get(j));
            }
        }

        return playerTeamAssignmentsMatrix;
    }
}