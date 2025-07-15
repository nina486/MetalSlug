package element;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import manager.GameLoad;

public class ArtilleryBullet extends EnemyFile {
    // 炮弹速度（比步兵子弹慢，更符合炮弹特性）
    private static final int SPEED = 5; 
    // 炮弹存活时间（更长，飞行距离更远）
    private static final long MAX_LIFETIME = 3000; 
    private BufferedImage[] explosionFrames;
    private int explosionIndex = 0;
    private boolean isExploding = false;
    private long explosionStartTime = 0; // 改为记录 gameTime
    private static final int FRAMES_PER_EXPLOSION = 4;
    private static final int FRAME_DURATION = 5; // 每帧持续 20 * 10ms = 200ms
 // 新增：碰撞状态标记（默认未碰撞）
    private boolean isPked = false; 

    public ArtilleryBullet() {}
    
 // 在 ArtilleryBullet 类中添加一个方法，用于触发表爆炸并标记炮弹为不存活
    public void explodeAndDie() {
        explode(); // 仅触发爆炸动画，不立即终止
        // 移除 setLive(false); 这句，避免提前终止
    }
    
 // 新增：设置碰撞状态的方法（类似setPkType）
    public void setPked(boolean isPked) {
        this.isPked = isPked;
    }
    
    public boolean isPked() {
        return isPked;
    }

    public ArtilleryBullet(int x, int y, boolean isRightDirection) {
        super(x, y, isRightDirection);
        this.createTime = System.currentTimeMillis();
        loadBulletImage();
        loadExplosionImages(); // 加载爆炸图片
        updateIconByDirection();
    }

    // 加载炮兵专用子弹图片（bullet30.png/31.png）
    public void loadBulletImage() {
        try {
            // 向右炮弹：bullet31.png；向左炮弹：bullet30.png
            String rightPath = "images/子弹/bullet31.png";
            String leftPath = "images/子弹/bullet30.png";

            // 加载向右炮弹（原图）
            InputStream rightStream = GameLoad.class.getClassLoader().getResourceAsStream(rightPath);
            if (rightStream != null) {
                BufferedImage rightImg = ImageIO.read(rightStream);
                originalIcon = new ImageIcon(rightImg);
                rightStream.close();
            }

            // 加载向左炮弹（无需镜像，直接使用bullet30.png）
            InputStream leftStream = GameLoad.class.getClassLoader().getResourceAsStream(leftPath);
            if (leftStream != null) {
                BufferedImage leftImg = ImageIO.read(leftStream);
                flippedIcon = new ImageIcon(leftImg);
                leftStream.close();
            }
        } catch (IOException e) {
            System.err.println("加载炮兵子弹图片失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 加载爆炸动画图片
    private void loadExplosionImages() {
        try {
            String explosionPath = "images/爆炸/fire.gif";
            InputStream imgStream = GameLoad.class.getClassLoader().getResourceAsStream(explosionPath);
            if (imgStream == null) {
                System.err.println("错误：未找到爆炸图片 " + explosionPath);
                return;
            }

            BufferedImage explosionImg = ImageIO.read(imgStream);
            int totalWidth = explosionImg.getWidth();
            int frameWidth = totalWidth / 4; // 横向4帧，每帧宽度=总宽/4
            int frameHeight = explosionImg.getHeight();

            // 打印日志验证切割是否正确
            System.out.println("爆炸图总宽：" + totalWidth + "，每帧宽：" + frameWidth + "，高：" + frameHeight);

            explosionFrames = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                // 从左到右切割第i帧（0=最左，3=最右）
                explosionFrames[i] = explosionImg.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
                System.out.println("加载爆炸帧 " + i + "：宽=" + explosionFrames[i].getWidth());
            }
            imgStream.close();
        } catch (IOException e) {
            System.err.println("加载爆炸图片失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

 // 绘制爆炸帧
    @Override
    public void showElement(Graphics g) {
        if (isExploding) {
            if (explosionIndex < FRAMES_PER_EXPLOSION) {
                int drawX = getX();  // 默认X坐标
                int drawY = getY();  // 默认Y坐标
                
                // 根据炮弹方向微调爆炸位置
                if (isRightDirection) {
                    drawX += 60;  // 向右发射的炮弹，爆炸位置右移20像素
                } else {
                    drawX -= 90;  // 向左发射的炮弹，爆炸位置左移20像素
                }
                
                // 可以进一步微调Y坐标，让爆炸更贴近主角中心
                drawY -= 80;
                
                g.drawImage(
                    explosionFrames[explosionIndex],
                    drawX, drawY,  // 使用调整后的坐标
                    explosionFrames[explosionIndex].getWidth(),
                    explosionFrames[explosionIndex].getHeight(),
                    null
                );
            }
        } else {
            super.showElement(g);
        }
    }
 // 重写 updateImage：使用 gameTime（单位：10ms）
    @Override
    protected void updateImage(long gameTime) {
        if (isExploding) {
            if (explosionStartTime == 0) {
                explosionStartTime = gameTime; // 首次记录爆炸开始帧
            }
            long elapsedFrames = gameTime - explosionStartTime;
            explosionIndex = (int) (elapsedFrames / FRAME_DURATION);
            if (explosionIndex >= FRAMES_PER_EXPLOSION) {
                setLive(false); // 动画结束
            }
        }
    }

    // 触发爆炸
    public void explode() {
        isExploding = true;
        explosionIndex = 0;
        explosionStartTime = 0; // 会在第一次 updateImage 时设置
    }

    // 炮弹移动逻辑（水平直线飞行，速度更慢）
    @Override
    public void move(long time) {
        if (!isExploding) { // 只有在未爆炸时才更新位置
            if (isRightDirection) {
                this.setX(this.getX() + SPEED);
            } else {
                this.setX(this.getX() - SPEED);
            }
        }

        // 如果子弹超出屏幕范围同时未爆炸，触发表爆炸
        if (isOutOfBounds() && !isExploding) {
            explode(); // 触发表爆炸
        }
    }

    @Override
    public ElementObj createElement(String str) {
        String[] split = str.split(",");
        if (split.length < 3) {
            System.err.println("错误：炮兵子弹参数不足！");
            return null;
        }
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        boolean direction = Integer.parseInt(split[2]) == 1;
        return new ArtilleryBullet(x, y, direction);
    }

    // 重写碰撞检测逻辑，在碰撞时触发表爆炸
    @Override
    public boolean pk(ElementObj obj) {
        // 仅当：未碰撞过 + 碰撞对象是主角 + 父类pk检测通过
        if (!isPked() && super.pk(obj) && obj.getClass().getSimpleName().equals("Play")) {
            explode(); // 触发爆炸动画
            return true; // 仅返回碰撞成功，不标记状态
        }
        return false;
    }
}