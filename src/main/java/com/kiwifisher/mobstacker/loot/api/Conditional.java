package com.kiwifisher.mobstacker.loot.api;

import java.util.List;

/**
 * Interface defining behavior for an object which may require several IConditions to be met.
 * 
 * @author Jikoo
 */
public interface Conditional {

    public List<Condition> getConditions();

}
