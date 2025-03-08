package dev.waterchick.chesthunt.managers;

import dev.waterchick.chesthunt.Rarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RarityManager {

    private List<Rarity> rarities = new ArrayList<>();

    public List<Rarity> getRarities() {
        return Collections.unmodifiableList(rarities);
    }

    public void setRarities(List<Rarity> rarities) {
        this.rarities = rarities;
    }

    public Optional<Rarity> getRarityByDisplayName(String displayName){
        return this.rarities.stream().filter(rarity -> rarity.getDisplayName().equalsIgnoreCase(displayName)).findAny();
    }

    public Optional<Rarity> getRarityByName(String name){
        return this.rarities.stream().filter(rarity -> rarity.getName().equalsIgnoreCase(name)).findAny();
    }

    public Rarity getDefaultRarity(){
        return this.rarities.getFirst();
    }

    public Rarity scrollRarity(Rarity currentRarity) {
        if (this.rarities == null || this.rarities.isEmpty()) {
            return null;
        }

        int currentIndex = this.rarities.indexOf(currentRarity);

        if (currentIndex == -1) {
            return this.rarities.getFirst();
        }

        int nextIndex = currentIndex + 1;

        if (nextIndex >= this.rarities.size()) {
            return this.rarities.getFirst();
        }
        return this.rarities.get(nextIndex);
    }

    public void print(){
        for(Rarity rarity : getRarities()){
            LoggingManager.getInstance().debug(rarity.getDisplayName() + " | Chance: "+rarity.getChance()+"%");
        }
    }
}
