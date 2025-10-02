package cn.noblefull.utils.fontrender;


import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import cn.noblefull.module.impl.visuals.InterFace;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.render.GradientUtil;
import cn.noblefull.utils.render.RenderUtil;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;


public class FontRenderer{
    private static final int[] colorCode = new int[32];
    private final boolean antiAlias;

    // 添加字符缓存映射，支持中文和图标字符
    private final Map<Character, CharacterInfo> charMap = new HashMap<>();
    private static final int CHAR_BUFFER_SIZE = 64;

    static {
        for (int i = 0; i < 32; ++i) {
            int base = (i >> 3 & 1) * 85;
            int r = (i >> 2 & 1) * 170 + base;
            int g = (i >> 1 & 1) * 170 + base;
            int b = (i & 1) * 170 + base;
            if (i == 6) {
                r += 85;
            }

            if (i >= 16) {
                r /= 4;
                g /= 4;
                b /= 4;
            }

            colorCode[i] = (r & 255) << 16 | (g & 255) << 8 | b & 255;
        }
    }

    public final float drawCenteredString(String text, float x, float y, int color) {
        return drawString(text, x - getStringWidth(text) / 2, y, color);
    }

    public final float drawCenteredString(String text, double x, double y, int color) {
        return drawString(text, (float) (x - getStringWidth(text) / 2), (float) y, color);
    }

    public final float drawCenteredStringNoFormat(String text, float x, float y, int color) {
        return drawStringNoFormat(text, (x - (float) getStringWidth(text) / 2), y, color, false);
    }

