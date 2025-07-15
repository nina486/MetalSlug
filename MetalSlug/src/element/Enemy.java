package element;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

import manager.ElementManager;
import manager.GameElement;
import manager.GameLoad;

public class Enemy extends ElementObj {
    // 预留属性：血量、子弹、移动状态等
	protected int hp; // 血量
    protected boolean isAttacking; // 是否攻击中
    protected int attackFrameIndex = 0; // 攻击动画帧索引（初始为0，对应enemy_attack10）
    // 新增：记录上次更新时间（控制动画帧率）
    protected long lastFrameTime = 0;
    // 动画帧率：每10毫秒切换一帧
    protected static final long FRAME_INTERVAL = 10;
    protected boolean isHit = false; // 是否被击中
    protected long hitTime = 0; // 被击中的时间
    
 // 新增：装弹停顿时间（30毫秒，比普通帧长）
    protected static final long RELOAD_INTERVAL = 30; 
    
    // 新增：血条相关属性
    protected static final int MAX_HP = 100; // 最大生命值
    protected static final int HP_BAR_WIDTH = 40; // 满血时血条宽度
    protected static final int HP_BAR_HEIGHT = 5; // 血条高度
    protected static final int HP_BAR_OFFSET_Y = -15; // 血条在敌人头顶上方的偏移量
    
    protected boolean isDying = false; // 是否处于死亡状态（播放死亡动画中）
    protected int dieFrameIndex = 0; // 死亡动画帧索引
    protected long dieStartTime = 0; // 死亡动画开始时间
    protected static final long DIE_FRAME_INTERVAL = 20; // 死亡动画帧间隔（毫秒）
    
    
    // 新增：子弹发射间隔（毫秒）
    protected static final long FIRE_INTERVAL = 500;
    // 上次发射时间
    protected long lastFireTime = 0;
    // 子弹发射的动画帧索引（根据实际动画帧调整）
    protected static final int FIRE_FRAME_INDEX = 2;
    
 // 新增：方向控制和攻击范围
    protected boolean isFacingRight = false; // 默认向左
    protected static final int ATTACK_RANGE = 200; // 攻击范围
    
    // 新增：跑动相关属性
    protected boolean isRunning = false; // 是否处于跑动状态
    protected int runFrameIndex = 0; // 跑动动画帧索引
    protected long lastRunFrameTime = 0; // 上次跑动帧更新时间
    protected static final long RUN_FRAME_INTERVAL = 10; // 跑动动画帧间隔（毫秒）
    protected static final int RUN_SPEED = 1; // 跑动速度（像素/帧）
    protected static final int VERTICAL_SPEED = 1; // 垂直移动速度（比水平慢，更自然）
    protected static final int RUN_STOP_THRESHOLD = 300; // 从100改为150（保持更远距离）
    protected static final int VERTICAL_TOLERANCE = 30; // 垂直方向允许的误差（像素，用于判断是否"水平可击中"）
 // 
    
    
    protected int standBottomY = 0; // 站立时的底部Y坐标（脚部位置）
    
    public Enemy() {
        this.hp = MAX_HP; // 初始化满血
    }

    public Enemy(int x, int y, int w, int h, ImageIcon icon) {
        super(x, y, w, h, icon);
    }

 // 【核心修改：重写绘制方法，根据地图偏移修正位置】
 // 【核心修改】重写绘制方法，添加血条绘制
    @Override
    public void showElement(Graphics g) {
        // 1. 检查icon是否为null，避免崩溃
        if (this.getIcon() == null) {
            System.err.println("警告：Enemy的icon为null，跳过绘制！");
            return;
        }

        // 2. 处理地图滚动，计算绘制位置（核心修正）
        List<ElementObj> maps = ElementManager.getManager().getElementsByKey(GameElement.MAPS);
        int drawX = this.getX();
        int drawY = this.getY();

        if (!maps.isEmpty()) {
            ElementObj map = maps.get(0);
            // 修正：地图偏移量直接使用map.getX()，而非取负（关键修改）
            int mapOffsetX = map.getX(); 
            drawX = this.getX() + mapOffsetX; // 敌人屏幕位置 = 绝对位置 + 地图偏移
        }

        // 3. 绘制敌人图片
        g.drawImage(this.getIcon().getImage(), this.getX(), this.getY(), getW(), getH(), null);

        // 4. 绘制血条
        if (this.isLive()) {
            drawHealthBar(g, this.getX(), this.getY());
        }
    }
    
