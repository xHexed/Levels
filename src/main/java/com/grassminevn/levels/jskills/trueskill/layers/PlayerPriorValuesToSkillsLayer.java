package com.grassminevn.levels.jskills.trueskill.layers;

import com.grassminevn.levels.jskills.ITeam;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;
import com.grassminevn.levels.jskills.numerics.MathUtils;
import com.grassminevn.levels.jskills.trueskill.TrueSkillFactorGraph;
import com.grassminevn.levels.jskills.factorgraphs.DefaultVariable;
import com.grassminevn.levels.jskills.factorgraphs.KeyedVariable;
import com.grassminevn.levels.jskills.factorgraphs.Schedule;
import com.grassminevn.levels.jskills.factorgraphs.ScheduleStep;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.IPlayer;
import com.grassminevn.levels.jskills.Rating;
import com.grassminevn.levels.jskills.trueskill.factors.GaussianPriorFactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

// We intentionally have no Posterior schedule since the only purpose here is to 
public class PlayerPriorValuesToSkillsLayer extends
    TrueSkillFactorGraphLayer<DefaultVariable<GaussianDistribution>,
                              GaussianPriorFactor,
            KeyedVariable<IPlayer, GaussianDistribution>> {

    private final Collection<ITeam> teams;

    public PlayerPriorValuesToSkillsLayer(final TrueSkillFactorGraph parentGraph, final Collection<ITeam> teams) {
        super(parentGraph);
        this.teams = teams;
    }

    @Override
    public void buildLayer() {
        for(final ITeam currentTeam : teams) {
            final List<KeyedVariable<IPlayer, GaussianDistribution>> currentTeamSkills = new ArrayList<>();

            for(final Entry<IPlayer, Rating> currentTeamPlayer : currentTeam.entrySet()) {
                final KeyedVariable<IPlayer, GaussianDistribution> playerSkill =
                    createSkillOutputVariable(currentTeamPlayer.getKey());
                AddLayerFactor(createPriorFactor(currentTeamPlayer.getKey(), currentTeamPlayer.getValue(), playerSkill));
                currentTeamSkills.add(playerSkill);
            }

            addOutputVariableGroup(currentTeamSkills);
        }
    }

    @Override
    public Schedule<GaussianDistribution> createPriorSchedule() {
        final Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<>();
        for (final GaussianPriorFactor prior : getLocalFactors()) {
            schedules.add(new ScheduleStep<>("Prior to Skill Step", prior, 0));
        }
        return ScheduleSequence(schedules, "All priors");
    }

    private GaussianPriorFactor createPriorFactor(final IPlayer player, final Rating priorRating,
                                                  final Variable<GaussianDistribution> skillsVariable) {
        return new GaussianPriorFactor(priorRating.getMean(),
                                       MathUtils.square(priorRating.getStandardDeviation()) +
                                       MathUtils.square(getParentFactorGraph().getGameInfo().getDynamicsFactor()), skillsVariable);
    }

    private KeyedVariable<IPlayer, GaussianDistribution> createSkillOutputVariable(final IPlayer key) {
        return new KeyedVariable<>(key, GaussianDistribution.UNIFORM, "%s's skill", key.toString());
    }
}