package com.kiwifisher.mobstacker.loot.api;

import org.bukkit.entity.Entity;

/**
 * Interface defining behavior for an entry for a pool of experience.
 * 
 * @author Jikoo
 */
public interface IExperienceEntry extends IConditional {

    public int getMinimum(Entity entity);

    public int getMaximum(Entity entity);

}
