package cn.noblefull.utils.pack;

import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import cn.noblefull.event.annotations.EventPriority;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.misc.WorldLoadEvent;
import cn.noblefull.event.impl.events.packet.PacketSendEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

import static cn.noblefull.utils.Instance.mc;

public class BlinkComponent {
    public static final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    public static boolean blinking, dispatch;

    @EventTarget
    @EventPriority(-1)
    public void onPacketSend(PacketSendEvent event) {
        if (mc.thePlayer == null) {
            packets.clear();
            return;
        }

        if (mc.thePlayer.isDead || mc.isSingleplayer() || !mc.getNetHandler().doneLoadingTerrain) {
            packets.forEach(PacketUtil::sendPacketNoEvent);
            packets.clear();
            blinking = false;
            return;
        }

        final Packet<?> packet = event.getPacket();

        if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart ||
                packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing ||
                packet instanceof C01PacketEncryptionResponse) {
            return;
        }

        if (blinking) {

            if (!event.isCancelled()) {
                packets.add(packet);
                event.setCancelled(true);
            }
        }

    }

    public static void dispatch() {
        blinking = false;
        packets.forEach(PacketUtil::sendPacketNoEvent);
        packets.clear();

    }

    @EventTarget
    @EventPriority(-1)
    public void onWorld(WorldLoadEvent event) {
        packets.clear();
        BlinkComponent.blinking = false;
    }
}
