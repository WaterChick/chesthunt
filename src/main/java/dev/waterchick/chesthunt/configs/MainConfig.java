package dev.waterchick.chesthunt.configs;

import com.google.common.base.Enums;
import cz.waterchick.configwrapper.Config;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.Settings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.managers.LoggingManager;

import java.io.File;
import java.util.List;
import java.util.Set;

public class MainConfig extends Config {

    public MainConfig(File dataFolder) {
        super(dataFolder, "config.yml");
    }

    @Override
    public void onLoad() {
        Set<Object> section = getConfig().getKeys();
        LoggingManager logger = LoggingManager.getInstance();
        for (Object key : section) {
            String keyString = key.toString();
            String keyStringUpperCase = keyString.toUpperCase();
            if (Enums.getIfPresent(ConfigValue.class, keyStringUpperCase).isPresent()) {
                String value = getConfig().getString(keyString);
                ConfigValue configValue = ConfigValue.valueOf(keyStringUpperCase);
                configValue.setValue(value);
            }

            if (keyString.equalsIgnoreCase("chest_hunt_spawn")) {
                Section chestHuntSection = getConfig().getSection("chest_hunt_spawn");

                if (chestHuntSection != null) {
                    List<String> days = chestHuntSection.getStringList("days");
                    List<String> times = chestHuntSection.getStringList("times");
                    ConfigValue.CHEST_HUNT_SPAWN_TIMES.setListValues(times);
                    ConfigValue.CHEST_HUNT_SPAWN_DAYS.setListValues(days);

                    logger.debug("Chest hunt");
                    logger.debug(ConfigValue.CHEST_HUNT_SPAWN_TIMES.getListValues().toString());
                    logger.debug(ConfigValue.CHEST_HUNT_SPAWN_DAYS.getListValues().toString());
                }
            }
        }
    }

    @Override
    public void onSave() {
        getConfig().set("panic", ConfigValue.PANIC.getValue());
    }

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
