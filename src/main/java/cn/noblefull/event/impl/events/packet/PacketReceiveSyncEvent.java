package cn.noblefull.event.impl.events.packet;

import cn.noblefull.event.impl.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

/**
 * @Author: GLORY
 * 2025/5/11
 */

@Getter
@Setter
public class PacketReceiveSyncEvent extends CancellableEvent {
    private Packet<?> packet;
    private NetworkManager networkManager;

    public PacketReceiveSyncEvent(Packet<?> packet) {
        this.packet = packet;
    }
}
