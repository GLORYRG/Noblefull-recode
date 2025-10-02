package cn.noblefull.module.impl.visuals;


import cn.noblefull.event.impl.events.render.RenderNameTagEvent;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import cn.noblefull.Client;
import cn.noblefull.event.annotations.EventTarget;
import cn.noblefull.event.impl.events.misc.WorldLoadEvent;
import cn.noblefull.event.impl.events.render.Render2DEvent;
import cn.noblefull.event.impl.events.render.Render3DEvent;
import cn.noblefull.event.impl.events.render.Shader2DEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.fontrender.FontManager;
import cn.noblefull.utils.math.MathUtils;
import cn.noblefull.utils.render.GLUtil;
import cn.noblefull.utils.render.RenderUtil;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.value.impl.BoolValue;
import cn.noblefull.value.impl.ColorValue;
import cn.noblefull.value.impl.NumberValue;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * @Author：GLORY
 * @Date：2025/7/3 12:31
 */

public final class ESP extends Module {
    public ESP() {
        super("ESP",Category.Visuals);
    }
    public static final BoolValue fontTags = new BoolValue("TagsName", true);
    public static final NumberValue scale = new NumberValue("TagsScale", 1,0.1,2,0.1);
    // ==================== 新增的设置项 ====================
    public static final NumberValue minScale = new NumberValue("MinScale", fontTags::get, 0.4, 0.1, 1.0, 0.05);
    // =======================================================
    public static final BoolValue fonttagsBackground = new BoolValue("TagsBackground", fontTags::get, true);
    public static final BoolValue fonttagsHealth = new BoolValue("TagsHealth", fontTags::get, true);
    public static final BoolValue esp2d = new BoolValue("2DESP", true);
    public static final BoolValue box = new BoolValue("Box", esp2d::get, true);
    public static final BoolValue boxSyncColor = new BoolValue("BoxSyncColor", () -> esp2d.get() && box.get(), false);
    public static final ColorValue boxColor = new ColorValue("BoxColor", () -> esp2d.get() && box.get() && !boxSyncColor.get(), Color.RED);
    public static final BoolValue healthBar = new BoolValue("Health", esp2d::get, true);
    public static final BoolValue healthBarSyncColor = new BoolValue("HealthColor", () -> esp2d.get() && healthBar.get(),false);
    public static final ColorValue absorptionColor = new ColorValue("AbsorptionColor", () -> esp2d.get() && healthBar.get() && !healthBarSyncColor.get(), new Color(255, 255, 50));
    public static final BoolValue armorBar = new BoolValue("Armor", esp2d::get,true);
    public static final ColorValue armorBarColor = new ColorValue("ArmorColor", () -> esp2d.get() && armorBar.get(), new Color(50, 255, 255));
    public final Map<EntityPlayer, float[][]> playerRotationMap = new HashMap<>();
    private final Map<EntityPlayer, float[]> entityPosMap = new HashMap<>();

    @Override
    public void onDisable() {
        entityPosMap.clear();
        playerRotationMap.clear();
    }

    @EventTarget
    public void onWorld(WorldLoadEvent event) {
        entityPosMap.clear();
        playerRotationMap.clear();
    }

    @EventTarget
    public void onRenderNametag(RenderNameTagEvent event) {
        if (this.isEnabled() && fontTags.get()) {
            event.setCancelled(true);
        }
    }

    public static String getPing(EntityPlayer entityPlayer) {
        int latency = 0;
        if (!mc.isSingleplayer()) {
            NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(entityPlayer.getUniqueID());
            if (info != null) latency = info.getResponseTime();

            if (isOnHypixel() && latency == 1) {
                int temp = Client.INSTANCE.getPingerUtils().getServerPing().intValue();
                if (temp != -1) {
                    latency = temp;
                }
            }
        } else {
            return "0";
        }

        return latency == 0 ? "?" : String.valueOf(latency);
    }
    public static boolean isOnHypixel() {
        if (mc.isSingleplayer() || mc.getCurrentServerData() == null || mc.getCurrentServerData().serverIP == null)
            return false;
        String ip = mc.getCurrentServerData().serverIP.toLowerCase();
        if (ip.contains("hypixel")) {
            if (mc.thePlayer == null) return true;
            String brand = mc.thePlayer.getClientBrand();
            return brand != null && brand.startsWith("Hypixel BungeeCord");
        }
        return false;
    }

