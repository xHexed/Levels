package com.grassminevn.levels.listeners;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import com.grassminevn.levels.gui.GUI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InventoryClick implements Listener {
    private final Levels plugin;

    public InventoryClick(final Levels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClick(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof GUI) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().getItemMeta() == null) {
                return;
            }
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                e.setCancelled(true);
            }
            if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
                e.setCancelled(true);
                return;
            }
            if (e.isShiftClick()) {
                e.setCancelled(true);
            }
            final Player player = (Player) e.getWhoClicked();
            player.updateInventory();
            for (final GUI gui : plugin.guiList.values()) {
                if (gui == e.getInventory().getHolder()) {
                    final FileConfiguration fileConfiguration = gui.fileConfiguration;
                    if (fileConfiguration.contains("settings.global-boosters")) {
                        boostersClick(player, e.getSlot(), "global-settings", "global", true);
                    } else if (fileConfiguration.contains("settings.personal-boosters")) {
                        boostersClick(player, e.getSlot(), "personal-settings", "personal", false);
                    } else if (fileConfiguration.contains("settings.profile-all")) {
                        if (e.getSlot() == fileConfiguration.getInt("settings.profile-all.next.POSITION")) {
                            plugin.guiPageID.put(player.getUniqueId().toString(), plugin.guiPageID.get(player.getUniqueId().toString()) + 1);
                            plugin.getServer().dispatchCommand(plugin.consoleCommandSender, "levels gui open profileAll.yml " + player.getName());
                        } else if (e.getSlot() == fileConfiguration.getInt("settings.profile-all.back.POSITION")) {
                            plugin.guiPageID.put(player.getUniqueId().toString(), plugin.guiPageID.get(player.getUniqueId().toString()) - 1);
                            plugin.getServer().dispatchCommand(plugin.consoleCommandSender, "levels gui open profileAll.yml " + player.getName());
                        } else {
                            if (e.isShiftClick() && e.isRightClick() && player.hasPermission("levels.gui.admin.delete")) {
                                if (e.getCurrentItem().getItemMeta().hasLore()) {
                                    for (final String lore : e.getCurrentItem().getItemMeta().getLore()) {
                                        final Matcher matcher = Pattern.compile("\\[([^]]+)]").matcher(lore);
                                        while (matcher.find()) {
                                            final OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(matcher.group(1)));
                                            if (offlinePlayer != null) {
                                                final String targetUUID = offlinePlayer.getUniqueId().toString();
                                                if (!player.getUniqueId().toString().equalsIgnoreCase(targetUUID)) {
                                                    if (offlinePlayer.isOnline()) {
                                                        final Player target = (Player) offlinePlayer;
                                                        target.kickPlayer("");
                                                    }
                                                    plugin.unload(targetUUID);
                                                    plugin.database.delete(targetUUID);
                                                    for (final String command : plugin.language.get.getStringList("player.pvpadmin.deleted-commands")) {
                                                        plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{levels_target}", offlinePlayer.getName()).replace("{levels_player}", player.getName()));
                                                    }
                                                } else {
                                                    for (final String message : plugin.language.get.getStringList("player.pvpadmin.you")) {
                                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (final String keyItem : fileConfiguration.getConfigurationSection("").getKeys(false)) {
                        if (!keyItem.equalsIgnoreCase("settings")) {
                            if (e.getSlot() == fileConfiguration.getInt(keyItem + ".POSITION")) {
                                if (fileConfiguration.contains(keyItem + ".OPTIONS")) {
                                    final String uuid = player.getUniqueId().toString();
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("SORT_KILLS")) { plugin.guiPageSort.put(uuid, "kills"); }
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("SORT_DEATHS")) { plugin.guiPageSort.put(uuid, "deaths"); }
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("SORT_XP")) { plugin.guiPageSort.put(uuid, "xp"); }
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("SORT_LEVEL")) { plugin.guiPageSort.put(uuid, "level"); }
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("SORT_KILLSTREAK")) { plugin.guiPageSort.put(uuid, "killstreak"); }
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("SORT_LASTSEEN")) { plugin.guiPageSort.put(uuid, "lastseen"); }
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("SORT_KDR")) { plugin.guiPageSort.put(uuid, "kdr"); }
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("SORT_KILLFACTOR")) { plugin.guiPageSort.put(uuid, "killfactor"); }
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("SORT_XPREQUIRED")) { plugin.guiPageSort.put(uuid, "xprequired"); }
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("SORT_REVERSE")) { plugin.guiPageSortReverse.put(uuid, !plugin.guiPageSortReverse.get(uuid)); }
                                    if (fileConfiguration.getStringList(keyItem + ".OPTIONS").contains("CLOSE")) { player.closeInventory(); }
                                }
                                if (fileConfiguration.contains(keyItem + ".COMMANDS")) {
                                    for (final String command : fileConfiguration.getStringList(keyItem + ".COMMANDS")) {
                                        plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{levels_player}", player.getName()));
                                    }
                                }
                                if (fileConfiguration.contains(keyItem + ".SHOP")) {
                                    final PlayerConnect playerConnect = plugin.get(player.getUniqueId().toString());
                                    final long back = playerConnect.coins() - fileConfiguration.getLong(keyItem + ".SHOP.COST");
                                    if (back >= 0L) {
                                        playerConnect.coins(back);
                                        for (final String command : fileConfiguration.getStringList(keyItem + ".SHOP.COMMANDS")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{levels_player}", player.getName()));
                                        }
                                    } else {
                                        for (final String message : fileConfiguration.getStringList(keyItem + ".SHOP.ENOUGH")) {
                                            plugin.getServer().dispatchCommand(plugin.consoleCommandSender, message.replace("{levels_player}", player.getName()));
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void boostersClick(final Player player, final int slot, final String path, final String key, final boolean global) {
        final List<String> list = plugin.boosters.get.getStringList("players." + player.getUniqueId() + "." + key);
        for (final String line : list) {
            if (line.split(" ").length == 3) {
                if (slot == Integer.parseInt(line.split(" ")[2])) {
                    if (global) {
                        global(player, line, list);
                    } else {
                        personal(player, line, list);
                    }
                    if (plugin.boosters.get.contains(path + ".gui." + line.split(" ")[0] + ".OPTIONS") && plugin.boosters.get.getStringList(path + ".gui." + line.split(" ")[0] + ".OPTIONS").contains("CLOSE")) { player.closeInventory(); }
                    if (plugin.boosters.get.contains(path + ".gui.none.OPTIONS") && plugin.boosters.get.getStringList(path + ".gui.none.OPTIONS").contains("CLOSE")) { player.closeInventory(); }
                    break;
                }
            }
        }
    }


    private void global(final Player player, final String line, final Collection<String> list) {
        final String uuid = player.getUniqueId().toString();
        final int maxQueue = plugin.boostersManager.isInQueueSize(uuid);
        if (maxQueue < plugin.boosters.get.getInt("global-settings.max-queue")) {
            final List<String> queue = plugin.boosters.get.getStringList("global-queue");
            queue.add(uuid + " " + line.split(" ")[0] + " " + line.split(" ")[1]);
            plugin.boosters.get.set("global-queue", queue);
            for (final String command : plugin.boosters.get.getStringList("global-settings.queue.add")) {
                plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command
                        .replace("{levels_player}", player.getName())
                        .replace("{levels_booster_global_type}", String.valueOf(line.split(" ")[0]))
                        .replace("{levels_booster_global_time}", plugin.boostersManager.timeLeft(Integer.parseInt(line.split(" ")[1])))
                        .replace("{levels_booster_global_queue}", String.valueOf(plugin.boostersManager.queueNumber(uuid))));
            }
            list.remove(line);
            plugin.boosters.get.set("players." + uuid + ".global", list);
            plugin.boosters.save();
        } else {
            for (final String command : plugin.boosters.get.getStringList("global-settings.queue.max")) {
                plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command
                        .replace("{levels_player}", player.getName())
                        .replace("{levels_booster_global_max}", String.valueOf(maxQueue)));
            }
        }
    }

    private void personal(final Player player, final String line, final Collection<String> list) {
        final String uuid = player.getUniqueId().toString();
        final PlayerConnect playerConnect = plugin.get(uuid);
        if (playerConnect.getPersonalBooster() == null && !plugin.boosters.get.contains("players." + uuid + ".personal-active")) {
            playerConnect.timer(Integer.parseInt(line.split(" ")[1]), Double.valueOf(line.split(" ")[0]));
            plugin.boosters.get.set("players." + uuid + ".personal-active", line.split(" ")[0] + " " + line.split(" ")[1]);
            for (final String command : plugin.boosters.get.getStringList("personal-settings.start")) {
                plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command
                        .replace("{levels_player}", player.getName())
                        .replace("{levels_booster_personal_type}", String.valueOf(line.split(" ")[0]))
                        .replace("{levels_booster_personal_time}", plugin.boostersManager.timeLeft(Integer.parseInt(line.split(" ")[1]))));
            }
            list.remove(line);
            plugin.boosters.get.set("players." + uuid + ".personal", list);
            plugin.boosters.save();
        } else {
            for (final String command : plugin.boosters.get.getStringList("personal-settings.active")) {
                plugin.getServer().dispatchCommand(plugin.consoleCommandSender, command.replace("{levels_player}", player.getName()));
            }
        }
    }
}