package dev.waterchick.chesthunt.utils;

import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.managers.LoggingManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.function.Consumer;

public class LocationUtils {

    private static final int ATTEMPTS_PER_TICK = 50;

    public static void findRandomSafeLocation(World world, JavaPlugin plugin, Consumer<Location> callback) {
        int radius = Integer.parseInt(ConfigValue.RADIUS.getValue());
        Random random = new Random();
        Location spawn = world.getSpawnLocation();

        new BukkitRunnable() {
            int totalAttempts = 0;

            @Override
            public void run() {
                for (int i = 0; i < ATTEMPTS_PER_TICK; i++) {
                    int x = spawn.getBlockX() + random.nextInt(2 * radius) - radius;
                    int z = spawn.getBlockZ() + random.nextInt(2 * radius) - radius;
                    int y = world.getHighestBlockYAt(x, z);

                    Block block = world.getBlockAt(x, y, z);
                    Block blockAbove = world.getBlockAt(x, y + 1, z);

                    totalAttempts++;

                    if (block.getType().isSolid() && block.getType() != Material.BARRIER && blockAbove.getType() == Material.AIR) {
                        LoggingManager.getInstance().debug("Found a safe location after: " + totalAttempts + " attempts.");
                        cancel();
                        callback.accept(blockAbove.getLocation());
                        return;
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
