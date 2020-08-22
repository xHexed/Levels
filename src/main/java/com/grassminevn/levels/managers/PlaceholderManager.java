package com.grassminevn.levels.managers;

import com.grassminevn.levels.Levels;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlaceholderManager {

    private final Levels plugin;

    public PlaceholderManager(final Levels plugin) {
        this.plugin = plugin;
    }

    public long[] getDurability(final ItemStack itemStack) {
        if (itemStack != null && isValid(itemStack.getType().name())) {
            final short max = itemStack.getType().getMaxDurability();
            return new long[] {max - itemStack.getDurability(), max};
        }
        return new long[] {0, 0};
    }

    private boolean isValid(final String name) {
        return name.endsWith("_HELMET")
                || name.endsWith("_CHESTPLATE")
                || name.endsWith("_LEGGINGS")
                || name.endsWith("_BOOTS")
                || name.endsWith("_SWORD")
                || name.endsWith("_PICKAXE")
                || name.endsWith("_AXE")
                || name.endsWith("_SHOVEL")
                || name.endsWith("SHIELD");
    }

    public ItemStack getHandItemStack(final Player player, final boolean main) {
        final ItemStack itemStack;
        if (main) {
            itemStack = player.getInventory().getItemInMainHand();
        } else {
            itemStack = player.getInventory().getItemInOffHand();
        }
        return itemStack;
    }
}