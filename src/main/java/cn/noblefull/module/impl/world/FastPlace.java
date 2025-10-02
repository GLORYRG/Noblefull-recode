package cn.noblefull.module.impl.world;

import net.minecraft.item.ItemBlock;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.player.MotionEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.value.impl.NumberValue;

/**
 * @Author：GLORY
 * @Date：7/7/2025 12:07 AM
 */
public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace",Category.World);
    }

    public final NumberValue speed = new NumberValue("Speed", 1, 0, 4, 1);

    @EventTarget
    public void onMotion(MotionEvent event) {
        setsuffix(String.valueOf(speed.get()));
        if (mc.thePlayer == null && mc.theWorld == null)
            return;
        if (mc.thePlayer.getHeldItem() == null)
            return;
        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)
            mc.rightClickDelayTimer = speed.getValue().intValue();
    }
}
