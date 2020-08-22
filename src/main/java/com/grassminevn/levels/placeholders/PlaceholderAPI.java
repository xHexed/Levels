package com.grassminevn.levels.placeholders;

import com.grassminevn.levels.Levels;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

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
        if(identifier.equals("xp")){
            return String.valueOf(plugin.get(player.getUniqueId().toString()).xp());
        }
        if(identifier.equals("level")){
            return String.valueOf(plugin.get(player.getUniqueId().toString()).level());
        }
        if (identifier.equals("coins")) {
            return String.valueOf(plugin.get(player.getUniqueId().toString()).coins());
        }
        if(identifier.equals("xp_required")){
            return String.valueOf(plugin.statsManager.xp_required(player.getUniqueId().toString(), false));
        }
        if(identifier.equals("xp_required_next")){
            return String.valueOf(plugin.statsManager.xp_required(player.getUniqueId().toString(), true));
        }
        if(identifier.equals("xp_progress")){
            return String.valueOf(plugin.statsManager.xp_progress(player.getUniqueId().toString()));
        }
        if(identifier.equals("xp_progress_style")){
            return String.valueOf(plugin.statsManager.xp_progress_style(player.getUniqueId().toString()));
        }
        if(identifier.equals("group")){
            return String.valueOf(plugin.statsManager.group(player));
        }
        if(identifier.equals("group_to")){
            return String.valueOf(plugin.statsManager.group_to(player));
        }
        if(identifier.equals("prefix")){
            return String.valueOf(plugin.statsManager.prefix(player));
        }
        if(identifier.equals("global_booster")){
            return plugin.boostersManager.getGlobalPlaceholder();
        }
        if(identifier.equals("global_booster_name")){
            return plugin.boostersManager.getGlobalNamePlaceholder();
        }
        if(identifier.equals("global_booster_time")){
            return plugin.boostersManager.getGlobalTimePlaceholder();
        }
        if(identifier.equals("global_booster_time_left")){
            return plugin.boostersManager.getGlobalTimeLeftPlaceholder();
        }
        if(identifier.equals("global_booster_time_prefix")){
            return plugin.boostersManager.getGlobalTimePrefixPlaceholder();
        }
        if(identifier.equals("global_booster_time_left_prefix")){
            return plugin.boostersManager.getGlobalTimeLeftPrefixPlaceholder();
        }
        if(identifier.equals("personal_booster")){
            return plugin.boostersManager.getPersonalPlaceholder(player.getUniqueId().toString());
        }
        if(identifier.equals("personal_booster_time")){
            return plugin.boostersManager.getPersonalTimePlaceholder(player.getUniqueId().toString());
        }
        if(identifier.equals("personal_booster_time_left")){
            return plugin.boostersManager.getPersonalTimeLeftPlaceholder(player.getUniqueId().toString());
        }
        if(identifier.equals("personal_booster_time_prefix")){
            return plugin.boostersManager.getPersonalTimePrefixPlaceholder(player.getUniqueId().toString());
        }
        if(identifier.equals("personal_booster_time_left_prefix")){
            return plugin.boostersManager.getPersonalTimeLeftPrefixPlaceholder(player.getUniqueId().toString());
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
        return null;
    }
}