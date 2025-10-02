
package cn.noblefull.module.impl.visuals;

import net.minecraft.network.play.server.S03PacketTimeUpdate;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.packet.PacketSendEvent;
import cn.noblefull.event.impl.events.player.UpdateEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.value.impl.BoolValue;
import cn.noblefull.value.impl.ColorValue;
import cn.noblefull.value.impl.ModeValue;
import cn.noblefull.value.impl.NumberValue;

import java.awt.*;

public class Atmosphere extends Module {
    private final BoolValue time = new BoolValue("Time Editor", true);
    private final NumberValue timeValue = new NumberValue("Time", time::get, 18000, 0, 24000, 1000);
    private static final BoolValue weather = new BoolValue("Weather Editor", true);
    public static final ModeValue weatherValue = new ModeValue("Weather", weather::get, "Clean",
            new String[]{"Clean", "Rain", "Thunder", "Snow", "Blizzard"});
    public static final BoolValue forceSnow = new BoolValue("Force Snow", false);
    public final BoolValue worldColor = new BoolValue("World Color", true);
    public final ColorValue worldColorRGB = new ColorValue("World Color RGB", worldColor::get, Color.WHITE);
    public final BoolValue worldFog = new BoolValue("World Fog", false);
    public final ColorValue worldFogRGB = new ColorValue("World Fog RGB", worldFog::get, Color.WHITE);
    public final NumberValue worldFogDistance = new NumberValue("World Fog Distance", worldFog::get, 0.10F, -1F, 0.9F, 0.1F);

    public Atmosphere() {
        super("Atmosphere",Category.Visuals);
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if(time.get())
            mc.theWorld.setWorldTime((long) timeValue.get().longValue());
        if (weather.get()) {
            switch (weatherValue.get()) {
                case "Rain":
                    mc.theWorld.setRainStrength(1);
                    mc.theWorld.setThunderStrength(0);
                    break;
                case "Thunder":
                    mc.theWorld.setRainStrength(1);
                    mc.theWorld.setThunderStrength(1);
                    break;
                case "Snow":
                    mc.theWorld.setRainStrength(0.5f);
                    mc.theWorld.setThunderStrength(0);
                    break;
                case "Blizzard":
                    mc.theWorld.setRainStrength(1);
                    mc.theWorld.setThunderStrength(0);
                    break;
                default:
                    mc.theWorld.setRainStrength(0);
                    mc.theWorld.setThunderStrength(0);
            }
        }
    }

    @EventTarget
    private void onPacket(PacketSendEvent event) {
        if (time.get() && event.getPacket() instanceof S03PacketTimeUpdate)
            event.setCancelled(true);
    }

    public static boolean shouldForceSnow() {
        return forceSnow.get() && (weatherValue.get().equals("Snow") || weatherValue.get().equals("Blizzard"));
    }
}
