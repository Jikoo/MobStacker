package com.kiwifisher.mobstacker.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract base for all entity loot algorithms.
 * 
 * @author KiwiFisher
 * @author Jikoo
 */
public abstract class LootAlgorithm {

    private final List<Loot> loot = new ArrayList<>();

    /**
     * Gets a list of basic loot.
     * 
     * @return the List
     */
    protected final List<Loot> getLootArray() {
        return this.loot;
    }

    /**
     * Gets the quantity of experience to drop for a number of entities.
     * 
     * @param entity the Entity killed
     * @param numberOfMobs the number of mobs
     * @return the quantity of experience
     */
    public abstract int getExp(Entity entity, int numberOfMobs);

    /**
     * Gets a list of items to drop when an entity is killed.
     * 
     * @param entity the Entity killed
     * @param numberOfMobs the number of entities in the stack
     * @param playerKill true if killed by a player
     * @param looting the looting level of the player's weapon
     * @return the items to drop
     */
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        List<ItemStack> drops = new ArrayList<>();

        for (Loot loot : getLootArray()) {
            // Rare loot requires a player kill.
            if (loot instanceof RareLoot && !playerKill) {
                continue;
            }

            int amount = this.getRandomQuantity(loot, numberOfMobs, looting);
            this.addDrops(drops, loot.getMaterial(entity), loot.getData(entity), amount);
        }

        return drops;
    }

    protected final int getRandomQuantity(Loot loot, int numberOfMobs, int looting) {
        int max = loot.getMaxQuantity();
        if (loot.lootingAddsResults()) {
            max += looting;
        }
        return getRandomQuantity(loot.getMinimumQuantity(), max, loot.getDropChance(looting), numberOfMobs);
    }

    protected final int getRandomQuantity(int min, int max, double dropChance, int numberOfMobs) {
        if (dropChance >= 1) {
            // Guaranteed drops
            min *= numberOfMobs;
            max = max * numberOfMobs - min;
            return ThreadLocalRandom.current().nextInt(max) + min;
        }

        // By design, minimum must be 0 (and can be ignored) if drop chance < 1
        int total = 0;
        // Total attempts to make = 1 per maximum drop quantity per mob
        numberOfMobs *= max;
        for (int i = 0; i < numberOfMobs; i++) {
            if (ThreadLocalRandom.current().nextDouble() < dropChance) {
                total++;
            }
        }
        return total;
    }

    protected final void addDrops(List<ItemStack> drops, Material material, short data, int amount) {
        // Drop max stacks, don't overstack.
        while (amount > 0) {
            if (amount > material.getMaxStackSize()) {
                drops.add(new ItemStack(material, material.getMaxStackSize(), data));
                amount -= material.getMaxStackSize();
            } else {
                drops.add(new ItemStack(material, amount, data));
                amount = 0;
            }
        }
    }

}
