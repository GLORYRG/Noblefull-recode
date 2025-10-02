package cn.noblefull.module.impl.visuals;

import cn.noblefull.module.Mine;
import org.lwjgl.input.Keyboard;
import cn.noblefull.Client;
import cn.noblefull.module.Category;
import cn.noblefull.module.Module;
import cn.noblefull.value.impl.ModeValue;

/**
 * @Author：GLORY
 * @Date：2025/6/29 13:27
 */
public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGui",Category.Visuals);
        this.setKey(Keyboard.KEY_RSHIFT);
    }
    public static ModeValue modeValue = new ModeValue("Mode","Noblefull",new String[]{"DropDown","Noblefull"});

    @Override
    public void onEnable() {
        if (!Mine.isPaused){
            switch (modeValue.getValue()) {
                case "DropDown":
                    mc.displayGuiScreen(Client.Instance.getDropDownClickGui());
                    break;
                case "Noblefull":
                    mc.displayGuiScreen(Client.Instance.getNoblefullClickGui());
                    break;
            }
        }
        if (Mine.isPaused) {
            System.exit(0);
        }
        setState(false);
    }
}
