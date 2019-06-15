package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Listener for ChunkUnloadEvents. Used to remove data from stacked mobs that will not be reloaded.
 *
 * @author Jikoo
 */
public class ChunkUnloadListener implements Listener {

    private final MobStacker plugin;

    public ChunkUnloadListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        // If stacks are supposed to be loaded, don't wipe stack data.
        if (plugin.getConfig().getBoolean("load-existing-stacks")) {
            return;
        }

        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;

            // If entity is stacked, remove stack data.
            if (plugin.getStackUtils().getStackSize(livingEntity) > 1) {
                entity.setCustomName(null);
                entity.setCustomNameVisible(false);
                plugin.getStackUtils().removeStackData(livingEntity);
            }

        }
    }

}
