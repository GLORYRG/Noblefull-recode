package cn.noblefull.module.impl.misc;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.packet.PacketReceiveEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.value.impl.ModeValue;

/**
 * @Author：GLORY
 * @Date：7/7/2025 12:06 AM
 */

public class ClientSpoofer extends Module {
    public ClientSpoofer() {
        super("ClientSpoofer", Category.Misc);
    }
    public final ModeValue mode = new ModeValue("Mode", "Lunar", new String[]{"Lunar", "Feather"});

    @EventTarget
    public void onPacket(PacketReceiveEvent packetEvent) {
        setsuffix(mode.getValue());
        if (packetEvent.getPacket() instanceof C17PacketCustomPayload packet) {

            String data = switch (mode.getValue()) {
                case "Lunar" -> "lunarclient:v2.14.5-2411";
                case "Feather" -> "Feather Forge";
                default -> "";
            };

            ByteBuf byteBuf = Unpooled.wrappedBuffer(data.getBytes());
            PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(byteBuf));

            packet.setData(buffer);
        }
    }
}
