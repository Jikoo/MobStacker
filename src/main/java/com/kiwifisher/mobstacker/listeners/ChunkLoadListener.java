package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Listener for ChunkLoadEvents. Used to load existing stacks based on names.
 * 
 * @author Jikoo
 */
public class ChunkLoadListener implements Listener {

    private final MobStacker plugin;

    public ChunkLoadListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {

        // Check if loading stacks and stacking is enabled in this world.
        if (!plugin.getConfig().getBoolean("load-existing-stacks")
                || plugin.getConfig().getStringList("blacklist-world").contains(event.getWorld().getName().toLowerCase())) {
            return;
        }

        // Schedule loading - chunk may not be fully loaded when ChunkLoadEvent is called.
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (Entity entity : event.getChunk().getEntities()) {
                // Check for and load from custom name.
                if (entity instanceof LivingEntity && entity.getCustomName() != null) {
                    plugin.getStackUtils().loadFromName((LivingEntity) entity);
                }
            }
        });

    }

}
