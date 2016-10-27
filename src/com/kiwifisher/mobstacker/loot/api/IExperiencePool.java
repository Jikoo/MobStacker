package com.kiwifisher.mobstacker.loot.api;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

/**
 * Interface defining behavior for a pool of experience entries for an entity.
 * 
 * @author Jikoo
 */
public interface IExperiencePool extends ConfigurationSerializable {

    public int getExperience(Entity entity, int numberOfMobs);

}
