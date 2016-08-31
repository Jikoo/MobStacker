package com.kiwifisher.mobstacker.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kiwifisher.mobstacker.MobStacker;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.bukkit.ChatColor;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.material.Colorable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * The main guts of this plugin. All the stacking, peeling, searching and renaming is done here.
 */
public class StackUtils {

    private final MobStacker plugin;

    private Pattern namePattern;

    public StackUtils(MobStacker plugin) {
        this.plugin = plugin;

        this.loadConfig();
    }

    public void loadConfig() {
        String naming = plugin.getConfig().getString("stack-naming", "&e&l{QTY}X &6&l{TYPE}");

        // Translate chat colors and remove all quote start/ends to prevent issues.
        naming = ChatColor.translateAlternateColorCodes('&', naming).replace("\\Q", "")
                .replace("\\E", "");

        // Replace quantity and type tags with regular expressions to match any valid names
        naming = naming.replace("{QTY}", "\\E([0-9]+)\\Q").replace("{TYPE}", "\\E[A-Z ]+\\Q");

        // Prepend quote start and append quote end
        StringBuilder builder = new StringBuilder(naming.length() + 4).append("\\Q").append(naming).append("\\E");

        namePattern = Pattern.compile(builder.toString());
    }

    public void attemptToStack(final Entity stack, final int attempts) {

        if (!plugin.isStacking() || !isStackable(stack)) {
            return;
        }

        // Set max stack size. Specific entity type overrides
        final int maxStackSize = plugin.getMaxStackSize(stack.getType());

        if (maxStackSize < 2) {
            return;
        }

        new BukkitRunnable() {

            Entity entity = stack;
            int count = 0;

            @Override
            public void run() {

                // If entity is dead or not stackable, cancel and return.
                if (!plugin.isStacking() || entity.isDead() || !isStackable(entity)) {
                    cancel();
                    return;
                }

                boolean stackSuccess = false;

                // Check all nearby entities.
                for (Entity nearbyEntity : entity.getNearbyEntities(
                        plugin.getConfig().getInt("stack-range.x"),
                        plugin.getConfig().getInt("stack-range.y"),
                        plugin.getConfig().getInt("stack-range.z"))) {

                    // Attempt to stack
                    Pair<Entity, Boolean> pair = stackEntities(entity, nearbyEntity, maxStackSize);

                    // If the mobs stack and we've reached max stack size, cancel and return.
                    if (pair.getRight()) {
                        if (!isStackable(entity)) {
                            cancel();
                            return;
                        }

                        stackSuccess = true;

                        // Re-assign entity - if stack-mobs-down is enabled, the original may be removed
                        entity = pair.getLeft();
                    }

                }

                // If search time is up, cancel and return.
                if (attempts >= 0) {

                    // Matched successfully, continue searching a little longer
                    if (stackSuccess) {
                        --count;
                    } else {
                        ++count;
                    }

                    if (count > attempts) {
                        cancel();
                        return;
                    }
                }
            }

        }.runTaskTimer(plugin, 0L, 20L); // 20 ticks per check, 1 second.

    }

    /**
     * Attempts to merge two entities into a single stack.
     * 
     * @param entity1 the first Entity
     * @param entity2 the second Entity
     * @param maxStackSize the maximum allowed stack size
     * @return true if the stacks were merged successfully.
     */
    public Pair<Entity, Boolean> stackEntities(Entity entity1, Entity entity2, int maxStackSize) {

        Entity mergeTo = entity1;
        Entity removed = entity2;
        // If entities are supposed to stack down, ensure mergeTo is the lower one.
        if (entity1.getLocation().getBlockY() > entity2.getLocation().getBlockY()
                && plugin.getConfig().getBoolean("stack-mobs-down")) {
            mergeTo = entity2;
            removed = entity1;
        }

        int additionalStacks = getStackSize(removed);
        int newStackSize = getStackSize(mergeTo) + additionalStacks;

        if (!canStack(mergeTo, removed, maxStackSize, newStackSize)) {
            return new ImmutablePair<>(entity1, false);
        }

        // Get the current average health of the added entity for later use
        double addedAverageHealth = getAverageHealth(removed, true);

        // Set stack size to new stack size
        setStackSize(mergeTo, newStackSize);

        // TODO: mergeProperties method - merge potion effects, meta, etc.? For now, using copyEntityProperties
        try {
            copyEntityProperties(mergeTo, removed);
        } catch (Exception e) {
            // Catch generic exception to prevent unforeseen issues causing entity dupes
            e.printStackTrace();
        };

        increaseAverageHealth(mergeTo, additionalStacks, addedAverageHealth);

        // Remove merged entity
        removed.remove();

        return new ImmutablePair<>(mergeTo, true);

    }

