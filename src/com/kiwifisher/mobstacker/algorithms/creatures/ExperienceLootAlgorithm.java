package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;

import org.bukkit.entity.Entity;

/**
 * Generic LootAlgorithm for entities that do not have drops.
 * 
 * @author Jikoo
 */
public class ExperienceLootAlgorithm extends LootAlgorithm {

    private final int minimum, maximum;

    public ExperienceLootAlgorithm(int maximum) {
        this(0, maximum);
    }


    public ExperienceLootAlgorithm(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        if (maximum > minimum) {
            return ThreadLocalRandom.current().nextInt(minimum * numberOfMobs, maximum * numberOfMobs);
        } else {
            return maximum * numberOfMobs;
        }
    }

}
