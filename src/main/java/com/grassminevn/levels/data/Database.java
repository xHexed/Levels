package com.grassminevn.levels.data;

import com.grassminevn.levels.Levels;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.UUID;

public class Database {
    private final Levels plugin;
    private Connection connection;

    public Database(final Levels plugin) {
        this.plugin = plugin;
        (new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.createStatement().execute("SELECT 1");
                    }
                } catch (final SQLException e) {
                    connection = get();
                }
            }
        }).runTaskTimerAsynchronously(plugin, 60 * 20, 60 * 20);
    }

    private Connection get() {
        try {
            if (plugin.config.get.getBoolean("mysql.use")) {
                plugin.textUtils.info("Database ( Connected ) ( MySQL )");
                Class.forName("com.mysql.jdbc.Driver");
                return DriverManager.getConnection("jdbc:mysql://" + plugin.config.get.getString("mysql.host") + ":" + plugin.config.get.getString("mysql.port") + "/" + plugin.config.get.getString("mysql.database"), plugin.config.get.getString("mysql.username"), plugin.config.get.getString("mysql.password"));
            } else {
                plugin.textUtils.info("Database ( Connected ) ( SQLite )");
                Class.forName("org.sqlite.JDBC");
                return DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "data.db"));
            }
        } catch (final ClassNotFoundException | SQLException e) {
            plugin.textUtils.exception(e.getStackTrace(), e.getMessage());
            return null;
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private boolean check() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = get();
            if (connection == null || connection.isClosed()) {
                return false;
            }
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `levels` (`uuid` char(36) PRIMARY KEY, `group` text(255), `xp` bigint(255), `level` bigint(255), `rating` double, `multiplier` text(255), `lastseen` DATETIME);");
        }
        return true;
    }

    public boolean set() {
        try {
            return check();
        } catch (final SQLException e) {
            plugin.textUtils.exception(e.getStackTrace(), e.getMessage());
            return false;
        }
    }

    public void insert(final UUID uuid) {
        if (set()) {
            final BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM levels WHERE uuid= '" + uuid + "';");
                        if (!resultSet.next()) {
                            preparedStatement = connection.prepareStatement("INSERT INTO levels (uuid, group, xp, level, rating, multiplier, lastseen) VALUES(?, ?, ?, ?, ?, ?, ?);");
                            preparedStatement.setString(1, uuid.toString());
                            preparedStatement.setString(2, "default");
                            preparedStatement.setLong(3, 0L);
                            preparedStatement.setLong(4, plugin.config.get.getLong("start-level"));
                            preparedStatement.setDouble(5, 0);
                            preparedStatement.setString(6, "0.0 0 0");
                            preparedStatement.setTimestamp(7, new Timestamp(new Date().getTime()));
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
            };
            r.runTaskAsynchronously(plugin);
        }
    }

    public void delete(final UUID uuid) {
        if (set()) {
            final BukkitRunnable r = new BukkitRunnable() {
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM levels WHERE uuid= '" + uuid + "';");
                        if (resultSet.next()) {
                            preparedStatement = connection.prepareStatement("DELETE FROM levels WHERE uuid = ?");
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

    public void setValuesSync(final UUID uuid, final String group, final Long xp, final Long level, final Double rating, final String multiplier, final Timestamp timestamp) {
        if (set()) {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                resultSet = connection.createStatement().executeQuery("SELECT * FROM levels WHERE uuid= '" + uuid + "';");
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE levels SET group = ?, xp = ?, level = ?, rating = ?, multiplier = ?, lastseen = ? WHERE uuid = ?");
                    preparedStatement.setString(1, group);
                    preparedStatement.setLong(2, xp);
                    preparedStatement.setLong(3, level);
                    preparedStatement.setDouble(4, rating);
                    preparedStatement.setString(5, multiplier);
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

    public void setValues(final UUID uuid, final String group, final Long xp, final Long level, final Double rating, final String multiplier, final Timestamp timestamp) {
        if (set()) {
            final BukkitRunnable r = new BukkitRunnable() {
                public void run() {
                    setValuesSync(uuid, group, xp, level, rating, multiplier, timestamp);
                }
            };
            r.runTaskAsynchronously(plugin);
        }
    }

    public String[] getValues(final UUID uuid) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM levels WHERE uuid= '" + uuid + "';");
            if (resultSet.next()) {
                return new String[]{ resultSet.getString("group"), String.valueOf(resultSet.getLong("xp")), String.valueOf(resultSet.getLong("level")), String.valueOf(resultSet.getLong("rating")), resultSet.getString("multiplier"), String.valueOf(resultSet.getTimestamp("lastseen")) };
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
        return new String[] { "default", String.valueOf(0L), String.valueOf(0L), String.valueOf(0L), String.valueOf(plugin.config.get.getLong("start-level")), String.valueOf(0L), String.valueOf(0L), "0.0 0 0", String.valueOf(new Timestamp(new Date().getTime())) };
    }
}