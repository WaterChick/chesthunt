package dev.waterchick.chesthunt.hooks;

import dev.waterchick.chesthunt.data.TreasureChest;
import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.managers.ChestHuntManager;
import dev.waterchick.chesthunt.managers.PlayerManager;
import dev.waterchick.chesthunt.utils.ChatUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PAPIHook extends PlaceholderExpansion {

    private final JavaPlugin plugin;
    private final ChestHuntManager chestHuntManager;

    public PAPIHook(JavaPlugin plugin, ChestHuntManager chestHuntManager){
        this.plugin = plugin;
        this.chestHuntManager = chestHuntManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "chesthunt"; // zde si nastav svůj identifikátor
    }

    @Override
    public @NotNull String getAuthor() {
        List<String> authors = plugin.getDescription().getAuthors();
        return authors.isEmpty() ? "Unknown" : String.join(", ", authors);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        TreasureChest currentChest = chestHuntManager.getCurrentChest();
        switch (params.toLowerCase()) {
            case "active":
                return (currentChest != null && !currentChest.isFound()) ? ConfigValue.MESSAGES_PLACEHOLDERACTIVEYES.getValue() : ConfigValue.MESSAGES_PLACEHOLDERACTIVENO.getValue();

            case "timeleft":
                return (currentChest != null && !currentChest.isFound())
                        ? ChatUtils.formatTimeLeft(currentChest.getSecondsLeft())
                        : "0s";

            case "found":
                PlayerManager pm = PlayerManager.getInstance();
                return String.valueOf(pm.get(player).getFoundTreasureChests());

            default:
                return null;
        }
    }
}
