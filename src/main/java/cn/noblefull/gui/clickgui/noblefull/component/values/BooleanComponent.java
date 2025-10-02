package cn.noblefull.gui.clickgui.noblefull.component.values;

import cn.noblefull.Client;
import cn.noblefull.gui.clickgui.Component;
import cn.noblefull.module.impl.visuals.InterFace;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.DecelerateAnimation;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.fontrender.FontManager;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.value.impl.BoolValue;

import java.awt.*;

public class BooleanComponent extends Component {
    private final BoolValue setting;
    private final Animation enabled = new DecelerateAnimation(250,1);
    private final Animation hover = new DecelerateAnimation(250,1);
    public BooleanComponent(BoolValue setting) {
        this.setting = setting;
        setHeight(22);
        enabled.setDirection(setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float toggleX = getX() + getWidth() - 30;

        enabled.setDirection(setting.get() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(RenderUtil.isHovering(toggleX, getY() + 15, 22, 12, mouseX, mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);

        FontManager.Semibold.get(18).drawString(setting.getName(), getX() + 5, getY() + 15 + 1.5f, new Color(0x31324B).getRGB());
        RoundedUtil.drawRound(toggleX, getY() + 15, 22, 12, 5.5f, setting.get() ? new Color(ColorUtil.interpolateColor2(new Color(Client.Instance.getModuleManager().getModule(InterFace.class).color()), new Color(0x7C88F5),(float) hover.getOutput().floatValue())) : new Color(ColorUtil.interpolateColor2(new Color(0xC6C6C6), new Color(0xB7B7B7),(float) hover.getOutput().floatValue())));
        RenderUtil.drawCircleCGUI(toggleX + 7 + enabled.getOutput().floatValue() * 9f, getY() + 21, 12,  ColorUtil.interpolateColor2(new Color(0x8A8A8A),new Color(-1),(float) enabled.getOutput().floatValue()));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float toggleX = getX() + getWidth() - 30;
        if (RenderUtil.isHovering(toggleX, getY() + 15, 22, 12, mouseX, mouseY) && mouseButton == 0){
            setting.toggle();
        }
    }

    @Override public void mouseReleased(int mouseX, int mouseY, int state) { }
    @Override public void keyTyped(char typedChar, int keyCode) { }
    @Override public boolean isVisible() { return setting.isAvailable(); }
}
