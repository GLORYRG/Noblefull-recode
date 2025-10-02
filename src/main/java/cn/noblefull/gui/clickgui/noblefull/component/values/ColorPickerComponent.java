package cn.noblefull.gui.clickgui.noblefull.component.values;

import cn.noblefull.gui.clickgui.Component;
import cn.noblefull.module.impl.visuals.ESP;
import cn.noblefull.module.impl.visuals.InterFace;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.DecelerateAnimation;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.value.impl.ColorValue;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class ColorPickerComponent extends Component {
    private final ColorValue setting;
    private final Animation open = new DecelerateAnimation(250, 1);
    private boolean opened, pickingHue, picking, pickingAlpha;

    public ColorPickerComponent(ColorValue setting) {
        this.setting = setting;
        this.setHeight(22);
    }

    // 【核心修复】getHeight() 必须返回最终的、非动画的高度
    @Override
    public float getHeight() {
        // 如果是展开状态，直接返回完全展开的高度 22 + 90
        if (opened) {
            return 112;
        }
        // 否则返回关闭时的高度
        return 22;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        float animationOutput = open.getOutput().floatValue();

        float rightPadding = 5;
        float circleX = getX() + getWidth() - rightPadding - 15;

        Semibold.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, new Color(49, 50, 75).getRGB());
        RenderUtil.drawCircle(circleX, getY() + 22, 0, 360, 7, 2, true, (setting.isRainbow() ? INSTANCE.getModuleManager().getModule(InterFace.class).getRainbow(0) : setting.get().getRGB()));
        ESP.resetColor();

        // 绘制逻辑使用动画值，这是正确的
        if (animationOutput > 0) {
            // 这里可以添加一个 GlStateManager.push/pop 和 GlStateManager.color(1,1,1, animationOutput)
            // 来实现淡入淡出效果，或者用 Scissor 实现滑出效果。
            // 为了简单，我们暂时只保证布局正确。

            float pickerWidth = getWidth() - 10;
            float pickerX = getX() + 5;

            float gradientWidth = pickerWidth - 30;
            // 绘制时的高度依赖于动画
            float gradientHeight = 80 * animationOutput;
            float gradientX = pickerX + 25;
            float gradientY = getY() + 34;

            float sliderHeight = 78 * animationOutput;

            float hueSliderX = pickerX + 12;
            float alphaSliderX = pickerX;

            float[] hsb = {setting.getHue(), setting.getSaturation(), setting.getBrightness()};

            // Hue Slider
            RenderUtil.drawRect(hueSliderX, getY() + 34, 10, sliderHeight, Color.BLACK.getRGB());
            for (float i = 0; i <= sliderHeight; i++) {
                RenderUtil.drawRect(hueSliderX + 1, getY() + 34 + i, 8, 1, Color.getHSBColor(i / sliderHeight, 1f, 1f).getRGB());
            }
            RenderUtil.drawRect(hueSliderX, (float) (getY() + 34 + (setting.isRainbow() ? INSTANCE.getModuleManager().getModule(InterFace.class).getRainbowHSB(0)[0] : setting.getHue()) * sliderHeight), 10, 1, Color.WHITE.getRGB());

            // Alpha Slider
            for (float i = 0; i <= sliderHeight; i++) {
                RenderUtil.drawRect(alphaSliderX + 1, getY() + 34 + i, 8, 1, ColorUtil.applyOpacity(Color.WHITE, 1 - (i / 78f)).getRGB());
            }
            RenderUtil.drawRect(alphaSliderX, (float) (getY() + 34 + (1 - setting.getAlpha()) * sliderHeight), 10, 1, Color.WHITE.getRGB());

            // Picker
            if (pickingHue) {
                setting.setHue(MathHelper.clamp_float((mouseY - (getY() + 34)) / 78, 0, 1));
            }
            if (pickingAlpha) {
                setting.setAlpha(MathHelper.clamp_float(1 - ((mouseY - (getY() + 34)) / 78), 0, 1));
            }
            if (picking) {
                setting.setBrightness(MathHelper.clamp_float(1 - ((mouseY - gradientY) / 80), 0, 1));
                setting.setSaturation(MathHelper.clamp_float((mouseX - gradientX) / gradientWidth, 0, 1));
            }

            Color firstColor = Color.getHSBColor(hsb[0], 1, 1);
            RoundedUtil.drawRound(gradientX, gradientY, gradientWidth, gradientHeight, 2, firstColor);
            RoundedUtil.drawGradientHorizontal(gradientX, gradientY, gradientWidth, gradientHeight, 2 + .5f, Color.WHITE, ColorUtil.applyOpacity(Color.WHITE, 0));
            RoundedUtil.drawGradientVertical(gradientX, gradientY, gradientWidth, gradientHeight, 2, ColorUtil.applyOpacity(Color.BLACK, 0), Color.BLACK);

            float pickerY = (gradientY) + (gradientHeight * (1 - hsb[2]));
            float pickerXPos = (gradientX) + (gradientWidth * hsb[1]);
            RenderUtil.drawCircle((int) pickerXPos, (int) pickerY, 0, 360, 2, .1f, false, -1);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float rightPadding = 5;
        float circleX = getX() + getWidth() - rightPadding - 15;

        if (RenderUtil.isHovering(circleX - 7, getY() + 15f, 14, 14, mouseX, mouseY) && mouseButton == 1) {
            opened = !opened;
        }
        if (opened) {
            float pickerWidth = getWidth() - 10;
            float pickerX = getX() + 5;
            float gradientWidth = pickerWidth - 30;
            float gradientX = pickerX + 25;
            float hueSliderX = pickerX + 12;
            float alphaSliderX = pickerX;

            if (mouseButton == 0) {
                if (RenderUtil.isHovering(hueSliderX, getY() + 34, 10, 78, mouseX, mouseY)) pickingHue = true;
                if (RenderUtil.isHovering(gradientX, getY() + 34, gradientWidth, 80, mouseX, mouseY)) picking = true;
                if (RenderUtil.isHovering(alphaSliderX, getY() + 34, 10, 78, mouseX, mouseY)) pickingAlpha = true;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            pickingHue = false;
            picking = false;
            pickingAlpha = false;
        }
    }

    @Override public void keyTyped(char typedChar, int keyCode) {}
    @Override public boolean isVisible() { return setting.isAvailable(); }
}
