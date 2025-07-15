package element;

import java.util.List;
import javax.swing.ImageIcon;
import manager.ElementManager;
import manager.GameElement;
import manager.GameLoad;

public class ArtilleryEnemy extends Enemy {
    // 炮兵特有属性（重写父类常量，实现差异化）
    private static final int MAX_HP = 100; // 炮兵血量更高
 // 移除固定的FIRE_INTERVAL，改用动画周期控制
//    private static final long FIRE_INTERVAL = 2000; // 发射间隔更长（1秒）
    private static final int ATTACK_RANGE = 400; // 攻击范围更大
    private static final int RUN_SPEED = 1; // 跑动速度更慢
    private static final int FIRE_FRAME_INDEX = 2; // 发射子弹的帧索引

 // 移除冗余的canFire，改用动画周期控制
    private boolean hasFiredInThisCycle = false; // 标记当前动画周期是否已发射
    
 // 新增：死亡时的固定底部位置（死亡瞬间的脚部位置，不再变化）
    private int dieBottomY; 
    
    // 构造方法
    public ArtilleryEnemy() {
        this.hp = MAX_HP; // 初始化炮兵血量
    }

 // 1. 修正初始化方法：确保加载炮兵初始动画
    @Override
    public ElementObj createElement(String str) {
        String[] split = str.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);

        // 炮兵初始动画key（必须与GameLoad加载的key一致）
        String initKey = "artillery_attack_left"; 
        List<ImageIcon> attackImages = GameLoad.imgMaps.get(initKey);

        if (attackImages == null || attackImages.isEmpty()) {
            System.err.println("错误：炮兵攻击动画 " + initKey + " 资源缺失！请检查EnemyData.pro配置");
            this.setIcon(new ImageIcon());
        } else {
            ImageIcon firstIcon = attackImages.get(0);
            this.setIcon(firstIcon);
            this.setX(x);
            this.setY(y);
            this.setW(firstIcon.getIconWidth());
            this.setH(firstIcon.getIconHeight());
            this.standBottomY = y + firstIcon.getIconHeight();
            System.out.println("炮兵初始化成功：使用" + initKey + "第0帧，位置(" + x + "," + y + ")");
        }

