package com.grassminevn.levels.commands;

import com.grassminevn.levels.Levels;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
    private final Levels plugin;

    public StatsCommand(final Levels plugin) {
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
        if (sender.hasPermission("levels.command.pvpstats")) {
            if (args.length == 0) {
                if (type.equalsIgnoreCase("player")) {
                    final Player player = (Player) sender;
                    message(player, player, "player.pvpstats.you.message");
                } else {
                    for (final String message : plugin.language.get.getStringList("console.pvpstats.usage")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            } else if (args.length == 1) {
                final Player target = plugin.getServer().getPlayer(args[0]);
                if (type.equalsIgnoreCase("player")) {
                    if (sender.hasPermission("levels.command.pvpstats.target")) {
                        if (target != null) {
                            message(sender, target, "player.pvpstats.target.message");
                        } else {
                            for (final String message : plugin.language.get.getStringList("player.pvpstats.target.online")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.pvpstats.target.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else {
                    if (target != null) {
                        message(target, target, "console.pvpstats.message");
                    } else {
                        for (final String message : plugin.language.get.getStringList("console.pvpstats.online")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                }
            } else {
                for (final String message : plugin.language.get.getStringList(type + ".pvpstats.usage")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        } else {
            for (final String message : plugin.language.get.getStringList("player.pvpstats.you.permission")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
        return true;
    }

    private void message(final CommandSender player, final Player target, final String path) {
        for (final String message : plugin.language.get.getStringList(path)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(target, message)));
        }
    }
}