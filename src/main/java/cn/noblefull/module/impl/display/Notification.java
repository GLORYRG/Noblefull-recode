package cn.noblefull.module.impl.display;


import cn.noblefull.Client;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.render.Render2DEvent;
import cn.noblefull.event.impl.events.render.Shader2DEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.ModuleWidget;
import cn.noblefull.value.impl.ModeValue;
import net.minecraft.client.gui.ScaledResolution;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 13:38
 */
public class Notification extends ModuleWidget {
    public ModeValue modeValue = new ModeValue("Mode", "Normal",new String[]{"Normal","Custom","Noblefull"});

    public Notification() {
        super("Notification",Category.Display);
    }

    @Override
    public void onShader(Shader2DEvent event) {
        if (mc == null) return;

        ScaledResolution scaledResolution = sr != null ? sr : new ScaledResolution(mc);
        
        switch (modeValue.getValue()) {
            case "Custom":
                Client.Instance.getNotification().customshader(scaledResolution.getScaledHeight() - 6);
                break;
            case "Normal":
                Client.Instance.getNotification().shader(scaledResolution.getScaledHeight() - 6);
                break;
        }
    }

    @EventTarget
    public void onRender(Render2DEvent event) {
        if (mc == null) return;
        
        // 确保sr不为null
        ScaledResolution scaledResolution = sr != null ? sr : new ScaledResolution(mc);
        
        switch (modeValue.getValue()) {
            case "Custom":
                Client.Instance.getNotification().custom(scaledResolution.getScaledHeight() - 6);
                break;
            case "Normal":
                Client.Instance.getNotification().render(scaledResolution.getScaledHeight() - 6);
                break;

        }
    }

    @Override
    public void render() {
    }

    @Override
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}