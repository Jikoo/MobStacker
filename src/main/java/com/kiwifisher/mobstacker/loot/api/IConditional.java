package com.kiwifisher.mobstacker.loot.api;

import java.util.List;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Interface defining behavior for an object which may require several IConditions to be met.
 * 
 * @author Jikoo
 */
public interface IConditional extends ConfigurationSerializable {

    public List<ICondition> getConditions();

}
