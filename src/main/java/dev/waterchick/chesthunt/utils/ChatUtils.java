package dev.waterchick.chesthunt.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {

    public static String color(String message){
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    public static List<String> color(List<String> lines){
        List<String> coloredList = new ArrayList<>();
        for(String line : lines){
            line = color(line);
            coloredList.add(line);
        }
        return coloredList;
    }

    public static String formatTimeLeft(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");

        return sb.toString().trim();
    }
}
