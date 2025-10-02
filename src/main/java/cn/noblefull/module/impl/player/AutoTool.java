package cn.noblefull.module.impl.player;

import net.minecraft.util.MovingObjectPosition;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.misc.TickEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.utils.player.PlayerUtil;
import cn.noblefull.utils.player.SlotSpoofComponent;
import cn.noblefull.value.impl.BoolValue;

/**
 * @Author：GLORY
 * @Date：7/7/2025 12:01 AM
 */
public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool",Category.Player);
    }
    public final BoolValue ignoreUsingItem = new BoolValue("Ignore Using Item",false);
    public final BoolValue spoof = new BoolValue("Spoof",false);
    public final BoolValue switchBack = new BoolValue("Switch Back",() -> !spoof.get(),true);
    private int oldSlot;
    public boolean wasDigging;
    @Override
    public void onDisable() {
        if (this.wasDigging) {
            mc.thePlayer.inventory.currentItem = this.oldSlot;
            this.wasDigging = false;
        }
        SlotSpoofComponent.stopSpoofing();
    }

    @EventTarget
    public void onTick(TickEvent event) {
        setsuffix("Spoof");
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.gameSettings.keyBindAttack.isKeyDown() && (ignoreUsingItem.get() && !mc.thePlayer.isUsingItem() || !ignoreUsingItem.get()) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && PlayerUtil.findTool(mc.objectMouseOver.getBlockPos()) != -1) {
            if (!this.wasDigging) {
                this.oldSlot = mc.thePlayer.inventory.currentItem;
                if (this.spoof.get()) {
                    SlotSpoofComponent.startSpoofing(this.oldSlot);
                }
            }
            mc.thePlayer.inventory.currentItem = PlayerUtil.findTool(mc.objectMouseOver.getBlockPos());
            this.wasDigging = true;
        } else if (this.wasDigging && (switchBack.get() || spoof.get())) {
            mc.thePlayer.inventory.currentItem = this.oldSlot;
            SlotSpoofComponent.stopSpoofing();
            this.wasDigging = false;
        } else {
            this.oldSlot = mc.thePlayer.inventory.currentItem;
        }
    }
}
