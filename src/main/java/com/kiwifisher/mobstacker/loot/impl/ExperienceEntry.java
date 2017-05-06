package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.IExperienceEntry;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

import org.bukkit.entity.Entity;

/**
 * 
 * 
 * @author Jikoo
 */
public class ExperienceEntry implements IExperienceEntry {

    private int minimum, maximum;
    private List<ICondition> conditions;

    public ExperienceEntry() {
        this.minimum = 0;
        this.maximum = 0;
    }

    @Override
    public List<ICondition> getConditions() {
        return this.conditions;
    }

    public void setConditions(List<ICondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public int getMinimum(Entity entity) {
        return minimum;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(Integer minimum) {
        this.minimum = Math.max(0, minimum);
    }

    @Override
    public int getMaximum(Entity entity) {
        return this.maximum;
    }

    public int getMaximum() {
        return this.maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = Math.max(0, maximum);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = new HashMap<>();

        if (this.minimum != 0) {
            serialization.put("minimum", this.minimum);
        }
        if (this.maximum != 0) {
            serialization.put("maximum", this.maximum);
        }
        if (this.conditions != null && !this.conditions.isEmpty()) {
            serialization.put("conditions", this.conditions);
        }

        return serialization;
    }

    public ExperienceEntry deserialize(Map<String, Object> serialization) {
        ExperienceEntry experienceEntry = new ExperienceEntry();

        SerializationUtils.load(experienceEntry, Integer.class, "minimum", serialization);
        SerializationUtils.load(experienceEntry, Integer.class, "maximum", serialization);
        SerializationUtils.loadList(experienceEntry, ICondition.class, "conditions", serialization);

        return experienceEntry;
    }

}
