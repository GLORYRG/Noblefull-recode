/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package cn.noblefull.utils.player;

import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.misc.WorldLoadEvent;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import cn.noblefull.utils.Instance;
import cn.noblefull.utils.time.TimerUtil;
import net.minecraft.network.Packet;

import java.util.*;

public class InventoryUtils implements Instance {

    // What slot is selected on server-side?
    // TODO: Is this equal to mc.playerController.currentPlayerItem?
    private static int _serverSlot = 0;
    
    public static int getServerSlot() {
        return _serverSlot;
    }
    
    public static void setServerSlot(int value) {
        if (value != _serverSlot) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(value));
            _serverSlot = value;
        }
    }

    // Is inventory open on server-side?
    private static boolean _serverOpenInventory = false;
    
    public static boolean isServerOpenInventory() {
        return _serverOpenInventory;
    }
    
    public static void setServerOpenInventory(boolean value) {
        if (value != _serverOpenInventory) {
            if (value) {
                mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            } else {
                mc.getNetHandler().addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer != null ? mc.thePlayer.openContainer.windowId : 0));
            }
            _serverOpenInventory = value;
        }
    }
    private static boolean serverOpenContainer = false;
    
    public static boolean isServerOpenContainer() {
        return serverOpenContainer;
    }
    
    private static void setServerOpenContainer(boolean value) {
        serverOpenContainer = value;
    }

    private static boolean isFirstInventoryClick = true;
    
    public static boolean isFirstInventoryClick() {
        return isFirstInventoryClick;
    }
    
    public static void setFirstInventoryClick(boolean value) {
        isFirstInventoryClick = value;
    }

    public static final TimerUtil CLICK_TIMER = new TimerUtil();

    public static final List<Block> BLOCK_BLACKLIST = Arrays.asList(
        Blocks.chest,
        Blocks.ender_chest,
        Blocks.trapped_chest,
        Blocks.anvil,
        Blocks.sand,
        Blocks.web,
        Blocks.torch,
        Blocks.crafting_table,
        Blocks.furnace,
        Blocks.waterlily,
        Blocks.dispenser,
        Blocks.stone_pressure_plate,
        Blocks.wooden_pressure_plate,
        Blocks.noteblock,
        Blocks.dropper,
        Blocks.tnt,
        Blocks.standing_banner,
        Blocks.wall_banner,
        Blocks.redstone_torch
    );

    public static Integer findItem(int startInclusive, int endInclusive, Item item) {
        for (int i = startInclusive; i <= endInclusive; i++) {
            if (mc.thePlayer.openContainer.getSlot(i).getStack() != null && 
                mc.thePlayer.openContainer.getSlot(i).getStack().getItem() == item) {
                return i;
            }
        }

        return null;
    }

    public static boolean hasSpaceInHotbar() {
        for (int i = 36; i <= 44; i++) {
            if (mc.thePlayer.openContainer.getSlot(i).getStack() == null) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasSpaceInInventory() {
        return mc.thePlayer != null && mc.thePlayer.inventory != null && mc.thePlayer.inventory.getFirstEmptyStack() != -1;
    }

    public static int countSpaceInInventory() {
        int count = 0;
        for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
            if (mc.thePlayer.inventory.mainInventory[i] == null) {
                count++;
            }
        }
        return count;
    }

    public static Integer findBlockInHotbar() {
        if (mc.thePlayer == null) return null;
        final int inventorySize = mc.thePlayer.openContainer.inventorySlots.size();

        int result = -1;
        float blockHardness = -1f;
        
        for (int i = 36; i <= 44; i++) {
            final net.minecraft.item.ItemStack stack = mc.thePlayer.openContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                final ItemBlock itemBlock = (ItemBlock) stack.getItem();
                final net.minecraft.block.Block block = itemBlock.getBlock();
                
                if (stack.stackSize > 0 && 
                    !BLOCK_BLACKLIST.contains(block) && 
                    !(block instanceof BlockBush) && 
                    block.isFullCube()) {
                    
                    final float hardness = block.getBlockHardness(mc.theWorld, mc.thePlayer.getPosition());
                    if (hardness > blockHardness) {
                        blockHardness = hardness;
                        result = i;
                    }
                }
            }
        }
        
        return result == -1 ? null : result;
    }

    public static Integer findLargestBlockStackInHotbar() {
        if (mc.thePlayer == null) return null;
        int result = -1;
        int stackSize = -1;
        
        for (int i = 36; i <= 44; i++) {
            final net.minecraft.item.ItemStack stack = mc.thePlayer.openContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBlock) {
                final ItemBlock itemBlock = (ItemBlock) stack.getItem();
                final net.minecraft.block.Block block = itemBlock.getBlock();
                
                if (stack.stackSize > 0 && 
                    block.isFullCube() &&
                    !BLOCK_BLACKLIST.contains(block) && 
                    !(block instanceof BlockBush)) {
                    
                    if (stack.stackSize > stackSize) {
                        stackSize = stack.stackSize;
                        result = i;
                    }
                }
            }
        }
        
        return result == -1 ? null : result;
    }

    // Converts container slot to hotbar slot id, else returns null
    public static Integer toHotbarIndex(int slot, int stacksSize) {
        final int parsed = slot - stacksSize + 9;

        return (parsed >= 0 && parsed <= 8) ? parsed : null;
    }

    @EventTarget
    public static void onPacket(cn.noblefull.event.impl.events.packet.PacketReceiveEvent event) {
        if (event.isCancelled()) return;

        final Object packet = event.getPacket();

        if (packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C0EPacketClickWindow) {
            CLICK_TIMER.reset();

            if (packet instanceof C0EPacketClickWindow)
                isFirstInventoryClick = false;
        } else if (packet instanceof C16PacketClientStatus) {
            C16PacketClientStatus statusPacket = (C16PacketClientStatus) packet;
            if (statusPacket.getStatus() == EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                if (_serverOpenInventory) event.setCancelled(true);
                else {
                    isFirstInventoryClick = true;
                    _serverOpenInventory = true;
                }
            }
        } else if (packet instanceof C0DPacketCloseWindow || packet instanceof S2EPacketCloseWindow || packet instanceof S2DPacketOpenWindow) {
            isFirstInventoryClick = false;
            _serverOpenInventory = false;
            serverOpenContainer = false;

            if (packet instanceof S2DPacketOpenWindow) {
                S2DPacketOpenWindow openWindow = (S2DPacketOpenWindow) packet;
                if ("minecraft:chest".equals(openWindow.getGuiId()) || "minecraft:container".equals(openWindow.getGuiId()))
                    serverOpenContainer = true;
            }
        } else if (packet instanceof C09PacketHeldItemChange) {
            // Support for Singleplayer
            // (client packets get sent and received, duplicates would get cancelled, making slot changing impossible)
            // Assuming RECEIVE is a constant in the Event class
            // if (event.getEventType() == cn.noblefull.event.EventState.RECEIVE) return;

            C09PacketHeldItemChange heldItemChange = (C09PacketHeldItemChange) packet;
            if (heldItemChange.getSlotId() == _serverSlot) event.setCancelled(true);
            else _serverSlot = heldItemChange.getSlotId();
        } else if (packet instanceof S09PacketHeldItemChange) {
            S09PacketHeldItemChange heldItemChange = (S09PacketHeldItemChange) packet;
            if (_serverSlot == heldItemChange.getHeldItemHotbarIndex())
                return;

            _serverSlot = heldItemChange.getHeldItemHotbarIndex();
        }
    }

    @EventTarget
    public static void onWorld(WorldLoadEvent event) {
        // Prevents desync
        _serverOpenInventory = false;
        _serverSlot = 0;
        serverOpenContainer = false;
    }

    public static boolean handleEvents() {
        return true;
    }
}