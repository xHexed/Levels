package com.grassminevn.levels.jskills.trueskill.factors;

import com.grassminevn.levels.jskills.Guard;
import com.grassminevn.levels.jskills.factorgraphs.Message;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Factor that sums together multiple Gaussians. See the accompanying math paper for more
 * details.
 */
public class GaussianWeightedSumFactor extends GaussianFactor {

    private final List<int[]> variableIndexOrdersForWeights = new ArrayList<>();

    // This following is used for convenience, for example, the first entry is [0, 1, 2] 
    // corresponding to v[0] = a1*v[1] + a2*v[2]
    private final double[][] weights;
    private final double[][] weightsSquared;

    public GaussianWeightedSumFactor(final Variable<GaussianDistribution> sumVariable,
                                     final List<? extends Variable<GaussianDistribution>> variablesToSum) {
        // By default, set the weight to 1.0, which is what null indicates
        this(sumVariable, variablesToSum, null);
    }

    public GaussianWeightedSumFactor(final Variable<GaussianDistribution> sumVariable,
                                     final List<? extends Variable<GaussianDistribution>> variablesToSum,
                                     double[] variableWeights) {
        super(createName(sumVariable, variablesToSum, variableWeights));
        // Have to add in this workaround because Arrays.fill returns void
        if (variableWeights == null) {
            variableWeights = new double[variablesToSum.size()];
            Arrays.fill(variableWeights, 1.);
        }
        weights = new double[variableWeights.length + 1][];
        weightsSquared = new double[weights.length][];

        // The first weights are a straightforward copy
        // v_0 = a_1*v_1 + a_2*v_2 + ... + a_n * v_n
        weights[0] = new double[variableWeights.length];
        System.arraycopy(variableWeights, 0, weights[0], 0, variableWeights.length);
        weightsSquared[0] = new double[variableWeights.length];
        for (int i = 0; i < weights[0].length; i++) {
            weightsSquared[0][i] = weights[0][i] * weights[0][i];
        }

        // 0..n-1
        final int[] temp = new int[1 + variablesToSum.size()];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = i;
        }
        variableIndexOrdersForWeights.add(temp);

        // The rest move the variables around and divide out the constant.
        // For example:
        // v_1 = (-a_2 / a_1) * v_2 + (-a3/a1) * v_3 + ... + (1.0 / a_1) * v_0
        // By convention, we'll put the v_0 term at the end

        for (int weightsIndex = 1; weightsIndex < weights.length; weightsIndex++) {
            final double[] currentWeights = new double[variableWeights.length];
            weights[weightsIndex] = currentWeights;

            final int[] variableIndices = new int[variableWeights.length + 1];
            variableIndices[0] = weightsIndex;

            final double[] currentWeightsSquared = new double[variableWeights.length];
            weightsSquared[weightsIndex] = currentWeightsSquared;

            // keep a single variable to keep track of where we are in the array.
            // This is helpful since we skip over one of the spots
            int currentDestinationWeightIndex = 0;

            for (int currentWeightSourceIndex = 0;
                 currentWeightSourceIndex < variableWeights.length;
                 currentWeightSourceIndex++) {

                if (currentWeightSourceIndex == (weightsIndex - 1)) {
                    continue;
                }

                double
                        currentWeight =
                        (-variableWeights[currentWeightSourceIndex] / variableWeights[weightsIndex
                                                                                      - 1]);

                if (variableWeights[weightsIndex - 1] == 0)
                // HACK: Getting around division by zero
                {
                    currentWeight = 0;
                }

                currentWeights[currentDestinationWeightIndex] = currentWeight;
                currentWeightsSquared[currentDestinationWeightIndex] =
                        currentWeight * currentWeight;

                variableIndices[currentDestinationWeightIndex + 1] = currentWeightSourceIndex + 1;
                currentDestinationWeightIndex++;
            }

            // And the final one
            double finalWeight = 1.0 / variableWeights[weightsIndex - 1];

            if (variableWeights[weightsIndex - 1] == 0)
            // HACK: Getting around division by zero
            {
                finalWeight = 0;
            }

            currentWeights[currentDestinationWeightIndex] = finalWeight;
            currentWeightsSquared[currentDestinationWeightIndex] = finalWeight * finalWeight;
            variableIndices[variableIndices.length - 1] = 0;
            variableIndexOrdersForWeights.add(variableIndices);
        }

        createVariableToMessageBinding(sumVariable);