    /**
     * Check if two entities are similar enough to be stacked.
     * 
     * @param entity1 the first entity
     * @param entity2 the second entity
     * @return true if the mobs can stack
     */
    private boolean canStack(Entity entity1, Entity entity2, int maxStackSize, int newStackSize) {

        // Ensure entities are alive, of the same type, stackable, and won't exceed max stack size.
        if (entity1.equals(entity2) || entity1.isDead() || entity2.isDead()
                || entity1.getType() != entity2.getType() || !isStackable(entity1)
                || !isStackable(entity2) || maxStackSize < newStackSize) {
            return false;
        }

        long lastBred1 = entity1.hasMetadata("lastBred") ? entity1.getMetadata("lastBred").get(0).asLong() : 0;
        long lastBred2 = entity2.hasMetadata("lastBred") ? entity2.getMetadata("lastBred").get(0).asLong() : 0;
        // Breeding status takes 5 minutes to reset.
        long irrelevantAfter = System.currentTimeMillis() - 300000L;

        /*
         * Assuming entity1 is the entity being stacked to: If entity2 was bred later, it has a
         * longer time until it can next breed and should not stack. However, if it was bred earlier
         * than 5 minutes ago, it doesn't matter - the breeding timer resets then.
         */
        if (lastBred1 < lastBred2 && lastBred2 < irrelevantAfter) {
            return false;
        }

        if (entity1 instanceof LivingEntity && entity2 instanceof LivingEntity) {
            LivingEntity living1 = (LivingEntity) entity1;
            LivingEntity living2 = (LivingEntity) entity2;

            // Prevent accidental despawns
            if (living1.getRemoveWhenFarAway() != living2.getRemoveWhenFarAway()) {
                return false;
            }

            // No-AI property
            if (plugin.getConfig().getBoolean("stack-properties.AI", true)
                    && living1.hasAI() != living2.hasAI()) {
                return false;
            }

            // Leashed mobs
            if (!plugin.getConfig().getBoolean("stack-leashed-mobs", false)) {
                if (living1.isLeashed() || living2.isLeashed()) {
                    return false;
                }
            } else if (living1.isLeashed() != living2.isLeashed()) {
            }
        }

        if (plugin.getConfig().getBoolean("stack-properties.age", true)
                && entity1 instanceof Ageable && entity2 instanceof Ageable
                && ((Ageable) entity1).isAdult() != ((Ageable) entity2).isAdult()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.color", true)
                && entity1 instanceof Colorable && entity2 instanceof Colorable
                && ((Colorable) entity1).getColor() != ((Colorable) entity2).getColor()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.poweredCreeper", true)
                && entity1 instanceof Creeper && entity2 instanceof Creeper
                && ((Creeper) entity1).isPowered() != ((Creeper) entity2).isPowered()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.maxHealth", true)
                && entity1 instanceof Damageable && entity2 instanceof Damageable
                &&((Damageable) entity1).getMaxHealth() != ((Damageable) entity2).getMaxHealth()) {
            return false;
        }

        if (entity1 instanceof Horse && entity2 instanceof Horse) {
            Horse horse1 = (Horse) entity1;
            Horse horse2 = (Horse) entity2;
            if (plugin.getConfig().getBoolean("stack-properties.horse.type", true)
                    && horse1.getVariant() != horse2.getVariant()) {
                return false;
            }

            if (plugin.getConfig().getBoolean("stack-properties.horse.color", true)
                    && (horse1.getStyle() != horse2.getStyle() || horse1.getColor() != horse2.getColor())) {
                return false;
            }
        }

        if (plugin.getConfig().getBoolean("stack-properties.playerCreatedGolem", true)
                && entity1 instanceof IronGolem && entity2 instanceof IronGolem
                &&((IronGolem) entity1).isPlayerCreated() != ((IronGolem) entity2).isPlayerCreated()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.ocelot", true)
                && entity1 instanceof Ocelot && entity2 instanceof Ocelot
                && ((Ocelot) entity1).getCatType() != ((Ocelot) entity2).getCatType()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.rabbit", true)
                && entity1 instanceof Rabbit && entity2 instanceof Rabbit
                && ((Rabbit) entity1).getRabbitType() != ((Rabbit) entity2).getRabbitType()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.sheep", true)
                && entity1 instanceof Sheep && entity2 instanceof Sheep
                && ((Sheep) entity1).isSheared() != ((Sheep) entity2).isSheared()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.tameable", true)
                && entity1 instanceof Tameable && entity2 instanceof Tameable) {
            Tameable tame1 = (Tameable) entity1;
            Tameable tame2 = (Tameable) entity2;
            if (tame1.isTamed() != tame2.isTamed()
                    || tame1.isTamed() && !tame1.getOwner().equals(tame2.getOwner())) {
                return false;
            }
        }

        if (plugin.getConfig().getBoolean("stack-properties.villager", true)
                && entity1 instanceof Villager && entity2 instanceof Villager
                && ((Villager) entity1).getProfession() != ((Villager) entity2).getProfession()) {
            return false;
        }

        if (entity1 instanceof Wolf && entity2 instanceof Wolf) {
            Wolf wolf1 = (Wolf) entity1;
            Wolf wolf2 = (Wolf) entity2;
            if (plugin.getConfig().getBoolean("stack-properties.wolf.anger", true)
                    && wolf1.isAngry() != wolf2.isAngry()) {
                return false;
            }
            if (plugin.getConfig().getBoolean("stack-properties.wolf.color", true)
                    && wolf1.getCollarColor() != wolf2.getCollarColor()) {
                return false;
            }
        }

        // Separate guardians by elder status
        if (entity1 instanceof Guardian && entity2 instanceof Guardian
                && ((Guardian) entity1).isElder() != ((Guardian) entity2).isElder()) {
            return false;
        }

        // Separate skeletons by type
        if (entity1 instanceof Skeleton && entity2 instanceof Skeleton
                && ((Skeleton) entity1).getSkeletonType() != ((Skeleton) entity2).getSkeletonType()) {
            return false;
        }

        // Separate slimes by size
        if (entity1 instanceof Slime && entity2 instanceof Slime
                && ((Slime) entity1).getSize() != ((Slime) entity2).getSize()) {
            return false;
        }

        // Separate zombies by type
        if (entity1 instanceof Zombie && entity2 instanceof Zombie
                && ((Zombie) entity1).getVillagerProfession() != ((Zombie) entity2).getVillagerProfession()) {
            return false;
        }

        return true;
    }

