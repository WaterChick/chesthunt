package dev.waterchick.chesthunt.configs;

import cz.waterchick.configwrapper.Config;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.Settings;
import dev.waterchick.chesthunt.data.PlayerData;
import dev.waterchick.chesthunt.managers.PlayerManager;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDataConfig extends Config {

    public PlayerDataConfig(File dataFolder) {
        super(dataFolder, "player-data.yml");
    }

    @Override
    public void onLoad() {
        YamlDocument config = getConfig();
        PlayerManager pm = PlayerManager.getInstance();
        Set<Object> uuidSet = config.getKeys();
        if(uuidSet.isEmpty()){
            return;
        }
        for(Object uuidObject : uuidSet){
            UUID uuid = UUID.fromString(uuidObject.toString());
            int foundTreasureChests = config.getInt(uuid+".foundTreasureChests");
            pm.get(uuid).setFoundTreasureChests(foundTreasureChests);
        }
    }

    @Override
    public void onSave() {
        YamlDocument config = getConfig();
        PlayerManager pm = PlayerManager.getInstance();
        for(Map.Entry<UUID, PlayerData> entry : pm.getPlayerData().entrySet()){
            UUID uuid = entry.getKey();
            PlayerData playerData = entry.getValue();
            config.set(uuid+".foundTreasureChests", playerData.getFoundTreasureChests());
        }
    }

    @Override
    public Settings[] getSettings(){
        return null;
    }
}
