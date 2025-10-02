package cn.noblefull.gui.clickgui.noblefull.component.values;

import cn.noblefull.gui.clickgui.Component;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.DecelerateAnimation;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.value.impl.ModeValue;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ModeComponent extends Component {
    private final ModeValue setting;
    private final Animation open = new DecelerateAnimation(250, 1);
    private boolean opened;

    private final Map<String, DecelerateAnimation> select = new HashMap<>();

    public ModeComponent(ModeValue setting) {
        this.setting = setting;
        setHeight(22);
    }

    @Override
    public float getHeight() {
        return 22;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY) {
        open.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        float animationOutput = open.getOutput().floatValue();

        float boxWidth = 80;
        float rightPadding = 5;
        float boxX = getX() + getWidth() - rightPadding - boxWidth;

        RoundedUtil.drawRound(boxX, getY() + 13, boxWidth, 17, 4, new Color(222, 219, 219));

        // 【新增】根据展开动画，计算文字向下的位移动画，使其有“按下”效果
        float textYOffset = 1.5f * animationOutput;
        Semibold.get(16).drawString(setting.get(), boxX + 4, getY() + 15 + textYOffset + Semibold.get(16).getMiddleOfBox(17), new Color(0x31324B).getRGB());

        Semibold.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, new Color(0x31324B).getRGB());

        if (animationOutput > 0) {
            GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 2f);

            float listHeight = Math.min(setting.getModes().length, 6) * 16 + 4;
            float animatedListHeight = listHeight * animationOutput;
            float listY = getY() + 31;

            RenderUtil.startGlScissor((int)boxX, (int)listY, (int)boxWidth, (int)animatedListHeight);
            RoundedUtil.drawRound(boxX, listY, boxWidth, listHeight, 6, new Color(222, 219, 219));

            float itemYOffset = 2;
            for (String str : setting.getModes()) {
                float itemY = listY + itemYOffset;
                select.putIfAbsent(str, new DecelerateAnimation(250, 1));
                select.get(str).setDirection(str.equals(setting.get()) ? Direction.FORWARDS : Direction.BACKWARDS);
                float selectionAnim = select.get(str).getOutput().floatValue();
                if (selectionAnim > 0.01) {
                    RoundedUtil.drawRound(boxX + 2, itemY, boxWidth - 4, 16f, 4,
                            ColorUtil.applyOpacity(new Color(194, 191, 191), selectionAnim));
                }
                Semibold.get(16).drawString(str, boxX + 6, itemY + 2 + Semibold.get(16).getMiddleOfBox(16), ColorUtil.interpolateColor(new Color(0x31324B), Color.WHITE, selectionAnim));
                itemYOffset += 16;
            }

            RenderUtil.stopGlScissor();
            GlStateManager.popMatrix();
            GL11.glPopAttrib();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float boxWidth = 80;
        float rightPadding = 5;
        float boxX = getX() + getWidth() - rightPadding - boxWidth;

        if (RenderUtil.isHovering(boxX, getY() + 13, boxWidth, 17, mouseX, mouseY)) {
            // 【修改】将打开/关闭的操作限制为仅鼠标右键 (mouseButton == 1)
            if (mouseButton == 1) {
                opened = !opened;
            }
            // 无论左键右键，点击了主框就直接返回，不触发下面的“点击外部关闭”逻辑
            return;
        }

        if (opened) {
            float listHeight = Math.min(setting.getModes().length, 6) * 16 + 4;
            float listY = getY() + 31;

            if(RenderUtil.isHovering(boxX, listY, boxWidth, listHeight, mouseX, mouseY)) {
                float itemYOffset = 2;
                for (String str : setting.getModes()) {
                    float itemY = listY + itemYOffset;
                    // 列表项的选择保持为鼠标左键 (mouseButton == 0)
                    if (RenderUtil.isHovering(boxX, itemY, boxWidth, 16, mouseX, mouseY) && mouseButton == 0) {
                        setting.set(str);
                        opened = false;
                        break;
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
