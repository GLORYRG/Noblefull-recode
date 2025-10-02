package cn.noblefull.module.impl.movement;


import net.minecraft.client.settings.KeyBinding;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.player.MotionEvent;
import cn.noblefull.event.impl.events.player.UpdateEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.module.impl.world.Scaffold;
import cn.noblefull.utils.player.MovementUtil;
import cn.noblefull.value.impl.BoolValue;


public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Category.Movement);
    }
    private final BoolValue omni = new BoolValue("Omni", false);

    public static boolean keepSprinting = false;
    @EventTarget
    public void onSuffix(UpdateEvent event){
        setsuffix("Omni" + " " + omni.get().toString());
    }
    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        mc.thePlayer.omniSprint = false;
        keepSprinting = false;
        super.onDisable();
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (!keepSprinting) {
            if (!isEnabled(Scaffold.class))
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        } else {
            keepSprinting = false;
        }

        if (omni.get()) {
            mc.thePlayer.omniSprint = MovementUtil.isMoving();
        }
    }
}