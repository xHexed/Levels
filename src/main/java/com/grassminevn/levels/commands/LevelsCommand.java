package com.grassminevn.levels.commands;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import com.grassminevn.levels.gui.GUI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LevelsCommand implements CommandExecutor {
    private final Levels plugin;

    public LevelsCommand(final Levels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if (sender.hasPermission("levels.command")) {
            boolean unknown = true;
            final String path;
            if (sender instanceof Player) {
                path = "player";
            } else {
                path = "console";
            }
            if (args.length == 0) {
                for (final String message : plugin.language.get.getStringList(path + ".levels.command.message")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_version}", Levels.call.getDescription().getVersion())));
                }
            } else {
                if (args[0].equalsIgnoreCase("help")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.help")) {
                        for (final String message : plugin.language.get.getStringList(path + ".levels.help.message")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.help.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.reload")) {
                        plugin.config.load();
                        plugin.language.load();
                        plugin.levels.load();
                        plugin.boosters.load();
                        plugin.guiFolder.load();
                        for (final String message : plugin.language.get.getStringList(path + ".levels.reload.reloaded")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.reload.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("wand")) {
                    unknown = false;
                    if (path.equalsIgnoreCase("player")) {
                        if (sender.hasPermission("levels.command.wand")) {
                            final InventoryHolder player = (InventoryHolder) sender;
                            player.getInventory().addItem(plugin.wand);
                            for (final String message : plugin.language.get.getStringList("player.levels.wand.add")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList("player.levels.wand.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("console.levels.wand")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("zone")) {
                    unknown = false;
                    if (path.equalsIgnoreCase("player")) {
                        if (sender.hasPermission("levels.command.zone")) {
                            if (args.length > 1) {
                                if (args[1].equalsIgnoreCase("set")) {
                                    if (sender.hasPermission("levels.command.zone.set")) {
                                        if (args.length == 4) {
                                            final Entity player = (Entity) sender;
                                            final String uuid = player.getUniqueId().toString();
                                            if (plugin.config.get.contains("xp." + args[2])) {
                                                if (!plugin.zones.get.contains("zones." + args[2] + "." + args[3])) {
                                                    if (plugin.wandPos1.containsKey(uuid) && plugin.wandPos2.containsKey(uuid)) {
                                                        final Location loc1 = plugin.wandPos1.get(uuid);
                                                        final Location loc2 = plugin.wandPos2.get(uuid);
                                                        plugin.zones.get.set("zones." + args[2] + "." + args[3] + ".start", loc1.getBlockX() + " " + loc1.getBlockY() + " " + loc1.getBlockZ());
                                                        plugin.zones.get.set("zones." + args[2] + "." + args[3] + ".end", loc2.getBlockX() + " " + loc2.getBlockY() + " " + loc2.getBlockZ());
                                                        plugin.zones.get.set("zones." + args[2] + "." + args[3] + ".world", player.getWorld().getName());
                                                        plugin.zones.save();
                                                        for (final String message : plugin.language.get.getStringList("player.levels.zone.set.set")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_zone}", args[3]).replace("{levels_type}", args[2])));
                                                        }
                                                    } else {
                                                        for (final String message : plugin.language.get.getStringList("player.levels.zone.set.select")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList("player.levels.zone.set.name")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList("player.levels.zone.set.type")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList("player.levels.zone.set.usage")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList("player.levels.zone.set.permission")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("player.levels.zone.usage")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("player.levels.zone.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList("player.levels.zone.permission")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("console.levels.zone")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("broadcast")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.broadcast")) {
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
                            for (final String message : plugin.language.get.getStringList(path + ".levels.broadcast.usage")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.broadcast.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("message")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.message")) {
                        if (args.length <= 2) {
                            for (final String message : plugin.language.get.getStringList(path + ".levels.message.usage")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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
                                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(target, text)));
                                } else {
                                    for (final String message : text.split(Pattern.quote("\\n"))) {
                                        target.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(target, message)));
                                    }
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList(path + ".levels.message.online")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.message.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("actionbar")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.actionbar")) {
                        if (args.length <= 2) {
                            for (final String message : plugin.language.get.getStringList(path + ".levels.actionbar.usage")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        } else {
                            final Player target = plugin.getServer().getPlayer(args[1]);
                            if (target != null) {
                                final StringBuilder sb = new StringBuilder();
                                for (int i = 2; i < args.length; i++) {
                                    sb.append(args[i]).append(" ");
                                }
                                try {
                                    target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(target, sb.toString().trim()))));
                                } catch (final NoSuchMethodError exception) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&blevels&7] &cThis command is not supported in this spigot version"));
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList(path + ".levels.actionbar.online")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.actionbar.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("save")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.save")) {
                        for (final String uuid : plugin.list()) {
                            plugin.get(uuid).save();
                        }
                        for (final String message : plugin.language.get.getStringList(path + ".levels.save.saved")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.save.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("item")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.item")) {
                        if (args.length <= 1) {
                            for (final String message : plugin.language.get.getStringList(path + ".levels.item.usage")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        } else {
                            if (args[1].equalsIgnoreCase("add")) {
                                if (sender.hasPermission("levels.command.item.add")) {
                                    if (args.length >= 5) {
                                        final Player target = plugin.getServer().getPlayer(args[2]);
                                        if (target != null) {
                                            if (plugin.isInt(args[4])) {
                                                if (Integer.parseInt(args[4]) > 0) {
                                                    final ItemStack itemStack = plugin.getID(args[3].toUpperCase(), Integer.parseInt(args[4]));
                                                    if (itemStack != null) {
                                                        if (args.length > 5) {
                                                            final StringBuilder sb = new StringBuilder();
                                                            for (int i = 5; i < args.length; i++) {
                                                                sb.append(args[i]).append(" ");
                                                            }
                                                            final String text = sb.toString().trim();
                                                            final ItemMeta itemMeta = itemStack.getItemMeta();
                                                            if (text.contains("\\n")) {
                                                                final String[] split = text.split(Pattern.quote("\\n"));
                                                                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(target, split[0])));
                                                                if (split.length > 1) {
                                                                    final List<String> lores = new ArrayList<>();
                                                                    for (int i = 1; i < split.length; i++) {
                                                                        lores.add(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(target, split[i])));
                                                                    }
                                                                    itemMeta.setLore(lores);
                                                                }
                                                            } else {
                                                                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(target, text)));
                                                            }
                                                            itemStack.setItemMeta(itemMeta);
                                                        }
                                                        target.getInventory().addItem(itemStack);
                                                        if (path.equalsIgnoreCase("player")) {
                                                            for (final String message : plugin.language.get.getStringList(path + ".levels.item.add.added")) {
                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_player}", target.getName())));
                                                            }
                                                        } else {
                                                            for (final String message : plugin.language.get.getStringList(path + ".levels.item.add.added")) {
                                                                target.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_player}", target.getName())));
                                                            }
                                                        }
                                                    } else {
                                                        for (final String message : plugin.language.get.getStringList(path + ".levels.item.found")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList(path + ".levels.item.0")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList(path + ".levels.item.number")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList(path + ".levels.item.online")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList(path + ".levels.item.add.usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("player.levels.item.add.permission")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else if (args[1].equalsIgnoreCase("set")) {
                                if (sender.hasPermission("levels.command.item.set")) {
                                    if (args.length >= 7) {
                                        if (plugin.isInt(args[2]) && Integer.parseInt(args[2]) >= 0) {
                                            if (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")) {
                                                final Player target = plugin.getServer().getPlayer(args[4]);
                                                if (target != null) {
                                                    if (plugin.isInt(args[6]) && Integer.parseInt(args[6]) > 0) {
                                                        final ItemStack itemStack = plugin.getID(args[5].toUpperCase(), Integer.parseInt(args[6]));
                                                        if (itemStack != null) {
                                                            if (args.length > 7) {
                                                                final StringBuilder sb = new StringBuilder();
                                                                for (int i = 7; i < args.length; i++) {
                                                                    sb.append(args[i]).append(" ");
                                                                }
                                                                final String text = sb.toString().trim();
                                                                final ItemMeta itemMeta = itemStack.getItemMeta();
                                                                if (text.contains("\\n")) {
                                                                    final String[] split = text.split(Pattern.quote("\\n"));
                                                                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(target, split[0])));
                                                                    if (split.length > 1) {
                                                                        final List<String> lores = new ArrayList<>();
                                                                        for (int i = 1; i < split.length; i++) {
                                                                            lores.add(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(target, split[i])));
                                                                        }
                                                                        itemMeta.setLore(lores);
                                                                    }
                                                                } else {
                                                                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.PlaceholderReplace(target, text)));
                                                                }
                                                                itemStack.setItemMeta(itemMeta);
                                                            }
                                                            final Inventory inventory = target.getInventory();
                                                            if (args[3].equalsIgnoreCase("true")) {
                                                                inventory.setItem(Integer.parseInt(args[2]), itemStack);
                                                            } else if (inventory.getItem(Integer.parseInt(args[2])) == null) {
                                                                inventory.setItem(Integer.parseInt(args[2]), itemStack);
                                                            }
                                                            if (path.equalsIgnoreCase("player")) {
                                                                for (final String message : plugin.language.get.getStringList(path + ".levels.item.set.set")) {
                                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_player}", target.getName()).replace("{levels_slot}", args[2])));
                                                                }
                                                            } else {
                                                                for (final String message : plugin.language.get.getStringList(path + ".levels.item.set.set")) {
                                                                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_player}", target.getName()).replace("{levels_slot}", args[2])));
                                                                }
                                                            }
                                                        } else {
                                                            for (final String message : plugin.language.get.getStringList(path + ".levels.item.found")) {
                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                            }
                                                        }
                                                    } else {
                                                        for (final String message : plugin.language.get.getStringList(path + ".levels.item.0")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList(path + ".levels.item.online")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList(path + ".levels.item.set.boolean")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList(path + ".levels.item.number")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList(path + ".levels.item.set.usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("player.levels.item.set.permission")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList(path + ".levels.item.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.item.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command." + args[0])) {
                        if (args.length == 4) {
                            final Player target = plugin.getServer().getPlayer(args[2]);
                            if (target != null) {
                                if (plugin.isInt(args[3]) && !args[3].contains("-")) {
                                    if (args[1].equalsIgnoreCase("xp")) {
                                        if (args[0].equalsIgnoreCase("set")) {
                                            setValue(sender, target, "xp", Long.valueOf(args[3]), plugin.get(target.getUniqueId().toString()), args, "set", path);
                                        } else if (args[0].equalsIgnoreCase("add")) {
                                            final PlayerConnect playerConnect = plugin.get(target.getUniqueId().toString());
                                            setValue(sender, target, "xp", playerConnect.xp() + Long.parseLong(args[3]), playerConnect, args, "add", path);
                                        } else if (args[0].equalsIgnoreCase("remove")) {
                                            final PlayerConnect playerConnect = plugin.get(target.getUniqueId().toString());
                                            setValue(sender, target, "xp", playerConnect.xp() - Long.parseLong(args[3]), playerConnect, args, "remove", path);
                                        }
                                    } else if (args[1].equalsIgnoreCase("level")) {
                                        if (args[0].equalsIgnoreCase("set")) {
                                            setValue(sender, target, "level", Long.valueOf(args[3]), plugin.get(target.getUniqueId().toString()), args, "set", path);
                                        } else if (args[0].equalsIgnoreCase("add")) {
                                            final PlayerConnect playerConnect = plugin.get(target.getUniqueId().toString());
                                            setValue(sender, target, "level", playerConnect.level() + Long.parseLong(args[3]), playerConnect, args, "add", path);
                                        } else if (args[0].equalsIgnoreCase("remove")) {
                                            final PlayerConnect playerConnect = plugin.get(target.getUniqueId().toString());
                                            setValue(sender, target, "level", playerConnect.level() - Long.parseLong(args[3]), playerConnect, args, "remove", path);
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList(path + ".levels." + args[0] + ".usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList(path + ".levels." + args[0] + ".number")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList(path + ".levels." + args[0] + ".online")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList(path + ".levels." + args[0] + ".usage")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels." + args[0] + ".permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("generate")) {
                    if (path.equalsIgnoreCase("console")) {
                        unknown = false;
                        if (!plugin.config.get.getBoolean("generate.disable")) {
                            if (args.length == 2) {
                                if (plugin.isInt(args[1]) && !args[1].contains("-")) {
                                    final ArrayList<Integer> xpList = new ArrayList<>();
                                    xpList.add(plugin.config.get.getInt("generate.xp.static") + plugin.random(plugin.config.get.getInt("generate.xp.min"), plugin.config.get.getInt("generate.xp.max")));
                                    for (int i = 0; i < Integer.parseInt(args[1]) - 1; i++) {
                                        xpList.add(xpList.get(i) + (plugin.config.get.getInt("generate.xp.static") + plugin.random(plugin.config.get.getInt("generate.xp.min"), plugin.config.get.getInt("generate.xp.max"))));
                                    }
                                    for (int i = 1; i <= xpList.size(); i++) {
                                        final List<String> list = plugin.config.get.getStringList("generate.commands");
                                        if (plugin.config.get.getBoolean("generate.random.use")) {
                                            if (plugin.levels.get.contains("levels." + plugin.random(plugin.config.get.getInt("generate.random.min"), plugin.config.get.getInt("generate.random.max")))) {
                                                list.add(plugin.config.get.getStringList("generate.random.commands").get(plugin.random(0, plugin.config.get.getStringList("generate.random.commands").size() - 1)));
                                            }
                                        }
                                        plugin.levels.get.set("levels." + i + ".commands", list);
                                        plugin.levels.get.set("levels." + i + ".xp", xpList.get(i - 1));
                                    }
                                    plugin.levels.save();
                                    for (final String message : plugin.language.get.getStringList("console.levels.generate.message")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_levels}", String.valueOf(xpList.size()))));
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("console.levels.generate.number")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList("console.levels.generate.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList("console.levels.generate.disable")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("boosters")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.boosters")) {
                        if (args.length >= 2) {
                            if (args[1].equalsIgnoreCase("give")) {
                                if (sender.hasPermission("levels.command.boosters.give")) {
                                    if (args.length == 6) {
                                        final Player target = plugin.getServer().getPlayer(args[2]);
                                        if (target != null) {
                                            if (args[3].equalsIgnoreCase("global")) {
                                                if (plugin.isDouble(args[4])) {
                                                    if (plugin.isInt(args[5])) {
                                                        final List<String> list = plugin.boosters.get.getStringList("players." + target.getUniqueId() + ".global");
                                                        list.add(args[4] + " " + args[5]);
                                                        plugin.boosters.get.set("players." + target.getUniqueId() + ".global", list);
                                                        plugin.boosters.save();
                                                        if (path.equalsIgnoreCase("player")) {
                                                            for (final String message : plugin.language.get.getStringList("player.levels.boosters.give.message")) {
                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_player_target}", target.getName()).replace("{levels_booster_global_type}", args[4]).replace("{levels_booster_global_time}", plugin.boostersManager.timeLeft(Integer.parseInt(args[5])))));
                                                            }
                                                            if (!target.getName().equals(sender.getName())) {
                                                                for (final String message : plugin.language.get.getStringList("player.levels.boosters.give.target")) {
                                                                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_booster_global_type}", args[4]).replace("{levels_booster_global_time}", plugin.boostersManager.timeLeft(Integer.parseInt(args[5])))));
                                                                }
                                                            }
                                                        } else {
                                                            for (final String message : plugin.language.get.getStringList("console.levels.boosters.give.target")) {
                                                                target.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_booster_global_type}", args[4]).replace("{levels_booster_global_time}", plugin.boostersManager.timeLeft(Integer.parseInt(args[5])))));
                                                            }
                                                        }
                                                    } else {
                                                        for (final String message : plugin.language.get.getStringList(path + ".levels.boosters.give.number")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList(path + ".levels.boosters.give.booster")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else if (args[3].equalsIgnoreCase("personal")) {
                                                if (plugin.isDouble(args[4])) {
                                                    if (plugin.isInt(args[5])) {
                                                        final List<String> list = plugin.boosters.get.getStringList("players." + target.getUniqueId() + ".personal");
                                                        list.add(args[4] + " " + args[5]);
                                                        plugin.boosters.get.set("players." + target.getUniqueId() + ".personal", list);
                                                        plugin.boosters.save();
                                                        if (path.equalsIgnoreCase("player")) {
                                                            for (final String message : plugin.language.get.getStringList("player.levels.boosters.personal.give.message")) {
                                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_player_target}", target.getName()).replace("{levels_booster_personal_type}", args[4]).replace("{levels_booster_personal_time}", plugin.boostersManager.timeLeft(Integer.parseInt(args[5])))));
                                                            }
                                                            if (!target.getName().equals(sender.getName())) {
                                                                for (final String message : plugin.language.get.getStringList("player.levels.boosters.personal.give.target")) {
                                                                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_booster_personal_type}", args[4]).replace("{levels_booster_personal_time}", plugin.boostersManager.timeLeft(Integer.parseInt(args[5])))));
                                                                }
                                                            }
                                                        } else {
                                                            for (final String message : plugin.language.get.getStringList("console.levels.boosters.personal.give.target")) {
                                                                target.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_booster_personal_type}", args[4]).replace("{levels_booster_personal_time}", plugin.boostersManager.timeLeft(Integer.parseInt(args[5])))));
                                                            }
                                                        }
                                                    } else {
                                                        for (final String message : plugin.language.get.getStringList(path + ".levels.boosters.personal.give.number")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                        }
                                                    }
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList(path + ".levels.boosters.personal.give.booster")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList(path + ".levels.boosters.give.usage")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList(path + ".levels.boosters.give.online")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList(path + ".levels.boosters.give.usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("player.levels.boosters.give.permission")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList(path + ".levels.boosters.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList(path + ".levels.boosters.usage")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.boosters.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("gui")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.gui")) {
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("open")) {
                                if (args.length > 2) {
                                    if (sender.hasPermission("levels.command.gui.open." + args[2])) {
                                        if (args.length == 3) {
                                            if (path.equalsIgnoreCase("player")) {
                                                final Player player = (Player) sender;
                                                final GUI gui = plugin.guiList.get(args[2]);
                                                if (gui != null) {
                                                    gui.open(player);
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList("player.levels.gui.open.found")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_gui_file}", args[2])));
                                                    }
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList("console.levels.gui.open.usage")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else if (args.length == 4) {
                                            final Player target = plugin.getServer().getPlayer(args[3]);
                                            if (target != null) {
                                                final GUI gui = plugin.guiList.get(args[2]);
                                                if (gui != null) {
                                                    gui.open(target);
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList(path + ".levels.gui.open.found")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_gui_file}", args[2])));
                                                    }
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList(path + ".levels.gui.open.online")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList(path + ".levels.gui.open.usage")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList("player.levels.gui.open.permission")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList(path + ".levels.gui.open.usage")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList(path + ".levels.gui.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList(path + ".levels.gui.usage")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.gui.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("player")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.player")) {
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("get")) {
                                playerCheckXP(sender, args, "get", path);
                            } else if (args[1].equalsIgnoreCase("lose")) {
                                playerCheckXP(sender, args, "lose", path);
                            } else {
                                for (final String message : plugin.language.get.getStringList(path + ".levels.player.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList(path + ".levels.player.usage")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.player.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("coins")) {
                    unknown = false;
                    if (sender.hasPermission("levels.command.coins")) {
                        if (args.length > 2) {
                            if (args[1].equalsIgnoreCase("set")) {
                                if (sender.hasPermission("levels.command.coins.set")) {
                                    if (args.length == 4) {
                                        final Player target = plugin.getServer().getPlayer(args[2]);
                                        if (target != null) {
                                            if (plugin.isInt(args[3])) {
                                                final long set = Long.parseLong(args[3]);
                                                if (set >= 0) {
                                                    final PlayerConnect playerConnect = plugin.get(target.getUniqueId().toString());
                                                    playerConnect.coins(set);
                                                    if (sender instanceof Player) {
                                                        for (final String message : plugin.language.get.getStringList(path + ".levels.coins.set.set")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_coins_amount}", String.valueOf(set)).replace("{levels_player}", target.getName())));
                                                        }
                                                    } else {
                                                        for (final String message : plugin.language.get.getStringList(path + ".levels.coins.set")) {
                                                            target.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_coins_amount}", String.valueOf(set))));
                                                        }
                                                    }
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList(path + ".levels.coins.0")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList(path + ".levels.coins.number")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList(path + ".levels.coins.online")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList(path + ".levels.coins.usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("player.levels.coins.set.permission")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else if (args[1].equalsIgnoreCase("add")) {
                                if (sender.hasPermission("levels.command.coins.add")) {
                                    if (args.length == 4) {
                                        final Player target = plugin.getServer().getPlayer(args[2]);
                                        if (target != null) {
                                            if (plugin.isInt(args[3])) {
                                                final PlayerConnect playerConnect = plugin.get(target.getUniqueId().toString());
                                                final long set = playerConnect.coins() + Long.parseLong(args[3]);
                                                if (set >= 0) {
                                                    playerConnect.coins(set);
                                                    if (sender instanceof Player) {
                                                        for (final String message : plugin.language.get.getStringList(path + ".levels.coins.add.added")) {
                                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_coins_amount}", args[3]).replace("{levels_player}", target.getName())));
                                                        }
                                                    } else {
                                                        for (final String message : plugin.language.get.getStringList(path + ".levels.coins.added")) {
                                                            target.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_coins_amount}", args[3])));
                                                        }
                                                    }
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList(path + ".levels.coins.0")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList(path + ".levels.coins.number")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList(path + ".levels.coins.online")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList(path + ".levels.coins.usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("player.levels.coins.add.permission")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else if (args[1].equalsIgnoreCase("remove")) {
                                if (sender.hasPermission("levels.command.coins.remove")) {
                                    if (args.length == 4) {
                                        final Player target = plugin.getServer().getPlayer(args[2]);
                                        if (target != null) {
                                            if (plugin.isInt(args[3])) {
                                                final PlayerConnect playerConnect = plugin.get(target.getUniqueId().toString());
                                                final long set = playerConnect.coins() - Long.parseLong(args[3]);
                                                if (set >= 0) {
                                                    playerConnect.coins(set);
                                                } else {
                                                    playerConnect.coins(0L);
                                                }
                                                if (sender instanceof Player) {
                                                    for (final String message : plugin.language.get.getStringList(path + ".levels.coins.remove.removed")) {
                                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_coins_amount}", args[3]).replace("{levels_player}", target.getName())));
                                                    }
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList(path + ".levels.coins.removed")) {
                                                        target.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_coins_amount}", args[3])));
                                                    }
                                                }
                                            } else {
                                                for (final String message : plugin.language.get.getStringList(path + ".levels.coins.number")) {
                                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                }
                                            }
                                        } else {
                                            for (final String message : plugin.language.get.getStringList(path + ".levels.coins.online")) {
                                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            }
                                        }
                                    } else {
                                        for (final String message : plugin.language.get.getStringList(path + ".levels.coins.usage")) {
                                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        }
                                    }
                                } else {
                                    for (final String message : plugin.language.get.getStringList("player.levels.coins.remove.permission")) {
                                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                            } else {
                                for (final String message : plugin.language.get.getStringList(path + ".levels.coins.usage")) {
                                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                }
                            }
                        } else {
                            for (final String message : plugin.language.get.getStringList(path + ".levels.coins.usage")) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                            }
                        }
                    } else {
                        for (final String message : plugin.language.get.getStringList("player.levels.coins.permission")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                }
                if (unknown) {
                    for (final String message : plugin.language.get.getStringList(path + ".levels.command.unknown")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_command}", args[0])));
                    }
                }
            }
        } else {
            for (final String message : plugin.language.get.getStringList("player.levels.command.permission")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
        return true;
    }

    private void playerCheckXP(final CommandSender sender, final String[] args, final String type, final String path) {
        if (sender.hasPermission("levels.command." + type)) {
            if (args.length == 4) {
                if (plugin.config.get.contains("xp." + args[2])) {
                    final Player target = plugin.getServer().getPlayer(args[3]);
                    if (target != null) {
                        if (type.equalsIgnoreCase("get")) {
                            plugin.xpManager.check(plugin.get(target.getUniqueId().toString()), args[2], null, target, true);
                            return;
                        }
                        plugin.xpManager.check(plugin.get(target.getUniqueId().toString()), args[2], null, target, false);
                    } else {
                        for (final String message : plugin.language.get.getStringList(path + ".levels.player." + type + ".online")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                        }
                    }
                } else {
                    for (final String message : plugin.language.get.getStringList(path + ".levels.player." + type + ".config")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{levels_xp_type}", args[2])));
                    }
                }
            } else {
                for (final String message : plugin.language.get.getStringList(path + ".levels.player." + type + ".usage")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        } else {
            for (final String message : plugin.language.get.getStringList("player.levels.player." + type + ".permission")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    private void setValue(final CommandSender sender, final Player target, final String colum, final Long set, final PlayerConnect playerConnect, final String[] args, final String type, final String path) {
        boolean xpAddCheck = true;
        boolean xpRemoveCheck = true;
        boolean xpSetCheck = true;
        boolean levelCheck = true;
        if (colum.equalsIgnoreCase("xp")) {
            if (type.equalsIgnoreCase("add")) {
                final ArrayList<Long> xp = xpControl();
                if (set <= xp.get(xp.size() - 1)) {
                    if (!plugin.xpManager.isMaxLevel(target, playerConnect)) {
                        playerConnect.xp(set);
                        plugin.xpManager.getLevel(playerConnect, target);
                    } else {
                        xpAddCheck = false;
                    }
                } else {
                    xpAddCheck = false;
                }
            } else if (type.equalsIgnoreCase("remove")) {
                if (set >= 0) {
                    playerConnect.xp(set);
                    if (!plugin.config.get.getBoolean("levelup.xp-clear") && playerConnect.xp() < plugin.levels.get.getLong("levels." + playerConnect.level() + ".xp")) {
                        plugin.xpManager.loseLevel(playerConnect, playerConnect.level() - 1, target, null);
                    }
                } else {
                    final long lowerLevel = playerConnect.level() - 1;
                    if (plugin.levels.get.contains("levels." + lowerLevel + ".xp")) {
                        final long all = plugin.levels.get.getLong("levels." + playerConnect.level() + ".xp") - Long.parseLong(args[3]);
                        if (all >= 0) {
                            playerConnect.xp(all);
                        } else {
                            playerConnect.xp(0L);
                        }
                        plugin.xpManager.loseLevel(playerConnect, lowerLevel, target, null);
                    } else {
                        xpRemoveCheck = false;
                    }
                }
            } else {
                final ArrayList<Long> xp = xpControl();
                if (set <= xp.get(xp.size() - 1)) {
                    if (!plugin.xpManager.isMaxLevel(target, playerConnect)) {
                        playerConnect.xp(set);
                        plugin.xpManager.getLevel(playerConnect, target);
                    } else {
                        xpSetCheck = false;
                    }
                } else {
                    xpSetCheck = false;
                }
            }
        } else if (colum.equalsIgnoreCase("level")) {
            if (set > 0L) {
                if (plugin.levels.get.contains("levels." + set)) {
                    playerConnect.level(set);
                    if (plugin.config.get.getBoolean("levelup.xp-clear")) {
                        playerConnect.xp(0L);
                    } else {
                        playerConnect.xp(plugin.levels.get.getLong("levels." + set + ".xp"));
                    }
                } else {
                    levelCheck = false;
                }
            } else if (set == 0) {
                playerConnect.level(0L);
                playerConnect.xp(0L);
            }
        }
        if (xpAddCheck && xpRemoveCheck && xpSetCheck && levelCheck) {
            if (sender instanceof Player) {
                for (final String message : plugin.language.get.getStringList(path + ".levels." + args[0] + "." + colum))
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message
                            .replace("{levels_player}", args[2]).replace("{levels_" + args[0] + "}", args[3])));
            } else {
                for (final String message : plugin.language.get.getStringList(path + ".levels." + args[0] + "." + colum))
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', message
                            .replace("{levels_player}", args[2]).replace("{levels_" + args[0] + "}", args[3])));
            }
        } else if (!xpAddCheck) {
            for (final String message : plugin.language.get.getStringList(path + ".levels.set.xp-add-cannot")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        } else if (!xpRemoveCheck) {
            for (final String message : plugin.language.get.getStringList(path + ".levels.set.xp-remove-cannot")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        } else if (!xpSetCheck) {
            for (final String message : plugin.language.get.getStringList(path + ".levels.set.xp-set-cannot")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        } else {
            for (final String message : plugin.language.get.getStringList(path + ".levels.set.level-cannot")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    private ArrayList<Long> xpControl() {
        final ArrayList<Long> xp = new ArrayList<>();
        for (final String level : plugin.levels.get.getConfigurationSection("levels").getKeys(false)) {
            xp.add(plugin.levels.get.getLong("levels." + level + ".xp"));
        }
        return xp;
    }

    private void broadcast(final String text, final String[] args) {
        if (args[1].equalsIgnoreCase("null")) {
            plugin.getServer().broadcastMessage(text);
        } else {
            plugin.getServer().broadcast(text, args[1]);
        }
    }
}