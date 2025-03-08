package dev.waterchick.chesthunt.configs;

import com.google.common.base.Enums;
import cz.waterchick.configwrapper.Config;
import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.utils.ChatUtils;

import java.io.File;
import java.util.Set;

public class MessageConfig extends Config {
    public MessageConfig(File dataFolder) {
        super(dataFolder, "messages.yml");
    }

    @Override
    public void onLoad() {
        Set<Object> section = getConfig().getKeys();
        for(Object key : section){
            String keyString = key.toString();
            String enumPrefix = "MESSAGES_"+keyString.toUpperCase();
            String value = getConfig().getString(keyString);

            if (!Enums.getIfPresent(ConfigValue.class, enumPrefix).isPresent()) continue;
            ConfigValue configValue = ConfigValue.valueOf(enumPrefix);
            configValue.setValue(ChatUtils.color(value));
        }
    }

    @Override
    public void onSave() {

    }
}
