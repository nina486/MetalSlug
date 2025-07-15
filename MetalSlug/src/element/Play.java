package element;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

import controller.GameThread;
import manager.ElementManager;
import manager.GameElement;
import manager.GameLoad;

public class Play extends ElementObj{
	private boolean xmove=false;
//	private boolean up=false;
//	private boolean right=false;
	private boolean ymove=false;
	private int hp=400; 
	private int MAX_HP=400; 
//	private Map<String,ImageIcon> imgMap;
	private String fx="right";
	private	boolean pkType=false;
	private long time=0;
	private ImageIcon icon2 = null;
	private int h2=0;
	private int w2=0;
	private String movement="stand";
	private static String weaponType="pistol";
	public static String getWeaponType() {
		return weaponType;
	}
	private String tx="null";
	public ImageIcon getIcon2() {
		return icon2;
	}
	public void setIcon2(ImageIcon icon2) {
		this.icon2 = icon2;
	}
	public int getH2() {
		return h2;
	}
	public void setH2(int h2) {
		this.h2 = h2;
	}
	public int getW2() {
		return w2;
	}
	public void setW2(int w2) {
		this.w2 = w2;
	}
	public Play() {}
	public Play(int x, int y, int w, int h, ImageIcon icon) {
		super(x, y, w, h, icon);
		
//		imgMap=new HashMap<>();
//		imgMap.put("left", new ImageIcon("image/plays/attack000.png"));
//		imgMap.put("right", new ImageIcon("image/plays/attack001.png"));
	}
	
	@Override
	public ElementObj createElement(String str) {
		//
		String[] split = str.split(",");
		this.setX(new Integer(split[0]));
		this.setY(new Integer(split[1]));
		ImageIcon icon1 = GameLoad.imgMaps.get("right_pistol_nojump").get(0);
		this.setH(icon1.getIconHeight());
		this.setW(icon1.getIconWidth());
		this.setIcon(icon1);
		ImageIcon icon2 = GameLoad.imgMaps.get("right_stand").get(0);
		this.setH2(icon2.getIconHeight());
		this.setW2(icon2.getIconWidth());
		this.setIcon2(icon2);
		return this;
	} 
	@Override
	public void showElement(Graphics g) {
		drawHealthBar(g);
		if(this.fx == "right") {
			g.drawImage(this.getIcon().getImage(),
					this.getX(), this.getY(), 
					this.getW(), this.getH(), null);
			g.drawImage(this.getIcon2().getImage(),
					this.getX()+4, this.getY()+49, 
					this.getW2(), this.getH2(), null);
		}
		else {
			g.drawImage(this.getIcon().getImage(),
					this.getX()-44, this.getY(), 
					this.getW(), this.getH(), null);
			g.drawImage(this.getIcon2().getImage(),
					this.getX()+4, this.getY()+49, 
					this.getW2(), this.getH2(), null);
		}
	}
	
