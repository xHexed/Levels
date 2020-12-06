package com.grassminevn.levels.data.database;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.playerinfo.PlayerInfo;
import com.grassminevn.levels.jskills.Rating;

import java.sql.*;
import java.util.Date;
import java.util.UUID;

public class PlayerDatabase extends SQLDatabase {
    public PlayerDatabase(final Levels plugin) {
        super(plugin);
    }

    @Override
    protected void createTable() {
        plugin.asyncExecutorManager.execute(() -> {
            try (final Connection connection = getConnection()) {
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `levels_players` (`uuid` char(36) PRIMARY KEY, `group` text(255), `xp` bigint, `level` bigint, `rating` double, `deviation` double, `lastseen` datetime);");
            }
            catch (final SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void insert(final UUID uuid) {
        setValues(uuid, "default", 0L, 0L, 25D, 25D / 3, new Timestamp(new Date().getTime()));
    }

    public void delete(final UUID uuid) {
        plugin.asyncExecutorManager.execute(() -> {
            try (final Connection connection = getConnection();
                final ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_players WHERE uuid= '" + uuid + "';");
                final PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM levels_players WHERE uuid = ?")) {
                if (resultSet.next()) {
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.executeUpdate();
                    plugin.unloadPlayerConnect(uuid);
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void setValuesSync(final UUID uuid, final String group, final Long xp, final Long level, final Double rating, final Double deviation, final Timestamp timestamp) {
        try (final Connection connection = getConnection();
            final ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_players WHERE uuid= '" + uuid + "'")) {
            final PreparedStatement preparedStatement;
            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE levels_players SET `group` = ?, xp = ?, level = ?, rating = ?, deviation =  ?, lastseen = ? WHERE uuid = ?");
                preparedStatement.setString(1, group);
                preparedStatement.setLong(2, xp);
                preparedStatement.setLong(3, level);
                preparedStatement.setDouble(4, rating);
                preparedStatement.setDouble(5, deviation);
                preparedStatement.setTimestamp(6, timestamp);
                preparedStatement.setString(7, uuid.toString());
            }
            else {
                preparedStatement = connection.prepareStatement("INSERT INTO levels_players (uuid, `group`, xp, level, rating, deviation, lastseen) VALUES(?, ?, ?, ?, ?, ?, ?)");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, group);
                preparedStatement.setLong(3, xp);
                preparedStatement.setLong(4, level);
                preparedStatement.setDouble(5, rating);
                preparedStatement.setDouble(6, deviation);
                preparedStatement.setTimestamp(7, timestamp);
            }
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void setValues(final UUID uuid, final String group, final Long xp, final Long level, final Double rating, final Double deviation, final Timestamp timestamp) {
        plugin.asyncExecutorManager.execute(() -> setValuesSync(uuid, group, xp, level, rating, deviation, timestamp));
    }

    public void setPlayerInfo(final UUID uuid, final PlayerInfo info) {
        setValuesSync(uuid,
                      info.getGroup(),
                      info.getXP(),
                      info.getLevel(),
                      info.getRating().getMean(),
                      info.getRating().getStandardDeviation(),
                      info.getTime());
    }

    public PlayerInfo getPlayerInfo(final UUID uuid) {
        try (final Connection connection = getConnection();
            final ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_players WHERE uuid= '" + uuid + "'")){
            if (resultSet.next()) {
                return new PlayerInfo(uuid,
                                      resultSet.getString("group"),
                                      resultSet.getLong("xp"),
                                      resultSet.getLong("level"),
                                      new Rating(resultSet.getDouble("rating"), resultSet.getDouble("deviation")),
                                      resultSet.getTimestamp("lastseen"));
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return new PlayerInfo(uuid,
                              "default",
                              0L,
                              0L,
                              new Rating(25D, 25D / 3),
                              new Timestamp(new Date().getTime()));
    }

    public TopRating getTopRating() {
        return new TopRating(plugin);
    }

    public static class TopRating {
        private ResultSet resultSet;

        public TopRating(final Levels plugin) {
            try (final Connection connection = plugin.database.getPlayerDatabase().getConnection()) {
                resultSet = connection.createStatement().executeQuery("SELECT * FROM `levels_players` ORDER BY `levels_players`.`rating` DESC");
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
        }

        public Double getTop(final int index) {
            try {
                resultSet.getDouble(index);
            }
            catch (final SQLException exception) {
                return 25D;
            }
        }
    }
}