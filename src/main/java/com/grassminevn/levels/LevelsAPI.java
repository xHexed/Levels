package com.grassminevn.levels;

import com.grassminevn.levels.data.PlayerConnect;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class LevelsAPI {
    public static final LevelsAPI api = new LevelsAPI();

    public Long xp(final String uuid) {
        return Levels.call.get(uuid).xp();
    }

    public void xp(final String uuid, final Long set) {
        Levels.call.get(uuid).xp(set);
    }

    public Long level(final String uuid) {
        return Levels.call.get(uuid).level();
    }

    public void level(final String uuid, final Long set) {
        Levels.call.get(uuid).level(set);
    }

    public Long coins(final String uuid) {
        return Levels.call.get(uuid).coins();
    }

    public void coins(final String uuid, final Long set) {
        Levels.call.get(uuid).coins(set);
    }

    public String xp_progress(final String uuid) {
        return Levels.call.statsManager.xp_progress(uuid);
    }

    public Long xp_required(final String uuid, final boolean next) {
        return Levels.call.statsManager.xp_required(uuid, next);
    }

    public Long xp_required(final String uuid) {
        return Levels.call.statsManager.xp_required(uuid, false);
    }

    public String xp_progress_style(final String uuid) {
        return Levels.call.statsManager.xp_progress_style(uuid);
    }

    public String group(final Player player) {
        return Levels.call.statsManager.group(player);
    }

    public String group_to(final Player player) {
        return Levels.call.statsManager.group_to(player);
    }

    public void save(final String uuid) {
        Levels.call.get(uuid).save();
    }

    public void syncSave(final String uuid) {
        final PlayerConnect player = Levels.call.get(uuid);
        if (player == null) return;
        player.syncSave();
    }

    public Set<String> list() {
        return Levels.call.list();
    }

    public String getTopValue(final String type, final int number, final boolean key, final boolean reverse) {
        return Levels.call.statsManager.getTopValue(type, number, key, reverse);
    }

    public LinkedHashMap getTopMap(final String type, final boolean reverse) {
        return Levels.call.statsManager.getTopMap(type, reverse);
    }

    public Double getPersonalBooster(final String uuid) {
        return Levels.call.get(uuid).getPersonalBooster();
    }

    public boolean hasGlobalActive() {
        return Levels.call.boostersManager.hasGlobalActive();
    }

    public OfflinePlayer getGlobalOfflinePlayer() {
        return Levels.call.boostersManager.getGlobalOfflinePlayer();
    }

    public Double getGlobalBooster() {
        return Levels.call.boostersManager.getGlobalBooster();
    }

    public int getGlobalSeconds() {
        return Levels.call.boostersManager.getGlobalSeconds();
    }

    public String getTimeFormat(final int seconds) {
        return Levels.call.boostersManager.timeLeft(seconds);
    }

    public List<String> getGlobalQueue() {
        return Levels.call.boostersManager.globalQueue();
    }

    public int getGlobalQueueNumber(final String uuid) {
        return Levels.call.boostersManager.queueNumber(uuid);
    }

    public int getGlobalQueueSize(final String uuid) {
        return Levels.call.boostersManager.isInQueueSize(uuid);
    }

    public String getGlobalNamePlaceholder() {
        return Levels.call.boostersManager.getGlobalNamePlaceholder();
    }

    public String getGlobalPlaceholder() {
        return Levels.call.boostersManager.getGlobalPlaceholder();
    }

    public String getGlobalTimePlaceholder() {
        return Levels.call.boostersManager.getGlobalTimePlaceholder();
    }

    public String getGlobalTimeLeftPlaceholder() {
        return Levels.call.boostersManager.getGlobalTimeLeftPlaceholder();
    }

    public String getGlobalTimePrefixPlaceholder() {
        return Levels.call.boostersManager.getGlobalTimePrefixPlaceholder();
    }

    public String getGlobalTimeLeftPrefixPlaceholder() {
        return Levels.call.boostersManager.getGlobalTimeLeftPrefixPlaceholder();
    }

    public String getPersonalPlaceholder(final String uuid) {
        return Levels.call.boostersManager.getPersonalPlaceholder(uuid);
    }

    public String getPersonalTimePlaceholder(final String uuid) {
        return Levels.call.boostersManager.getPersonalTimePlaceholder(uuid);
    }

    public String getPersonalTimeLeftPlaceholder(final String uuid) {
        return Levels.call.boostersManager.getPersonalTimeLeftPlaceholder(uuid);
    }

    public String getPersonalTimePrefixPlaceholder(final String uuid) {
        return Levels.call.boostersManager.getPersonalTimePrefixPlaceholder(uuid);
    }

    public String getPersonalTimeLeftPrefixPlaceholder(final String uuid) {
        return Levels.call.boostersManager.getPersonalTimeLeftPrefixPlaceholder(uuid);
    }
}