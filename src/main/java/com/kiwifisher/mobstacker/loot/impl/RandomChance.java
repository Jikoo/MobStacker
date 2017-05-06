package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.loot.api.IRandomChance;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

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

    public void setChance(Double chance) {
        this.chance = chance;
    }

    public double getLootingModifier() {
        return lootingModifier;
    }

    public void setLootingModifier(Double lootingModifier) {
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

        SerializationUtils.load(randomChance, Double.class, "chance", serialization);
        SerializationUtils.load(randomChance, Double.class, "lootingModifier", serialization);

        return randomChance;
    }

}
