package com.kiwifisher.mobstacker.loot;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.loot.api.ExperiencePool;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Manages loot and experience for entities.
 *
 * @author Jikoo
 */
public class LootManager {

    private Map<String, Map<String, ExperiencePool>> experience = new HashMap<>();

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
                        new TypeToken<Map<String, Map<String, ExperiencePool>>>() {}.getType());
            } catch (IOException | JsonParseException e) {
                e.printStackTrace();
            }
        }
    }

    public List<ItemStack> getLoot(LivingEntity entity, int numberOfMobs) {
        if (!(entity instanceof Lootable)) {
            return Collections.emptyList();
        }
        Lootable lootable = ((Lootable) entity);
        LootTable lootTable = lootable.getLootTable();
        if (lootTable == null) {
            return Collections.emptyList();
        }
        LootContext.Builder contextBuilder = new LootContext.Builder(entity.getLocation())
//                .lootedEntity(entity).lootingModifier(looting) // Appears to be a Spigot issue with loot
                ;
        Player killer = entity.getKiller();
        if (killer != null && false) { // Appears to be a Spigot issue with loot
            contextBuilder.killer(killer);
            AttributeInstance attributeLuck = killer.getAttribute(Attribute.GENERIC_LUCK);
            if (attributeLuck != null) {
                contextBuilder.luck(((float) attributeLuck.getValue()));
            }
        }
        LootContext context = contextBuilder.build();
        List<ItemStack> loot = new ArrayList<>();
        for (int i = 0; i < numberOfMobs; ++i) {
            // TODO merge drops
            loot.addAll(lootTable.populateLoot(ThreadLocalRandom.current(), context));
        }
        return loot;
    }

    public int getExperience(Entity entity, int numberOfMobs) {
        if (entity == null || numberOfMobs < 1) {
            return 0;
        }

        String entityName = entity.getType().getKey().toString();
        String worldName = entity.getWorld().getName().toUpperCase();
        Map<String, ExperiencePool> worldMappings;

        // World-specific experience settings.
        if (!experience.containsKey(worldName)
                || !(worldMappings = experience.get(worldName)).containsKey(entityName)) {
            // Fall through to default settings.
            if (!experience.containsKey("DEFAULT")
                    || !(worldMappings = experience.get("DEFAULT")).containsKey(entityName)) {
                return 0;
            }
        }


        return worldMappings.get(entityName).getExperience(entity, numberOfMobs);
    }

}
