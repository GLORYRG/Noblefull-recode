package cn.noblefull.utils.chats;

import cn.noblefull.module.Mine;
import net.minecraft.util.EnumChatFormatting;

/**
 * @Author: GLORY
 * 2025/5/25
 */
public class ChatUtils {
    private static final Mine mc = Mine.getMinecraft();

    public static void sendMessage(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(EnumChatFormatting.RED + "[Noblefull] " + EnumChatFormatting.RESET + message));
        }
    }
}
