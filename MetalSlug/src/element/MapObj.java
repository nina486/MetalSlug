package element;

import java.awt.Graphics;

import javax.swing.ImageIcon;

import controller.GameThread;
import manager.ElementManager;
import manager.GameElement;
import manager.GameLoad;

public class MapObj extends ElementObj{
	private int zy=0;//0为不动，1为左，2为右
	private int pspeed=3;
	@Override
	public void showElement(Graphics var1) {
		var1.drawImage(this.getIcon().getImage(),getX(), getY(), getW(), getH(), null);
		
	}
	private static boolean mapmove=true;
	public static void setMapmove(boolean mapmove) {
		MapObj.mapmove = mapmove;
	}

	@Override
	protected void move(long gametime) {
		if(mapmove) {
			switch(this.zy) {
			case 0: return;
			case 1: 
				if (this.getX()<0) {
					this.setX(this.getX()+pspeed);
				}break;
			case 2://右
				if (this.getX()>-getW()+800) {
					this.setX(this.getX()-pspeed);
				}
			}
		}
		if(this.getX()+this.getW()<=800 && mapmove) {
			mapmove=false;
		}
	}
	
	public static boolean isMapmove() {
		return mapmove;
	}

	@Override
	public void keyClick(boolean bl, int key) {
		if (bl) {
			switch(key) {
			case 65:
				this.zy=1;break;
			case 68:
				this.zy=2;	
			}
		}else {
			switch(key) {
			case 65:
				this.zy=0;break;
			case 68:
				this.zy=0;break;
			}
			
		}
	}
	@Override
	public ElementObj createElement(String str) {
		ImageIcon imageIcon = new ImageIcon(str);
		this.setIcon(imageIcon);
		this.setX(0);this.setY(0);
		this.setW(imageIcon.getIconWidth());
		this.setH(600);
		return this;
	}
	private static boolean creatdata=false;
	public static void setCreatdata(boolean creatdata) {
		MapObj.creatdata = creatdata;
	}
	private static long curtime=0;
	public static long getCurtime() {
		return curtime;
	}

	@Override
	protected void add(long... gameTime) {
		if(!mapmove && !creatdata) {
			curtime=gameTime[0];
			switch(GameThread.index) {
			case 1:
				GameLoad.loadEnemy();
				GameLoad.loadArtilleryEnemy();break;
	        case 2:
	        	GameLoad.loadBoos();
		        GameLoad.loadBoss2();break;
		    case 3:
		    	GameLoad.loadEnemy();
				GameLoad.loadArtilleryEnemy();
				GameLoad.loadBoos();
		        GameLoad.loadBoss2();
			}
	        GameLoad.loadPlane();
			ElementObj element =new Hostage().createElement(this.tohostString());
			ElementManager.getManager().addElement(element, GameElement.HOSTAGE);
			creatdata=true;
			
		}
	}
	
	public String tohostString() {
		return "300,400,hostage";
	}

}

