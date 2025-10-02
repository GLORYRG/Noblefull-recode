package cn.noblefull.gui.clickgui.noblefull;


import cn.noblefull.Client;
import cn.noblefull.gui.clickgui.noblefull.panel.CategoryPanel;
import cn.noblefull.module.Category;
import cn.noblefull.module.ModuleWidget;
import cn.noblefull.module.impl.visuals.InterFace;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.EaseOutSine;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.fontrender.FontManager;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.utils.render.StencilUtils;
import cn.noblefull.utils.render.shader.impl.Blur;
import cn.noblefull.utils.render.shader.impl.Shadow;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Getter
public class NoblefullClickGui extends GuiScreen {

    private static final float LEFT_HIGHLIGHT_ANIMATION_SPEED = 10f;
    public static Animation openingAnimation = new EaseOutSine(300, 1);

    // 【新增】定义顶部标题栏和底部信息栏的高度，方便CategoryPanel引用
    public static final float TOP_BAR_HEIGHT = 30f;
    public static final float BOTTOM_BAR_HEIGHT = 8f;


    private final List<CategoryPanel> categoryPanels = new ArrayList<>();
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);
    public int x;
    public int y;
    public int w = 400;
    public final int h = 300;
    private int dragX;
    private int dragY;
    private boolean dragging = false;

    private float animatedHighlightRelativeY;
    private float targetHighlightRelativeY;

    public NoblefullClickGui() {
        Arrays.stream(Category.values())
                .forEach(moduleCategory -> categoryPanels
                        .add(new CategoryPanel(moduleCategory)));

        if (!categoryPanels.isEmpty()) {
            categoryPanels.get(0).setSelected(true);
            float initialRelativeY = 73 + getSelected().getCategory().ordinal() * 30;
            this.animatedHighlightRelativeY = initialRelativeY;
            this.targetHighlightRelativeY = initialRelativeY;
        }
        x = 40;
        y = 40;
    }


    @Override
    public void initGui() {
        openingAnimation.setDirection(Direction.FORWARDS);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (openingAnimation.isDone() && openingAnimation.getDirection() == Direction.BACKWARDS) {
            mc.displayGuiScreen(null);
            return;
        }

        float scale = openingAnimation.getOutput().floatValue();
        float alpha = scale;

        GlStateManager.pushMatrix();
        float centerX = x + w / 2f;
        float centerY = y + h / 2f;
        GlStateManager.translate(centerX, centerY, 0);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-centerX, -centerY, 0);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);


        if (dragging) {
            x = mouseX + dragX;
            y = mouseY + dragY;
        }

        this.animatedHighlightRelativeY = RenderUtil.animate(this.animatedHighlightRelativeY, this.targetHighlightRelativeY, LEFT_HIGHLIGHT_ANIMATION_SPEED);

        // 绘制阴影效果在模糊背景之下
        InterFace interfaceModule = Client.Instance.getModuleManager().getModule(InterFace.class);
        if (interfaceModule.shadow.get()) {
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer, true);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(true);

            // 绘制要应用阴影的形状到帧缓冲区
            RoundedUtil.drawRound(x, y, w, h, 4, new Color(215, 221, 229, 255));
            RoundedUtil.drawRound(x + 120, y, w - 120, h, 4, new Color(236, 238, 242, 255));

            // 解绑帧缓冲区
            stencilFramebuffer.unbindFramebuffer();

            // 应用阴影效果
            mc.getFramebuffer().bindFramebuffer(true);
            Shadow.renderBloom(stencilFramebuffer.framebufferTexture,
                    (int) interfaceModule.shadowRadius.get().floatValue(),
                    (int) interfaceModule.shadowOffset.get().floatValue());
        }

        // 使用模糊效果绘制背景
        if (interfaceModule.clickguimoduleBlur.get()) {

            Blur.startBlur();
            RoundedUtil.drawRound(x, y, w, h, 4, new Color(215, 221, 229, 255));
            RoundedUtil.drawRound(x + 120, y, w - 120, h, 4, new Color(236, 238, 242, 255));
            Blur.endBlur(interfaceModule.blurRadius.getValue().floatValue(), interfaceModule.blurCompression.getValue().floatValue());
        } else {
            // 如果没有启用模糊，则直接绘制背景

            RoundedUtil.drawRound(x, y, w, h, 4, new Color(215, 221, 229, 255));
            RoundedUtil.drawRound(x + 120, y, w - 120, h, 4, new Color(236, 238, 242, 255));
        }
        if(interfaceModule.clickguimoduleBlur.get()) {
            float shadowOffset = interfaceModule.shadowOffset.get().floatValue() + 0.4f;
            FontManager.Bold.get(35).drawCenteredString("Noblefull".toUpperCase(Locale.ROOT), x + 60 + shadowOffset, y + 20 + shadowOffset, new Color(0x8B28282C, true).getRGB());
        }
        FontManager.Bold.get(35).drawCenteredString("Noblefull".toUpperCase(Locale.ROOT), x + 60, y + 20, Client.Instance.getModuleManager().getModule(InterFace.class).color());
