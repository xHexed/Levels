package com.grassminevn.levels.data.database;

import com.grassminevn.levels.Levels;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class SQLDatabase {
    protected final Levels plugin;
    protected Connection connection;

    public SQLDatabase(final Levels plugin) {
        this.plugin = plugin;
        set();
    }

    private Connection get() {
        try {
            if (plugin.config.get.getBoolean("mysql.use")) {
                Class.forName("com.mysql.jdbc.Driver");
                final Connection mysqlConnection = DriverManager.getConnection("jdbc:mysql://" + plugin.config.get.getString("mysql.host") + ":" + plugin.config.get.getString("mysql.port") + "/" + plugin.config.get.getString("mysql.database"), plugin.config.get.getString("mysql.username"), plugin.config.get.getString("mysql.password"));
                plugin.textUtils.info("MySQL Database Connected");
                return mysqlConnection;
            } else {
                Class.forName("org.sqlite.JDBC");
                final Connection sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "data.db"));
                plugin.textUtils.info("SQLite Database Connected");
                return sqliteConnection;
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

    public boolean set() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = get();
                if (connection == null || connection.isClosed()) {
                    return false;
                }
                createTable();
            }
            return true;
        } catch (final SQLException e) {
            plugin.textUtils.exception(e.getStackTrace(), e.getMessage());
            return false;
        }
    }

    protected abstract void createTable();
}
