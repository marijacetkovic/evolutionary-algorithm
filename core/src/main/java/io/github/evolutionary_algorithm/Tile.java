package io.github.evolutionary_algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tile {
    private List<Integer> creatures;
    private List<Food> foodList;

    public Tile() {
        this.creatures = new ArrayList<>();
        this.foodList = new ArrayList<>();
    }

    public void addCreature(int creatureId) {
        creatures.add(creatureId);
    }

    public void removeCreature(int creatureId) {
        creatures.remove(Integer.valueOf(creatureId));
    }

    public void addFood(Food f) {
        foodList.add(f);
    }

    public void removeFood(Food f) {
        foodList.remove(f);
    }

    public List<Integer> getCreatures() {
        return creatures;
    }

    public List<Food> getFoodItems() {
        return foodList;
    }

    public boolean hasFood() {
        return foodList.size() > 0;
    }

    public boolean hasCreature(int id) {
        return creatures.stream().anyMatch(creatureId -> creatureId != id);
    }

    public List<Integer> getOtherCreatures(int id){
        return creatures.stream().filter(c->c!=id).collect(Collectors.toList());
    }

}
