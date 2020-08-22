package com.grassminevn.levels.commands;

import com.grassminevn.levels.Levels;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand implements CommandExecutor {
    private final Levels plugin;

    public TopCommand(final Levels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if (sender.hasPermission("levels.command.pvptop")) {
            final String type;
            if (sender instanceof Player) {
                type = "player";
            } else {
                type = "console";
            }
            if (args.length == 0) {
                if (type.equalsIgnoreCase("player")) {
                    for (final String message : plugin.language.get.getStringList("player.pvptop.usage")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                } else {
                    for (final String message : plugin.language.get.getStringList("console.pvptop.usage")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            } else if (args.length == 1) {
                if (type.equalsIgnoreCase("player")) {
                    final Player player = (Player) sender;
                    if (args[0].equalsIgnoreCase("kills")) {
                        if (sender.hasPermission("levels.command.pvptop.kills")) {
                            message(player, "player.pvptop.kills.message");
                        } else {
                            for (final String message : plugin.language.get.getStringList("player.pvptop.kills.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("deaths")) {
                        if (sender.hasPermission("levels.command.pvptop.deaths")) {
                            message(player, "player.pvptop.deaths.message");
                        } else {
                            for (final String message : plugin.language.get.getStringList("player.pvptop.deaths.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("xp")) {
                        if (sender.hasPermission("levels.command.pvptop.xp")) {
                            message(player, "player.pvptop.xp.message");
                        } else {
                            for (final String message : plugin.language.get.getStringList("player.pvptop.xp.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("level")) {
                        if (sender.hasPermission("levels.command.pvptop.level")) {
                            message(player, "player.pvptop.level.message");
                        } else {
                            for (final String message : plugin.language.get.getStringList("player.pvptop.level.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("killstreak")) {
                        if (sender.hasPermission("levels.command.pvptop.killstreak")) {
                            message(player, "player.pvptop.killstreak.message");
                        } else {
                            for (final String message : plugin.language.get.getStringList("player.pvptop.killstreak.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("killstreak_top")) {
                        if (sender.hasPermission("levels.command.pvptop.killstreak_top")) {
                            message(player, "player.pvptop.killstreak_top.message");
                        } else {
                            for (final String message : plugin.language.get.getStringList("player.pvptop.killstreak_top.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.pvptop.usage")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else {
                    for (final String message : plugin.language.get.getStringList("console.pvptop.usage")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            } else if (args.length == 2) {
                if (type.equalsIgnoreCase("console")) {
                    final Player target = plugin.getServer().getPlayer(args[1]);
                    if (target != null) {
                        if (args[0].equalsIgnoreCase("kills")) {
                            message(target, "console.pvptop.kills.message");
                        } else if (args[0].equalsIgnoreCase("deaths")) {
                            message(target, "console.pvptop.deaths.message");
                        } else if (args[0].equalsIgnoreCase("xp")) {
                            message(target, "console.pvptop.xp.message");
                        } else if (args[0].equalsIgnoreCase("level")) {
                            message(target, "console.pvptop.level.message");
                        } else if (args[0].equalsIgnoreCase("killstreak")) {
                            message(target, "console.pvptop.killstreak.message");
                        } else if (args[0].equalsIgnoreCase("killstreak_top")) {
                            message(target, "console.pvptop.killstreak_top.message");
                        } else {
                            for (final String message : plugin.language.get.getStringList("console.pvptop.usage")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("console.pvptop.online")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else {
                    for (final String message : plugin.language.get.getStringList("player.pvptop.usage")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            } else {
                if (type.equalsIgnoreCase("console")) {
                    for (final String message : plugin.language.get.getStringList("console.pvptop.usage")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                } else {
                    for (final String message : plugin.language.get.getStringList("player.pvptop.usage")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            }
        } else {
            for (final String message : plugin.language.get.getStringList("player.pvptop.permission")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
        return true;
    }

    private void message(final Player player, final String path) {
        for (final String message : plugin.language.get.getStringList(path)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(player, message)));
        }
    }
}