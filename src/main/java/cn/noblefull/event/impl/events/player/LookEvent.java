package cn.noblefull.event.impl.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.noblefull.event.impl.Event;
import cn.noblefull.utils.math.Vector2f;

/**
 * @Author：GLORY
 * @Date：2025/7/9 23:55
 */
@Getter
@Setter
@AllArgsConstructor
public class LookEvent implements Event {
    private Vector2f rotation;
}