    @EventTarget
    public void onShader2D(Shader2DEvent event){
        // 渲染逻辑已统一到 onRender2D
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        for (EntityPlayer player : entityPosMap.keySet()) {
            if ((player.getDistanceToEntity(mc.thePlayer) < 1.0F && mc.gameSettings.thirdPersonView == 0) ||
                    !RenderUtil.isInViewFrustum(player))
                continue;
            final float[] positions = entityPosMap.get(player);
            final float x = positions[0];
            final float y = positions[1];
            final float x2 = positions[2];
            final float y2 = positions[3];

            if (fontTags.get()) {
                glPushMatrix();

                final float distance = player.getDistanceToEntity(mc.thePlayer);
                float finalScale = scale.get().floatValue() / Math.max(1.0f, distance / 8.0f);

                // ==================== 应用了新的设置项 ====================
                // 使用 Math.max 确保缩放值不会低于你在 "MinScale" 中设置的值
                finalScale = Math.max(finalScale, minScale.get().floatValue());
                // ========================================================

                final float xDif = x2 - x;
                final float middle = x + (xDif / 2);

                glTranslatef(middle, y, 0);
                glScalef(finalScale, finalScale, 1.0f);
                glTranslatef(-middle, -y, 0);

                final String name = player.getDisplayName().getFormattedText() + " - " + "§c" + (int)player.getHealth() + "HP" + " - " + "§a" + getPing(player) + "ms";
                float halfWidth = (FontManager.Bold.get(20).getStringWidth(name) + 8) / 2.0f;
                final float textHeight = 15;
                float renderY = y - textHeight - 2;

                final float left = middle - halfWidth;
                final float left2 = middle - halfWidth + FontManager.Icon.get(35).getStringWidth("SBSBS") / 2;

                if (fonttagsBackground.get()) {
                    RoundedUtil.drawRound(left, renderY - 6, halfWidth * 2, textHeight + 1,5, new Color(0, 0, 0, 190));
                }
                RenderUtil.renderItemStack(player,left2,renderY - 25,1,false,0,false,false);
                FontManager.Bold.get(20).drawString(name, left + 4, renderY - 2f, -1);

                glPopMatrix();
            }

            if (esp2d.get()) {
                glDisable(GL_TEXTURE_2D);
                GLUtil.startBlend();

                if (armorBar.get()) {
                    final float armorPercentage = player.getTotalArmorValue() / 20.0F;
                    final float armorBarWidth = (x2 - x) * armorPercentage;

                    glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);
                    glBegin(GL_QUADS);
                    glVertex2f(x, y2 + 0.5F); glVertex2f(x, y2 + 2.5F); glVertex2f(x2, y2 + 2.5F); glVertex2f(x2, y2 + 0.5F);
                    glEnd();

                    if (armorPercentage > 0) {
                        color(armorBarColor.get().getRGB());
                        glBegin(GL_QUADS);
                        glVertex2f(x + 0.5F, y2 + 1); glVertex2f(x + 0.5F, y2 + 2); glVertex2f(x + armorBarWidth - 0.5F, y2 + 2); glVertex2f(x + armorBarWidth - 0.5F, y2 + 1);
                        glEnd();
                        resetColor();
                    }
                }

                if (healthBar.get()) {
                    float healthBarLeft = x - 2.5F;
                    float healthBarRight = x - 0.5F;

                    glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);
                    glBegin(GL_QUADS);
                    glVertex2f(healthBarLeft, y); glVertex2f(healthBarLeft, y2); glVertex2f(healthBarRight, y2); glVertex2f(healthBarRight, y);
                    glEnd();

                    healthBarLeft += 0.5F;
                    healthBarRight -= 0.5F;

                    final float healthPercentage = player.getHealth() / player.getMaxHealth();
                    final float heightDif = y - y2;
                    final float healthBarHeight = heightDif * healthPercentage;
                    final float topOfHealthBar = y2 + 0.5F + healthBarHeight;

                    int healthColor = healthBarSyncColor.get() ? Client.Instance.getModuleManager().getModule(InterFace.class).color(0).getRGB() : ColorUtil.getColorFromPercentage(healthPercentage);
                    color(healthColor);
                    glBegin(GL_QUADS);
                    glVertex2f(healthBarLeft, topOfHealthBar); glVertex2f(healthBarLeft, y2 - 0.5F); glVertex2f(healthBarRight, y2 - 0.5F); glVertex2f(healthBarRight, topOfHealthBar);
                    glEnd();
                    resetColor();

                    final float absorption = player.getAbsorptionAmount();
                    if (absorption > 0) {
                        final float absorptionPercentage = Math.min(1.0F, absorption / 20.0F);
                        int absorptionColorValue = healthBarSyncColor.get() ? Client.Instance.getModuleManager().getModule(InterFace.class).color(1).getRGB() : this.absorptionColor.get().getRGB();
                        final float absorptionHeight = heightDif * absorptionPercentage;
                        final float topOfAbsorptionBar = y2 + 0.5F + absorptionHeight;
                        color(absorptionColorValue);
                        glBegin(GL_QUADS);
                        glVertex2f(healthBarLeft, topOfAbsorptionBar); glVertex2f(healthBarLeft, y2 - 0.5F); glVertex2f(healthBarRight, y2 - 0.5F); glVertex2f(healthBarRight, topOfAbsorptionBar);
                        glEnd();
                        resetColor();
                    }
                }

                if (box.get()) {
                    glColor4ub((byte) 0, (byte) 0, (byte) 0, (byte) 0x96);
                    glBegin(GL_QUADS);
                    glVertex2f(x, y); glVertex2f(x, y2); glVertex2f(x + 1.5F, y2); glVertex2f(x + 1.5F, y);
                    glVertex2f(x2 - 1.5F, y); glVertex2f(x2 - 1.5F, y2); glVertex2f(x2, y2); glVertex2f(x2, y);
                    glVertex2f(x + 1.5F, y); glVertex2f(x + 1.5F, y + 1.5F); glVertex2f(x2 - 1.5F, y + 1.5F); glVertex2f(x2 - 1.5F, y);
                    glVertex2f(x + 1.5F, y2 - 1.5F); glVertex2f(x + 1.5F, y2); glVertex2f(x2 - 1.5F, y2); glVertex2f(x2 - 1.5F, y2 - 1.5F);
                    glEnd();

                    int boxCol = boxSyncColor.get() ? Client.Instance.getModuleManager().getModule(InterFace.class).color(7).getRGB() : boxColor.get().getRGB();
                    color(boxCol);
                    glBegin(GL_QUADS);
                    glVertex2f(x + 0.5F, y + 0.5F); glVertex2f(x + 0.5F, y2 - 0.5F); glVertex2f(x + 1, y2 - 0.5F); glVertex2f(x + 1, y + 0.5F);
                    glVertex2f(x2 - 1, y + 0.5F); glVertex2f(x2 - 1, y2 - 0.5F); glVertex2f(x2 - 0.5F, y2 - 0.5F); glVertex2f(x2 - 0.5F, y + 0.5F);
                    glVertex2f(x + 0.5F, y + 0.5F); glVertex2f(x + 0.5F, y + 1); glVertex2f(x2 - 0.5F, y + 1); glVertex2f(x2 - 0.5F, y + 0.5F);
                    glVertex2f(x + 0.5F, y2 - 1); glVertex2f(x + 0.5F, y2 - 0.5F); glVertex2f(x2 - 0.5F, y2 - 0.5F); glVertex2f(x2 - 0.5F, y2 - 1);
                    glEnd();
                    resetColor();
                }

                glEnable(GL_TEXTURE_2D);
                GLUtil.endBlend();
            }
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        final boolean project2D = esp2d.get();
        if (project2D && !entityPosMap.isEmpty())
            entityPosMap.clear();

