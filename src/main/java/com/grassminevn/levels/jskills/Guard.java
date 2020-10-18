package com.grassminevn.levels.jskills;

/**
 * Verifies argument contracts.
 * <p>
 * These are used until I figure out how to do this better in Java
 */
public class Guard {

    /**
     * No instances allowed
     */
    private Guard() { }

    public static void argumentNotNull(final Object value, final String parameterName) {
        if (value == null) {
            throw new NullPointerException(parameterName);
        }
    }

    public static void argumentIsValidIndex(final int index, final int count, final String parameterName) {
        if ((index < 0) || (index >= count)) {
            throw new IndexOutOfBoundsException(parameterName);
        }
    }

    public static void argumentInRangeInclusive(final double value, final double min, final double max, final String parameterName) {
        if ((value < min) || (value > max)) {
            throw new IndexOutOfBoundsException(parameterName);
        }
    }
}