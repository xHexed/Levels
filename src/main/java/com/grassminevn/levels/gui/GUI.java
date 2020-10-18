package com.grassminevn.levels.gui;

import com.grassminevn.levels.Levels;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class GUI implements InventoryHolder {

    protected final Menu playerMenu;
    protected Inventory inventory;

    public abstract String getName();
    public abstract int getSize();
    public abstract void click(InventoryClickEvent e);
    public abstract void setItems();

    protected int page;
    protected int index;
    protected List<OfflinePlayer> players;

    public GUI(final Menu playerMenu) {
        this.playerMenu = playerMenu;
    }

    public void open() {
        inventory = Levels.call.getServer().createInventory(this, getSize(), getName());
        setItems();
        playerMenu.getPlayer().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setPlayers() {
        players = new ArrayList<>(Levels.call.statsManager.getTopMap(playerMenu.getSort(), playerMenu.getReverse()).keySet());
    }
}