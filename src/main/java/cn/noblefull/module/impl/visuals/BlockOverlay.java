
package cn.noblefull.module.impl.visuals;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import cn.noblefull.Client;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.render.Render3DEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.value.impl.BoolValue;
import cn.noblefull.value.impl.ColorValue;

import java.awt.*;

public class BlockOverlay extends Module {

    public final BoolValue outline = new BoolValue("Outline", true);
    public final BoolValue filled = new BoolValue("Filled", false);
    public final BoolValue syncColor = new BoolValue("Sync Color", false);
    public final ColorValue color = new ColorValue("Color",() -> !syncColor.get(),new Color(255,255,255));

    public BlockOverlay() {
        super("BlockOverlay", Category.Visuals);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if(getBlock(mc.objectMouseOver.getBlockPos()) instanceof BlockAir)
            return;
        if (syncColor.get()) {
            RenderUtil.renderBlock(mc.objectMouseOver.getBlockPos(), Client.Instance.getModuleManager().getModule(InterFace.class).color(0).getRGB(), outline.get(), filled.get());
        } else {
            RenderUtil.renderBlock(mc.objectMouseOver.getBlockPos(), color.get().getRGB(), outline.get(), filled.get());
        }
    }

    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }
}
