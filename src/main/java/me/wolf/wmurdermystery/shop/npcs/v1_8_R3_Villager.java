package me.wolf.wmurdermystery.shop.npcs;

import me.wolf.wmurdermystery.utils.Utils;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.List;


public class v1_8_R3_Villager extends EntityVillager {
    public v1_8_R3_Villager(World world) {
        super(((CraftWorld) world).getHandle());
        this.setCustomNameVisible(true);
        this.setCustomName(Utils.colorize("&eGold Shop &7(2 gold, 1 random effect!)"));

        final List<?> goalB = (List<?>) Utils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
        goalB.clear();

    }



    @Override
    public void move(double d0, double d1, double d2) {

    }

    @Override
    public void makeSound(String s, float f, float f1) {

    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean isInvulnerable(DamageSource damagesource) {
        return true;
    }

    @Override
    public void g(double d0, double d1, double d2) {

    }


}
