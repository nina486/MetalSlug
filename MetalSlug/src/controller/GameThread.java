//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package controller;

import java.io.File;
import java.io.IOException;
import java.security.Identity;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingUtilities;

import element.ArtilleryBullet;
import element.Boos;
import element.Die;
import element.ElementObj;
import element.Enemy;
import element.EnemyFile;
import element.GreenTaitan;
import element.Hostage;
import element.MapObj;
import element.PlaneFile;
import element.Play;
import element.PlayFile;
import element.Tool;
import manager.ElementManager;
import manager.GameElement;
import manager.GameLoad;
import show.GameJFrame;

public class GameThread extends Thread {
    private ElementManager em = ElementManager.getManager();
    private GameJFrame mainFrame; // 新增：主窗口引用
    private Clip bgmClip; // 全局BGM播放器对象

    // 新增：接收主窗口的构造方法
    public GameThread(GameJFrame frame) {
        this.mainFrame = frame;
    }
    
    // 新增：停止BGM播放的方法
    private void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop(); // 停止播放
            bgmClip.close(); // 释放资源
        }
    }


    public void run() {
        
    	try {
            this.gameLoad();
            this.gameRun();
            this.gameOver();
            sleep(50L); // 可能被中断的休眠
        } catch (InterruptedException var2) {
            // 捕获线程中断异常，不打印堆栈（这是预期的终止行为）
            System.out.println("游戏线程已正常终止");
        }
        
    }
    public static int index=0;
    private void gameLoad() {
    	// 加载对应关卡（index+1对应A=1,B=2,C=3）
    	GameLoad.MapLoad(++index);
        GameLoad.loadImg();
        GameLoad.loadPlay();
        //GameLoad.loadBoss2();
//        GameLoad.loadBoos();
//        GameLoad.loadEnemy();
    }

    private void gameRun() {
    	try {
			File fireFile = new File("music/bgm.wav");
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fireFile);
			bgmClip = AudioSystem.getClip();
			bgmClip.open(audioInputStream);
			bgmClip.start();
			System.out.println("BGM开始播放");
			//Thread.sleep(clip.getMicrosecondLength()/1000);
		} catch (UnsupportedAudioFileException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} 
        long gameTime = 0L;
        while(true) {
            Map<GameElement, List<ElementObj>> all = this.em.getGameElements();
            List<ElementObj> enemys = this.em.getElementsByKey(GameElement.ENEMY);
            List<ElementObj> files = this.em.getElementsByKey(GameElement.PLAYFILE);
            List<ElementObj> enemyFiles = this.em.getElementsByKey(GameElement.ENEMYFILE); // 敌人子弹（
            List<ElementObj> maps = this.em.getElementsByKey(GameElement.MAPS);
            List<ElementObj> plays = this.em.getElementsByKey(GameElement.PLAY);
            List<ElementObj> hostages = this.em.getElementsByKey(GameElement.HOSTAGE);
            List<ElementObj> boosList = this.em.getElementsByKey(GameElement.BOSS);
            List<ElementObj> planefiledie = this.em.getElementsByKey(GameElement.DIE);
            List<ElementObj> tool = this.em.getElementsByKey(GameElement.TOOL);
            this.moveAndUpdate(all, gameTime);
            this.ElementPK(enemys, files);
            this.ElementPK(boosList, files);
            this.ElementPK(hostages, plays);
            this.ElementPK(boosList, plays);
            this.ElementPK(enemyFiles, plays); // 敌人子弹 vs 主角
            this.ElementPK(planefiledie, plays);
            this.ElementPK(tool, plays);
            // 新增：主角死亡时返回选关面板
            if (plays.isEmpty()) {
            	 stopBGM(); // 停止BGM
            	 SwingUtilities.invokeLater(() -> {
                     mainFrame.showDefeatPanel(); // 显示失败界面
                 });
                 break;
            }
            
            // 2. 通关条件：所有敌人和Boss被消灭，且地图已停止移动
            boolean allEnemiesDead = enemys.isEmpty() && boosList.isEmpty();
            boolean mapStopped = !MapObj.isMapmove();
            if (allEnemiesDead && mapStopped) {
            	stopBGM(); // 停止BGM
                // 切换到胜利结算界面（在UI线程中执行）
                SwingUtilities.invokeLater(() -> mainFrame.showVictoryPanel());
                break;
            }
            
            // 新增：更新敌人状态
            updateEnemyStates(enemys, gameTime);
            if(this.em.getElementsByKey(GameElement.ENEMY).isEmpty() && 
            		this.em.getElementsByKey(GameElement.BOSS).isEmpty() &&
            		!MapObj.isMapmove()) {
            	break;
            }
            if(this.em.getElementsByKey(GameElement.PLAY).isEmpty()) {index--;break;}
            //this.ElementPK(files, maps);
            ++gameTime;

            try {
                sleep(10L);
            } catch (InterruptedException var8) {
                var8.printStackTrace();
            }
        }
    }

