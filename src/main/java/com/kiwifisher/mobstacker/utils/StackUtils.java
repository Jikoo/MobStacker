package com.kiwifisher.mobstacker.utils;

import com.github.jikoo.util.Pair;
import com.kiwifisher.mobstacker.MobStacker;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Cat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Steerable;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The main guts of this plugin. All the stacking, peeling, searching and renaming is done here.
 */
public class StackUtils {

    private final MobStacker plugin;
    private final NamespacedKey stackSize, canStack, stackAverageHealth, stackBreedTime;

    private Pattern namePattern;

    public StackUtils(MobStacker plugin) {
        this.plugin = plugin;

        this.loadConfig();
        stackSize = new NamespacedKey(plugin, "stackSize");
        canStack = new NamespacedKey(plugin, "canStack");
        stackAverageHealth = new NamespacedKey(plugin, "stackAverageHealth");
        stackBreedTime = new NamespacedKey(plugin, "stackBreedTime");
    }

    public void loadConfig() {
        String naming = plugin.getConfig().getString("stack-naming");
        if (naming == null) {
            naming = "&e&l{QTY}X &6&l{TYPE}";
        }

        // Translate chat colors and remove all quote start/ends to prevent issues.
        naming = ChatColor.translateAlternateColorCodes('&', naming).replace("\\Q", "")
                .replace("\\E", "");

        // Replace quantity and type tags with regular expressions to match any valid names
        naming = naming.replace("{QTY}", "\\E([0-9]+)\\Q").replace("{TYPE}", "\\E[A-Z ]+\\Q");

        // Prepend quote start and append quote end

        namePattern = Pattern.compile("\\Q" + naming + "\\E");
    }

