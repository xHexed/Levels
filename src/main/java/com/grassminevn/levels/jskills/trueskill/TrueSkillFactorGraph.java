package com.grassminevn.levels.jskills.trueskill;

import com.grassminevn.levels.jskills.GameInfo;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;
import com.grassminevn.levels.jskills.factorgraphs.Factor;
import com.grassminevn.levels.jskills.factorgraphs.FactorGraph;
import com.grassminevn.levels.jskills.factorgraphs.FactorGraphLayerBase;
import com.grassminevn.levels.jskills.factorgraphs.FactorList;
import com.grassminevn.levels.jskills.factorgraphs.KeyedVariable;
import com.grassminevn.levels.jskills.factorgraphs.Schedule;
import com.grassminevn.levels.jskills.factorgraphs.ScheduleSequence;
import com.grassminevn.levels.jskills.trueskill.layers.IteratedTeamDifferencesInnerLayer;
import com.grassminevn.levels.jskills.trueskill.layers.PlayerPerformancesToTeamPerformancesLayer;
import com.grassminevn.levels.jskills.IPlayer;
import com.grassminevn.levels.jskills.ITeam;
import com.grassminevn.levels.jskills.Rating;
import com.grassminevn.levels.jskills.trueskill.layers.PlayerPriorValuesToSkillsLayer;
import com.grassminevn.levels.jskills.trueskill.layers.PlayerSkillsToPerformancesLayer;
import com.grassminevn.levels.jskills.trueskill.layers.TeamDifferencesComparisonLayer;
import com.grassminevn.levels.jskills.trueskill.layers.TeamPerformancesToTeamPerformanceDifferencesLayer;

import java.util.*;

public class TrueSkillFactorGraph extends FactorGraph<TrueSkillFactorGraph> {

    private final List<FactorGraphLayerBase<GaussianDistribution>> layers;
    private final PlayerPriorValuesToSkillsLayer priorLayer;
    private GameInfo gameInfo;

    public TrueSkillFactorGraph(final GameInfo gameInfo, final Collection<ITeam> teams, final int[] teamRanks) {
        this.priorLayer = new PlayerPriorValuesToSkillsLayer(this, teams);
        setGameInfo(gameInfo);

        this.layers = new ArrayList<>();
        this.layers.add(priorLayer);
        this.layers.add(new PlayerSkillsToPerformancesLayer(this));
        this.layers.add(new PlayerPerformancesToTeamPerformancesLayer(this));
        this.layers.add(new IteratedTeamDifferencesInnerLayer(
                this,
                new TeamPerformancesToTeamPerformanceDifferencesLayer(this),
                new TeamDifferencesComparisonLayer(this, teamRanks)));
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    private void setGameInfo(final GameInfo info) {
        gameInfo = info;
    }

    public void buildGraph() {
        Object lastOutput = null;

        for (final FactorGraphLayerBase<GaussianDistribution> currentLayer : layers) {
            if (lastOutput != null) {
                currentLayer.setRawInputVariablesGroups(lastOutput);
            }

            currentLayer.buildLayer();
            lastOutput = currentLayer.getRawOutputVariablesGroups();
        }
    }

    public void runSchedule() {
        final Schedule<GaussianDistribution> fullSchedule = createFullSchedule();
        // TODO: Maybe something can be done w/ this?
        final double fullScheduleDelta = fullSchedule.visit();
    }

    public double getProbabilityOfRanking() {
        final FactorList<GaussianDistribution> factorList = new FactorList<>();

        for (final FactorGraphLayerBase<GaussianDistribution> currentLayer : layers) {
            for (final Factor<GaussianDistribution> currentFactor : currentLayer.getUntypedFactors()) {
                factorList.addFactor(currentFactor);
            }
        }

        final double logZ = factorList.getLogNormalization();
        return Math.exp(logZ);
    }

    private Schedule<GaussianDistribution> createFullSchedule() {
        final List<Schedule<GaussianDistribution>> fullSchedule = new ArrayList<>();

        for (final FactorGraphLayerBase<GaussianDistribution> currentLayer : layers) {
            final Schedule<GaussianDistribution>
                    currentPriorSchedule =
                    currentLayer.createPriorSchedule();
            if (currentPriorSchedule != null) {
                fullSchedule.add(currentPriorSchedule);
            }
        }

        // Getting as a list to use reverse()
        final List<FactorGraphLayerBase<GaussianDistribution>> allLayers = new ArrayList<>(layers);
        Collections.reverse(allLayers);

        for (final FactorGraphLayerBase<GaussianDistribution> currentLayer : allLayers) {
            final Schedule<GaussianDistribution>
                    currentPosteriorSchedule =
                    currentLayer.createPosteriorSchedule();
            if (currentPosteriorSchedule != null) {
                fullSchedule.add(currentPosteriorSchedule);
            }
        }

        return new ScheduleSequence<>("Full schedule", fullSchedule);
    }

    public Map<IPlayer, Rating> getUpdatedRatings() {
        final Map<IPlayer, Rating> result = new HashMap<>();
        for (final List<KeyedVariable<IPlayer, GaussianDistribution>> currentTeam : priorLayer
                .getOutputVariablesGroups()) {
            for (final KeyedVariable<IPlayer, GaussianDistribution> currentPlayer : currentTeam) {
                final Rating rating = new Rating(currentPlayer.getValue().getMean(),
                                                 currentPlayer.getValue().getStandardDeviation());
                result.put(currentPlayer.getKey(), rating);
            }
        }

        return result;
    }
}