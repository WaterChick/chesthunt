package dev.waterchick.chesthunt.managers;

import dev.waterchick.chesthunt.Chesthunt;
import dev.waterchick.chesthunt.data.TreasureChest;
import dev.waterchick.chesthunt.enums.ConfigValue;
import dev.waterchick.chesthunt.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBarManager {

    private final Chesthunt plugin;
    private BossBar bossBar;
    private BukkitRunnable task;
    private final TreasureChest treasureChest;

    public BossBarManager(Chesthunt plugin, TreasureChest treasureChest) {
        this.plugin = plugin;
        this.treasureChest = treasureChest;
    }

    public void startBossBar(long durationSeconds) {

        if (bossBar != null) {
            bossBar.removeAll();
        }

        bossBar = Bukkit.createBossBar(
                ConfigValue.MESSAGES_BOSSBARRUNNING.getValue().replace("{time}", ChatUtils.formatTimeLeft(treasureChest.getSecondsLeft())),
                BarColor.YELLOW,
                BarStyle.SOLID
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        bossBar.setVisible(true);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                int left = treasureChest.getSecondsLeft();

                if (left <= 0 || treasureChest.isFound()) {
                    bossBar.setProgress(0);
                    if (treasureChest.isFound()) {
                        bossBar.setTitle(ConfigValue.MESSAGES_BOSSBARFOUND.getValue());
                    } else {
                        bossBar.setTitle(ConfigValue.MESSAGES_BOSSBARTIMEOUT.getValue());
                    }

                    cancel();

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            bossBar.setVisible(false);
                        }
                    }.runTaskLater(plugin, 60L); // skryj po 3 sekundách

                    return;
                }

                double progress = Math.max(0.0, Math.min(1.0, (double) left / durationSeconds));
                bossBar.setProgress(progress);
                bossBar.setTitle(ConfigValue.MESSAGES_BOSSBARRUNNING.getValue().replace("{time}",ChatUtils.formatTimeLeft(left)));
            }
        };

        task.runTaskTimer(plugin, 0L, 20L);
    }

    public void stopBossBar() {
        if (task != null) {
            task.cancel();
        }
        if (bossBar != null) {
            bossBar.setVisible(false);
            bossBar.removeAll();
        }
    }

    public void addPlayer(Player player) {
        if (bossBar != null && bossBar.isVisible()) {
            bossBar.addPlayer(player);
        }
    }
}
