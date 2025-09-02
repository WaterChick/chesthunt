package dev.waterchick.chesthunt.managers;


import dev.waterchick.chesthunt.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    private static PlayerManager instance;

    public static PlayerManager getInstance(){
        return instance;
    }

    public static void initialize(){
        instance = new PlayerManager();
    }

    private final HashMap<UUID, PlayerData> playerData = new HashMap<>();

    public PlayerData get(UUID uuid) {
        return playerData.computeIfAbsent(uuid, k -> new PlayerData());
    }

    public PlayerData get(Player player) {
        return playerData.computeIfAbsent(player.getUniqueId(), k -> new PlayerData());
    }

    public HashMap<UUID, PlayerData> getPlayerData() {
        return playerData;
    }
}
