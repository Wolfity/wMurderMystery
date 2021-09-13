package me.wolf.wmurdermystery.player;

import me.wolf.wmurdermystery.MurderMysteryPlugin;
import me.wolf.wmurdermystery.role.Role;
import me.wolf.wmurdermystery.shop.ShopEffect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class MMPlayer {

    private final MurderMysteryPlugin plugin;

    private final UUID uuid;
    private int kills;
    private boolean isSpectator;
    private Role role;
    private final List<ShopEffect> shopEffectList;
    private UUID killer;


    public MMPlayer(final UUID uuid, final MurderMysteryPlugin plugin) {
        this.plugin = plugin;
        this.role = Role.UNASSIGNED;
        this.uuid = uuid;
        this.kills = 0;
        this.isSpectator = false;
        this.shopEffectList = new ArrayList<>();


        final FileConfiguration effectCfg = plugin.getFileManager().getShopEffectsConfig().getConfig();
        for (final String effect : effectCfg.getConfigurationSection("shop-effects").getKeys(false)) {
             final int duration = effectCfg.getInt("shop-effects." + effect + ".duration");
             final int amplifier = effectCfg.getInt("shop-effects." + effect + ".amplifier") - 1;
             final boolean enabled = effectCfg.getBoolean("shop-effects." + effect + ".enabled");
             final String type = effectCfg.getString("shop-effects." + effect + ".type");

             shopEffectList.add(new ShopEffect(PotionEffectType.getByName(type), duration, amplifier, enabled));
        }
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(final boolean spectator) {
        isSpectator = spectator;
    }

    public void incrementKills() {
        this.kills++;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getKills() {
        return kills;
    }

    public List<ShopEffect> getShopEffectList() {
        return shopEffectList;
    }

    public void setKiller(final UUID killer) {
        this.killer = killer;
    }

    public UUID getKiller() {
        return killer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MMPlayer mmPlayer = (MMPlayer) o;
        return uuid.equals(mmPlayer.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
