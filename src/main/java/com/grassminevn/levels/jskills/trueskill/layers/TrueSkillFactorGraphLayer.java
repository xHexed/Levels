package com.grassminevn.levels.jskills.trueskill.layers;

import com.grassminevn.levels.jskills.factorgraphs.Factor;
import com.grassminevn.levels.jskills.factorgraphs.FactorGraphLayer;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;
import com.grassminevn.levels.jskills.trueskill.TrueSkillFactorGraph;

public abstract class TrueSkillFactorGraphLayer<TInputVariable extends Variable<GaussianDistribution>,
                                                TFactor extends Factor<GaussianDistribution>,
                                                TOutputVariable extends Variable<GaussianDistribution>>
    extends FactorGraphLayer
        <TrueSkillFactorGraph, GaussianDistribution, Variable<GaussianDistribution>, TInputVariable,
                TFactor, TOutputVariable>
{
    public TrueSkillFactorGraphLayer(final TrueSkillFactorGraph parentGraph)
    {
        super(parentGraph);
    }
}