        this.hp = MAX_HP; // 炮兵血量100
        this.isAttacking = true;
        return this;
    }

 // 2. 修正动画更新方法：强制使用炮兵专属动画
    @Override
    protected void updateImage(long gameTime) {
    	if (isDying) {
            // 首次进入死亡状态时，记录当前底部位置（仅执行一次）
            if (dieFrameIndex == 0) { 
                // 死亡瞬间的底部位置 = 当前Y（顶部） + 当前高度（站立状态）
                dieBottomY = this.getY() + this.getH(); 
                System.out.println("死亡开始：固定底部位置=" + dieBottomY);
            }

            String dieKey = isFacingRight ? "artillery_die_right" : "artillery_die_left";
            List<ImageIcon> dieImages = GameLoad.imgMaps.get(dieKey);
            if (dieImages == null || dieImages.isEmpty()) {
                System.err.println("错误：炮兵死亡动画 " + dieKey + " 资源缺失！");
                return;
            }

            // 死亡动画播放完毕
            if (dieFrameIndex >= dieImages.size()) {
                ImageIcon finalFrame = dieImages.get(dieImages.size() - 1);
                this.setIcon(finalFrame);
                // 用固定底部位置计算最终帧Y坐标
                this.setY(dieBottomY - finalFrame.getIconHeight()); 
                this.setW(finalFrame.getIconWidth());
                this.setH(finalFrame.getIconHeight());
                return;
            }

            // 死亡帧切换（核心修复：用固定底部位置计算Y）
            if (gameTime - dieStartTime >= DIE_FRAME_INTERVAL) {
                dieStartTime = gameTime;
                ImageIcon currentFrame = dieImages.get(dieFrameIndex);
                
                // 关键：Y坐标 = 死亡时的固定底部位置 - 当前死亡帧高度
                this.setY(dieBottomY - currentFrame.getIconHeight()); 
                this.setIcon(currentFrame);
                this.setW(currentFrame.getIconWidth());
                this.setH(currentFrame.getIconHeight());
                
                dieFrameIndex++;
                System.out.println("死亡帧" + dieFrameIndex + "：Y=" + this.getY() + "，底部=" + dieBottomY);
            }
            return;
        }

        // 跑动状态：强制使用炮兵跑动key
        if (isRunning) {
            String runKey = isFacingRight ? "artillery_run_right" : "artillery_run_left";
            List<ImageIcon> runImages = GameLoad.imgMaps.get(runKey);
            if (runImages == null || runImages.isEmpty()) {
                System.err.println("错误：炮兵跑动动画 " + runKey + " 资源缺失！");
                return;
            }

            if (gameTime - lastRunFrameTime >= RUN_FRAME_INTERVAL) {
                lastRunFrameTime = gameTime;
                runFrameIndex = (runFrameIndex + 1) % runImages.size();
                ImageIcon currentFrame = runImages.get(runFrameIndex);
                this.setIcon(currentFrame);
                // 修复图片过大：更新宽高
                this.setW(currentFrame.getIconWidth());
                this.setH(currentFrame.getIconHeight());
            }
            return;
        }

        // 攻击状态（仅非跑动、非死亡时执行）
        if (isAttacking) {
            String attackKey = isFacingRight ? "artillery_attack_right" : "artillery_attack_left";
            List<ImageIcon> attackImages = GameLoad.imgMaps.get(attackKey);
            if (attackImages == null || attackImages.isEmpty()) {
                System.err.println("错误：炮兵攻击动画 " + attackKey + " 资源缺失！");
                return;
            }

            // 攻击帧切换逻辑
            boolean isReloadFrame = (attackFrameIndex == 0);
            long frameInterval = isReloadFrame ? RELOAD_INTERVAL : FRAME_INTERVAL;

            if (gameTime - lastFrameTime >= frameInterval) {
                lastFrameTime = gameTime;
                int oldFrameIndex = attackFrameIndex; // 记录切换前的帧索引
                attackFrameIndex = (attackFrameIndex + 1) % attackImages.size(); // 切换到下一帧

                // 关键修复：仅在“从非发射帧进入发射帧”时，才触发一次发射
                if (attackFrameIndex == FIRE_FRAME_INDEX && oldFrameIndex != FIRE_FRAME_INDEX) {
                    fireBullet(); // 仅一次触发
                }

                // 更新当前攻击帧图片
                ImageIcon currentFrame = attackImages.get(attackFrameIndex);
                this.setIcon(currentFrame);
                this.setW(currentFrame.getIconWidth());
                this.setH(currentFrame.getIconHeight());
            }
        }
    }
    // 重写子弹发射：使用炮兵专用子弹
    @Override
    protected void fireBullet() {
        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastFireTime < FIRE_INTERVAL) {
//            return; // 控制发射频率（炮兵更慢）
//        }
        lastFireTime = currentTime;

        // 计算炮弹初始位置（炮兵枪口位置，微调偏移）
        int bulletX = isFacingRight ? this.getX() + this.getW() - 10 : this.getX() + 10;
        int bulletY = this.getY() + (this.getH() / 2) -20; // 炮弹位置略低于步兵

        // 创建炮兵子弹（参数：位置+方向）
        String bulletParam = bulletX + "," + bulletY + "," + (isFacingRight ? "1" : "0");
        ElementObj bullet = new ArtilleryBullet().createElement(bulletParam);
        if (bullet != null) {
            ElementManager.getManager().addElement(bullet, GameElement.ENEMYFILE);
            System.out.println("炮兵发射炮弹：位置=" + bulletX + "," + bulletY);
        }
    }

 // 3. 修正移动逻辑：确保跑动时坐标正确更新
    @Override
    protected void move(long time) {
    	// 死亡状态下不执行移动逻辑，也不更新底部位置
        if (isDying) { 
            return;
        }
    	
        if (isRunning && !isDying) {
            List<ElementObj> players = ElementManager.getManager().getElementsByKey(GameElement.PLAY);
            if (players.isEmpty()) return;
            ElementObj player = players.get(0);

            // 水平移动（炮兵速度更慢）
            int targetX = player.getX();
            if (isFacingRight && this.getX() < targetX - 50) { // 保持距离
                this.setX(this.getX() + 1); // 每次移动1像素（慢于步兵）
            } else if (!isFacingRight && this.getX() > targetX + 50) {
                this.setX(this.getX() - 1);
            } else {
                isRunning = false; // 到达攻击范围，停止跑动
                isAttacking = true; // 切换到攻击状态
            }

            // 垂直对齐（微调Y坐标）
            int targetY = player.getY();
            if (this.getY() < targetY - 10) {
                this.setY(this.getY() + 1);
            } else if (this.getY() > targetY + 10) {
                this.setY(this.getY() - 1);
            }
        }
    }
}