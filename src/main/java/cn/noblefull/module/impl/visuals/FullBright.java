package cn.noblefull.module.impl.visuals;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.player.UpdateEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;

/**
 * @Author：GLORY
 * @Date：2025/7/3 12:31
 */
public class FullBright extends Module {
    public FullBright() {
        super("FullBright", Category.Visuals);
    }

    @EventTarget
    public void onUpdate(UpdateEvent event){
        mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 5200, 1));
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer.isPotionActive(Potion.nightVision)) {
            mc.thePlayer.removePotionEffect(Potion.nightVision.id);
        }
    }
}
