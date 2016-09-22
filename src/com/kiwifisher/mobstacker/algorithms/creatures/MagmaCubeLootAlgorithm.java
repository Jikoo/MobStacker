package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.ArrayList;
import java.util.List;

import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

/**
 * LootAlgorithm for Magma Cubes.
 * 
 * @author Jikoo
 */
public class MagmaCubeLootAlgorithm extends SlimeLootAlgorithm {

    public MagmaCubeLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.MAGMA_CREAM).withMinimum(-2)
                .withAdditionalLootingResults().toLoot());
    }

    @Override
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        if (!(entity instanceof Slime)) {
            return new ArrayList<>();
        }

        if (((Slime) entity).getSize() != 1) {
            return this.superLoot(entity, numberOfMobs, playerKill, looting);
        }

        return new ArrayList<>();
    }

}
