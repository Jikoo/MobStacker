package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.ArrayList;
import java.util.List;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

public class SlimeLootAlgorithm extends LootAlgorithm {

    public SlimeLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.SLIME_BALL).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        if (!(entity instanceof Slime)) {
            return 0;
        }

        return (((Slime) entity).getSize() + 1) * numberOfMobs;
    }

    @Override
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        if (!(entity instanceof Slime)) {
            return new ArrayList<>();
        }

        if (((Slime) entity).getSize() == 0) {
            return super.getRandomLoot(entity, numberOfMobs, playerKill, looting);
        }

        return new ArrayList<>();
    }

    protected List<ItemStack> superLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        return super.getRandomLoot(entity, numberOfMobs, playerKill, looting);
    }

}
