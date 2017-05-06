package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.ILootEntry;
import com.kiwifisher.mobstacker.loot.api.ILootPool;
import com.kiwifisher.mobstacker.loot.api.IRandomChance;
import com.kiwifisher.mobstacker.utils.ConditionUtils;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/**
 * A LootPool containing possible loot from an Entity.
 * 
 * @author Jikoo
 */
public class LootPool implements ILootPool {

    private int rollsMin, rollsMax, bonusRollsMin, bonusRollsMax;
    private List<ICondition> conditions;
    private List<ILootEntry> entries;
    private IRandomChance randomChance;

    public LootPool() {
        this.rollsMin = 1;
        this.rollsMax = 1;
        this.bonusRollsMin = 0;
        this.bonusRollsMax = 0;
    }

    public int getRollsMin() {
        return rollsMin;
    }

    public void setRollsMin(Integer rollsMin) {
        this.rollsMin = Math.min(0, rollsMin);
    }

    public int getRollsMax() {
        return rollsMax;
    }

    public void setRollsMax(Integer rollsMax) {
        this.rollsMax = Math.min(0, rollsMax);
    }

    public int getBonusRollsMin() {
        return bonusRollsMin;
    }

    public void setBonusRollsMin(Integer bonusRollsMin) {
        this.bonusRollsMin = bonusRollsMin;
    }

    public int getBonusRollsMax() {
        return bonusRollsMax;
    }

    public void setBonusRollsMax(Integer bonusRollsMax) {
        this.bonusRollsMax = bonusRollsMax;
    }

    public List<ILootEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ILootEntry> entries) {
        this.entries = entries;
    }

    @Override
    public List<ICondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<ICondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public IRandomChance getRandomChance() {
        return this.randomChance;
    }

    public void setRandomChance(IRandomChance randomChance) {
        this.randomChance = randomChance;
    }

    @Override
    public void roll(List<ItemStack> drops, Entity entity, int numberOfMobs, int looting) {
        if (this.entries == null || this.entries.isEmpty()
                || !ConditionUtils.meetsConditions(entity, this.conditions)) {
            return;
        }

        // Quantify rolls.
        int rolls = (rollsMin + bonusRollsMin) * numberOfMobs;
        int max = (rollsMax + bonusRollsMax) * numberOfMobs - rolls;
        if (max > 0) {
            rolls += ThreadLocalRandom.current().nextInt(max + 1);
        }

        // No rolls means no loot.
        if (rolls < 1) {
            return;
        }

        // Get weighted loot.
        TreeMap<Integer, ILootEntry> weightedEntries = this.getWeightedLoot(entity);

        // Ensure any entries qualify.
        if (weightedEntries.isEmpty()) {
            return;
        }

        int weightMax = weightedEntries.lastKey() + 1;
        Map<ILootEntry, Integer> selectedLoot = new HashMap<>();

        // Select loot based on weight.
        for (int i = 0; i < rolls; i++) {
            // Handle random chance.
            if (this.getRandomChance() != null && !this.getRandomChance().test(looting)) {
                continue;
            }

            // Select weighted loot.
            ILootEntry loot;
            if (weightedEntries.size() == 1) {
                loot = weightedEntries.firstEntry().getValue();
            } else {
                loot = weightedEntries.floorEntry(ThreadLocalRandom.current().nextInt(weightMax)).getValue();
            }

            // Increment selection count.
            selectedLoot.put(loot, selectedLoot.getOrDefault(loot, 0) + 1);
        }

        // Generate and add loot.
        for (Entry<ILootEntry, Integer> loot : selectedLoot.entrySet()) {
            loot.getKey().generateLoot(drops, loot.getValue(), entity, looting);
        }

    }

    private TreeMap<Integer, ILootEntry> getWeightedLoot(Entity entity) {
        TreeMap<Integer, ILootEntry> weightedEntries = new TreeMap<>();

        // Ensure loot is set, just in case.
        if (this.entries == null || this.entries.isEmpty()) {
            return weightedEntries;
        }

        // Get Luck stat weight modifier if present.
        Entity killer = null;
        if (entity instanceof LivingEntity) {
            killer = ((LivingEntity) entity).getKiller();
        }
        double attributeWeight = 0;
        if (killer instanceof Attributable) {
            AttributeInstance attribute = ((Attributable) killer).getAttribute(Attribute.GENERIC_LUCK);
            if (attribute != null) {
                attributeWeight = attribute.getValue();
            }
        }

        int currentWeightMax = 0;
        for (ILootEntry entry : this.entries) {
            // Ensure weight allows for the entry to be selected.
            int weight = entry.getWeight() + (int) (attributeWeight * entry.getQuality());
            if (weight < 1) {
                continue;
            }

            // Ensure conditions are met.
            if (!ConditionUtils.meetsConditions(entity, entry.getConditions())) {
                continue;
            }

            weightedEntries.put(currentWeightMax, entry);
            currentWeightMax += weight;
        }

        return weightedEntries;
    }

    @Override
    public Map<String, Object> serialize() {
        LootPool defaults = new LootPool();
        Map<String, Object> serialization = new HashMap<>();

        if (this.rollsMin != defaults.getRollsMin()) {
            serialization.put("rollsMin", this.rollsMin);
        }
        if (this.rollsMax != defaults.getRollsMax()) {
            serialization.put("rollsMax", this.rollsMax);
        }
        if (this.bonusRollsMin != defaults.getBonusRollsMin()) {
            serialization.put("bonusRollsMin", this.bonusRollsMin);
        }
        if (this.bonusRollsMax != defaults.getBonusRollsMax()) {
            serialization.put("bonusRollsMax", this.bonusRollsMax);
        }
        if (this.conditions != null) {
            serialization.put("conditions", this.conditions);
        }
        if (this.entries != null) {
            serialization.put("entries", this.entries);
        }
        if (this.randomChance != null) {
            serialization.put("randomChance", this.randomChance);
        }

        return serialization;
    }

    public static LootPool deserialize(Map<String, Object> serialization) {
        LootPool pool = new LootPool();

        SerializationUtils.load(pool, Integer.class, "rollsMin", serialization);
        SerializationUtils.load(pool, Integer.class, "rollsMax", serialization);
        SerializationUtils.load(pool, Integer.class, "bonusRollsMin", serialization);
        SerializationUtils.load(pool, Integer.class, "bonusRollsMax", serialization);
        SerializationUtils.load(pool, IRandomChance.class, "randomChance", serialization);
        SerializationUtils.loadList(pool, ICondition.class, "conditions", serialization);
        SerializationUtils.loadList(pool, ILootEntry.class, "entries", serialization);

        return pool;
    }

}
