package dev.waterchick.chesthunt.enums;

import java.util.List;

public enum ConfigValue {
    RADIUS("250"),
    WORLD("world"),
    WORLD_MAXY("65"),
    DEBUG("false"),
    MAX_ITEMS_IN_CHEST("3"),
    CHEST_HUNT_TIME("30"),
    BOSS_BAR("true"),
    PANIC("false"),
    CHEST_HUNT_SPAWN_DAYS(List.of("WEDNESDAY")),
    CHEST_HUNT_SPAWN_TIMES(List.of("12:00")),


    MESSAGES_PREFIX("&8[&6ChestHunt&8] &7"),
    MESSAGES_CONFIGRELOADED("&aConfig reloaded"),
    MESSAGES_NOPERMISSION("&cNo permission"),
    MESSAGES_MUSTBECREATIVEEDITOR("&cYou must be in CREATIVE mode to enter GUI editor."),
    MESSAGES_CHESTHUNTSTARTSIN("&aChest hunt will begin in {minutes} minutes! Get ready!"),

    MESSAGES_CHESTHUNTSTARTEDTITLE("&7Chest hunt"),
    MESSAGES_CHESTHUNTSTARTEDSUBTITLE("&astarted! Have Fun"),

    MESSAGES_CHESTHUNTBEGINS("&6⚔️ The Chest Hunt has begun! Search for hidden treasures! 🏆"),
    MESSAGES_CHESTHUNTFOUND("&a🎉 {player} has found a chest at &eX: {x} Y: {y} Z: {z}! &aThe treasure has been claimed! 🏅"),
    MESSAGES_CHESTHUNTNOTFOUND("&c❌ No chest was found at &eX: {x} Y: {y} Z: {z}. Better luck next time 🍀"),

    MESSAGES_CHESTHUNTFORCESTART("&aSuccessfully force started a ChestHunt!"),
    MESSAGES_ONLYPLAYER("&cOnly player may execute this command."),
    MESSAGES_PANIC("&aPanic mode enabled. Disabling plugin.."),
    MESSAGES_DEBUGSAVED("&aSuccessfully created a debug log file in the plugin data folder."),

    MESSAGES_PLACEHOLDERACTIVEYES("&a✔"),
    MESSAGES_PLACEHOLDERACTIVENO("&c✘"),

    MESSAGES_BOSSBARRUNNING("&6⏳ Chest Hunt is active! Time left: &e{time}"),
    MESSAGES_BOSSBARFOUND("&a✔ The treasure has been found! Well done!"),
    MESSAGES_BOSSBARTIMEOUT("&c✖ Time is up! Nobody found the chest."),

    MESSAGES_HELP(List.of(
            "&7Commands Help:",
            "  - &6/chesthunt reload &7- &eReload the plugin configuration files.",
            "  - &6/chesthunt gui &7- &eOpen the Chest Hunt GUI editor in Creative mode.",
            "  - &6/chesthunt forcestart &7- &eForce start a Chest Hunt event.",
            "  - &6/chesthunt panic &7- &eEnable panic mode to stop and disable the plugin.",
            "  - &6/chesthunt savedebug &7- &eSave a debug log file."
    ))


    ;
    private String value;
    private List<String> listValues;
    ConfigValue(String value){
        this.value = value;
    }

    ConfigValue(List<String> listValues){
        this.listValues = listValues;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public List<String> getListValues() {
        return listValues;
    }

    public void setListValues(List<String> listValues) {
        this.listValues = listValues;
    }
}
