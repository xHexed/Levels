package com.grassminevn.levels.commands;

import com.grassminevn.levels.Levels;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;

public class BoostersCommand implements CommandExecutor {
    private final Levels plugin;

    public BoostersCommand(final Levels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        final String type;
        if (sender instanceof Player) {
            type = "player";
        } else {
            type = "console";
        }
        if (sender.hasPermission("levels.command.pvpboosters")) {
            if (args.length == 0) {
                if (type.equalsIgnoreCase("player")) {
                    run((AnimalTamer) sender, type);
                } else {
                    for (final String message : plugin.language.get.getStringList("console.pvpboosters.usage")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            } else if (args.length == 1) {
                final Player target = plugin.getServer().getPlayer(args[0]);
                if (type.equalsIgnoreCase("player")) {
                    if (sender.hasPermission("levels.command.pvpboosters.target")) {
                        if (target != null) {
                            run(target, type);
                        } else {
                            for (final String message : plugin.language.get.getStringList("player.pvpboosters.online")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.pvpboosters.target.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else {
                    if (target != null) {
                        run(target, type);
                    } else {
                        for (final String message : plugin.language.get.getStringList("console.pvpboosters.online")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                }
            } else {
                for (final String message : plugin.language.get.getStringList(type + ".pvpboosters.usage")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        } else {
            for (final String message : plugin.language.get.getStringList("player.pvpboosters.permission")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
        return true;
    }

    private void run(final AnimalTamer target, final String type) {
        for (final String command : plugin.language.get.getStringList(type + ".pvpboosters.commands")) {
            plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{levels_player}", target.getName()));
        }
    }
}
