package com.grassminevn.levels.data;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.jskills.Player;
import com.grassminevn.levels.jskills.Rating;

import java.sql.Timestamp;
import java.util.*;

public class PlayerConnect extends Player {
    private final UUID uuid;
    private Rating rating;
    private String group;
    private Long xp;
    private Long level;
    private Double multiplier;
    private Integer multiplier_time;
    private Integer multiplier_time_left;
    private Timestamp time;

    public PlayerConnect(final UUID uuid) {
        super(uuid);
        this.uuid = uuid;
        final String[] data = Levels.call.database.getValues(uuid);
        group = data[0];
        xp = Long.parseLong(data[1]);
        level = Long.parseLong(data[2]);
        rating = new Rating(Double.parseDouble(data[3]), Double.parseDouble(data[4]));
        final String[] split = data[5].split(" ");
        if (split.length == 3) {
            multiplier = Double.parseDouble(split[0]);
            multiplier_time = Integer.parseInt(split[1]);
            multiplier_time_left = Integer.parseInt(split[2]);
        } else {
            multiplier = Double.parseDouble(split[0]);
            multiplier_time = Integer.parseInt(split[1]);
            multiplier_time_left = 0;
            save();
        }
        time = Timestamp.valueOf(data[6]);
    }

    public void setGroup(final String group) {
        this.group = group;
    }

    public void setXp(final Long xp) {
        this.xp = xp;
    }

    public void setLevel(final Long level) {
        this.level = level;
    }

    public void setRating(final Rating rating) {
        this.rating = rating;
    }

    public void setMultiplier(final Double multiplier) {
        this.multiplier = multiplier;
    }

    public void setMultiplier_time(final Integer multiplier_time) {
        this.multiplier_time = multiplier_time;
    }

    public void setMultiplier_time_left(final Integer multiplier_time_left) {
        this.multiplier_time_left = multiplier_time_left;
    }

    public void setTime() {
        time = new Timestamp(new Date().getTime());
    }

    public String getGroup() {
        return group;
    }

    public Long getXp() {
        return xp;
    }

    public Long getLevel() {
        return level;
    }

    public Rating getRating() {
        return rating;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public Integer getMultiplier_time() {
        return multiplier_time;
    }

    public Integer getMultiplier_time_left() {
        return multiplier_time_left;
    }

    public Timestamp getTime() {
        return time;
    }

    public void save() {
        Levels.call.database.setValues(uuid, group, xp, level, rating.getMean(), rating.getStandardDeviation(),(multiplier + " " + multiplier_time + " " + multiplier_time_left), time);
    }

    public void syncSave() {
        Levels.call.database.setValuesSync(uuid, group, xp, level, rating.getMean(), rating.getStandardDeviation(), (multiplier + " " + multiplier_time + " " + multiplier_time_left), time);
    }
}