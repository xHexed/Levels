package com.grassminevn.levels.jskills.numerics;

import com.grassminevn.levels.jskills.Rating;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static com.grassminevn.levels.jskills.numerics.MathUtils.square;

/**
 * Immutable representation of the Gaussian distribution of one variable. Not normalized:
 *
 * <pre>
 *            1          -(x)^2 / (2)
 * P(x) = ----------- * e
 *        sqrt(2*pi)
 * </pre>
 *
 * Normalized:
 *
 * <pre>
 *               1           -(x-μ)^2 / (2*σ^2)
 * P(x) = --------------- * e
 *        σ * sqrt(2*pi)
 * </pre>
 *
 * see http://mathworld.wolfram.com/NormalDistribution.html
 */
public class GaussianDistribution {

    /**
     * The Gaussian representation of a flat line.
     **/
    public static final GaussianDistribution UNIFORM = fromPrecisionMean(0, 0);

    /**
     * The peak of the Gaussian, μ
     **/
    private final double mean;

    /**
     * The width of the Gaussian, σ, where the height drops to max/e
     **/
    private final double standardDeviation;

    /**
     * The square of the standard deviation, σ^2
     **/
    private final double variance;

    // Precision and PrecisionMean are used because they make multiplying and
    // dividing simpler (see the accompanying math paper for more details)

    /**
     * 1/σ^2
     **/
    private final double precision;

    /**
     * Precision times mean, μ/σ^2
     **/
    private final double precisionMean;

    /**
     * The normalization constant multiplies the exponential and causes the integral over (-Inf,Inf)
     * to equal 1
     *
     * @return 1/sqrt(2*pi*σ)
     */
    public double getNormalizationConstant() {
        // Great derivation of this is at
        // http://www.astro.psu.edu/~mce/A451_2/A451/downloads/notes0.pdf
        return 1.0 / (sqrt(2 * PI) * standardDeviation);
    }

