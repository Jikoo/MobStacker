package com.kiwifisher.mobstacker.loot.api;

import org.bukkit.entity.Entity;

/**
 * Interface defining the functions of a Condition.
 * 
 * @author Jikoo
 */
public interface Condition {

    public abstract boolean test(Entity entity);

}
