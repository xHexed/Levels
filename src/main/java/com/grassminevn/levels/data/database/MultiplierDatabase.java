package com.grassminevn.levels.data.database;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.playerinfo.MultiplierInfo;

import java.sql.*;
import java.util.UUID;

public class MultiplierDatabase extends SQLDatabase {
    public MultiplierDatabase(final Levels plugin) {
        super(plugin);
    }

    @Override
    protected void createTable() {
        plugin.asyncExecutorManager.execute(() -> {
            try {
                getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS `levels_multiplier` (`uuid` char(36) PRIMARY KEY, `multiplier` double, `multiplier_start_time` datetime, `multiplier_end_time` datetime);");
            }
            catch (final SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void delete(final UUID uuid) {
        plugin.asyncExecutorManager.execute(() -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            final Connection connection = getConnection();
            try {
                resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_multiplier WHERE uuid= '" + uuid + "';");
                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("DELETE FROM levels_multiplier WHERE uuid = ?");
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.executeUpdate();
                    plugin.unloadPlayerConnect(uuid);
                }
            } catch (final SQLException exception) {
                exception.printStackTrace();
            } finally {
                closeQuery(resultSet, preparedStatement);
            }
        });
    }

    public void setValuesSync(final UUID uuid, final MultiplierInfo info) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        final Connection connection = getConnection();
        try {
            resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_multiplier WHERE uuid= '" + uuid + "';");
            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE levels_multiplier SET multiplier = ?, multiplier_start_time = ?, multiplier_end_time = ? WHERE uuid = ?");
                preparedStatement.setDouble(1, info.getMultiplier());
                preparedStatement.setTimestamp(2, new Timestamp(info.getStartTime()));
                preparedStatement.setTimestamp(3, new Timestamp(info.getEndTime()));
                preparedStatement.setString(4, uuid.toString());
            }
            else {
                preparedStatement = connection.prepareStatement("INSERT INTO levels_multiplier (uuid, multiplier, multiplier_start_time, multiplier_end_time) VALUES(?, ?, ?, ?);");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setDouble(2, info.getMultiplier());
                preparedStatement.setTimestamp(3, new Timestamp(info.getStartTime()));
                preparedStatement.setTimestamp(4, new Timestamp(info.getEndTime()));
            }
            preparedStatement.executeUpdate();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        } finally {
            closeQuery(resultSet, preparedStatement);
        }
    }

    public void setValue(final UUID uuid, final MultiplierInfo multiplierInfo) {
        plugin.asyncExecutorManager.execute(() -> setValuesSync(uuid, multiplierInfo));
    }

    public MultiplierInfo getMultiplierInfo(final UUID uuid) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = getConnection().createStatement();
            resultSet = statement.executeQuery("SELECT * FROM levels_multiplier WHERE uuid= '" + uuid + "';");
            if (resultSet.next()) {
                return new MultiplierInfo(uuid, resultSet.getDouble("multiplier"), resultSet.getTimestamp("multiplier_start_time").getTime(), resultSet.getTimestamp("multiplier_end_time").getTime());
            }
        } catch (final SQLException exception) {
            exception.printStackTrace();
        } finally {
            closeQuery(resultSet, statement);
        }
        return new MultiplierInfo(uuid);
    }
}
