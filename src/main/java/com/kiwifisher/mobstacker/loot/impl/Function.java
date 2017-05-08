package com.kiwifisher.mobstacker.loot.impl;

import java.util.List;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.IFunction;

/**
 * Base abstraction for every IFunction.
 * 
 * @author Jikoo
 */
public abstract class Function implements IFunction {

    @Expose
    private List<ICondition> conditions;

    @Override
    public List<ICondition> getConditions() {
        return this.conditions;
    }

    public void setConditions(List<ICondition> conditions) {
        this.conditions = conditions;
    }

}
