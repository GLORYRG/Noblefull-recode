package cn.noblefull.module.impl.movement;

import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.player.UpdateEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;

/**
 * @Author：GLORY
 * @Date：7/7/2025 12:58 AM
 */
public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("NoJumpDelay",Category.Movement);
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        mc.thePlayer.jumpTicks = 0;
    }
}
