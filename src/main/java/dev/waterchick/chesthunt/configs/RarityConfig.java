package dev.waterchick.chesthunt.configs;

import cz.waterchick.configwrapper.Config;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.Settings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.waterchick.chesthunt.data.Rarity;
import dev.waterchick.chesthunt.managers.LoggingManager;
import dev.waterchick.chesthunt.managers.RarityManager;
import dev.waterchick.chesthunt.utils.ChatUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RarityConfig extends Config {

    private final RarityManager rarityManager;


    public RarityConfig(File dataFolder, RarityManager rarityManager) {
        super(dataFolder, "rarity.yml");
        this.rarityManager = rarityManager;
    }

    @Override
    public void onLoad() {
        Section section = getConfig().getSection("rarities");
        if(section == null) return;
        List<Rarity> rarities = new ArrayList<>();
        for(Object key : section.getKeys()){
            String rarityName = key.toString();
            String displayName = ChatUtils.color(getConfig().getString(section.getNameAsString()+"."+rarityName+".displayName"));
            Integer chance = getConfig().getInt(section.getNameAsString()+"."+rarityName+".chance");

            Rarity rarity = new Rarity(rarityName,displayName, chance);
            rarities.add(rarity);

            LoggingManager.getInstance().debug("Loaded rarity at: "+section.getNameAsString()+"."+rarityName);
        }
        rarityManager.setRarities(rarities);

    }

    @Override
    public void onSave() {}

    @Override
    public Settings[] getSettings(){
        return new Settings[]
                {
                        UpdaterSettings.builder()
                                .setVersioning(new BasicVersioning("config-version"))
                                .build(),
                        LoaderSettings.builder().setAutoUpdate(true).build()
                };
    }
}
