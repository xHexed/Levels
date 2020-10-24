package com.grassminevn.levels.data;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.database.MultiplierDatabase;
import com.grassminevn.levels.data.database.PlayerDatabase;
import com.grassminevn.levels.data.playerinfo.MultiplierInfo;
import com.grassminevn.levels.data.playerinfo.PlayerInfo;

import java.sql.*;
import java.util.UUID;

public class Database {
    private final MultiplierDatabase multiplierDatabase;
    private final PlayerDatabase playerDatabase;

    public Database(final Levels plugin) {
        multiplierDatabase = new MultiplierDatabase(plugin);
        playerDatabase     = new PlayerDatabase(plugin);
    }

    public void close() throws SQLException {
        playerDatabase.close();
        multiplierDatabase.close();
    }

    public boolean set() {
        return playerDatabase.set() && multiplierDatabase.set();
    }

    public void insert(final UUID uuid) {
        multiplierDatabase.insert(uuid);
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
}