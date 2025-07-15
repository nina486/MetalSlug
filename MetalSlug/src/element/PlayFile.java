package element;

import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

public class PlayFile extends ElementObj{
	private int attack=1;
	private int speed=5;
	private int mx=0;
	private int my=0;
	private double x2;
	private double y2;
	private double c=0;
	@Override
	public void showElement(Graphics var1) {
		// TODO 自动生成的方法存根
		var1.drawImage(this.getIcon().getImage(), this.getX(), this.getY(),this.getW(),this.getH(), null);
	}
	@Override
	public ElementObj createElement(String str) {
		if(Play.getWeaponType()=="pistol") {
			this.setIcon(new ImageIcon("images/bullet/bomb3.gif"));
		}
		else {
			this.setIcon(new ImageIcon("images/bullet/boss_bomb.png"));
			this.attack=5;
		}
		String[] split = str.split(",");
		for(String str1 : split) {//X:3
			String[] split2 = str1.split(":");// 0下标 是 x,y,
			switch(split2[0]) {
			case "x": this.setX(Integer.parseInt(split2[1]));break;
			case "y":this.setY(Integer.parseInt(split2[1]));break;
			case "a":this.mx=new Integer(split2[1]);break;
			case "b":this.my=new Integer(split2[1]);break;
			}
		}
		this.setW(this.getIcon().getIconWidth());
		this.setH(this.getIcon().getIconHeight());
		this.x2 = this.getX();
		this.y2 = this.getY();
		if (this.c == 0) {
			this.c= Math.sqrt((this.getX()-this.mx)*(this.getX()-this.mx)+(this.getY()-this.my)*(this.getY()-this.my));
		}
		try {
			File fireFile = new File("music/buzi.wav");
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fireFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
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
		
		return this;
	}
	@Override
	protected void move(long time) {
		this.setX((int) (getX()-speed*(this.x2-this.mx)/this.c));
		this.setY((int) (getY()-speed*(this.y2-this.my)/this.c));
		if (this.getX()>800 || this.getY()>600 || this.getX()<0 || this.getY()<0) {
			this.setLive(false);
		}
	}

	@Override
	public void die() {
		this.setIcon(new ImageIcon("images/bullet/bomb2.png"));
		this.setW(50);
		this.setH(50);
	}
}
