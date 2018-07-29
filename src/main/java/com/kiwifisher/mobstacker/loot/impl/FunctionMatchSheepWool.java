package com.kiwifisher.mobstacker.loot.impl;

import com.kiwifisher.mobstacker.loot.api.LootData;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.material.Colorable;

/**
 * Function implementation for setting data to match a sheep (or any Colorable)'s drop data.
 * <br>
 * N.B.: Material MUST be white variant.
 *
 * @author Jikoo
 */
public class FunctionMatchSheepWool extends Function {

	@Override
	public void modify(LootData lootData, Entity entity, int looting) {

		if (!(entity instanceof Colorable)) {
			return;
		}

		Material newMaterial = Material.matchMaterial(lootData.getMaterial().name()
				.replace("WHITE", ((Colorable) entity).getColor().name()));

		if (newMaterial != null) {
			lootData.setMaterial(newMaterial);
		}
	}

	@Override
	public boolean isVariable() {

		return false;
	}

}
