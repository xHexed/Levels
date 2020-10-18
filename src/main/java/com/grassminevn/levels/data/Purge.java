package com.grassminevn.levels.data;

import com.grassminevn.levels.Levels;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Purge {

    private final Levels plugin;

    public Purge(final Levels plugin) {
        this.plugin = plugin;
        final int interval = plugin.config.get.getInt("mysql.purge.interval");
        int startInterval = interval;
        if (plugin.config.get.getBoolean("mysql.purge.check-on-startup")) {
            startInterval = 1;
        }
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (final UUID uuid : plugin.listPlayerConnect()) {
                if (isOld(plugin.getPlayerConnect(uuid).getTime())) {
                    plugin.database.delete(uuid);
                    if (plugin.config.get.contains("mysql.purge.commands")) {
                        for (final String command : plugin.config.get.getStringList("mysql.purge.commands")) {
                            plugin.getServer().dispatchCommand(plugin.consoleSender, command.replace("{uuid}", uuid.toString()));
                        }
                    }
                }
            }
            }, startInterval * 20, interval * 20);
    }

    private boolean isOld(final Timestamp date) {
        return ChronoUnit.DAYS.between(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()) > plugin.config.get.getInt("mysql.purge.inactive-days");
    }
}