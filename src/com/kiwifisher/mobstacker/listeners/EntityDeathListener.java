package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.algorithms.AlgorithmEnum;
import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
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
            return;
        }

        int stackSize = StackUtils.getStackSize(entity);

        // Ensure we have a stack.
        if (stackSize < 2) {
            return;
        }

        // Check if the whole stack is supposed to die.
        if (!plugin.getConfig().getBoolean("persistent-damage.enable")
                || plugin.getStackUtils().getAverageHealth(entity, false) >= 1) {

            // Whole stack is not dying, peel off the remainder and return.
            plugin.getStackUtils().peelOffStack(entity);

            return;
        }

        // Check if we are dropping proportionate loot. If not, return.
        if (!plugin.getConfig().getBoolean("persistent-damage.multiply-loot")) {
            return;
        }

        // Set proportionate experience.

        // Try to drop proportionate loot.
        try {
            LootAlgorithm algorithm = AlgorithmEnum.valueOf(entity.getType().name()).getLootAlgorithm();
            Player player = event.getEntity().getKiller();
            int looting = player != null ? player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) : 0;
            event.getDrops().addAll(algorithm.getRandomLoot(entity, stackSize - 1, player != null, looting));
            if (event.getDroppedExp() > 0) {
                event.setDroppedExp(algorithm.getExp(entity, stackSize));
            }
        } catch (IllegalArgumentException e) {
            event.setDroppedExp(event.getDroppedExp() * stackSize);
            // Rather than print a stack trace, warn about unimplemented loot.
            plugin.log(entity.getType().name() + " doesn't have proportionate loot implemented - please request it be added if you need it");
        }

    }

}