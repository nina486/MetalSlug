package element;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import manager.ElementManager;
import manager.GameElement;
import manager.GameLoad;

public class PlaneFile extends ElementObj{

	@Override
	public void showElement(Graphics g) {
		g.drawImage(this.getIcon().getImage(),
				this.getX(), this.getY(), 
				this.getW(), this.getH(), null);
	}
	@Override
	public ElementObj createElement(String str) {
		String[] split = str.split(",");
		this.setX(new Integer(split[0]));
		this.setY(new Integer(split[1]));
		ImageIcon icon1 = GameLoad.imgMaps.get(split[2]).get(0);
		this.setH(icon1.getIconHeight());
		this.setW(icon1.getIconWidth());
		this.setIcon(icon1);
		return this;
	}
	private long time=MapObj.getCurtime();
	@Override
	protected void move(long gametime) {
		if(gametime-time>10) {
			time=gametime;
			this.setY(this.getY()+20);
		}
		if(this.getY()+this.getH()>450)this.setLive(false);
	}
	@Override
	public void die() {
		ElementObj elementObj = new Die().createElement(this.toString());
		ElementManager.getManager().addElement(elementObj, GameElement.DIE);
		this.setIcon(null);
	}
	@Override
	public String toString() {
		return (this.getX()-this.getW()-20)+","+(this.getY()+this.getH()/2)+","+"planefiledie";
	}
}
