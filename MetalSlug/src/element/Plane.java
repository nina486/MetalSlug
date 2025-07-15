package element;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import manager.ElementManager;
import manager.GameElement;
import manager.GameLoad;

public class Plane extends ElementObj{
	private boolean fx=true;//朝左为true
	@Override
	public void showElement(Graphics g) {
		g.drawImage(this.getIcon().getImage(),
				this.getX(), this.getY(), 
				this.getW(), this.getH(), null);
	}
	private long time=0;
	private int index=0;
	@Override
	public ElementObj createElement(String str) {
		String[] split = str.split(",");
		this.setX(new Integer(split[0]));
		this.setY(new Integer(split[1]));
		ImageIcon icon1 = GameLoad.imgMaps.get("left").get(0);
		this.setH(icon1.getIconHeight());
		this.setW(icon1.getIconWidth());
		this.setIcon(icon1);
		return this;
	} 
	private int curtime=0;
	@Override
	protected void move(long gametime) {
		if(gametime-MapObj.getCurtime()<100)return;
		if(gametime-time>10) {
			if(this.getX()>-200 && this.fx) {
				time=gametime;
				this.setX(this.getX()-15);
			}
			else if(this.getX()>900){
				this.setLive(false);
			}
			else{
				time=gametime;
				this.setX(this.getX()+15);
			}
		}
	}
	@Override
	protected void updateImage(long time) {
		if(this.getX()<=-200)this.fx=false;
		if(this.fx)this.setIcon(GameLoad.imgMaps.get("left").get(0));
		else this.setIcon(GameLoad.imgMaps.get("right").get(0));
	}
	private long filetime=MapObj.getCurtime();
	@Override
	protected void add(long... gameTime) {
		long gametime=gameTime[0];
		if(gametime-filetime>160) {
			filetime=gametime;
			ElementObj elementObj = new PlaneFile().createElement(this.toString());
			ElementManager.getManager().addElement(elementObj, GameElement.PLANEFILE);
		}
	}
	@Override
	public String toString() {
		return (this.getX()+this.getW()/2)+","+(this.getY()+this.getH())+","+"planefile";
	}
}
