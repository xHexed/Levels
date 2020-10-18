package com.grassminevn.levels.jskills.trueskill.factors;

import com.grassminevn.levels.jskills.factorgraphs.Message;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;
import com.grassminevn.levels.jskills.trueskill.TruncatedGaussianCorrectionFunctions;

/**
 * Factor representing a team difference that has exceeded the draw margin. See the accompanying
 * math paper for more details.
 */
public class GaussianGreaterThanFactor extends GaussianFactor {

    private final double epsilon;

    public GaussianGreaterThanFactor(final double epsilon, final Variable<GaussianDistribution> variable) {
        super(String.format("%s > %4.3f", variable, epsilon));
        this.epsilon = epsilon;
        createVariableToMessageBinding(variable);
    }

    @Override
    public double getLogNormalization() {
        final GaussianDistribution marginal = getVariables().get(0).getValue();
        final GaussianDistribution message = getMessages().get(0).getValue();
        final GaussianDistribution messageFromVariable = GaussianDistribution.divide(marginal, message);
        return -GaussianDistribution.logProductNormalization(messageFromVariable, message)
               +
               Math.log(
                       GaussianDistribution.cumulativeTo((messageFromVariable.getMean() - epsilon) /
                                    messageFromVariable.getStandardDeviation()));
    }

    @Override
    protected double updateMessage(final Message<GaussianDistribution> message,
                                   final Variable<GaussianDistribution> variable) {
        final GaussianDistribution oldMarginal = new GaussianDistribution(variable.getValue());
        final GaussianDistribution oldMessage = new GaussianDistribution(message.getValue());
        final GaussianDistribution messageFromVar = GaussianDistribution.divide(oldMarginal, oldMessage);

        final double c = messageFromVar.getPrecision();
        double d = messageFromVar.getPrecisionMean();

        final double sqrtC = Math.sqrt(c);

        final double dOnSqrtC = d / sqrtC;

        final double epsilsonTimesSqrtC = epsilon * sqrtC;
        d = messageFromVar.getPrecisionMean();

        final double denom = 1.0 - TruncatedGaussianCorrectionFunctions.wExceedsMargin(dOnSqrtC, epsilsonTimesSqrtC);

        final double newPrecision = c / denom;
        final double newPrecisionMean = (d +
                                   sqrtC *
                                   TruncatedGaussianCorrectionFunctions.vExceedsMargin(dOnSqrtC, epsilsonTimesSqrtC)) /
                                  denom;

        final GaussianDistribution newMarginal = GaussianDistribution.fromPrecisionMean(newPrecisionMean, newPrecision);

        final GaussianDistribution newMessage = GaussianDistribution.divide(GaussianDistribution.mult(oldMessage, newMarginal), oldMarginal);

        // Update the message and marginal
        message.setValue(newMessage);
        variable.setValue(newMarginal);

        // Return the difference in the new marginal
        return GaussianDistribution.absoluteDifference(newMarginal, oldMarginal);
    }
}