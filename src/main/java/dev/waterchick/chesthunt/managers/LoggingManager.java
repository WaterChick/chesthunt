package dev.waterchick.chesthunt.managers;

import dev.waterchick.chesthunt.enums.ConfigValue;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LoggingManager {

    private static LoggingManager instance;

    private final Logger logger;
    private final File dataFolder;

    private final List<String> debugMessages = new ArrayList<>();

    public boolean isDebug(){
        return ConfigValue.DEBUG.getValue().equalsIgnoreCase("true");
    }

    public LoggingManager(JavaPlugin plugin){
        this.logger = plugin.getLogger();
        this.dataFolder = plugin.getDataFolder();
    }

    public static LoggingManager getInstance() {
        return instance;
    }

    public static void initialize(JavaPlugin plugin){
        instance = new LoggingManager(plugin);
    }

    public void debug(String message){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        String formattedMessage = formattedDateTime + ": " + message;
        debugMessages.add(formattedMessage);
        if(isDebug()) this.logger.info("DEBUG: " + message);
    }

    public void print(String message){
        this.logger.info(message);
    }

    public void saveDebug(@Nullable String prefix, boolean force){
        if(prefix == null){
            prefix = "debug";
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedDateTime = now.format(formatter);

        File logsFolder = new File(dataFolder, "logs");
        if (!logsFolder.exists()) {
            if (logsFolder.mkdirs()) {
                logger.info("Created logs folder: " + logsFolder.getAbsolutePath());
            } else {
                logger.severe("Could not create logs folder!");
                return;
            }
        }
        File logFile = new File(logsFolder, prefix+"-" + formattedDateTime + ".log");
        if(!isDebug() && !force){
            return;
        }
        if(!debugMessages.isEmpty()) {
            try (FileWriter writer = new FileWriter(logFile)) {
                for (String message : getDebugMessages()) {
                    writer.write(message + "\n");
                }

            } catch (IOException e) {
                logger.severe("Error writing to log file: " + e.getMessage());
                e.printStackTrace();

            }
            logger.info("Debug messages have been saved to " + logFile.getAbsolutePath());
        }
        debugMessages.clear();
    }

    public Logger getLogger() {
        return logger;
    }

    public List<String> getDebugMessages() {
        return debugMessages;
    }
}
