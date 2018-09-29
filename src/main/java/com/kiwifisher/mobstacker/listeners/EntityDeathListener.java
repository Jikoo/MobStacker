package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.loot.LootManager;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    private final MobStacker plugin;

    public EntityDeathListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        LivingEntity entity = event.getEntity();

        /*
         * If void is the cause of death, always let the entity just die. This prevents falling
         * stacks reaching excessive (and potentially problematic - see SPIGOT-58) depths.
         */
        if (entity.getLastDamageCause() != null && entity.getLastDamageCause().getCause() == DamageCause.VOID) {
            // Clean up metadata storage.
            plugin.getStackUtils().removeStackMetadata(entity);

            return;
        }

        int stackSize = StackUtils.getStackSize(entity);

        // Ensure we have a stack.
        if (stackSize < 2) {
            // Clean up metadata storage.
            plugin.getStackUtils().removeStackMetadata(entity);

            return;
        }

        // Check if the whole stack is supposed to die.
        if (!plugin.getConfig().getBoolean("persistent-damage.enable")
                || plugin.getStackUtils().getAverageHealth(entity, false) > 0) {

            // Whole stack is not dying, peel off the remainder and return.
            plugin.getStackUtils().peelOffStack(entity);

            // Clean up metadata storage.
            plugin.getStackUtils().removeStackMetadata(entity);

            return;
        }

        // Check if we are dropping proportionate loot. If not, return.
        if (!plugin.getConfig().getBoolean("persistent-damage.multiply-loot")) {
            // Clean up metadata storage.
            plugin.getStackUtils().removeStackMetadata(entity);

            return;
        }

        LootManager manager = plugin.getLootManager();

        // Set proportionate experience.
        if (event.getEntity().getKiller() != null) {
            event.setDroppedExp(event.getDroppedExp() + manager.getExperience(entity, stackSize - 1));
        }

        // Try to drop proportionate loot.
        Player player = event.getEntity().getKiller();
        int looting = player != null ? player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) : 0;
        event.getDrops().addAll(manager.getLoot(entity, player, stackSize - 1, looting));

        // Clean up metadata storage.
        plugin.getStackUtils().removeStackMetadata(entity);

    }

}
