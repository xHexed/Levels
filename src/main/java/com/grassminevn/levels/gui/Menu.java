package com.grassminevn.levels.gui;

import org.bukkit.entity.Player;

public class Menu {

    private final Player player;

    private final String uuid;

    private String sort;

    private boolean reverse;

    public Menu(final Player player) {
        this.player = player;
        uuid = player.getUniqueId().toString();
        sort = "kills";
        reverse = true;
    }

    public Player getPlayer() {
        return player;
    }

    public String getUuid() {
        return uuid;
    }

    public String getSort() {
        return sort;
    }

    public boolean getReverse() {
        return reverse;
    }

    public void setSort(final String sort) {
        this.sort = sort;
    }

    public void setReverse(final boolean reverse) {
        this.reverse = reverse;
    }
}