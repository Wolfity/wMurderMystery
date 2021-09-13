package me.wolf.wmurdermystery.shop.npcs;

import com.google.common.collect.Sets;
import me.wolf.wmurdermystery.utils.Utils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.lang.reflect.Field;
import java.util.Collections;

@SuppressWarnings("ConstantConditions")
public class v1_16_R3_Villager extends EntityVillager {

    public v1_16_R3_Villager(final Location location) {
        super(EntityTypes.VILLAGER, ((CraftWorld) location.getWorld()).getHandle());
        this.setPosition(location.getX(), location.getY(), location.getZ());
        this.setBaby(false);
        this.setCustomNameVisible(true);
        this.setCustomName(new ChatComponentText(Utils.colorize("&eGold Shop &7(2 gold, 1 random effect!)")));
        this.setInvulnerable(true);
        this.collides = false;
        this.setSilent(true);
        this.setNoGravity(true);
        clearhPathfinders();
        this.goalSelector.a(0, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 0, 0));
        world.addEntity(this);
    }

    private void clearhPathfinders() {
        try {
            final Field availableGoalsField = PathfinderGoalSelector.class.getDeclaredField("d");
            final Field priorityBehaviorsField = BehaviorController.class.getDeclaredField("e");
            final Field coreActivitysField = BehaviorController.class.getDeclaredField("i");

            availableGoalsField.setAccessible(true);
            priorityBehaviorsField.setAccessible(true);
            coreActivitysField.setAccessible(true);

            availableGoalsField.set(this.goalSelector, Sets.newLinkedHashSet());
            availableGoalsField.set(this.targetSelector, Sets.newLinkedHashSet());
            priorityBehaviorsField.set(this.getBehaviorController(), Collections.emptyMap());
            coreActivitysField.set(this.getBehaviorController(), Sets.newHashSet());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
