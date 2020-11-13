package com.grassminevn.levels.placeholders;

import com.grassminevn.levels.Levels;
import com.grassminevn.levels.data.PlayerConnect;
import com.grassminevn.levels.util.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public class PlaceholderAPI extends PlaceholderExpansion {

    private final Levels plugin;

    public PlaceholderAPI(final Levels plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "levels";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(final Player player, final String identifier){
        if(player == null){
            return "";
        }
        final UUID uuid = player.getUniqueId();
        final PlayerConnect playerConnect = plugin.getPlayerConnect(uuid);
        if(identifier.equals("xp")){
            return String.valueOf(playerConnect.getXP());
        }
        if(identifier.equals("level")){
            return String.valueOf(playerConnect.getLevel());
        }
        if(identifier.equals("rating")) {
            return String.valueOf(playerConnect.getRating().getMean());
        }
        if(identifier.equals("level_next")){
            return Utils.DOUBLE_FORMAT.format(playerConnect.getLevel() + 1);
        }
        if (identifier.equals("xp_need")) {
            return String.valueOf(plugin.statsManager.xp_need(playerConnect));
        }
        if(identifier.equals("xp_required")){
            return String.valueOf(plugin.statsManager.xp_required(playerConnect, false));
        }
        if(identifier.equals("xp_required_next")){
            return String.valueOf(plugin.statsManager.xp_required(playerConnect, true));
        }
        if(identifier.equals("xp_progress")){
            return String.valueOf(plugin.statsManager.xp_progress(playerConnect));
        }
        if(identifier.equals("xp_progress_style")){
            return String.valueOf(plugin.statsManager.xp_progress_style(playerConnect, "xp-progress-style"));
        }
        if(identifier.equals("xp_progress_style_2")){
            return String.valueOf(plugin.statsManager.xp_progress_style(playerConnect, "xp-progress-style-2"));
        }
        if(identifier.equals("time")){
            return String.valueOf(plugin.statsManager.time("time", playerConnect.getTime().getTime()));
        }
        if(identifier.equals("date")){
            return String.valueOf(plugin.statsManager.time("date", playerConnect.getTime().getTime()));
        }
        if(identifier.equals("group")) {
            return playerConnect.getGroup();
        }
        if(identifier.equals("level_group")) {
            return plugin.statsManager.group(playerConnect);
        }
        if(identifier.equals("level_prefix")) {
            return plugin.statsManager.prefix(playerConnect);
        }
        if(identifier.equals("level_suffix")) {
            return plugin.statsManager.suffix(playerConnect);
        }
        if(identifier.equals("multiplier")) {
            if (playerConnect.getMultiplier() != 0D) {
                return String.valueOf(playerConnect.getMultiplier());
            } else {
                return String.valueOf(1);
            }
        }
        if(identifier.equals("multiplier_time")) {
            if (playerConnect.getMultiplier_time() != 0D) {
                return plugin.statsManager.time("multiplier", (new GregorianCalendar(0, Calendar.JANUARY,0,0,0, playerConnect.getMultiplier_time()).getTime().getTime()));
            } else {
                return String.valueOf(0);
            }
        }
        if(identifier.equals("multiplier_time_left")) {
            if (playerConnect.getMultiplier_time() != 0D) {
                return plugin.statsManager.time("multiplier", (new GregorianCalendar(0, Calendar.JANUARY,0,0,0, playerConnect.getMultiplier_time_left()).getTime().getTime()));
            } else {
                return String.valueOf(0);
            }
        }
        if(identifier.equals("top_1_xp_name")){
            return plugin.statsManager.getTopValue("xp", 0, true, true);
        }
        if(identifier.equals("top_1_xp")){
            return plugin.statsManager.getTopValue("xp", 0, false, true);
        }
        if(identifier.equals("top_2_xp_name")){
            return plugin.statsManager.getTopValue("xp", 1, true, true);
        }
        if(identifier.equals("top_2_xp")){
            return plugin.statsManager.getTopValue("xp", 1, false, true);
        }
        if(identifier.equals("top_3_xp_name")){
            return plugin.statsManager.getTopValue("xp", 2, true, true);
        }
        if(identifier.equals("top_3_xp")){
            return plugin.statsManager.getTopValue("xp", 2, false, true);
        }
        if(identifier.equals("top_4_xp_name")){
            return plugin.statsManager.getTopValue("xp", 3, true, true);
        }
        if(identifier.equals("top_4_xp")){
            return plugin.statsManager.getTopValue("xp", 3, false, true);
        }
        if(identifier.equals("top_5_xp_name")){
            return plugin.statsManager.getTopValue("xp", 4, true, true);
        }
        if(identifier.equals("top_5_xp")){
            return plugin.statsManager.getTopValue("xp", 4, false, true);
        }
        if(identifier.equals("top_6_xp_name")){
            return plugin.statsManager.getTopValue("xp", 5, true, true);
        }
        if(identifier.equals("top_6_xp")){
            return plugin.statsManager.getTopValue("xp", 5, false, true);
        }
        if(identifier.equals("top_7_xp_name")){
            return plugin.statsManager.getTopValue("xp", 6, true, true);
        }
        if(identifier.equals("top_7_xp")){
            return plugin.statsManager.getTopValue("xp", 6, false, true);
        }
        if(identifier.equals("top_8_xp_name")){
            return plugin.statsManager.getTopValue("xp", 7, true, true);
        }
        if(identifier.equals("top_8_xp")){
            return plugin.statsManager.getTopValue("xp", 7, false, true);
        }
        if(identifier.equals("top_9_xp_name")){
            return plugin.statsManager.getTopValue("xp", 8, true, true);
        }
        if(identifier.equals("top_9_xp")){
            return plugin.statsManager.getTopValue("xp", 8, false, true);
        }
        if(identifier.equals("top_10_xp_name")){
            return plugin.statsManager.getTopValue("xp", 9, true, true);
        }
        if(identifier.equals("top_10_xp")){
            return plugin.statsManager.getTopValue("xp", 9, false, true);
        }
        if(identifier.equals("top_1_level_name")){
            return plugin.statsManager.getTopValue("level", 0, true, true);
        }
        if(identifier.equals("top_1_level")){
            return plugin.statsManager.getTopValue("level", 0, false, true);
        }
        if(identifier.equals("top_2_level_name")){
            return plugin.statsManager.getTopValue("level", 1, true, true);
        }
        if(identifier.equals("top_2_level")){
            return plugin.statsManager.getTopValue("level", 1, false, true);
        }
        if(identifier.equals("top_3_level_name")){
            return plugin.statsManager.getTopValue("level", 2, true, true);
        }
        if(identifier.equals("top_3_level")){
            return plugin.statsManager.getTopValue("level", 2, false, true);
        }
        if(identifier.equals("top_4_level_name")){
            return plugin.statsManager.getTopValue("level", 3, true, true);
        }
        if(identifier.equals("top_4_level")){
            return plugin.statsManager.getTopValue("level", 3, false, true);
        }
        if(identifier.equals("top_5_level_name")){
            return plugin.statsManager.getTopValue("level", 4, true, true);
        }
        if(identifier.equals("top_5_level")){
            return plugin.statsManager.getTopValue("level", 4, false, true);
        }
        if(identifier.equals("top_6_level_name")){
            return plugin.statsManager.getTopValue("level", 5, true, true);
        }
        if(identifier.equals("top_6_level")){
            return plugin.statsManager.getTopValue("level", 5, false, true);
        }
        if(identifier.equals("top_7_level_name")){
            return plugin.statsManager.getTopValue("level", 6, true, true);
        }
        if(identifier.equals("top_7_level")){
            return plugin.statsManager.getTopValue("level", 6, false, true);
        }
        if(identifier.equals("top_8_level_name")){
            return plugin.statsManager.getTopValue("level", 7, true, true);
        }
        if(identifier.equals("top_8_level")){
            return plugin.statsManager.getTopValue("level", 7, false, true);
        }
        if(identifier.equals("top_9_level_name")){
            return plugin.statsManager.getTopValue("level", 8, true, true);
        }
        if(identifier.equals("top_9_level")){
            return plugin.statsManager.getTopValue("level", 8, false, true);
        }
        if(identifier.equals("top_10_level_name")){
            return plugin.statsManager.getTopValue("level", 9, true, true);
        }
        if(identifier.equals("top_10_level")){
            return plugin.statsManager.getTopValue("level", 9, false, true);
        }
        if(identifier.equals("top_1_rating_name")){
            return plugin.statsManager.getTopValue("rating", 0, true, true);
        }
        if(identifier.equals("top_1_rating")){
            return plugin.statsManager.getTopValue("rating", 0, false, true);
        }
        if(identifier.equals("top_2_rating_name")){
            return plugin.statsManager.getTopValue("rating", 1, true, true);
        }
        if(identifier.equals("top_2_rating")){
            return plugin.statsManager.getTopValue("rating", 1, false, true);
        }
        if(identifier.equals("top_3_rating_name")){
            return plugin.statsManager.getTopValue("rating", 2, true, true);
        }
        if(identifier.equals("top_3_rating")){
            return plugin.statsManager.getTopValue("rating", 2, false, true);
        }
        if(identifier.equals("top_4_rating_name")){
            return plugin.statsManager.getTopValue("rating", 3, true, true);
        }
        if(identifier.equals("top_4_rating")){
            return plugin.statsManager.getTopValue("rating", 3, false, true);
        }
        if(identifier.equals("top_5_rating_name")){
            return plugin.statsManager.getTopValue("rating", 4, true, true);
        }
        if(identifier.equals("top_5_rating")){
            return plugin.statsManager.getTopValue("rating", 4, false, true);
        }
        if(identifier.equals("top_6_rating_name")){
            return plugin.statsManager.getTopValue("rating", 5, true, true);
        }
        if(identifier.equals("top_6_rating")){
            return plugin.statsManager.getTopValue("rating", 5, false, true);
        }
        if(identifier.equals("top_7_rating_name")){
            return plugin.statsManager.getTopValue("rating", 6, true, true);
        }
        if(identifier.equals("top_7_rating")){
            return plugin.statsManager.getTopValue("rating", 6, false, true);
        }
        if(identifier.equals("top_8_rating_name")){
            return plugin.statsManager.getTopValue("rating", 7, true, true);
        }
        if(identifier.equals("top_8_rating")){
            return plugin.statsManager.getTopValue("rating", 7, false, true);
        }
        if(identifier.equals("top_9_rating_name")){
            return plugin.statsManager.getTopValue("rating", 8, true, true);
        }
        if(identifier.equals("top_9_rating")){
            return plugin.statsManager.getTopValue("rating", 8, false, true);
        }
        if(identifier.equals("top_10_rating_name")){
            return plugin.statsManager.getTopValue("rating", 9, true, true);
        }
        if(identifier.equals("top_10_rating")){
            return plugin.statsManager.getTopValue("rating", 9, false, true);
        }
        return null;
    }
}