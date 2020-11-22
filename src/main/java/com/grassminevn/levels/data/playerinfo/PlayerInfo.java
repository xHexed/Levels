package com.grassminevn.levels.data.playerinfo;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.jskills.Rating;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PlayerInfo {
    private final UUID uuid;
    private String group;
    private long xp;
    private long level;
    private Rating rating;
    private Timestamp time;

    public PlayerInfo(final UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerInfo(final UUID uuid, final String group, final long xp, final long level, final Rating rating, final Timestamp time) {
        this.uuid = uuid;
        this.group = group;
        this.xp = xp;
        this.level = level;
        this.rating = rating;
        this.time = time;
    }

    public void setGroup(final String group) {
        this.group = group;
        save();
    }

    public void setXP(final long xp) {
        this.xp = xp;
        save();
    }

    public void setLevel(final long level) {
        this.level = level;
        save();
    }

    public void setRating(final Rating rating) {
        final double pointChanged = rating.getMean() - this.rating.getMean();
        this.rating = rating;
        save();
    }

    public void setTime() {
        time = new Timestamp(new Date().getTime());
    }

    public String getGroup() {
        return group;
    }

    public Long getXP() {
        return xp;
    }

    public Long getLevel() {
        return level;
    }

    public Rating getRating() {
        return rating;
    }

    public Timestamp getTime() {
        return time;
    }

    public void save() {
        Levels.call.asyncExecutorManager.execute(this::syncSave);
    }

    public void syncSave() {
        Levels.call.database.setPlayerInfo(uuid, this);
    }
}
