package dev.waterchick.chesthunt.managers;

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

}
