package dev.waterchick.chesthunt.gui;

import dev.waterchick.chesthunt.Rarity;
import dev.waterchick.chesthunt.data.CustomItem;
import dev.waterchick.chesthunt.managers.ItemManager;
import dev.waterchick.chesthunt.managers.LoggingManager;
import dev.waterchick.chesthunt.managers.RarityManager;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GUIEditor extends CustomGUI {

    private final ItemManager itemManager;
    private final RarityManager rarityManager;

    public GUIEditor(ItemManager itemManager, RarityManager rarityManager) {
        this.itemManager = itemManager;
        this.rarityManager = rarityManager;
    }

    @Override
    public void onSetItems(Inventory inventory) {
        List<CustomItem> items = itemManager.getItems();
        for (int i = 0; i < items.size(); i++) {
            CustomItem customItem = items.get(i);
            ItemStack itemStack = customItem.getItemStack();
            Rarity rarity = customItem.getRarity();
            rarity.addRarityToItemStack(itemStack);
            inventory.setItem(i, itemStack);
        }
    }

    @Override
    public boolean onClick(Player player, Inventory inventory, int slot, ClickType clickType) {
        if (clickType == ClickType.MIDDLE) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack == null) {
                return true;
            }

            UUID itemId = CustomItem.getItemIdFromItemStack(itemStack);
            if (itemId == null) {
                return true; // Pokud UUID není nastaveno, nic neděláme
            }

            CustomItem customItem = itemManager.getItemById(itemId).orElse(null);
            if (customItem == null) {
                return true; // Pokud CustomItem neexistuje, nic neděláme
            }

            Rarity currentRarity = customItem.getRarity();
            currentRarity.removeRarityFromItemStack(itemStack);
            Rarity newRarity = rarityManager.scrollRarity(currentRarity);
            customItem.setRarity(newRarity);
            newRarity.addRarityToItemStack(itemStack);

            LoggingManager.getInstance().debug("ItemStack:" + itemStack);
            LoggingManager.getInstance().debug("Changed rarity from " + currentRarity.getName() + " to " + newRarity.getName());

            inventory.setItem(slot, itemStack);
            return true;
        }
        return false;
    }

    @Override
    public void onClose(Player player, Inventory inventory) {
        List<CustomItem> updatedItems = new ArrayList<>();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null) continue;

            UUID itemId = CustomItem.getItemIdFromItemStack(itemStack);

            if (itemId == null) {
                // Nový ItemStack bez PDC
                CustomItem newItem = new CustomItem(itemStack, rarityManager.getDefaultRarity());
                CustomItem.setItemIdToItemStack(itemStack, newItem.getId());

                updatedItems.add(newItem);
            } else {
                // Existující ItemStack s PDC
                Optional<CustomItem> optionalCustomItem = itemManager.getItemById(itemId);
                if (optionalCustomItem.isPresent()) {
                    CustomItem customItem = optionalCustomItem.get();
                    Rarity rarity = customItem.getRarity();
                    rarity.removeRarityFromItemStack(itemStack);
                    customItem.setItemStack(itemStack);
                    updatedItems.add(customItem);
                }else{
                    // Item s UUID, ale nebyl v původním listu - může se stát, pokud byl přidán během editace a pak prohozen
                    CustomItem newItem = new CustomItem(itemStack, rarityManager.getDefaultRarity());
                    CustomItem.setItemIdToItemStack(itemStack, newItem.getId());
                    updatedItems.add(newItem);
                }
            }
        }

        itemManager.setItems(updatedItems);
    }


    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(new CustomInventoryHolder(), 54, "GUI Editor");
    }
}