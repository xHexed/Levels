package com.grassminevn.levels.listeners;

import com.grassminevn.levels.Levels;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {
    private final Levels plugin;

    public EntityDamageByEntity(final Levels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntity(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            plugin.lastDamagers.put(e.getEntity().getUniqueId().toString(), e.getDamager().getUniqueId().toString());
        }
    }
}