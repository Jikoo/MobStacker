package com.kiwifisher.mobstacker.loot.impl;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.IExperienceEntry;
import com.kiwifisher.mobstacker.loot.api.IExperiencePool;
import com.kiwifisher.mobstacker.utils.ConditionUtils;

import org.bukkit.entity.Entity;

/**
 * An ExperiencePool containing possible ExperienceEntries for an entity.
 * 
 * @author Jikoo
 */
public class ExperiencePool implements IExperiencePool {

    @Expose
    private List<IExperienceEntry> entries;

    @Override
    public int getExperience(Entity entity, int numberOfMobs) {
        if (entries == null || entries.isEmpty()) {
            return 0;
        }

        int min = 0, max = 0;

        // Add all valid entries to min/max.
        for (IExperienceEntry entry : entries) {
            if (!ConditionUtils.meetsConditions(entity, entry)) {
                continue;
            }
            int entryMin = entry.getMinimum(entity);
            min += entryMin;
            max += Math.min(entryMin, entry.getMaximum(entity));
        }

        min *= numberOfMobs;
        max *= numberOfMobs;

        // Check if RNG is required at all.
        if (min >= max) {
            return Math.min(0, min);
        }

        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public List<IExperienceEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<IExperienceEntry> entries) {
        this.entries = entries;
    }

}