//    public void ElementPK(List<ElementObj> listA, List<ElementObj> listB) {
//        for(int i = 0; i < listA.size(); ++i) {
//            ElementObj enemy = (ElementObj)listA.get(i);
//
//            for(int j = 0; j < listB.size(); ++j) {
//                ElementObj file = (ElementObj)listB.get(j);
//                if (enemy.pk(file)) {
//                    System.out.println(listB);
//                    enemy.setLive(false);
//                    file.setLive(false);
//                    break;
//                }
//            }
//        }
//
//    }
    public void ElementPK(List<ElementObj> listA, List<ElementObj> listB) {
        for(int i = 0; i < listA.size(); ++i) {
            ElementObj enemy = listA.get(i);
            // 只处理Enemy类型（避免误伤其他元素）
            if (enemy instanceof Hostage) {//人质碰撞角色
            	Hostage host = (Hostage) enemy;
            	if(!host.isRescued()){
	            	for(int j=0;j<listB.size();j++) {
	    				ElementObj file = listB.get(j);
	    				if(enemy.pk(file)) {
	    					host.setRescued(true);
	    					break;
	    				}
	    			}
            	}
            }
            else if (enemy instanceof Tool) {//道具碰撞角色
            	Tool tool = (Tool) enemy;
	            for(int j=0;j<listB.size();j++) {
	            	Play file = (Play)listB.get(j);
	    			if(tool.pk(file)) {
	    				file.setHp(file.getHp()+ 100);
	    				tool.setLive(false);
	    				break;
	    			}
            	}
            }
            else if (enemy instanceof GreenTaitan) {
            	GreenTaitan greenTaitan=(GreenTaitan) enemy;
				for (int j = 0; j < listB.size(); j++) {
					if (listB.get(j) instanceof Play) {
						Play playElementObj = (Play) listB.get(j);
						if (greenTaitan.pk(playElementObj)) {
							if (playElementObj.getHp()>0) {
								greenTaitan.setPktype(false);
								playElementObj.setHp(playElementObj.getHp()- 50);
							}else {
								playElementObj.setLive(false);
							}
						}
					}
					else if (listB.get(j) instanceof PlayFile)  {
						PlayFile playFile = (PlayFile) listB.get(j);
						if (greenTaitan.pk(playFile)) {
							playFile.setLive(false);
							playFile.die();
							if(Play.getWeaponType()=="pistol")
							greenTaitan.setHp(greenTaitan.getHp()-10);
							else greenTaitan.setHp(greenTaitan.getHp()-20);
							if (greenTaitan.getHp()<=0) {
								greenTaitan.setLive(false);
							}
						}
					}
					
				}
			}
            else if (enemy instanceof Die) {
            	Die planefile = (Die) enemy;
            	if(planefile.isPkType()){
	            	for(int j=0;j<listB.size();j++) {
	    				Play file = (Play)listB.get(j);
	    				if(planefile.pk(file)) {
	    					if (file.getHp()>0) {
	    						planefile.setPkType(false);
								file.setHp(file.getHp()- 50);
								break;
							}else {
								file.setLive(false);
								break;
							}
	    				}
	    			}
            	}
            }
            else if (enemy instanceof Boos) {
            	Boos Boos=(Boos) enemy;
				for (int j = 0; j < listB.size(); j++) {
					if (listB.get(j) instanceof Play) {
						Play playElementObj = (Play) listB.get(j);
						if (Boos.pk(playElementObj)) {
							if (playElementObj.getHp()>0) {
								Boos.setPktype(false);
								playElementObj.setHp(playElementObj.getHp()- 30);
							}else {
								playElementObj.setLive(false);
							}
						}
					}
					else if (listB.get(j) instanceof PlayFile) {
						PlayFile playFile = (PlayFile) listB.get(j);
						if (Boos.pk(playFile)) {
							playFile.setLive(false);
							playFile.die();
							if(Play.getWeaponType()=="pistol")
								Boos.setHp(Boos.getHp()-10);
							else Boos.setHp(Boos.getHp()-20);
							if (Boos.getHp()<=0) {
								Boos.setLive(false);
							}
						}
					}
				}
			}  
         // 炮兵子弹与主角的碰撞逻辑（特殊处理）
            else if (enemy instanceof ArtilleryBullet) { 
                ArtilleryBullet bullet = (ArtilleryBullet) enemy; 
                for(int j = 0; j < listB.size(); j++) {
                    ElementObj player = listB.get(j);
                    if (player instanceof Play && bullet.pk(player)) { // pk返回true（碰撞成功）
                        // 此时isPked仍为false，可进入扣血逻辑
                        Play play = (Play) player;
                        if (play.getHp() > 0) {
                            play.setHp(play.getHp() - 20); // 执行扣血
                        } else {
                            play.setLive(false);
                        }
                        bullet.setPked(true); // 扣血后再标记为已碰撞，避免重复扣血
                        break; 
                    }
                }
            }
         // 3. 敌人子弹与主角的碰撞（新增逻辑，核心修改）
            else if (enemy instanceof EnemyFile) { // elementA是敌人子弹
                EnemyFile enemyBullet = (EnemyFile) enemy; // 明确转换为敌人子弹
                for(int j = 0; j < listB.size(); j++) {
                    ElementObj player = listB.get(j);
                    // 检测：碰撞对象是主角（Play），且发生碰撞
                    if (player instanceof Play && enemyBullet.pk(player)) {
                        enemyBullet.setLive(false); // 敌人子弹消失d
                         if (player instanceof Play) {
                             Play play = (Play) player;
                             if (play.getHp()>0) {
                            	 play.setHp(play.getHp() - 10);// 假设扣10血
							}else {
								play.setLive(false);
							}
                              
                         }
                        
                        break; // 一个子弹只碰撞一次
                    }
                }
            }
            else if(enemy instanceof Enemy){//子弹碰撞敌人
	            Enemy enemyWithHp = (Enemy) enemy; // 强转为Enemy，获取hp
	            for(int j = 0; j < listB.size(); ++j) {
	                ElementObj bullet = listB.get(j);
	                if (enemy.pk(bullet)) { // 检测到碰撞
	                    // 1. 子弹消失
	                    bullet.setLive(false);
	                    if(Play.getWeaponType()=="pistol")
	                    // 2. 敌人扣除生命值（假设一发子弹伤害为20）
	                    enemyWithHp.setHp(enemyWithHp.getHp() - 20);
	                    else enemyWithHp.setHp(enemyWithHp.getHp() - 40);
	                    System.out.println("敌人剩余血量：" + enemyWithHp.getHp());
	                    
	                    // 3. 只有血量<=0时，敌人才死亡
	                    if (enemyWithHp.getHp() <= 0) {
	                        enemy.setLive(false);
	                        System.out.println("敌人被击败！");
	                    }
	                    break; // 一个子弹只攻击一个敌人
	                    
	                }
	            }
            }
            else {
            	for(int j=0;j<listB.size();j++) {
    				ElementObj file = listB.get(j);
    				if(enemy.pk(file)) {
    					enemy.setLive(false);
    					file.setLive(false);
    					break;
    				}
    			}
            }
        }
    }

    public void moveAndUpdate(Map<GameElement, List<ElementObj>> all, long gameTime) {
        GameElement[] var7;
        int var6 = (var7 = GameElement.values()).length;

        for(int var5 = 0; var5 < var6; ++var5) {
            GameElement ge = var7[var5];
            List<ElementObj> list = (List)all.get(ge);

            for(int i = list.size() - 1; i >= 0; --i) {
                ElementObj obj = (ElementObj)list.get(i);
                if (!obj.isLive()) {
                    obj.die();
                    list.remove(i);
                } else {
                    obj.model(gameTime);
                }
            }
        }

    }
    
 // 新增：更新敌人状态的方法
    private void updateEnemyStates(List<ElementObj> enemys, long gameTime) {
        for (ElementObj enemy : enemys) {
            if (enemy instanceof Enemy) {
                ((Enemy) enemy).updateState(gameTime);
            }
        }
    }

    private void gameOver() {
    	Map<GameElement, List<ElementObj>> all = this.em.getGameElements();
        for (GameElement ge : GameElement.values()) {
            List<ElementObj> list = all.get(ge);
            if (list != null) { 
                list.clear(); 
            }
        }
    }

// // 新增：将关卡编号（0,1,2...）转换为字母（A,B,C...）
//    public static String getLevelLetter() {
//        switch (index) {
//            case 1: return "A";
//            case 2: return "B";
//            case 3: return "C";
//            // 可扩展更多关卡
//            default: return String.valueOf(index);
//        }
//    }
}
