package element;

import java.awt.Graphics;
import java.util.List;

import javax.swing.ImageIcon;

import manager.ElementManager;
import manager.GameElement;
import manager.GameLoad;

public class Hostage extends ElementObj{

	public boolean isRescued() {
		return rescued;
	}
	public void setRescued(boolean rescued) {
		this.rescued = rescued;
		this.index=0;
	}
	@Override
	public void showElement(Graphics g) {
		g.drawImage(this.getIcon().getImage(),
				this.getX(), this.getY(), 
				this.getW(), this.getH(), null);
	}
	private boolean rescued=false;
	private long time=0;
	private int index=0;
	@Override
	public ElementObj createElement(String str) {
		String[] split = str.split(",");
		this.setX(new Integer(split[0]));
		this.setY(new Integer(split[1]));
		ImageIcon icon1 = GameLoad.imgMaps.get("unrescued").get(0);
		this.setH(icon1.getIconHeight());
		this.setW(icon1.getIconWidth());
		this.setIcon(icon1);
		return this;
	} 
	@Override
	protected void updateImage(long gameTime) {
		if(!MapObj.isMapmove()) {
			if(rescued) {
				if(gameTime-time > 8) {
					time = gameTime;
					List<ImageIcon> list = GameLoad.imgMaps.get("rescued");
					this.setIcon(list.get(index));
					System.out.println("c.w"+this.getIcon().getIconWidth());
					index++;
					System.out.println("cc"+index);
					index %= list.size();
				}
			}
			else {
				if(gameTime-time > 12) {
					time = gameTime;
					List<ImageIcon> list = GameLoad.imgMaps.get("unrescued");
					this.setIcon(list.get(index));
					index++;
					index %= list.size();
				}
			}
		}
	}
	private int resindex=100;
	@Override
	protected void move(long time) {
		if(rescued) {
			if(resindex!=index && index<=8) {
				resindex=index;
			}
			else if(resindex!=index && index>8 && index<=14) {
				resindex=index;
				this.setX(this.getX()-8);
			}
			else if(resindex!=index && index>14 && index<=21) {
				resindex=index;
			}
			else if(resindex!=index && index>21 && index<27) {
				resindex=index;
				this.setX(this.getX()-20);
			}
			else if (index==27) {
				this.setLive(false);
				ElementObj elementObj = new Tool().createElement(this.toString());
				ElementManager.getManager().addElement(elementObj, GameElement.TOOL);
			}
		}
	}
	@Override
	public String toString() {
		return (this.getX()+this.getW()/2)+","+(this.getY()+this.getH()-20)+","+"tool";
	}
	@Override
	public void die() {
	}
}
