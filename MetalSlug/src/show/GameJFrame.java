//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package show;

import java.awt.Component;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import controller.GameListener;
import controller.GameThread;
import manager.GameLoad;

public class GameJFrame extends JFrame {
    public static int GameX = 800;
    public static int GameY = 600;
    private JPanel jPanel = null;
//    private KeyListener keyListener = null;
    private MouseMotionListener mouseMotionListener = null;
    private MouseListener mouseListener = null;
    private Thread thead = null;
    
 // 新增：选关面板和游戏面板
    private SelectLevelPanel selectPanel;
    private GameMainJPanel gamePanel;
    private VictoryPanel victoryPanel; // 新增：胜利结算面板
    private DefeatPanel defeatPanel; // 新增：失败面板

    public GameJFrame() {
        this.init();
     // 初始化面板（新增）
        gamePanel = new GameMainJPanel();
        selectPanel = new SelectLevelPanel(this);
        victoryPanel = new VictoryPanel(this); // 初始化胜利面板
        defeatPanel = new DefeatPanel(this); // 初始化失败面板
        System.out.println("胜利面板初始化完成：" + (victoryPanel != null)); // 确保不为null
        // 默认显示选关面板
        this.setContentPane(selectPanel);
    }
    
    // 新增：切换到胜利结算界面
    public void showVictoryPanel() {
    	// 确保在事件调度线程（EDT）中更新UI
        SwingUtilities.invokeLater(() -> {
            this.thead.interrupt(); // 停止游戏线程
            this.setContentPane(victoryPanel); // 切换到胜利面板
            this.setTitle("合金弹头 - 通关成功");
            // 关键：刷新组件树和重绘
            victoryPanel.setFocusable(true);
            victoryPanel.requestFocusInWindow();
            this.revalidate(); // 强制重新计算布局
            this.repaint(); // 强制重绘
            System.out.println("已切换到胜利面板"); // 调试日志
//            gamePanel.removeAll();
//            gamePanel.removeKeyListener(null);
//    	    gamePanel.removeMouseListener(listener);
//    	    gamePanel.removeMouseMotionListener(listener);
        });
    }
    
    // 新增：显示失败界面的方法
    public void showDefeatPanel() {
        SwingUtilities.invokeLater(() -> {
            this.thead.interrupt(); // 停止游戏线程
            this.setContentPane(defeatPanel);
            this.setTitle("合金弹头 - 游戏失败");
            defeatPanel.setFocusable(true);
            defeatPanel.requestFocusInWindow();
            this.revalidate(); // 强制刷新布局
            this.repaint(); // 强制重绘
            System.out.println("已切换到失败面板");
//            gamePanel.removeAll();
        });
    }

    public void init() {
        this.setSize(GameX, GameY);
        this.setTitle("合金弹头");
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo((Component)null);
    }
    
    // 新增：启动游戏（从选关面板切换到游戏）
    public void startGame() {
    	 this.jPanel = gamePanel;
    	    this.setContentPane(gamePanel);
    	    this.setTitle("合金弹头 - 游戏中");

    	    // 创建监听器
    	    GameListener listener = new GameListener();
    	    // 关键：将监听器添加到游戏面板（而非JFrame）
    	    if (gamePanel.getKeyListeners().length == 0) {
    	        gamePanel.addKeyListener(listener);
    	    }
    	    if (gamePanel.getMouseListeners().length == 0) {
    	    	gamePanel.addMouseListener(listener);
    	    }
    	    if (gamePanel.getMouseMotionListeners().length == 0) {
    	    	gamePanel.addMouseMotionListener(listener);
    	    }
//    	    gamePanel.addKeyListener(listener);
//    	    gamePanel.addMouseListener(listener);
//    	    gamePanel.addMouseMotionListener(listener);

    	    // 启动游戏线程
    	    this.setThead(new GameThread(this));

    	    // 强制请求焦点
    	    gamePanel.requestFocusInWindow();

    	    this.start();
    }

    // 新增：返回选关面板（主角死亡时调用）
    public void backToSelect() {
        this.thead.interrupt(); // 停止当前游戏线程
        this.setContentPane(selectPanel);
        this.setTitle("合金弹头 - 选关");
     // 选关面板获取焦点
        selectPanel.setFocusable(true);
        selectPanel.requestFocusInWindow();
        this.repaint();
    }

    public void addButton() {
    }

    public void start() {
    	// 移除：this.add(jPanel); // 避免重复添加面板

//        if (this.keyListener != null) {
//            this.addKeyListener(this.keyListener);
//        }
        if (this.mouseListener != null) {
			this.addMouseListener(mouseListener);
		}
        if (this.mouseMotionListener != null) {
			this.addMouseMotionListener(mouseMotionListener);
		}

        if (this.thead != null) {
            this.thead.start();
        }

        this.setVisible(true);
        if (this.jPanel instanceof Runnable) {
            Runnable run = (Runnable)this.jPanel;
            Thread th = new Thread(run);
            th.start();
            System.out.println("是否启动");
        }

    }

    public void setjPanel(JPanel jPanel) {
        this.jPanel = jPanel;
    }

//    public void setKeyListener(KeyListener keyListener) {
//        this.keyListener = keyListener;
//    }

    public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
    }

    public void setMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    public void setThead(Thread thead) {
        this.thead = thead;
    }
    
    // 新增：公开方法，用于设置选关面板的焦点
    public void setSelectPanelFocus() {
        if (selectPanel != null) {
            selectPanel.setFocusable(true);
            selectPanel.requestFocusInWindow();
            System.out.println("选关面板已获取焦点");
        }
    }
}