    public final void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        drawStringWithShadow(text, (x - (float) getStringWidth(text) / 2), y, color);
    }

    // 字符信息类，用于存储字符的纹理和尺寸信息
    private static class CharacterInfo {
        int textureId;
        float width;
        float height;
        float u1, v1, u2, v2; // 纹理坐标

        CharacterInfo(int textureId, float width, float height, float u1, float v1, float u2, float v2) {
            this.textureId = textureId;
            this.width = width;
            this.height = height;
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
        }
    }

    private final FontRenderContext context = new FontRenderContext(new AffineTransform(), true, true);
    private Font font = null;

    private float size = 0;
    private int fontHeight = 0;

    public FontRenderer(Font font) {
        this.antiAlias = true;
        this.font = font;
        size = font.getSize2D();
        Rectangle2D maxBounds = font.getMaxCharBounds(context);
        this.fontHeight = (int) Math.ceil(maxBounds.getHeight());
    }

    public FontRenderer(Font font,boolean antiAlias) {
        this.antiAlias = antiAlias;
        this.font = font;
        size = font.getSize2D();
        Rectangle2D maxBounds = font.getMaxCharBounds(context);
        this.fontHeight = (int) Math.ceil(maxBounds.getHeight());
    }

    public final int getHeight() {
        return fontHeight / 2;
    }

    // 修改字符绘制方法，支持Unicode字符
    protected final int drawChar(char chr, float x, float y) {
        CharacterInfo charInfo = getOrGenerateCharacterInfo(chr);
        if (charInfo == null) {
            // 如果无法获取字符信息，绘制一个默认的占位符
            return drawDefaultChar(x, y);
        }

        GlStateManager.bindTexture(charInfo.textureId);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glBegin(GL_QUADS);
        glTexCoord2f(charInfo.u1, charInfo.v1);
        glVertex2f(x, y);
        glTexCoord2f(charInfo.u1, charInfo.v2);
        glVertex2f(x, y + charInfo.height);
        glTexCoord2f(charInfo.u2, charInfo.v2);
        glVertex2f(x + charInfo.width, y + charInfo.height);
        glTexCoord2f(charInfo.u2, charInfo.v1);
        glVertex2f(x + charInfo.width, y);
        glEnd();
        
        return (int) charInfo.width;
    }

    // 绘制默认字符（当字符无法渲染时）
    private int drawDefaultChar(float x, float y) {
        GlStateManager.disableTexture2D();
        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x, y + 8);
        glVertex2f(x + 8, y + 8);
        glVertex2f(x + 8, y);
        glEnd();
        GlStateManager.enableTexture2D();
        return 8; // 返回默认宽度
    }

    public int drawString(String str, float x, float y, int color) {
        return drawString(str, x, y, color, false);
    }
    
    public void drawStringDynamic(String text, double x, double y, int tick1, int tick2) {
        drawStringDynamic(text, x, y, tick1, tick2, 1.0F);
    }

    public void drawStringDynamic(String text, double x, double y, int tick1, int tick2, float opacity) {
        GradientUtil.applyGradientHorizontal((float) x, (float) y, (float) getStringWidth(text), getHeight(), opacity, InterFace.color(tick1), InterFace.color(tick2), () -> {
            RenderUtil.setAlphaLimit(0);
            drawString(text, (float) x, (float) y, -1);
        });
    }

    public int drawString(String str, double x, double y, int color) {
        return drawString(str, (float) x, (float) y, color, false);
    }

    public int drawStringNoFormat(String str, double x, double y, int color) {
        return drawString(str, (float) x, (float) y, color, false);
    }

    public final void drawStringWithShadowNoFormat(String str, float x, float y, int color) {
        drawStringNoFormat(str,x + 0.5f,y + 0.5f,color,true);
        drawStringNoFormat(str, x, y, color, false);
    }

    public final int drawStringNoFormat(String str, float x, float y, int color, boolean darken) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        str = str.replace("▬", "=");
        y = y - 2;
        x *= 2;
        y *= 2;
        y -= 2;
        int offset = 0;
        if (darken) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        float r, g, b, a;
        r = (color >> 16 & 0xFF) / 255f;
        g = (color >> 8 & 0xFF) / 255f;
        b = (color & 0xFF) / 255f;
        a = (color >> 24 & 0xFF) / 255f;
        if (a == 0)
            a = 1;
        GlStateManager.color(r, g, b, a);
        glPushMatrix();
        glScaled(0.5, 0.5, 0.5);
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char chr = chars[i];
            if (chr == '\u00A7' && i != chars.length - 1) {
                i++;
                int colorIndex = "0123456789abcdef".indexOf(chars[i]);
                if (colorIndex != -1) {
                    if (darken) colorIndex |= 0x10;
                    colorIndex = colorCode[colorIndex];
                    r = (colorIndex >> 16 & 0xFF) / 255f;
                    g = (colorIndex >> 8 & 0xFF) / 255f;
                    b = (colorIndex & 0xFF) / 255f;
                    GlStateManager.color(r, g, b, a);
                }
                continue;
            }
            offset += drawChar(chr, x + offset, y);
        }
        glPopMatrix();
        return offset;
    }

    public final int drawString(String str, float x, float y, int color, boolean darken) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        str = str.replace("▬", "=");
        y = y - 2;
        x *= 2;
        y *= 2;
        y -= 2;
        int offset = 0;
        if (darken) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        float r, g, b, a;
        r = (color >> 16 & 0xFF) / 255f;
        g = (color >> 8 & 0xFF) / 255f;
        b = (color & 0xFF) / 255f;
        a = (color >> 24 & 0xFF) / 255f;
        if (a == 0)
            a = 1;
        GlStateManager.color(r, g, b, a);
        glPushMatrix();
        glScaled(0.5, 0.5, 0.5);
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char chr = chars[i];
            if (chr == '\u00A7' && i != chars.length - 1) {
                i++;
                int colorIndex = "0123456789abcdef".indexOf(chars[i]);
                if (colorIndex != -1) {
                    if (darken) colorIndex |= 0x10;
                    colorIndex = colorCode[colorIndex];
                    r = (colorIndex >> 16 & 0xFF) / 255f;
                    g = (colorIndex >> 8 & 0xFF) / 255f;
                    b = (colorIndex & 0xFF) / 255f;
                    GlStateManager.color(r, g, b, a);
                }
                continue;
            }
            offset += drawChar(chr, x + offset, y);
        }
        glPopMatrix();
        return offset;
    }

    public float getMiddleOfBox(float height) {
        return height / 2f - getHeight() / 2f;
    }

    // 修改字符串宽度计算方法，支持Unicode字符
    public final int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (chr == '\u00A7' && i + 1 < text.length()) {
                // 跳过颜色代码
                i++;
                continue;
            }
            
            CharacterInfo charInfo = getOrGenerateCharacterInfo(chr);
            if (charInfo != null) {
                width += charInfo.width;
            } else {
                width += 8; // 默认宽度
            }
        }
        return width / 2;
    }

    public final float getSize() {
        return size;
    }

    // 获取或生成字符信息
    private CharacterInfo getOrGenerateCharacterInfo(char chr) {
        // 检查缓存中是否已存在该字符信息
        if (charMap.containsKey(chr)) {
            return charMap.get(chr);
        }

        // 生成新的字符信息
        return generateCharacterInfo(chr);
    }

    // 生成字符信息
    private CharacterInfo generateCharacterInfo(char chr) {
        // 创建一个足够大的图像来容纳字符
        BufferedImage img = new BufferedImage(CHAR_BUFFER_SIZE, CHAR_BUFFER_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setFont(font);
        g.setColor(Color.WHITE);
        
        // 测量字符尺寸
        FontMetrics fontMetrics = g.getFontMetrics();
        String charStr = String.valueOf(chr);
        Rectangle2D bounds = fontMetrics.getStringBounds(charStr, g);
        
        int charWidth = (int) Math.ceil(bounds.getWidth());
        int charHeight = (int) Math.ceil(bounds.getHeight());
        
        if (charWidth <= 0 || charHeight <= 0) {
            // 无法测量字符尺寸，返回null
            return null;
        }
        
        // 调整图像大小以适应字符
        img = new BufferedImage(Math.max(1, charWidth), Math.max(1, charHeight), BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) img.getGraphics();
        
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setFont(font);
        g.setColor(Color.WHITE);
        
        // 绘制字符
        g.drawString(charStr, 0, fontMetrics.getAscent());
        
        // 生成纹理
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, imageToBuffer(img));
        
        // 创建字符信息对象
        CharacterInfo charInfo = new CharacterInfo(
            textureId,
            charWidth,
            charHeight,
            0f,
            0f,
            1f,
            1f
        );
        
        // 缓存字符信息
        charMap.put(chr, charInfo);
        
        return charInfo;
    }

    private double wrapTextureCoord(int coord, int size) {
        return coord / (double) size;
    }

    private static final ByteBuffer imageToBuffer(BufferedImage img) {
        int[] arr = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        ByteBuffer buf = ByteBuffer.allocateDirect(4 * arr.length);

        for (int i : arr) {
            buf.putInt(i << 8 | i >> 24 & 0xFF);
        }

        buf.flip();
        return buf;
    }

    protected final void finalize() {
        // 清理纹理资源
        for (CharacterInfo charInfo : charMap.values()) {
            glDeleteTextures(charInfo.textureId);
        }
        charMap.clear();
    }

    public final void drawStringWithShadow(String newstr, float i, float i1, int rgb) {
        drawString(newstr, i + 0.5f, i1 + 0.5f, rgb, true);
        drawString(newstr, i, i1, rgb);
    }
    
    private static final float KERNING = 8.2f;
    
    public int drawStringWithShadows(String name, float x, float y, int color) {
        drawString(name, x + .5f, y + .5f, color);
        return (int) drawString(name, x, y, color);
    }

    public final void drawLimitedString(String text, float x, float y, int color, float maxWidth) {
        drawLimitedStringWithAlpha(text, x, y, color, (((color >> 24) & 0xFF) / 255f), maxWidth);
    }

    public final void drawLimitedStringWithAlpha(String text, float x, float y, int color, float alpha, float maxWidth) {
        //   text = processString(text);
        x *= 2.0F;
        y *= 2.0F;
        float originalX = x;
        float curWidth = 0;

        GL11.glPushMatrix();
        GL11.glScaled(0.5F, 0.5F, 0.5F);

        final boolean wasBlend = glGetBoolean(GL_BLEND);
        GlStateManager.enableAlpha();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);

        int currentColor = color;
        char[] characters = text.toCharArray();

        int index = 0;
        for (char c : characters) {
            if (c == '\r') {
                x = originalX;
            }
            if (c == '\n') {
                y += getHeight() * 2.0F;
            }
            if (c != '\247' && (index == 0 || index == characters.length - 1 || characters[index - 1] != '\247')) {
                if (index >= 1 && characters[index - 1] == '\247') continue;
                glPushMatrix();
                drawString(Character.toString(c), x, y, ColorUtil.reAlpha(new Color(currentColor), (int) alpha).getRGB(), false);
                glPopMatrix();

                curWidth += (getStringWidth(Character.toString(c)) * 2.0F);
                x += (getStringWidth(Character.toString(c)) * 2.0F);

                if (curWidth > maxWidth) {
                    break;
                }

            } else if (c == ' ') {
                x += getStringWidth(" ");
            } else if (c == '\247' && index != characters.length - 1) {
                int codeIndex = "0123456789abcdefklmnor".indexOf(text.charAt(index + 1));
                if (codeIndex < 0) continue;

                if (codeIndex < 16) {
                    currentColor = colorCode[codeIndex];
                }
            }
            index++;
        }

        if (!wasBlend) glDisable(GL_BLEND);
        GL11.glPopMatrix();
    }
}