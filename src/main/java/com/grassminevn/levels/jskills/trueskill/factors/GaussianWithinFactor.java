package com.grassminevn.levels.jskills.trueskill.factors;

import com.grassminevn.levels.jskills.factorgraphs.Message;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;
import com.grassminevn.levels.jskills.trueskill.TruncatedGaussianCorrectionFunctions;

import static com.grassminevn.levels.jskills.trueskill.TruncatedGaussianCorrectionFunctions.vWithinMargin;
import static com.grassminevn.levels.jskills.trueskill.TruncatedGaussianCorrectionFunctions.wWithinMargin;

/**
 * Factor representing a team difference that has not exceeded the draw margin. See the
 * accompanying math paper for more details.
 */
public class GaussianWithinFactor extends GaussianFactor {

    private final double epsilon;

    public GaussianWithinFactor(final double epsilon, final Variable<GaussianDistribution> variable) {
        super(String.format("%s <= %4.3f", variable, epsilon));
        this.epsilon = epsilon;
        createVariableToMessageBinding(variable);
    }

    @Override
    public double getLogNormalization() {
        final GaussianDistribution marginal = variables.get(0).getValue();
        final GaussianDistribution message = messages.get(0).getValue();
        final GaussianDistribution messageFromVariable = GaussianDistribution.divide(marginal, message);
        final double mean = messageFromVariable.getMean();
        final double std = messageFromVariable.getStandardDeviation();
        final double z = GaussianDistribution.cumulativeTo((epsilon - mean) / std)
                   -
                   GaussianDistribution.cumulativeTo((-epsilon - mean) / std);

        return -GaussianDistribution.logProductNormalization(messageFromVariable, message) + Math.log(z);
    }

    @Override
    protected double updateMessage(final Message<GaussianDistribution> message,
                                   final Variable<GaussianDistribution> variable) {
        final GaussianDistribution oldMarginal = new GaussianDistribution(variable.getValue());
        final GaussianDistribution oldMessage = new GaussianDistribution(message.getValue());
        final GaussianDistribution messageFromVariable = GaussianDistribution.divide(oldMarginal, oldMessage);

        final double c = messageFromVariable.getPrecision();
        double d = messageFromVariable.getPrecisionMean();

        final double sqrtC = Math.sqrt(c);
        final double dOnSqrtC = d / sqrtC;

        final double epsilonTimesSqrtC = epsilon * sqrtC;
        d = messageFromVariable.getPrecisionMean();

        final double denominator = 1.0 - TruncatedGaussianCorrectionFunctions.wWithinMargin(dOnSqrtC, epsilonTimesSqrtC);
        final double newPrecision = c / denominator;
        final double newPrecisionMean = (d +
                                   sqrtC *
                                   TruncatedGaussianCorrectionFunctions.vWithinMargin(dOnSqrtC, epsilonTimesSqrtC)) /
                                  denominator;

        final GaussianDistribution newMarginal = GaussianDistribution.fromPrecisionMean(newPrecisionMean, newPrecision);
        final GaussianDistribution newMessage = GaussianDistribution.divide(GaussianDistribution.mult(oldMessage, newMarginal), oldMarginal);

        // Update the message and marginal
        message.setValue(newMessage);
        variable.setValue(newMarginal);

        // Return the difference in the new marginal
        return GaussianDistribution.absoluteDifference(newMarginal, oldMarginal);
    }
}