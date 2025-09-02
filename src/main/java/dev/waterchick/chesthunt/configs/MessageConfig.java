package dev.waterchick.chesthunt.configs;

import com.google.common.base.Enums;
import cz.waterchick.configwrapper.Config;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.Settings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.utils.ChatUtils;

import java.io.File;
import java.util.List;
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


            if (!Enums.getIfPresent(ConfigValue.class, enumPrefix).isPresent()) continue;
            ConfigValue configValue = ConfigValue.valueOf(enumPrefix);

            if(getConfig().getStringList(keyString).isEmpty()){
                String value = getConfig().getString(keyString);
                configValue.setValue(ChatUtils.color(value));
            }else{
                List<String> listValues = getConfig().getStringList(keyString);
                configValue.setListValues(ChatUtils.color(listValues));
            }
        }
    }

    @Override
    public void onSave() {

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
