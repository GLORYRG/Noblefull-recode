
package cn.noblefull.module.impl.visuals;

import org.lwjgl.input.Mouse;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.player.MotionEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.value.impl.ModeValue;

public class FreeLook extends Module {
    private boolean released;
    public static ModeValue modeValue = new ModeValue("Mode", "Middle", new String[]{"Middle", "Right"});

    public FreeLook() {
        super("FreeLook",Category.Visuals);
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.isPost()) {
            if (Mouse.isButtonDown(modeValue.is("Middle") ? 2 : 1)) {
                mc.gameSettings.thirdPersonView = 1;
                released = false;
            } else {
                if (!released) {
                    mc.gameSettings.thirdPersonView = 0;
                    released = true;
                }
            }
        }
    }
}
