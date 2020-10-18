package com.grassminevn.levels.jskills.trueskill.factors;

import com.grassminevn.levels.jskills.factorgraphs.Factor;
import com.grassminevn.levels.jskills.factorgraphs.Message;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;

public abstract class GaussianFactor extends Factor<GaussianDistribution> {

    GaussianFactor(final String name) { super(name); }

    /** Sends the factor-graph message with and returns the log-normalization constant **/
    @Override
    protected double sendMessage(final Message<? extends GaussianDistribution> message, final Variable<GaussianDistribution> variable) {
        final GaussianDistribution marginal = variable.getValue();
        final GaussianDistribution messageValue = message.getValue();
        final double logZ = GaussianDistribution.logProductNormalization(marginal, messageValue);
        variable.setValue(marginal.mult(messageValue));
        return logZ;
    }

    @Override
    public Message<GaussianDistribution> createVariableToMessageBinding(final Variable<GaussianDistribution> variable) {
        return createVariableToMessageBinding(variable, new Message<>(GaussianDistribution.fromPrecisionMean(0, 0),
                                              "message from %s to %s", this, variable));
    }
}