    /**
     * This method takes a stack and turns it into a single mob, spawning in a new stack with the
     * remainder in the same location.
     * 
     * @param entity The stack to peel off of
     * @param restackable true if the new stack should attempt to stack again
     * @return the new stack
     */
    public Entity peelOffStack(Entity entity) {
       return peelOff(entity, true);
    }

    /**
     * This method takes a stack and removes a single entity, spawning it in the same location.
     * 
     * @param entity The stack to peel off of
     * @param restackable true if the new stack should attempt to stack again
     * @return the new entity
     */
    public Entity peelOffSingle(Entity entity) {
        return peelOff(entity, false);
    }

    private Entity peelOff(Entity entity, boolean stack) {

        // Get the new stack size post-peel.
        int newQuantity = getStackSize(entity) - 1;

        // If stacking is disabled, remove the stack data.
        if (!plugin.isStacking() || !isConfigStackable(entity)) {
            stack = false;
            newQuantity = 0;
        }

        if (stack) {
            // Set the old stack to a single entity.
            setStackSize(entity, 1);
        }

        // Nothing to unstack.
        if (newQuantity < 1) {
            if (!stack) {
                // Rename the stack (or lack thereof), just in case.
                nameStack(entity, 1);
            }
            return entity;
        }

        // Spawn the new entity in.
        Entity newEntity = entity.getWorld().spawn(entity.getLocation(), entity.getClass());

        // Copy properties to the new entity.
        try {
            copyEntityProperties(entity, newEntity);
        } catch (Exception e) {
            // Catch generic exception to prevent unforeseen issues causing entity dupes
            e.printStackTrace();
        }

        // If the entity has health, set its health to the stack's average.
        if (newEntity instanceof Damageable) {
            double averageHealth = getAverageHealth(entity, false);
            ((Damageable) newEntity).setHealth(averageHealth);
        }

        if (stack) {
            // Set the new stack to the remaining amount.
            setStackSize(newEntity, newQuantity);
        } else {
            // Set the new stack's size.
            setStackSize(newEntity, 1);

            // Set the old stack's new size.
            setStackSize(entity, newQuantity);
        }

        // Attempt to restack once.
        attemptToStack(entity, 1);
        attemptToStack(newEntity, 1);

        return newEntity;

    }

