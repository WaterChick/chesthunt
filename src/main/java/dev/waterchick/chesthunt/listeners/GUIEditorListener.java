package dev.waterchick.chesthunt.listeners;

import dev.waterchick.chesthunt.gui.CustomInventoryHolder;
import dev.waterchick.chesthunt.gui.GUIEditor;
import dev.waterchick.chesthunt.managers.LoggingManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class GUIEditorListener implements Listener {

    private final GUIEditor editor;

    public GUIEditorListener(GUIEditor editor) {
        this.editor = editor;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && clickedInventory.getHolder() instanceof CustomInventoryHolder) {
            LoggingManager.getInstance().debug("GUIEditorListener: clicked");
            boolean cancelled = editor.onClick((Player) event.getWhoClicked(), event.getInventory(), event.getSlot(), event.getClick());
            if(cancelled) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof CustomInventoryHolder) {
            LoggingManager.getInstance().debug("GUIEditorListener: closed");
            editor.onClose((Player) event.getPlayer(), event.getInventory());
        }
    }
}
