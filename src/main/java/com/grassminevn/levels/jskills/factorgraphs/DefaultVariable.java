package com.grassminevn.levels.jskills.factorgraphs;

public class DefaultVariable<TValue> extends Variable<TValue> {

    public DefaultVariable() { super(null, "Default"); }

    @Override
    public void setValue(final TValue value) {
        throw new UnsupportedOperationException();
    }
}