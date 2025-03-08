package dev.waterchick.chesthunt.utils;

import dev.waterchick.chesthunt.enums.ConfigValue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class LocationUtils {

    public static Location getRandomSafeLocation(World world) {
        Random random = new Random();
        Location location = null;
        Location spawn = world.getSpawnLocation();
        int radius = Integer.parseInt(ConfigValue.RADIUS.getValue());
        while(location == null) {

            int x = spawn.getBlockX() + random.nextInt(2 * radius) - radius;
            int z = spawn.getBlockZ() + random.nextInt(2 * radius) - radius;
            int y = world.getHighestBlockYAt(x, z);

            if (y < 0) {
                y = 0;
            } else if (y > 319) {
                y = 319;
            }

            Block block = world.getBlockAt(x, y, z);
            Block blockAbove = world.getBlockAt(x, y+1, z);

            if (block.getType().isSolid() && blockAbove.getType() == Material.AIR) {
                location = blockAbove.getLocation();
            }
        }
        return location;
    }
}
