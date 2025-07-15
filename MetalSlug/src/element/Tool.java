package element;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import manager.GameLoad;

public class Tool extends ElementObj{

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
	@Override
	public void die() {
		this.setIcon(null);
	}
}