        final float partialTicks = event.partialTicks();

        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (isHypixelSpecialEntity(player)) {
                continue;
            }
            if (project2D) {

                final double posX = (MathUtils.interpolate(player.prevPosX, player.posX, partialTicks) -
                        mc.getRenderManager().viewerPosX);
                final double posY = (MathUtils.interpolate(player.prevPosY, player.posY, partialTicks) -
                        mc.getRenderManager().viewerPosY);
                final double posZ = (MathUtils.interpolate(player.prevPosZ, player.posZ, partialTicks) -
                        mc.getRenderManager().viewerPosZ);

                final double halfWidth = player.width / 2.0D;
                final AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth,
                        posX + halfWidth, posY + player.height + (player.isSneaking() ? -0.2D : 0.1D), posZ + halfWidth).expand(0.1, 0.1, 0.1);

                final double[][] vectors = {{bb.minX, bb.minY, bb.minZ},
                        {bb.minX, bb.maxY, bb.minZ},
                        {bb.minX, bb.maxY, bb.maxZ},
                        {bb.minX, bb.minY, bb.maxZ},
                        {bb.maxX, bb.minY, bb.minZ},
                        {bb.maxX, bb.maxY, bb.minZ},
                        {bb.maxX, bb.maxY, bb.maxZ},
                        {bb.maxX, bb.minY, bb.maxZ}};

