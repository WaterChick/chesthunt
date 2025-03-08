package dev.waterchick.chesthunt.gui;

import dev.waterchick.chesthunt.managers.LoggingManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

public abstract class CustomGUI {

    public abstract void onSetItems(Inventory inventory);

    public abstract boolean onClick(Player player, Inventory inventory, int slot, ClickType clickType);

    public abstract void onClose(Player player, Inventory inventory);

    public void openInventory(Player player) {
        LoggingManager.getInstance().debug("GUIEditorListener: opened to " + player.getName());
        Inventory inventory = createInventory();
        onSetItems(inventory);
        player.openInventory(inventory);
    }

    protected abstract Inventory createInventory();
}
