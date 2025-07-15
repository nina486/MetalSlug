//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import element.ElementObj;
import element.MapObj;

public class GameLoad {
    private static ElementManager em = ElementManager.getManager();
    public static Map<String, ImageIcon> imgMap = new HashMap();
    public static Map<String, List<ImageIcon>> imgMaps = new HashMap<String, List<ImageIcon>>();
    private static Properties pro = new Properties();
    private static Map<String, Class<?>> objMap = new HashMap();
    public static Map<Integer, ImageIcon> boosAttackMap = new HashMap<Integer, ImageIcon>();
    public static Map<String, ImageIcon> greenTaitan = new HashMap<String, ImageIcon>();
    public GameLoad() {
    }
    
    public static void MapLoad(int mapId) {
    	MapObj.setMapmove(true);
    	MapObj.setCreatdata(false);
        String mapName = "text/map";
        ClassLoader classLoader = GameLoad.class.getClassLoader();
        InputStream map = classLoader.getResourceAsStream(mapName);
        pro.clear();
        if(map ==null) {
			System.out.println("配置文件读取异常,请重新安装");
			return;
		}
        try {
			pro.load(map);
			String mapUrl=pro.getProperty("map"+mapId);
			ElementObj elementObj=new MapObj().createElement(mapUrl);
			em.addElement(elementObj, GameElement.MAPS);
		} catch (IOException e) {
			e.printStackTrace();
		}
       
    }
    
    public static void loadImg() {
        String texturl = "text/GameData.pro";
        ClassLoader classLoader = GameLoad.class.getClassLoader();
        InputStream texts = classLoader.getResourceAsStream(texturl);
        pro.clear();

        try {
            pro.load(texts);
            Set<Object> set = pro.keySet();
            for (Object o : set) {
                String dirPath = pro.getProperty(o.toString());  
                List<ImageIcon> imageList = new ArrayList<>();
                int index = 0;
                while (true) {
                    String imagePath = dirPath + "/" + index + ".png";
                    File file = new File(imagePath);
                    if(!file.exists())break;
                    ImageIcon icon = new ImageIcon(imagePath);
                    imageList.add(icon);
                    index++;
                }
                imgMaps.put(o.toString(), imageList); 
            }
        } catch (IOException var7) {
            var7.printStackTrace();
        }
        loadBoosImage();
    }

