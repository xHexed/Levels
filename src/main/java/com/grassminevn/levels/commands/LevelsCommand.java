package com.grassminevn.levels.commands;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import com.grassminevn.levels.gui.AdminGUI;
import com.grassminevn.levels.gui.ProfilesGUI;
import com.grassminevn.levels.jskills.Rating;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LevelsCommand implements CommandExecutor {

    private final Levels plugin;

    public LevelsCommand(final Levels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        boolean unknown = true;
        final String type;
        if (sender instanceof Player) {
            type = "player";
        } else {
            type = "console";
        }
        if (sender.hasPermission("pvplevels")) {
            if (args.length == 0) {
                if (type.equalsIgnoreCase("player")) {
                    for (final String message : plugin.language.get.getStringList("command.message")) {
                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName()).replace("{version}", plugin.getDescription().getVersion())));
                    }
                } else {
                    for (final String message : plugin.language.get.getStringList("console.command.message")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{version}", plugin.getDescription().getVersion())));
                    }
                }
            } else {
                if (args[0].equalsIgnoreCase("help")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.player.help")) {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("help.message")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList("console.help.message")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("help.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.admin.reload")) {
                        plugin.config.load();
                        plugin.language.load();
                        plugin.levels.load();
                        plugin.execute.load();
                        plugin.guiFolder.load();
                        if (plugin.isLevelsValid()) {
                            if (type.equalsIgnoreCase("player")) {
                                for (final String message : plugin.language.get.getStringList("reload.all")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.reload.all")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            if (type.equalsIgnoreCase("player")) {
                                for (final String message : plugin.language.get.getStringList("reload.validator")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.reload.validator")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("reload.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("admin")) {
                    unknown = false;
                    if (type.equalsIgnoreCase("player")) {
                        if (sender.hasPermission("pvplevels.admin.admin")) {
                            final Player player = (Player) sender;
                            new AdminGUI(plugin.getPlayerMenu(player)).open();
                        } else {
                            for (final String message : plugin.language.get.getStringList("admin.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("console.only-player")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("profiles")) {
                    unknown = false;
                    if (type.equalsIgnoreCase("player")) {
                        if (sender.hasPermission("pvplevels.player.profiles")) {
                            final Player player = (Player) sender;
                            new ProfilesGUI(plugin.getPlayerMenu(player)).open();
                        } else {
                            for (final String message : plugin.language.get.getStringList("profiles.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("console.only-player")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("top")) {
                    unknown = false;
                    if (type.equalsIgnoreCase("player")) {
                        final Player player = (Player) sender;
                        if (sender.hasPermission("pvplevels.player.top")) {
                            if (args.length == 2) {
                                if (args[1].equalsIgnoreCase("xp")) {
                                    for (final String message : plugin.language.get.getStringList("top.xp")) {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.replacePlaceholders(player, message)));
                                    }
                                } else if (args[1].equalsIgnoreCase("level")) {
                                    for (final String message : plugin.language.get.getStringList("top.level")) {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.replacePlaceholders(player, message)));
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("top.usage")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                    }
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("top.usage")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList("top.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("console.only-player")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("stats")) {
                    unknown = false;
                    if (type.equalsIgnoreCase("player")) {
                        if (sender.hasPermission("pvplevels.player.top")) {
                            final Player player = (Player) sender;
                            if (args.length == 1) {
                                for (final String message : plugin.language.get.getStringList("stats.message")) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.replacePlaceholders(player, message)));
                                }
                            } else {
                                final Player target = plugin.getServer().getPlayer(args[1]);
                                if (target != null) {
                                    for (final String message : plugin.language.get.getStringList("stats.target")) {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.replacePlaceholders(target, message)));
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("stats.online")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                    }
                                }
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList("stats.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("console.only-player")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("save")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.admin.save")) {
                        for (final UUID uuid : plugin.listPlayerConnect()) {
                            plugin.getPlayerConnect(uuid).save();
                        }
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("save.message")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList("console.save.message")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("save.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("broadcast")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.admin.broadcast")) {
                        if (args.length > 2) {
                            final StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                sb.append(args[i]).append(" ");
                            }
                            final String text = sb.toString().trim();
                            if (!text.contains("\\n")) {
                                broadcast(ChatColor.translateAlternateColorCodes('&', text), args);
                            } else {
                                for (final String message : text.split(Pattern.quote("\\n"))) {
                                    broadcast(ChatColor.translateAlternateColorCodes('&', message), args);
                                }
                            }
                        } else {
                            if (type.equalsIgnoreCase("player")) {
                                for (final String message : plugin.language.get.getStringList("broadcast.usage")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.broadcast.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("broadcast.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("message")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.admin.message")) {
                        if (args.length <= 2) {
                            if (type.equalsIgnoreCase("player")) {
                                for (final String message : plugin.language.get.getStringList("message.usage")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.message.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            final Player target = plugin.getServer().getPlayer(args[1]);
                            if (target != null) {
                                final StringBuilder sb = new StringBuilder();
                                for (int i = 2; i < args.length; i++) {
                                    sb.append(args[i]).append(" ");
                                }
                                final String text = sb.toString().trim();
                                if (!text.contains("\\n")) {
                                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
                                } else {
                                    for (final String message : text.split(Pattern.quote("\\n"))) {
                                        target.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                if (type.equalsIgnoreCase("player")) {
                                    for (final String message : plugin.language.get.getStringList("message.online")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("console.message.online")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("message.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("actionbar")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.admin.actionbar")) {
                        if (args.length > 3) {
                            final Player target = plugin.getServer().getPlayer(args[1]);
                            if (target != null) {
                                if (plugin.isInt(args[2])) {
                                    final StringBuilder sb = new StringBuilder();
                                    for (int i = 3; i < args.length; i++) {
                                        sb.append(args[i]).append(" ");
                                    }
                                    try {
                                        final int seconds = Integer.parseInt(args[2]);
                                        new BukkitRunnable() {
                                            int time;

                                            @Override
                                            public void run() {
                                                if (time >= seconds) {
                                                    cancel();
                                                } else {
                                                    time++;
                                                    target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.replacePlaceholders(target, sb.toString().trim()))));
                                                }
                                            }
                                        }.runTaskTimer(plugin, 0, 20);
                                    } catch (final NoSuchMethodError exception) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command is not supported in this version."));
                                    }
                                } else {
                                    if (type.equalsIgnoreCase("player")) {
                                        for (final String message : plugin.language.get.getStringList("actionbar.number")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList("console.actionbar.number")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                }
                            } else {
                                if (type.equalsIgnoreCase("player")) {
                                    for (final String message : plugin.language.get.getStringList("actionbar.online")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("console.actionbar.online")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            }
                        } else {
                            if (type.equalsIgnoreCase("player")) {
                                for (final String message : plugin.language.get.getStringList("actionbar.usage")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.actionbar.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("actionbar.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("group")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.admin.group")) {
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("set")) {
                                if (args.length == 4) {
                                    final Player target = plugin.getServer().getPlayer(args[2]);
                                    if (target != null) {
                                        if (plugin.isString(args[3]) && plugin.levels.get.contains(args[3])) {
                                            final PlayerConnect playerConnect = plugin.getPlayerConnect(target.getUniqueId());
                                            playerConnect.setGroup(args[3]);
                                            if (type.equalsIgnoreCase("player")) {
                                                for (final String message : plugin.language.get.getStringList("group.set.message")) {
                                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName()).replace("{target}", target.getName()).replace("{group}", args[3])));
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList("console.group.set.message")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{group}", args[3])));
                                                }
                                            }
                                        } else {
                                            if (type.equalsIgnoreCase("player")) {
                                                for (final String message : plugin.language.get.getStringList("group.valid")) {
                                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList("console.group.valid")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        }
                                    } else {
                                        if (type.equalsIgnoreCase("player")) {
                                            for (final String message : plugin.language.get.getStringList("group.online")) {
                                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList("console.group.online")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    }
                                } else {
                                    if (type.equalsIgnoreCase("player")) {
                                        for (final String message : plugin.language.get.getStringList("group.set.usage")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList("console.group.set.usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                }
                            } else if (args[1].equalsIgnoreCase("reset")) {
                                if (args.length == 3) {
                                    final Player target = plugin.getServer().getPlayer(args[2]);
                                    if (target != null) {
                                        final PlayerConnect playerConnect = plugin.getPlayerConnect(target.getUniqueId());
                                        playerConnect.setGroup("default");
                                        if (type.equalsIgnoreCase("player")) {
                                            for (final String message : plugin.language.get.getStringList("group.reset.message")) {
                                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName()).replace("{target}", target.getName())));
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList("console.group.reset.message")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        if (type.equalsIgnoreCase("player")) {
                                            for (final String message : plugin.language.get.getStringList("group.online")) {
                                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList("console.group.online")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    }
                                } else {
                                    if (type.equalsIgnoreCase("player")) {
                                        for (final String message : plugin.language.get.getStringList("group.reset.usage")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList("console.group.reset.usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                }
                            } else {
                                if (type.equalsIgnoreCase("player")) {
                                    for (final String message : plugin.language.get.getStringList("group.usage")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("console.group.usage")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            }
                        } else {
                            if (type.equalsIgnoreCase("player")) {
                                for (final String message : plugin.language.get.getStringList("group.usage")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.group.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("group.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("reset")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.admin.reset")) {
                        if (args.length > 2) {
                            final Player target = plugin.getServer().getPlayer(args[2]);
                            if (target != null) {
                                if (args[1].equalsIgnoreCase("level")) {
                                    final PlayerConnect playerConnect = plugin.getPlayerConnect(target.getUniqueId());
                                    playerConnect.setLevel(plugin.config.get.getLong("start-level"));
                                    playerConnect.setXp(0L);
                                    triggerReset(type, sender, target, "level");
                                } else if (args[1].equalsIgnoreCase("stats")) {
                                    if (args.length == 4) {
                                        final PlayerConnect playerConnect = plugin.getPlayerConnect(target.getUniqueId());
                                        playerConnect.setXp(0L);
                                        playerConnect.setLevel(plugin.config.get.getLong("start-level"));
                                        playerConnect.setRating(new Rating(25, 25D / 3));
                                        if (Boolean.parseBoolean(args[2])) {
                                            playerConnect.setGroup("default");
                                        }
                                        triggerReset(type, sender, target, "stats");
                                    } else {
                                        if (type.equalsIgnoreCase("player")) {
                                            for (final String message : plugin.language.get.getStringList("reset.stats.usage")) {
                                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList("console.reset.stats.usage")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    }
                                } else {
                                    if (type.equalsIgnoreCase("player")) {
                                        for (final String message : plugin.language.get.getStringList("reset.found")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList("console.reset.found")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                }
                            } else {
                                if (type.equalsIgnoreCase("player")) {
                                    for (final String message : plugin.language.get.getStringList("reset.online")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("console.reset.online")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            }
                        } else {
                            if (type.equalsIgnoreCase("player")) {
                                for (final String message : plugin.language.get.getStringList("reset.usage")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.reset.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("reset.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("xp")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.admin.xp")) {
                        if (args.length == 3) {
                            final Player target = plugin.getServer().getPlayer(args[1]);
                            if (target != null) {
                                if (plugin.isLong(args[2])) {
                                    final PlayerConnect playerConnect = plugin.getPlayerConnect(target.getUniqueId());
                                    long set = Long.parseLong(args[2]);
                                    boolean plus = true;
                                    if (args[2].contains("+")) {
                                        set = playerConnect.getXp() + Long.parseLong(args[2].replace("+", ""));
                                    } else if (args[2].contains("-")) {
                                        set = playerConnect.getXp() - Long.parseLong(args[2].replace("-", ""));
                                        plus = false;
                                    }
                                    long number = plugin.config.get.getLong("start-level");
                                    for (final long integer : xpList(playerConnect)) {
                                        if (set >= integer && integer != 0) {
                                            number++;
                                        }
                                    }
                                    if (set >= 0) {
                                        final Set<String> stringList = plugin.levels.get.getConfigurationSection(playerConnect.getGroup()).getKeys(false);
                                        stringList.remove("execute");
                                        final Set<Integer> list = stringList.stream().map(Integer::parseInt).collect(Collectors.toSet());
                                        final long maxXP = plugin.levels.get.getLong(playerConnect.getGroup() + "." + Collections.max(list) + ".xp");
                                        playerConnect.setXp(Math.min(set, maxXP));
                                    } else {
                                        playerConnect.setXp(0L);
                                    }
                                    if (playerConnect.getLevel() != number) {
                                        if (plus) {
                                            playerConnect.setLevel(number - 1);
                                            plugin.xpManager.sendCommands(target, plugin.levels.get.getString(playerConnect.getGroup() + "." + number + ".execute") + ".level.up", plugin.execute.get, "", 0, 0, 0, 0);
                                            playerConnect.setLevel(number);
                                        } else {
                                            playerConnect.setLevel(number);
                                            plugin.xpManager.sendCommands(target, plugin.levels.get.getString(playerConnect.getGroup() + "." + number + ".execute") + ".level.down", plugin.execute.get, "", 0, 0, 0, 0);
                                        }
                                    }
                                    if (type.equalsIgnoreCase("player")) {
                                        for (final String message : plugin.language.get.getStringList("xp.set")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName()).replace("{xp}", String.valueOf(args[2])).replace("{player}", sender.getName())));
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList("console.xp.set")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName()).replace("{xp}", String.valueOf(args[2]))));
                                        }
                                    }
                                    for (final String message : plugin.language.get.getStringList("xp.target")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName()).replace("{xp}", String.valueOf(args[2]))));
                                    }
                                } else {
                                    if (type.equalsIgnoreCase("player")) {
                                        for (final String message : plugin.language.get.getStringList("xp.number")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList("console.xp.number")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                }
                            } else {
                                if (type.equalsIgnoreCase("player")) {
                                    for (final String message : plugin.language.get.getStringList("xp.online")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("console.xp.online")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            }
                        } else {
                            if (type.equalsIgnoreCase("player")) {
                                for (final String message : plugin.language.get.getStringList("xp.usage")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.xp.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("xp.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("level")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.admin.level")) {
                        if (args.length == 3) {
                            final Player target = plugin.getServer().getPlayer(args[1]);
                            if (target != null) {
                                if (plugin.isLong(args[2])) {
                                    final PlayerConnect playerConnect = plugin.getPlayerConnect(target.getUniqueId());
                                    long set = Long.parseLong(args[2]);
                                    if (args[2].contains("+")) {
                                        set = playerConnect.getLevel() + Long.parseLong(args[2].replace("+", ""));
                                    } else if (args[2].contains("-")) {
                                        set = playerConnect.getLevel() - Long.parseLong(args[2].replace("-", ""));
                                    }
                                    if (set >= plugin.config.get.getLong("start-level") && plugin.levels.get.contains(playerConnect.getGroup() + "." + set)) {
                                        playerConnect.setLevel(set - 1);
                                        plugin.xpManager.sendCommands(target, plugin.levels.get.getString(playerConnect.getGroup() + "." + set + ".execute") + ".level.up", plugin.execute.get, "", 0, 0, 0, 0);
                                        playerConnect.setLevel(set);
                                        playerConnect.setXp(plugin.levels.get.getLong(playerConnect.getGroup() + "." + set + ".xp"));
                                        if (type.equalsIgnoreCase("player")) {
                                            for (final String message : plugin.language.get.getStringList("level.set")) {
                                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName()).replace("{level}", String.valueOf(set)).replace("{player}", sender.getName())));
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList("console.level.set")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName()).replace("{level}", String.valueOf(set))));
                                            }
                                        }
                                        for (final String message : plugin.language.get.getStringList("level.target")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName()).replace("{level}", String.valueOf(set))));
                                        }
                                    } else {
                                        if (type.equalsIgnoreCase("player")) {
                                            for (final String message : plugin.language.get.getStringList("level.found")) {
                                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList("console.level.found")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    }
                                } else {
                                    if (type.equalsIgnoreCase("player")) {
                                        for (final String message : plugin.language.get.getStringList("level.number")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList("console.level.number")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                }
                            } else {
                                if (type.equalsIgnoreCase("player")) {
                                    for (final String message : plugin.language.get.getStringList("level.online")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("console.level.online")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            }
                        } else {
                            if (type.equalsIgnoreCase("player")) {
                                for (final String message : plugin.language.get.getStringList("level.usage")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.level.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("level.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("multiplier")) {
                    unknown = false;
                    if (sender.hasPermission("pvplevels.admin.multiplier")) {
                        if (args.length == 4) {
                            final Player target = plugin.getServer().getPlayer(args[1]);
                            if (target != null) {
                                if (plugin.isDouble(args[2])) {
                                    if (plugin.isInt(args[3])) {
                                        final PlayerConnect playerConnect = plugin.getPlayerConnect(target.getUniqueId());
                                        playerConnect.setMultiplier(Double.parseDouble(args[2]));
                                        playerConnect.setMultiplier_time(Integer.parseInt(args[3]));
                                        playerConnect.setMultiplier_time_left(Integer.parseInt(args[3]));
                                        plugin.multipliers.add(target);
                                        if (type.equalsIgnoreCase("player")) {
                                            for (final String message : plugin.language.get.getStringList("multiplier.got")) {
                                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName()).replace("{player}", sender.getName()).replace("{multiplier}", args[2]).replace("{time}", plugin.statsManager.time("multiplier", new GregorianCalendar(0, Calendar.JANUARY, 0, 0, 0, Integer.parseInt(args[3])).getTime().getTime()))));
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList("console.multiplier.got")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName()).replace("{multiplier}", args[2]).replace("{time}", plugin.statsManager.time("multiplier", new GregorianCalendar(0, Calendar.JANUARY, 0, 0, 0, Integer.parseInt(args[3])).getTime().getTime()))));
                                            }
                                        }
                                        for (final String message : plugin.language.get.getStringList("multiplier.target")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName()).replace("{multiplier}", args[2]).replace("{time}", plugin.statsManager.time("multiplier", new GregorianCalendar(0, Calendar.JANUARY, 0, 0, 0, Integer.parseInt(args[3])).getTime().getTime()))));
                                        }
                                    } else {
                                        if (type.equalsIgnoreCase("player")) {
                                            for (final String message : plugin.language.get.getStringList("multiplier.number")) {
                                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList("console.multiplier.number")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    }
                                } else {
                                    if (type.equalsIgnoreCase("player")) {
                                        for (final String message : plugin.language.get.getStringList("multiplier.double")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList("console.multiplier.double")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                }
                            } else {
                                if (type.equalsIgnoreCase("player")) {
                                    for (final String message : plugin.language.get.getStringList("multiplier.online")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("console.multiplier.online")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            }
                        } else {
                            if (type.equalsIgnoreCase("player")) {
                                for (final String message : plugin.language.get.getStringList("multiplier.usage")) {
                                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.multiplier.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("player")) {
                            for (final String message : plugin.language.get.getStringList("multiplier.permission")) {
                                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                            }
                        }
                    }
                }
                if (unknown) {
                    if (type.equalsIgnoreCase("player")) {
                        for (final String message : plugin.language.get.getStringList("command.unknown")) {
                            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName()).replace("{command}", args[0])));
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("console.command.unknown")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{command}", args[0])));
                        }
                    }
                }
            }
        } else {
            if (type.equalsIgnoreCase("player")) {
                for (final String message : plugin.language.get.getStringList("command.permission")) {
                    plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName())));
                }
            }
        }
        return true;
    }

    private ArrayList<Long> xpList(final PlayerConnect playerConnect) {
        final ArrayList<Long> xp = new ArrayList<>();
        for (final String level : plugin.levels.get.getConfigurationSection(playerConnect.getGroup()).getKeys(false)) {
            if (!level.equalsIgnoreCase("execute")) {
                xp.add(plugin.levels.get.getLong(playerConnect.getGroup() + "." + level + ".xp"));
            }
        }
        return xp;
    }

    private void triggerReset(final String type, final CommandSender sender, final Player target, final String path) {
        if (type.equalsIgnoreCase("player")) {
            for (final String message : plugin.language.get.getStringList("reset." + path + ".reset")) {
                plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{player}", sender.getName()).replace("{target}", target.getName())));
            }
        } else {
            for (final String message : plugin.language.get.getStringList("console.reset." + path + ".reset")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName())));
            }
        }
        for (final String message : plugin.language.get.getStringList("reset." + path + ".target")) {
            plugin.getServer().dispatchCommand(plugin.consoleSender, ChatColor.translateAlternateColorCodes('&', message.replace("{target}", target.getName())));
        }
    }

    private void broadcast(final String text, final String[] args) {
        if (args[1].equalsIgnoreCase("null")) {
            plugin.getServer().broadcastMessage(text);
        } else {
            plugin.getServer().broadcast(text, args[1]);
        }
    }
}