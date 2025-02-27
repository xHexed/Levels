package com.grassminevn.levels.data;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.database.MultiplierDatabase;
import com.grassminevn.levels.data.database.PlayerDatabase;
import com.grassminevn.levels.data.database.SQLDatabase;
import com.grassminevn.levels.data.playerinfo.MultiplierInfo;
import com.grassminevn.levels.data.playerinfo.PlayerInfo;

import java.util.UUID;

public class Database {
    private final MultiplierDatabase multiplierDatabase;
    private final PlayerDatabase playerDatabase;

    public Database(final Levels plugin) {
        SQLDatabase.init(plugin);
        multiplierDatabase = new MultiplierDatabase(plugin);
        playerDatabase     = new PlayerDatabase(plugin);
    }

    public void close() {
        playerDatabase.close();
        multiplierDatabase.close();
    }

    public void insert(final UUID uuid) {
        playerDatabase.insert(uuid);
    }

    public void delete(final UUID uuid) {
        multiplierDatabase.delete(uuid);
        playerDatabase.delete(uuid);
    }

    public PlayerInfo getPlayerInfo(final UUID uuid) {
        return playerDatabase.getPlayerInfo(uuid);
    }

    public void setPlayerInfo(final UUID uuid, final PlayerInfo info) {
        playerDatabase.setPlayerInfo(uuid, info);
    }

    public MultiplierInfo getMultiplierInfo(final UUID uuid) {
        return multiplierDatabase.getMultiplierInfo(uuid);
    }

    public void setMultiplierInfo(final UUID uuid, final MultiplierInfo info) {
        multiplierDatabase.setValue(uuid, info);
    }

    public void deleteMultiplierInfo(final UUID uuid) {
        multiplierDatabase.delete(uuid);
    }

    public PlayerDatabase getPlayerDatabase() {
        return playerDatabase;
    }
}