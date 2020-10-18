package com.grassminevn.levels.jskills.factorgraphs;

public class KeyedVariable<TKey, TValue> extends Variable<TValue> {

    private final TKey key;

    public KeyedVariable(final TKey key, final TValue prior, final String name, final Object... args) {
        super(prior, name, args);
        this.key = key;
    }

    public TKey getKey() {
        return key;
    }
}