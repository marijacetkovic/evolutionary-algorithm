package io.github.evolutionary_algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tile {
    private List<Integer> creatures;
    private List<Integer> foodItems;

    public Tile() {
        this.creatures = new ArrayList<>();
        this.foodItems = new ArrayList<>();
    }

    public void addCreature(int creatureId) {
        creatures.add(creatureId);
    }

    public void removeCreature(int creatureId) {
        creatures.remove(Integer.valueOf(creatureId));
    }

    public void addFood(int foodCode) {
        foodItems.add(foodCode);
    }

    public void removeFood(int foodCode) {
        foodItems.remove(Integer.valueOf(foodCode));
    }

    public List<Integer> getCreatures() {
        return creatures;
    }

    public List<Integer> getFoodItems() {
        return foodItems;
    }

    public boolean hasFood() {
        return foodItems.size() > 0;
    }

    public boolean hasCreature(int id) {
        return creatures.stream().anyMatch(creatureId -> creatureId != id);
    }

    public List<Integer> getOtherCreatures(int id){
        return creatures.stream().filter(c->c!=id).collect(Collectors.toList());
    }

}
