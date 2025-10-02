package cn.noblefull.utils.player;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.misc.WorldLoadEvent;
import cn.noblefull.utils.Instance;

public class SlotSpoofComponent implements Instance {
    private static int spoofedSlot;

    @Getter
    private static boolean spoofing;

    public static void startSpoofing(int slot) {
        spoofing = true;
        spoofedSlot = slot;
    }

    public static void stopSpoofing() {
        spoofing = false;
    }

    public static int getSpoofedSlot() {
        return spoofing ? spoofedSlot : mc.thePlayer.inventory.currentItem;
    }

    public static ItemStack getSpoofedStack() {
        return spoofing ? mc.thePlayer.inventory.getStackInSlot(spoofedSlot) : mc.thePlayer.inventory.getCurrentItem();
    }

    @EventTarget
    public void onWorld(WorldLoadEvent event){
        stopSpoofing();
    }
}
