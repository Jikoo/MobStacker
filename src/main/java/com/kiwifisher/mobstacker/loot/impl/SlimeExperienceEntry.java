package com.kiwifisher.mobstacker.loot.impl;

import com.google.gson.annotations.Expose;
import com.kiwifisher.mobstacker.loot.api.Condition;
import com.kiwifisher.mobstacker.loot.api.ExperienceEntry;
import com.kiwifisher.mobstacker.utils.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;

import java.util.List;

/**
 * ExperienceEntry implementation returning amounts based on the size of the slime.
 * 
 * @author Jikoo
 */
public class SlimeExperienceEntry implements ExperienceEntry {

    @Expose
    private List<Condition> conditions;

    @Override
    public List<Condition> getConditions() {
        return this.conditions;
    }

    public void setConditions(List<Condition> conditions) {
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

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass().equals(obj.getClass())
                && CollectionUtils.equal(this.conditions, ((SlimeExperienceEntry) obj).conditions);
    }

    @Override
    public String toString() {
        return String.format("%s(conditions=%s)", this.getClass().getName(), this.conditions);
    }

}
