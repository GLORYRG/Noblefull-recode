package cn.noblefull.module.impl.display;

import cn.noblefull.Client;
import cn.noblefull.event.impl.events.render.Shader2DEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.ModuleWidget;
import cn.noblefull.module.impl.visuals.InterFace;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.fontrender.FontManager;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.value.impl.ModeValue;
import net.minecraft.item.ItemStack;

import java.awt.*;

/**
 * @Author：Guyuemang
 * @Date：2025/6/2 13:35
 */
public class Inventory extends ModuleWidget {
    public Inventory() {
        super("Inventory",Category.Display);
    }

    @Override
    public void onShader(Shader2DEvent event) {
        float x = renderX;
        float y = renderY;
        float itemWidth = 14;
        float itemHeight = 14;
        float y1 = 17.0F;
        float x1 = 0.7F;
        RoundedUtil.drawRound(x,y, itemWidth + 120, 65, InterFace.radius.get().intValue(), new Color(0, 0, 0, 255));
        for (int i = 9; i < 36; ++i) {
            ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
            RenderUtil.renderItemStack(slot, x + 0.7F, y + 17.5F, 0.80F);
            x += itemWidth;
            x += x1;
            if (i == 17) {
                y += y1 - 1;
                x -= itemWidth * 9.0F;
                x -= x1 * 8.5F;
            }

            if (i == 26) {
                y += y1 - 1;
                x -= itemWidth * 9.0F;
                x -= x1 * 9.0F;
            }
        }
        width = (itemWidth * 9.1F + x1 * 9.0F);
        height = (itemHeight * 3.0F + 19.0F);
    }

    InterFace setting = Client.Instance.getModuleManager().getModule(InterFace.class);
    @Override
    public void render() {
        float x = renderX;
        float y = renderY;
        float itemWidth = 14;
        float itemHeight = 14;
        float y1 = 17.0F;
        float x1 = 0.7F;

                RoundedUtil.drawRound(x,y, itemWidth + 120, 65, InterFace.radius.get().intValue(), new Color(0, 0, 0, 89));
//        RenderUtil.startGlScissor((int) (x - 2), (int) (y - 1), 159, 18);
//                RenderUtil.stopGlScissor();

                FontManager.Bold.get(18).drawString("Inventory",x + 5,y + 2,-1);
                for (int i = 9; i < 36; ++i) {
                    ItemStack slot = mc.thePlayer.inventory.getStackInSlot(i);
                    RenderUtil.renderItemStack(slot, x + 0.7F, y + 17.5F, 0.80F);
                    x += itemWidth;
                    x += x1;
                    if (i == 17) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 8.5F;
                    }

                    if (i == 26) {
                        y += y1 - 1;
                        x -= itemWidth * 9.0F;
                        x -= x1 * 9.0F;
                    }
                }
                width = (itemWidth * 9.1F + x1 * 9.0F);
                height = (itemHeight * 3.0F + 19.0F);


    }

    @Override
    public boolean shouldRender() {
        return getState() && setting.getState();
    }
}
