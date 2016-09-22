package com.kiwifisher.mobstacker.algorithms.loot;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class Loot {

    private final Function<Entity, Material> materialFunction;
    private final Function<Entity, Short> dataFunction;
    private final int minimumQuantity, maxQuantity;
    private final double dropChance, lootingModifier;
    private final boolean lootingAddsResults, playerKillRequired;

    Loot(Function<Entity, Material> materialFunction, Function<Entity, Short> dataFunction,
            int minimumQuantity, int maxQuantity, double dropChance, double lootingModifier,
            boolean lootingAddsResults, boolean playerKillRequired) {
        this.materialFunction = materialFunction;
        this.dataFunction = dataFunction;
        this.minimumQuantity = minimumQuantity;
        this.maxQuantity = maxQuantity;
        this.dropChance = dropChance;
        this.lootingModifier = lootingModifier;
        this.lootingAddsResults = lootingAddsResults;
        this.playerKillRequired = playerKillRequired;
    }

    public Material getMaterial(Entity entity) {
        return this.materialFunction.apply(entity);
    }

    public short getData(Entity entity) {
        return this.dataFunction.apply(entity);
    }

    public int getMinimumQuantity() {
        return this.minimumQuantity;
    }

    public int getMaxQuantity() {
        return this.maxQuantity;
    }

    public double getDropChance(int looting) {
        return this.dropChance + lootingModifier * looting;
    }

    public boolean getLootingAddsResults() {
        return this.lootingAddsResults;
    }

    public boolean getPlayerKillRequired() {
        return this.playerKillRequired;
    }

}
