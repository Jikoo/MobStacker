package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.IExperienceEntry;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;

/**
 * IExperienceEntry implementation returning amounts based on the size of the slime.
 * 
 * @author Jikoo
 */
public class SlimeExperienceEntry implements IExperienceEntry {

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

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = new HashMap<>();

        if (this.conditions != null && !this.conditions.isEmpty()) {
            serialization.put("conditions", this.conditions);
        }

        return serialization;
    }

    public static SlimeExperienceEntry deserialize(Map<String, Object> serialization) {
        SlimeExperienceEntry experienceEntry = new SlimeExperienceEntry();

        SerializationUtils.loadList(experienceEntry, ICondition.class, "conditions", serialization);

        return experienceEntry;
    }

}
