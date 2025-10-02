package cn.noblefull.gui;


import cn.noblefull.event.impl.events.render.Shader2DEvent;
import cn.noblefull.utils.render.shader.ShaderUtils;
import cn.noblefull.utils.render.shader.impl.Blur;
import cn.noblefull.utils.render.shader.impl.Shadow;
import net.minecraft.client.gui.*;
import cn.noblefull.Client;
import cn.noblefull.gui.alt.GuiAccountManager;
import cn.noblefull.gui.mcgui.GuiMultiplayer;
import cn.noblefull.module.ClientApplication;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.DecelerateAnimation;
import cn.noblefull.utils.animations.impl.LayeringAnimation;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.fontrender.FontManager;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author：GLORY
 * @Date：2025/6/28 23:22
 */

public class MainMenu extends GuiScreen {
    List<Button> buttons = Arrays.asList(
            new Button("Single Player","B"),
            new Button("Multi Player","E"),
            new Button("Alt Manager","D"),
            new Button("Options","O"),
            new Button("Shut down","R")
    );
    List<Button2> buttons2 = Arrays.asList(
            new Button2("Discord","V"),
            new Button2("Kook","W"),
            new Button2("Bilibili","X"),
            new Button2("YouTube","Y"),
            new Button2("Shop","Z")
    );
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);
    private Animation fadeInAnimation = new DecelerateAnimation(3000, 1).setDirection(Direction.FORWARDS);
    private static Animation progress4Anim;
    int alpha = 0;
    @Override
    public void initGui() {
        progress4Anim = new DecelerateAnimation(5000, 1).setDirection(Direction.BACKWARDS);
        if (mc.gameSettings.guiScale != 2) {
            mc.gameSettings.guiScale = 2;
            mc.resize(mc.displayWidth - 1, mc.displayHeight);
            mc.resize(mc.displayWidth + 1, mc.displayHeight);
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        cn.noblefull.utils.render.shader.MainMenu.drawBackground(width, height, mouseX, mouseY);
        Blur.startBlur();
        RoundedUtil.drawRound(width / 2 - 90, height - 120, 180, 30, 11, new Color(0, 0, 0, 120));
        Blur.endBlur(10,2);
        RoundedUtil.drawRound(width / 2 - 90, height - 120, 180, 30, 11, new Color(0, 0, 0, 52));
        stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer, true);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(true);

        // 绘制要应用阴影的形状到帧缓冲区
        FontManager.Bold.get(80).drawCenteredString(Client.name, sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 110, new Color(127, 127, 213).getRGB());

        // 解绑帧缓冲区
        stencilFramebuffer.unbindFramebuffer();

        // 应用阴影效果
        mc.getFramebuffer().bindFramebuffer(true);
        Shadow.renderBloom(stencilFramebuffer.framebufferTexture, 9, 1);

        FontManager.Bold.get(80).drawCenteredString(Client.name, sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 - 110, new Color(127, 127, 213, 255).getRGB());
        FontManager.Bold.get(22).drawCenteredString("Version: " + Client.version, sr.getScaledWidth() / 2 + 65, sr.getScaledHeight() / 2 - 70, new Color(127, 127, 213, 255).getRGB());

        // 使用与clickgui相同的阴影绘制方式
        stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer, true);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(true);
        
        // 绘制要应用阴影的形状到帧缓冲区
        RoundedUtil.drawRound(sr.getScaledWidth() / 4f + 15f, sr.getScaledHeight() / 2 - 22, 450, 40, 10, new Color(255, 255, 255, 255));
        
        // 解绑帧缓冲区
        stencilFramebuffer.unbindFramebuffer();
        
        // 应用阴影效果
        mc.getFramebuffer().bindFramebuffer(true);
        Shadow.renderBloom(stencilFramebuffer.framebufferTexture, 20, 1);

        Blur.startBlur();
        RoundedUtil.drawRound(sr.getScaledWidth() / 4f + 15f , sr.getScaledHeight() / 2 - 22, 450, 40, 10, new Color(255, 255, 255, 255));
        Blur.endBlur(10,2);
        RoundedUtil.drawRound(sr.getScaledWidth() / 4f + 15f, sr.getScaledHeight() / 2 - 22, 450, 40, 10, new Color(255, 255, 255, 34));
        
        

        float count = 0;
        for (Button button : buttons) {
            button.x = sr.getScaledWidth() / 4 + 50  +count;
            button.y = sr.getScaledHeight() / 2 -18;
            button.width = 40;
            button.height = 34;
            // =============================================================
            button.clickAction = () -> {
                switch (button.name) {
                    case "Single Player": {
                        LayeringAnimation.play(new GuiSelectWorld(this));
                    }
                    break;
                    case "Multi Player": {

                        LayeringAnimation.play(new GuiMultiplayer());
                    }
                    break;
                    case "Alt Manager": {
                        LayeringAnimation.play(new GuiAccountManager(this));
                    }
                    break;
                    case "Options": {
                        LayeringAnimation.play(new GuiOptions(this, mc.gameSettings));
                    }
                    break;
                    case "Shut down": {
                        mc.shutdown();
                    }
                    break;
                }
            };
            count += 85;
            button.drawScreen(mouseX, mouseY);
        }

        // ==================== Button2 的代码保持原样，未做任何修改 ====================
        float count2 = 0;
        for (Button2 buttons2 : buttons2) {
            buttons2.x = width / 2 - 90 + count2;
            buttons2.y = height - 112;
            buttons2.width = FontManager.Icon.get(40).getStringWidth(buttons2.icon);
            buttons2.height = 15;
            buttons2.clickAction = () -> {
                switch (buttons2.name) {
                    case "Discord": {
                        URI uri = null;
                        try {
                            uri = new URI("https://discord.gg/mEDAu8fa");
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            Desktop.getDesktop().browse(uri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                    case "Kook": {
                        URI uri = null;
                        try {
                            uri = new URI("https://kook.vip/iPrnZC");
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            Desktop.getDesktop().browse(uri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                    case "Bilibili": {
                        URI uri = null;
                        try {
                            uri = new URI("https://space.bilibili.com/80873885?spm_id_from=333.1007.0.0");
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            Desktop.getDesktop().browse(uri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                    case "YouTube": {
                        URI uri = null;
                        try {
                            uri = new URI("https://www.youtube.com/@glory_-ms8so");
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            Desktop.getDesktop().browse(uri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                    case "Shop": {
                        URI uri = null;
                        try {
                            uri = new URI("https://munan.shop/");
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            Desktop.getDesktop().browse(uri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            };
            count2 += FontManager.Icon.get(40).getStringWidth(buttons2.icon) + 15;
            buttons2.drawScreen(mouseX, mouseY);
        }
        // ======================================================================

        float progress = fadeInAnimation.getOutput().floatValue();
        alpha = (int) (255 * (1 - progress)); // 从完全不透明到完全透明

        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, alpha).getRGB());
        if (fadeInAnimation.getOutput() <= 0.9) {
            FontManager.Bold.get(60).drawString("Noblefull " + Client.version, sr.getScaledWidth() / 2 - FontManager.Bold.get(60).getStringWidth("Noblefull " + Client.version) / 2, sr.getScaledHeight() / 2 - 20, ColorUtil.applyOpacity(-1,fadeInAnimation.getOutput().floatValue()));
            FontManager.Bold.get(30).drawString("Welcome!! " + ClientApplication.usernameField.getText(), sr.getScaledWidth() / 2 - FontManager.Bold.get(30).getStringWidth("Welcome!! " + ClientApplication.usernameField.getText()) / 2, sr.getScaledHeight() / 2 + 20, ColorUtil.applyOpacity(-1,fadeInAnimation.getOutput().floatValue()));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttons.forEach(button -> {button.mouseClicked(mouseX, mouseY, mouseButton);});
        buttons2.forEach(button -> {button.mouseClicked(mouseX, mouseY, mouseButton);});
    }

    class Button {
        String name;
        String icon;
        public float x, y, width, height;
        public Runnable clickAction;
        private Animation hoverAnimation = new DecelerateAnimation(1000, 1);;

        public Button(String name,String icon){
            this.name = name;
            this.icon = icon;
        }

        public void drawScreen(int mouseX, int mouseY) {
            boolean hovered = RenderUtil.isHovering(x, y, width, height, mouseX, mouseY);
            Color rectColor = new Color(35, 37, 43, 150);
            rectColor = ColorUtil.interpolateColorC(rectColor, ColorUtil.brighter(rectColor, 0.4f), this.hoverAnimation.getOutput().floatValue());
            hoverAnimation.setDirection(hovered ? Direction.BACKWARDS : Direction.FORWARDS);

            // 背景现在会使用新的、更大的 40x40 尺寸
            if (hovered){
                RoundedUtil.drawRound(x, y, width, height, 3, rectColor);
            }
            if (hovered) {
                FontManager.Semibold.get(20).drawCenteredString(name, x + width / 2, y - 15, rectColor.brighter().brighter().brighter().brighter().getRGB());
            }

            // ==================== FIX #2: 修正图标居中逻辑 ====================
            // 图标的居中计算现在也基于新的 40x40 尺寸，确保它在背景内居中
            float iconWidth = FontManager.Icon.get(80).getStringWidth(icon);
            float iconHeight = FontManager.Icon.get(80).getHeight();
            float iconX = x + (width - iconWidth) / 2;
            // 加上 +2 的偏移来修正字体基线渲染导致的视觉偏差
            float iconY = y + (height - iconHeight) / 2 + 2;
            FontManager.Icon.get(80).drawString(icon, iconX, iconY, new Color(-1).getRGB());
            // =============================================================
        }

        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = RenderUtil.isHovering(x, y, width, height, mouseX, mouseY);
            if (hovered) clickAction.run();
        }
    }

    class Button2 {
        String name;
        String icon;
        public float x, y, width, height;
        public Runnable clickAction;
        private Animation hoverAnimation = new DecelerateAnimation(1000, 1);;

        public Button2(String name,String icon){
            this.name = name;
            this.icon = icon;
        }

        public void drawScreen(int mouseX, int mouseY) {
            boolean hovered = RenderUtil.isHovering(x + 11 - FontManager.Icon.get(40).getStringWidth(icon) / 2 + width / 2, y - 1, width, height, mouseX, mouseY);
            Color rectColor = new Color(35, 37, 43, 150);
            rectColor = ColorUtil.interpolateColorC(rectColor, ColorUtil.brighter(rectColor, 0.4f), this.hoverAnimation.getOutput().floatValue());
            hoverAnimation.setDirection(hovered ? Direction.BACKWARDS : Direction.FORWARDS);
            if (hovered){
                RoundedUtil.drawRound(x + 11 - FontManager.Icon.get(40).getStringWidth(icon) / 2 + width / 2, y - 1, width, height, 3, rectColor);
            }
            if (hovered) {
                FontManager.Semibold.get(20).drawCenteredString(name, x + 13 + width / 2, y - 15, rectColor.brighter().brighter().brighter().brighter().getRGB());
            }
            FontManager.Icon.get(40).drawString(icon,x + 13  ,y,new Color(-1).getRGB());
        }

        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = RenderUtil.isHovering(x + 11 - FontManager.Icon.get(40).getStringWidth(icon) / 2 + width / 2, y - 1, width, height, mouseX, mouseY);
            if (hovered) clickAction.run();
        }
    }
}
