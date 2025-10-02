package cn.noblefull.module.impl.display;

import cn.noblefull.event.impl.events.render.Shader2DEvent;
import cn.noblefull.gui.notification.IslandRender;
import cn.noblefull.module.Category;
import cn.noblefull.module.ModuleWidget;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 13:38
 */
public class IsLand extends ModuleWidget {
    public IsLand() {
        super("IsLand",Category.Display);
    }

    @Override
    public void onShader(Shader2DEvent event) {
        if (IslandRender.INSTANCE != null) {
            try {
                IslandRender.INSTANCE.rendershader(new ScaledResolution(mc));
            } catch (Exception e) {
                // 防止渲染过程中出现异常导致游戏崩溃
                System.err.println("[IsLand] Error during shader rendering: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void render() {
        IslandRender.INSTANCE.render(new ScaledResolution(mc));
    }

    @Override
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}
