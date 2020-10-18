package com.grassminevn.levels.jskills.trueskill.factors;

import com.grassminevn.levels.jskills.factorgraphs.Message;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;

/**
 * Connects two variables and adds uncertainty. See the accompanying math paper for more details.
 */
public class GaussianLikelihoodFactor extends GaussianFactor {

    private final double precision;

    public GaussianLikelihoodFactor(final double betaSquared, final Variable<GaussianDistribution> variable1,
                                    final Variable<GaussianDistribution> variable2) {
        super(String.format("Likelihood of %s going to %s", variable2, variable1));
        precision = 1.0 / betaSquared;
        createVariableToMessageBinding(variable1);
        createVariableToMessageBinding(variable2);
    }

    @Override
    public double getLogNormalization() {
        return GaussianDistribution.logRatioNormalization(variables.get(0).getValue(), messages.get(0).getValue());
    }

    private double UpdateHelper(final Message<GaussianDistribution> message1,
                                final Message<? extends GaussianDistribution> message2,
                                final Variable<GaussianDistribution> variable1,
                                final Variable<? extends GaussianDistribution> variable2) {
        final GaussianDistribution message1Value = new GaussianDistribution(message1.getValue());
        final GaussianDistribution message2Value = new GaussianDistribution(message2.getValue());

        final GaussianDistribution marginal1 = new GaussianDistribution(variable1.getValue());
        final GaussianDistribution marginal2 = new GaussianDistribution(variable2.getValue());

        final double
                a =
                precision / (precision + marginal2.getPrecision() - message2Value.getPrecision());

        final GaussianDistribution newMessage = GaussianDistribution.fromPrecisionMean(
                a * (marginal2.getPrecisionMean() - message2Value.getPrecisionMean()),
                a * (marginal2.getPrecision() - message2Value.getPrecision()));

        final GaussianDistribution oldMarginalWithoutMessage = GaussianDistribution.divide(marginal1, message1Value);

        final GaussianDistribution newMarginal = GaussianDistribution.mult(oldMarginalWithoutMessage, newMessage);

        // Update the message and marginal

        message1.setValue(newMessage);
        variable1.setValue(newMarginal);

        // Return the difference in the new marginal
        return GaussianDistribution.absoluteDifference(newMarginal, marginal1);
    }

    @Override
    public double updateMessage(final int messageIndex) {

        switch (messageIndex) {
            case 0:
                return UpdateHelper(getMessages().get(0), getMessages().get(1),
                                    getVariables().get(0), getVariables().get(1));
            case 1:
                return UpdateHelper(getMessages().get(1), getMessages().get(0),
                                    getVariables().get(1), getVariables().get(0));
            default:
                throw new IllegalArgumentException();
        }
    }
}