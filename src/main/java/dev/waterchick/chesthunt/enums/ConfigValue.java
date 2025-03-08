package dev.waterchick.chesthunt.enums;

public enum ConfigValue {
    RADIUS("250"),
    WORLD("world"),
    DEBUG("false"),
    MAX_ITEMS_IN_CHEST("3"),
    CHEST_HUNT_INTERVAL("60"),
    CHEST_HUNT_TIME("30"),
    PANIC("false"),

    MESSAGES_PREFIX("&8[&6ChestHunt&8] &7"),
    MESSAGES_CONFIGRELOADED("&aConfig reloaded"),
    MESSAGES_NOPERMISSION("&cNo permission"),
    MESSAGES_MUSTBECREATIVEEDITOR("&cYou must be in CREATIVE mode to enter GUI editor."),

    MESSAGES_CHESTHUNTBEGINS("&6⚔️ The Chest Hunt has begun! Search for hidden treasures! 🏆"),
    MESSAGES_CHESTHUNTFOUND("&a🎉 {player} has found a chest at &eX: {x} Y: {y} Z: {z}! &aThe treasure has been claimed! 🏅"),
    MESSAGES_CHESTHUNTNOTFOUND("&c❌ No chest was found at &eX: {x} Y: {y} Z: {z}. Better luck next time 🍀"),

    MESSAGES_CHESTHUNTFORCESTART("&aSuccessfully force started a ChestHunt!"),
    MESSAGES_ONLYPLAYER("&cOnly player may execute this command."),
    MESSAGES_PANIC("&aPanic mode enabled. Disabling plugin.."),
    MESSAGES_DEBUGSAVED("&aSuccessfully created a debug log file in the plugin data folder.")
    ;



    ;
    private String value;
    ConfigValue(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