    /**
     * Copy properties to a new Entity. Both can be different types of entity safely.
     * Non-living entities are not supported (AreaEffectCloud, Boat, etc.).
     * 
     * @param original the original Entity
     * @param copy the Entity to copy properties to
     * @throws Exception when some un-handled weird special case comes into play
     */
    private void copyEntityProperties(Entity original, Entity copy) throws Exception {

        if (original == copy || original.equals(copy)
                || original.getUniqueId().equals(copy.getUniqueId())) {
            return;
        }

        copy.setFallDistance(original.getFallDistance());
        copy.setFireTicks(original.getFireTicks());
        copy.setGlowing(original.isGlowing());
        copy.setGravity(original.hasGravity());
        copy.setInvulnerable(original.isInvulnerable());
        copy.setLastDamageCause(original.getLastDamageCause());
        copy.setSilent(original.isSilent());
        /*
         * Special case: Cannot set ticks lived under 1. Occasionally, an entity will be copied
         * before it has lived a tick.
         */
        copy.setTicksLived(Math.max(1, original.getTicksLived()));
        // TODO: Configurable velocity percentage multiplier?
        /*
         * Of note: When splitting a stack, the new entity is spawned after the hit knockback takes
         * effect but before the knockback enchantment is applied. The resulting velocity is NOT
         * affected by the knockback enchantment.
         */
        copy.setVelocity(original.getVelocity());

        if (original instanceof Ageable && copy instanceof Ageable) {
            Ageable originalAgeable = (Ageable) original;
            Ageable copyAgeable = (Ageable) copy;
            copyAgeable.setAge(originalAgeable.getAge());
            copyAgeable.setAgeLock(originalAgeable.getAgeLock());
            copyAgeable.setBreed(originalAgeable.canBreed());
        }

        // TODO: Config option? If stack-down, could be abused to duplicate nice horses. Commented out for now.
//        if (original instanceof Attributable && copy instanceof Attributable) {
//            Attributable originalAttributable = (Attributable) original;
//            Attributable copyAttributable = (Attributable) copy;
//            for (Attribute attribute : Attribute.values()) {
//                AttributeInstance attributeInstance = originalAttributable.getAttribute(attribute);
//                if (attributeInstance != null && attributeInstance.getModifiers() != null) {
//                    for (AttributeModifier modifier : attributeInstance.getModifiers()) {
//                        AttributeInstance copyInstance = copyAttributable.getAttribute(attribute);
//                        if (copyInstance.getModifiers().contains(modifier)) {
//                            continue;
//                        }
//                        copyInstance.addModifier(modifier);
//                    }
//                }
//            }
//        }

        if (original instanceof Colorable && copy instanceof Colorable) {
            ((Colorable) copy).setColor(((Colorable) original).getColor());
        }

        if (original instanceof Creature && copy instanceof Creature) {
            ((Creature) copy).setTarget(((Creature) original).getTarget());
        }

        if (original instanceof Creeper && copy instanceof Creeper) {
            ((Creeper) copy).setPowered(((Creeper) original).isPowered());
        }

        if (original instanceof Damageable && copy instanceof Damageable) {
            ((Damageable) copy).setMaxHealth(((Damageable) original).getMaxHealth());
        }

        if (original instanceof EnderDragon && copy instanceof EnderDragon) {
            ((EnderDragon) copy).setPhase(((EnderDragon) original).getPhase());
        }

        // Enderman: held block drops on death, do not set to prevent dupes

        if (original instanceof Guardian && copy instanceof Guardian) {
            ((Guardian) copy).setElder(((Guardian) original).isElder());
        }

        if (original instanceof Horse && copy instanceof Horse) {
            Horse originalHorse = (Horse) original;
            Horse copyHorse = (Horse) copy;
            copyHorse.setVariant(originalHorse.getVariant());
            copyHorse.setCarryingChest(originalHorse.isCarryingChest());
            copyHorse.setColor(originalHorse.getColor());
            copyHorse.setDomestication(originalHorse.getDomestication());
            // Don't set jump strength - prevent duplication of good horses
            copyHorse.setJumpStrength(originalHorse.getJumpStrength());
            copyHorse.setMaxDomestication(originalHorse.getMaxDomestication());
            copyHorse.setStyle(originalHorse.getStyle());
        }

        if (original instanceof IronGolem && copy instanceof IronGolem) {
            ((IronGolem) copy).setPlayerCreated(((IronGolem) original).isPlayerCreated());
        }

        if (original instanceof LivingEntity && copy instanceof LivingEntity) {
            LivingEntity originalLiving = (LivingEntity) original;
            LivingEntity copyLiving = (LivingEntity) copy;
            for (PotionEffect effect : originalLiving.getActivePotionEffects()) {
                copyLiving.addPotionEffect(effect);
            }
            copyLiving.setAI(originalLiving.hasAI());
            copyLiving.setCanPickupItems(originalLiving.getCanPickupItems());
            copyLiving.setCollidable(originalLiving.isCollidable());
            copyLiving.setGliding(originalLiving.isGliding());
            copyLiving.setLastDamage(originalLiving.getLastDamage());
            // TODO: re-leash without causing leash dupe bugs?
            copyLiving.setMaximumAir(originalLiving.getMaximumAir());
            copyLiving.setMaximumNoDamageTicks(originalLiving.getMaximumNoDamageTicks());
            copyLiving.setNoDamageTicks(originalLiving.getNoDamageTicks());
            copyLiving.setRemainingAir(originalLiving.getRemainingAir());
            copyLiving.setRemoveWhenFarAway(originalLiving.getRemoveWhenFarAway());
        }

        if (original instanceof Ocelot && copy instanceof Ocelot) {
            Ocelot originalOcelot = (Ocelot) original;
            Ocelot copyOcelot = (Ocelot) copy;
            copyOcelot.setCatType(originalOcelot.getCatType());
            copyOcelot.setSitting(originalOcelot.isSitting());
        }

        // Pig: Saddle drops on death, do not set to prevent dupes

        if (original instanceof PigZombie && copy instanceof PigZombie) {
            ((PigZombie) copy).setAnger(((PigZombie) original).getAnger());
        }

        if (original instanceof Rabbit && copy instanceof Rabbit) {
            ((Rabbit) copy).setRabbitType(((Rabbit) original).getRabbitType());
        }

        if (original instanceof Sheep && copy instanceof Sheep) {
            ((Sheep) copy).setSheared(((Sheep) original).isSheared());
        }

        if (original instanceof Skeleton && copy instanceof Skeleton) {
            ((Skeleton) copy).setSkeletonType(((Skeleton) original).getSkeletonType());
        }

        if (original instanceof Slime && copy instanceof Slime) {
            ((Slime) copy).setSize(((Slime) original).getSize());
        }

        if (original instanceof Snowman && copy instanceof Snowman) {
            ((Snowman) copy).setDerp(((Snowman) original).isDerp());
        }

        if (original instanceof Tameable && copy instanceof Tameable) {
            Tameable originalTameable = (Tameable) original;
            Tameable copyTameable = (Tameable) copy;
            copyTameable.setOwner(originalTameable.getOwner());
            copyTameable.setTamed(originalTameable.isTamed());
        }

        if (original instanceof Villager && copy instanceof Villager) {
            Villager originalVillager = (Villager) original;
            Villager copyVillager = (Villager) copy;
            copyVillager.setProfession(originalVillager.getProfession());
            copyVillager.setRecipes(originalVillager.getRecipes());
            copyVillager.setRiches(originalVillager.getRiches());
        }

        if (original instanceof Wolf && copy instanceof Wolf) {
            Wolf originalWolf = (Wolf) original;
            Wolf copyWolf = (Wolf) copy;
            copyWolf.setAngry(originalWolf.isAngry());
            copyWolf.setCollarColor(originalWolf.getCollarColor());
            copyWolf.setSitting(originalWolf.isSitting());
        }

        if (original instanceof Zombie && copy instanceof Zombie) {
            Zombie originalZombie = (Zombie) original;
            Zombie copyZombie = (Zombie) copy;
            copyZombie.setBaby(originalZombie.isBaby());
            copyZombie.setVillagerProfession(originalZombie.getVillagerProfession());
        }

        /*
         * Use reflection to copy all metadata to the new entity. Note: This is dangerous and kinda
         * dumb. Not necessarily a good idea, but it prevents us breaking plugins such as mcMMO.
         * It may also cause us to break other plugins.
         */
        try {
            Method getEntityMetadata = plugin.getServer().getClass().getMethod("getEntityMetadata");
            Object entityMetadata = getEntityMetadata.invoke(plugin.getServer());
            Field metadataMapField = entityMetadata.getClass().getSuperclass().getDeclaredField("metadataMap");
            metadataMapField.setAccessible(true);
            Object metadataMap = metadataMapField.get(entityMetadata);
            @SuppressWarnings("unchecked")
            Map<String, Map<Plugin, MetadataValue>> internal = (Map<String, Map<Plugin, MetadataValue>>) metadataMap;
            String identifier = original.getUniqueId().toString() + ':';
            for (Object entryObject : internal.entrySet().toArray()) {
                @SuppressWarnings("unchecked")
                Entry<String, Map<Plugin, MetadataValue>> entry = (Entry<String, Map<Plugin, MetadataValue>>) entryObject;
                if (!entry.getKey().startsWith(identifier)) {
                    continue;
                }
                String key = entry.getKey().substring(identifier.length());
                for (MetadataValue value : entry.getValue().values().toArray(new MetadataValue[entry.getValue().size()])) {
                    copy.setMetadata(key, value);
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | NoSuchFieldException
                | ClassCastException e) {
            // Eat exception, internal changes
        }
    }

    /**
     * Used to get the number of mobs represented by a stack.
     * 
     * @param entity the entity
     * @return the number of mobs
     */
    public static int getStackSize(Entity entity) {

        if (!entity.hasMetadata("quantity")) {
            return 1;
        }

        List<MetadataValue> list = entity.getMetadata("quantity");
        if (list.isEmpty()) {
            return 1;
        }

        // Sanity, return at least 1.
        return Math.max(1, list.get(0).asInt());
    }

    /**
     * Sets an entity's stack size and updates its name.
     * 
     * @param entity the Entity
     * @param newQuantity the new quantity
     */
    private void setStackSize(Entity entity, int newQuantity) {

        // Sanity
        newQuantity = Math.max(1, newQuantity);

        // Set metadata
        setMetadata(entity, "quantity", newQuantity);

        // Rename to match new quantity
        nameStack(entity, newQuantity);
    }

    /**
     * Sets a stack's name.
     * 
     * @param entity the Entity
     * @param quantity the amount in the stack
     */
    private void nameStack(Entity entity, int quantity) {

        String configNaming = plugin.getConfig().getString("stack-naming");

        // Configured to not rename entities, don't overwrite existing names.
        if (configNaming == null || configNaming.length() < 1) {
            return;
        }

        // Hide name, stack size is 1.
        if (quantity < 2) {
            entity.setCustomName(null);
            entity.setCustomNameVisible(false);
            return;
        }

        configNaming = configNaming.replace("{QTY}", String.valueOf(quantity));
        configNaming = configNaming.replace("{TYPE}", entity.getType().toString().replace("_", " "));
        configNaming = ChatColor.translateAlternateColorCodes('&', configNaming);
        entity.setCustomName(configNaming);

        if (plugin.getConfig().getBoolean("name-always-visible")) {
            entity.setCustomNameVisible(true);
        }

    }

    /**
     * Checks whether a String matches the configured naming pattern for stacked mobs.
     * <p>
     * This method will also return true if the name provided is null.
     * 
     * @param name the String to check
     * @return true if the naming pattern matches or the name provided is null
     */
    public boolean matchesStackName(String name) {
        if (name == null) {
            return true;
        }

        // Ensure we have a match with the name pattern. Remove all quote start/ends to prevent issues.
        Matcher matcher = namePattern.matcher(name.replace("\\Q", "").replace("\\E", ""));

        return matcher.matches();
    }

    /**
     * Sets stack data for an entity based on its name.
     * 
     * @param entity the Entity
     */
    public void loadFromName(Entity entity) {
        String name = entity.getCustomName();

        // Check if name is set and naming pattern contains quantity tag
        if (name == null || !plugin.getConfig().getString("stack-naming").contains("{QTY}")) {
            return;
        }

        // Create a matcher for the Pattern. Remove all quote start/ends to prevent issues.
        Matcher matcher = namePattern.matcher(name.replace("\\Q", "").replace("\\E", ""));

        // Ensure we have a match.
        if (!matcher.matches()) {
            return;
        }

        // Parse the number from the name.
        int size;
        try {
            size = Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException e) {
            // Given the regex used it shouldn't be possible to hit this, but better safe than sorry.
            return;
        }

        setStackSize(entity, size);
    }

    /**
     * Gets whether an entity is stackable.
     * 
     * @param entity the Entity
     * @return true if the Entity is stackable
     */
    public boolean isStackable(Entity entity) {
        // HumanEntities are not allowed to stack, period.
        return !(entity instanceof HumanEntity) && isMetaStackable(entity) && isConfigStackable(entity);
    }

    private boolean isMetaStackable(Entity entity) {

        if (entity.hasMetadata("stackable") && !entity.getMetadata("stackable").get(0).asBoolean()) {
            return false;
        }

        // Do not allow entities bred within the last 15 seconds to stack at all
        if (entity.hasMetadata("lastBred")
                && entity.getMetadata("lastBred").get(0).asLong() < System.currentTimeMillis() - 15000) {
            return false;
        }

        return true;
    }

    private boolean isConfigStackable(Entity entity) {
        return plugin.getConfig().getBoolean("stack-mob-type." + entity.getType().name(), false)
                && !plugin.getConfig().getStringList("blacklist-world").contains(entity.getWorld().getName().toLowerCase())
                && (plugin.getConfig().getBoolean("stack-custom-named-mobs") || matchesStackName(entity.getCustomName()));
    }

    /**
     * Alters whether an entity is stackable. Note that this will not allow for stacks exceeding the
     * configured limit.
     * 
     * @param entity the Entity
     * @param stackable if the Entity is stackable
     */
    public void setStackable(Entity entity, boolean stackable) {
        setMetadata(entity, "stackable", stackable);
    }

    /**
     * Sets an entity recently bred.
     * 
     * @param entity the Entity
     */
    public void setBred(Entity entity) {
        setMetadata(entity, "lastBred", System.currentTimeMillis());
    }

    /**
     * Gets an entity's breedability.
     * 
     * @param entity the Entity
     */
    public boolean canBreed(Entity entity) {
        return !entity.hasMetadata("lastBred") || entity.getMetadata("lastBred").get(0).asLong() < System.currentTimeMillis() - 300000L;
    }

    /**
     * Gets the average health of a stacked entity.
     * 
     * @param entity the Entity
     * @param recalculate true if the value is to be recalculated including the base entity's health
     * @return the average health
     */
    public double getAverageHealth(Entity entity, boolean recalculate) {

        /*
         * Default to 0 health for anything that isn't actually damageable.
         * This prevents large stacks living excessively long, provided the plugin is configured to kill them.
         */
        if (!(entity instanceof Damageable)) {
            return 0;
        }

        List<MetadataValue> list;

        // Check if metadata is set.
        if (recalculate || !entity.hasMetadata("stackAverageHealth")
                || (list = entity.getMetadata("stackAverageHealth")).isEmpty()) {

            // Check if the entity is a Damageable.
            if (entity instanceof Damageable) {
                Damageable damageable = (Damageable) entity;

                // Are we supposed to include current health in calculation?
                if (!recalculate) {
                    return damageable.getMaxHealth();
                }

                int stackSize = getStackSize(entity);
                double currentHealth = damageable.getHealth();

                // If the stack is larger than 1, average total maximum health in.
                if (stackSize > 1) {
                    currentHealth += (stackSize - 1) * damageable.getMaxHealth();
                    currentHealth /= stackSize;
                }

                // Return the current health.
                return currentHealth;
            }

            return 0;
        }

        return list.get(0).asDouble();
    }

    /**
     * Reduces the average health of a stack by the specified amount.
     * 
     * @param entity the stacked Entity
     * @param amount the amount to reduce health by
     */
    public void damageAverageHealth(Entity entity, Double amount) {
        setMetadata(entity, "stackAverageHealth", getAverageHealth(entity, false) - amount);
    }

    /**
     * Increases the average health of a stack by adding an entity's stack data.
     * </p>
     * This method assumes that the stack size has already been increased by the relevant amount.
     * 
     * @param entity the Entity
     * @param addedSize the amount of entities added
     * @param addedHealth the average health of the added entities
     */
    private void increaseAverageHealth(Entity entity, int addedSize, double addedHealth) {
        double averageHealth = getAverageHealth(entity, false);

        // Entity is undamageable or dead.
        if (averageHealth <= 0 || !(entity instanceof Damageable)) {
            return;
        }

        averageHealth += addedSize * addedHealth;
        averageHealth /= getStackSize(entity);

        averageHealth = Math.min(((Damageable) entity).getMaxHealth(), averageHealth);
    }

    /**
     * Helper method for replacing existing metadata with our own for simplicity.
     * 
     * @param entity the Entity to set metadata for
     * @param key the metadata identifier
     * @param value the metadata value
     */
    private void setMetadata(Entity entity, String key, Object value) {
        if (entity.hasMetadata(key)) {
            // Remove existing meta
            for (Iterator<MetadataValue> iterator = entity.getMetadata(key).iterator(); iterator.hasNext();) {
                entity.removeMetadata(key, iterator.next().getOwningPlugin());
            }
        }

        entity.setMetadata(key, new FixedMetadataValue(plugin, value));
    }

}
