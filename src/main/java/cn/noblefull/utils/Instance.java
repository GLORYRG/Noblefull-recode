package cn.noblefull.utils;


import cn.noblefull.module.Mine;
import cn.noblefull.Client;
import cn.noblefull.utils.fontrender.FontManager;

/**
 * @Author：GLORY
 * @Date：2025/6/29 00:46
 */

public interface Instance {
    Mine mc = Mine.getMinecraft();
    Client INSTANCE = Client.Instance;
    FontManager Semibold = FontManager.Semibold;
    FontManager Bold = FontManager.Bold;
    FontManager Icon = FontManager.Icon;
    FontManager Light = FontManager.Light;
    FontManager Regular = FontManager.Regular;
}
