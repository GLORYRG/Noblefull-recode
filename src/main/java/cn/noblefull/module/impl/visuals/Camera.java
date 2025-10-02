package cn.noblefull.module.impl.visuals;

import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.value.impl.BoolValue;
import cn.noblefull.value.impl.NumberValue;

/**
 * @Author：GLORY
 * @Date：2025/7/3 12:31
 */
public class Camera extends Module {
    public final BoolValue noFovValue = new BoolValue("NoFov", false);
    public final NumberValue fovValue = new NumberValue("Fov", 1.0, 0.0, 4.0, 0.1);
    public final BoolValue motionCamera = new BoolValue("Motion Camera", true);
    public final NumberValue interpolation = new NumberValue("Interpolation", 0.01, 0.01, 0.4, 0.01);
    public Camera() {
        super("Camera", Category.Visuals);
    }
}
