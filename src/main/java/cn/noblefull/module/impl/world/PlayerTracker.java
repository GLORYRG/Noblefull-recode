package cn.noblefull.module.impl.world;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.misc.TickEvent;
import cn.noblefull.event.impl.events.misc.WorldLoadEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.module.impl.misc.Teams;
import cn.noblefull.utils.player.HYTUtils;

import java.util.ArrayList;
import java.util.List;


public class PlayerTracker
        extends Module {
    public static List<Entity> flaggedEntity = new ArrayList<Entity>();

    public PlayerTracker() {
        super("PlayerTracker", Category.World);
    }

    @EventTarget
    public void onWorld(WorldLoadEvent e) {
        flaggedEntity.clear();
    }

    @EventTarget
    public void onTick(TickEvent e) {
        if (PlayerTracker.mc.theWorld == null || PlayerTracker.mc.theWorld.loadedEntityList.isEmpty()) {
            return;
        }
        if (PlayerTracker.mc.thePlayer.ticksExisted % 6 == 0) {
            for (Entity ent : PlayerTracker.mc.theWorld.loadedEntityList) {
                if (!(ent instanceof EntityPlayer) || ent == PlayerTracker.mc.thePlayer) continue;
                EntityPlayer player = (EntityPlayer)ent;
                if (HYTUtils.isStrength(player) > 0 && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                }
                if (HYTUtils.isRegen(player) > 0 && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                }
                if (HYTUtils.isHoldingGodAxe(player) && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                }
                if (HYTUtils.isKBBall(player.getHeldItem()) && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                }
                if (HYTUtils.hasEatenGoldenApple(player) <= 0 || flaggedEntity.contains(player) || Teams.isSameTeam(player)) continue;
                flaggedEntity.add(player);
            }
        }
    }
}

