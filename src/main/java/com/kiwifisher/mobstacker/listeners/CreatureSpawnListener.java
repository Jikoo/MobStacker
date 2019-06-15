package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener {

    private final MobStacker plugin;

    public CreatureSpawnListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        final LivingEntity spawnedCreature = event.getEntity();

        if (event.isCancelled()) {
            return;
        }

        // Spigot support: Nerf spawned creatures in a way that we can easily make persist.
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER
                && plugin.nerfSpawnerMobs(spawnedCreature.getWorld())) {
            spawnedCreature.setAI(false);
        }

        // If the spawned entity has been spawned for a reason configured not to stack, flag it.
        if (!plugin.getConfig().getBoolean("stack-spawn-reason." + event.getSpawnReason().name())) {
            plugin.getStackUtils().setStackable(spawnedCreature, false);
            return;
        }

        // Check if the spawned entity is stackable.
        if (!plugin.getStackUtils().isStackable(spawnedCreature)) {
            return;
        }

        // Attempt to stack.
        plugin.getStackUtils().attemptToStack(spawnedCreature, plugin.getSearchTime());

    }

}
