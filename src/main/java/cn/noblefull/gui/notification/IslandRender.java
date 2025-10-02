package cn.noblefull.gui.notification;

import cn.noblefull.Client;
import cn.noblefull.module.Mine;
import cn.noblefull.utils.Instance;
import cn.noblefull.utils.animations.AnimationUtils;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.ContinualAnimation;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.fontrender.FontManager;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import cn.noblefull.module.impl.visuals.InterFace;
import java.awt.*;
import java.util.Deque;
import java.util.List;

/**
 * @Author: Guyuemang
 * 2025/5/23
 */
public class IslandRender implements Instance {
    public static IslandRender INSTANCE = new IslandRender();
    public ContinualAnimation animatedX = new ContinualAnimation();
    public ContinualAnimation animatedY = new ContinualAnimation();
    public float x, y, width, height;
    private ScaledResolution sr;

    public String title, description;

    public IslandRender() {
        this.sr = new ScaledResolution(mc);
        if (mc.theWorld == null) {
            resetDisplay();
        }
    }

    public void rendershader(ScaledResolution sr){
        if (sr == null || Client.Instance == null || Client.Instance.getNotification() == null) {
            return;
        }
        
        this.sr = sr;
        List<Notification> notifications = Client.Instance.getNotification().getNotifications();
        if (notifications != null && !notifications.isEmpty()) {
            Notification notification = notifications.get(notifications.size() - 1);
            if (notification != null && !notification.getAnimation().finished(Direction.FORWARDS)) {
                renderNotification(notification);
                return;
            }
        }
        renderPersistentInfo();
    }
    public void render(ScaledResolution sr){
        if (sr == null || Client.Instance == null || Client.Instance.getNotification() == null) {
            return;
        }
        
        this.sr = sr;
        List<Notification> notifications = Client.Instance.getNotification().getNotifications();
        if (notifications != null && !notifications.isEmpty()) {
            Notification notification = notifications.get(notifications.size() - 1);
            if (notification != null && !notification.getAnimation().finished(Direction.FORWARDS)) {
                renderNotification(notification);
                return;
            }
        }
        renderPersistentInfo();
    }
    private void renderNotification(Notification notification) {
        if (notification == null || sr == null) {
            return;
        }
        
        title = notification.getMessage();
        description = notification.getTitle();
        width = FontManager.Bold.get(18).getStringWidth(title) + 35 + 20;
        height = 34;
        x = sr.getScaledWidth() / 2f;
        y = 30;

        runToXy(x, y);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        drawBackgroundAuto(1);
        notification.animations = AnimationUtils.animate(notification.animations,notification.getType() == notification.getType().SUCCESS? 10 : 9,0.9f);
        float progress = Math.min(notification.getTimer().getTime2() / notification.getTime(), 1);
        RoundedUtil.drawRound(animatedX.getOutput() + 6, animatedY.getOutput() + ((y - animatedY.getOutput()) * 2),
                width - 12, 5f, 2.5f,InterFace.mainColor.get().darker());
        RoundedUtil.drawRound(animatedX.getOutput() + 6, animatedY.getOutput() + ((y - animatedY.getOutput()) * 2),
                (width - 12) * progress, 5f, 2.5f, InterFace.mainColor.get());
        if (notification.getType() == notification.getType().SUCCESS){
            RoundedUtil.drawRound(x - width / 2 + 5,y - 9,35,18,8,InterFace.mainColor.get().darker());
            RenderUtil.drawCircleCGUI(x - width / 2 + 22 + notification.animations,y,14,InterFace.mainColor.get().getRGB());
        }else {
            RoundedUtil.drawRound(x - width / 2 + 5,y - 9,35,18,8,InterFace.mainColor.get().darker());
            RenderUtil.drawCircleCGUI(x - width / 2 + 22 - notification.animations,y,14,InterFace.mainColor.get().getRGB());
        }
        FontManager.Bold.get(18).drawString(title,x - width / 2 + 46,y + 2,Client.Instance.getModuleManager().getModule(InterFace.class).color());
        FontManager.Bold.get(18).drawString(description,x - width / 2 + 46,y - 8,Client.Instance.getModuleManager().getModule(InterFace.class).color());
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }
    public static String getCurrentConnectionInfo() {
        if (mc.isSingleplayer()) {
            return "SinglePlayer";
        } else if (mc.getCurrentServerData() != null) {
            return mc.getCurrentServerData().serverIP;
        } else {
            return "null";
        }
    }
    private void renderPersistentInfo() {
        if (sr == null || Client.Instance == null || Client.Instance.getModuleManager() == null) {
            return;
        }
        
        String sb = Client.name + " | ";
        String sbs = getCurrentConnectionInfo();
        float sb2 = FontManager.Bold.get(24).getStringWidth(sb) + 10 + FontManager.Bold.get(14).getStringWidth(sbs);
        float sb3 = FontManager.Bold.get(24).getStringWidth(sb) + 6;
        width = sb2;
        height = 23;
        x = sr.getScaledWidth() / 2f;
        y = 20;
        runToXy(x, y);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        drawBackgroundAuto(0);
        FontManager.Bold.get(24).drawString(Client.name + " | ",x - sb2 / 2 + 4,y - 6,Client.Instance.getModuleManager().getModule(InterFace.class).color());
        FontManager.Bold.get(14).drawString(getCurrentConnectionInfo(),x + sb3 - width / 2,y - 5,Client.Instance.getModuleManager().getModule(InterFace.class).color());
        FontManager.Bold.get(14).drawString("FPS"+ Mine.getDebugFPS(),x + sb3 - width / 2,y + 2,Client.Instance.getModuleManager().getModule(InterFace.class).color());

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }
    public float getRenderX(float x) {
        return x - width / 2;
    }

    public float getRenderY(float y) {
        return y - height / 2;
    }

    public void runToXy(float realX, float realY) {
        animatedX.animate(getRenderX(realX), 30);
        animatedY.animate(getRenderY(realY), 30);
    }
    public void drawBackgroundAuto(int identifier) {
        float renderHeight = ((y - animatedY.getOutput()) * 2) + (identifier == 1 ? 10 : 0);
        RenderUtil.scissor(animatedX.getOutput() - 1, animatedY.getOutput() - 1,
                ((x - animatedX.getOutput()) * 2) + 2, renderHeight + 2);
        RoundedUtil.drawRound(animatedX.getOutput(), animatedY.getOutput(),
                (x - animatedX.getOutput()) * 2, renderHeight, 8, new Color(1,1,1,100));
        RoundedUtil.drawRound(animatedX.getOutput(), animatedY.getOutput(),
                (x - animatedX.getOutput()) * 2, renderHeight, 8, ColorUtil.applyOpacity3(Client.Instance.getModuleManager().getModule(InterFace.class).color(),0.3f));
    }

    private void resetDisplay() {
        x = sr.getScaledWidth() / 2f;
        y = 20;
        width = 0;
        height = 0;
        title = "";
    }
}