    /**
     * Private constructor that sets everything at once. Only allow other constructors to use this
     * because if the public were to mess up the relationship between the parameters, who knows what
     * would happen?
     *
     * @param mean              Mean of the distribution.
     * @param standardDeviation Standard deviation of the distribution.
     * @param variance          Variance
     * @param precision         Precision
     * @param precisionMean     Precision mean
     */
    private GaussianDistribution(final double mean, final double standardDeviation,
                                 final double variance, final double precision, final double precisionMean) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        this.variance = variance;
        this.precision = precision;
        this.precisionMean = precisionMean;
    }

    public GaussianDistribution(final double mean, final double standardDeviation) {
        this(mean,
             standardDeviation,
             square(standardDeviation),
             1. / square(standardDeviation),
             mean / square(standardDeviation));
    }

    public GaussianDistribution(final Rating rating) {
        this(rating.getMean(), rating.getStandardDeviation());
    }

    public GaussianDistribution(final GaussianDistribution distribution) {
        this(distribution.mean, distribution.standardDeviation,
             distribution.variance, distribution.precision,
             distribution.precisionMean);
    }

    public static GaussianDistribution fromPrecisionMean(final double precisionMean,
                                                         final double precision) {
        return new GaussianDistribution(precisionMean / precision,
                                        sqrt(1. / precision),
                                        1. / precision,
                                        precision,
                                        precisionMean);
    }

    public GaussianDistribution mult(final GaussianDistribution other) {
        return mult(this, other);
    }

    public static GaussianDistribution mult(final GaussianDistribution left, final GaussianDistribution right) {
        // Although we could use equations from
        // http://www.tina-vision.net/tina-knoppix/tina-memo/2003-003.pdf
        // for multiplication, the precision mean ones are easier to write :)
        return fromPrecisionMean(left.precisionMean + right.precisionMean,
                                 left.precision + right.precision);
    }

    /**
     * Computes the absolute difference between two Gaussian distributions.
     *
     * @param left  Left Gaussian distribution
     * @param right Right Gaussian distribution
     * @return Difference between the two distributions.
     **/
    public static double absoluteDifference(final GaussianDistribution left, final GaussianDistribution right) {
        return max(
                abs(left.precisionMean - right.precisionMean),
                sqrt(abs(left.precision - right.precision)));
    }

    public static double logProductNormalization(final GaussianDistribution left,
                                                 final GaussianDistribution right) {
        if ((left.precision == 0) || (right.precision == 0)) {
            return 0;
        }

        final double varianceSum = left.variance + right.variance;
        final double meanDifference = left.mean - right.mean;

        final double logSqrt2Pi = log(sqrt(2 * PI));
        return -logSqrt2Pi - (log(varianceSum) / 2.0) - (square(meanDifference) /
                                                         (2.0 * varianceSum));
    }


    public static GaussianDistribution divide(final GaussianDistribution numerator,
                                              final GaussianDistribution denominator) {
        return fromPrecisionMean(numerator.precisionMean - denominator.precisionMean,
                                 numerator.precision - denominator.precision);
    }

    public static double logRatioNormalization(final GaussianDistribution numerator,
                                               final GaussianDistribution denominator) {
        if ((numerator.precision == 0) || (denominator.precision == 0)) {
            return 0;
        }

        final double varianceDifference = denominator.variance - numerator.variance;
        final double meanDifference = numerator.mean - denominator.mean;

        final double logSqrt2Pi = log(sqrt(2 * PI));

        return log(denominator.variance) + logSqrt2Pi - log(varianceDifference) / 2.0 +
               square(meanDifference) / (2 * varianceDifference);
    }

    /**
     * <pre>
     *               1          -(x)^2 / (2*stdDev^2)
     *   P(x) = ------------ * e
     *           sqrt(2*pi)
     * </pre>
     * see http://mathworld.wolfram.com/NormalDistribution.html
     *
     * @param x the location to evaluate a normalized Gaussian at
     * @return the value at x of a normalized Gaussian centered at 0 of unit width.
     */
    public static double at(final double x) {
        return at(x, 0, 1);
    }

    /**
     * <pre>
     *               1          -(x)^2 / (2*stdDev^2)
     *   P(x) = ------------ * e
     *           sqrt(2*pi)
     * </pre>
     * see http://mathworld.wolfram.com/NormalDistribution.html
     *
     * @param x                 the location to evaluate a normalized Gaussian at
     * @param mean              Mean of the Gaussian distribution.
     * @param standardDeviation Standard deviation of the Gaussian distribution.
     * @return the value at x of a normalized Gaussian centered at 0 of unit width.
     */
    public static double at(final double x, final double mean, final double standardDeviation) {
        final double multiplier = 1.0 / (standardDeviation * sqrt(2 * PI));
        final double
                expPart =
                exp((-1.0 * pow(x - mean, 2.0)) / (2 * (standardDeviation * standardDeviation)));
        return multiplier * expPart;
    }

    public static double cumulativeTo(final double x, final double mean, final double standardDeviation) {
        final double invsqrt2 = -0.707106781186547524400844362104;
        final double result = errorFunctionCumulativeTo(invsqrt2 * x);
        return 0.5 * result;
    }

    public static double cumulativeTo(final double x) {
        return cumulativeTo(x, 0, 1);
    }

    private static double errorFunctionCumulativeTo(final double x) {
        // Derived from page 265 of Numerical Recipes 3rd Edition            
        final double z = abs(x);

        final double t = 2.0 / (2.0 + z);
        final double ty = 4 * t - 2;

        final double[] coefficients = {-1.3026537197817094, 6.4196979235649026e-1,
                                 1.9476473204185836e-2, -9.561514786808631e-3,
                                 -9.46595344482036e-4, 3.66839497852761e-4, 4.2523324806907e-5,
                                 -2.0278578112534e-5, -1.624290004647e-6, 1.303655835580e-6,
                                 1.5626441722e-8, -8.5238095915e-8, 6.529054439e-9,
                                 5.059343495e-9, -9.91364156e-10, -2.27365122e-10,
                                 9.6467911e-11, 2.394038e-12, -6.886027e-12, 8.94487e-13,
                                 3.13092e-13, -1.12708e-13, 3.81e-16, 7.106e-15, -1.523e-15,
                                 -9.4e-17, 1.21e-16, -2.8e-17};

        final int ncof = coefficients.length;
        double d = 0.0;
        double dd = 0.0;

        for (int j = ncof - 1; j > 0; j--) {
            final double tmp = d;
            d = ty * d - dd + coefficients[j];
            dd = tmp;
        }

        final double ans = t * exp(-z * z + 0.5 * (coefficients[0] + ty * d) - dd);
        return x >= 0.0 ? ans : (2.0 - ans);
    }

    private static double InverseErrorFunctionCumulativeTo(final double p) {
        // From page 265 of numerical recipes                       

        if (p >= 2.0) {
            return -100;
        }
        if (p <= 0.0) {
            return 100;
        }

        final double pp = (p < 1.0) ? p : 2 - p;
        final double t = sqrt(-2 * log(pp / 2.0)); // Initial guess
        double x = -0.70711 * ((2.30753 + t * 0.27061) / (1.0 + t * (0.99229 + t * 0.04481)) - t);

        for (int j = 0; j < 2; j++) {
            final double err = errorFunctionCumulativeTo(x) - pp;
            x += err / (1.12837916709551257 * exp(-(x * x)) - x * err); // Halley
        }

        return p < 1.0 ? x : -x;
    }

    public static double inverseCumulativeTo(final double x, final double mean, final double standardDeviation) {
        // From numerical recipes, page 320
        return mean - sqrt(2) * standardDeviation * InverseErrorFunctionCumulativeTo(2 * x);
    }

    public static double inverseCumulativeTo(final double x) {
        return inverseCumulativeTo(x, 0, 1);
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public double getVariance() {
        return variance;
    }

    public double getPrecision() {
        return precision;
    }

    public double getPrecisionMean() {
        return precisionMean;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GaussianDistribution that = (GaussianDistribution) o;

        if (Double.compare(that.mean, mean) != 0) {
            return false;
        }
        if (Double.compare(that.standardDeviation, standardDeviation) != 0) {
            return false;
        }
        if (Double.compare(that.variance, variance) != 0) {
            return false;
        }
        if (Double.compare(that.precision, precision) != 0) {
            return false;
        }
        return Double.compare(that.precisionMean, precisionMean) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(mean);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(standardDeviation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(variance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(precision);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(precisionMean);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() { // Debug help
        return String.format("Mean(μ)=%f, Std-Dev(σ)=%f", mean, standardDeviation);
    }
}