                float[] projection;
                final float[] position = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F};

                for (final double[] vec : vectors) {
                    projection = GLUtil.project2D((float) vec[0], (float) vec[1], (float) vec[2], event.scaledResolution().getScaleFactor());
                    if (projection != null && projection[2] >= 0.0F && projection[2] < 1.0F) {
                        final float pX = projection[0];
                        final float pY = projection[1];
                        position[0] = Math.min(position[0], pX);
                        position[1] = Math.min(position[1], pY);
                        position[2] = Math.max(position[2], pX);
                        position[3] = Math.max(position[3], pY);
                    }
                }

                entityPosMap.put(player, position);
            }
        }
    }

    private boolean isHypixelSpecialEntity(EntityPlayer player) {
        if (mc.getCurrentServerData() == null || !mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
            return false;
        }
        if (player.getDisplayName().getUnformattedText().startsWith("[NPC] ") ||
                player.getDisplayName().getUnformattedText().startsWith("NPC ")) {
            return true;
        }
        if (player.getDisplayName().getUnformattedText().toLowerCase().contains("helper") ||
                player.getDisplayName().getUnformattedText().toLowerCase().contains("mod") ||
                player.getDisplayName().getUnformattedText().toLowerCase().contains("admin")) {
            return true;
        }
        if (player.getDisplayName().getUnformattedText().startsWith("BOT ") ||
                player.getDisplayName().getUnformattedText().endsWith(" BOT") ||
                player.getDisplayName().getUnformattedText().contains("Robot")) {
            return true;
        }
        if (player.getScore() == -9999 || player.getScore() == 0) {
            return true;
        }
        if (player.posX == player.prevPosX && player.posZ == player.prevPosZ &&
                player.rotationYaw == player.prevRotationYaw) {
            return true;
        }

        return false;
    }
    public boolean isValid(Entity entity) {
        if (entity instanceof EntityPlayer player) {
            if (!player.isEntityAlive()) {
                return false;
            }
            return RenderUtil.isBBInFrustum(entity.getEntityBoundingBox()) && mc.theWorld.playerEntities.contains(player);
        }

        return false;
    }
    public void addEntity(EntityPlayer e, ModelPlayer model) {
        playerRotationMap.put(e, new float[][]{
                {model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ},
                {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ},
                {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ},
                {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ},
                {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}
        });
    }
    public static void resetColor() {
        color(1, 1, 1, 1);
    }
    public static void color(double red, double green, double blue, double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void color(int color) {
        glColor4ub(
                (byte) (color >> 16 & 0xFF),
                (byte) (color >> 8 & 0xFF),
                (byte) (color & 0xFF),
                (byte) (color >> 24 & 0xFF));
    }

}
