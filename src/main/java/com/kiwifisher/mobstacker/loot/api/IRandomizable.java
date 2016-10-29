package com.kiwifisher.mobstacker.loot.api;

/**
 * Interface representing anything with a random chance of occurrence.
 * 
 * @author Jikoo
 */
public interface IRandomizable {

    public IRandomChance getRandomChance();

}
