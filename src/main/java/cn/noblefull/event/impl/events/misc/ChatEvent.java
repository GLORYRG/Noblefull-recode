package cn.noblefull.event.impl.events.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import cn.noblefull.event.impl.CancellableEvent;

/**
 * @Author: GLORY
 * 2025/4/21
 */
@Getter
@AllArgsConstructor
public class ChatEvent extends CancellableEvent {
    private final String message;
}
