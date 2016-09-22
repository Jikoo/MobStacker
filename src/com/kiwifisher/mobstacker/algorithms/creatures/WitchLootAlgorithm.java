package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.Loot;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class WitchLootAlgorithm extends LootAlgorithm {

    public WitchLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.GLASS_BOTTLE).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.GLOWSTONE_DUST).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.SULPHUR).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.REDSTONE).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.SPIDER_EYE).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.SUGAR).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
        // Stick is entered twice because it has a 2/8 drop chance for easier weighted averaging
        this.getLootArray().add(new LootBuilder(Material.STICK).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.STICK).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 5 * numberOfMobs;
    }

    @Override
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        /*
         * Each witch is only supposed to drop 1-3 of each of the types of item. Rather than
         * calculate that out, we calculate the random average number of results and then multiply
         * the results by that number. May result in weird drops for small stacks.
         */
        // TODO: check math
        double averageDropTypes = ThreadLocalRandom.current().nextDouble(numberOfMobs, numberOfMobs * 3) / numberOfMobs / 8;

        List<ItemStack> drops = new ArrayList<>();

        for (Loot loot : getLootArray()) {
            int amount = (int) (this.getRandomQuantity(loot, numberOfMobs, looting) * averageDropTypes);
            this.addDrops(drops, loot.getMaterial(entity), loot.getData(entity), amount);
        }

        return drops;
    }

}
