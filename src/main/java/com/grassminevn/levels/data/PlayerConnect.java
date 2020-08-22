package com.grassminevn.levels.data;

import com.grassminevn.levels.Levels;

import java.util.UUID;

public class PlayerConnect {
    private final String playeruuid;
    private String name;
    private Long xp;
    private Long level;
    private Long coins;
    private Double personalBooster;
    private int personalBoosterTime;
    private int personalBoosterTime_left;
    private int taskID;

    public PlayerConnect(final String uuid) {
        playeruuid = uuid;
        final String[] data = Levels.call.database.getValues(uuid);
        name = data[0];
        xp = Long.parseLong(data[1]);
        level = Long.parseLong(data[2]);
        coins = Long.parseLong(data[3]);
        loadTimer();
    }

    public String name() {
        return name;
    }

    public Long xp() {
        return xp;
    }

    public Long level() {
        return level;
    }

    public Long coins() {
        return coins;
    }

    public void name(final String set) {
        name = set;
    }

    public void xp(final Long set) {
        xp = set;
    }

    public void level(final Long set) {
        level = set;
    }

    public void coins(final Long set) {
        coins = set;
    }

    public void save() {
        Levels.call.database.setValuesAsync(playeruuid, name, xp, level, coins);
    }

    public void syncSave() {
        Levels.call.database.setVaules(playeruuid, name, xp, level, coins);
    }

    public Double getPersonalBooster() {
        return personalBooster;
    }

    public int getPersonalBoosterTime() {
        return personalBoosterTime;
    }

    public int getPersonalBoosterTime_left() {
        return personalBoosterTime_left;
    }

    public void timer(final int timeAmount, final Double personalBooster) {
        this.personalBooster = personalBooster;
        personalBoosterTime  = timeAmount;
        taskID               = Levels.call.getServer().getScheduler().scheduleSyncRepeatingTask(Levels.call, new Runnable(){
            private int timeRemaining = timeAmount;
            public void run(){
                personalBoosterTime_left = timeRemaining;
                if (timeRemaining <= 0) {
                    endBoost();
                    Levels.call.boosters.get.set("players." + playeruuid + ".personal-active", null);
                    Levels.call.boosters.save();
                }
                timeRemaining--;
            }
        }, 0L, 20L);
    }

    private void endBoost() {
        Levels.call.getServer().getScheduler().cancelTask(taskID);
        personalBooster          = null;
        personalBoosterTime      = 0;
        personalBoosterTime_left = 0;
        for (final String command : Levels.call.boosters.get.getStringList("personal-settings.end")) {
            Levels.call.getServer().dispatchCommand(Levels.call.consoleCommandSender, command.replace("{levels_player}", Levels.call.getServer().getOfflinePlayer(UUID.fromString(playeruuid)).getName()));
        }
    }

    private void loadTimer() {
        final String path = "players." + playeruuid + ".personal-active";
        if (Levels.call.boosters.get.contains(path)) {
            final String[] get = Levels.call.boosters.get.getString(path).split(" ");
            timer(Integer.parseInt(get[1]), Double.valueOf(get[0]));
        }
    }
}