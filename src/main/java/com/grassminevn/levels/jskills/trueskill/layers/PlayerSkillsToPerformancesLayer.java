package com.grassminevn.levels.jskills.trueskill.layers;

import com.grassminevn.levels.jskills.IPlayer;
import com.grassminevn.levels.jskills.factorgraphs.KeyedVariable;
import com.grassminevn.levels.jskills.factorgraphs.Schedule;
import com.grassminevn.levels.jskills.factorgraphs.ScheduleStep;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;
import com.grassminevn.levels.jskills.numerics.MathUtils;
import com.grassminevn.levels.jskills.trueskill.TrueSkillFactorGraph;
import com.grassminevn.levels.jskills.trueskill.factors.GaussianLikelihoodFactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerSkillsToPerformancesLayer extends
    TrueSkillFactorGraphLayer<KeyedVariable<IPlayer, GaussianDistribution>,
            GaussianLikelihoodFactor,
                              KeyedVariable<IPlayer, GaussianDistribution>> {

    public PlayerSkillsToPerformancesLayer(final TrueSkillFactorGraph parentGraph)
    {
        super(parentGraph);
    }

    @Override
    public void buildLayer() {
        for(final List<KeyedVariable<IPlayer, GaussianDistribution>> currentTeam : getInputVariablesGroups()) {
            final List<KeyedVariable<IPlayer, GaussianDistribution>> currentTeamPlayerPerformances = new ArrayList<>();

            for(final KeyedVariable<IPlayer, GaussianDistribution> playerSkillVariable : currentTeam) {
                final KeyedVariable<IPlayer, GaussianDistribution> playerPerformance =
                    createOutputVariable(playerSkillVariable.getKey());
                AddLayerFactor(createLikelihood(playerSkillVariable, playerPerformance));
                currentTeamPlayerPerformances.add(playerPerformance);
            }

            addOutputVariableGroup(currentTeamPlayerPerformances);
        }
    }

    private GaussianLikelihoodFactor createLikelihood(final KeyedVariable<IPlayer, GaussianDistribution> playerSkill,
                                                      final KeyedVariable<IPlayer, GaussianDistribution> playerPerformance) {
        return new GaussianLikelihoodFactor(MathUtils.square(parentFactorGraph.getGameInfo().getBeta()), playerPerformance, playerSkill);
    }

    private KeyedVariable<IPlayer, GaussianDistribution> createOutputVariable(final IPlayer key) {
        return new KeyedVariable<>(key, GaussianDistribution.UNIFORM, "%s's performance", key);
    }

    @Override
    public Schedule<GaussianDistribution> createPriorSchedule() {
        final Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<>();
        for (final GaussianLikelihoodFactor likelihood : getLocalFactors()) {
            schedules.add(new ScheduleStep<>("Skill to Perf step", likelihood, 0));
        }
        return ScheduleSequence(schedules, "All skill to performance sending");
    }

    @Override
    public Schedule<GaussianDistribution> createPosteriorSchedule() {
        final Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<>();
        for (final GaussianLikelihoodFactor likelihood : getLocalFactors()) {
            schedules.add(new ScheduleStep<>("Skill to Perf step", likelihood, 1));
        }
        return ScheduleSequence(schedules, "All skill to performance sending");
    }
}