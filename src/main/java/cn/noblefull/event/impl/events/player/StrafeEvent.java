package cn.noblefull.event.impl.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import cn.noblefull.event.impl.CancellableEvent;
import cn.noblefull.utils.Instance;
import cn.noblefull.utils.player.MovementUtil;

/**
 * @Author：GLORY
 * @Date：2025/7/9 23:56
 */
@Getter
@Setter
@AllArgsConstructor
public class StrafeEvent extends CancellableEvent implements Instance {
    private float forward;
    private float strafe;
    private float friction;
    private float yaw;

    public void setSpeed(final double speed, final double motionMultiplier) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        mc.thePlayer.motionX *= motionMultiplier;
        mc.thePlayer.motionZ *= motionMultiplier;
    }

    public void setSpeed(final double speed) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        MovementUtil.stop();
    }
}
