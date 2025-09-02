package dev.waterchick.chesthunt.data;

public class PlayerData {

    private int foundTreasureChests = 0;


    public int getFoundTreasureChests() {
        return foundTreasureChests;
    }

    public void setFoundTreasureChests(int foundTreasureChests) {
        this.foundTreasureChests = foundTreasureChests;
    }

    public void addFoundTreasureChests(){
        this.foundTreasureChests++;
    }
}
