package com.grassminevn.levels.placeholders;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import com.grassminevn.levels.data.database.PlayerDatabase;
import com.grassminevn.levels.util.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PlaceholderAPI extends PlaceholderExpansion {
    private final HashMap<String, PlayerIdentifierHandler> playerIdentifiers = new HashMap<>();
    private final HashMap<String, IdentifierHandler> identifiers = new HashMap<>();
    private final HashMap<String, IdentifierHandler> topIdentifiers = new HashMap<>();

    private final Levels plugin;
    private final Updater updater;

    public PlaceholderAPI(final Levels plugin) {
        this.plugin = plugin;
        updater = new Updater();

        playerIdentifiers.put("xp", (player, params) -> String.valueOf(player.getXP()));
        playerIdentifiers.put("level", (player, params) -> String.valueOf(player.getLevel()));
        playerIdentifiers.put("rating", (player, params) -> Utils.DOUBLE_FORMAT.format(player.getRating().getMean()));
        playerIdentifiers.put("level_next", (player, params) -> String.valueOf(player.getLevel() + 1));
        playerIdentifiers.put("xp_need", (player, params) -> String.valueOf(plugin.statsManager.xp_need(player)));
        playerIdentifiers.put("xp_required", (player, params) -> String.valueOf(plugin.statsManager.xp_required(player, false)));
        playerIdentifiers.put("xp_required_next", (player, params) -> String.valueOf(plugin.statsManager.xp_required(player, true)));
        playerIdentifiers.put("xp_progress", (player, params) -> String.valueOf(plugin.statsManager.xp_progress(player)));
        playerIdentifiers.put("xp_progress_style", (player, params) -> String.valueOf(plugin.statsManager.xp_progress_style(player, "xp-progress-style")));
        playerIdentifiers.put("xp_progress_style_2", (player, params) -> String.valueOf(plugin.statsManager.xp_progress_style(player, "xp-progress-style-2")));
        playerIdentifiers.put("time", (player, params) -> String.valueOf(plugin.statsManager.time("time", player.getTime())));
        playerIdentifiers.put("date", (player, params) -> String.valueOf(plugin.statsManager.time("date", player.getTime())));
        playerIdentifiers.put("group", (player, params) -> player.getGroup());
        playerIdentifiers.put("level_group", (player, params) -> plugin.statsManager.group(player));
        playerIdentifiers.put("level_prefix", (player, params) -> plugin.statsManager.prefix(player));
        playerIdentifiers.put("level_suffix", (player, params) -> plugin.statsManager.suffix(player));
        playerIdentifiers.put("multiplier", (player, params) -> player.getMultiplier() != 0D ? String.valueOf(player.getMultiplier()) : "1");
        playerIdentifiers.put("multiplier_time", (player, params) -> player.getMultiplierTime() != 0D ? plugin.statsManager.time("multiplier", new GregorianCalendar(0, Calendar.JANUARY, 0, 0, 0, player.getMultiplierTime()).getTime()) : "0");
        playerIdentifiers.put("multiplier_time_left", (player, params) -> player.getMultiplierTime() != 0D ? plugin.statsManager.time("multiplier", new GregorianCalendar(0, Calendar.JANUARY, 0, 0, 0, player.getMultiplierTimeLeft()).getTime()) : "0");

        topIdentifiers.put("level", params -> {
            final int pos = Integer.parseInt(params[1]);
            if (params.length > 3 && params[3].equals("name")) {
                if (updater.topLevelStorage.topUUIDPlayers.containsKey(pos)) {
                    return Bukkit.getOfflinePlayer(updater.topLevelStorage.topUUIDPlayers.get(pos)).getName();
                }
                else {
                    updater.topLevelStorage.topPlayerPosUUIDLookups.add(pos);
                }
            }
            else {
                if (updater.topLevelStorage.topPlayers.containsKey(pos)) {
                    return String.valueOf(updater.topLevelStorage.topPlayers.get(pos));
                } else {
                    updater.topLevelStorage.topPlayerPosLookups.add(pos);
                }
            }
            return "???";
        });
        topIdentifiers.put("rating", params -> {
            final int pos = Integer.parseInt(params[1]);
            if (params.length > 3 && params[3].equals("name")) {
                if (updater.topRatingStorage.topUUIDPlayers.containsKey(pos)) {
                    return Bukkit.getOfflinePlayer(updater.topRatingStorage.topUUIDPlayers.get(pos)).getName();
                }
                else {
                    updater.topRatingStorage.topPlayerPosUUIDLookups.add(pos);
                }
            }
            else {
                if (updater.topRatingStorage.topPlayers.containsKey(pos)) {
                    return Utils.DOUBLE_FORMAT.format(updater.topRatingStorage.topPlayers.get(pos));
                } else {
                    updater.topRatingStorage.topPlayerPosLookups.add(pos);
                }
            }
            return "???";
        });

        identifiers.put("top", (params -> {
            if (topIdentifiers.containsKey(params[2])) {
                return topIdentifiers.get(params[2]).handle(params);
            }
            if (params.length > 3) {
                if (params[3].equals("name")) {
                    return plugin.statsManager.getTopKey(params[2], Integer.parseInt(params[2]), true);
                }
            }
            return "???";
        }));
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "levels";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(final OfflinePlayer player, final @NotNull String identifier) {
        if (identifier.isEmpty()) {
            return "";
        }

        final String[] params = identifier.split("_");
        if (identifiers.containsKey(params[0].toLowerCase())) {
            return identifiers.get(params[0]).handle(params);
        }

        if (player == null) {
            return "";
        }
        final UUID uuid = player.getUniqueId();
        if (player.isOnline()) {
            return playerIdentifiers.get(params[0]).handle(plugin.getPlayerConnect(uuid), params);
        }
        else if (updater.players.containsKey(uuid)) {
            return playerIdentifiers.get(params[0]).handle(updater.players.get(uuid), params);
        }
        else {
            updater.playerLookups.add(uuid);
            return "Loading...";
        }
    }

    public Updater getUpdater() {
        return updater;
    }

    public class Updater implements Runnable {
        public TopStorage<Double> topRatingStorage;
        public TopStorage<Integer> topLevelStorage;

        public Map<UUID, PlayerConnect> players;
        public Set<UUID> playerLookups;

        private ScheduledFuture<?> task;

        public Listener listener;

        public void startUpdating() {
            final long delay = plugin.config.get.getLong("top.update-interval");
            task                           = plugin.asyncExecutorManager.scheduleAtFixedRate(this, delay, delay, TimeUnit.MILLISECONDS);

            topLevelStorage = new TopStorage<>();
            topRatingStorage = new TopStorage<>();

            players = new HashMap<>();
            playerLookups = new HashSet<>();

            if (plugin.config.get.getBoolean("top.update-on-player-join")) {
                listener = new Listener() {};
                Bukkit.getPluginManager().registerEvent(PlayerJoinEvent.class, listener, EventPriority.NORMAL,
                        (li, e) -> plugin.asyncExecutorManager.execute(this), plugin);
            }
        }

        public void stopUpdating() {
            task.cancel(true);

            topLevelStorage = null;
            topRatingStorage = null;

            players = null;
            playerLookups = null;

            if (listener != null) {
                HandlerList.unregisterAll(listener);
            }
        }

        @Override
        public void run() {
            final PlayerDatabase.TopResult<Double> topRatingResult = plugin.database.getPlayerDatabase().getTopRatingResult();
            topRatingStorage.lookup(topRatingResult);
            topRatingResult.closeResult();

            final PlayerDatabase.TopResult<Integer> topLevelResult = plugin.database.getPlayerDatabase().getTopLevelResult();
            topLevelStorage.lookup(topLevelResult);
            topLevelResult.closeResult();

            for (final UUID uuid : playerLookups) {
                players.put(uuid, new PlayerConnect(uuid));
            }
        }
    }

    public static class TopStorage<V> {
        public Map<Integer, V> topPlayers = new HashMap<>();
        public Set<Integer> topPlayerPosLookups = new HashSet<>();
        public Map<Integer, UUID> topUUIDPlayers = new HashMap<>();
        public Set<Integer> topPlayerPosUUIDLookups = new HashSet<>();

        public void lookup(final PlayerDatabase.TopResult<? extends V> topResult) {
            for (final Integer i : topPlayerPosLookups) {
                topPlayers.put(i, topResult.getTop(i));
            }
            for (final Integer i : topPlayerPosUUIDLookups) {
                topUUIDPlayers.put(i, topResult.getTopUUID(i));
            }
        }
    }

    public interface PlayerIdentifierHandler {
        String handle(@NotNull final PlayerConnect player, final String[] params);
    }

    public interface IdentifierHandler {
        String handle(final String[] params);
    }
}