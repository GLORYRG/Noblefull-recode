package cn.noblefull.gui.clickgui.noblefull.component;

import cn.noblefull.Client;
import cn.noblefull.gui.clickgui.Component;
import cn.noblefull.gui.clickgui.IComponent;
import cn.noblefull.gui.clickgui.noblefull.component.values.*;
import cn.noblefull.module.Mine;
import cn.noblefull.module.Module;
import cn.noblefull.module.impl.visuals.InterFace;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.DecelerateAnimation;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.fontrender.FontManager;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.utils.render.shader.ShaderElement;
import cn.noblefull.value.Value;
import cn.noblefull.value.impl.*;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.ArrayList;

@Getter
@Setter
public class ModuleComponent implements IComponent {
    // 【修改】将头部高度从 28f 减小到 24f，使模块更紧凑
    private static final float HEADER_HEIGHT = 24f;
    private static final float SETTING_TITLE_AREA_HEIGHT = 25f;
    private static final float BOTTOM_PADDING = 10f;

    private final Module module;
    private final ArrayList<Component> components = new ArrayList<>();
    private boolean extended = false;
    private float x, y, width, height;
    private int panelX, panelY, scroll, panelHeight;

    private final Animation extendAnimation = new DecelerateAnimation(250, 1);
    private final Animation toggleAnimation = new DecelerateAnimation(250, 1);

