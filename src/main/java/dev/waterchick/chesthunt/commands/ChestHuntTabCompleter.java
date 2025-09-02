package dev.waterchick.chesthunt.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChestHuntTabCompleter implements TabCompleter {

    private final List<String> subcommands = Arrays.asList(
            "reload", "gui", "forcestart", "panic", "savedebug"
    );

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            List<String> suggestions = new ArrayList<>();
            for (String sub : subcommands) {
                if (sub.toLowerCase().startsWith(strings[0].toLowerCase())) {
                    suggestions.add(sub);
                }
            }
            return suggestions;
        }
        return Collections.emptyList();
    }


}
