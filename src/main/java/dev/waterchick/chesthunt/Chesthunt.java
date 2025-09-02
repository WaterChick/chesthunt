package dev.waterchick.chesthunt;

import dev.waterchick.chesthunt.commands.ChestHuntTabCompleter;
import dev.waterchick.chesthunt.commands.Command;
import dev.waterchick.chesthunt.configs.*;
import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.gui.GUIEditor;
import dev.waterchick.chesthunt.hooks.PAPIHook;
import dev.waterchick.chesthunt.listeners.ChestHuntListener;
import dev.waterchick.chesthunt.listeners.GUIEditorListener;
import dev.waterchick.chesthunt.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Chesthunt extends JavaPlugin {

    private MainConfig mainConfig;
    private ItemsConfig itemsConfig;
    private ItemManager itemManager;
    private RarityManager rarityManager;
    private RarityConfig rarityConfig;
    private MessageConfig messageConfig;
    private ChestHuntManager chestHuntManager;
    private PlayerDataConfig playerDataConfig;


    private static NamespacedKey itemIdKey;

    public static NamespacedKey getItemIdKey() {
        return itemIdKey;
    }

    @Override
    public void onEnable() {
        LoggingManager.initialize(this);
        itemIdKey = new NamespacedKey(this, "item_id");

        this.mainConfig = new MainConfig(this.getDataFolder());
        this.mainConfig.loadConfig();

        if(ConfigValue.PANIC.getValue().equalsIgnoreCase("true")){
            LoggingManager.getInstance().getLogger().severe("Plugin disabled due to PANIC mode. If you think the problem has been fixed, set panic mode to false in config.yml");
            disable();
            return;
        }

        this.messageConfig = new MessageConfig(this.getDataFolder());
        this.messageConfig.loadConfig();

        this.rarityManager = new RarityManager();
        this.rarityConfig = new RarityConfig(this.getDataFolder(), rarityManager);
        this.rarityConfig.loadConfig();
        this.rarityManager.print();

        this.itemManager = new ItemManager();
        this.itemsConfig = new ItemsConfig(this.getDataFolder(),itemManager, rarityManager);
        this.itemsConfig.loadConfig();

        PlayerManager.initialize();

        this.playerDataConfig = new PlayerDataConfig(this.getDataFolder());
        this.playerDataConfig.loadConfig();


        this.chestHuntManager = new ChestHuntManager(itemManager, this);

        GUIEditor guiEditor = new GUIEditor(itemManager, rarityManager);
        GUIEditorListener guiEditorListener = new GUIEditorListener(guiEditor);

        PluginCommand command =  this.getCommand("chesthunt");
        if(command != null){
             command.setExecutor(new Command(this,chestHuntManager, guiEditor));
             command.setTabCompleter(new ChestHuntTabCompleter());
        }

        this.getServer().getPluginManager().registerEvents(guiEditorListener, this);
        this.getServer().getPluginManager().registerEvents(new ChestHuntListener(chestHuntManager, this), this);

        chestHuntManager.createRunnable();
        initHooks();
    }

    @Override
    public void onDisable() {
        if(itemsConfig != null) {
            this.itemsConfig.saveConfig();
        }
        if(mainConfig != null) {
            this.mainConfig.reloadConfig();
            this.mainConfig.saveConfig();
        }
        if(playerDataConfig != null){
            this.playerDataConfig.saveConfig();
        }
        this.chestHuntManager.stop();
        LoggingManager loggingManager = LoggingManager.getInstance();
        loggingManager.saveDebug(null, false);
    }

    public void reloadConfigs(){
        this.mainConfig.reloadConfig();
        this.rarityConfig.reloadConfig();
        this.messageConfig.reloadConfig();
    }

    public void panic() {
        LoggingManager loggingManager = LoggingManager.getInstance();
        loggingManager.saveDebug("panic", true);
        ConfigValue.PANIC.setValue("true");
        disable();
    }

    private void disable(){
        Bukkit.getPluginManager().disablePlugin(this);
    }

    private void initHooks(){
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {

            new PAPIHook(this, chestHuntManager).register();
            LoggingManager.getInstance().print("Hooked to PlaceholderAPI");
        }
    }
}
