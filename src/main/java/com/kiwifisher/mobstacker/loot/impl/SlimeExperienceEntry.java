package com.kiwifisher.mobstacker.loot.impl;

import java.util.List;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.IExperienceEntry;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;

/**
 * IExperienceEntry implementation returning amounts based on the size of the slime.
 * 
 * @author Jikoo
 */
public class SlimeExperienceEntry implements IExperienceEntry {

    @Expose
    private List<ICondition> conditions;

    @Override
    public List<ICondition> getConditions() {
        return this.conditions;
    }

    public void setConditions(List<ICondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public int getMinimum(Entity entity) {
        if (!(entity instanceof Slime)) {
            return 0;
        }
        return ((Slime) entity).getSize() + 1;
    }

    @Override
    public int getMaximum(Entity entity) {
        return 0;
    }

}
