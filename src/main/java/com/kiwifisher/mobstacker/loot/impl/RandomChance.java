package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.loot.api.IRandomChance;

/**
 * A class used to represent vanilla's random chance condition. To reduce load, Conditions are only
 * calculated once per ILootEntry. However, the random chance condition needs to be calculated once
 * per drop, so it cannot be handled the same way.
 * 
 * @author Jikoo
 */
public class RandomChance implements IRandomChance {

    private double chance;
    private double lootingModifier;

    public RandomChance() {
        this.chance = 1;
        this.lootingModifier = 0;
    }

    @Override
    public boolean test(int looting) {
        return ThreadLocalRandom.current().nextDouble() < this.chance + lootingModifier * looting;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public double getLootingModifier() {
        return lootingModifier;
    }

    public void setLootingModifier(double lootingModifier) {
        this.lootingModifier = lootingModifier;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = new HashMap<>();
        serialization.put("chance", this.getChance());
        serialization.put("lootingModifier", this.getLootingModifier());
        return serialization;
    }

    public static RandomChance deserialize(Map<String, Object> serialization) {
        RandomChance randomChance = new RandomChance();

        if (serialization.containsKey("chance")) {
            Object chance = serialization.get("chance");
            if (double.class.isAssignableFrom(chance.getClass())) {
                randomChance.setChance((double) chance);
            }
        }

        if (serialization.containsKey("lootingModifier")) {
            Object lootingModifier = serialization.get("lootingModifier");
            if (double.class.isAssignableFrom(lootingModifier.getClass())) {
                randomChance.setLootingModifier((double) lootingModifier);
            }
        }

        return randomChance;
    }

}
