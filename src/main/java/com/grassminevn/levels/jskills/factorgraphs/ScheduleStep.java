package com.grassminevn.levels.jskills.factorgraphs;

public class ScheduleStep<T> extends Schedule<T> {
    private final Factor<T> factor;
    private final int index;

    public ScheduleStep(final String name, final Factor<T> factor, final int index) {
        super(name);
        this.factor = factor;
        this.index = index;
    }

    @Override
    public double visit(final int depth, final int maxDepth) {
        return factor.updateMessage(index);
    }
}