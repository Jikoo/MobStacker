package com.kiwifisher.mobstacker.loot.api;

import org.bukkit.entity.Entity;

/**
 * Interface defining behavior for an entry for a pool of experience.
 * 
 * @author Jikoo
 */
public interface ExperienceEntry extends Conditional {

    public int getMinimum(Entity entity);

    public int getMaximum(Entity entity);

}
