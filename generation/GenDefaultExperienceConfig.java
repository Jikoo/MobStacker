import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.kiwifisher.mobstacker.loot.api.IExperienceEntry;
import com.kiwifisher.mobstacker.loot.impl.ConditionPropertiesAdult;
import com.kiwifisher.mobstacker.loot.impl.ExperienceEntry;
import com.kiwifisher.mobstacker.loot.impl.ExperiencePool;
import com.kiwifisher.mobstacker.loot.impl.SlimeExperienceEntry;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Generates the default experience.yml file.
 * 
 * @author Jikoo
 */
public class GenDefaultExperienceConfig {

    public static void main(String[] args) {
        File file = new File("experience.yml");
        YamlConfiguration config = new YamlConfiguration();

        ExperiencePool pool;
        List<IExperienceEntry> entries;
        ExperienceEntry entry;

        pool = new ExperiencePool();
        config.set("DEFAULT.BAT", pool);
        config.set("DEFAULT.IRON_GOLEM", pool);
        config.set("DEFAULT.SNOW_GOLEM", pool);
        config.set("DEFAULT.VILLAGER", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(10);
        pool.setEntries(Arrays.asList(entry));
        config.set("DEFAULT.BLAZE", pool);
        config.set("DEFAULT.GUARDIAN", pool);
        config.set("DEFAULT.ELDER_GUARDIAN", pool);
        config.set("DEFAULT.EVOCATION_ILLAGER", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(1);
        entry.setMaximum(3);
        entry.setConditions(Arrays.asList(new ConditionPropertiesAdult()));
        pool.setEntries(Arrays.asList(entry));
        config.set("DEFAULT.CHICKEN", pool);
        config.set("DEFAULT.COW", pool);
        config.set("DEFAULT.DONKEY", pool);
        config.set("DEFAULT.HORSE", pool);
        config.set("DEFAULT.LLAMA", pool);
        config.set("DEFAULT.MULE", pool);
        config.set("DEFAULT.MUSHROOM_COW", pool);
        config.set("DEFAULT.OCELOT", pool);
        config.set("DEFAULT.PIG", pool);
        config.set("DEFAULT.POLAR_BEAR", pool);
        config.set("DEFAULT.SHEEP", pool);
        config.set("DEFAULT.SKELETON_HORSE", pool);
        config.set("DEFAULT.SQUID", pool);
        config.set("DEFAULT.RABBIT", pool);
        config.set("DEFAULT.WOLF", pool);
        config.set("DEFAULT.ZOMBIE_HORSE", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(5);
        pool.setEntries(Arrays.asList(entry));
        config.set("DEFAULT.CAVE_SPIDER", pool);
        config.set("DEFAULT.CREEPER", pool);
        config.set("DEFAULT.ENDERMAN", pool);
        config.set("DEFAULT.GHAST", pool);
        config.set("DEFAULT.SHULKER", pool);
        config.set("DEFAULT.SILVERFISH", pool);
        config.set("DEFAULT.SKELETON", pool);
        config.set("DEFAULT.SPIDER", pool);
        config.set("DEFAULT.STRAY", pool);
        config.set("DEFAULT.VINDICATION_ILLAGER", pool);
        config.set("DEFAULT.WITCH", pool);
        config.set("DEFAULT.WITHER_SKELETON", pool);

        pool = new ExperiencePool();
        entries = new ArrayList<>();
        entry = new ExperienceEntry();
        entry.setMinimum(5);
        entries.add(entry);
        entry = new ExperienceEntry();
        // Baby zombies drop 12 exp, base guarantees 5. 7 needs to be added if not adult.
        entry.setMinimum(7);
        ConditionPropertiesAdult conditionAdult = new ConditionPropertiesAdult();
        conditionAdult.setAdult(false);
        entry.setConditions(Arrays.asList(conditionAdult));
        entries.add(entry);
        pool.setEntries(entries);
        config.set("DEFAULT.HUSK", pool);
        config.set("DEFAULT.PIG_ZOMBIE", pool);
        config.set("DEFAULT.ZOMBIE", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(3);
        pool.setEntries(Arrays.asList(entry));
        config.set("DEFAULT.ENDERMITE", pool);
        config.set("DEFAULT.VEX", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(500);
        pool.setEntries(Arrays.asList(entry));
        config.set("DEFAULT.ENDER_DRAGON", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(50);
        pool.setEntries(Arrays.asList(entry));
        config.set("DEFAULT.WITHER", pool);

        pool = new ExperiencePool();
        pool.setEntries(Arrays.asList(new SlimeExperienceEntry()));
        config.set("DEFAULT.SLIME", pool);
        config.set("DEFAULT.MAGMA_CUBE", pool);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
