package cn.noblefull.event.impl.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.noblefull.event.impl.CancellableEvent;

/**
 * @Author：GLORY
 * @Date：7/7/2025 12:37 AM
 */
@Setter
@Getter
@AllArgsConstructor
public class SlowDownEvent extends CancellableEvent {
    private float strafe;
    private float forward;
    private boolean sprinting;
}
