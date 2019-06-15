package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {

    private final MobStacker plugin;

    public EntityExplodeListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();

        int quantity = plugin.getStackUtils().getStackSize(entity);

        // Only bother with entities that are actually stacked.
        if (quantity < 2) {
            return;
        }

        if (!plugin.getConfig().getBoolean("exploding-creeper-kills-stack")) {

            // If we're not killing the full stack, peel off the rest.
            entity = plugin.getStackUtils().peelOffStack(entity);

            // Set 1 tick of no damage to prevent resulting explosion harming the new stack.
            entity.setNoDamageTicks(1);

            return;

        }

        // Amplify explosions if configured to do so.
        if (plugin.getConfig().getBoolean("magnify-stack-explosion.enable")) {

            /*
             * Charged creepers have a power of 6, TNT has a power of 4, normal creepers have a
             * power of 3. For everything else, 1 is probably a safe default. Really, it's a minimum
             * of 2 as that's how many mobs must be in the stack to reach this point.
             */
            float power = quantity + (entity instanceof Creeper ? ((Creeper) entity).isPowered() ? 5 : 2 : 0);

            // Cap explosion power to configured maximum.
            power = Math.max(1, Math.min(power,
                    plugin.getConfig().getInt("magnify-stack-explosion.max-creeper-explosion-size")));

            // Remove the entity - exploding entities don't fly off dying, they vanish with the explosion.
            entity.remove();

            // Create the explosion.
            entity.getWorld().createExplosion(event.getLocation(), power);

        }

    }

}
