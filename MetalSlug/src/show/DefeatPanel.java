package show;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.IOException;

import controller.GameThread;

public class DefeatPanel extends JPanel implements ActionListener {
    private GameJFrame mainFrame;
    private Image backgroundImage; // 用于存储背景图

    public DefeatPanel(GameJFrame frame) {
        this.mainFrame = frame;
        this.setLayout(null);
        this.setFocusable(true);
        this.setRequestFocusEnabled(true);
        this.setSize(GameJFrame.GameX, GameJFrame.GameY); // 设置面板大小
        loadBackgroundImage(); // 加载背景图
        initComponents();
    }

    // 加载背景图的方法
    private void loadBackgroundImage() {
        try {
            // 使用绝对路径，从类路径根目录开始查找
            java.net.URL imgUrl = getClass().getResource("/images/lose.jpg");
            
            if (imgUrl == null) {
                System.err.println("错误：无法找到图片资源 /images/lose.jpg");
                return;
            }
            
            backgroundImage = ImageIO.read(imgUrl);
            System.out.println("成功加载失败界面背景图片");
        } catch (IOException e) {
            System.err.println("读取图片时发生IO错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initComponents() {
        // 失败标题
        JLabel title = new JLabel("游戏失败！");
        title.setFont(new Font("宋体", Font.BOLD, 40));
        title.setForeground(java.awt.Color.RED);
        title.setBounds(315, 220, 300, 60);
        this.add(title);

        // 关卡信息
        JLabel levelInfo = new JLabel("你输了");
        levelInfo.setFont(new Font("宋体", Font.PLAIN, 20));
        levelInfo.setForeground(java.awt.Color.RED); // 设置文字为红色
        levelInfo.setBounds(370, 280, 200, 30);
        this.add(levelInfo);

        // 返回选关按钮
        JButton backBtn = new JButton("返回选关");
        backBtn.setBounds(300, 350, 200, 50);
        backBtn.addActionListener(this);
        this.add(backBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mainFrame.backToSelect();
        mainFrame.setSelectPanelFocus();
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