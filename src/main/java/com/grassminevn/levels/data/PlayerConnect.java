package com.grassminevn.levels.data;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.playerinfo.MultiplierInfo;
import com.grassminevn.levels.data.playerinfo.PlayerInfo;
import com.grassminevn.levels.jskills.Player;
import com.grassminevn.levels.jskills.Rating;

import java.sql.Timestamp;
import java.util.*;

public class PlayerConnect extends Player {
    private final PlayerInfo playerInfo;
    private final MultiplierInfo multiplierInfo;

    public PlayerConnect(final UUID uuid) {
        super(uuid);
        playerInfo = Levels.call.database.getPlayerInfo(uuid);
        multiplierInfo = Levels.call.database.getMultiplierInfo(uuid);
    }

    public void setGroup(final String group) {
        playerInfo.setGroup(group);
        save();
    }

    public void setXP(final long xp) {
        playerInfo.setXP(xp);
        save();
    }

    public void setLevel(final long level) {
        playerInfo.setLevel(level);
        save();
    }

    public void setRating(final Rating rating) {
        playerInfo.setRating(rating);
        save();
    }

    public void setMultiplier(final double multiplier, final long multiplierStartTime, final long multiplierEndTime) {
        multiplierInfo.setMultiplier(multiplier, multiplierStartTime, multiplierEndTime);
        save();
    }

    public void setTime() {
        playerInfo.setTime();
    }

    public String getGroup() {
        return playerInfo.getGroup();
    }

    public Long getXP() {
        return playerInfo.getXP();
    }

    public Long getLevel() {
        return playerInfo.getLevel();
    }

    public Rating getRating() {
        return playerInfo.getRating();
    }

    public double getMultiplier() {
        return multiplierInfo.getMultiplier();
    }

    public int getMultiplierTime() {
        return (int) multiplierInfo.getTime();
    }

    public int getMultiplierTimeLeft() {
        return (int) (multiplierInfo.getEndTime() - new Date().getTime());
    }

    public Timestamp getTime() {
        return playerInfo.getTime();
    }

    public void save() {
        Levels.call.asyncExecutorManager.execute(() -> Levels.call.database.setPlayerInfo(uuid, playerInfo));
    }

    public void syncSave() {
        Levels.call.database.setPlayerInfo(uuid, playerInfo);
    }

    public MultiplierInfo getMultiplierInfo() {
        return multiplierInfo;
    }
}