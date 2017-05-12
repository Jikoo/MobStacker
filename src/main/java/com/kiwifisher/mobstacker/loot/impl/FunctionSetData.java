package com.kiwifisher.mobstacker.loot.impl;

import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.LootData;

import org.bukkit.entity.Entity;

/**
 * IFunction implementation for setting data to a number or range of numbers.
 * 
 * @author Jikoo
 */
public class FunctionSetData extends Function {

    @Expose
    private short minimum, maximum;

    public FunctionSetData() {
        this.minimum = 0;
        this.maximum = 0;
    }

    public short getMinimum() {
        return minimum;
    }

    public void setMinimum(Short minimum) {
        this.minimum = minimum;
    }

    public short getMaximum() {
        return maximum;
    }

    public void setMaximum(Short maximum) {
        this.maximum = maximum;
    }

    @Override
    public void modify(LootData lootData, Entity entity, int looting) {
        if (isVariable()) {
            lootData.setData((short) ThreadLocalRandom.current().nextInt(minimum, maximum + 1));
        } else {
            lootData.setData(getMinimum());
        }
    }

    @Override
    public boolean isVariable() {
        return minimum < maximum;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        FunctionSetData other = (FunctionSetData) obj;

        return this.minimum == other.minimum && this.maximum == other.maximum;
    }

    @Override
    public String toString() {
        String superString = super.toString();
        return String.format("%s,minimum=%s,maximum=%s)",
                superString.substring(0, superString.length() - 1), this.minimum, this.maximum);
    }

}