 // 新增：绘制血条方法
    private void drawHealthBar(Graphics g, int enemyDrawX, int enemyDrawY) {
        // 新增：死亡状态（isDying为true）时不绘制血条
        if (isDying) {
            return;
        }

        // 原有血条绘制逻辑...
        int barX = enemyDrawX + (getW() - HP_BAR_WIDTH) / 2;
        int barY = enemyDrawY + HP_BAR_OFFSET_Y;
        float hpPercent = (float) hp / MAX_HP;
        Color hpColor;
        if (hpPercent > 0.6f) {
            hpColor = Color.GREEN;
        } else if (hpPercent > 0.3f) {
            hpColor = Color.YELLOW;
        } else {
            hpColor = Color.RED;
        }
        g.setColor(Color.BLACK);
        g.fillRect(barX - 1, barY - 1, HP_BAR_WIDTH + 2, HP_BAR_HEIGHT + 2);
        int currentHpWidth = (int) (HP_BAR_WIDTH * hpPercent);
        g.setColor(hpColor);
        g.fillRect(barX, barY, currentHpWidth, HP_BAR_HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString(hp + "/" + MAX_HP, barX + HP_BAR_WIDTH + 5, barY + HP_BAR_HEIGHT);
    }

    // 重写初始化方法：从配置创建敌人
    @Override
    public ElementObj createElement(String str) {
        String[] split = str.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);

        // 核心修改：初始图片使用向左攻击动画的第一帧（enemy_attack100），移除闲置逻辑
        String initKey = "enemy_attack_left"; 
        List<ImageIcon> attackImages = GameLoad.imgMaps.get(initKey);

//        System.out.println("Enemy初始化：尝试加载初始攻击帧 key=" + initKey);
        if (attackImages == null) {
            System.err.println("错误：攻击动画 key=" + initKey + " 无图片列表！");
            this.setIcon(new ImageIcon()); // 容错：避免null
        } else if (attackImages.isEmpty()) {
            System.err.println("错误：key=" + initKey + " 的图片列表为空！");
            this.setIcon(new ImageIcon());
        } else {
            // 正常赋值初始帧（enemy_attack100）
            ImageIcon firstIcon = attackImages.get(0);
            this.setIcon(firstIcon);
            this.setX(x);
            this.setY(y);
            this.setW(firstIcon.getIconWidth());
            this.setH(firstIcon.getIconHeight());
//            System.out.println("Enemy初始化成功：初始帧为" + initKey + "的第0帧（放下枪）");
            
         // 新增：记录站立时的底部Y坐标（Y + 高度 = 脚部位置）
            this.standBottomY = y + firstIcon.getIconHeight();
        }
        
        

        this.hp = MAX_HP;
        this.isAttacking = true; // 强制初始为攻击状态
        return this;
    }

 // 新增：更新敌人状态（由GameThread调用）
    public void updateState(long gameTime) {
        if (this.hp <= 0 && !this.isDying) {
            // 死亡状态逻辑（原有）
            this.isDying = true;
            this.isAttacking = false;
            this.dieStartTime = gameTime;
            this.dieFrameIndex = 0;
            
         // 新增：播放死亡音效（倒地动画开始时）
            playDeathSound(); // 调用播放音效的方法
            
            return;
        }

        List<ElementObj> players = ElementManager.getManager().getElementsByKey(GameElement.PLAY);
        if (players.isEmpty()) return;
        ElementObj player = players.get(0);

     // 计算水平和垂直距离
        int playerX = player.getX();
        int playerY = player.getY();
        int enemyX = this.getX();
        int enemyY = this.getY();
        int horizontalDistance = Math.abs(playerX - enemyX);
        int verticalDistance = Math.abs(playerY - enemyY); // 垂直距离
        
        
     // 新增调试日志，验证水平距离是否正确
//        System.out.println("水平距离：" + horizontalDistance + "，是否需要水平跑动：" );
        // 更新朝向（始终面向主角）
        isFacingRight = playerX > enemyX;


        // 判断是否需要跑动（水平或垂直距离超出阈值）
        boolean needHorizontalRun = horizontalDistance > RUN_STOP_THRESHOLD;
        boolean needVerticalRun = verticalDistance > VERTICAL_TOLERANCE;
        isRunning = needHorizontalRun || needVerticalRun; // 任一方向需要调整则跑动
        isAttacking = !isRunning; // 跑动时不攻击

        // 执行移动和动画更新
        move(gameTime);
        updateImage(gameTime);
        
    }
    
