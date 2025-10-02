package cn.noblefull.module.impl.combat;

import cn.jnic.JNICInclude;
import cn.noblefull.Client;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.packet.PacketReceiveEvent;
import cn.noblefull.event.impl.events.packet.PacketSendEvent;
import cn.noblefull.event.impl.events.player.UpdateEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.utils.player.Rotation;
import cn.noblefull.utils.rotation.RotationUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * @Author：GLORY&Daniel
 * @Date：8/28/2025 Fuck
 */
@JNICInclude
public class AntiKnockBack extends Module{
    public AntiKnockBack() {
        super("AntiKnockBack", Category.Combat);
    }

    public boolean packetOnGround;
    public int ticksSinceVelocity;
    public boolean receiveVelocity;
    public boolean jumped;
    public boolean forward;
    public Rotation oppositeRotation;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        this.setsuffix("Precise");
        if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isInWeb) return;
        if (ticksSinceVelocity >= 1) ticksSinceVelocity++;
        if (ticksSinceVelocity >= 11) {
            if (receiveVelocity) receiveVelocity = false;
            ticksSinceVelocity = -1;
        }
        if (ticksSinceVelocity >= 1) {
            //TODO: Jump
            if (ticksSinceVelocity <= 3 && packetOnGround) {
                mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent("Jump"));
                mc.gameSettings.keyBindJump.pressed = true;
                jumped = true;
            }
            //TODO: ComputeMovementKey
            if (ticksSinceVelocity <= 4 && jumped) {
                computeMovementKeys();
                forward = true;
            }
        }
        //TODO: ResetKeys
        if (ticksSinceVelocity >= 5 && ticksSinceVelocity <= 10 && jumped) {
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
            jumped = false;
            if (forward) {
                resetKeys();
                forward = false;
            }
        }
    }

    @EventTarget
    public void onPacket(PacketSendEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer packet) {
            packetOnGround = packet.isOnGround();
        }
    }

    @EventTarget
    public void onPacket(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                int mx = packet.getMotionX();
                int my = packet.getMotionY();
                int mz = packet.getMotionZ();
                if ((mx != 0 || mz != 0) && my >= 0 && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()) {
                    ticksSinceVelocity = 1;
                    receiveVelocity = true;

                    Vec3 knockback = new Vec3(mx / 8000D, 0, mz / 8000D).normalize();
                    knockback = new Vec3(knockback.xCoord * -1, 0, knockback.zCoord * -1);
                    Vec3 lookAt = mc.thePlayer.getPositionVector().addVector(knockback.xCoord, 0, knockback.zCoord);
                    float currentPitch = Client.Instance.getRotationManager().lastServerRotation.getY();
                    Rotation rotation = RotationUtil.toRotation(lookAt, false,mc.thePlayer);
                    oppositeRotation = rotation;

                    Client.Instance.getRotationManager().setRotation(rotation.toVec2f(), 360, true, false);
                    mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent("Rot"));
                }
            }
        }
    }

    // TODO: To compute Sprint move keys.
    private void computeMovementKeys() {
        float deltaYaw = 0F;
        deltaYaw = MathHelper.wrapAngleTo180_float(oppositeRotation.getYaw() - mc.thePlayer.rotationYaw);
        resetKeys();

        double radian = Math.toRadians(deltaYaw);
        double x = Math.sin(radian);
        double z = Math.cos(radian);
        if (!jumped && !packetOnGround) {
            return;
        }
        if (z > 0.5) {
            mc.gameSettings.keyBindForward.pressed = true;

            mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent("W"));
        } else if (z < -0.5) {
            mc.gameSettings.keyBindBack.pressed = true;

            mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent("S"));
        }
        if (x > 0.5) {
            mc.gameSettings.keyBindRight.pressed = true;

            mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent("D"));
        } else if (x < -0.5) {
            mc.gameSettings.keyBindLeft.pressed = true;

            mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent("A"));
        }
    }
    public static void resetKeys() {
        mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
        mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
        mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
        mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
    }
}
