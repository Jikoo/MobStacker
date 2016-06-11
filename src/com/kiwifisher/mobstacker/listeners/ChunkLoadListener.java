package com.kiwifisher.mobstacker.listeners;

import java.util.List;

import com.kiwifisher.mobstacker.MobStacker;

import org.bukkit.entity.Entity;
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
        if (!plugin.getConfig().getBoolean("load-existing-stacks.enabled")
                || plugin.getConfig().getStringList("blacklist-world").contains(event.getWorld().getName().toLowerCase())) {
            return;
        }

        // Get acceptable mob types.
        List<String> types = plugin.getConfig().getStringList("load-existing-stacks.mob-types");

        // Check if any mob types are acceptable.
        if (types.isEmpty()) {
            return;
        }

        for (Entity entity : event.getChunk().getEntities()) {

            // Check for a custom name and an approved type. If not, not an existing stack.
            if (entity.getCustomName() == null || !types.contains(entity.getType().name())
                    || !plugin.getConfig().getBoolean("stack-mob-type." + entity.getType().name(), false)) {
                continue;
            }

            // Load the existing stack.
            plugin.getStackUtils().loadFromName(entity);

        }

    }

}