    public static void loadPlay() {
        loadObj();
        String playStr = "100,450,play";
        ElementObj obj = getObj("play");
        ElementObj play = obj.createElement(playStr);
        em.addElement(play, GameElement.PLAY);
    }
    public static void loadHostage() {
        loadObj();
        String hostStr = "400,400,hostage";
        ElementObj obj = getObj("hostage");
        ElementObj play = obj.createElement(hostStr);
        em.addElement(play, GameElement.HOSTAGE);
    }
    public static void loadPlane() {
        loadObj();
        String plaStr = "820,70,Plane";
        ElementObj obj = getObj("plane");
        ElementObj play = obj.createElement(plaStr);
        em.addElement(play, GameElement.PLANE);
    }
 // 新增：专门加载敌人图片的方法
    // 在GameLoad类的loadEnemyImg方法中添加：
       public static void loadEnemyImg() {
           String texturl = "text/EnemyData.pro";
           ClassLoader classLoader = GameLoad.class.getClassLoader();
           InputStream texts = classLoader.getResourceAsStream(texturl);
           if (texts == null) {
               System.err.println("错误：未找到text/EnemyData.pro配置文件！");
               return;
           }
           pro.clear();
           try {
               pro.load(texts);
               Set<Object> set = pro.keySet();
               for (Object o : set) {
                   String key = o.toString();
                   String dirPath = pro.getProperty(key);
                   List<ImageIcon> imageList = new ArrayList<>();

                   // 核心修复：区分步枪和炮兵的帧系列
                   // 1. 步枪动画（100系列帧）
                   if (key.startsWith("enemy_")) { // 步枪key以enemy_开头
                       if (key.contains("attack_left")) {
                           int[] frames = {100, 110, 120, 130, 140}; // 步枪攻击帧
                           loadFrames(imageList, dirPath, "enemy_attack", frames);
                       } else if (key.contains("attack_right")) {
                           int[] frames = {101, 111, 121, 131, 141}; // 步枪攻击帧
                           loadFrames(imageList, dirPath, "enemy_attack", frames);
                       } else if (key.contains("run_left")) {
                           int[] frames = {100, 110, 120, 130, 140}; // 步枪跑动帧
                           loadFrames(imageList, dirPath, "enemy_run", frames);
                       } else if (key.contains("run_right")) {
                           int[] frames = {101, 111, 121, 131, 141}; // 步枪跑动帧
                           loadFrames(imageList, dirPath, "enemy_run", frames);
                       } else if (key.contains("die_left")) {
                           int[] frames = {100, 110, 120, 130, 140}; // 步枪死亡帧
                           loadFrames(imageList, dirPath, "enemy_die", frames);
                       } else if (key.contains("die_right")) {
                           int[] frames = {101, 111, 121, 131, 141}; // 步枪死亡帧
                           loadFrames(imageList, dirPath, "enemy_die", frames);
                       }
                   }
                   // 2. 炮兵动画（200系列帧，核心修复）
                   else if (key.startsWith("artillery_")) { // 炮兵key以artillery_开头
                       if (key.contains("attack_left")) {
                           int[] frames = {200, 210, 220, 230, 240}; // 炮兵攻击帧（200系列）
                           loadFrames(imageList, dirPath, "enemy_attack", frames);
                       } else if (key.contains("attack_right")) {
                           int[] frames = {201, 211, 221, 231, 241}; // 炮兵攻击帧（200系列）
                           loadFrames(imageList, dirPath, "enemy_attack", frames);
                       } else if (key.contains("run_left")) {
                           int[] frames = {200, 210, 220, 230, 240}; // 炮兵跑动帧（200系列）
                           loadFrames(imageList, dirPath, "enemy_run", frames);
                       } else if (key.contains("run_right")) {
                           int[] frames = {201, 211, 221, 231, 241}; // 炮兵跑动帧（200系列）
                           loadFrames(imageList, dirPath, "enemy_run", frames);
                       } else if (key.contains("die_left")) {
                           int[] frames = {200, 210, 220, 230, 240}; // 炮兵死亡帧（200系列）
                           loadFrames(imageList, dirPath, "enemy_die", frames);
                       } else if (key.contains("die_right")) {
                           int[] frames = {201, 211, 221, 231, 241}; // 炮兵死亡帧（200系列）
                           loadFrames(imageList, dirPath, "enemy_die", frames);
                       }
                   }

                   if (!imageList.isEmpty()) {
                       imgMaps.put(key, imageList);
                       System.out.println("加载动画成功：key=" + key + "，共" + imageList.size() + "帧");
                   } else {
                       System.err.println("警告：key=" + key + " 未加载到任何图片！");
                   }
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
 // 辅助方法：加载单个图片（兼容Java 8及以下版本）
    private static void loadSingleImage(List<ImageIcon> list, String dirPath, String fileName) {
        String imagePath = dirPath + "/" + fileName;
        InputStream imgStream = GameLoad.class.getClassLoader().getResourceAsStream(imagePath);
        
        if (imgStream != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024]; // 缓冲区
            int bytesRead;
            try {
                // 循环读取输入流到缓冲区
                while ((bytesRead = imgStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                // 将缓冲的字节转换为字节数组，创建ImageIcon
                byte[] imageData = baos.toByteArray();
                ImageIcon icon = new ImageIcon(imageData);
                list.add(icon);
                System.out.println("成功加载：" + imagePath);
            } catch (IOException e) {
                System.err.println("加载图片失败：" + imagePath);
                e.printStackTrace();
            } finally {
                // 关闭流（无论成功失败都要关闭）
                try {
                    imgStream.close();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.err.println("未找到图片：" + imagePath);
        }
    }

    // 辅助方法：加载多个指定帧的图片（兼容Java 8及以下版本）
    private static void loadFrames(List<ImageIcon> list, String dirPath, String prefix, int[] frameNumbers) {
        for (int num : frameNumbers) {
            String imagePath = dirPath + "/" + prefix + num + ".png";
            InputStream imgStream = GameLoad.class.getClassLoader().getResourceAsStream(imagePath);
            
            if (imgStream != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                try {
                    // 循环读取输入流到缓冲区
                    while ((bytesRead = imgStream.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    // 转换为字节数组并创建ImageIcon
                    byte[] imageData = baos.toByteArray();
                    ImageIcon icon = new ImageIcon(imageData);
                    list.add(icon);
                    System.out.println("成功加载：" + imagePath);
                } catch (IOException e) {
                    System.err.println("加载图片失败：" + imagePath);
                    e.printStackTrace();
                } finally {
                    // 关闭流
                    try {
                        imgStream.close();
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.err.println("未找到图片：" + imagePath);
            }
        }
    }
    

//     加载敌人到ElementManager
    public static void loadEnemy() {
        loadObj();           // 加载类映射
        loadEnemyImg();      // 加载敌人图片（新增）

        // 创建敌人实例
        String enemyStr = "500,350";
        ElementObj enemy = getObj("enemy");
        ElementObj enemyObj = enemy.createElement(enemyStr);
        em.addElement(enemyObj, GameElement.ENEMY);
    }
    
    public static void loadArtilleryEnemy() {
        loadObj();
        loadEnemyImg(); // 确保炮兵动画已加载
        String artilleryStr = "500,450"; 
        ElementObj obj = getObj("artillery_enemy");
        ElementObj artillery = obj.createElement(artilleryStr);
        em.addElement(artillery, GameElement.ENEMY); // 注意这里使用的是ENEMY类型
        System.out.println("加载炮兵敌人成功");
    }

    public static ElementObj getObj(String str) {
        try {
            Class<?> class1 = (Class)objMap.get(str);
            Object newInstance = class1.newInstance();
            if (newInstance instanceof ElementObj) {
                return (ElementObj)newInstance;
            }
        } catch (InstantiationException var3) {
            var3.printStackTrace();
        } catch (IllegalAccessException var4) {
            var4.printStackTrace();
        }

        return null;
    }

    public static void loadObj() {
        String texturl = "text/obj.pro";
        ClassLoader classLoader = GameLoad.class.getClassLoader();
        InputStream texts = classLoader.getResourceAsStream(texturl);
        pro.clear();

        try {
            pro.load(texts);
            Set<Object> set = pro.keySet();
            Iterator var5 = set.iterator();

            while(var5.hasNext()) {
                Object o = var5.next();
                String classUrl = pro.getProperty(o.toString());
                Class<?> forName = Class.forName(classUrl);
                objMap.put(o.toString(), forName);
            }
        } catch (IOException var8) {
            var8.printStackTrace();
        } catch (ClassNotFoundException var9) {
            var9.printStackTrace();
        }

    }

    public static void loadBoosImage() {
    	String boosUrl="text/boos";
    	ClassLoader classLoader = GameLoad.class.getClassLoader();
    	InputStream stream = classLoader.getResourceAsStream(boosUrl);
    	pro.clear();
    	try {
    		pro.load(stream);
    		Set<Object> set = pro.keySet();
    		Iterator var5 = set.iterator();
    		while(var5.hasNext()) {
    			Object o = var5.next();
    			String imageUrl = pro.getProperty(o.toString());
    			boosAttackMap.put(Integer.parseInt(o.toString()), new ImageIcon(imageUrl));
    		}
    	} catch (IOException e) {
    		// TODO 自动生成的 catch 块
    		e.printStackTrace();
    	}
    	boosUrl="text/GreenTaitan";
    	stream = classLoader.getResourceAsStream(boosUrl);
    	pro.clear();
    	try {
    		pro.load(stream);
    		Set<Object> set = pro.keySet();
    		Iterator var5 = set.iterator();
    		while(var5.hasNext()) {
    			Object o = var5.next();
    			String imageUrl = pro.getProperty(o.toString());
    			greenTaitan.put(o.toString(), new ImageIcon(imageUrl));
    		}
    	} catch (IOException e) {
    		// TODO 自动生成的 catch 块
    		e.printStackTrace();
    	}
    }
    public static void loadBoos() {
    	loadObj();
    	String boosStr = "300,300,1";
        ElementObj obj = getObj("boos");
        ElementObj boos = obj.createElement(boosStr);
        em.addElement(boos, GameElement.BOSS);
    }
    public static void loadBoss2() {
    	loadObj();
    	String boosStr = "500,300";
        ElementObj obj = getObj("GreenTaitan");
        ElementObj boos = obj.createElement(boosStr);
        em.addElement(boos, GameElement.BOSS);
    }
//    public static void main(String[] args) {
//        MapLoad(1);
//
//        try {
//            Class<?> forName = Class.forName("");
//            Class<?> forName1 = GameLoad.class;
//            GameLoad gameLoad = new GameLoad();
//            Class var4 = gameLoad.getClass();
//        } catch (ClassNotFoundException var5) {
//            var5.printStackTrace();
//        }
//
//    }
}
