
package cn.noblefull.event.impl.events.render;

import net.minecraft.client.gui.ScaledResolution;
import cn.noblefull.event.impl.Event;

public record Render3DEvent(float partialTicks, ScaledResolution scaledResolution) implements Event {

}
