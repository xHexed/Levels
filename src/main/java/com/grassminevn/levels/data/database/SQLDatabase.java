package com.grassminevn.levels.data.database;

import com.grassminevn.levels.Levels;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.sql.*;

public abstract class SQLDatabase {
    private static final HikariDataSource dataSource = new HikariDataSource();
    protected final Levels plugin;

    public SQLDatabase(final Levels plugin) {
        this.plugin = plugin;
        createTable();
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        dataSource.close();
    }

    public void closeResultSet(final ResultSet resultSet) {
        if (resultSet != null)
            try {
                resultSet.close();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
    }

    public void closeQuery(final ResultSet resultSet, final PreparedStatement preparedStatement) {
        closeResultSet(resultSet);
        if (preparedStatement != null)
            try {
                preparedStatement.close();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
    }

    public void closeQuery(final ResultSet resultSet, final Statement statement) {
        closeResultSet(resultSet);
        if (statement != null)
            try {
                statement.close();
            } catch (final SQLException exception) {
                exception.printStackTrace();
            }
    }

    protected abstract void createTable();

    public static void init(final Levels plugin) {
        final ConfigurationSection section = plugin.config.get.getConfigurationSection("mysql");
        dataSource.setJdbcUrl("jdbc:mysql://" + section.getString("host") + ":" + section.getString("port") + "/" + section.getString("database") + section.getString("parameters"));
        if (!section.getBoolean("use")) {
            dataSource.setJdbcUrl("jdbc:sqlite:" + new File(plugin.getDataFolder(), "data.db"));
        }
        dataSource.setPoolName("levels-connection-pool");
        dataSource.setUsername(section.getString("username"));
        dataSource.setPassword(section.getString("password"));
    }
}
