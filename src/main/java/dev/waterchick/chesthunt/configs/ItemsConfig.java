package dev.waterchick.chesthunt.configs;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.spigot.SpigotSerializer;
import dev.waterchick.chesthunt.data.Rarity;
import dev.waterchick.chesthunt.data.CustomItem;
import dev.waterchick.chesthunt.managers.ItemManager;
import dev.waterchick.chesthunt.managers.LoggingManager;
import dev.waterchick.chesthunt.managers.RarityManager;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class ItemsConfig {

    private final ItemManager itemManager;
    private final RarityManager rarityManager;
    private final Logger logger = LoggingManager.getInstance().getLogger();

    private YamlDocument config;
    private final File file;
    private final File dataFolder;

    public ItemsConfig(File dataFolder, ItemManager itemManager, RarityManager rarityManager) {
        this.itemManager = itemManager;
        this.rarityManager = rarityManager;
        this.dataFolder = dataFolder;
        String name = "items.yml";
        this.file = new File(dataFolder, name);
    }

    public void loadConfig(){
        try {
            this.config = YamlDocument.create(file,
                    GeneralSettings.builder().setUseDefaults(false)
                            .setSerializer(SpigotSerializer.getInstance())

                            .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        onLoad();
    }

    public void saveConfig(){
        onSave();
        try {
            this.config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void onLoad() {
        Section section = getConfig().getSection("items");
        if(section == null) return;
        List<CustomItem> items = new ArrayList<>();
        for(Object key : section.getKeys()){
            UUID uuid = UUID.fromString(key.toString());
            ItemStack itemStack = getConfig().getAs(section.getNameAsString()+"."+uuid+".item", ItemStack.class);
            String rarityName = getConfig().getString(section.getNameAsString()+"."+uuid+".rarityName");
            Optional<Rarity> optionalRarity = this.rarityManager.getRarityByName(rarityName);
            if(itemStack == null) {
                logger.severe("Could not load items."+key+" | Invalid ItemStack Material");
                continue;
            }
            Rarity rarity = optionalRarity.orElseGet(() -> {
                Rarity defaultRarity = this.rarityManager.getDefaultRarity();
                logger.severe("Could not load items." + key + " | Invalid Rarity. Setting " + defaultRarity.getName() + " as a default rarity");
                return defaultRarity;
            });
            CustomItem customItem = new CustomItem(itemStack,rarity);
            CustomItem.setItemIdToItemStack(customItem.getItemStack(), customItem.getId());
            items.add(customItem);

            LoggingManager.getInstance().debug("Loaded item at: "+section.getNameAsString()+"."+uuid);
            LoggingManager.getInstance().debug("Material: " + itemStack);
        }
        itemManager.setItems(items);

    }

    private void onSave() {
        config.set("items", null); // Vymažeme dočasnou konfiguraci
        for (CustomItem customItem : itemManager.getItems()) {
            Rarity rarity = customItem.getRarity();
            String rarityName = rarity.getName();
            ItemStack itemStack = customItem.getItemStack();
            rarity.removeRarityFromItemStack(itemStack);
            UUID uuid = customItem.getId();
            config.set("items." + uuid + ".item", itemStack);
            config.set("items." + uuid + ".rarityName", rarityName);
        }
    }

    public YamlDocument getConfig() {
        return config;
    }

    public void reloadConfig(){
        this.loadConfig();
    }
}
