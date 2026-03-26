package org.Douwe.limited_life_v2;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;

public class Config {
    public static Config Load(File configFile) {
        var loaderoptions = new LoaderOptions();
        TagInspector taginspector =
                tag -> tag.getClassName().equals(Config.class.getName());
        loaderoptions.setTagInspector(taginspector);
        DumperOptions options = new DumperOptions();
        Representer representer = new Representer(options);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(new Constructor(Config.class, loaderoptions), representer, options);

        try {
            return yaml.load(new FileInputStream(configFile));
        } catch(Exception exc) {
            Limited_life_v2.LOGGER.warn("Failed to parse the config file");
            Limited_life_v2.LOGGER.warn(exc.getMessage());
            return new Config();
        }
    }

    public static class Numbers {
        public float startTime = 43200;   // 12 hours
        public float turnYellow = 28800;  // 8 hours
        public float turnRed = 14400;     // 4 hours
        public float deathPenalty = 3600; // 1 hour
        public float killReward = 1800;   // half hour
    }
    public static class Enable {
        public boolean showTimeToPlayer = false;
        public boolean redBoogeyman = true;
    }
    public Numbers numbers = new Numbers();
    public Enable enable = new Enable();
}
