package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.algorithms.CookableLoot;
import com.kiwifisher.mobstacker.algorithms.Loot;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class RabbitLootAlgorithm extends AnimalLootAlgorithm {

    public RabbitLootAlgorithm() {
        this.getLootArray().add(new CookableLoot(Material.RABBIT, Material.COOKED_RABBIT, 0, 1, false));
        this.getLootArray().add(new Loot(Material.RABBIT_HIDE, 1));
    }

    @Override
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        List<ItemStack> drops = super.getRandomLoot(entity, numberOfMobs, playerKill, looting);

        // 10 + 3% per level looting chance of a rabbit's foot.
        double dropChance = (10 + looting * 3) / 100.0;
        int amount = (int) (ThreadLocalRandom.current().nextInt((int) (numberOfMobs / dropChance)) * dropChance);

        this.addDrops(drops, Material.RABBIT_FOOT, (short) 0, amount);

        return drops;
    }

}
