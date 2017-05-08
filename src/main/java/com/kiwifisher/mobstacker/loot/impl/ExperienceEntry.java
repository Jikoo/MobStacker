package com.kiwifisher.mobstacker.loot.impl;

import java.util.List;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.IExperienceEntry;

import org.bukkit.entity.Entity;

/**
 * 
 * 
 * @author Jikoo
 */
public class ExperienceEntry implements IExperienceEntry {

    @Expose
    private int minimum, maximum;
    @Expose
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

}
