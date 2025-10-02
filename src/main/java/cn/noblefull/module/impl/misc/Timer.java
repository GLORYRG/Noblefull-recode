package cn.noblefull.module.impl.misc;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.packet.PacketSendEvent;
import cn.noblefull.event.impl.events.player.MotionEvent;
import cn.noblefull.event.impl.events.player.UpdateEvent;
import cn.noblefull.event.impl.events.render.Render2DEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.module.impl.visuals.InterFace;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.DecelerateAnimation;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.math.MathUtils;
import cn.noblefull.utils.pack.PacketUtil;
import cn.noblefull.utils.player.MovementUtil;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.value.impl.NumberValue;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author：GLORY
 * @Date：7/7/2025 12:04 AM
 */
public class Timer extends Module {
    final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue();
    final Animation anim = new DecelerateAnimation(250, 1.0);
    private final NumberValue amount = new NumberValue("Amount", 1.0, 1.0, 10.0, 0.1);
    public int count = 0;

    public Timer() {
        super("Timer", Category.Misc);
    }
    @EventTarget
    public void onSuffix(UpdateEvent event){
        setsuffix(amount.get().toString());
    }
    @EventTarget
    public void onMotion(MotionEvent eventMotion) {
        PacketUtil.sendPacketNoEvent(new C0FPacketConfirmTransaction(MathUtils.getRandom(114514, 191981000), (short)MathUtils.getRandomInRange(114514, 191981000), true));
        if (this.count > 0) {
            mc.timer.timerSpeed = MovementUtil.isMoving() ? ((Double)this.amount.getValue()).floatValue() : 1.0f;
        } else {
            mc.timer.timerSpeed = 1.0f;
            if (!this.packets.isEmpty()) {
                this.packets.forEach((Packet<?> packet) -> PacketUtil.sendPacketNoEvent(packet));
                this.packets.clear();
            }
        }
    }

    @EventTarget
    public void onPacketSendEvent(PacketSendEvent eventPacket) {
        if (eventPacket.getPacket() instanceof C03PacketPlayer && !(eventPacket.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook)) {
            if (!((C03PacketPlayer) eventPacket.getPacket()).isMoving()) {
                this.count += 50;
                eventPacket.setCancelled(true);
            } else if (this.count > 0) {
                this.count -= 50;
            }
        }
        if (eventPacket.getPacket() instanceof C0FPacketConfirmTransaction) {
            eventPacket.setCancelled(true);
            this.packets.add(eventPacket.getPacket());
        }
        if (eventPacket.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) eventPacket.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            this.toggle();
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
        if (!this.packets.isEmpty()) {
            this.packets.forEach((Packet<?> packet) -> PacketUtil.sendPacketNoEvent(packet));
            this.packets.clear();
        }
        this.count = 0;
    }
    @EventTarget
    public void onRender2D(Render2DEvent event){
        renderProgessBar4();
    }
    public void renderProgessBar4() {
        this.anim.setDirection(this.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!this.getState() && this.anim.isDone()) {
            return;
        }
        String string = String.valueOf(this.count);
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        float f = (float)this.anim.getOutput().floatValue();
        int n = 3;
        String string2 = "§r Grim Timer Balance: §l" + string;
        float f2 = Bold.get(18).getStringWidth(string2);
        float f3 = (f2 + (float)n + 6.0f) * f;
        float f4 = (float)scaledResolution.getScaledWidth() / 2.0f - f3 / 2.0f;
        float f5 = (float)scaledResolution.getScaledHeight() - ((float)scaledResolution.getScaledHeight() / 2.0f - 20.0f);
        float f6 = 20.0f;
        Color color = ColorUtil.applyOpacity(InterFace.color(1), 222.0f);
        Color color2 = ColorUtil.applyOpacity(InterFace.color(6), 222.0f);
        RenderUtil.scissorStart((double)f4 - 1.5, (double)f5 - 1.5, f3 + 3.0f, f6 + 23.0f);
        RoundedUtil.drawRound(f4, f5, f3, f6 + 20.0f, 4.0f, ColorUtil.tripleColor(20, 0.45f));
        Bold.get(18).drawString(string2, f4 + 2.0f + (float)n, f5 + 9.5f, -1);
        RoundedUtil.drawRound((f4 + 3.0f) * f, f5 + 25.0f, f3 - 8.0f * f, 5.0f, 2.0f, new Color(166, 164, 164, 81));
        RoundedUtil.drawGradientHorizontal(f4 + 3.0f, f5 + 25.0f, (f3 - 8.0f) * Math.min(Math.max((float)this.count / 7500.0f, 0.0f), 1.0f), 5.0f, 2.0f, color, color2);
        RenderUtil.scissorEnd();
    }
}
