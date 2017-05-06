package com.kiwifisher.mobstacker.loot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.loot.api.IExperiencePool;
import com.kiwifisher.mobstacker.loot.api.ILootPool;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.ZombieVillager;
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
        // For some reason, Bukkit always logs when not saving due to existence, which is annoying.
        if (!new File(plugin.getDataFolder(), "loot.yml").exists()) {
            plugin.saveResource("loot.yml", false);
        }

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
        if (!new File(plugin.getDataFolder(), "experience.yml").exists()) {
            plugin.saveResource("experience.yml", false);
        }

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

        File testDump = new File(plugin.getDataFolder(), "LOOTDUMP.yml");
        YamlConfiguration testConfiguration = new YamlConfiguration();
        for (Map.Entry<String, Collection<ILootPool>> entry : loot.entrySet()) {
            testConfiguration.set(entry.getKey(), entry.getValue());
        }
        try {
            testConfiguration.save(testDump);
        } catch (IOException e) {
            e.printStackTrace();
        }

        testDump = new File(plugin.getDataFolder(), "EXPDUMP.yml");
        testConfiguration = new YamlConfiguration();
        for (Entry<String, IExperiencePool> entry : experience.entrySet()) {
            testConfiguration.set(entry.getKey(), entry.getValue());
        }
        try {
            testConfiguration.save(testDump);
        } catch (IOException e) {
            e.printStackTrace();
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
        case PIG_ZOMBIE:
            return "ZOMBIE_PIGMAN";
        case ZOMBIE_VILLAGER:
            if (!(entity instanceof ZombieVillager)) {
                return type.name();
            }
            Profession profession = ((ZombieVillager) entity).getVillagerProfession();
            if (profession == null) {
                return type.name();
            }
            switch (profession) {
            case HUSK:
                return "HUSK";
            default:
                return "ZOMBIE_VILLAGER";
            }
        case EVOKER:
            return "EVOCATION_ILLAGER";
        case VINDICATOR:
            return "VINDICATION_ILLAGER";
        default:
            return type.name();
        }
    }

}
