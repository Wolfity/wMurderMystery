package me.wolf.wmurdermystery.files;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import me.wolf.wmurdermystery.utils.Utils;
import org.bukkit.Bukkit;

public class FileManager {


    private YamlConfig shopEffectsConfig;

    public FileManager(final MurderMysteryPlugin plugin) {
        try {
            shopEffectsConfig = new YamlConfig("shopeffects.yml", plugin);

        } catch (final Exception e) {
            Bukkit.getLogger().info(Utils.colorize("&4Something went wrong while loading the yml files"));
        }

    }

    public void reloadConfigs() {
        shopEffectsConfig.reloadConfig();

    }

    public YamlConfig getShopEffectsConfig() {
        return shopEffectsConfig;
    }
}
