package com.grassminevn.levels.listeners;

import com.grassminevn.levels.Levels;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

public class EntityDeath implements Listener {
    private final Levels plugin;

    public EntityDeath(final Levels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntity(final EntityDeathEvent e) {
        final LivingEntity entity = e.getEntity();
        if (entity == null) {
            return;
        }
        final String uuid = entity.getUniqueId().toString();
        if (plugin.lastDamagers.containsKey(uuid)) {
            final Player target = plugin.getServer().getPlayer(UUID.fromString(plugin.lastDamagers.get(uuid)));
            if (target != null && target.isOnline()) {
                plugin.entityManager.getXP(e.getEntity(), target);
            }
            plugin.lastDamagers.remove(uuid);
            return;
        }
        plugin.entityManager.getXP(entity, entity.getKiller());
    }
}