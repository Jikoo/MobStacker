package com.kiwifisher.mobstacker.listeners;

import java.util.List;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Listener for ChunkUnloadEvents. Used to remove names of stacked mobs that won't be loaded back
 * when chunks load.
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
        // Loading existing stacks requires the {QTY} tag in naming convention.
        boolean loadStacks = plugin.getConfig().getBoolean("load-existing-stacks.enabled")
                && plugin.getConfig().getString("stack-naming").contains("{QTY}");
        List<String> types = plugin.getConfig().getStringList("load-existing-stacks.mob-types");

        for (Entity entity : event.getChunk().getEntities()) {
            // Ensure entity is stacked and has a custom name, otherwise skip.
            if (entity.getCustomName() == null || StackUtils.getStackSize(entity) < 2
                    || !plugin.getStackUtils().matchesStackName(entity.getCustomName())) {
                continue;
            }

            // If stacks aren't supposed to be loaded for the entity's type, un-name.
            if (!loadStacks || !types.contains(entity.getType().toString())) {
                entity.setCustomName(null);
                entity.setCustomNameVisible(false);
            }
        }
    }

}
