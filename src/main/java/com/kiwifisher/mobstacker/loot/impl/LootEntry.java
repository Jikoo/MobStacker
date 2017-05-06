package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.IFunction;
import com.kiwifisher.mobstacker.loot.api.ILootEntry;
import com.kiwifisher.mobstacker.loot.api.IRandomChance;
import com.kiwifisher.mobstacker.loot.api.LootData;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Implementation of ILootEntry. Handles generation of loot for a specific LootPool result.
 * 
 * @author Jikoo
 */
public class LootEntry implements ILootEntry {

    private Material material;
    private int minimumQuantity, maximumQuantity, weight, quality;
    private List<ICondition> conditions;
    private List<IFunction> functions;
    private IRandomChance randomChance;
    private boolean hasVariableFunctions;

    public LootEntry() {
        this.material = Material.AIR;
        this.minimumQuantity = 1;
        this.maximumQuantity = 1;
        this.weight = 1;
        this.quality = 0;
        this.hasVariableFunctions = false;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        if (material == null) {
            this.material = Material.AIR;
        } else {
            this.material = material;
        }
    }

    @Override
    public int getMinimumQuantity() {
        return this.minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    @Override
    public int getMaximumQuantity() {
        return this.maximumQuantity;
    }

    public void setMaximumQuantity(Integer maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public double getQuality() {
        return this.quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    @Override
    public List<ICondition> getConditions() {
        return this.conditions;
    }

    public void setConditions(List<ICondition> conditions) {
        this.conditions = conditions;
    }

    public List<IFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<IFunction> functions) {
        this.functions = functions;

        if (functions == null) {
            this.hasVariableFunctions = false;
            return;
        }

        for (IFunction function : functions) {
            if (function.isVariable()) {
                this.hasVariableFunctions = true;
                return;
            }
        }
        this.hasVariableFunctions = false;
    }

    @Override
    public IRandomChance getRandomChance() {
        return this.randomChance;
    }

    public void setRandomChance(IRandomChance randomChance) {
        this.randomChance = randomChance;
    }

    @Override
    public void generateLoot(List<ItemStack> stacks, int value, Entity entity, int looting) {
        if (hasVariableFunctions) {
            // Variable functions. Looping required so they can generate random whatever.
            Map<LootData, Integer> lootDataTotals = new HashMap<>();

            for (int i = 0; i < value; i++) {
                LootData data = new LootData(this.getMaterial(), this.getMinimumQuantity(), this.getMaximumQuantity());
                for (IFunction function : functions) {
                    function.modify(data, entity, looting);
                }

                // Guaranteed quantity.
                if (data.getMinimumQuantity() >= data.getMaximumQuantity()) {
                    lootDataTotals.put(data, lootDataTotals.getOrDefault(data, 0) + data.getMinimumQuantity());
                    continue;
                }

                // RNG quantity.
                lootDataTotals.put(data, lootDataTotals.getOrDefault(data, 0)
                        + Math.min(0, ThreadLocalRandom.current().nextInt(data.getMinimumQuantity(),
                                data.getMaximumQuantity() + 1)));
            }

            for (Map.Entry<LootData, Integer> entry : lootDataTotals.entrySet()) {
                this.addDrops(stacks, entry.getKey(), entry.getValue());
            }
            return;
        }

        // Non-variable functions. Apply once and be done with.
        LootData data = new LootData(this.getMaterial(), this.getMinimumQuantity(), this.getMaximumQuantity());
        if (functions != null) {
            for (IFunction function : functions) {
                function.modify(data, entity, looting);
            }
        }

        // Check if RNG is required at all.
        if (data.getMaximumQuantity() <= data.getMinimumQuantity()) {
            addDrops(stacks, data, data.getMinimumQuantity() * value);
            return;
        }

        // Check if a loop is required.
        if (data.getMinimumQuantity() > -1) {
            int localMin = data.getMinimumQuantity() * value;
            int localMax = data.getMaximumQuantity() * value;

            addDrops(stacks, data, ThreadLocalRandom.current().nextInt(localMin, localMax + 1));
            return;
        }

        // Weighted drop quantity. Loop and roll.
        int total = 0;
        for (int i = 0; i < value; i++) {
            total += Math.min(0, ThreadLocalRandom.current()
                    .nextInt(data.getMinimumQuantity(), data.getMaximumQuantity() + 1));
        }
        addDrops(stacks, data, total);

    }

    private final void addDrops(List<ItemStack> drops, LootData lootData, int amount) {
        ItemStack drop = new ItemStack(lootData.getMaterial(), 0, lootData.getData());
        if (lootData.getMeta() != null) {
            drop.setItemMeta(lootData.getMeta());
        }

        // Drop max stacks, don't overstack.
        int maxStack = drop.getType().getMaxStackSize();
        while (amount > 0) {
            if (amount > maxStack) {
                ItemStack clone = drop.clone();
                clone.setAmount(maxStack);
                drops.add(clone);
                amount -= maxStack;
            } else {
                drop.setAmount(amount);
                drops.add(drop);
                amount = 0;
            }
        }
    }

    @Override
    public Map<String, Object> serialize() {
        LootEntry defaults = new LootEntry();
        Map<String, Object> serialization = new HashMap<>();

        serialization.put("material", this.getMaterial().name());

        if (this.minimumQuantity != defaults.getMinimumQuantity()) {
            serialization.put("minimumQuantity", this.minimumQuantity);
        }
        if (this.maximumQuantity != defaults.getMaximumQuantity()) {
            serialization.put("maximumQuantity", this.maximumQuantity);
        }
        if (this.weight != defaults.getWeight()) {
            serialization.put("weight", this.weight);
        }
        if (this.quality != defaults.getQuality()) {
            serialization.put("quality", this.quality);
        }
        if (this.conditions != null) {
            serialization.put("conditions", this.conditions);
        }
        if (this.functions != null) {
            serialization.put("functions", this.functions);
        }
        if (this.randomChance != null) {
            serialization.put("randomChance", this.randomChance);
        }

        return serialization;
    }

    public LootEntry deserialize(Map<String, Object> serialization) {
        LootEntry lootEntry = new LootEntry();

        if (serialization.containsKey("material")) {
            Object material = serialization.get("material");
            if (String.class.isAssignableFrom(material.getClass())) {
                lootEntry.setMaterial(Material.matchMaterial((String) material));
            }
        }

        SerializationUtils.load(lootEntry, Integer.class, "minimumQuantity", serialization);
        SerializationUtils.load(lootEntry, Integer.class, "maximumQuantity", serialization);
        SerializationUtils.load(lootEntry, Integer.class, "weight", serialization);
        SerializationUtils.load(lootEntry, Integer.class, "quality", serialization);
        SerializationUtils.load(lootEntry, IRandomChance.class, "randomChance", serialization);
        SerializationUtils.loadList(lootEntry, ICondition.class, "conditions", serialization);
        SerializationUtils.loadList(lootEntry, IFunction.class, "functions", serialization);

        return lootEntry;
    }

}
