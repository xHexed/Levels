package com.grassminevn.levels;

import com.grassminevn.levels.data.PlayerConnect;
import com.grassminevn.levels.managers.StatsManager;
import com.grassminevn.levels.managers.XPManager;
import com.grassminevn.levels.jskills.ITeam;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LevelsAPI {
    public static PlayerConnect getPlayerConnect(final UUID uuid) {
        return Levels.call.getPlayerConnect(uuid);
    }

    public static Set<UUID> listPlayerConnect() {
        return Levels.call.listPlayerConnect();
    }

    public static void unloadPlayerConnect(final UUID uuid) {
        Levels.call.unloadPlayerConnect(uuid);
    }

    public static StatsManager getStatsManager() {
        return Levels.call.statsManager;
    }

    public static XPManager getXPManager() {
        return Levels.call.xpManager;
    }

    public static void syncSave(final UUID uuid) {
        Levels.call.getPlayerConnect(uuid).syncSave();
    }

    public static void calculateRatings(final List<? extends ITeam> teams, final int[] teamRanks) {
        Levels.call.xpManager.calculateRatings(teams, teamRanks);
    }
}