package com.grassminevn.levels.jskills.factorgraphs;

import static java.lang.Math.max;

import java.util.Collection;

public class ScheduleSequence<TValue>
        extends Schedule<TValue> {
    private final Collection<Schedule<TValue>> schedules;

    public ScheduleSequence(final String name, final Collection<Schedule<TValue>> schedules) {
        super(name);
        this.schedules = schedules;
    }

    @Override
    public double visit(final int depth, final int maxDepth) {
        double maxDelta = 0;

        for (final Schedule<TValue> schedule : schedules)
            maxDelta = max(schedule.visit(depth + 1, maxDepth), maxDelta);

        return maxDelta;
    }
}
