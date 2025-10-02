package cn.noblefull.module.impl.player;

import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.misc.WorldLoadEvent;
import cn.noblefull.event.impl.events.packet.PacketReceiveEvent;
import cn.noblefull.event.impl.events.packet.PacketSendEvent;
import cn.noblefull.event.impl.events.player.MotionEvent;
import cn.noblefull.utils.pack.PacketUtil;
import cn.noblefull.value.impl.BoolValue;
import cn.noblefull.value.impl.NumberValue;
import com.sun.jdi.BooleanValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

import cn.noblefull.module.Module;
import cn.noblefull.module.Category;
import cn.noblefull.event.EventManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Stealer extends Module {
    public Stealer() {
        super("ChestStealer", Category.World);
    }

    private final NumberValue delayValue = new NumberValue("Delay", 0.0, 0.0, 1000.0,0.1);
    private final BoolValue onlyItemsValue = new BoolValue("OnlyItems", false);
    private final BoolValue noCompassValue = new BoolValue("NoCompass", true);
    private final BoolValue autoCloseValue = new BoolValue("AutoClose", true);
    private final BoolValue chestTitleValue = new BoolValue("ChestTitle", true);
    public static final BoolValue silent = new BoolValue("SilentSteal", true);
    private final BoolValue saveC0F = new BoolValue("SaveC0F", false);

    private short transactionID = 0;
    private int lastWindowId = -1;
    private long lastActionTime = 0;
    private final Deque<C0FPacketConfirmTransaction> storedC0FPackets = new ArrayDeque<>();
    private boolean containerOpen = false;
    private final Deque<Slot> slotQueue = new ArrayDeque<>();

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.state == event.state.PRE) {
            GuiScreen screen = mc.currentScreen;
            if (screen == null) {
                resetContainerState();
                return;
            }
            handleNonSilentStealing(screen);
            performInstantSteal(screen);
        }
    }

    private void handleNonSilentStealing(GuiScreen screen) {
        if (delayValue.get() == 0 || !(screen instanceof GuiChest) || slotQueue.isEmpty()) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < delayValue.get()) {
            return;
        }

        GuiChest chest = (GuiChest) screen;
        Slot slot = slotQueue.poll();
        chest.handleMouseClick(slot, slot.slotNumber, 0, 1);
        lastActionTime = currentTime;

        if (slotQueue.isEmpty() && autoCloseValue.get()) {
            mc.thePlayer.closeScreen();
        }
    }

    @EventTarget
    public void onWorld(WorldLoadEvent event) {
        resetAllState();
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        if (!saveC0F.get()) return;
        if (event.getPacket() instanceof C0DPacketCloseWindow) {
            containerOpen = false;
            sendStoredC0FPackets();
            return;
        }
        if (containerOpen && event.getPacket() instanceof C0FPacketConfirmTransaction) {
            C0FPacketConfirmTransaction packet = (C0FPacketConfirmTransaction) event.getPacket();
            if (packet.getUid() >= 0) {
                storedC0FPackets.add(packet);
                event.setCancelled(true);
            }
        }
    }
    @EventTarget
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S2DPacketOpenWindow) {
            containerOpen = true;
            storedC0FPackets.clear();
        }
    }

    private void performInstantSteal(GuiScreen screen) {
        if (!(screen instanceof GuiChest)) {
            resetContainerState();
            return;
        }

        GuiChest chest = (GuiChest) screen;
        ContainerChest container = (ContainerChest) chest.inventorySlots;
        int currentWindowId = container.windowId;

        if (lastWindowId != currentWindowId) {
            lastWindowId = currentWindowId;
            containerOpen = true;
            slotQueue.clear();

            if (noCompassValue.get() &&
                    mc.thePlayer.inventory.getCurrentItem() != null &&
                    mc.thePlayer.inventory.getCurrentItem().getItem() == Item.getByNameOrId("compass")) {
                return;
            }
            if (chestTitleValue.get() &&
                    (chest.lowerChestInventory == null ||
                            !chest.lowerChestInventory.getDisplayName().getUnformattedText().contains("Chest"))) {
                return;
            }

            List<Slot> validSlots = getValidSlots(chest);

            if (validSlots.isEmpty()) {
                if (autoCloseValue.get()) {
                    mc.thePlayer.closeScreen();
                }
                return;
            }

            if (delayValue.get() == 0) {
                for (Slot slot : validSlots) {
                    moveItemInstantly(chest, slot);
                }
                if (autoCloseValue.get()) {
                    mc.thePlayer.closeScreen();
                }
            } else {
                slotQueue.addAll(validSlots);
                lastActionTime = 0;
            }
        }
    }

    private List<Slot> getValidSlots(GuiChest chest) {
        List<Slot> validSlots = new ArrayList<>();
        int rows = chest.inventoryRows;

        for (int slotIndex = 0; slotIndex < rows * 9; slotIndex++) {
            Slot slot = chest.inventorySlots.getSlot(slotIndex);
            if (shouldTake(slot.getStack())) {
                validSlots.add(slot);
            }
        }

        return validSlots;
    }

    private boolean shouldTake(ItemStack stack) {
        if (stack == null) return false;
        return !onlyItemsValue.get() || !(stack.getItem() instanceof ItemBlock);
    }

    private void sendStoredC0FPackets() {
        if (storedC0FPackets.isEmpty()) {
            return;
        }
        while (!storedC0FPackets.isEmpty()) {
            C0FPacketConfirmTransaction packet = storedC0FPackets.pollFirst();
            PacketUtil.sendPacketNoEvent(packet);
        }
    }

    private void moveItemInstantly(GuiChest chest, Slot slot) {
        if (slot.getStack() == null) return;
        sendClickPacket(chest, slot);
    }

    private void sendClickPacket(GuiChest chest, Slot slot) {
        transactionID = (short)((transactionID + 1) & 0x7FFF);

        C0EPacketClickWindow packet = new C0EPacketClickWindow(
                chest.inventorySlots.windowId,
                slot.slotNumber,
                0,
                1,
                slot.getStack(),
                transactionID
        );

        mc.getNetHandler().addToSendQueue(packet);
    }

    private void resetContainerState() {
        lastWindowId = -1;
        containerOpen = false;
        slotQueue.clear();
    }

    private void resetAllState() {
        resetContainerState();
        storedC0FPackets.clear();
        transactionID = 0;
        lastActionTime = 0;
    }

    @Override
    public void onDisable() {
        resetAllState();
    }
}