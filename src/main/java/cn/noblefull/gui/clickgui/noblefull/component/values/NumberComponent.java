package cn.noblefull.gui.clickgui.noblefull.component.values;

import cn.noblefull.gui.clickgui.Component;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.DecelerateAnimation;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.fontrender.FontManager;
import cn.noblefull.utils.math.MathUtils;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.value.impl.NumberValue;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class NumberComponent extends Component {
    private final NumberValue setting;
    private boolean dragging;
    private final Animation drag = new DecelerateAnimation(250, 1);
    public NumberComponent(NumberValue setting) {
        this.setting = setting;
        setHeight(22);
        drag.setDirection(Direction.BACKWARDS);
    }
    private float anim;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float rightPadding = 5;
        String valueStr = setting.get().toString();
        float valueWidth = FontManager.Semibold.get(18).getStringWidth(valueStr);
        float valueBoxWidth = valueWidth + 4;
        float valueBoxX = getX() + getWidth() - rightPadding - valueBoxWidth;

        float sliderBarWidth = 65;
        float sliderX = valueBoxX - rightPadding - sliderBarWidth;

        anim = RenderUtil.animate(anim, (float) (sliderBarWidth * (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin())), 50);
        float currentSliderFill = anim;
        drag.setDirection(dragging ? Direction.FORWARDS : Direction.BACKWARDS);

        FontManager.Semibold.get(18).drawString(setting.getName(), getX() + 5, getY() + 15 + 1.5f, ColorUtil.interpolateColor2(new Color(0x31324B),new Color(0x31324B),(float) drag.getOutput().floatValue()));

        RoundedUtil.drawRound(valueBoxX, getY() + 15, valueBoxWidth, 12, 2,new Color(224, 221, 221));
        FontManager.Semibold.get(17).drawString(valueStr, valueBoxX + 2, getY() + 15 + 3f, ColorUtil.interpolateColor2(new Color(0x31324B),new Color(0x515379),(float) drag.getOutput().floatValue()));

        RoundedUtil.drawRound(sliderX, getY() + 20, sliderBarWidth, 2, 2, new Color(0xC4C4C4));
        RoundedUtil.drawRound(sliderX, getY() + 20, currentSliderFill, 2, 2, new Color(0x98A1EC));
        RenderUtil.drawCircleCGUI(sliderX + currentSliderFill, getY() + 21, 6, ColorUtil.interpolateColor2(new Color(0xE1DFDF),new Color(0xDCDBDB),(float) drag.getOutput().floatValue()));

        if (dragging) {
            final double difference = this.setting.getMax() - this.setting.getMin();
            final double value = this.setting.getMin() + MathHelper.clamp_double((mouseX - sliderX) / sliderBarWidth, 0, 1) * difference;
            setting.setValue(MathUtils.incValue(value, setting.getStep()));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float rightPadding = 5;
        String valueStr = setting.get().toString();
        float valueWidth = FontManager.Semibold.get(18).getStringWidth(valueStr);
        float valueBoxWidth = valueWidth + 4;
        float valueBoxX = getX() + getWidth() - rightPadding - valueBoxWidth;
        float sliderBarWidth = 65;
        float sliderX = valueBoxX - rightPadding - sliderBarWidth;

        if (RenderUtil.isHovering(sliderX - 2, getY() + 18, sliderBarWidth + 4, 6, mouseX, mouseY) && mouseButton == 0) {
            dragging = true;
        }
    }

    @Override public void mouseReleased(int mouseX, int mouseY, int state) { if (state == 0) dragging = false; }
    @Override public void keyTyped(char typedChar, int keyCode) { }
    @Override public boolean isVisible() { return setting.isAvailable(); }
}
