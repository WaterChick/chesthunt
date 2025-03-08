package dev.waterchick.chesthunt.managers;

import dev.waterchick.chesthunt.data.TreasureChest;
import dev.waterchick.chesthunt.data.CustomItem;
import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ChestHuntManager {

    private final ItemManager itemManager;
    private final JavaPlugin plugin;

    public ChestHuntManager(ItemManager itemManager, JavaPlugin plugin){
        this.itemManager = itemManager;
        this.plugin = plugin;
    }

    public TreasureChest getCurrentChest() {
        return currentChest;
    }

    public enum CHESTERROR{
        INVALIDWORLD("The specified world could not be found. Please check the world name in your configuration."),
        NOITEMSLOADED("No items were loaded. Please ensure that your items are properly configured and loaded.")
        ;

        private final String message;

        CHESTERROR(String message){
            this.message = message;
        }


        public void throwError(){
            LoggingManager.getInstance().getLogger().severe(this.name()+" | "+message);
        }
    }

    private TreasureChest currentChest = null;
    private final LoggingManager logger = LoggingManager.getInstance();

    public void start() {
        Random random = new Random();
        World world = Bukkit.getWorld(ConfigValue.WORLD.getValue());
        if (world == null) {
            CHESTERROR.INVALIDWORLD.throwError();
            return;
        }
        Collection<CustomItem> itemStacks = this.itemManager.getItems();
        if (itemStacks.isEmpty()) {
            CHESTERROR.NOITEMSLOADED.throwError();
            return;
        }
        int maxItems = Integer.parseInt(ConfigValue.MAX_ITEMS_IN_CHEST.getValue());
        if (itemStacks.size() < maxItems) {
            maxItems = itemStacks.size();
        }
        int numOfItemsInChest = random.nextInt(1, maxItems + 1);
        while (numOfItemsInChest > Math.min(itemStacks.size(), maxItems)) {
            numOfItemsInChest = random.nextInt(1, maxItems + 1);
        }
        logger.debug("Created a treasure chest: ");
        logger.debug("  Number of items: " + numOfItemsInChest);
        Bukkit.broadcastMessage(ConfigValue.MESSAGES_PREFIX.getValue() + ConfigValue.MESSAGES_CHESTHUNTBEGINS.getValue());
        scheduleChestRemoval(); // Naplánujeme odstranění truhly po uplynutí časového limitu
        Set<ItemStack> loot = new HashSet<>();
        List<CustomItem> itemList = new ArrayList<>(itemStacks); // Create a copy to avoid modifying the original collection
        for (int i = 0; i < numOfItemsInChest; i++) {
            CustomItem selectedItem = this.itemManager.getRandomItemWithRarity(random);
            if (selectedItem != null) {
                loot.add(selectedItem.getItemStack());
                itemList.remove(selectedItem); // Remove from the copy
            }
        }
        Location safeLocation = LocationUtils.getRandomSafeLocation(world);
        this.currentChest = new TreasureChest(safeLocation, loot.stream().toList());
        this.currentChest.createChestBlockWithContents();
        logger.debug("Chest spawned at " + safeLocation);
    }

    public void createRunnable() {
        int intervalMinutes = Integer.parseInt(ConfigValue.CHEST_HUNT_INTERVAL.getValue());
        int intervalTicks = intervalMinutes * 60 * 20; // Převod minut na ticky (20 ticků = 1 sekunda)

        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentChest == null) {
                    // Truhla neexistuje, takže vytvoříme novou
                    start();
                    logger.debug("New treasure chest spawned.");
                    return; // Ukončíme metodu, protože truhla byla vytvořena
                }
                logger.debug("Treasure chest exists. Time limit not reached yet.");
            }
        }.runTaskTimer(plugin, 0L, intervalTicks); // Spustit runnable každých intervalTicks
    }

    public void stop(){
        if(currentChest == null){
            return;
        }
        Location chestLocation = currentChest.getChestLocation();
        if(!currentChest.isFound()) {
            String prefix = ConfigValue.MESSAGES_PREFIX.getValue();
            String chestHuntNotFound = ConfigValue.MESSAGES_CHESTHUNTNOTFOUND.getValue();
            chestHuntNotFound = chestHuntNotFound.replace("{x}", chestLocation.getBlockX() + "")
                    .replace("{y}", chestLocation.getBlockY() + "")
                    .replace("{z}", chestLocation.getBlockZ() + "");
            Bukkit.broadcastMessage(prefix + chestHuntNotFound);
        }
        currentChest.removeChestBlock(); // Odstranit truhlu
        currentChest = null; // Nastavit currentChest na null
        logger.debug("Treasure chest despawned.");
    }

    private void scheduleChestRemoval() {
        int timeLimitMinutes = Integer.parseInt(ConfigValue.CHEST_HUNT_TIME.getValue());
        long timeLimitTicks = timeLimitMinutes * 60 * 20L; // Převod minut na ticky

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (currentChest != null) {
                stop(); // Použijeme metodu stop() pro odstranění truhly a další akce
            }
        }, timeLimitTicks);
    }


}
