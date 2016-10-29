package com.kiwifisher.mobstacker.loot.api;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Interface representing a random chance.
 * 
 * @author Jikoo
 */
public interface IRandomChance extends ConfigurationSerializable {

    public boolean test(int looting);

}
