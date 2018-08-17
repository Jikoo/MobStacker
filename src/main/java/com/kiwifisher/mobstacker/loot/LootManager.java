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
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.loot.api.IExperiencePool;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.Lootable;
import org.bukkit.potion.PotionEffectType;

/**
 * Manages loot and experience for entities.
 *
 * @author Jikoo
 */
public class LootManager {

    private Map<String, Map<String, IExperiencePool>> experience = new HashMap<>();

    public LootManager(MobStacker plugin) {
        Gson gson = MobStacker.getGson();

        File file = new File(plugin.getDataFolder(), "experience.json");
        // Save default loot configuration if not present.
        if (!file.exists()) {
            plugin.saveResource("experience.json", false);
        }

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                experience = gson.fromJson(reader,
                        new TypeToken<Map<String, Map<String, IExperiencePool>>>() {}.getType());
            } catch (IOException | JsonParseException e) {
                e.printStackTrace();
            }
        }
    }

    public List<ItemStack> getLoot(Entity entity, Player killer, int numberOfMobs, int looting) {
        if (!(entity instanceof Lootable)) {
            return Collections.emptyList();
        }
        Lootable lootable = ((Lootable) entity);
        LootContext.Builder contextBuilder = new LootContext.Builder(entity.getLocation()).lootedEntity(entity).lootingModifier(looting);
        if (killer != null) {
            contextBuilder.killer(killer).luck(((float) killer.getAttribute(Attribute.GENERIC_LUCK).getValue()));
        }
        LootContext context = contextBuilder.build();
        List<ItemStack> loot = new ArrayList<>();
        for (int i = 0; i < numberOfMobs; ++i) {
            // TODO merge drops
            loot.addAll(lootable.getLootTable().populateLoot(ThreadLocalRandom.current(), context));
        }
        return loot;
    }

    public int getExperience(Entity entity, int numberOfMobs) {
        String entityName = getEntityName(entity);
        String worldName = entity.getWorld().getName().toUpperCase();
        Map<String, IExperiencePool> worldMappings;

        // Test world-specific experience settings.
        if (!experience.containsKey(worldName)
                || !(worldMappings = experience.get(worldName)).containsKey(entityName)) {
            // Fall through to default settings.
            worldName = "DEFAULT";
        }

        if (!experience.containsKey(worldName)
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
        default:
            return type.name();
        }
    }

}
