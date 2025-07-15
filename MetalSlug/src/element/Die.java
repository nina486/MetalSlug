package element;

import java.awt.Graphics;
import java.util.List;

import javax.swing.ImageIcon;

import manager.GameLoad;

public class Die extends ElementObj{

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
	private long time=0;
	private int index=0;
	private boolean pkType=true;
	
	public void setPkType(boolean pkType) {
		this.pkType = pkType;
	}
	public boolean isPkType() {
		return pkType;
	}
	@Override
	protected void updateImage(long gametime) {
		if(gametime-time>3){
			time=gametime;
			List<ImageIcon> list = GameLoad.imgMaps.get("planefiledie");
			this.setIcon(list.get(index));
			index++;
			if(index>10) {
				this.pkType=false;
			}
			index %= list.size();
			if(index==0) {
				this.setLive(false);
			}
		}
	}
	@Override
	public void die() {
		this.setIcon(null);
	}
}
