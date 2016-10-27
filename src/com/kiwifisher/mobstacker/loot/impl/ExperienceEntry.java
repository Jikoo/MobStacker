package com.kiwifisher.mobstacker.loot.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.IExperienceEntry;

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

    public void setMinimum(int minimum) {
        this.minimum = Math.max(0, minimum);
    }

    @Override
    public int getMaximum(Entity entity) {
        return this.maximum;
    }

    public int getMaximum() {
        return this.maximum;
    }

    public void setMaximum(int maximum) {
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

        if (serialization.containsKey("minimum")) {
            Object minimum = serialization.get("minimum");
            if (int.class.isAssignableFrom(minimum.getClass())) {
                experienceEntry.setMinimum((int) minimum);
            }
        }

        if (serialization.containsKey("maximum")) {
            Object maximum = serialization.get("maximum");
            if (int.class.isAssignableFrom(maximum.getClass())) {
                experienceEntry.setMaximum((int) maximum);
            }
        }

        if (serialization.containsKey("conditions")) {
            Object conditions = serialization.get("conditions");
            if (conditions instanceof List) {
                List<ICondition> newConditions = new ArrayList<>();
                List<?> conditionList = (List<?>) conditions;
                for (Object condition : conditionList) {
                    if (condition instanceof ICondition) {
                        newConditions.add((ICondition) condition);
                    }
                }
                if (!newConditions.isEmpty()) {
                    experienceEntry.setConditions(newConditions);
                }
            }
        }

        return experienceEntry;
    }

}
