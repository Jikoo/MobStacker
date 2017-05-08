package com.kiwifisher.mobstacker.loot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.loot.api.IExperiencePool;
import com.kiwifisher.mobstacker.loot.api.ILootPool;

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

    private Map<String, Map<String, Collection<ILootPool>>> loot = new HashMap<>();
    private Map<String, Map<String, IExperiencePool>> experience = new HashMap<>();

    public LootManager(MobStacker plugin) {
        File file = new File(plugin.getDataFolder(), "loot.json");
        // Save default loot configuration if not present.
        // For some reason, Bukkit always logs when not saving due to existence, which is annoying.
        if (!file.exists()) {
            plugin.saveResource("loot.json", false);
        }

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                loot = MobStacker.getGson().fromJson(reader,
                        new TypeToken<Map<String, Map<String, Collection<ILootPool>>>>() {}.getType());
            } catch (IOException | JsonParseException e) {
                e.printStackTrace();
            }
        }

        file = new File(plugin.getDataFolder(), "experience.json");
        // Save default loot configuration if not present.
        if (!file.exists()) {
            plugin.saveResource("experience.json", false);
        }

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                experience = MobStacker.getGson().fromJson(reader,
                        new TypeToken<Map<String, Map<String, IExperiencePool>>>() {}.getType());
            } catch (IOException | JsonParseException e) {
                e.printStackTrace();
            }
        }
    }

    public List<ItemStack> getLoot(Entity entity, int numberOfMobs, int looting) {
        String entityName = getEntityName(entity);
        String worldName = entity.getWorld().getName().toUpperCase();
        Map<String, Collection<ILootPool>> worldMappings;

        // Test world-specific loot and default settings.
        if (!loot.containsKey(worldName)
                || !(worldMappings = loot.get(worldName)).containsKey(entityName)
                || !loot.containsKey(worldName = "DEFAULT")
                || !(worldMappings = loot.get(worldName)).containsKey(entityName)) {
            return Collections.emptyList();
        }

        List<ItemStack> drops = new ArrayList<>();
        for (ILootPool pool : worldMappings.get(entityName)) {
            pool.roll(drops, entity, numberOfMobs, looting);
        }
        return drops;
    }

    public int getExperience(Entity entity, int numberOfMobs) {
        String entityName = getEntityName(entity);
        String worldName = entity.getWorld().getName().toUpperCase();
        Map<String, IExperiencePool> worldMappings;

        // Test world-specific experience and default settings.
        if (!experience.containsKey(worldName)
                || !(worldMappings = experience.get(worldName)).containsKey(entityName)
                || !experience.containsKey(worldName = "DEFAULT")
                || !(worldMappings = experience.get(worldName)).containsKey(entityName)) {
            return 0;
        }

        return worldMappings.get(entityName).getExperience(entity, numberOfMobs);
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
