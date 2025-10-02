package cn.noblefull.module;



/**
 * @Author：GLORY
 * @Date：2025/6/29 00:50
 */

public enum Category {
    Combat("K"),
    Movement("O"),
    Misc("Q"),
    Player("N"),
    World("D"),
    Visuals("P"),
    Display("M");
    public String icon;
    Category(String icon){
        this.icon = icon;
    }
}
