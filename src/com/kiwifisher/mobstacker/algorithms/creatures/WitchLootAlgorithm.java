package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class WitchLootAlgorithm extends LootAlgorithm {

    public WitchLootAlgorithm() {
        this.getLootArray().add(new Loot(Material.GLASS_BOTTLE, 2));
        this.getLootArray().add(new Loot(Material.GLOWSTONE_DUST, 2));
        this.getLootArray().add(new Loot(Material.SULPHUR, 2));
        this.getLootArray().add(new Loot(Material.REDSTONE, 2));
        this.getLootArray().add(new Loot(Material.SPIDER_EYE, 2));
        this.getLootArray().add(new Loot(Material.SUGAR, 2));
        // Stick is entered twice because it has a 2/8 drop chance for easier weighted averaging
        this.getLootArray().add(new Loot(Material.STICK, 2));
        this.getLootArray().add(new Loot(Material.STICK, 2));
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
        double averageDropTypes = ThreadLocalRandom.current().nextDouble(numberOfMobs, numberOfMobs * 3) / numberOfMobs / 8;

        List<ItemStack> drops = new ArrayList<>();

        for (Loot loot : getLootArray()) {
            int amount = (int) (this.getRandomQuantity(loot, numberOfMobs, looting) * averageDropTypes);
            this.addDrops(drops, loot.getMaterial(entity), loot.getData(entity), amount);
        }

        return drops;
    }

}
