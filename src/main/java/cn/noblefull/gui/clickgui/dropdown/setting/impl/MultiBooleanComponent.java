
package cn.noblefull.gui.clickgui.dropdown.setting.impl;

import cn.noblefull.gui.clickgui.Component;
import cn.noblefull.module.impl.visuals.InterFace;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.EaseOutSine;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.fontrender.FontManager;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.value.impl.BoolValue;
import cn.noblefull.value.impl.MultiBooleanValue;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author：GLORY
 * @Date：2025/7/3 12:31
 */
public class MultiBooleanComponent extends Component {
    private final MultiBooleanValue setting;
    private final Map<BoolValue, EaseOutSine> select = new HashMap<>();

    public MultiBooleanComponent(MultiBooleanValue setting) {
        this.setting = setting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float offset = 8;
        float heightoff = 0;

        RoundedUtil.drawRound(getX() + offset, getY() + FontManager.Bold.get(15).getHeight() + 2, getWidth() - 5, heightoff, 4, new Color(128, 128, 128));
        FontManager.Bold.get(15).drawString(setting.getName(), getX() + 4, getY(), -1);

        for (BoolValue boolValue : setting.getValues()) {
            float off = FontManager.Bold.get(13).getStringWidth(boolValue.getName()) + 4;
            if (offset + off >= getWidth() - 5) {
                offset = 8;
                heightoff += FontManager.Bold.get(13).getHeight() + 2;
            }
            select.putIfAbsent(boolValue, new EaseOutSine(250, 1));
            select.get(boolValue).setDirection(boolValue.get() ? Direction.FORWARDS : Direction.BACKWARDS);

            FontManager.Bold.get(13).drawString(boolValue.getName(), getX() + offset, getY() + FontManager.Bold.get(15).getHeight() + 2 + heightoff, ColorUtil.interpolateColor2(InterFace.mainColor.get(),new Color(-1), (float) select.get(boolValue).getOutput().floatValue()));

            offset += off;
        }

        setHeight(FontManager.Bold.get(15).getHeight() + 10 + heightoff);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouse) {
        float offset = 8;
        float heightoff = 0;
        for (BoolValue boolValue : setting.getValues()) {
            float off = FontManager.Bold.get(13).getStringWidth(boolValue.getName()) + 4;
            if (offset + off >= getWidth() - 5) {
                offset = 8;
                heightoff += FontManager.Bold.get(13).getHeight() + 2;
            }
            if (RenderUtil.isHovering(getX() + offset, getY() + FontManager.Bold.get(15).getHeight() + 2 + heightoff, FontManager.Bold.get(13).getStringWidth(boolValue.getName()), FontManager.Bold.get(13).getHeight(), mouseX, mouseY) && mouse == 0) {
                boolValue.set(!boolValue.get());
            }
            offset += off;
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return setting.isAvailable();
    }
}
