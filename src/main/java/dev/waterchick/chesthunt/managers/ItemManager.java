package dev.waterchick.chesthunt.managers;

import dev.waterchick.chesthunt.Rarity;
import dev.waterchick.chesthunt.data.CustomItem;

import java.util.*;

public class ItemManager {
    private List<CustomItem> items = new ArrayList<>();

    public void setItems(List<CustomItem> items){
        this.items = items;
    }

    public List<CustomItem> getItems() {
        return new ArrayList<>(items);
    }

    public Optional<CustomItem> getItemById(UUID uuid){
        return this.items.stream().filter(item -> item.getId().equals(uuid)).findAny();
    }

    public CustomItem getRandomItemWithRarity(Random random) {
        // Vytvoření seznamu s váhami podle rarity

        List<CustomItem> weightedItems = new ArrayList<>();
        for (CustomItem item : this.items) {
            // Získání rarity a její šance
            Rarity itemRarity = item.getRarity();
            int chance = itemRarity.getChance();

            // Podle šance rarity přidáme položku do seznamu opakovaně
            for (int j = 0; j < chance; j++) {
                weightedItems.add(item);  // Přidáváme item podle jeho šance
            }
        }

        // Náhodný výběr položky z váženého seznamu
        return weightedItems.get(random.nextInt(weightedItems.size()));
    }

}
