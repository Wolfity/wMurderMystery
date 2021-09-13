package me.wolf.wmurdermystery.shop.versions;

import me.wolf.wmurdermystery.shop.IShopNPC;
import me.wolf.wmurdermystery.shop.npcs.v1_8_R3_Villager;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityTypes;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SuppressWarnings("ConstantConditions")
public class v1_8_R3 implements IShopNPC {

    public v1_8_R3() {
        registerEntity();
    }

    @Override
    public void spawnShopNPC(Location location) {
        v1_8_R3_Villager npc = new v1_8_R3_Villager(location.getWorld());
        npc.setPosition(location.getX(), location.getY(), location.getZ());
        (((CraftWorld) location.getWorld())).getHandle().addEntity(npc, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
    private void registerEntity() {
        try {
            List<Map<?, ?>> dataMaps = new ArrayList<>();
            for (Field f : EntityTypes.class.getDeclaredFields()) {
                if (f.getType().isAssignableFrom(Map.class)) {
                    f.setAccessible(true);
                    dataMaps.add((Map<?, ?>) f.get(null));
                }
            }
            ((Map<Class<? extends EntityInsentient>, String>) dataMaps.get(0)).put(v1_8_R3_Villager.class, "VillageVillager");
            ((Map<Class<? extends EntityInsentient>, Integer>) dataMaps.get(3)).put(v1_8_R3_Villager.class, 120);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
