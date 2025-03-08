package dev.waterchick.chesthunt.data;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TreasureChest {

    private final Location chestLocation;
    private final List<ItemStack> chestItems;
    private final long millisStarted = System.currentTimeMillis();
    private boolean found;

    public TreasureChest(Location chestLocation, List<ItemStack> chestItems){
        this.chestLocation = chestLocation;
        this.chestItems = chestItems;
    }


    public Location getChestLocation() {
        return chestLocation;
    }

    public long getMillisLeft() {
        return System.currentTimeMillis() - millisStarted;
    }

    public void createChestBlockWithContents(){
        Block block = chestLocation.getBlock();
        block.setType(Material.CHEST);
        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getBlockInventory();
        for(ItemStack itemStack : chestItems){
            System.out.println(itemStack);
        }
        System.out.println(Arrays.stream(inventory.getContents()).filter(Objects::nonNull).count());
        System.out.println(inventory.getSize());
        Random random = new Random();
        for (ItemStack itemStack : chestItems) {
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
