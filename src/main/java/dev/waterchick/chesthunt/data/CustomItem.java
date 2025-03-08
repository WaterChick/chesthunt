package dev.waterchick.chesthunt.data;

import dev.waterchick.chesthunt.Chesthunt;
import dev.waterchick.chesthunt.Rarity;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class CustomItem {

    private final UUID uuid;
    private ItemStack itemStack;
    private Rarity rarity;

    public CustomItem(ItemStack itemStack, Rarity rarity){
        this.itemStack = itemStack;
        this.rarity = rarity;
        this.uuid = UUID.randomUUID();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public UUID getId() {
        return uuid;
    }

    public static void setItemIdToItemStack(ItemStack itemStack, UUID id) {
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(Chesthunt.getItemIdKey(), PersistentDataType.STRING, id.toString());
        itemStack.setItemMeta(meta);
    }

    public static UUID getItemIdFromItemStack(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String itemIdString = container.get(Chesthunt.getItemIdKey(), PersistentDataType.STRING);
        if (itemIdString != null) {
            return UUID.fromString(itemIdString);
        }
        return null;
    }

}
