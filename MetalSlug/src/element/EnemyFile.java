package element;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import manager.GameLoad;

public class EnemyFile extends ElementObj {
    // 子弹速度（像素/帧）
    private static final int SPEED = 8;
    // 子弹存活时间（毫秒）
    private static final long MAX_LIFETIME = 2000;
    // 子弹创建时间
    public long createTime;
    // 子弹方向（向右为true，向左为false）
    protected boolean isRightDirection;
    // 原始图片和镜像图片（避免重复创建）
    protected ImageIcon originalIcon;
    protected ImageIcon flippedIcon;

 // 无参构造函数（必须添加，否则会报错）
    public EnemyFile() {
        // 可以为空，或初始化必要的默认值
    }

    // 带参构造函数（保留原有逻辑）
    public EnemyFile(int x, int y, boolean isRightDirection) {
        this.setX(x);
        this.setY(y);
        this.isRightDirection = isRightDirection;
        this.createTime = System.currentTimeMillis();
        loadBulletImage();
        updateIconByDirection();
    }
    
    public void loadBulletImage() {
        try {
            // 加载原始图片（向右的子弹）
            String imagePath = "images/子弹/bomb.gif";
            InputStream imgStream = GameLoad.class.getClassLoader().getResourceAsStream(imagePath);
            if (imgStream == null) {
                System.err.println("错误：未找到子弹图片 " + imagePath);
                return;
            }
            
            BufferedImage originalImage = ImageIO.read(imgStream);
            originalIcon = new ImageIcon(originalImage);
            
            // 创建镜像图片（向左的子弹）
            BufferedImage flippedImage = new BufferedImage(
                originalImage.getWidth(), 
                originalImage.getHeight(), 
                originalImage.getType()
            );
            
            java.awt.Graphics2D g2d = flippedImage.createGraphics();
            g2d.drawImage(originalImage, 
                originalImage.getWidth(), 0, 
                -originalImage.getWidth(), originalImage.getHeight(), 
                null
            );
            g2d.dispose();
            
            flippedIcon = new ImageIcon(flippedImage);
            imgStream.close();
        } catch (IOException e) {
            System.err.println("加载子弹图片失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 根据方向更新子弹图片
    public void updateIconByDirection() {
        this.setIcon(isRightDirection ? originalIcon : flippedIcon);
        // 设置子弹宽高（使用图片尺寸）
        if (this.getIcon() != null) {
            this.setW(this.getIcon().getIconWidth());
            this.setH(this.getIcon().getIconHeight());
        }
    }

    @Override
    public void showElement(Graphics g) {
        if (this.getIcon() == null) return;
        
        // 绘制子弹
        g.drawImage(this.getIcon().getImage(), this.getX(), this.getY(), getW(), getH(), null);
    }

    // 更新子弹位置（每帧调用）
    @Override
    public void move(long time) {
        // 根据方向更新X坐标（水平直线飞行）
        if (isRightDirection) {
            this.setX(this.getX() + SPEED);
        } else {
            this.setX(this.getX() - SPEED);
        }
        
        // 检查是否超出屏幕范围，标记为过期
        if (this.getX() < -50 || this.getX() > 850) {
            this.createTime = 0; // 强制过期
        }
    }

    // 检查子弹是否已过期
    public boolean isExpired() {
        return System.currentTimeMillis() - createTime > MAX_LIFETIME;
    }

    @Override
    public ElementObj createElement(String str) {
        // 解析参数格式：x,y,方向（1为右，0为左）
        String[] split = str.split(",");
        if (split.length < 3) {
            System.err.println("错误：EnemyFile参数不足，格式应为 x,y,direction");
            return null;
        }
        
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        boolean direction = Integer.parseInt(split[2]) == 1;
        
        // 使用带参构造函数创建实例
        return new EnemyFile(x, y, direction);
    }
    
 // 重写die方法（子弹死亡时的处理）
    @Override
    public void die() {
        // 清空图片，标记为不存活
        this.setIcon(null);
        this.setLive(false);
    }

    // 重写isLive方法（判断子弹是否存活）
    @Override
    public boolean isLive() {
        // 子弹存活条件：未过期且未超出屏幕
        return super.isLive() && !isExpired() && !isOutOfBounds();
    }

    // 新增：检查是否超出屏幕范围
    public boolean isOutOfBounds() {
        // 假设屏幕宽度为800（根据实际游戏窗口调整）
        return this.getX() < -50 || this.getX() > 850;
    }

	public void draw(Graphics g) {
		// TODO 自动生成的方法存根
		
	}

}