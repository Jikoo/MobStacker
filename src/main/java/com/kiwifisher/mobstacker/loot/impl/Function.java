package com.kiwifisher.mobstacker.loot.impl;

import java.util.List;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.IFunction;
import com.kiwifisher.mobstacker.utils.CollectionUtils;

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

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass().equals(obj.getClass())
                && CollectionUtils.equal(this.conditions, ((Function) obj).conditions);
    }

    @Override
    public String toString() {
        return String.format("%s(conditions=%s)", this.getClass().getName(), this.conditions);
    }

}
