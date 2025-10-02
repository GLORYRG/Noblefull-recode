package cn.noblefull.gui.clickgui.noblefull.component.values;

import cn.noblefull.gui.clickgui.Component;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.DecelerateAnimation;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.value.impl.BoolValue;
import cn.noblefull.value.impl.MultiBooleanValue;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MultiBoxComponent extends Component {
    private final MultiBooleanValue setting;
    private final Animation open = new DecelerateAnimation(250, 1);
    private boolean opened;

    private final Map<BoolValue, DecelerateAnimation> select = new HashMap<>();

    public MultiBoxComponent(MultiBooleanValue setting) {
        this.setting = setting;
        setHeight(22);
    }

    @Override
    public float getHeight() {
        if (!opened) {
            return 22;
        }
        float listHeight = Math.min(setting.getValues().size(), 6) * 16 + 4;
        return 22 + listHeight;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        float animationOutput = open.getOutput().floatValue();

        float boxWidth = 80;
        float rightPadding = 5;
        float boxX = getX() + getWidth() - rightPadding - boxWidth;

        Semibold.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, new Color(49, 50, 75).getRGB());
        RoundedUtil.drawRound(boxX, getY() + 13, boxWidth, 17, 2, new Color(220, 217, 217));
        String enabledText = setting.isEnabled().isEmpty() ? "None" : (setting.isEnabled().length() > 10 ? setting.isEnabled().substring(0, 10) + "..." : setting.isEnabled());
        Semibold.get(16).drawString(enabledText, boxX + 4, getY() + 15 + Semibold.get(16).getMiddleOfBox(17), new Color(0x31324B).getRGB());

        if (animationOutput > 0) {
            GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 2f);

            float listHeight = Math.min(setting.getValues().size(), 6) * 16 + 4;
            float animatedListHeight = listHeight * animationOutput;
            float listY = getY() + 31;

            RenderUtil.startGlScissor((int)boxX, (int)listY, (int)boxWidth, (int)animatedListHeight);
            RoundedUtil.drawRound(boxX, listY, boxWidth, listHeight, 2, new Color(222, 219, 219));

            float itemYOffset = 2;
            for (BoolValue boolValue : setting.getValues()) {
                float itemY = listY + itemYOffset;
                select.putIfAbsent(boolValue, new DecelerateAnimation(250, 1));
                select.get(boolValue).setDirection(boolValue.get() ? Direction.FORWARDS : Direction.BACKWARDS);
                float selectionAnim = select.get(boolValue).getOutput().floatValue();
                if (selectionAnim > 0.01) {
                    RoundedUtil.drawRound(boxX + 2, itemY, boxWidth - 4, 16f, 2,
                            ColorUtil.applyOpacity(new Color(192, 190, 190), selectionAnim));
                }
                Semibold.get(16).drawString(boolValue.getName(), boxX + 6, itemY + Semibold.get(16).getMiddleOfBox(16), ColorUtil.interpolateColor(new Color(0x31324B), Color.WHITE, selectionAnim));
                itemYOffset += 16;
            }

            RenderUtil.stopGlScissor(); // 加上对应的 stop
            GlStateManager.popMatrix();

            GL11.glPopAttrib();
        }
    }

    // mouseClicked 等其他方法保持不变
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float boxWidth = 80;
        float rightPadding = 5;
        float boxX = getX() + getWidth() - rightPadding - boxWidth;

        if (RenderUtil.isHovering(boxX, getY() + 13, boxWidth, 17, mouseX, mouseY)) {
            if (mouseButton == 0 || mouseButton == 1) {
                opened = !opened;
            }
            return;
        }

        if (opened) {
            float listHeight = Math.min(setting.getValues().size(), 6) * 16 + 4;
            float listY = getY() + 31;

            if(RenderUtil.isHovering(boxX, listY, boxWidth, listHeight, mouseX, mouseY)) {
                float itemYOffset = 2;
                for (BoolValue boolValue : setting.getValues()) {
                    float itemY = listY + itemYOffset;
                    if (RenderUtil.isHovering(boxX, itemY, boxWidth, 16, mouseX, mouseY) && mouseButton == 0) {
                        boolValue.toggle();
                    }
                    itemYOffset += 16;
                }
            } else {
                opened = false;
            }
        }
    }

    @Override public void mouseReleased(int mouseX, int mouseY, int state) {}
    @Override public void keyTyped(char typedChar, int keyCode) {}
    @Override public boolean isVisible() { return setting.isAvailable(); }
}
