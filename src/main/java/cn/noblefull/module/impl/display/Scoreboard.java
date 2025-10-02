package cn.noblefull.module.impl.display;

import cn.noblefull.Client;
import cn.noblefull.event.impl.events.render.Shader2DEvent;
import cn.noblefull.module.Category;
import cn.noblefull.module.ModuleWidget;
import cn.noblefull.module.impl.visuals.InterFace;
import cn.noblefull.utils.color.ColorUtil;
import cn.noblefull.utils.fontrender.FontManager;
import cn.noblefull.utils.render.RoundedUtil;
import cn.noblefull.utils.render.shader.ShaderElement;
import cn.noblefull.value.impl.BoolValue;
import cn.noblefull.value.impl.ModeValue;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * @Author：Guyuemang
 * @Date：2025/6/7 12:26
 */
public class Scoreboard extends ModuleWidget {
    public Scoreboard() {
        super("Scoreboard", Category.Display);
    }
    public ModeValue modeValue = new ModeValue("Mode", "Normal",new String[]{"Normal"});
    private final BoolValue leftLayout = new BoolValue("Left Layout", true);
    private final BoolValue redNumbers = new BoolValue("Red Numbers", false);
    @Override
    public void onShader(Shader2DEvent event) {
        int x = (int) renderX;
        int y = (int) renderY;
        switch (modeValue.getValue()){
            case "Normal":
                // 【修复】这里的逻辑必须和 render() 方法中的完全一致
                FontManager font = FontManager.Semibold;

                ScoreObjective scoreObjective = getScoreObjective();
                if (scoreObjective == null) {
                    return;
                }

                net.minecraft.scoreboard.Scoreboard scoreboard = scoreObjective.getScoreboard();
                Collection<Score> sortedScores = scoreboard.getSortedScores(scoreObjective);
                List<Score> list = Lists.newArrayList(Iterables.filter(sortedScores, score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")));

                if (list.size() > 15) {
                    sortedScores = Lists.newArrayList(Iterables.skip(list, sortedScores.size() - 15));
                } else {
                    sortedScores = list;
                }

                int maxWidth = 0;
                maxWidth = Math.max(maxWidth, font.get(18).getStringWidth("ScoreBoard"));
                maxWidth = Math.max(maxWidth, font.get(18).getStringWidth(scoreObjective.getDisplayName()));
                maxWidth = Math.max(maxWidth, font.get(18).getStringWidth("Noblefull @1337"));

                for (Score score : sortedScores) {
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName());
                    String scoreText = "" + score.getScorePoints();
                    String combinedLine = playerName + "  " + scoreText;
                    maxWidth = Math.max(maxWidth, font.get(18).getStringWidth(combinedLine));
                }
                maxWidth += 10;

                int lineHeight = 10;
                // 高度计算也需要统一
                int totalHeight = (sortedScores.size() + 3) * lineHeight;

                if (!leftLayout.get()) {
                    x -= maxWidth;
                }

                // 绘制背景，以便在编辑器中看到大小
                RoundedUtil.drawRound(x, y, maxWidth, totalHeight + lineHeight + 15, InterFace.radius.get().floatValue(), new Color(1,1,1,255));

                // 更新模块尺寸
                this.setWidth(maxWidth);
                this.renderY = y;
                this.setHeight(totalHeight + lineHeight + 15);
                break;

        }
    }

    @Override
    public void render() {
        int x = (int) renderX;
        int y = (int) renderY;

        switch (modeValue.getValue()){
            case "Normal":
                FontManager font = FontManager.Semibold;
                ScoreObjective scoreObjective = getScoreObjective();
                if (scoreObjective == null) {
                    return;
                }

                net.minecraft.scoreboard.Scoreboard scoreboard = scoreObjective.getScoreboard();
                Collection<Score> sortedScoresSource = scoreboard.getSortedScores(scoreObjective); // 使用新变量名以示区分
                List<Score> list = Lists.newArrayList(Iterables.filter(sortedScoresSource, score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")));

                List<Score> scoresToRender; // 创建一个最终用于渲染的列表
                if (list.size() > 15) {
                    scoresToRender = Lists.newArrayList(Iterables.skip(list, list.size() - 15));
                } else {
                    scoresToRender = list;
                }

                // --- [核心修复] ---
                // 在渲染前，将列表反转，以确保分数高的在前面
                java.util.Collections.reverse(scoresToRender);
                // --- [修复结束] ---

                int maxWidth = 0;
                maxWidth = Math.max(maxWidth, font.get(18).getStringWidth("ScoreBoard"));
                maxWidth = Math.max(maxWidth, font.get(18).getStringWidth(scoreObjective.getDisplayName()));
                maxWidth = Math.max(maxWidth, font.get(18).getStringWidth("Noblefull @1337"));

                for (Score score : scoresToRender) { // 使用修复后的列表进行遍历
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName());
                    String scoreText = "" + score.getScorePoints();
                    String combinedLine = playerName + "  " + scoreText;
                    maxWidth = Math.max(maxWidth, font.get(18).getStringWidth(combinedLine));
                }

                maxWidth += 10;

                int lineHeight = 10;
                // 高度计算现在基于最终渲染的列表大小
                int totalHeight = (scoresToRender.size() + 3) * lineHeight;

                if (!leftLayout.get()) {
                    x -= maxWidth;
                }

                int xEnd = x + maxWidth;

                RoundedUtil.drawRound(x, y, maxWidth, totalHeight + lineHeight + 15, InterFace.radius.get().floatValue(), new Color(1,1,1,100));

                font.get(18).drawString("ScoreBoard", x + maxWidth / 2 - font.get(18).getStringWidth("ScoreBoard") / 2, y + 5, -1);
                font.get(18).drawString(scoreObjective.getDisplayName(), x + maxWidth / 2 - font.get(18).getStringWidth(scoreObjective.getDisplayName()) / 2, y + 15, -1);

                int index = 0;
                for (Score score : scoresToRender) { // 使用修复后的列表进行渲染
                    ScorePlayerTeam playerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                    String playerName = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName());

                    int y1 = y + 25 + index * lineHeight;
                    index++;

                    font.get(18).drawString(playerName, x + 4, y1, -1);

                    if (redNumbers.get()) {
                        String scorePoint = EnumChatFormatting.RED + "" + score.getScorePoints();
                        font.get(18).drawString(scorePoint, xEnd - font.get(18).getStringWidth(scorePoint) - 4, y1, -1);
                    }
                }

                font.get(18).drawString("Noblefull @1337", x + maxWidth / 2 - font.get(18).getStringWidth("Noblefull @1337") / 2, y + 30 + scoresToRender.size() * lineHeight, Client.Instance.getModuleManager().getModule(InterFace.class).color());

                this.setWidth(maxWidth);
                this.renderY = y;
                this.setHeight(totalHeight + lineHeight + 15);
                break;
        }
    }

    private static ScoreObjective getScoreObjective() {
        net.minecraft.scoreboard.Scoreboard worldScoreboard = mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective = null;
        ScorePlayerTeam scoreplayerteam = worldScoreboard.getPlayersTeam(mc.thePlayer.getName());

        if (scoreplayerteam != null) {
            int colorIndex = scoreplayerteam.getChatFormat().getColorIndex();

            if (colorIndex >= 0) {
                scoreobjective = worldScoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
            }
        }

        return scoreobjective != null ? scoreobjective : worldScoreboard.getObjectiveInDisplaySlot(1);
    }

    @Override
    public boolean shouldRender() {
        return getState() && INTERFACE.getState();
    }
}
