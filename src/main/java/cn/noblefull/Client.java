package cn.noblefull;

import cn.noblefull.gui.clickgui.noblefull.NoblefullClickGui;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import lombok.Setter;
import cn.noblefull.module.ClientApplication;
import org.lwjgl.opengl.Display;
import cn.noblefull.command.CommandManager;
import cn.noblefull.config.ConfigManager;
import cn.noblefull.event.EventManager;
import cn.noblefull.gui.clickgui.dropdown.DropDownClickGui;
import cn.noblefull.gui.notification.NotificationManager;
import cn.noblefull.module.ModuleManager;
import cn.noblefull.utils.Instance;
import cn.noblefull.utils.pack.BlinkComponent;
import cn.noblefull.utils.player.PingerUtils;
import cn.noblefull.utils.player.SelectorDetectionComponent;
import cn.noblefull.utils.player.SlotSpoofComponent;
import cn.noblefull.utils.rotation.RotationManager;

import javax.swing.*;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author：GLORY
 * @Date：2025/6/28 19:42
 */

@Getter
@Setter
public class Client implements Instance {
    public static Client Instance = new Client();
    public static String name = "Noblefull";
    public static String version = "1.1";
    public static String userName;
    private EventManager eventManager;
    private ModuleManager moduleManager;
    private NoblefullClickGui noblefullClickGui;
    private DropDownClickGui dropDownClickGui;
    private ConfigManager configManager;
    private NotificationManager notification;
    private CommandManager commandManager;
    private RotationManager rotationManager;
    private PingerUtils pingerUtils;
    private SelectorDetectionComponent selectorDetectionComponent;
    public static boolean debug = true;
    int startTime;
    
    // 心跳检测相关
    private Timer heartbeatTimer;
    private String userCredentials; // 保存用户凭证用于心跳检测

    // 添加静态NettyClient实例引用
    public static cn.noblefull.utils.NettyClient nettyClientInstance;
    
    // kick用户的方法
    public static void kickUser(String username) {
        if (nettyClientInstance != null) {
            String userIP = nettyClientInstance.getUserIP();
            if (userIP != null && !userIP.isEmpty()) {
                System.out.println("准备踢出用户: " + username + " IP: " + userIP);
                // 这里可以添加实际的kick逻辑
                // 例如发送一个特殊的包到服务器要求踢出指定用户
            } else {
                System.out.println("无法获取用户IP地址");
            }
        } else {
            System.out.println("NettyClient实例未初始化");
        }
    }

    public void Init(){
        startTime = (int) System.currentTimeMillis();
        if (ClientApplication.Hwid){
            System.exit(0);
        }
        if (Client.debug){
            System.exit(0);
        }
        ViaMCP.create();
        ViaMCP.INSTANCE.initAsyncSlider();
        Display.setTitle(name + " " + version + " |  Hello User: " + userName);
        eventManager = new EventManager();
        eventManager.register(this);
        eventManager.register(new BlinkComponent());
        eventManager.register(new SlotSpoofComponent());

        rotationManager = new RotationManager();
        eventManager.register(rotationManager);

        selectorDetectionComponent = new SelectorDetectionComponent();
        eventManager.register(selectorDetectionComponent);

        pingerUtils = new PingerUtils();
        eventManager.register(pingerUtils);

        moduleManager = new ModuleManager();
        moduleManager.Init();

        notification = new NotificationManager();

        configManager = new ConfigManager();
        configManager.loadConfig("config",moduleManager);

        commandManager = new CommandManager(moduleManager);

        noblefullClickGui = new NoblefullClickGui();
        dropDownClickGui = new DropDownClickGui();
        
        // 启动心跳检测
        startHeartbeat();
    }
    
    /**
     * 启动心跳检测任务
     */
    private void startHeartbeat() {
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel();
        }
        
        heartbeatTimer = new Timer("ClientHeartbeat", true);
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendHeartbeat();
            }
        }, 30000, 30000); // 每30秒发送一次心跳
    }
    
    /**
     * 发送心跳包到服务器
     */
    private void sendHeartbeat() {
        // 这里应该实现实际的心跳逻辑
        // 可以通过HTTP请求或Socket连接发送心跳包
        SwingUtilities.invokeLater(() -> {
            System.out.println("Sending heartbeat...");
            // 实际的心跳发送逻辑需要根据您的服务器协议来实现
        });
    }
    
    /**
     * 停止心跳检测
     */
    public void stopHeartbeat() {
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel();
            heartbeatTimer = null;
        }
    }
}