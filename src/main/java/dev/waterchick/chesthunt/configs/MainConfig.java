package dev.waterchick.chesthunt.configs;

import com.google.common.base.Enums;
import cz.waterchick.configwrapper.Config;
import dev.waterchick.chesthunt.enums.ConfigValue;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Set;

public class MainConfig extends Config {

    public MainConfig(File dataFolder) {
        super(dataFolder, "config.yml");
    }

    @Override
    public void onLoad() {
        Set<Object> section = getConfig().getKeys();
        for(Object key : section){
            String keyString = key.toString();
            String keyStringUpperCase = keyString.toUpperCase();
            String value = getConfig().getString(keyString);

            if (!Enums.getIfPresent(ConfigValue.class, keyStringUpperCase).isPresent()) continue;
            ConfigValue configValue = ConfigValue.valueOf(keyStringUpperCase);
            configValue.setValue(value);
        }
    }

    @Override
    public void onSave() {
        getConfig().set("panic", ConfigValue.PANIC.getValue());
    }
}
