package show;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import controller.GameListener;
import controller.GameThread;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;

// 选关面板：仅包含3个关卡按钮
public class SelectLevelPanel extends JPanel implements ActionListener {
    private GameJFrame mainFrame;
    private Image backgroundImage; // 用于存储背景图

    public SelectLevelPanel(GameJFrame frame) {
        this.mainFrame = frame;
        this.setLayout(null);
        initButtons();
        // 加载背景图
        loadBackgroundImage();
    }

    // 加载背景图的方法
    private void loadBackgroundImage() {
        try {
            // 使用绝对路径，从类路径根目录开始查找
            java.net.URL imgUrl = getClass().getResource("/images/ChooseBackground.jpg");
            
            if (imgUrl == null) {
                System.err.println("错误：无法找到图片资源 /images/ChooseBackground.jpg");
                System.err.println("当前类路径：" + getClass().getResource(""));
                System.err.println("类路径根目录：" + getClass().getResource("/"));
                return;
            }
            
            backgroundImage = ImageIO.read(imgUrl);
            System.out.println("成功加载背景图片");
        } catch (IOException e) {
            System.err.println("读取图片时发生IO错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 初始化A/B/C三个关卡按钮
    private void initButtons() {
        // 关卡A（对应原关卡1）
        JButton btnA = new JButton("关卡A");
        btnA.setBounds(300, 150, 200, 50);
        btnA.setActionCommand("1");
        btnA.addActionListener(this);

        // 关卡B（对应原关卡2）
        JButton btnB = new JButton("关卡B");
        btnB.setBounds(300, 250, 200, 50);
        btnB.setActionCommand("2");
        btnB.addActionListener(this);

        // 关卡C（对应原关卡3）
        JButton btnC = new JButton("关卡C");
        btnC.setBounds(300, 350, 200, 50);
        btnC.setActionCommand("3");
        btnC.addActionListener(this);

        this.add(btnA);
        this.add(btnB);
        this.add(btnC);
    }

    // 点击按钮后进入对应关卡
    @Override
    public void actionPerformed(ActionEvent e) {
        // 1. 设置关卡索引（A=1, B=2, C=3）
        GameThread.index = Integer.parseInt(e.getActionCommand()) - 1;
        
        // 2. 切换到游戏面板并启动
        mainFrame.startGame();
    }

    // 重写paintComponent方法绘制背景图
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // 如果背景图加载失败，绘制一个默认的背景色
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(java.awt.Color.RED);
            g.drawString("背景图加载失败", getWidth()/2 - 50, getHeight()/2);
        }
    }
}