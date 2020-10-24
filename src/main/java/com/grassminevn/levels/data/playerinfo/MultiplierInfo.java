package com.grassminevn.levels.data.playerinfo;

public class MultiplierInfo {
    private double multiplier;
    private long startTime;
    private long endTime;

    public MultiplierInfo(final double multiplier, final long startTime, final long endTime) {
        this.multiplier = multiplier;
        this.startTime  = startTime;
        this.endTime    = endTime;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(final double multiplier) {
        this.multiplier = multiplier;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }
}
