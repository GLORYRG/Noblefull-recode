package cn.noblefull.event.impl.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.noblefull.event.impl.Event;

/**
 * @Author：GLORY
 * @Date：7/7/2025 1:00 AM
 */
@Getter
@Setter
@AllArgsConstructor
public class MoveInputEvent implements Event {
    private float forward;
    private float strafe;
    private boolean jumping;
    private boolean sneaking;
}
