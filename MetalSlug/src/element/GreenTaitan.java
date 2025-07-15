package element;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import manager.ElementManager;
import manager.GameElement;
import manager.GameLoad;

public class GreenTaitan extends ElementObj{
	private int hp=100;//生命值
	private boolean pktype=false;//是否存于攻击状态
	private int index=0;//boos攻击图放置
	private long localtime=0;
	private int attack=50;
	private boolean movetype=false;//是否攻击图片
	private String fx="right";
	private int index2=0;
	private long localtime2=0;
	private int offset=60;
	private ElementManager em=ElementManager.getManager();
	@Override
	public void showElement(Graphics var1) {
		var1.drawImage(this.getIcon().getImage(), this.getX(), this.getY(),this.getW(),this.getH(), null);
		
	}
	@Override
	public ElementObj createElement(String str) {
		String[] split = str.split(",");
		this.setX(new Integer(split[0]));
		this.setY(new Integer(split[1]));
		this.setIcon(new ImageIcon("images/Enemy/boss_lv/01.png"));
		this.setW(this.getIcon().getIconWidth());
		this.setH(this.getIcon().getIconHeight());
		return this;
	}
	@Override
	protected void updateImage(long time) {
		//if (movetype) {
			if (time-localtime>8) {
				if (fx.equals("right")) {
					localtime=time;
					++index;
					this.setIcon(GameLoad.greenTaitan.get("right"+index));
					if (index == 9) {
						this.pktype=true;
					}
					index= index % 21; 
				}
				if (fx.equals("left")) {
					localtime=time;
					++index2;
					this.setIcon(GameLoad.greenTaitan.get("left"+index2));
					if (index2 == 9) {
						this.pktype=true;
					}
					index2= index2 % 20; 
				}
			}
		}
		
	//}
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
		if (time>localtime2) {
			localtime2=time;
			List<ElementObj> plays = em.getElementsByKey(GameElement.PLAY);
			for(int i=0; i<plays.size();i++) {
				Play play = (Play) plays.get(i);
				if (this.getX()-play.getX()>-50) {
					this.fx="right";
					this.setX(getX()-1);
				}else if(this.getX()-play.getX()<-150){
					this.fx="left";
					this.setX(getX()+1);
				}
				if (this.getY()!=play.getY()-offset) {
					int dy = this.getY()-play.getY();
					if (dy>-offset) {
						this.setY(getY()-1);
					}
					else if(dy<offset){
						this.setY(getY()+1);
					}
					
				}
			}
		}
	}
	@Override
	public Rectangle getRectangle() {
		// TODO 自动生成的方法存根
		return new Rectangle(this.getX()+this.getW()/2, this.getY(), this.getW()/3, this.getH());
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
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
