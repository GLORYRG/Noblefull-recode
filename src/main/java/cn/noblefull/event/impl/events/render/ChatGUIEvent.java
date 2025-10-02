package cn.noblefull.event.impl.events.render;

import lombok.Getter;
import cn.noblefull.event.impl.CancellableEvent;

/**
 * @Author: GLORY
 * 2025/4/22
 */
@Getter
public class ChatGUIEvent extends CancellableEvent {
    private final int mouseX,mouseY;

    public ChatGUIEvent(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
