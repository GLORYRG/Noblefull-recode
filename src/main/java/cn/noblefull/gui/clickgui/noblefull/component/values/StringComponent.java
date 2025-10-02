package cn.noblefull.gui.clickgui.noblefull.component.values;

import cn.noblefull.gui.clickgui.Component;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.DecelerateAnimation;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.value.impl.TextValue;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StringComponent extends Component {
    private final TextValue setting;
    private final Animation input = new DecelerateAnimation(250, 1);
    private boolean inputting;
    private String text = "";
    public StringComponent(TextValue setting) {
        this.setting = setting;
        // 【修复】初始高度仍然设置，但getHeight()将覆盖它
        setHeight(22);
        input.setDirection(Direction.BACKWARDS);
    }

    // 【核心修复】覆盖getHeight方法，根据文本行数返回动态的真实高度
    @Override
    public float getHeight() {
        float boxWidth = 90;
        String textToDraw = setting.get().isEmpty() && !inputting ? "Empty..." : setting.getText();
        List<String> wrappedLines = wrapText(textToDraw, boxWidth - 4);
        // 基础高度22，每多一行，高度增加 (行数-1) * 字体高度
        return 22 + Math.max(0, (wrappedLines.size() - 1) * Semibold.get(16).getHeight());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float boxWidth = 90;
        float rightPadding = 5;
        float boxX = getX() + getWidth() - rightPadding - boxWidth;

        input.setDirection(inputting ? Direction.FORWARDS : Direction.BACKWARDS);
        text = setting.get();
        if (setting.isOnlyNumber() && !NumberUtils.isNumber(text)) {
            text = text.replaceAll("[a-zA-Z]", "");
        }
        String textToDraw = setting.get().isEmpty() && !inputting ? "Empty..." : setting.getText();

        Semibold.get(17).drawString(setting.getName(), getX() + 6, getY() + 20, new Color(0x31324A).getRGB());
        RoundedUtil.drawRound(boxX, getY() + 13, boxWidth, 16, 2, new Color(ColorUtil.interpolateColor2(
                new Color(220, 217, 217), new Color(178, 178, 178), (float) input.getOutput().floatValue())));
        drawTextWithLineBreaks(textToDraw + (inputting && text.length() < 59 && System.currentTimeMillis() % 1000 > 500 ? "|" : ""), boxX + 2, getY() + 19, boxWidth - 4);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float boxWidth = 90;
        float rightPadding = 5;
        float boxX = getX() + getWidth() - rightPadding - boxWidth;

        if (RenderUtil.isHovering(boxX, getY() + 13, boxWidth, 16, mouseX, mouseY) && mouseButton == 0) {
            inputting = !inputting;
        } else {
            inputting = false;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (inputting) {
            if (setting.isOnlyNumber() && !("1234567890.".contains(String.valueOf(typedChar)))) {
                if (keyCode != Keyboard.KEY_BACK) return;
            }
            if (keyCode == Keyboard.KEY_BACK) {
                deleteLastCharacter();
            } else if (text.length() < 18 && (Character.isLetterOrDigit(typedChar) || Character.isWhitespace(typedChar) || ".,!?_".contains(String.valueOf(typedChar)))) {
                text += typedChar;
                setting.setText(text);
            }
        }
    }
    private void drawTextWithLineBreaks(String text, float x, float y, float maxWidth) {
        List<String> wrappedLines = wrapText(text, maxWidth);
        float currentY = y;
        for (String wrappedLine : wrappedLines) {
            Semibold.get(16).drawString(wrappedLine, x, currentY, ColorUtil.interpolateColor2(new Color(0x34354D).darker(),
                    new Color(-1), (float) input.getOutput().floatValue()));
            currentY += Semibold.get(16).getHeight();
        }
    }

    private List<String> wrapText(String text, float maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Semibold.get(16).getStringWidth(currentLine.toString() + c) > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
            }
            currentLine.append(c);
        }
        lines.add(currentLine.toString());
        return lines;
    }

    private void deleteLastCharacter() {
        if (!text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
            setting.setText(text);
        }
    }

    @Override public void mouseReleased(int mouseX, int mouseY, int state) {}
    @Override public boolean isVisible() { return setting.isAvailable(); }
}
