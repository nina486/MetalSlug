package element;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

import manager.GameLoad;

public class Boos extends ElementObj{
	private int hp=100;//生命值
	private boolean pktype=false;//是否存于攻击状态
	private int index=0;//boos攻击图放置
	private long localtime=0;
	private static final int MAX_HP = 100; 
	private boolean fy = false;//false向下跑，true向上跑；
	@Override
	public void showElement(Graphics var1) {
		var1.drawImage(this.getIcon().getImage(), this.getX(), this.getY(),this.getW(),this.getH(), null);
		
	}
	@Override
	public ElementObj createElement(String str) {
		String[] split = str.split(",");
		this.setX(new Integer(split[0]));
		this.setY(new Integer(split[1]));
		this.setIcon(GameLoad.boosAttackMap.get(new Integer(split[2])));
		this.setW(this.getIcon().getIconWidth());
		this.setH(this.getIcon().getIconHeight());
		return this;
	}
	@Override
	protected void updateImage(long time) {
		if (time-localtime>8) {
			localtime=time;
			this.setIcon(GameLoad.boosAttackMap.get(++index));
			if (index == 9) {
				this.pktype=true;
//				try {
//					File fireFile = new File("music/knife.wav");
//					AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fireFile);
//					Clip clip = AudioSystem.getClip();
//					clip.open(audioInputStream);
//					clip.start();
//					//Thread.sleep(clip.getMicrosecondLength()/1000);
//				} catch (UnsupportedAudioFileException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				} catch (LineUnavailableException e) {
//					// TODO 自动生成的 catch 块
//					e.printStackTrace();
//				} 
			}
			index= index % GameLoad.boosAttackMap.size(); 
		}
	}
	@Override
	public boolean pk(ElementObj obj) {
		if (pktype) {
			return super.pk(obj);
		}else {
			return false;
		}
	}
	@Override
	protected void move(long time) {
		File knife = new File("/MetalSlug/music/knife.wav");
		if (!fy) {
			this.setY(getY()+1);
		}else {
			this.setY(getY()-1);
		}
		if (this.getY()<=300) {
			this.fy=false;
		}
		if (this.getY()>=400) {
			this.fy=true;
		}
	}
	@Override
	public Rectangle getRectangle() {
		// TODO 自动生成的方法存根
		return new Rectangle(this.getX(),this.getY()+50,getW(),getH()/3);
	}
	public boolean isPktype() {
		return pktype;
	}
	public void setPktype(boolean pktype) {
		this.pktype = pktype;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	
}
