package com.kiwifisher.mobstacker.loot.impl;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.Condition;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;

/**
 * Condition requiring a Slime to be a certain size or within a range of sizes.
 * 
 * @author Jikoo
 */
public class ConditionSlimeSize implements Condition {

    @Expose
    private int minimum, maximum;

    public ConditionSlimeSize() {
        this.minimum = 0;
        this.maximum = 255;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    @Override
    public boolean test(Entity entity) {
        if (!(entity instanceof Slime)) {
            return false;
        }
        Slime slime = (Slime) entity;
        if (slime.getSize() < this.getMinimum()) {
            return false;
        }
        if (this.getMinimum() >= this.getMaximum()) {
            return slime.getSize() == this.getMinimum();
        }
        return slime.getSize() <= this.getMaximum();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }

        ConditionSlimeSize other = (ConditionSlimeSize) obj;

        return this.minimum == other.minimum && this.maximum == other.maximum;
    }

    @Override
    public String toString() {
        return String.format("%s(minimum=%s,maximum=%s)", this.getClass().getName(), this.minimum,
                this.maximum);
    }

}
