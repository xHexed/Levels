package com.grassminevn.levels.utils;

import com.grassminevn.levels.Levels;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class KillSessionUtils {
    private final Levels plugin;

    public KillSessionUtils(final Levels plugin) {
        this.plugin = plugin;
    }

    private final Map<String, ArrayList<String>> killsession = new HashMap<>();

    private final Map<String, String> killsessiontime = new HashMap<>();

    public boolean check(final Entity entity, final Entity killer) {
        if (plugin.config.get.getBoolean("kill-session.use")) {
            if (entity instanceof Player) {
                boolean returning = false;
                boolean check = false;
                final Player killed = (Player) entity;
                final String attacker = killer.getUniqueId().toString();
                if (!killsession.containsKey(attacker)) {
                    killsession.put(attacker, new ArrayList<>(Collections.singletonList(killed.getUniqueId() + ";1")));
                } else {
                    for (int i = 0; i < killsession.get(attacker).size(); i++) {
                        if (killed.getUniqueId().toString().equalsIgnoreCase(killsession.get(attacker).get(i).split(";")[0])) {
                            final String uuid = killsession.get(attacker).get(i).split(";")[0];
                            final int nameamount = Integer.parseInt(killsession.get(attacker).get(i).split(";")[1]);
                            final int SessionInt = plugin.config.get.getInt("kill-session.amount");
                            if (killed.getUniqueId().toString().equalsIgnoreCase(uuid))
                                if (nameamount >= SessionInt) {
                                    returning = true;
                                    task(killed, attacker, killer);
                                } else {
                                    killsession.get(attacker).set(i, uuid + ";" + (nameamount + 1));
                                }
                            check = false;
                            break;
                        }
                        check = true;
                    }
                }
                if (check) {
                    killsession.get(attacker).add(killed.getUniqueId() + ";1");
                }
                return returning;
            }
        }
        return false;
    }

    private void task(final Player killed, final String attacker, final CommandSender killer) {
        if (!killsessiontime.containsKey(killed.getUniqueId() + "=" + attacker)) {
            final int id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    if (killsessiontime.containsKey(killed.getUniqueId() + "=" + attacker)) {
                        final String[] split = killsessiontime.get(killed.getUniqueId() + "=" + attacker).split("=");
                        final int time = Integer.parseInt(split[1]);
                        if (time > -1) {
                            killsessiontime.put(killed.getUniqueId() + "=" + attacker, Integer.valueOf(split[0]) + "=" + (time - 1));
                        }
                        if (time == 0) {
                            plugin.getServer().getScheduler().cancelTask(Integer.parseInt(split[0]));
                            killsessiontime.remove(killed.getUniqueId() + "=" + attacker);
                            killsession.remove(attacker);
                            sendMessage(killer, killed, "remove");
                        }
                    }
            }, 0L, 20L);
            killsessiontime.put(killed.getUniqueId() + "=" + attacker, id + "=" + plugin.config.get.getInt("kill-session.time"));
            sendMessage(killer, killed, "get");
        } else {
            sendMessage(killer, killed, "abuse");
        }
    }

    private void sendMessage(final CommandSender killer, final AnimalTamer killed, final String path) {
        for (final String command : plugin.config.get.getStringList("kill-session.commands." + path)) {
            plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{levels_player}", killer.getName()).replace("{levels_killed}", killed.getName()).replace("{levels_amount}", String.valueOf(plugin.config.get.getInt("kill-session.amount"))));
        }
    }
}