package dev.waterchick.chesthunt.listeners;

import dev.waterchick.chesthunt.data.TreasureChest;
import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.managers.ChestHuntManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestHuntListener implements Listener {
    private final ChestHuntManager chestHuntManager;

    public ChestHuntListener(ChestHuntManager chestHuntManager){
        this.chestHuntManager = chestHuntManager;
    }

    @EventHandler
    public void ChestFound(PlayerInteractEvent event){
        Player player = event.getPlayer();
        String name = player.getName();
        TreasureChest treasureChest = chestHuntManager.getCurrentChest();
        if(treasureChest == null){
            return;
        }
        Block block = event.getClickedBlock();
        if(block == null){
            return;
        }
        Location blockLocation = block.getLocation();
        Location chestLocation = treasureChest.getChestLocation();
        if(chestLocation == null){
            return;
        }
        if(!blockLocation.equals(chestLocation)){
            return;
        }
        if(treasureChest.isFound()){
            return;
        }
        String prefix = ConfigValue.MESSAGES_PREFIX.getValue();
        String chestHuntFound = ConfigValue.MESSAGES_CHESTHUNTFOUND.getValue();
        chestHuntFound = chestHuntFound.replace("{player}", name).replace("{x}", chestLocation.getBlockX()+"").replace("{y}", chestLocation.getBlockY()+"").replace("{z}", chestLocation.getBlockZ()+"");
        Bukkit.broadcastMessage(prefix+ chestHuntFound);
        treasureChest.setFound(true);
    }

    @EventHandler
    public void DespawnChest(InventoryCloseEvent event){
        if (!(event.getInventory().getHolder() instanceof Chest)) {
            return; // Není to truhla, ukončíme metodu
        }

        Chest chest = (Chest) event.getInventory().getHolder();
        Location chestLocation = chest.getLocation();
        TreasureChest currentChest = chestHuntManager.getCurrentChest();
        if (chestHuntManager.getCurrentChest() == null || !currentChest.getChestLocation().equals(chestLocation)) {
            return; // Není to naše truhla, ukončíme metodu
        }

        Inventory inventory = event.getInventory();
        if (!isInventoryEmpty(inventory)) {
            return; // Truhla není prázdná, ukončíme metodu
        }

        currentChest.removeChestBlock();
    }

    private boolean isInventoryEmpty(Inventory inventory) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false; // Truhla obsahuje itemy, není prázdná
            }
        }
        return true; // Truhla je prázdná
    }
}
