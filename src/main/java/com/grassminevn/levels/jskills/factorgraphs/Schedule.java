package com.grassminevn.levels.jskills.factorgraphs;

public abstract class Schedule<T> {
    private final String name;

    protected Schedule(final String name) {
        this.name = name;
    }

    public abstract double visit(int depth, int maxDepth);

    public double visit() { return visit(-1, 0); }

    @Override public String toString() {
        return name;
    }
}
