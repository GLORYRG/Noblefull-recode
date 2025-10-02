package cn.noblefull.gui.clickgui.noblefull.panel;

import cn.noblefull.gui.clickgui.IComponent;
import cn.noblefull.gui.clickgui.noblefull.NoblefullClickGui;
import cn.noblefull.gui.clickgui.noblefull.component.ModuleComponent;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.utils.Instance;
import cn.noblefull.utils.animations.Animation;
import cn.noblefull.utils.animations.Direction;
import cn.noblefull.utils.animations.impl.SmoothStepAnimation;
import cn.noblefull.utils.render.RenderUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Mouse;

@Getter
public class CategoryPanel implements IComponent, Instance {
    private float maxScroll = 0;
    private float scroll;
    private float scrollTarget;
    private float scrollStart;
    private final Animation scrollAnimation = new SmoothStepAnimation(250, 0, Direction.FORWARDS);

    private final Category category;
    @Setter
    private boolean selected;
    private final ObjectArrayList<ModuleComponent> moduleComponents = new ObjectArrayList<>();

    public CategoryPanel(Category category) {
        this.category = category;
        for (Module module : INSTANCE.getModuleManager().getAllModules()){
            if (module.getCategory().equals(this.category)){
                moduleComponents.add(new ModuleComponent(module));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (!isSelected()) {
            return;
        }

        NoblefullClickGui gui = INSTANCE.getNoblefullClickGui();

        // 【核心修改】精确计算可滚动区域的 Y 坐标和高度
        final int panelX = gui.getX() + 120;
        // Y坐标从顶部标题栏的下方开始
        final int panelY = (int) (gui.getY() + NoblefullClickGui.TOP_BAR_HEIGHT);
        final int panelWidth = gui.getW() - 120;
        // 高度是总高度减去顶部标题栏和底部信息栏的高度
        final int panelHeight = (int) (gui.getH() - NoblefullClickGui.TOP_BAR_HEIGHT - NoblefullClickGui.BOTTOM_BAR_HEIGHT);

        // 使用这个精确计算的区域进行裁切
        RenderUtil.startGlScissor(panelX, panelY, panelWidth, panelHeight);

        scroll = scrollStart + scrollAnimation.getOutput().floatValue();
        onScroll(panelX, panelY, panelWidth, panelHeight, mouseX, mouseY);

        // 这里的 topPadding 是滚动区域内部的边距，而不是整个面板的留白
        final int topPadding = 10;
        final int horizontalPadding = 10;
        final int verticalPadding = 8;
        final int moduleWidth = panelWidth - (horizontalPadding * 2);

        float currentY = topPadding;

        for (ModuleComponent module : moduleComponents) {
            float componentCurrentHeight = module.getCurrentHeight();

            module.setX(horizontalPadding);
            module.setY(currentY);
            module.setWidth(moduleWidth);

            module.setPanelX(panelX);
            module.setPanelY(panelY);
            module.setScroll((int) scroll);
            module.setPanelHeight(panelHeight);

            module.drawScreen(mouseX, mouseY);

            currentY += componentCurrentHeight + verticalPadding;
        }

        float totalContentHeight = currentY - verticalPadding;
        // 底部留白，防止最后一个模块紧贴底部
        final int bottomPadding = 10;
        this.maxScroll = Math.max(0, totalContentHeight - panelHeight + bottomPadding);

        RenderUtil.stopGlScissor();
    }

    public void onScroll(int panelX, int panelY, int panelWidth, int panelHeight, int mx, int my) {
        if (RenderUtil.isHovering(panelX, panelY, panelWidth, panelHeight, mx, my)) {
            int dWheel = Mouse.getDWheel();
            if (dWheel != 0) {
                if (dWheel > 0) {
                    scrollTarget += 20;
                } else {
                    scrollTarget -= 20;
                }
            }
        }
        scrollTarget = Math.max(-maxScroll, Math.min(0, scrollTarget));
        if (scrollAnimation.isDone() && scroll != scrollTarget) {
            scrollStart = scroll;
            scrollAnimation.setEndPoint(scrollTarget - scrollStart);
            scrollAnimation.reset();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isSelected()) {
            moduleComponents.forEach(moduleComponent -> moduleComponent.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (isSelected()) {
            moduleComponents.forEach(moduleComponent -> moduleComponent.mouseReleased(mouseX, mouseY, state));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (isSelected()) {
            moduleComponents.forEach(moduleComponent -> moduleComponent.keyTyped(typedChar, keyCode));
        }
    }
}
