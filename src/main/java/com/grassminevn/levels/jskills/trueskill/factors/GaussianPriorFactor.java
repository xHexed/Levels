package com.grassminevn.levels.jskills.trueskill.factors;

import com.grassminevn.levels.jskills.factorgraphs.Message;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;

/**
 * Supplies the factor graph with prior information. See the accompanying math paper for more
 * details.
 */
public class GaussianPriorFactor extends GaussianFactor {

    private final GaussianDistribution newMessage;

    public GaussianPriorFactor(final double mean, final double variance,
                               final Variable<GaussianDistribution> variable) {
        super(String.format("Prior value going to %s", variable));
        newMessage = new GaussianDistribution(mean, Math.sqrt(variance));
        createVariableToMessageBinding(variable,
                                       new Message<>(GaussianDistribution.fromPrecisionMean(0, 0),
                                                     "message from %s to %s", this, variable));
    }

    @Override
    protected double updateMessage(final Message<GaussianDistribution> message,
                                   final Variable<GaussianDistribution> variable) {
        final GaussianDistribution oldMarginal = new GaussianDistribution(variable.getValue());
        final GaussianDistribution newMarginal =
                GaussianDistribution.fromPrecisionMean(
                        oldMarginal.getPrecisionMean() + newMessage.getPrecisionMean() - message
                                .getValue().getPrecisionMean(),
                        oldMarginal.getPrecision() + newMessage.getPrecision() - message
                                .getValue().getPrecision());
        variable.setValue(newMarginal);
        message.setValue(newMessage);
        return GaussianDistribution.absoluteDifference(oldMarginal, newMarginal);
    }

    @Override
    public double getLogNormalization() {
        return 0;
    }
}