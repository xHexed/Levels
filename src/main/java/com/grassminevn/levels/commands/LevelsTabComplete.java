package com.grassminevn.levels.commands;

import com.grassminevn.levels.Levels;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class LevelsTabComplete implements TabCompleter {

    private final Levels plugin;

    public LevelsTabComplete(final Levels plugin) {
        this.plugin = plugin;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final List<String> commands = new ArrayList<>();
            final List<String> list = new ArrayList<>();
            if (player.hasPermission("levels.player.help")) {
                if (args.length == 1) {
                    commands.add("help");
                }
            }
            if (player.hasPermission("levels.admin.reload")) {
                if (args.length == 1) {
                    commands.add("reload");
                }
            }
            if (player.hasPermission("levels.admin.admin")) {
                if (args.length == 1) {
                    commands.add("admin");
                }
            }
            if (player.hasPermission("levels.player.profiles")) {
                if (args.length == 1) {
                    commands.add("profiles");
                }
            }
            if (player.hasPermission("levels.player.stats")) {
                if (args.length == 1) {
                    commands.add("stats");
                } else if (args.length > 1 && args[0].equalsIgnoreCase("stats")) {
                    if (args.length == 2) {
                        commands.addAll(getPlayers(args[1]));
                    }
                }
            }
            if (player.hasPermission("levels.player.top")) {
                if (args.length == 1) {
                    commands.add("top");
                } else if (args.length > 1 && args[0].equalsIgnoreCase("top")) {
                    if (args.length == 2) {
                        commands.add("xp");
                        commands.add("level");
                    }
                }
            }
            if (player.hasPermission("levels.admin.save")) {
                if (args.length == 1) {
                    commands.add("save");
                }
            }
            if (player.hasPermission("levels.admin.broadcast")) {
                if (args.length == 1) {
                    commands.add("broadcast");
                } else if (args.length > 1 && args[0].equalsIgnoreCase("broadcast")) {
                    if (args.length == 2) {
                        commands.add("null");
                    } else if (args.length == 3) {
                        commands.add("text");
                    }
                }
            }
            if (player.hasPermission("levels.admin.message")) {
                if (args.length == 1) {
                    commands.add("message");
                } else if (args.length > 1 && args[0].equalsIgnoreCase("message")) {
                    if (args.length == 2) {
                        commands.addAll(getPlayers(args[1]));
                    } else if (args.length == 3) {
                        commands.add("text");
                    }
                }
            }
            if (player.hasPermission("levels.admin.actionbar")) {
                if (args.length == 1) {
                    commands.add("actionbar");
                } else if (args.length > 1 && args[0].equalsIgnoreCase("actionbar")) {
                    if (args.length == 2) {
                        commands.addAll(getPlayers(args[1]));
                    } else if (args.length == 3) {
                        commands.add("seconds");
                    } else if (args.length == 4) {
                        commands.add("text");
                    }
                }
            }
            if (player.hasPermission("levels.admin.group")) {
                if (args.length == 1) {
                    commands.add("group");
                } else if (args.length > 1 && args[0].equalsIgnoreCase("group")){
                    if (args.length == 2) {
                        commands.add("set");
                        commands.add("reset");
                    } else if (args.length == 3) {
                        commands.addAll(getPlayers(args[2]));
                    } else if (args.length == 4) {
                        if (args[1].equalsIgnoreCase("set")) {
                            commands.addAll(plugin.levels.get.getConfigurationSection("").getKeys(false));
                        }
                    }
                }
            }
            if (player.hasPermission("levels.admin.reset")) {
                if (args.length == 1) {
                    commands.add("reset");
                } else if (args.length > 1 && args[0].equalsIgnoreCase("reset")){
                    if (args.length == 2) {
                        commands.add("level");
                        commands.add("stats");
                    } else if (args.length == 3) {
                        commands.addAll(getPlayers(args[2]));
                    } else if (args.length == 4) {
                        if (args[1].equalsIgnoreCase("stats")) {
                            commands.add("false");
                            commands.add("true");
                        }
                    }
                }
            }
            if (player.hasPermission("levels.admin.xp")) {
                if (args.length == 1) {
                    commands.add("xp");
                } else if (args.length > 1 && args[0].equalsIgnoreCase("xp")){
                    if (args.length == 2) {
                        commands.addAll(getPlayers(args[1]));
                    } else if (args.length == 3) {
                        commands.add("+-amount");
                    }
                }
            }
            if (player.hasPermission("levels.admin.level")) {
                if (args.length == 1) {
                    commands.add("level");
                } else if (args.length > 1 && args[0].equalsIgnoreCase("level")){
                    if (args.length == 2) {
                        commands.addAll(getPlayers(args[1]));
                    } else if (args.length == 3) {
                        commands.add("+-amount");
                    }
                }
            }
            if (player.hasPermission("levels.admin.multiplier")) {
                if (args.length == 1) {
                    commands.add("multiplier");
                } else if (args.length > 1 && args[0].equalsIgnoreCase("multiplier")){
                    if (args.length == 2) {
                        commands.addAll(getPlayers(args[1]));
                    } else if (args.length == 3) {
                        commands.add("1.8");
                    } else if (args.length == 4) {
                        commands.add("seconds");
                    }
                }
            }
            StringUtil.copyPartialMatches(args[args.length - 1], commands, list);
            Collections.sort(list);
            return list;
        }
        return null;
    }

    private List<String> getPlayers(final String startsWith) {
        final List<String> list = new ArrayList<>();
        for (final Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer.isOnline()) {
                final String name = onlinePlayer.getName();
                if (name.startsWith(startsWith)) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
