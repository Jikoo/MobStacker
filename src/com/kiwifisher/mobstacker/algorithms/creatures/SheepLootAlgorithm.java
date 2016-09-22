package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.loot.Function;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;
import com.kiwifisher.mobstacker.algorithms.loot.LootUtil;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;

public class SheepLootAlgorithm extends AnimalLootAlgorithm {

    public SheepLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(
                LootUtil.getCookableFunction(Material.MUTTON, Material.COOKED_MUTTON))
                        .withMinimum(1).withMaximum(2).withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.WOOL).withMinimum(1)
                .withDataFunction(new Function<Entity, Short>() {
                    @Override
                    public Short apply(Entity entity) {
                        if (!(entity instanceof Sheep)) {
                            return 0;
                        }
                        return (short) ((Sheep) entity).getColor().getWoolData();
                    }
                }).toLoot());
    }

}
