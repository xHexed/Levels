package com.grassminevn.levels.jskills;

import com.grassminevn.levels.jskills.numerics.GaussianDistribution;
import com.grassminevn.levels.jskills.numerics.MathUtils;

import java.util.Collection;

/**
 * Container for a player's rating.
 */
public class Rating {

    private static final int defaultConservativeStandardDeviationMultiplier = 3;

    private final double conservativeStandardDeviationMultiplier;

    // The statistical mean value of the rating (also known as μ).
    private final double mean;

    // The standard deviation (the spread) of the rating. This is also known as σ.
    private final double standardDeviation;

    // A conservative estimate of skill based on the mean and standard deviation.
    private final double conservativeRating;

    /**
     * Constructs a rating.
     *
     * @param mean              The statistical mean value of the rating (also known as μ).
     * @param standardDeviation The standard deviation of the rating (also known as σ).
     */
    public Rating(final double mean, final double standardDeviation) {
        this(mean, standardDeviation, defaultConservativeStandardDeviationMultiplier);
    }

    /**
     * Constructs a rating.
     *
     * @param mean                                    The statistical mean value of the rating (also
     *                                                known as μ).
     * @param standardDeviation                       The number of standardDeviation to subtract
     *                                                from the mean to achieve a conservative
     *                                                rating.
     * @param conservativeStandardDeviationMultiplier The number of standardDeviations to subtract
     *                                                from the mean to achieve a conservative
     *                                                rating.
     */
    public Rating(final double mean, final double standardDeviation,
                  final double conservativeStandardDeviationMultiplier) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        this.conservativeStandardDeviationMultiplier = conservativeStandardDeviationMultiplier;
        conservativeRating =
                mean - conservativeStandardDeviationMultiplier * standardDeviation;
    }

    /**
     * @return The variance of the rating (standard deviation squared).
     */
    public double getVariance() {
        return MathUtils.square(standardDeviation);
    }

    public double getConservativeStandardDeviationMultiplier() {
        return conservativeStandardDeviationMultiplier;
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public double getConservativeRating() {
        return conservativeRating;
    }

    public static Rating partialUpdate(final Rating prior, final Rating fullPosterior,
                                       final double updatePercentage) {
        final GaussianDistribution priorGaussian = new GaussianDistribution(prior);
        final GaussianDistribution posteriorGaussian = new GaussianDistribution(fullPosterior);

        // From a clarification email from Ralf Herbrich:
        // "the idea is to compute a linear interpolation between the prior and
        // posterior skills of each player ... in the canonical space of
        // parameters"

        final double
                precisionDifference =
                posteriorGaussian.getPrecision() - priorGaussian.getPrecision();
        final double partialPrecisionDifference = updatePercentage * precisionDifference;

        final double
                precisionMeanDifference =
                posteriorGaussian.getPrecisionMean() - priorGaussian.getPrecisionMean();
        final double partialPrecisionMeanDifference = updatePercentage * precisionMeanDifference;

        final GaussianDistribution partialPosteriorGaussion = GaussianDistribution.fromPrecisionMean(
                priorGaussian.getPrecisionMean() + partialPrecisionMeanDifference,
                priorGaussian.getPrecision() + partialPrecisionDifference);

        return new Rating(partialPosteriorGaussion.getMean(),
                          partialPosteriorGaussion.getStandardDeviation(),
                prior.conservativeStandardDeviationMultiplier);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Rating rating = (Rating) o;

        if (Double.compare(rating.conservativeStandardDeviationMultiplier,
                conservativeStandardDeviationMultiplier) != 0) {
            return false;
        }
        if (Double.compare(rating.mean, mean) != 0) {
            return false;
        }
        if (Double.compare(rating.standardDeviation, standardDeviation) != 0) {
            return false;
        }
        return Double.compare(rating.conservativeRating, conservativeRating) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(conservativeStandardDeviationMultiplier);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mean);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(standardDeviation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(conservativeRating);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        // As a debug helper, display a localized rating:
        return String.format("Mean(μ)=%f, Std-Dev(σ)=%f", mean, standardDeviation);
    }

    public static double calcMeanMean(final Collection<? extends Rating> ratings) {
        double ret = 0;
        for (final Rating rating : ratings) {
            ret += rating.mean;
        }
        return ret / ratings.size();
    }
}