    public void attemptToStack(final LivingEntity stack, final int attempts) {

        if (!plugin.isStacking() || !isStackable(stack)) {
            return;
        }

        // Get max stack size. Specific entity type overrides
        final int maxStackSize = plugin.getMaxStackSize(stack.getType());

        if (maxStackSize < 2) {
            setStackable(stack, false);
            return;
        }

        new BukkitRunnable() {

            LivingEntity entity = stack;
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
                    if (!(nearbyEntity instanceof LivingEntity)) {
                        continue;
                    }

                    // Attempt to stack
                    Pair<LivingEntity, Boolean> pair = stackEntities(entity, (LivingEntity) nearbyEntity, maxStackSize);

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
    public Pair<LivingEntity, Boolean> stackEntities(LivingEntity entity1, LivingEntity entity2, int maxStackSize) {

        LivingEntity mergeTo = entity1;
        LivingEntity removed = entity2;
        // If entities are supposed to stack down, ensure mergeTo is the lower one.
        if (entity1.getLocation().getBlockY() > entity2.getLocation().getBlockY()
                && plugin.getConfig().getBoolean("stack-mobs-down")) {
            mergeTo = entity2;
            removed = entity1;
        }

        int additionalStacks = getStackSize(removed);
        int newStackSize = getStackSize(mergeTo) + additionalStacks;

        if (!canStack(mergeTo, removed, maxStackSize, newStackSize)) {
            return new Pair<>(entity1, false);
        }

        // Get the current average health of the added entity for later use.
        double addedAverageHealth = getAverageHealth(removed, true);

        // Set stack size to new stack size.
        setStackSize(mergeTo, newStackSize);

        // TODO: mergeProperties method - merge potion effects, meta, etc.? For now, using copyEntityProperties
        try {
            copyEntityProperties(mergeTo, removed);
        } catch (Exception e) {
            // Catch generic exception to prevent unforeseen issues causing entity duplication.
            e.printStackTrace();
        }

        increaseAverageHealth(mergeTo, additionalStacks, addedAverageHealth);

        // Remove merged entity
        removed.remove();

        return new Pair<>(mergeTo, true);

    }

    /**
     * Check if two entities are similar enough to be stacked.
     *
     * @param entity1 the first entity
     * @param entity2 the second entity
     * @return true if the mobs can stack
     */
    private boolean canStack(LivingEntity entity1, LivingEntity entity2, int maxStackSize, int newStackSize) {

        // Ensure entities are alive, of the same type, stackable, and won't exceed max stack size.
        if (entity1.equals(entity2) || entity1.isDead() || entity2.isDead()
                || entity1.getType() != entity2.getType() || !isStackable(entity1)
                || !isStackable(entity2) || maxStackSize < newStackSize) {
            return false;
        }

        long lastBred1 = entity1.getPersistentDataContainer().getOrDefault(stackBreedTime, PersistentDataType.LONG, 0L);
        long lastBred2 = entity2.getPersistentDataContainer().getOrDefault(stackBreedTime, PersistentDataType.LONG, 0L);
        // Breeding status takes 5 minutes to reset.
        long irrelevantAfter = System.currentTimeMillis() - 300000L;

        /*
         * If entities have been bred recently, only more recently bred entity is allowed to be stacked onto.
         */
        if (lastBred1 > irrelevantAfter && lastBred2 < irrelevantAfter
                || lastBred1 <= irrelevantAfter && lastBred2 > irrelevantAfter) {
            return false;
        }

        // TODO ensure items are not deleted (i.e. zombie picks up player gear, zombie is merged onto a stack)
        // May require additional tags applied on entity item pick up

        // Prevent accidental despawns
        if (entity1.getRemoveWhenFarAway() != entity2.getRemoveWhenFarAway()) {
            return false;
        }

        // No-AI property
        if (plugin.getConfig().getBoolean("stack-properties.AI", true)
                && entity1.hasAI() != entity2.hasAI()) {
            return false;
        }

        // Leashed mobs
        if (!plugin.getConfig().getBoolean("stack-leashed-mobs", false)) {
            if (entity1.isLeashed() || entity2.isLeashed()) {
                return false;
            }
        } else if (entity1.isLeashed() != entity1.isLeashed()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.age", true)
                && entity1 instanceof Ageable && entity2 instanceof Ageable
                && ((Ageable) entity1).isAdult() != ((Ageable) entity2).isAdult()) {
            return false;
        }

        if (entity1 instanceof Bee && entity2 instanceof Bee && ((Bee) entity1).hasNectar() != ((Bee) entity2).hasNectar()) {
            return false;
        }

        boolean checkVariants = plugin.getConfig().getBoolean("stack-properties.variant", true);

        if (checkVariants && entity1 instanceof Colorable && entity2 instanceof Colorable
                && ((Colorable) entity1).getColor() != ((Colorable) entity2).getColor()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.creeper.powered", true)
                && entity1 instanceof Creeper && entity2 instanceof Creeper
                && ((Creeper) entity1).isPowered() != ((Creeper) entity2).isPowered()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.maxHealth", true)) {
            AttributeInstance maxHealth1 = entity1.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            AttributeInstance maxHealth2 = entity2.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (maxHealth1 != null && maxHealth2 != null && maxHealth1.getValue() != maxHealth2.getValue()) {
                return false;
            }
        }

        if (entity2 instanceof AbstractHorse) {
            AbstractHorse horse2 = (AbstractHorse) entity2;

            // Don't delete saddles, armor, llama decor, or items in chests.
            for (ItemStack itemStack : horse2.getInventory().getContents()) {
                //noinspection ConstantConditions - Array is not null, but elements may be.
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    return false;
                }
            }
        }

        if (checkVariants && entity1 instanceof Horse && entity2 instanceof Horse
                && (((Horse) entity1).getStyle() != ((Horse) entity2).getStyle() || ((Horse) entity1).getColor() != ((Horse) entity2).getColor())) {
            return false;
        }

        if (entity1 instanceof ChestedHorse && entity2 instanceof ChestedHorse
                && ((ChestedHorse) entity1).isCarryingChest() != ((ChestedHorse) entity2).isCarryingChest()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.golem.playerCreated", true)
                && entity1 instanceof IronGolem && entity2 instanceof IronGolem
                &&((IronGolem) entity1).isPlayerCreated() != ((IronGolem) entity2).isPlayerCreated()) {
            return false;
        }

        if (checkVariants && entity1 instanceof Cat && entity2 instanceof Cat
                && ((Cat) entity1).getCatType() != ((Cat) entity2).getCatType()) {
            return false;
        }

        if (checkVariants && entity1 instanceof Fox && entity2 instanceof Fox
                && ((Fox) entity1).getFoxType() != ((Fox) entity2).getFoxType()) {
            return false;
        }

        if (checkVariants && entity1 instanceof Panda && entity2 instanceof Panda
                && ((Panda) entity1).getMainGene() != ((Panda) entity2).getMainGene()) {
            return false;
        }

        if (checkVariants && entity1 instanceof Parrot && entity2 instanceof Parrot
                && ((Parrot) entity1).getVariant() != ((Parrot) entity2).getVariant()) {
            return false;
        }

        if (entity1 instanceof PiglinAbstract && entity2 instanceof PiglinAbstract
                && ((PiglinAbstract) entity1).isImmuneToZombification() != ((PiglinAbstract) entity2).isImmuneToZombification()) {
            return false;
        }

        if (entity1 instanceof Piglin && entity2 instanceof Piglin
                && ((Piglin) entity1).isAbleToHunt() != ((Piglin) entity2).isAbleToHunt()) {
            return false;
        }

        if (checkVariants && entity1 instanceof Rabbit && entity2 instanceof Rabbit
                && ((Rabbit) entity1).getRabbitType() != ((Rabbit) entity2).getRabbitType()) {
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.sheep.sheared", true)
                && entity1 instanceof Sheep && entity2 instanceof Sheep
                && ((Sheep) entity1).isSheared() != ((Sheep) entity2).isSheared()) {
            return false;
        }

        // Separate slimes by size
        if (entity1 instanceof Slime && entity2 instanceof Slime
                && ((Slime) entity1).getSize() != ((Slime) entity2).getSize()) {
            return false;
        }

        if (entity1 instanceof Steerable && entity2 instanceof Steerable && ((Steerable) entity2).hasSaddle()) {
            // Don't stack saddled pigs or striders - saddle is intentionally not set on split.
            return false;
        }

        if (plugin.getConfig().getBoolean("stack-properties.tameable", true)
                && entity1 instanceof Tameable && entity2 instanceof Tameable) {
            Tameable tame1 = (Tameable) entity1;
            Tameable tame2 = (Tameable) entity2;
            if (tame1.isTamed() != tame2.isTamed() || tame1.isTamed() && (tame1.getOwner() == tame2.getOwner()
                    || tame1.getOwner() != null && !tame1.getOwner().equals(tame2.getOwner()))) {
                return false;
            }
        }

        if (entity1 instanceof TropicalFish && entity2 instanceof TropicalFish) {
            TropicalFish fish1 = ((TropicalFish) entity1);
            TropicalFish fish2 = (TropicalFish) entity2;

            if (checkVariants && (fish1.getBodyColor() != fish2.getBodyColor() || fish1.getPatternColor() != fish2.getPatternColor()
                    || fish1.getPattern() != fish2.getPattern())) {
                return false;
            }

        }

        if (entity1 instanceof Villager && entity2 instanceof Villager
                && ((Villager) entity2).getVillagerLevel() > 1) {
            return false;
        }

        if (entity1 instanceof Wolf && entity2 instanceof Wolf) {
            Wolf wolf1 = (Wolf) entity1;
            Wolf wolf2 = (Wolf) entity2;
            if (plugin.getConfig().getBoolean("stack-properties.wolf.anger", true)
                    && wolf1.isAngry() != wolf2.isAngry()) {
                return false;
            }
            // Suppress inspection for easy reordering
            //noinspection RedundantIfStatement
            if (checkVariants
                    && wolf1.getCollarColor() != wolf2.getCollarColor()) {
                return false;
            }
        }

        return true;
    }

    /**
     * This method takes a stack and turns it into a single mob, spawning in a new stack with the
     * remainder in the same location.
     *
     * @param entity The stack to peel off of
     * @return the new stack
     */
    public LivingEntity peelOffStack(LivingEntity entity) {
       return peelOff(entity, true);
    }

    /**
     * This method takes a stack and removes a single entity, spawning it in the same location.
     *
     * @param entity The stack to peel off of
     * @return the new entity
     */
    public LivingEntity peelOffSingle(LivingEntity entity) {
        return peelOff(entity, false);
    }

    private LivingEntity peelOff(LivingEntity entity, boolean stack) {

        // Get the new stack size post-peel.
        int newQuantity = getStackSize(entity) - 1;

        // If stacking is disabled, remove the stack data.
        if (!plugin.isStacking() || !isStackable(entity)) {
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
        LivingEntity newEntity = entity.getWorld().spawn(entity.getLocation(), entity.getClass());

        // Copy properties to the new entity.
        try {
            copyEntityProperties(entity, newEntity);
        } catch (Exception e) {
            // Catch generic exception to prevent unforeseen issues causing entity dupes
            e.printStackTrace();
        }

        // Set entity health to the stack's average.
        double averageHealth = getAverageHealth(entity, false);
        if (averageHealth < 1) {
            averageHealth = 1;
        }
        newEntity.setHealth(averageHealth);

        if (stack) {
            // Set the new stack to the remaining amount.
            setStackSize(newEntity, newQuantity);
        } else {
            // Set the new stack's size.
            setStackSize(newEntity, 1);

            // Set the old stack's new size.
            setStackSize(entity, newQuantity);
        }

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
    @SuppressWarnings("RedundantThrows") // Occasionally an unforseen change or special case will cause an illegal state
    private void copyEntityProperties(LivingEntity original, LivingEntity copy) throws Exception {

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
        copy.setPortalCooldown(original.getPortalCooldown());
        copy.setSilent(original.isSilent());
        /*
         * Special case: Cannot set ticks lived under 1. Occasionally, an entity will be copied
         * before it has lived a tick.
         */
        copy.setTicksLived(Math.max(1, original.getTicksLived()));
        /*
         * Of note: When splitting a stack, the new entity is spawned after the hit knockback takes
         * effect but before the knockback enchantment is applied. The resulting velocity is NOT
         * affected by the knockback enchantment.
         */
        copy.setVelocity(original.getVelocity());
        copy.setAI(original.hasAI());
        copy.setCanPickupItems(original.getCanPickupItems());
        copy.setCollidable(original.isCollidable());
        copy.setGliding(original.isGliding());
        copy.setLastDamage(original.getLastDamage());
        if (original.isLeashed()) {
            Entity leashHolder = original.getLeashHolder();
            original.setLeashHolder(null);
            copy.setLeashHolder(leashHolder);
        }
        copy.setMaximumAir(original.getMaximumAir());
        copy.setMaximumNoDamageTicks(original.getMaximumNoDamageTicks());
        copy.setNoDamageTicks(0);
        copy.setRemainingAir(original.getRemainingAir());
        copy.setRemoveWhenFarAway(original.getRemoveWhenFarAway());

        if (original instanceof AbstractHorse && copy instanceof AbstractHorse) {
            AbstractHorse originalAbstractHorse = (AbstractHorse) original;
            AbstractHorse copyAbstractHorse = (AbstractHorse) copy;
            copyAbstractHorse.setDomestication(originalAbstractHorse.getDomestication());
            // Don't set jump strength - prevent duplication of good horses
            copyAbstractHorse.setJumpStrength(originalAbstractHorse.getJumpStrength());
            copyAbstractHorse.setMaxDomestication(originalAbstractHorse.getMaxDomestication());
        }

        if (original instanceof Ageable && copy instanceof Ageable) {
            ((Ageable) copy).setAge(((Ageable) original).getAge());
        }

        if (original instanceof Bat && copy instanceof Bat) {
            ((Bat) copy).setAwake(((Bat) original).isAwake());
        }

        if (original instanceof Bee && copy instanceof Bee) {
            Bee originalitbee = (Bee) original;
            Bee copbee = (Bee) copy;
            copbee.setAnger(originalitbee.getAnger());
            copbee.setCannotEnterHiveTicks(originalitbee.getCannotEnterHiveTicks());
            copbee.setFlower(originalitbee.getFlower());
            copbee.setHasNectar(originalitbee.hasNectar());
            // Do not copy sting state - kills entire stacks
            copbee.setHive(originalitbee.getHive());
        }

        if (original instanceof Breedable && copy instanceof Breedable) {
            Breedable originalBreedable = (Breedable) original;
            Breedable copyBreedable = (Breedable) copy;
            copyBreedable.setAgeLock(originalBreedable.getAgeLock());
            copyBreedable.setBreed(originalBreedable.canBreed());

        }

        if (original instanceof Cat && copy instanceof Cat) {
            ((Cat) copy).setCatType(((Cat) original).getCatType());
        }

        if (original instanceof Colorable && copy instanceof Colorable) {
            ((Colorable) copy).setColor(((Colorable) original).getColor());
        }

        if (original instanceof ChestedHorse && copy instanceof ChestedHorse) {
            ((ChestedHorse) copy).setCarryingChest(((ChestedHorse) original).isCarryingChest());
        }

        if (original instanceof Creature && copy instanceof Creature) {
            ((Creature) copy).setTarget(((Creature) original).getTarget());
        }

        if (original instanceof Creeper && copy instanceof Creeper) {
            Creeper originalCreeper = (Creeper) original;
            Creeper copyCreeper = (Creeper) copy;
            copyCreeper.setExplosionRadius(originalCreeper.getExplosionRadius());
            copyCreeper.setMaxFuseTicks(originalCreeper.getMaxFuseTicks());
            copyCreeper.setPowered(originalCreeper.isPowered());
        }

        if (original instanceof EnderDragon && copy instanceof EnderDragon) {
            ((EnderDragon) copy).setPhase(((EnderDragon) original).getPhase());
        }

        // Enderman: held block drops on death, do not set to prevent dupes

        if (original instanceof Fox && copy instanceof Fox) {
            ((Fox) copy).setFoxType(((Fox) original).getFoxType());
        }

        if (original instanceof Horse && copy instanceof Horse) {
            Horse originalHorse = (Horse) original;
            Horse copyHorse = (Horse) copy;
            copyHorse.setColor(originalHorse.getColor());
            copyHorse.setStyle(originalHorse.getStyle());
        }

        if (original instanceof Llama && copy instanceof Llama) {
            ((Llama) copy).setColor(((Llama) original).getColor());
        }

        if (original instanceof IronGolem && copy instanceof IronGolem) {
            ((IronGolem) copy).setPlayerCreated(((IronGolem) original).isPlayerCreated());
        }

        for (PotionEffect effect : original.getActivePotionEffects()) {
            copy.addPotionEffect(effect);
        }

        if (original instanceof Mob && copy instanceof Mob) {
            ((Mob) copy).setTarget(((Mob) original).getTarget());
        }

        if (original instanceof Panda && copy instanceof Panda) {
            ((Panda) copy).setMainGene(((Panda) original).getMainGene());
        }

        if (original instanceof Parrot && copy instanceof Parrot) {
            ((Parrot) copy).setVariant(((Parrot) original).getVariant());
        }

        if (original instanceof PiglinAbstract && copy instanceof PiglinAbstract) {
            PiglinAbstract originalPiglin = (PiglinAbstract) original;
            PiglinAbstract copyPiglin = (PiglinAbstract) copy;
            copyPiglin.setConversionTime(originalPiglin.getConversionTime());
            copyPiglin.setImmuneToZombification(originalPiglin.isImmuneToZombification());
        }

        if (original instanceof Piglin && copy instanceof Piglin) {
            ((Piglin) copy).setIsAbleToHunt(((Piglin) original).isAbleToHunt());
        }

        if (original instanceof PigZombie && copy instanceof PigZombie) {
            ((PigZombie) copy).setAnger(((PigZombie) original).getAnger());
        }

        if (original instanceof Rabbit && copy instanceof Rabbit) {
            ((Rabbit) copy).setRabbitType(((Rabbit) original).getRabbitType());
        }

        if (original instanceof Sheep && copy instanceof Sheep) {
            ((Sheep) copy).setSheared(((Sheep) original).isSheared());
        }

        if (original instanceof Sittable && copy instanceof Sittable) {
            ((Sittable) copy).setSitting(((Sittable) original).isSitting());
        }

        if (original instanceof Slime && copy instanceof Slime) {
            ((Slime) copy).setSize(((Slime) original).getSize());
        }

        if (original instanceof Snowman && copy instanceof Snowman) {
            ((Snowman) copy).setDerp(((Snowman) original).isDerp());
        }

        // Steerable: Saddle drops on death, do not set to prevent dupes

        if (original instanceof Tameable && copy instanceof Tameable) {
            Tameable originalTameable = (Tameable) original;
            Tameable copyTameable = (Tameable) copy;
            copyTameable.setOwner(originalTameable.getOwner());
            copyTameable.setTamed(originalTameable.isTamed());
        }

        if (original instanceof TropicalFish && copy instanceof TropicalFish) {
            TropicalFish originalFish = (TropicalFish) original;
            TropicalFish copyFish = (TropicalFish) copy;
            copyFish.setBodyColor(originalFish.getBodyColor());
            copyFish.setPattern(originalFish.getPattern());
            copyFish.setPatternColor(originalFish.getPatternColor());
        }

        if (original instanceof Villager && copy instanceof Villager) {
            Villager copyVillager = (Villager) copy;
            copyVillager.setVillagerLevel(0);
            if (copyVillager.getProfession() != Villager.Profession.NITWIT) {
                copyVillager.setProfession(Villager.Profession.NONE);
            }
        }

        if (original instanceof Wolf && copy instanceof Wolf) {
            Wolf originalWolf = (Wolf) original;
            Wolf copyWolf = (Wolf) copy;
            copyWolf.setAngry(originalWolf.isAngry());
            copyWolf.setCollarColor(originalWolf.getCollarColor());
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
                for (MetadataValue value : entry.getValue().values().toArray(new MetadataValue[0])) {
                    copy.setMetadata(key, value);
                }
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | NoSuchFieldException
                | ClassCastException e) {
            // Eat exception, internal changes
        }

        // TODO: Config option? If stack-down and horses enabled, could be abused to duplicate nice horses.
        // Copy attributes last - they are the most error-prone.
        for (Attribute attribute : Attribute.values()) {
            AttributeInstance attributeInstance = original.getAttribute(attribute);
            if (attributeInstance != null) {
                for (AttributeModifier modifier : attributeInstance.getModifiers()) {
                    AttributeInstance copyInstance = copy.getAttribute(attribute);
                    if (copyInstance == null || copyInstance.getModifiers().contains(modifier)) {
                        continue;
                    }
                    try {
                        copyInstance.addModifier(modifier);
                    } catch (IllegalArgumentException e) {
                        // Shouldn't be possible,
                    }
                }
            }
        }
    }

    /**
     * Used to get the number of mobs represented by a stack.
     *
     * @param entity the entity
     * @return the number of mobs
     */
    public int getStackSize(LivingEntity entity) {

        Integer quantity = entity.getPersistentDataContainer().get(stackSize, PersistentDataType.INTEGER);
        if (quantity == null) {
            return 1;
        }

        // Sanitize stack size to a minimum of 1 and a max of max stack size.
        return Math.max(1, Math.min(plugin.getMaxStackSize(entity.getType()), quantity));
    }

    /**
     * Sets an entity's stack size and updates its name.
     *
     * @param entity the entity stack
     * @param newQuantity the new quantity
     */
    public void setStackSize(@NotNull LivingEntity entity, int newQuantity) {

        // Sanity
        newQuantity = Math.max(1, Math.min(plugin.getMaxStackSize(entity.getType()), newQuantity));

        // Set data
        entity.getPersistentDataContainer().set(stackSize, PersistentDataType.INTEGER, newQuantity);

        // Rename to match new quantity
        nameStack(entity, newQuantity);
    }

    /**
     * Sets a stack's name.
     *
     * @param entity the entity stack to name
     * @param quantity the amount in the stack
     */
    private void nameStack(@NotNull LivingEntity entity, int quantity) {

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
    public boolean matchesStackName(@Nullable String name) {
        if (name == null) {
            return true;
        }

        // Ensure we have a match with the name pattern. Remove all quote start/ends to prevent issues.
        return namePattern.matcher(Pattern.quote(name)).matches();
    }

    /**
     * Sets stack data for an entity based on its name.
     *
     * @param entity the entity
     */
    public void loadFromName(@NotNull LivingEntity entity) {
        String name = entity.getCustomName();

        // Check if name is set and naming pattern contains quantity tag
        if (name == null || !namePattern.pattern().contains("{QTY}")) {
            return;
        }

        // Create a matcher for the Pattern. Remove all quote start/ends to prevent issues.
        Matcher matcher = namePattern.matcher(Pattern.quote(name));

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
     * @param entity the entity
     * @return true if the Entity is stackable
     */
    public boolean isStackable(@NotNull LivingEntity entity) {
        // HumanEntities are not allowed to stack, period.
        return !(entity instanceof HumanEntity)
                && entity.getPersistentDataContainer().getOrDefault(canStack, PersistentDataType.BYTE, (byte) 0) == 0
                && plugin.getConfig().getBoolean("stack-mob-type." + entity.getType().name(), false)
                && !plugin.getConfig().getStringList("blacklist-world").contains(entity.getWorld().getName().toLowerCase())
                && (plugin.getConfig().getBoolean("stack-custom-named-mobs") || matchesStackName(entity.getCustomName()));
    }

    /**
     * Alters whether an entity is stackable. Note that this will not allow for stacks exceeding the
     * configured limit.
     *
     * @param entity the entity
     * @param stackable if the Entity is stackable
     */
    public void setStackable(@NotNull LivingEntity entity, boolean stackable) {
        entity.getPersistentDataContainer().set(canStack, PersistentDataType.BYTE, (byte) (stackable ? 0 : 1));
    }

    /**
     * Sets an stack recently bred.
     *
     * @param entity the entity stack
     */
    public void setBred(@NotNull LivingEntity entity) {
        entity.getPersistentDataContainer().set(stackBreedTime, PersistentDataType.LONG, System.currentTimeMillis());
    }

    /**
     * Gets a stack's breedability.
     *
     * @param entity the entity stack
     */
    public boolean canBreed(@NotNull LivingEntity entity) {
        return entity.getPersistentDataContainer().getOrDefault(stackBreedTime, PersistentDataType.LONG, 0L)
                < System.currentTimeMillis() - 300000L;
    }

    /**
     * Gets the average health of a stacked entity.
     *
     * @param entity the entity stack
     * @param recalculate true if the value is to be recalculated including the base entity's health
     * @return the average health
     */
    public double getAverageHealth(@NotNull LivingEntity entity, boolean recalculate) {

        AttributeInstance attribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) {
            return entity.getPersistentDataContainer().getOrDefault(stackAverageHealth,
                    PersistentDataType.DOUBLE, 0D);
        }

        // Are we supposed to include current health in calculation?
        if (!recalculate) {
            return entity.getPersistentDataContainer().getOrDefault(stackAverageHealth,
                    PersistentDataType.DOUBLE, attribute.getValue());
        }

        int stackSize = getStackSize(entity);
        double currentHealth = entity.getHealth();

        // If the stack is larger than 1, average total maximum health in.
        if (stackSize > 1) {
            currentHealth += (stackSize - 1) * attribute.getValue();
            currentHealth /= stackSize;
        }

        // Return the current health.
        return currentHealth;
    }

    /**
     * Reduces the average health of a stack by the specified amount.
     *
     * @param entity the entity stack
     * @param amount the amount to reduce health by
     */
    public void damageAverageHealth(@NotNull LivingEntity entity, double amount) {
        entity.getPersistentDataContainer().set(stackAverageHealth, PersistentDataType.DOUBLE,
                getAverageHealth(entity, false) - amount);
    }

    /**
     * Increases the average health of a stack by adding an entity's stack data.
     * </p>
     * This method assumes that the stack size has already been increased by the relevant amount.
     *
     * @param entity the entity stack
     * @param addedSize the amount of entities added
     * @param addedHealth the average health of the added entities
     */
    private void increaseAverageHealth(@NotNull LivingEntity entity, int addedSize, double addedHealth) {
        double averageHealth = getAverageHealth(entity, false);

        // Entity is undamageable or dead.
        if (averageHealth <= 0) {
            return;
        }

        int originalStackSize = Math.min(1, getStackSize(entity) - addedSize);
        averageHealth *= originalStackSize;
        averageHealth += addedSize * addedHealth;
        averageHealth /= originalStackSize + addedHealth;

        AttributeInstance attribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        averageHealth = attribute != null ? Math.min(attribute.getValue(), averageHealth) : averageHealth;

        entity.getPersistentDataContainer().set(stackAverageHealth, PersistentDataType.DOUBLE, averageHealth);
    }

    /**
     * Cleans up data for a stack.
     *
     * @param entity the entity stack
     */
    public void removeStackData(@NotNull LivingEntity entity) {
        for (NamespacedKey key : new NamespacedKey[] { stackSize, canStack, stackAverageHealth, stackBreedTime }) {
            entity.getPersistentDataContainer().remove(key);
        }
    }

}
