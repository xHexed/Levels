package com.grassminevn.levels.jskills.elo;

import com.grassminevn.levels.jskills.ITeam;
import com.grassminevn.levels.jskills.PairwiseComparison;
import com.grassminevn.levels.jskills.Rating;
import com.grassminevn.levels.jskills.SkillCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.grassminevn.levels.jskills.GameInfo;
import com.grassminevn.levels.jskills.IPlayer;
import com.grassminevn.levels.jskills.RankSorter;
import com.grassminevn.levels.jskills.numerics.Range;

public abstract class TwoPlayerEloCalculator extends SkillCalculator {

    protected final KFactor kFactor;

    protected TwoPlayerEloCalculator(final KFactor kFactor) {
        super(EnumSet.noneOf(SupportedOptions.class), Range.<ITeam> exactly(2),
                Range.<IPlayer> exactly(1));
        this.kFactor = kFactor;
    }

    @Override
    public Map<IPlayer, Rating> calculateNewRatings(final GameInfo gameInfo,
                                                    final Collection<ITeam> teams, final int... teamRanks) {
        validateTeamCountAndPlayersCountPerTeam(teams);
        final List<ITeam> teamsl = RankSorter.sort(teams, teamRanks);

        final Map<IPlayer, Rating> result = new HashMap<IPlayer, Rating>();
        final boolean isDraw = (teamRanks[0] == teamRanks[1]);

        final List<IPlayer> players = new ArrayList<IPlayer>(2);
        for(final ITeam team : teamsl)
            players.add(team.keySet().toArray(new IPlayer[1])[0]);

        final double player1Rating = teamsl.get(0).get(players.get(0)).getMean();
        final double player2Rating = teamsl.get(1).get(players.get(1)).getMean();

        result.put(players.get(0), calculateNewRating(gameInfo, player1Rating, player2Rating, isDraw ? PairwiseComparison.DRAW : PairwiseComparison.WIN));
        result.put(players.get(1), calculateNewRating(gameInfo, player2Rating, player1Rating, isDraw ? PairwiseComparison.DRAW : PairwiseComparison.LOSE));

        return result;
    }

    protected EloRating calculateNewRating(final GameInfo gameInfo,
                                           final double selfRating, final double opponentRating,
                                           final PairwiseComparison selfToOpponentComparison) {
        final double expectedProbability = getPlayerWinProbability(gameInfo, selfRating, opponentRating);
        final double actualProbability = getScoreFromComparison(selfToOpponentComparison);
        final double k = kFactor.getValueForRating(selfRating);
        final double ratingChange = k * (actualProbability - expectedProbability);
        final double newRating = selfRating + ratingChange;

        return new EloRating(newRating);
    }

    private static double getScoreFromComparison(final PairwiseComparison comparison) {
        switch (comparison) {
        case WIN: return 1;
        case DRAW: return 0.5;
        case LOSE: return 0;
        default: throw new IllegalArgumentException();
        }
    }

    protected abstract double getPlayerWinProbability(GameInfo gameInfo, double playerRating, double opponentRating);

    @Override
    public double calculateMatchQuality(final GameInfo gameInfo,
                                        final Collection<ITeam> teams) {
        validateTeamCountAndPlayersCountPerTeam(teams);
        
        // Extract both players from the teams
        final List<IPlayer> players = new ArrayList<IPlayer>(2);
        for(final ITeam team : teams) players.add(team.keySet().toArray(new IPlayer[0])[0]);

        // Extract each player's rating from their team
        final Iterator<ITeam> teamit = teams.iterator();
        final double player1Rating = teamit.next().get(players.get(0)).getMean();
        final double player2Rating = teamit.next().get(players.get(1)).getMean();

        // The TrueSkill paper mentions that they used s1 - s2 (rating 
        // difference) to determine match quality. I convert that to a 
        // percentage as a delta from 50% using the cumulative density function 
        // of the specific curve being used
        final double deltaFrom50Percent = Math.abs(getPlayerWinProbability(gameInfo, player1Rating, player2Rating) - 0.5);
        return (0.5 - deltaFrom50Percent) / 0.5;
    }
}