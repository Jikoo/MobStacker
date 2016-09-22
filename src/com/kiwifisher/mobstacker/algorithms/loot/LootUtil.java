package com.kiwifisher.mobstacker.algorithms.loot;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

/**
 * 
 * 
 * @author Jikoo
 */
public class LootUtil {

    public static final double RARE_LOOT_CHANCE = 0.025;
    public static final double RARE_LOOT_MODIFER = 0.01;

    public static Function<Entity, Material> getCookableFunction(Material raw, Material cooked) {
        return new Function<Entity, Material>() {
            @Override
            public Material apply(Entity entity) {
                return entity.getFireTicks() > 0 ? cooked : raw;
            }
        };
    }

    private LootUtil() {}

}
