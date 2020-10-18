package com.grassminevn.levels.jskills.factorgraphs;

public class Message<T> {

    private final String nameFormat;
    private final Object[] nameFormatArgs;

    private T value;

    public Message() { this(null, null, (Object[])null); }

    public Message(final T value, final String nameFormat, final Object... args) {
        this.nameFormat = nameFormat;
        nameFormatArgs = args;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(final T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return (nameFormat == null) ? 
                super.toString() : 
                    String.format(nameFormat, nameFormatArgs);
    }
}