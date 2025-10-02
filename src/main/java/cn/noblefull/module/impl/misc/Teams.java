package cn.noblefull.module.impl.misc;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import cn.noblefull.Client;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.player.UpdateEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.utils.player.PlayerUtil;
import cn.noblefull.value.impl.BoolValue;

import java.util.Objects;

public class Teams extends Module {
    private static final BoolValue armorValue = new BoolValue("ArmorColor", true);
    private static final BoolValue colorValue = new BoolValue("Color", true);
    private static final BoolValue scoreboardValue = new BoolValue("ScoreboardTeam", true);


    public Teams() {
        super("Teams", Category.Misc);
    }
    @EventTarget
    public void onSuffix(UpdateEvent event){
        setsuffix(armorValue.getName().toString());
    }
    public static boolean isSameTeam(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            if (Objects.requireNonNull(Client.Instance.getModuleManager().getModule(Teams.class)).getState()) {
                return (armorValue.getValue() && PlayerUtil.armorTeam(entityPlayer)) ||
                        (colorValue.getValue() && PlayerUtil.colorTeam(entityPlayer)) ||
                        (scoreboardValue.getValue() && PlayerUtil.scoreTeam(entityPlayer));
            }
            return false;
        }
        return false;
    }


}