        for (final Variable<GaussianDistribution> currentVariable : variablesToSum) {
            createVariableToMessageBinding(currentVariable);
        }
    }

    @Override
    public double getLogNormalization() {
        final List<Variable<GaussianDistribution>> vars = getVariables();
        final List<Message<GaussianDistribution>> messages = getMessages();

        double result = 0.0;

        // We start at 1 since offset 0 has the sum
        for (int i = 1; i < vars.size(); i++) {
            result +=
                    GaussianDistribution.logRatioNormalization(vars.get(i).getValue(),
                                                               messages.get(i).getValue());
        }

        return result;
    }

    private double updateHelper(final double[] weights, final double[] weightsSquared,
                                final List<? extends Message<GaussianDistribution>> messages,
                                final List<? extends Variable<GaussianDistribution>> variables) {
        // Potentially look at http://mathworld.wolfram.com/NormalSumDistribution.html for clues as 
        // to what it's doing

        final GaussianDistribution message0 = new GaussianDistribution(messages.get(0).getValue());
        final GaussianDistribution marginal0 = new GaussianDistribution(variables.get(0).getValue());

        // The math works out so that 1/newPrecision = sum of a_i^2 /marginalsWithoutMessages[i]
        double inverseOfNewPrecisionSum = 0.0;
        double anotherInverseOfNewPrecisionSum = 0.0;
        double weightedMeanSum = 0.0;
        double anotherWeightedMeanSum = 0.0;

        for (int i = 0; i < weightsSquared.length; i++) {
            // These flow directly from the paper

            inverseOfNewPrecisionSum += weightsSquared[i] /
                                        (variables.get(i + 1).getValue().getPrecision() - messages
                                                .get(i + 1).getValue().getPrecision());

            final GaussianDistribution
                    diff =
                    GaussianDistribution.divide(variables.get(i + 1).getValue(), messages.get(i + 1).getValue());
            anotherInverseOfNewPrecisionSum += weightsSquared[i] / diff.getPrecision();

            weightedMeanSum += weights[i]
                               *
                               (variables.get(i + 1).getValue().getPrecisionMean() - messages
                                       .get(i + 1).getValue().getPrecisionMean())
                               /
                               (variables.get(i + 1).getValue().getPrecision() - messages.get(i + 1)
                                       .getValue().getPrecision());

            anotherWeightedMeanSum += weights[i] * diff.getPrecisionMean() / diff.getPrecision();
        }

        final double newPrecision = 1.0 / inverseOfNewPrecisionSum;
        final double anotherNewPrecision = 1.0 / anotherInverseOfNewPrecisionSum;

        double newPrecisionMean = newPrecision * weightedMeanSum;
        double anotherNewPrecisionMean = anotherNewPrecision * anotherWeightedMeanSum;

        // AS value of newPrecision == 0.0 and weightedMeanSum is Infinity so
        // 0.0 * Infinity  = NaN which is causing the issues further down
        // 0.0 * Infinity  = 0.0 would not cause any issues in calculations.
        if (Double.isNaN(newPrecisionMean)) {
            newPrecisionMean = 0.0;
        }
        if (Double.isNaN(anotherNewPrecisionMean)) {
            anotherNewPrecisionMean = 0.0;
        }

        final GaussianDistribution oldMarginalWithoutMessage = GaussianDistribution.divide(marginal0, message0);

        final GaussianDistribution
                newMessage =
                GaussianDistribution.fromPrecisionMean(newPrecisionMean, newPrecision);
        final GaussianDistribution
                anotherNewMessage =
                GaussianDistribution
                        .fromPrecisionMean(anotherNewPrecisionMean, anotherNewPrecision);
        if (!newMessage.equals(anotherNewMessage)) {
            throw new RuntimeException("newMessage and anotherNewMessage aren't the same");
        }

        final GaussianDistribution newMarginal = GaussianDistribution.mult(oldMarginalWithoutMessage, newMessage);

        // Update the message and marginal

        messages.get(0).setValue(newMessage);
        variables.get(0).setValue(newMarginal);

        // Return the difference in the new marginal
        return GaussianDistribution.absoluteDifference(newMarginal, marginal0);
    }


    @Override
    public double updateMessage(final int messageIndex) {
        final List<Message<GaussianDistribution>> allMessages = getMessages();
        final List<Variable<GaussianDistribution>> allVariables = getVariables();

        Guard.argumentIsValidIndex(messageIndex, allMessages.size(), "messageIndex");

        final List<Message<GaussianDistribution>> updatedMessages = new ArrayList<>();
        final List<Variable<GaussianDistribution>> updatedVariables = new ArrayList<>();

        final int[] indicesToUse = variableIndexOrdersForWeights.get(messageIndex);

        // The tricky part here is that we have to put the messages and variables in the same
        // order as the weights. Thankfully, the weights and messages share the same index numbers,
        // so we just need to make sure they're consistent
        for (int i = 0; i < allMessages.size(); i++) {
            updatedMessages.add(allMessages.get(indicesToUse[i]));
            updatedVariables.add(allVariables.get(indicesToUse[i]));
        }

        return updateHelper(weights[messageIndex], weightsSquared[messageIndex], updatedMessages,
                            updatedVariables);
    }

    private static String createName(final Variable<GaussianDistribution> sumVariable,
                                     final List<? extends Variable<GaussianDistribution>> variablesToSum,
                                     final double[] weights) {
        final StringBuilder sb = new StringBuilder();
        sb.append(sumVariable);
        sb.append(" = ");
        for (int i = 0; i < variablesToSum.size(); i++) {
            final boolean isFirst = (i == 0);

            if (isFirst && (weights[i] < 0)) {
                sb.append("-");
            }

            sb.append(String.format("%.2f", Math.abs(weights[i])));
            sb.append("*[");
            sb.append(variablesToSum.get(i));
            sb.append("]");

            final boolean isLast = (i == variablesToSum.size() - 1);

            if (!isLast) {
                if (weights[i + 1] >= 0) {
                    sb.append(" + ");
                } else {
                    sb.append(" - ");
                }
            }
        }

        return sb.toString();
    }
}
