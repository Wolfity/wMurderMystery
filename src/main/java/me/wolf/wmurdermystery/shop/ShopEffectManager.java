package me.wolf.wmurdermystery.shop;



import me.wolf.wmurdermystery.MurderMysteryPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class ShopEffectManager {


    private final MurderMysteryPlugin plugin;

    public ShopEffectManager(final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
    }

    private final Set<ShopEffect> shopEffects = new HashSet<>();

    public void loadEffects() {
        final FileConfiguration effectCfg = plugin.getFileManager().getShopEffectsConfig().getConfig();
        for (final String effect : effectCfg.getConfigurationSection("shop-effects").getKeys(false)) {
            final int duration = effectCfg.getInt("shop-effects." + effect + ".duration");
            final int amplifier = effectCfg.getInt("shop-effects." + effect + ".amplifier") - 1;
            final boolean enabled = effectCfg.getBoolean("shop-effects." + effect + ".enabled");
            final String type = effectCfg.getString("shop-effects." + effect + ".type");

            shopEffects.add(new ShopEffect(PotionEffectType.getByName(type), duration, amplifier, enabled));
        }
    }

    public Set<ShopEffect> getShopEffects() {
        return shopEffects;
    }
}
