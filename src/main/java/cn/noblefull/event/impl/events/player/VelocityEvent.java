package cn.noblefull.event.impl.events.player;

import lombok.Getter;
import lombok.Setter;
import cn.noblefull.event.impl.Event;

@Setter
@Getter
public class VelocityEvent implements Event {
    private double reduceAmount;

    public VelocityEvent(double reduceAmount) {
        this.reduceAmount = reduceAmount;
    }
}
