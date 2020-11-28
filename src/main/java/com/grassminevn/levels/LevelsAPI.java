package com.grassminevn.levels;

import com.grassminevn.levels.data.PlayerConnect;
import com.grassminevn.levels.jskills.IPlayer;
import com.grassminevn.levels.jskills.ITeam;
import com.grassminevn.levels.jskills.Rating;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LevelsAPI {
    public static PlayerConnect getPlayerConnect(final UUID uuid) {
        return Levels.call.getPlayerConnect(uuid);
    }

    public static void syncSave(final UUID uuid) {
        Levels.call.getPlayerConnect(uuid).syncSave();
    }

    public static Map<IPlayer, Rating> calculateRatings(final List<? extends ITeam> teams, final int[] teamRanks) {
        return Levels.call.xpManager.calculateRatings(teams, teamRanks);
    }
}