    public ModuleComponent(Module module) {
        this.module = module;
        for (Value<?> value : module.getSettings()) {
            if (value instanceof BoolValue) {
                components.add(new BooleanComponent((BoolValue) value));
            }
            if (value instanceof ModeValue) {
                components.add(new ModeComponent((ModeValue) value));
            }
            if (value instanceof NumberValue) {
                components.add(new NumberComponent((NumberValue) value));
            }
            if (value instanceof ColorValue) {
                components.add(new ColorPickerComponent((ColorValue) value));
            }
            if (value instanceof TextValue) {
                components.add(new StringComponent((TextValue) value));
            }
            if (value instanceof MultiBooleanValue) {
                components.add(new MultiBoxComponent((MultiBooleanValue) value));
            }
        }
        this.extendAnimation.setDirection(Direction.BACKWARDS);
        this.toggleAnimation.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    public float getCurrentHeight() {
        extendAnimation.setDirection(extended ? Direction.FORWARDS : Direction.BACKWARDS);
        float totalSettingsHeight = getTotalSettingsHeight();
        float expandedAreaHeight = 0;
        if (totalSettingsHeight > 0) {
            expandedAreaHeight = SETTING_TITLE_AREA_HEIGHT + totalSettingsHeight + BOTTOM_PADDING;
        }
        return HEADER_HEIGHT + (expandedAreaHeight * extendAnimation.getOutput().floatValue());
    }

    private float getTotalSettingsHeight() {
        float height = 0;
        for (Component component : components) {
            if (component.isVisible()) {
                height += component.getHeight();
            }
        }
        return height;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        int drawX = panelX + (int) x;
        int drawY = panelY + (int) y + scroll;

        float currentAnimatedHeight = getCurrentHeight();
        InterFace interfaceModule = Client.Instance.getModuleManager().getModule(InterFace.class);
        float animationOutput = extendAnimation.getOutput().floatValue();

        // 绘制模块背景阴影，使用与NoblefullClickGui相同的阴影实现方式
        if (interfaceModule.clickguimoduleShadow.get()) {
            float shadowOffset = interfaceModule.shadowOffset.get().floatValue() + 0.5f;
            RoundedUtil.drawRound(drawX + shadowOffset, drawY + shadowOffset, width, currentAnimatedHeight, 6, new Color(0, 0, 0, 140));
            // 添加阴影任务，使用与Display类中相同的方式实现阴影效果
            ShaderElement.addBloomTask(() -> {
                RoundedUtil.drawRound(drawX, drawY, width, currentAnimatedHeight, 6, new Color(0, 0, 0, 255));
            });
        }

        RoundedUtil.drawRound(drawX, drawY, width, currentAnimatedHeight, 6, new Color(255, 255, 255));

        // 垂直居中公式会自动适应新的 HEADER_HEIGHT
        FontManager.Bold.get(20).drawString(module.getName(), drawX + 8, drawY + (HEADER_HEIGHT / 2f) - (FontManager.Bold.get(20).getHeight() / 2f), new Color(0, 0, 0, 200).getRGB());

        toggleAnimation.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        float toggleOutput = toggleAnimation.getOutput().floatValue();
        float switchX = drawX + width - 28;
        // 垂直居中公式会自动适应新的 HEADER_HEIGHT
        float switchY = drawY + (HEADER_HEIGHT - 10) / 2f;
        Color disabledColor = new Color(190, 190, 190);
        Color enabledColor = new Color(Client.Instance.getModuleManager().getModule(InterFace.class).color());
        RoundedUtil.drawRound(switchX, switchY, 20, 10, 5, new Color(ColorUtil.interpolateColor2(disabledColor, enabledColor, toggleOutput)));
        RoundedUtil.drawRound(switchX + 1 + (10 * toggleOutput), switchY + 1, 8, 8, 4, Color.WHITE);

        if (animationOutput > 0) {
            float totalSettingsHeight = getTotalSettingsHeight();
            if (totalSettingsHeight > 0) {
                float expandedContentHeight = SETTING_TITLE_AREA_HEIGHT + totalSettingsHeight + BOTTOM_PADDING;
                float animatedScissorHeight = expandedContentHeight * animationOutput;

                if (animatedScissorHeight > 1) {
                    GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);

                    try {
                        final ScaledResolution sr = new ScaledResolution(Mine.getMinecraft());
                        final int scaleFactor = sr.getScaleFactor();

                        IntBuffer scissorBuffer = BufferUtils.createIntBuffer(16);
                        GL11.glGetInteger(GL11.GL_SCISSOR_BOX, scissorBuffer);
                        int panelScissorX = scissorBuffer.get(0);
                        int panelScissorY = scissorBuffer.get(1);
                        int panelScissorWidth = scissorBuffer.get(2);
                        int panelScissorHeight = scissorBuffer.get(3);

                        int animScissorX = drawX * scaleFactor;
                        int animScissorWidth = (int)width * scaleFactor;
                        int animScissorHeight = (int)animatedScissorHeight * scaleFactor;
                        int animScissorY = (sr.getScaledHeight() - (drawY + (int)HEADER_HEIGHT) - (int)animatedScissorHeight) * scaleFactor;

                        int finalScissorX = Math.max(panelScissorX, animScissorX);
                        int finalScissorY = Math.max(panelScissorY, animScissorY);
                        int finalScissorX2 = Math.min(panelScissorX + panelScissorWidth, animScissorX + animScissorWidth);
                        int finalScissorY2 = Math.min(panelScissorY + panelScissorHeight, animScissorY + animScissorHeight);

                        int finalScissorWidth = finalScissorX2 - finalScissorX;
                        int finalScissorHeight = finalScissorY2 - finalScissorY;

                        if (finalScissorWidth > 0 && finalScissorHeight > 0) {
                            GL11.glScissor(finalScissorX, finalScissorY, finalScissorWidth, finalScissorHeight);

                            GlStateManager.color(1, 1, 1, 1);
                            // 绘制设置区域阴影，使用与NoblefullClickGui相同的阴影实现方式
                            FontManager.Semibold.get(18).drawString("Setting", drawX + 5, drawY + HEADER_HEIGHT + 5, new Color(0, 0, 0, 100).getRGB());
                            float settingsStartY = drawY + HEADER_HEIGHT + SETTING_TITLE_AREA_HEIGHT;
                            float settingYOffset = 0;
                            for (Component component : components) {
                                if (component.isVisible()) {
                                    component.setX(drawX);
                                    component.setY((int) settingsStartY + (int) settingYOffset);
                                    component.setWidth((int) width);
                                    component.drawScreen(mouseX, mouseY);
                                    settingYOffset += component.getHeight();
                                }
                            }
                        }
                    } finally {
                        GL11.glPopAttrib();
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        int drawX = panelX + (int) x;
        int drawY = panelY + (int) y + scroll;

        // isHovering 会自动使用新的 HEADER_HEIGHT
        if (RenderUtil.isHovering(drawX, drawY, width, HEADER_HEIGHT, mouseX, mouseY)) {
            if (mouseButton == 0) {
                module.toggle();
            } else if (mouseButton == 1) {
                if (!components.isEmpty()) {
                    extended = !extended;
                }
            }
            return;
        }

        if (extended) {
            float settingsStartY = drawY + HEADER_HEIGHT + SETTING_TITLE_AREA_HEIGHT;
            float settingYOffset = 0;
            for (Component component : components) {
                if (component.isVisible()) {
                    component.setY((int) settingsStartY + (int) settingYOffset);
                    component.mouseClicked(mouseX, mouseY, mouseButton);
                    settingYOffset += component.getHeight();
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (extended) {
            components.forEach(c -> c.mouseReleased(mouseX, mouseY, state));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (extended) {
            components.forEach(c -> c.keyTyped(typedChar, keyCode));
        }
    }
}
