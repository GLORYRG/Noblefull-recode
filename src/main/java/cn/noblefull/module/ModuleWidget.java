package cn.noblefull.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import cn.noblefull.Client;
import cn.noblefull.event.impl.events.render.Shader2DEvent;
import cn.noblefull.module.impl.visuals.InterFace;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;

import java.awt.*;

/**
 * @Author：GLORY
 * @Date：2025/6/1 14:41
 */

@Getter
@Setter
public abstract class ModuleWidget extends Module {
    public static volatile Mine mc = Mine.getMinecraft();
    
    @Expose
    @SerializedName("x")
    public float x;
    
    @Expose
    @SerializedName("y")
    public float y;
    
    protected float renderX, renderY;
    public float width;
    public float height;
    public boolean dragging;
    private int dragX, dragY;
    protected ScaledResolution sr;
    public static InterFace INTERFACE = Client.Instance.getModuleManager().getModule(InterFace.class);
    public static InterFace setting = Client.Instance.getModuleManager().getModule(InterFace.class);

    public ModuleWidget(String name, Category category) {
        super(name, category);
        this.x = 0f;
        this.y = 0f;
        this.width = 0f;
        this.height = 0f;
    }

    public abstract void onShader(Shader2DEvent event);

    public abstract void render();

    public abstract boolean shouldRender();

    public void updatePos() {
        if (mc == null) {
            synchronized (ModuleWidget.class) {
                if (mc == null) {
                    mc = Mine.getMinecraft();
                    if (mc == null) {
                        return;
                    }
                }
            }
        }
        
        sr = new ScaledResolution(mc);

        renderX = x * sr.getScaledWidth();
        renderY = y * sr.getScaledHeight();
        if (renderX < 0f) x = 0f;
        if (renderX > sr.getScaledWidth() - width) x = (sr.getScaledWidth() - width) / sr.getScaledWidth();
        if (renderY < 0f) y = 0f;
        if (renderY > sr.getScaledHeight() - height) y = (sr.getScaledHeight() - height) / sr.getScaledHeight();
    }
    
    public final void onChatGUI(int mouseX, int mouseY, boolean drag) {
        if (mc == null) {
            synchronized (ModuleWidget.class) {
                if (mc == null) {
                    mc = Mine.getMinecraft();
                    if (mc == null) {
                        return;
                    }
                }
            }
        }
        
        boolean hovering = RenderUtil.isHovering(renderX, renderY, width, height, mouseX, mouseY);

        Bold.get(16).drawString(name, renderX, renderY - 10, Color.WHITE.getRGB());
        if (dragging) {
            RoundedUtil.drawRoundOutline(renderX, renderY, width, height, 5f, 0.5f, new Color(0, 0, 0, 0), Color.WHITE);
        }

        if (hovering && Mouse.isButtonDown(0) && !dragging && drag) {
            dragging = true;
            dragX = mouseX;
            dragY = mouseY;
        }

        if (!Mouse.isButtonDown(0)) dragging = false;

        if (dragging) {
            float deltaX = (float) (mouseX - dragX) / sr.getScaledWidth();
            float deltaY = (float) (mouseY - dragY) / sr.getScaledHeight();

            x += deltaX;
            y += deltaY;

            dragX = mouseX;
            dragY = mouseY;
        }
    }
}