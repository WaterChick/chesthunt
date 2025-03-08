package dev.waterchick.chesthunt.configs;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.spigot.SpigotSerializer;
import dev.waterchick.chesthunt.Rarity;
import dev.waterchick.chesthunt.data.CustomItem;
import dev.waterchick.chesthunt.managers.LoggingManager;
import dev.waterchick.chesthunt.managers.ItemManager;
import dev.waterchick.chesthunt.managers.RarityManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
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
        }
        itemManager.setItems(items);

    }

    /**
     * Saves all custom items to the configuration file.
     * This method is designed to be robust and prevent data loss in case of errors during the save process.
     * It uses a temporary configuration file to store the data first, and only replaces the original configuration
     * if the save operation is successful. This ensures that the original data is preserved in case of any issues.
     *
     * The process involves the following steps:
     * 1. Create a temporary configuration file.
     * 2. Store all custom items in the temporary configuration.
     * 3. Save the temporary configuration to disk.
     * 4. If successful, replace the original configuration with the temporary configuration.
     * 5. Delete the temporary file.
     *
     * In case of any exceptions during the process, the original configuration is left unchanged,
     * and the temporary file is deleted to prevent any inconsistencies.
     */
    private void onSave() {
        File tempConfigFile = new File(dataFolder, "temp_items.yml");
        YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(tempConfigFile);

        try {
            tempConfig.set("items", null); // Vymažeme dočasnou konfiguraci
            for (CustomItem customItem : itemManager.getItems()) {
                Rarity rarity = customItem.getRarity();
                String rarityName = rarity.getName();
                ItemStack itemStack = customItem.getItemStack();
                rarity.removeRarityFromItemStack(itemStack);
                UUID uuid = customItem.getId();
                tempConfig.set("items." + uuid + ".item", itemStack);
                tempConfig.set("items." + uuid + ".rarityName", rarityName);
            }

            tempConfig.save(tempConfigFile); // Uložíme dočasnou konfiguraci

            // Nahradíme původní konfiguraci dočasnou konfigurací
            this.config.set("items", tempConfig.get("items"));

            // Smažeme dočasný soubor
            tempConfigFile.delete();

        } catch (IOException e) {
            logger.severe("Error saving data: " + e.getMessage());
            e.printStackTrace();
            // Ponecháme původní konfiguraci beze změn
            if (tempConfigFile.exists()) {
                tempConfigFile.delete(); // Smažeme dočasný soubor, pokud existuje
            }
        }
    }

    public YamlDocument getConfig() {
        return config;
    }

    public void reloadConfig(){
        this.loadConfig();
    }
}
