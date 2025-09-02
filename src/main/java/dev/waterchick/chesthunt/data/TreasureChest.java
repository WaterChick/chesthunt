package dev.waterchick.chesthunt.data;


import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.managers.LoggingManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class TreasureChest {

    private final Location chestLocation;
    private final List<CustomItem> chestItems;
    private final long millisStarted = System.currentTimeMillis();
    private final LoggingManager loggingManager = LoggingManager.getInstance();
    private boolean found;

    public TreasureChest(Location chestLocation, List<CustomItem> chestItems){
        this.chestLocation = chestLocation;
        this.chestItems = chestItems;
    }


    public Location getChestLocation() {
        return chestLocation;
    }

    public int getSecondsLeft() {
        int durationMillis = Integer.parseInt(ConfigValue.CHEST_HUNT_TIME.getValue()) * 60000;
        long timeLeft = durationMillis - (System.currentTimeMillis() - millisStarted);
        return (int) Math.max(0, timeLeft / 1000);
    }

    public void createChestBlockWithContents(){
        Block block = chestLocation.getBlock();
        block.setType(Material.CHEST);
        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getBlockInventory();
        chestItems.forEach(customItem -> customItem.getRarity().removeRarityFromItemStack(customItem.getItemStack()));
        loggingManager.debug("Chest spawned with following items: ");
        for(CustomItem customItem : chestItems){
            loggingManager.debug(customItem.getItemStack().toString());
        }
        loggingManager.debug("Chest spawned at: " + chestLocation);
        Random random = new Random();
        for (CustomItem customItem : chestItems) {
            ItemStack itemStack = customItem.getItemStack();
            int randomIndex = random.nextInt(0, inventory.getSize());
            while(inventory.getItem(randomIndex) != null){
                 randomIndex = random.nextInt(0, inventory.getSize());
            }
            inventory.setItem(randomIndex, itemStack);
        }
    }

    public void removeChestBlock(){
        Block block = chestLocation.getBlock();
        if(block.getType() == Material.AIR){
            return;
        }
        block.setType(Material.AIR);
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }
}
