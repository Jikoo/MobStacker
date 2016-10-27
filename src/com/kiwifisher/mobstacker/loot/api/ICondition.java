package com.kiwifisher.mobstacker.loot.api;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

/**
 * Interface defining the functions of a Condition.
 * 
 * @author Jikoo
 */
public interface ICondition extends ConfigurationSerializable {

    public abstract boolean test(Entity entity);

}
