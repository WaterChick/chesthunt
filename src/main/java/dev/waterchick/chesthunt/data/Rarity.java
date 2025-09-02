package dev.waterchick.chesthunt.data;

import dev.waterchick.chesthunt.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Rarity {
    private final String name;
    private final String displayName;
    private final int chance;

    public Rarity(String name,String displayName, int chance){
        this.displayName = displayName;
        this.chance = chance;
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getChance() {
        return chance;
    }

    public String getName() {
        return name;
    }

    public void addRarityToItemStack(ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        List<String> originalLore = meta.getLore();
        if(originalLore == null || originalLore.isEmpty()){
            originalLore = new ArrayList<>();
        }
        originalLore.add(ChatUtils.color("&7Rarity: " + getDisplayName()));
        meta.setLore(originalLore);
        itemStack.setItemMeta(meta);
    }

    public void removeRarityFromItemStack(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> originalLore = meta.getLore();

        if (originalLore == null || originalLore.isEmpty()) {
            return;
        }

        List<String> updatedLore = new ArrayList<>();
        for (String line : originalLore) {
            if (!ChatColor.stripColor(line).startsWith("Rarity: ")) {
                updatedLore.add(line);
            }
        }

        meta.setLore(updatedLore);
        itemStack.setItemMeta(meta);
    }
}
