package me.wolf.wmurdermystery.shop;

import org.bukkit.potion.PotionEffectType;

public class ShopEffect {

    private final PotionEffectType effect;
    private final int duration, amplifier;
    private final boolean isEnabled;

    public ShopEffect(final PotionEffectType effect, final int duration, final int amplifier, final boolean isEnabled) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.isEnabled = isEnabled;
    }

    public PotionEffectType getEffect() {
        return effect;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