	public void keyClick(boolean bl,int key) {
		if(bl) {
			switch(key){
				case 65: //左
					this.xmove=true;this.fx="left";
//					System.out.println("A键按下，xmove设为true"); // 新增
					break;
				case 87: //上
					this.ymove=true;this.tx="up" ;
//					 System.out.println("W键按下，ymove设为true"); // 新增
					break;
				case 68: //右
					this.xmove=true;this.fx="right";
//					 System.out.println("D键按下，xmove设为true"); // 新增
					break;
				case 83: //下
					this.ymove=true;this.tx="down" ;
//					System.out.println("S键按下，ymove设为true"); // 新增
					break;
				case 32:
					if(movement=="run") {
						index=0;
					}
					this.movement="jump";
					break;
				case 69:weaponType=(weaponType == "pistol") ? "rpg" : "pistol";
					break;
			}
		} 
		else {
			switch(key){
			case 65: this.xmove=false;   break;
			case 87: this.ymove=false;    this.tx="null"; break;
			case 68: this.xmove=false;  break;
			case 83: this.ymove=false;  this.tx="null";  break;
			case 32: break;
			}
		}
//		if(this.left) {
//			this.setX(this.getX()-10);
//		}
//		if(this.up) {
//			this.setY(this.getY()-10);
//		}
//		if(this.right) {
//			this.setX(this.getX()+10);
//		}
//		if(this.down) {
//			this.setY(this.getY()+10);
//		}
	}
//		if(bl) {
//			switch(key){
//				case 37: this.setX(this.getX()-10); break;
//				case 38: this.setY(this.getY()-10); break;
//				case 39: this.setX(this.getX()+10); break;
//				case 40: this.setY(this.getY()+10); break;
//			}
//		}
//	}
	private int jumpindex=0;
	private long uptime=0;
	@Override
	public void move(long gameTime) {
		 // 调试：输出地图状态和移动标记
//	    System.out.println("Map move: " + MapObj.isMapmove() + ", xmove: " + xmove + ", fx: " + fx);
		
		if (this.tx=="up" && this.getY()>340) {
			this.setY(this.getY() - 1);
		}
		if (this.tx=="down" && this.getY()<430) {
		this.setY(this.getY() + 1);
		}
		if (movement=="jump") {
			if(index>6)index=0;
			if(jumpindex!=index && index<=3) {
				jumpindex=index;
				this.setY(this.getY()-15);
			}
			else if(jumpindex!=index && index>3 && jumpindex!=0) {
				jumpindex=index;
				this.setY(this.getY()+15);
			}
			if(jumpindex==6) {
				jumpindex=0;
				movement="stand";
			}
		}
		if (!MapObj.isMapmove() && this.xmove ) {
			if(this.fx=="left" && this.getX()>0 )
			this.setX(this.getX() - 3);
			else if(this.fx=="right" && this.getX()<800-this.getW())
				this.setX(this.getX() + 3);
		}
	}
	private int index=0;
	private int upindex=0;
	private String bulletLocation;
	private boolean fire=false;
	@Override
	protected void updateImage(long gameTime) {
		if((this.xmove || this.ymove) && movement!="jump") {
			movement = "run";
		}
		else if(movement!="jump"){
			movement = "stand";
		}
		if(movement == "run") {
			if(gameTime-time > 8) {
				if(index==9)index=0;
				time = gameTime;
				List<ImageIcon> list = GameLoad.imgMaps.get(String.join("_",fx,movement));
				this.setIcon2(list.get(index));
				index++;
				index %= list.size();
			}
		}
		else if(movement == "stand") {
			List<ImageIcon> list = GameLoad.imgMaps.get(String.join("_",fx,movement));
			this.setIcon2(list.get(0));
		}
		else {//movement == jump
			if(index>6)index=0;
			List<ImageIcon> list = GameLoad.imgMaps.get(String.join("_",fx,movement));
			if(gameTime-time > 6) {
				time=gameTime;
				this.setIcon2(list.get(index));
				index++;
				index %= list.size();
			}
		}
		if(!xmove&&!ymove&&movement!="jump") {
			index=0;
		}
		if(this.pkType) {
			if(gameTime-uptime > 6) {
				uptime=gameTime;
				List<ImageIcon> list = GameLoad.imgMaps.get(String.join("_", fx,weaponType,"nojump"));
				this.setIcon(list.get(upindex));
				upindex++;
				upindex %= list.size();
				if(upindex==0) {
					pkType=false;
					this.fire=false;
				}
			}
		}
		else {
			List<ImageIcon> list = GameLoad.imgMaps.get(String.join("_", fx,weaponType,"nojump"));
			this.setIcon(list.get(0));
		}
	}
	private long firetime=0;
	@Override
	protected void add(long ... gameTimes) {
		long gameTime=gameTimes[0];
		if(!this.pkType) {
			return;
		}
		if(gameTimes!=null && gameTime-firetime>=25 && this.fire) {
			this.fire=false;
			firetime=gameTime;
			ElementObj elementObj = new PlayFile().createElement(this.bulletLocation);
			ElementManager.getManager().addElement(elementObj, GameElement.PLAYFILE); 
		}
		 
//		 Class<?> forName = Class.forName("com.tedu...");
//		 ElementObj element = forName.newInstance().createElement("");
	}
	
	@Override
	public void mouseClick(boolean b, int x, int y) {
		if (b) {
			if(this.pkType==true)return;
			this.pkType=true;this.fire=true;
			upindex=0;
			this.bulletLocation=new String(this.toString()+",a:"+x+",b:"+y);
		}
	}
	
	@Override
	public String toString() {
		int x=0;int y=0;
		switch(fx) {
		case "left":x=this.getX()-60;y=this.getY()+18;break;
		case "right":x=this.getX()+70;y=this.getY()+18;
		}
		
		return "x:"+x+",y:"+y;
	}
	
	public int getFileX() {
		
		return 0;
	}
	
	public int getFileY() {
		
		return 0;
	}
	 private void drawHealthBar(Graphics g) {
	        // 原有血条绘制逻辑...
	        int barX = 100;
	        int barY = 50;
	        float hpPercent = (float) hp / MAX_HP;
	        Color hpColor;
	        if (hpPercent > 0.6f) {
	            hpColor = Color.GREEN;
	        } else if (hpPercent > 0.3f) {
	            hpColor = Color.YELLOW;
	        } else {
	            hpColor = Color.RED;
	        }
	        g.setColor(Color.BLACK);
	        g.fillRect(barX , barY , 300, 30);
	        int currentHpWidth = (int) (300 * hpPercent);
	        g.setColor(hpColor);
	        g.fillRect(barX, barY, currentHpWidth, 30);
	        g.setColor(Color.WHITE);
	        g.drawImage(new ImageIcon("images/plays/attack001.png").getImage(), 10, 40, null);
	    }
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public Rectangle getRectangle() {
		return new Rectangle(this.getX(), this.getY(), this.getW(), this.getH()+this.getH2());
	}
}