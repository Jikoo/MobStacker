package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SheepDyeWoolEvent;

public class SheepDyeListener implements Listener {

    private final MobStacker plugin;

    public SheepDyeListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSheepDye(SheepDyeWoolEvent event) {

        Sheep sheep = event.getEntity();

        if (StackUtils.getStackSize(sheep) > 1) {
            // Peel off the dyed sheep.
            plugin.getStackUtils().peelOffStack(sheep);
        } else {
            //If the stack is a single sheep, attempt to stack once.
            plugin.getStackUtils().attemptToStack(sheep, 1);
        }

    }

}