 // 新增：播放死亡音效的方法
    private void playDeathSound() {
        try {
            // 播放music/die.wav（步兵和炮兵共用的死亡音效）
            File deathFile = new File("music/die.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(deathFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start(); // 播放音效
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace(); // 打印异常（不影响主逻辑）
        }
    }

    
    @Override
    protected void updateImage(long gameTime) {
    	if (this.hp <= 0 && !this.isDying) {
            this.isDying = true;
            this.isAttacking = false;
            this.dieStartTime = gameTime;
            this.dieFrameIndex = 0;
            // 注意：不要在这里设置setLive(false)，死亡动画完成后由isLive()自然判定为不存活
            return;
        }

        // 1. 死亡状态（最高优先级）
    	if (isDying) {
            String dieKey = isFacingRight ? "enemy_die_right" : "enemy_die_left";
            List<ImageIcon> dieImages = GameLoad.imgMaps.get(dieKey);
            if (dieImages == null || dieImages.isEmpty()) {
                System.err.println("错误：死亡动画资源 " + dieKey + " 不存在！");
                return;
            }

            // 关键修改：检查死亡动画是否已完成
            if (dieFrameIndex >= dieImages.size()) {
                // 动画已完成，保持最后一帧
                ImageIcon finalFrame = dieImages.get(dieImages.size() - 1);
                this.setIcon(finalFrame);
                return;
            }

            // 播放死亡动画帧
            if (gameTime - dieStartTime >= DIE_FRAME_INTERVAL) {
                dieStartTime = gameTime;
                ImageIcon currentFrame = dieImages.get(dieFrameIndex);
                this.setIcon(currentFrame);
                this.setW(currentFrame.getIconWidth());
                this.setH(currentFrame.getIconHeight());
                this.setY(standBottomY - currentFrame.getIconHeight()); // 保持底部对齐
                
                dieFrameIndex++; // 移动到下一帧
            }
            return;
        }

        // 2. 跑动状态（次高优先级，覆盖攻击）
        if (isRunning) {
            String runKey = isFacingRight ? "enemy_run_right" : "enemy_run_left";
            List<ImageIcon> runImages = GameLoad.imgMaps.get(runKey);
            if (runImages == null || runImages.isEmpty()) {
                System.err.println("错误：跑动动画资源 " + runKey + " 不存在！");
                return;
            }

            if (gameTime - lastRunFrameTime >= RUN_FRAME_INTERVAL) {
                lastRunFrameTime = gameTime;
                runFrameIndex = (runFrameIndex + 1) % runImages.size();
                ImageIcon currentFrame = runImages.get(runFrameIndex);
                this.setIcon(currentFrame);
                this.setW(currentFrame.getIconWidth());
                this.setH(currentFrame.getIconHeight());
            }
            return; // 跑动时不执行后续攻击逻辑
        }

        // 3. 攻击状态（最低优先级，仅在非跑动、非死亡时执行）
        if (isAttacking) {
            String attackKey = isFacingRight ? "enemy_attack_right" : "enemy_attack_left";
            List<ImageIcon> attackImages = GameLoad.imgMaps.get(attackKey);
            if (attackImages == null || attackImages.isEmpty()) {
                System.err.println("错误：攻击动画资源 " + attackKey + " 不存在！");
                return;
            }

            boolean isReloadFrame = (attackFrameIndex == 0);
            long frameInterval = isReloadFrame ? RELOAD_INTERVAL : FRAME_INTERVAL;

            if (gameTime - lastFrameTime >= frameInterval) {
                lastFrameTime = gameTime;
                attackFrameIndex = (attackFrameIndex + 1) % attackImages.size();
                ImageIcon currentFrame = attackImages.get(attackFrameIndex);
                this.setIcon(currentFrame);
                this.setW(currentFrame.getIconWidth());
                this.setH(currentFrame.getIconHeight());

                // 仅攻击状态下触发射击
                if (attackFrameIndex == FIRE_FRAME_INDEX) {
                    fireBullet();
                }
            }
        }
    }
    
    @Override
    public boolean isLive() {
        // 1. 非死亡状态：使用基类的存活状态（默认初始为true，未被击中时保持存活）
        if (!isDying) {
            return super.isLive();
        } 
        // 2. 死亡状态：动画未播放完毕前视为存活（避免提前消失）
        else {
            String dieKey = isFacingRight ? "enemy_die_right" : "enemy_die_left";
            List<ImageIcon> dieImages = GameLoad.imgMaps.get(dieKey);
            // 容错：如果动画资源不存在，直接判定为不存活
            if (dieImages == null) {
                return false;
            }
            // 动画未完成则存活，完成后则不存活
            return dieFrameIndex < dieImages.size();
        }
    }

    // 新增：检查死亡动画是否已结束
    public boolean isDieAnimationFinished() {
        return isDying && dieFrameIndex >= GameLoad.imgMaps.get(
            isFacingRight ? "enemy_die_right" : "enemy_die_left").size();
    }
    
    // 发射子弹方法
    protected void fireBullet() {
        long currentTime = System.currentTimeMillis();
        
        // 控制发射频率，避免过于频繁
        if (currentTime - lastFireTime < FIRE_INTERVAL) {
            return;
        }
        
        lastFireTime = currentTime;
        
        // 计算子弹初始位置（从敌人枪口发射）
        int bulletX, bulletY;
        // 子弹Y坐标（垂直居中）
        // 原逻辑：子弹Y坐标为敌人垂直居中 - 5
        // 新逻辑：额外减10（向上移动10像素，可根据需要调整数值
        bulletY = this.getY() + (this.getH() / 2) - 5 - 10; // 微调，使子弹从枪口发出
        
        if (isFacingRight) {
            // 向右射击，子弹从敌人右侧发出
            bulletX = this.getX() + this.getW() - 5;
        } else {
            // 向左射击，子弹从敌人左侧发出
            bulletX = this.getX() + 5;
        }
        
        // 创建子弹（方向参数：1为右，0为左）
        String bulletParam = bulletX + "," + bulletY + "," + (isFacingRight ? "1" : "0");
        ElementObj bullet = new EnemyFile().createElement(bulletParam);
        
        // 添加子弹到游戏管理器
        if (bullet != null) {
            // 修改：将ENEMY_FILE改为ENEMYFILE（与枚举一致）
            ElementManager.getManager().addElement(bullet, GameElement.ENEMYFILE);
            System.out.println("敌人发射子弹：位置=" + bulletX + "," + bulletY + 
                              "，方向=" + (isFacingRight ? "右" : "左"));
        }
    }
    
 // 添加受击方法（供碰撞检测调用）
    public void takeHit() {
        this.isHit = true;
        this.hitTime = System.currentTimeMillis();
    }

    // 预留接口：攻击动画更新（后续扩展用）
    protected void updateAttackFrame(long gameTime) {
        // 后续实现：根据时间切换帧索引（attackFrameIndex）
    }

    // 预留接口：移动逻辑
    @Override
    protected void move(long time) {
        super.move(time);

        if (isRunning && !isDying) {
            List<ElementObj> players = ElementManager.getManager().getElementsByKey(GameElement.PLAY);
            if (players.isEmpty()) return;
            ElementObj player = players.get(0);
            int playerY = player.getY();
            int enemyY = this.getY();
            int enemyX = this.getX();

            // 水平移动（直接使用整数速度，无浮点数转换）
            if (isFacingRight) {
                // 向右移动：不超过窗口右侧（800为窗口宽度）
                if (enemyX < 800 - this.getW()) { 
                    this.setX(enemyX + RUN_SPEED); // 直接加整数速度
                }
            } else {
                // 向左移动：不超过窗口左侧
                if (enemyX > 0) { 
                    this.setX(enemyX - RUN_SPEED); // 直接减整数速度
                }
            }

            // 垂直移动（直接使用整数速度）
            if (playerY > enemyY && enemyY < 450) {
                this.setY(enemyY + VERTICAL_SPEED);
            } else if (playerY < enemyY && enemyY > 100) {
                this.setY(enemyY - VERTICAL_SPEED);
            }

            // 更新底部位置
            this.standBottomY = this.getY() + this.getH();
        }
    }
    // 预留接口：血量操作
    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    // 预留接口：攻击控制
    public void startAttack() {
        this.isAttacking = true;
    }

    public void stopAttack() {
        this.isAttacking = false;
    }
    @Override
    public void die() {
        // 空实现：不执行任何操作，阻止父类ElementObj.die()中的音效播放
    }

}
