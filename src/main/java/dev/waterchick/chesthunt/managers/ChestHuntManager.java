package dev.waterchick.chesthunt.managers;

import dev.waterchick.chesthunt.Chesthunt;
import dev.waterchick.chesthunt.data.CustomItem;
import dev.waterchick.chesthunt.data.TreasureChest;
import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ChestHuntManager {

    private final ItemManager itemManager;
    private final Chesthunt plugin;

    private TreasureChest currentChest = null;
    private final LoggingManager logger = LoggingManager.getInstance();
    private BossBarManager bossBarManager;

    public ChestHuntManager(ItemManager itemManager, Chesthunt plugin){
        this.itemManager = itemManager;
        this.plugin = plugin;
    }

    public TreasureChest getCurrentChest() {
        return currentChest;
    }

    enum CHESTERROR{
        INVALIDWORLD("The specified world could not be found. Please check the world name in your configuration."),
        NOITEMSLOADED("No items were loaded. Please ensure that your items are properly configured and loaded."),
        COULDNTFINDLOCATION("Could not find a safe chest location. Please ensure that WORLD_MAXY is set correctly and allows safe spawning.")
        ;

        private final String message;

        CHESTERROR(String message){
            this.message = message;
        }


        public void throwError(){
            LoggingManager.getInstance().getLogger().severe(this.name()+" | "+message);
        }
    }

    public void start() {
        Random random = new Random();
        World world = Bukkit.getWorld(ConfigValue.WORLD.getValue());
        if (world == null) {
            CHESTERROR.INVALIDWORLD.throwError();
            return;
        }
        List<CustomItem> itemStacks = this.itemManager.getItems();
        if (itemStacks.isEmpty()) {
            CHESTERROR.NOITEMSLOADED.throwError();
            return;
        }
        Location safeLocation = LocationUtils.getRandomSafeLocation(world);
        if(safeLocation == null){
            CHESTERROR.COULDNTFINDLOCATION.throwError();
            return;
        }
        int maxItemsInChest = Integer.parseInt(ConfigValue.MAX_ITEMS_IN_CHEST.getValue());
        int numOfItemsInChest = random.nextInt(1, Math.min(itemStacks.size(), maxItemsInChest) + 1); // Počet itemů v truhle nemůže být větší než počet dostupných itemů

        logger.debug("New treasure chest spawned.");
        logger.debug("Created a treasure chest: ");
        logger.debug("  Number of items to select: " + numOfItemsInChest);

        Bukkit.broadcastMessage(ConfigValue.MESSAGES_PREFIX.getValue() + ConfigValue.MESSAGES_CHESTHUNTBEGINS.getValue());
        for(Player player : Bukkit.getOnlinePlayers()){
            player.sendTitle(ConfigValue.MESSAGES_CHESTHUNTSTARTEDTITLE.getValue(), ConfigValue.MESSAGES_CHESTHUNTSTARTEDSUBTITLE.getValue(), 20, 60, 20);
        }

        int durationSeconds = Integer.parseInt(ConfigValue.CHEST_HUNT_TIME.getValue()) * 60;

        scheduleChestRemoval(durationSeconds);



        List<CustomItem> sortedItems = new ArrayList<>(itemStacks);
        // Sort items by their rarity
        sortedItems.sort(Comparator.comparingInt((CustomItem customItem) -> customItem.getRarity().getChance()).reversed());

        List<CustomItem> weightedList = new ArrayList<>();
        for (CustomItem item : sortedItems) {
            int chance = item.getRarity().getChance();
            for (int i = 0; i < chance; i++) {
                weightedList.add(item);
            }
        }
        List<CustomItem> chestItems = new ArrayList<>();
        List<CustomItem> tempWeightedList = new ArrayList<>(weightedList);
        Set<UUID> selectedIds = new HashSet<>();
        while (chestItems.size() < numOfItemsInChest && !tempWeightedList.isEmpty()) {
            int randomIndex = random.nextInt(tempWeightedList.size());
            CustomItem selectedItem = tempWeightedList.remove(randomIndex);
            if (selectedIds.add(selectedItem.getId())) {
                chestItems.add(selectedItem);
            }
        }
        this.currentChest = new TreasureChest(safeLocation, chestItems);
        this.currentChest.createChestBlockWithContents();
        if(isBossBarEnabled()) {
            this.bossBarManager = new BossBarManager(plugin, currentChest);
            this.bossBarManager.startBossBar(durationSeconds);
        }
    }

    public void createRunnable() {
        List<String> days = ConfigValue.CHEST_HUNT_SPAWN_DAYS.getListValues();
        List<String> times = ConfigValue.CHEST_HUNT_SPAWN_TIMES.getListValues();

        List<LocalTime> spawnTimes = times.stream()
                .map(t -> LocalTime.parse(t, DateTimeFormatter.ofPattern("HH:mm")))
                .toList();

        new BukkitRunnable() {
            @Override
            public void run() {
                LocalDateTime now = LocalDateTime.now();
                DayOfWeek currentDay = now.getDayOfWeek();

                LocalTime currentTime = now.toLocalTime().truncatedTo(ChronoUnit.MINUTES);

                boolean dayMatches = days.stream()
                        .anyMatch(day -> day.equalsIgnoreCase(currentDay.name()));

                if (!dayMatches) return;

                for (LocalTime spawnTime : spawnTimes) {
                    long minutesUntil = Duration.between(currentTime, spawnTime).toMinutes();

                    if (minutesUntil == 30 || minutesUntil == 10 || minutesUntil == 1) {
                        String msgTemplate = ConfigValue.MESSAGES_CHESTHUNTSTARTSIN.getValue();
                        String message = msgTemplate.replace("{minutes}", String.valueOf(minutesUntil));
                        Bukkit.broadcastMessage(ConfigValue.MESSAGES_PREFIX.getValue() + message);
                    } else if (minutesUntil == 0) {
                        if (currentChest == null) {
                            start();
                        } else {
                            logger.debug("Treasure chest exists. Time limit not reached yet.");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 60); // každou minutu
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
        if(isBossBarEnabled()) {
            this.bossBarManager.stopBossBar();
            bossBarManager = null;
        }
        logger.debug("Treasure chest despawned.");
    }

    private void scheduleChestRemoval(int durationSeconds) {

        long timeLimitTicks = durationSeconds * 20L; // Převod minut na ticky

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (currentChest != null) {
                stop(); // Použijeme metodu stop() pro odstranění truhly a další akce
            }
        }, timeLimitTicks);
    }

    public boolean isRunning(){
        return (currentChest != null && !currentChest.isFound());
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public boolean isBossBarEnabled(){
        return ConfigValue.BOSS_BAR.getValue().equalsIgnoreCase("true");
    }


}
