package com.grassminevn.levels.managers;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityManager {

    private final Levels plugin;

    public EntityManager(final Levels plugin) {
        this.plugin = plugin;
    }

    public void getXP(final Entity entity, final Player killer) {
        if (entity instanceof Player) {
            final Player killed = (Player) entity;
            final String killedUUID = killed.getUniqueId().toString();
            if (!plugin.config.get.getStringList("levelup.all-excluded").contains(killedUUID)) {
                final PlayerConnect playerConnect = plugin.get(killedUUID);
                if (plugin.systemManager.world(killed, plugin.config.get, "deaths")) {
                    if (plugin.config.get.getBoolean("events.Deaths.only-players")) {
                        if (killer != null) {
                            death(killed);
                        }
                    } else {
                        death(killed);
                    }
                }
                final String entityKiller = plugin.entityManager.getEntityKiller(killed);
                plugin.xpManager.check(playerConnect, entityKiller, entity, killed, false);
            }
        }
        final String entityUUID = entity.getUniqueId().toString();
        if (killer != null && !plugin.config.get.getStringList("levelup.all-excluded").contains(killer.getUniqueId().toString())) {
            final String entityName = plugin.entityManager.getEntityName(entity);
            final PlayerConnect playerConnect = plugin.get(killer.getUniqueId().toString());
            if (entityName != null && !plugin.killSessionUtils.check(entity, killer) && !plugin.spawners.contains(entityUUID)) {
                if (!plugin.xpManager.isMaxLevel(killer, playerConnect)) {
                    plugin.xpManager.check(playerConnect, entityName, entity, killer, true);
                }
                if (entity instanceof Player) {
                    if (plugin.systemManager.world(killer, plugin.config.get, "kills")) {
                        if (plugin.config.get.getBoolean("events.Kills")) {
                            plugin.systemManager.executeCommands(killer, plugin.config.get, "kills", "commands", 0L);
                        }
                    }
                }
            }
        }
        plugin.spawners.remove(entityUUID);
    }

    private void death(final Player killed) {
        if (plugin.config.get.getBoolean("events.Deaths.use")) { plugin.systemManager.executeCommands(killed, plugin.config.get, "deaths", "commands", 0L); }
    }

    public String getEntityName(final Entity entity) {
        final String entityType = entity.getType().toString().toLowerCase();
        if (plugin.config.get.getConfigurationSection("xp").getKeys(false).contains(entityType)) {
            return entityType;
        }
        return null;
    }

    private String getEntityKiller(final Entity killed) {
        final EntityDamageEvent entityDamageEvent = killed.getLastDamageCause();
        String entityType = null;
        if (!plugin.config.get.getConfigurationSection("xp").getKeys(false).contains("all")) {
            if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
                final Entity entity = ((EntityDamageByEntityEvent)entityDamageEvent).getDamager();
                entityType = entity.getType().toString().toLowerCase();
                if (entity.getCustomName() != null) {
                    entityType = ChatColor.stripColor(entity.getCustomName().toLowerCase());
                }
            } else {
                if (entityDamageEvent != null) {
                    entityType = entityDamageEvent.getCause().toString().toLowerCase();
                }
            }
        } else {
            entityType = "all";
        }
        return entityType;
    }
}
