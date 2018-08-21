package com.kiwifisher.mobstacker;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Test to ensure YAML configuration contains all modern options.
 *
 * @author Jikoo
 */
public class YamlConfigTest {

	@Test
	public void testMobTypes() {
		YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));
		List<String> missing = new ArrayList<>();
		for (EntityType type : EntityType.values()) {
			if (type.getEntityClass() == null || !Mob.class.isAssignableFrom(type.getEntityClass())) {
				continue;
			}
			if (!defaultConfig.contains("stack-mob-type." + type.name())) {
				missing.add(type.name());
			}
		}

		if (missing.size() > 0) {
			fail("Missing mob types: " + missing);
		}
	}

	@Test
	public void testDamageCauses() {
		YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));
		List<String> missing = new ArrayList<>();
		List<String> ignored = Arrays.asList("ENTITY_ATTACK", "PROJECTILE", "ENTITY_EXPLOSION", "FIRE_TICK",
				"LIGHTNING", "POISON", "MAGIC", "WITHER", "FALLING_BLOCK", "THORNS", "CUSTOM", "FLY_INTO_WALL");
		List<String> causes = defaultConfig.getStringList("persistent-damage.reasons");
		for (EntityDamageEvent.DamageCause cause : EntityDamageEvent.DamageCause.values()) {
			if (!ignored.contains(cause.name()) && !causes.contains(cause.name())) {
				missing.add(cause.name());
			}
		}

		if (missing.size() > 0) {
			fail("Missing damage causes: " + missing);
		}
	}

	@Test
	public void testAnimalTypes() {
		YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));
		List<String> missing = new ArrayList<>();
		List<String> types = defaultConfig.getStringList("load-existing-stacks.mob-types");
		for (EntityType type : EntityType.values()) {
			if (type.getEntityClass() != null && Animals.class.isAssignableFrom(type.getEntityClass())
					&& !types.contains(type.name())) {
				missing.add(type.name());
			}
		}

		if (missing.size() > 0) {
			fail("Missing animal types: " + missing);
		}
	}

	@Test
	public void testSpawnReason() {
		YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new File("src/main/resources/config.yml"));
		List<String> missing = new ArrayList<>();
		for (CreatureSpawnEvent.SpawnReason reason : CreatureSpawnEvent.SpawnReason.values()) {
			if (!defaultConfig.contains("stack-spawn-reason." + reason.name())) {
				missing.add(reason.name());
			}
		}

		if (missing.size() > 0) {
			fail("Missing spawn reasons: " + missing);
		}
	}

}
