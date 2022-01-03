package me.wolf.wmurdermystery.shop.versions;

import me.wolf.wmurdermystery.shop.IShopNPC;
import me.wolf.wmurdermystery.shop.npcs.v1_16_R3_Villager;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;


@SuppressWarnings("ConstantConditions")
public class v1_16_R3 implements IShopNPC {


    @Override
    public void spawnShopNPC(Location location) {
        final v1_16_R3_Villager npc = new v1_16_R3_Villager(location);
        npc.setPosition(location.getX(), location.getY(), location.getZ());
        (((CraftWorld) location.getWorld())).getHandle().addEntity(npc, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

}
