package com.grassminevn.levels.data.database;

import com.grassminevn.levels.Levels;

import java.io.File;
import java.sql.*;

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

    public void closeResultSet(final ResultSet resultSet) {
        if (resultSet != null)
            try {
                resultSet.close();
            } catch (final SQLException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            }
    }

    public void closeQuery(final ResultSet resultSet, final PreparedStatement preparedStatement) {
        closeResultSet(resultSet);
        if (preparedStatement != null)
            try {
                preparedStatement.close();
            } catch (final SQLException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            }
    }

    public void closeQuery(final ResultSet resultSet, final Statement statement) {
        closeResultSet(resultSet);
        if (statement != null)
            try {
                statement.close();
            } catch (final SQLException exception) {
                plugin.textUtils.exception(exception.getStackTrace(), exception.getMessage());
            }
    }

    protected abstract void createTable();
}
