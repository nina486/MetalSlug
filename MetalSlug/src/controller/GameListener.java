//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import element.ElementObj;
import manager.ElementManager;
import manager.GameElement;

public class GameListener implements KeyListener,MouseListener,MouseMotionListener {
    private ElementManager em = ElementManager.getManager();
    private Set<Integer> set = new HashSet();

    public GameListener() {
    }

    public void keyTyped(KeyEvent e) {
    }
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (!this.set.contains(key)) {
            this.set.add(key);
            // 1. 单独处理主角（Play）的键盘事件
            List<ElementObj> playList = this.em.getElementsByKey(GameElement.PLAY);
            // 打印主角列表数量，确认主角已加载
            System.out.println("主角列表数量：" + playList.size());
            for (ElementObj playObj : playList) {
                // 打印事件传递日志
                System.out.println("传递按键（" + key + "）到主角");
                playObj.keyClick(true, key); // 调用主角的keyClick
            }
            // 2. 单独处理地图（MapObj）的键盘事件
            List<ElementObj> mapList = this.em.getElementsByKey(GameElement.MAPS);
            for (ElementObj mapObj : mapList) {
                mapObj.keyClick(true, key); // 调用地图的keyClick
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (this.set.contains(e.getKeyCode())) {
            this.set.remove(e.getKeyCode());
            // 1. 处理主角的释放事件
            List<ElementObj> playList = this.em.getElementsByKey(GameElement.PLAY);
            for (ElementObj playObj : playList) {
                playObj.keyClick(false, e.getKeyCode());
            }
            // 2. 处理地图的释放事件
            List<ElementObj> mapList = this.em.getElementsByKey(GameElement.MAPS);
            for (ElementObj mapObj : mapList) {
                mapObj.keyClick(false, e.getKeyCode());
            }
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("x:"+e.getX());
		System.out.println("Y:"+e.getY());
		 List<ElementObj> play = this.em.getElementsByKey(GameElement.PLAY);
         Iterator var4 = play.iterator();
         while (var4.hasNext()) {
			ElementObj obj= (ElementObj)var4.next();
			obj.mouseClick(true,e.getX(),e.getY());
			
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		List<ElementObj> play = this.em.getElementsByKey(GameElement.PLAY);
        Iterator var4 = play.iterator();
        while (var4.hasNext()) {
			ElementObj obj= (ElementObj)var4.next();
			obj.mouseClick(false,e.getX(),e.getY());
			
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		
	}
}
