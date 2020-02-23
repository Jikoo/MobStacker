package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.entity.Bee;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * Listener for EntityChangeBlockEvents. Used to modify block changes made by entities based on stack sizes.
 */
public class EntityChangeBlockListener implements Listener {

	private final MobStacker plugin;

	public EntityChangeBlockListener(MobStacker plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}

		int stackSize = plugin.getStackUtils().getStackSize((LivingEntity) event.getEntity());

		if (stackSize < 2) {
			return;
		}

		if (event.getEntity() instanceof Bee && event.getBlockData() instanceof Beehive) {
			Beehive beehive = (Beehive) event.getBlockData();
			beehive.setHoneyLevel(Math.min(beehive.getMaximumHoneyLevel(), beehive.getHoneyLevel() + stackSize - 1));
		}
	}

}
