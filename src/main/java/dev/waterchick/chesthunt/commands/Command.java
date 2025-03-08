package dev.waterchick.chesthunt.commands;

import dev.waterchick.chesthunt.Chesthunt;
import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.gui.GUIEditor;
import dev.waterchick.chesthunt.managers.ChestHuntManager;
import dev.waterchick.chesthunt.managers.LoggingManager;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Command implements CommandExecutor {

    private final Chesthunt plugin;
    private final ChestHuntManager chestHuntManager;
    private final GUIEditor guiEditor;

    public Command(Chesthunt plugin, ChestHuntManager chestHuntManager, GUIEditor guiEditor){
        this.plugin = plugin;
        this.chestHuntManager = chestHuntManager;
        this.guiEditor = guiEditor;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] args) {
        String prefix = ConfigValue.MESSAGES_PREFIX.getValue();
        if(args.length != 1){
            //HELP MESSAGE
            return false;
        }
        if(args[0].equalsIgnoreCase("reload")){
            if(!sender.hasPermission("chesthunt.reload")){
                sender.sendMessage(prefix+ConfigValue.MESSAGES_NOPERMISSION.getValue());
                return false;
            }
            this.plugin.reloadConfigs();
            sender.sendMessage(prefix+ConfigValue.MESSAGES_CONFIGRELOADED.getValue());
            return true;
        }
        if(args[0].equalsIgnoreCase("gui")){
            if(!sender.hasPermission("chesthunt.gui")){
                sender.sendMessage(prefix+ConfigValue.MESSAGES_NOPERMISSION.getValue());
                return false;
            }
            if(!(sender instanceof Player player)){
                sender.sendMessage(prefix+ConfigValue.MESSAGES_ONLYPLAYER.getValue());
                return false;
            }
            if(player.getGameMode() != GameMode.CREATIVE){
                player.sendMessage(prefix+ConfigValue.MESSAGES_MUSTBECREATIVEEDITOR.getValue());
                return false;
            }
            guiEditor.openInventory(player);
            return true;
        }
        if(args[0].equalsIgnoreCase("forcestart")){
            if(!sender.hasPermission("chesthunt.forcestart")){
                sender.sendMessage(prefix+ConfigValue.MESSAGES_NOPERMISSION.getValue());
                return false;
            }
            this.chestHuntManager.start();
            sender.sendMessage(prefix+ConfigValue.MESSAGES_CHESTHUNTFORCESTART.getValue());
            return true;
        }
        if(args[0].equalsIgnoreCase("panic")){
            if(!sender.hasPermission("chesthunt.panic")){
                sender.sendMessage(prefix+ConfigValue.MESSAGES_NOPERMISSION.getValue());
                return false;
            }
            sender.sendMessage(prefix+ConfigValue.MESSAGES_PANIC.getValue());
            plugin.panic();
            return true;
        }
        if(args[0].equalsIgnoreCase("savedebug")){
            if(!sender.hasPermission("chesthunt.savedebug")){
                sender.sendMessage(prefix+ConfigValue.MESSAGES_NOPERMISSION.getValue());
                return false;
            }
            sender.sendMessage(prefix+ConfigValue.MESSAGES_DEBUGSAVED.getValue());
            LoggingManager.getInstance().saveDebug(null, true);
            return true;
        }

        return true;
    }
}
