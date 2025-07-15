//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package game;

import java.awt.Image;

import javax.swing.ImageIcon;

import controller.GameListener;
import controller.GameThread;
import show.GameJFrame;
import show.GameMainJPanel;

public class GameStart {
    public GameStart() {
    }

    public static void main(String[] args) {
    	// 仅初始化主窗口（默认显示选关面板）
        new GameJFrame().setVisible(true);
    }
}
