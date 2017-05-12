package com.kiwifisher.mobstacker.loot.impl;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.ICondition;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

/**
 * Condition requiring an Entity to be an adult or not ageable.
 * 
 * @author Jikoo
 */
public class ConditionPropertiesAdult implements ICondition {

    @Expose
    private boolean adult;

    public ConditionPropertiesAdult() {
        this.adult = true;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    @Override
    public boolean test(Entity entity) {
        if (entity instanceof Ageable) {
            return adult == ((Ageable) entity).isAdult();
        }

        return adult;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass().equals(obj.getClass())
                && this.adult == ((ConditionPropertiesAdult) obj).adult;
    }

    @Override
    public String toString() {
        return String.format("%s(adult=%s)", this.getClass().getName(), this.adult);
    }

}
