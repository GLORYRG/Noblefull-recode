package cn.noblefull.event.impl.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.noblefull.event.impl.CancellableEvent;

/**
 * @Author：GLORY
 * @Date：7/7/2025 12:50 AM
 */
@Getter
@Setter
@AllArgsConstructor
public class JumpEvent extends CancellableEvent {
    private float yaw;
}
