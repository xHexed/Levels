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
            try (final Connection connection = getConnection()) {
                connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `levels_multiplier` (`uuid` char(36) PRIMARY KEY, `multiplier` double, `multiplier_start_time` datetime, `multiplier_end_time` datetime);");
            }
            catch (final SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void delete(final UUID uuid) {
        plugin.asyncExecutorManager.execute(() -> {
            try (final Connection connection = getConnection();
                final PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM levels_multiplier WHERE uuid = ?");
                final ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_multiplier WHERE uuid= '" + uuid + "';")){
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

    public void setValuesSync(final UUID uuid, final MultiplierInfo info) {
        try (final Connection connection = getConnection();
             final ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM levels_multiplier WHERE uuid= '" + uuid + "';")) {
            final PreparedStatement preparedStatement;
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
            preparedStatement.close();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void setValue(final UUID uuid, final MultiplierInfo multiplierInfo) {
        plugin.asyncExecutorManager.execute(() -> setValuesSync(uuid, multiplierInfo));
    }

    public MultiplierInfo getMultiplierInfo(final UUID uuid) {
        try (final Connection connection = getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT * FROM levels_multiplier WHERE uuid= '" + uuid + "';")) {
            if (resultSet.next()) {
                return new MultiplierInfo(uuid, resultSet.getDouble("multiplier"), resultSet.getTimestamp("multiplier_start_time").getTime(), resultSet.getTimestamp("multiplier_end_time").getTime());
            }
        }
        catch (final SQLException exception) {
            exception.printStackTrace();
        }
        return new MultiplierInfo(uuid);
    }
}
