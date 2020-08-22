package com.grassminevn.levels.data;

import com.grassminevn.levels.Levels;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class Database {
    private final Levels plugin;
    private Connection connection;
    private final boolean debug_database;

    public Database(final Levels plugin) {
        this.plugin = plugin;
        debug_database = plugin.config.get.getBoolean("debug.database");
        (new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.createStatement().execute("SELECT 1");
                        if (debug_database) { plugin.textUtils.debug("[Database] connection is still valid"); }
                    }
                } catch (final SQLException e) {
                    connection = get();
                    if (debug_database) { plugin.textUtils.debug("[Database] connection is not valid creating new"); }
                }
            }
        }).runTaskTimerAsynchronously(plugin, 60 * 20, 60 * 20);
    }

    private Connection get() {
        try {
            if (plugin.config.get.getBoolean("mysql.use")) {
                Class.forName("com.mysql.jdbc.Driver");
                if (debug_database) { plugin.textUtils.debug("[Database] Getting connection (MySQL)"); }
                return DriverManager.getConnection("jdbc:mysql://" + plugin.config.get.getString("mysql.host") + ":" + plugin.config.get.getString("mysql.port") + "/" + plugin.config.get.getString("mysql.database"), plugin.config.get.getString("mysql.username"), plugin.config.get.getString("mysql.password"));
            } else {
                Class.forName("org.sqlite.JDBC");
                if (debug_database) { plugin.textUtils.debug("[Database] Getting connection (SQLite)"); }
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
            if (debug_database) { plugin.textUtils.debug("[Database] Closing connection"); }
        }
    }

    private boolean check() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = get();
            if (connection == null || connection.isClosed()) {
                return false;
            }
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `levels` (`uuid` varchar(255) PRIMARY KEY, `name` varchar(255), `xp` bigint(255), `level` bigint(255), `coins` bigint(255));");
            if (debug_database) { plugin.textUtils.debug("[Database] Creating table if not exists"); }
        }
        return true;
    }

    public boolean set() {
        try {
            return check();
        } catch (final SQLException e) {
            return false;
        }
    }

    public void insert(final String uuid, final String name) {
        if (set()) {
            final BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM levels WHERE uuid= '" + uuid + "';");
                        if (!resultSet.next()) {
                            preparedStatement = connection.prepareStatement("INSERT INTO levels (uuid, name, xp, level, coins) VALUES(?, ?, ?, ?, ?);");
                            preparedStatement.setString(1, uuid);
                            preparedStatement.setString(2, name);
                            preparedStatement.setLong(3, 0L);
                            preparedStatement.setLong(4, 0L);
                            preparedStatement.setLong(5, 0L);
                            preparedStatement.executeUpdate();
                            if (debug_database) { plugin.textUtils.debug("[Database] Inserting default values for UUID: " + uuid); }
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

    public void delete(final String uuid) {
        if (set()) {
            final BukkitRunnable r = new BukkitRunnable() {
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM levels WHERE uuid= '" + uuid + "';");
                        if (resultSet.next()) {
                            preparedStatement = connection.prepareStatement("DELETE FROM levels WHERE uuid = ?");
                            preparedStatement.setString(1, uuid);
                            preparedStatement.executeUpdate();
                            plugin.unload(uuid);
                            if (debug_database) { plugin.textUtils.debug("[Database] Deleting player: " + uuid); }
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
    
    public void setVaules(final String uuid, final String name, final Long xp, final Long level, final Long coins) {
        if (!set()) return;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            resultSet = connection.createStatement().executeQuery("SELECT * FROM levels WHERE uuid= '" + uuid + "';");
            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE levels SET name = ?, xp = ?, level = ?, coins = ? WHERE uuid = ?");
                preparedStatement.setString(1, name);
                preparedStatement.setLong(2, xp);
                preparedStatement.setLong(3, level);
                preparedStatement.setLong(4, coins);
                preparedStatement.setString(5, uuid);
                preparedStatement.executeUpdate();
                if (debug_database) { plugin.textUtils.debug("[Database] Updating values for UUID: " + uuid); }
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

    public void setValuesAsync(final String uuid, final String name, final Long xp, final Long level, final Long coins) {
        new BukkitRunnable() {
            public void run() {
                setVaules(uuid, name, xp, level, coins);
            }
        }.runTaskAsynchronously(plugin);
    }

    public String[] getValues(final String uuid) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM levels WHERE uuid= '" + uuid + "';");
            if (resultSet.next()) {
                if (debug_database) { plugin.textUtils.debug("[Database] Getting values for UUID: " + uuid); }
                return new String[]{ resultSet.getString("name"), String.valueOf(resultSet.getLong("xp")), String.valueOf(resultSet.getLong("level")), String.valueOf(resultSet.getLong("coins")) };
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
        return new String[] { "", String.valueOf(0L), String.valueOf(0L), String.valueOf(0L), String.valueOf(0L), String.valueOf(0L), String.valueOf(0L), String.valueOf(0L), String.valueOf(new Timestamp(new Date().getTime())) };
    }

    private ArrayList<String> getUUIDList() {
        if (set()) {
            final ArrayList<String> array = new ArrayList<>();
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT * FROM levels");
                while (resultSet.next()) {
                    array.add(resultSet.getString("uuid"));
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
            if (debug_database) { plugin.textUtils.debug("[Database] Getting list of UUID in the table"); }
            return array;
        }
        return new ArrayList<>();
    }

    public void loadOnline() {
        if (!plugin.getServer().getOnlinePlayers().isEmpty() && set()) {
            for (final Player player : plugin.getServer().getOnlinePlayers()) {
                if (!plugin.list().contains(player.getUniqueId().toString()))
                    plugin.load(player.getUniqueId().toString());
            }
        }
        if (debug_database) { plugin.textUtils.debug("[Database] Loading all online players into cache"); }
    }

    public void loadAll() {
        if (set()) {
            for (final String list : getUUIDList()) {
                if (!plugin.list().contains(list))
                    plugin.load(list);
            }
        }
        if (debug_database) { plugin.textUtils.debug("[Database] Loading all players into cache"); }
    }
}