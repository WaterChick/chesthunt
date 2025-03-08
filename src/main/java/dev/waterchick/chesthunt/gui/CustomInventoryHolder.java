package dev.waterchick.chesthunt.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CustomInventoryHolder implements InventoryHolder {
    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }
}
