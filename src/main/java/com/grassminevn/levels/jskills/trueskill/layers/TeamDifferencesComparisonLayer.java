package com.grassminevn.levels.jskills.trueskill.layers;

import com.grassminevn.levels.jskills.GameInfo;
import com.grassminevn.levels.jskills.factorgraphs.DefaultVariable;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;
import com.grassminevn.levels.jskills.trueskill.DrawMargin;
import com.grassminevn.levels.jskills.trueskill.TrueSkillFactorGraph;
import com.grassminevn.levels.jskills.trueskill.factors.GaussianGreaterThanFactor;
import com.grassminevn.levels.jskills.trueskill.factors.GaussianWithinFactor;
import com.grassminevn.levels.jskills.trueskill.factors.GaussianFactor;

public class TeamDifferencesComparisonLayer extends
    TrueSkillFactorGraphLayer<Variable<GaussianDistribution>, GaussianFactor, DefaultVariable<GaussianDistribution>> {

    private final double epsilon;
    private final int[] teamRanks;

    public TeamDifferencesComparisonLayer(final TrueSkillFactorGraph parentGraph, final int[] teamRanks) {
        super(parentGraph);
        this.teamRanks = teamRanks;
        final GameInfo gameInfo = parentFactorGraph.getGameInfo();
        epsilon = DrawMargin
                .GetDrawMarginFromDrawProbability(gameInfo.getDrawProbability(), gameInfo.getBeta());
    }

    @Override
    public void buildLayer() {
        for (int i = 0; i < getInputVariablesGroups().size(); i++) {
            final boolean isDraw = (teamRanks[i] == teamRanks[i + 1]);
            final Variable<GaussianDistribution> teamDifference = getInputVariablesGroups().get(i).get(0);

            final GaussianFactor factor =
                isDraw
                    ? new GaussianWithinFactor(epsilon, teamDifference)
                    : new GaussianGreaterThanFactor(epsilon, teamDifference);

            AddLayerFactor(factor);
        }
    }
}