package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Base for all passive entities.
 * 
 * @author Jikoo
 */
public class AnimalLootAlgorithm extends LootAlgorithm {

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        // Babies drop no exp
        if (entity instanceof Ageable && !((Ageable) entity).isAdult()) {
            return 0;
        }
        // Adults drop 1-3 exp per
        return ThreadLocalRandom.current().nextInt(numberOfMobs, numberOfMobs * 3);
    }

    @Override
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        // Babies drop nothing.
        if (entity instanceof Ageable && !((Ageable) entity).isAdult()) {
            return new ArrayList<>();
        }
        return super.getRandomLoot(entity, numberOfMobs, playerKill, looting);
    }

}
