package com.kiwifisher.mobstacker.loot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.loot.api.IExperiencePool;
import com.kiwifisher.mobstacker.loot.api.ILootPool;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

/**
 * Manages loot and experience for entities.
 * 
 * @author Jikoo
 */
public class LootManager {

    private final Map<String, Collection<ILootPool>> loot = new HashMap<>();
    private final Map<String, IExperiencePool> experience = new HashMap<>();

    public LootManager(MobStacker plugin) {
        // Save default loot configuration if not present.
        plugin.saveResource("loot.yml", false);

        // Load loot configuration.
        YamlConfiguration lootConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "loot.yml"));

        /*
         * Load default and world configurations into memory. While the YamlConfiguration does
         * internally handle that for us on demand, using it causes a mess of casting, especially
         * for collections.
         */
        for (String world : lootConfig.getKeys(false)) {

            ConfigurationSection entityConfig = lootConfig.getConfigurationSection(world);
            world = world.toUpperCase();

            for (String entity : entityConfig.getKeys(false)) {
                Object poolsObject = entityConfig.get(entity);
                entity = entity.toUpperCase();

                if (!(poolsObject instanceof List<?>)) {
                    continue;
                }

                List<?> poolsList = (List<?>) poolsObject;
                ArrayList<ILootPool> pools = new ArrayList<>();
                for (Object pool : poolsList) {
                    if (pool instanceof ILootPool) {
                        pools.add((ILootPool) pool);
                    }
                }

                loot.put(world + "." + entity, pools);
            }
        }

        // Save default experience configuration if not present.
        plugin.saveResource("experience.yml", false);

        // Load experience configuration.
        YamlConfiguration experienceConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "experience.yml"));

        // Load default and world configurations.
        for (String world : experienceConfig.getKeys(false)) {

            ConfigurationSection entityConfig = experienceConfig.getConfigurationSection(world);
            world = world.toUpperCase();

            for (String entity : entityConfig.getKeys(false)) {
                Object pool = entityConfig.get(entity);
                entity = entity.toUpperCase();

                if (pool instanceof IExperiencePool) {
                    experience.put(world + "." + entity, (IExperiencePool) pool);
                }
            }
        }
    }

    public List<ItemStack> getLoot(Entity entity, int numberOfMobs, int looting) {
        String name = "." + getEntityName(entity);
        String key = entity.getWorld().getName().toUpperCase() + name;

        // Test world-specific loot and default settings.
        if (!loot.containsKey(key) && !loot.containsKey(key = "DEFAULT" + name)) {
            return Collections.emptyList();
        }

        List<ItemStack> drops = new ArrayList<>();
        for (ILootPool pool : loot.get(key)) {
            pool.roll(drops, entity, numberOfMobs, looting);
        }
        return drops;
    }

    public int getExperience(Entity entity, int numberOfMobs) {
        String name = "." + getEntityName(entity);
        String key = entity.getWorld().getName().toUpperCase() + name;

        // Test world-specific loot and default settings.
        if (!experience.containsKey(key) && !experience.containsKey(key = "DEFAULT" + name)) {
            return 0;
        }

        return experience.get(key).getExperience(entity, numberOfMobs);
    }

    private String getEntityName(Entity entity) {
        EntityType type = entity.getType();
        switch (type) {
        case GUARDIAN:
            if (entity instanceof Guardian && ((Guardian) entity).isElder()) {
                return "ELDER_GUARDIAN";
            }
            return type.name();
        case HORSE:
            if (!(entity instanceof Horse)) {
                return type.name();
            }
            Variant variant = ((Horse) entity).getVariant();
            switch (variant) {
            case UNDEAD_HORSE:
                return "ZOMBIE_HORSE";
            default:
                return variant.name();
            }
        case PIG_ZOMBIE:
            return "ZOMBIE_PIGMAN";
        case SKELETON:
            if (!(entity instanceof Skeleton)) {
                return type.name();
            }
            SkeletonType skeletonType = ((Skeleton) entity).getSkeletonType();
            switch (skeletonType) {
            case WITHER:
                return "WITHER_SKELETON";
            case STRAY:
                return "STRAY";
            default:
                return type.name();
            }
        case ZOMBIE:
            if (!(entity instanceof Zombie)) {
                return type.name();
            }
            Profession profession = ((Zombie) entity).getVillagerProfession();
            if (profession == null) {
                return type.name();
            }
            switch (profession) {
            case HUSK:
                return "HUSK";
            default:
                return "ZOMBIE_VILLAGER";
            }
        default:
            return type.name();
        }
    }

}
