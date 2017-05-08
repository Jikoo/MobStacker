package com.kiwifisher.mobstacker.loot.impl;

import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.IRandomChance;

/**
 * A class used to represent vanilla's random chance condition. To reduce load, Conditions are only
 * calculated once per ILootEntry. However, the random chance condition needs to be calculated once
 * per drop, so it cannot be handled the same way.
 * 
 * @author Jikoo
 */
public class RandomChance implements IRandomChance {

    @Expose
    private double chance, lootingModifier;

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

}
