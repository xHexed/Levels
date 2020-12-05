package com.grassminevn.levels.data;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.managers.Manager;
import org.bukkit.Bukkit;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class Purger extends Manager implements Runnable {
    private int taskId;

    public Purger(final Levels plugin) {
        super(plugin);
    }

    private boolean isOld(final Timestamp date) {
        return ChronoUnit.DAYS.between(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()) > plugin.config.get.getInt("mysql.purge.inactive-days");
    }

    public void startPurging() {
        final int interval = plugin.config.get.getInt("mysql.purge.interval");
        int startInterval = interval;
        if (plugin.config.get.getBoolean("mysql.purge.check-on-startup")) {
            startInterval = 1;
        }
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, startInterval * 20, interval * 20);
    }

    public void stopPurging() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    @Override
    public void run() {
        for (final PlayerConnect playerConnect : plugin.listPlayerConnect()) {
            if (!isOld(playerConnect.getTime())) continue;
            plugin.database.delete(playerConnect.getUUID());
            if (!plugin.config.get.contains("mysql.purge.commands")) continue;
            for (final String command : plugin.config.get.getStringList("mysql.purge.commands")) {
                plugin.getServer().dispatchCommand(plugin.consoleSender, command.replace("{uuid}", playerConnect.getUUID().toString()));
            }
        }
    }
}