package com.grassminevn.levels.jskills.trueskill.layers;

import com.grassminevn.levels.jskills.PartialPlay;
import com.grassminevn.levels.jskills.factorgraphs.KeyedVariable;
import com.grassminevn.levels.jskills.factorgraphs.Schedule;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;
import com.grassminevn.levels.jskills.trueskill.TrueSkillFactorGraph;
import com.grassminevn.levels.jskills.IPlayer;
import com.grassminevn.levels.jskills.factorgraphs.ScheduleStep;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.trueskill.factors.GaussianWeightedSumFactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerPerformancesToTeamPerformancesLayer extends
    TrueSkillFactorGraphLayer<KeyedVariable<IPlayer, GaussianDistribution>,
                              GaussianWeightedSumFactor,
                              Variable<GaussianDistribution>> {
    public PlayerPerformancesToTeamPerformancesLayer(final TrueSkillFactorGraph parentGraph)
    {
        super(parentGraph);
    }

    @Override
    public void buildLayer() {
        for(final List<KeyedVariable<IPlayer, GaussianDistribution>> currentTeam : getInputVariablesGroups()) {
            final Variable<GaussianDistribution> teamPerformance = CreateOutputVariable(currentTeam);
            AddLayerFactor(createPlayerToTeamSumFactor(currentTeam, teamPerformance));

            // REVIEW: Does it make sense to have groups of one?
            addOutputVariable(teamPerformance);
        }
    }

    @Override
    public Schedule<GaussianDistribution> createPriorSchedule() {
        final Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<>();
        for (final GaussianWeightedSumFactor weightedSumFactor : getLocalFactors()) {
            schedules.add(new ScheduleStep<>("Perf to Team Perf Step", weightedSumFactor, 0));
        }
        return ScheduleSequence(schedules, "all player perf to team perf schedule");
    }

    protected GaussianWeightedSumFactor createPlayerToTeamSumFactor(final List<? extends KeyedVariable<IPlayer, GaussianDistribution>> teamMembers,
                                                                    final Variable<GaussianDistribution> sumVariable) {
        final double[] weights = new double[teamMembers.size()];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = PartialPlay.getPartialPlayPercentage(teamMembers.get(i).getKey());
        }
        return new GaussianWeightedSumFactor(sumVariable, teamMembers, weights);
    }

    @Override
    public Schedule<GaussianDistribution> createPosteriorSchedule() {

        final List<Schedule<GaussianDistribution>> schedules = new ArrayList<>();
        for (final GaussianWeightedSumFactor currentFactor : getLocalFactors()) {
            // TODO is there an off by 1 error here?
            for (int i = 0; i < currentFactor.getNumberOfMessages(); i++) {
                schedules.add(new ScheduleStep<>(
                        "team sum perf @" + i,
                        currentFactor,
                        i));
            }
        }
        return ScheduleSequence(schedules, "all of the team's sum iterations");
    }

    private Variable<GaussianDistribution> CreateOutputVariable(final List<? extends KeyedVariable<IPlayer, GaussianDistribution>> team) {
        final StringBuilder sb = new StringBuilder();
        for (final KeyedVariable<IPlayer, GaussianDistribution> teamMember : team) {
            sb.append(teamMember.getKey());
            sb.append(", ");
        }
        sb.delete(sb.length()-2, sb.length());

        return new Variable<>(GaussianDistribution.UNIFORM, "Team[%s]'s performance", sb.toString());
    }
}