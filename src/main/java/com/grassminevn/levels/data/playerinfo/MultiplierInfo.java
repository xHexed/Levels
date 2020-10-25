package com.grassminevn.levels.data.playerinfo;

import com.grassminevn.levels.Levels;

import java.util.UUID;

public class MultiplierInfo {
    private final UUID uuid;
    private double multiplier;
    private long startTime;
    private long endTime;
    private boolean running;

    public MultiplierInfo(final UUID uuid) {
        this.uuid = uuid;
        running = false;
    }

    public MultiplierInfo(final UUID uuid, final double multiplier, final long startTime, final long endTime) {
        this.uuid = uuid;
        this.multiplier = multiplier;
        this.startTime  = startTime;
        this.endTime    = endTime;
        running = true;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setMultiplier(final double multiplier, final long startTime, final long endTime) {
        this.multiplier = multiplier;
        this.startTime = startTime;
        this.endTime = endTime;
        running = true;
        Levels.call.database.setMultiplierInfo(uuid, this);
        Levels.call.multiplierManager.addMultiplier(uuid, endTime - startTime);
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        running = false;
        Levels.call.database.deleteMultiplierInfo(uuid);
    }
}
