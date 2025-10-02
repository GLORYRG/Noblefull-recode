package cn.noblefull.module.impl.visuals;


import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.render.Render3DEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.value.impl.BoolValue;
import cn.noblefull.value.impl.ColorValue;
import net.minecraft.tileentity.*;

import java.awt.*;

/**
 * @Author: GLORY
 * 2025/5/1
 */

public class ContainerESP extends Module {
    public final BoolValue outline = new BoolValue("Outline", false);
    public final BoolValue filled = new BoolValue("Filled", true);
    public final BoolValue syncColor = new BoolValue("SyncColor", false);
    public final ColorValue color = new ColorValue("Color",()-> !syncColor.get(),new Color(128, 244, 255));

    public final BoolValue chests = new BoolValue("Chests", true);
    public final BoolValue furnaces = new BoolValue("Furnaces", false);
    public final BoolValue brewingStands = new BoolValue("BrewingStands", false);

    public ContainerESP() {
        super("ContainerESP",Category.Visuals);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if ((chests.get() && (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest)) ||
                    (furnaces.get() && tileEntity instanceof TileEntityFurnace) ||
                    (brewingStands.get() && tileEntity instanceof TileEntityBrewingStand)) {
                if (!tileEntity.isInvalid() && mc.theWorld.getBlockState(tileEntity.getPos()) != null) {
                    if (syncColor.get()) {
                        RenderUtil.renderBlock(tileEntity.getPos(),getModule(InterFace.class).color(20).getRGB(),outline.get(),filled.get());
                    } else {
                        RenderUtil.renderBlock(tileEntity.getPos(),color.get().getRGB(),outline.get(),filled.get());
                    }
                }
            }
        }
    }
}