//        RenderUtil.drawImage(new ResourceLocation("Noblefull/images/users/user.png"),x + 10, y +270 ,25,25);
//        FontManager.Semibold.get(25).drawCenteredString(Client.userName, x + 50, y + 279, new Color(151, 160, 234).getRGB());
        RoundedUtil.drawRound(x + 10, y + animatedHighlightRelativeY, 100, 19, 5, new Color(Client.Instance.getModuleManager().getModule(InterFace.class).color()));
        Color defaultTextColor = new Color(0x31324B);
        Color highlightedTextColor = new Color(-1);
        if(interfaceModule.clickguimoduleBlur.get()){
             defaultTextColor = new Color(-1);
             highlightedTextColor = new Color(-1);
        }
        Color highlightedIconColor = new Color(0xFFFFFF);
        for (CategoryPanel categoryPanel : categoryPanels) {

            float categoryTopRelativeY = 73 + categoryPanel.getCategory().ordinal() * 30;
            float distance = Math.abs((animatedHighlightRelativeY + 9.5f) - (categoryTopRelativeY + 9.5f));
            float highlightFactor = 1.0f - Math.min(distance / 30.0f, 1.0f);
            int dynamicTextColor = ColorUtil.interpolateColor(defaultTextColor, highlightedTextColor, highlightFactor);
            int dynamicIconColor = ColorUtil.interpolateColor(Client.Instance.getModuleManager().getModule(InterFace.class).color(), highlightedIconColor.getRGB(), highlightFactor);
            float categoryTextY = y + 79 + categoryPanel.getCategory().ordinal() * 30;
            float iconY = y + 75 + categoryPanel.getCategory().ordinal() * 30;
            FontManager.Semibold.get(18).drawString(categoryPanel.getCategory().name(), x + 36, categoryTextY, dynamicTextColor);
            FontManager.Icon1.get(45).drawString(categoryPanel.getCategory().icon, x + 14, iconY,dynamicIconColor);
        }

        CategoryPanel selected = getSelected();
        if (selected != null) {
            // 【核心修改】在这里绘制固定的顶部大类标题
            float topBarX = x + 110;
            float topBarY = y;



            if (interfaceModule.shadow.get()) {
                float shadowOffset = interfaceModule.shadowOffset.get().floatValue();
                FontManager.Bold.get(24).drawString(selected.getCategory().name(), topBarX + 15 + shadowOffset, topBarY + 18 + shadowOffset, new Color(0, 0, 0, 100).getRGB());
            }

            if(interfaceModule.clickguimoduleShadow.get()){
                FontManager.Bold.get(24).drawString(selected.getCategory().name(), topBarX + 15, topBarY + 18, new Color(255, 255, 255, 255).getRGB());
            }else FontManager.Bold.get(24).drawString(selected.getCategory().name(), topBarX + 15, topBarY + 18, new Color(0, 0, 0, 180).getRGB());
            // 让CategoryPanel去绘制中间的可滚动部分
            selected.drawScreen(mouseX, mouseY);
        }

        // 绘制固定的底部版权信息
        String clientInfo = Client.name + " " + Client.version +" | Powered by GLORY";
        float infoX = x + w - FontManager.Semibold.get(14).getStringWidth(clientInfo) - 5;
        float infoY = y + h - FontManager.Semibold.get(14).getHeight()+4; // Y坐标基于底部
        FontManager.Semibold.get(14).drawString(clientInfo, infoX, infoY, new Color(0, 0, 0, 100).getRGB());


        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    // ... 其他方法保持不变 ...
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!openingAnimation.isDone()) return;

        if (RenderUtil.isHovering(x, y, w, 30, mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
            dragX = x - mouseX;
            dragY = y - mouseY;
            return;
        }

        boolean clickedOnLeftPanel = RenderUtil.isHovering(x, y + 70, 120, h - 70, mouseX, mouseY);
        if (clickedOnLeftPanel) {
            for (CategoryPanel panel : categoryPanels) {
                if (RenderUtil.isHovering(x + 10, y + 73 + panel.getCategory().ordinal() * 30, 100, 19, mouseX, mouseY) && mouseButton == 0) {
                    if (getSelected() != panel) {
                        float newTargetRelativeY = 73 + panel.getCategory().ordinal() * 30;
                        this.targetHighlightRelativeY = newTargetRelativeY;

                        categoryPanels.forEach(p -> p.setSelected(false));
                        panel.setSelected(true);
                    }
                    return;
                }
            }
        }

        CategoryPanel selected = getSelected();
        if (selected != null) {
            selected.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (!openingAnimation.isDone()) return;

        if (state == 0) {
            dragging = false;
        }
        CategoryPanel selected = getSelected();
        if (selected != null) {
            selected.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            openingAnimation.setDirection(Direction.BACKWARDS);
            return;
        }

        if (!openingAnimation.isDone()) return;

        CategoryPanel selected = getSelected();
        if (selected != null) {
            selected.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }



    public CategoryPanel getSelected() {
        return categoryPanels.stream().filter(CategoryPanel::isSelected).findFirst().orElse(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}