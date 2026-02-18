package dev.waterchick.chesthunt.utils;

import dev.waterchick.chesthunt.enums.ConfigValue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class LocationUtils {

    public static Location getRandomSafeLocation(World world) {
        int radius = Integer.parseInt(ConfigValue.RADIUS.getValue());
        Random random = new Random();
        Location spawn = world.getSpawnLocation();

        for (int attempt = 0; attempt < 150; attempt++) {
            int x = spawn.getBlockX() + random.nextInt(2 * radius) - radius;
            int z = spawn.getBlockZ() + random.nextInt(2 * radius) - radius;
            int y = world.getHighestBlockYAt(x, z);

            Block block = world.getBlockAt(x, y, z);
            Block blockAbove = world.getBlockAt(x, y + 1, z);

            if (block.getType().isSolid() && block.getType() != Material.BARRIER && blockAbove.getType() == Material.AIR) {
                return blockAbove.getLocation(); // nalezena bezpečná pozice
            }
        }

        return null;
    }
}
