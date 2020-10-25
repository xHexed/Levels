package com.grassminevn.levels.data.database;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.playerinfo.MultiplierInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.UUID;

public class MultiplierDatabase extends SQLDatabase {
    public MultiplierDatabase(final Levels plugin) {
        super(plugin);
    }

    @Override
    protected void createTable() {
        if (set()) {
            try {
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `levels_multiplier` (`uuid` char(32) PRIMARY KEY, `multiplier` double, `multiplier_start_time` datetime, `multiplier_end_time` datetime);");
            }
            catch (final SQLException e) {
                plugin.textUtils.exception(e.getStackTrace(), e.getMessage());
            }
        }
    }

    public void insert(final UUID uuid) {
        setValue(uuid, new MultiplierInfo(uuid));
    }

    public void delete(final UUID uuid) {
        if (set()) {
            final BukkitRunnable r = new BukkitRunnable() {
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_multiplier WHERE uuid= '" + uuid + "';");
                        if (resultSet.next()) {
                            preparedStatement = connection.prepareStatement("DELETE FROM levels_multiplier WHERE uuid = ?");
                            preparedStatement.setString(1, uuid.toString());
                            preparedStatement.executeUpdate();
                            plugin.unloadPlayerConnect(uuid);
                        }
                    } catch (final SQLException exception) {
                        plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
                    } finally {
                        closeQuery(resultSet, preparedStatement);
                    }
                }
            };
            r.runTaskAsynchronously(plugin);
        }
    }

    public void setValuesSync(final UUID uuid, final MultiplierInfo info) {
        if (set()) {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_multiplier WHERE uuid= '" + uuid + "';");
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE levels_multiplier SET multiplier = ?, multiplier_start_time = ?, multiplier_end_time = ? WHERE uuid = ?");
                    preparedStatement.setDouble(1, info.getMultiplier());
                    preparedStatement.setLong(2, info.getStartTime());
                    preparedStatement.setLong(3, info.getEndTime());
                    preparedStatement.setString(4, uuid.toString());
                }
                else {
                    preparedStatement = connection.prepareStatement("INSERT INTO levels_multiplier (uuid, multiplier, multiplier_start_time, multiplier_end_time) VALUES(?, ?, ?, ?);");
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.setDouble(2, info.getMultiplier());
                    preparedStatement.setLong(3, info.getStartTime());
                    preparedStatement.setLong(4, info.getEndTime());
                }
                preparedStatement.executeUpdate();
            } catch (final SQLException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            } finally {
                closeQuery(resultSet, preparedStatement);
            }
        }
    }

    public void setValue(final UUID uuid, final MultiplierInfo multiplierInfo) {
        new BukkitRunnable() {
            public void run() {
                setValuesSync(uuid, multiplierInfo);
            }
        }.runTaskAsynchronously(plugin);
    }

    public MultiplierInfo getMultiplierInfo(final UUID uuid) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM levels_multiplier WHERE uuid= '" + uuid + "';");
            if (resultSet.next()) {
                return new MultiplierInfo(uuid, resultSet.getDouble("multiplier"), resultSet.getLong("multiplier_start_time"), resultSet.getLong("multiplier_end_time"));
            }
        } catch (final SQLException exception) {
            plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
        } finally {
            closeQuery(resultSet, statement);
        }
        return new MultiplierInfo(uuid);
    }
}
