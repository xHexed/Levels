package com.grassminevn.levels.data.database;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.playerinfo.PlayerInfo;
import com.grassminevn.levels.jskills.Rating;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.Date;
import java.util.UUID;

public class PlayerDatabase extends SQLDatabase {
    public PlayerDatabase(final Levels plugin) {
        super(plugin);
    }

    @Override
    protected void createTable() {
        if (set()) {
            try {
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `levels_players` (`uuid` char(32) PRIMARY KEY, `group` text(255), `xp` bigint, `level` bigint, `rating` double, `deviation` double, `last_seen` datetime);");
            }
            catch (final SQLException e) {
                plugin.textUtils.exception(e.getStackTrace(), e.getMessage());
            }
        }
    }

    public void insert(final UUID uuid) {
        if (set()) {
            setValues(uuid, "default", 0L, 0L, 25D, 25D / 3, new Timestamp(new Date().getTime()));
        }
    }

    public void delete(final UUID uuid) {
        if (set()) {
            final BukkitRunnable r = new BukkitRunnable() {
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_players WHERE uuid= '" + uuid + "';");
                        if (resultSet.next()) {
                            preparedStatement = connection.prepareStatement("DELETE FROM levels_players WHERE uuid = ?");
                            preparedStatement.setString(1, uuid.toString());
                            preparedStatement.executeUpdate();
                            plugin.unloadPlayerConnect(uuid);
                        }
                    } catch (final SQLException exception) {
                        plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
                    } finally {
                        if (resultSet != null)
                            try {
                                resultSet.close();
                            } catch (final SQLException exception) {
                                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
                            }
                        if (preparedStatement != null)
                            try {
                                preparedStatement.close();
                            } catch (final SQLException exception) {
                                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
                            }
                    }
                }
            };
            r.runTaskAsynchronously(plugin);
        }
    }

    public void setValues(final UUID uuid, final String group, final Long xp, final Long level, final Double rating, final Double deviation, final Timestamp timestamp) {
        if (set()) {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_players WHERE uuid= '" + uuid + "';");
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE levels_players SET group = ?, xp = ?, level = ?, rating = ?, deviation =  ?, lastseen = ? WHERE uuid = ?");
                    preparedStatement.setString(1, group);
                    preparedStatement.setLong(2, xp);
                    preparedStatement.setLong(3, level);
                    preparedStatement.setDouble(4, rating);
                    preparedStatement.setDouble(5, deviation);
                    preparedStatement.setTimestamp(6, timestamp);
                    preparedStatement.setString(7, uuid.toString());
                    preparedStatement.executeUpdate();
                }
            } catch (final SQLException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            } finally {
                if (resultSet != null)
                    try {
                        resultSet.close();
                    } catch (final SQLException exception) {
                        plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
                    }
                if (preparedStatement != null)
                    try {
                        preparedStatement.close();
                    } catch (final SQLException exception) {
                        plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
                    }
            }
        }
    }

    public void setPlayerInfo(final UUID uuid, final PlayerInfo info) {
        setValues(uuid,
                  info.getGroup(),
                  info.getXP(),
                  info.getLevel(),
                  info.getRating().getMean(),
                  info.getRating().getStandardDeviation(),
                  info.getTime());
    }

    public PlayerInfo getPlayerInfo(final UUID uuid) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM levels_players WHERE uuid= '" + uuid + "';");
            if (resultSet.next()) {
                return new PlayerInfo(uuid,
                                      resultSet.getString("group"),
                                      resultSet.getLong("xp"),
                                      resultSet.getLong("level"),
                                      new Rating(resultSet.getLong("rating"), resultSet.getLong("deviation")),
                                      resultSet.getTimestamp("time"));
            }
        } catch (final SQLException exception) {
            plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
        } finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                } catch (final SQLException exception) {
                    plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
                }
            if (statement != null)
                try {
                    statement.close();
                } catch (final SQLException exception) {
                    plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
                }
        }
        return new PlayerInfo(uuid,
                              "default",
                              0L,
                              0L,
                              new Rating(25D, 25D / 3),
                              new Timestamp(new Date().getTime()));
    }
}