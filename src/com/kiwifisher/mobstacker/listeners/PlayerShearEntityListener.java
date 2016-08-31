package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class PlayerShearEntityListener implements Listener {

    private final MobStacker plugin;

    public PlayerShearEntityListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {

        Entity entity = event.getEntity();

        if (StackUtils.getStackSize(entity) > 1) {
            // Peel off the sheared entity.
            plugin.getStackUtils().peelOffStack(entity);
        }

        // Attempt to re-stack.
        plugin.getStackUtils().attemptToStack(entity, 1);

    }

}
