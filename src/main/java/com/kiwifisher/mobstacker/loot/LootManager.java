package com.kiwifisher.mobstacker.loot;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.loot.api.ExperiencePool;
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
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

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
        LootContext.Builder contextBuilder = new LootContext.Builder(entity.getLocation()).lootedEntity(entity);
        Player killer = entity.getKiller();
        if (killer != null) {
            contextBuilder.killer(killer).lootingModifier(killer.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS));
            AttributeInstance attributeLuck = killer.getAttribute(Attribute.GENERIC_LUCK);
            if (attributeLuck != null) {
                contextBuilder.luck(((float) attributeLuck.getValue()));
            }
        }
        LootContext context = contextBuilder.build();
        Map<ItemStack, Integer> drops = new HashMap<>();
        try {
            for (int i = 0; i < numberOfMobs; ++i) {
                lootTable.populateLoot(ThreadLocalRandom.current(), context).forEach(itemStack -> {
                    if (itemStack.getType() == Material.AIR) {
                        return;
                    }
                    int amount = itemStack.getAmount();
                    itemStack.setAmount(1);
                    drops.compute(itemStack, (key, value) -> {
                        if (value == null) {
                            value = 0;
                        }
                        return value + amount;
                    });
                });
            }
        } catch (Exception e) {
            System.out.println("Unable to populate loot for " + entity.getType().name()
                    + " using assigned loot table " + lootTable.getKey().toString());
            e.printStackTrace();
            System.out.println("Attempting to fall back on context-free loot.");
            try {
                lootTable.populateLoot(ThreadLocalRandom.current(),
                        new LootContext.Builder(entity.getLocation()).build()).forEach(itemStack -> {
                    if (itemStack.getType() == Material.AIR) {
                        return;
                    }
                    int amount = itemStack.getAmount();
                    itemStack.setAmount(1);
                    drops.compute(itemStack, (key, value) -> {
                        if (value == null) {
                            value = 0;
                        }
                        return value + amount;
                    });
                });
                System.out.println("Context-free loot succeeded.");
            } catch (Exception e1) {
                System.out.println("Context-free loot failed.");
            }
        }
        List<ItemStack> loot = new ArrayList<>();
        drops.forEach((itemStack, integer) -> {
            while (integer > 0) {
                ItemStack drop = itemStack.clone();
                if (integer > itemStack.getType().getMaxStackSize()) {
                    drop.setAmount(itemStack.getType().getMaxStackSize());
                } else {
                    drop.setAmount(integer);
                }
                loot.add(drop);
                integer -= drop.getAmount();
            }
        });
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
