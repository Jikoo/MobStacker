package com.kiwifisher.mobstacker.loot.impl;

import com.google.gson.annotations.Expose;
import com.kiwifisher.mobstacker.loot.api.ExperienceEntry;
import com.kiwifisher.mobstacker.loot.api.ExperiencePool;
import com.kiwifisher.mobstacker.utils.CollectionUtils;
import com.kiwifisher.mobstacker.utils.ConditionUtils;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An BaseExperiencePool containing possible ExperienceEntries for an entity.
 *
 * @author Jikoo
 */
public class BaseExperiencePool implements ExperiencePool {

    @Expose
    private List<ExperienceEntry> entries;

    @Override
    public int getExperience(Entity entity, int numberOfMobs) {
        if (entries == null || entries.isEmpty()) {
            return 0;
        }

        int min = 0, max = 0;

        // Add all valid entries to min/max.
        for (ExperienceEntry entry : entries) {
            if (!ConditionUtils.meetsConditions(entity, entry)) {
                continue;
            }
            int entryMin = entry.getMinimum(entity);
            min += entryMin;
            max += Math.max(entryMin, entry.getMaximum(entity));
        }

        min *= numberOfMobs;
        max *= numberOfMobs;

        // Check if RNG is required at all.
        if (min >= max) {
            return Math.max(0, min);
        }

        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public List<ExperienceEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ExperienceEntry> entries) {
        this.entries = entries;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass().equals(obj.getClass())
                && CollectionUtils.equal(this.entries, ((BaseExperiencePool) obj).entries);
    }

    @Override
    public String toString() {
        return String.format("%s(entries=%s)", this.getClass().getName(), this.entries);
    }

}
