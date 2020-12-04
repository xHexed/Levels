package com.grassminevn.levels.data.database;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.managers.Manager;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.sql.*;

public abstract class SQLDatabase extends Manager {
    private static final HikariDataSource dataSource = new HikariDataSource();

    public SQLDatabase(final Levels plugin) {
        super(plugin);
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
