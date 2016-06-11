package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SheepRegrowWoolEvent;

public class SheepRegrowWoolListener implements Listener {

    private final MobStacker plugin;

    public SheepRegrowWoolListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSheepRegrowWool(SheepRegrowWoolEvent event) {

        // Attempt to stack sheep once
        plugin.getStackUtils().attemptToStack(event.getEntity(), 1);

    }

}
