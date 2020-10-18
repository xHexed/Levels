package com.grassminevn.levels.jskills.trueskill;

import com.grassminevn.levels.jskills.numerics.GaussianDistribution;

/**
 * These functions are from the bottom of page 4 of the TrueSkill paper.
 */
public class TruncatedGaussianCorrectionFunctions {

    private TruncatedGaussianCorrectionFunctions() {
    }

    /**
     * The "V" function where the team performance difference is greater than the draw margin. In
     * the reference F# implementation, this is referred to as "the additive correction of a
     * single-sided truncated Gaussian with unit variance."
     *
     * @param teamPerformanceDifference Difference of the teams.
     * @param drawMargin                In the paper, it's referred to as just "ε".
     * @param c                         constant??
     * @return double
     */
    public static double vExceedsMargin(final double teamPerformanceDifference, final double drawMargin,
                                        final double c) {
        return vExceedsMargin(teamPerformanceDifference / c, drawMargin / c);
    }

    public static double vExceedsMargin(final double teamPerformanceDifference, final double drawMargin) {
        final double denominator = GaussianDistribution.cumulativeTo(teamPerformanceDifference - drawMargin);

        if (denominator < 2.222758749e-162) {
            return -teamPerformanceDifference + drawMargin;
        }

        return GaussianDistribution.at(teamPerformanceDifference - drawMargin) / denominator;
    }

    /**
     * The "W" function where the team performance difference is greater than the draw margin. In
     * the reference F# implementation, this is referred to as "the multiplicative correction of a
     * single-sided truncated Gaussian with unit variance."
     *
     * @param teamPerformanceDifference Difference of the teams.
     * @param drawMargin                In the paper, it's referred to as just "ε".
     * @param c                         constant??
     * @return double
     */
    public static double wExceedsMargin(final double teamPerformanceDifference, final double drawMargin,
                                        final double c) {
        return wExceedsMargin(teamPerformanceDifference / c, drawMargin / c);
        //var vWin = vExceedsMargin(teamPerformanceDifference, drawMargin, c);
        //return vWin * (vWin + (teamPerformanceDifference - drawMargin) / c);
    }

    public static double wExceedsMargin(final double teamPerformanceDifference, final double drawMargin) {
        final double denominator = GaussianDistribution.cumulativeTo(teamPerformanceDifference - drawMargin);

        if (denominator < 2.222758749e-162) {
            if (teamPerformanceDifference < 0.0) {
                return 1.0;
            } else {
                return 0.0;
            }
        }

        final double vWin = vExceedsMargin(teamPerformanceDifference, drawMargin);
        return vWin * (vWin + teamPerformanceDifference - drawMargin);
    }

    // the additive correction of a double-sided truncated Gaussian with unit variance
    public static double vWithinMargin(final double teamPerformanceDifference, final double drawMargin,
                                       final double c) {
        return vWithinMargin(teamPerformanceDifference / c, drawMargin / c);
    }

    // from F#:
    public static double vWithinMargin(final double teamPerformanceDifference, final double drawMargin) {
        final double teamPerformanceDifferenceAbsoluteValue = Math.abs(teamPerformanceDifference);
        final double denominator = GaussianDistribution.cumulativeTo(drawMargin - teamPerformanceDifferenceAbsoluteValue) -
                             GaussianDistribution.cumulativeTo(-drawMargin - teamPerformanceDifferenceAbsoluteValue);

        if (denominator < 2.222758749e-162) {
            if (teamPerformanceDifference < 0.0) {
                return -teamPerformanceDifference - drawMargin;
            }

            return -teamPerformanceDifference + drawMargin;
        }

        final double numerator = GaussianDistribution.at(-drawMargin - teamPerformanceDifferenceAbsoluteValue) -
                           GaussianDistribution.at(drawMargin - teamPerformanceDifferenceAbsoluteValue);

        if (teamPerformanceDifference < 0.0) {
            return -numerator / denominator;
        }

        return numerator / denominator;
    }

    // the multiplicative correction of a double-sided truncated Gaussian with unit variance
    public static double wWithinMargin(final double teamPerformanceDifference, final double drawMargin,
                                       final double c) {
        return wWithinMargin(teamPerformanceDifference / c, drawMargin / c);
    }

    // From F#:
    public static double wWithinMargin(final double teamPerformanceDifference, final double drawMargin) {
        final double teamPerformanceDifferenceAbsoluteValue = Math.abs(teamPerformanceDifference);
        final double denominator = GaussianDistribution.cumulativeTo(drawMargin - teamPerformanceDifferenceAbsoluteValue) -
                             GaussianDistribution.cumulativeTo(-drawMargin - teamPerformanceDifferenceAbsoluteValue);

        if (denominator < 2.222758749e-162) {
            return 1.0;
        }

        final double vt = vWithinMargin(teamPerformanceDifferenceAbsoluteValue, drawMargin);

        return vt * vt +
               (
                       (drawMargin - teamPerformanceDifferenceAbsoluteValue)
                       *
                       GaussianDistribution.at(
                               drawMargin - teamPerformanceDifferenceAbsoluteValue)
                       - (-drawMargin - teamPerformanceDifferenceAbsoluteValue)
                         *
                         GaussianDistribution.at(-drawMargin - teamPerformanceDifferenceAbsoluteValue)) / denominator;
    }
}