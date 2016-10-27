package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.IFunction;

/**
 * Base abstraction for every IFunction.
 * 
 * @author Jikoo
 */
public abstract class Function implements IFunction {

    private List<ICondition> conditions;

    @Override
    public List<ICondition> getConditions() {
        return this.conditions;
    }

    public void setConditions(List<ICondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = new HashMap<>();
        if (this.conditions != null) {
            serialization.put("conditions", this.conditions);
        }
        return serialization;
    }

}
