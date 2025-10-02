package cn.noblefull.event.impl.events.packet;

import cn.noblefull.event.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@AllArgsConstructor
public class HigherPacketEvent extends CancellableEvent {
    @Setter
    private Packet<?> packet;
}
