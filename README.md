java项目-MVC框架-合金弹头游戏  
eclipse  
JDK版本 1.8  
controller包控制层）  
  核心作用：负责游戏逻辑的调度、线程管理和交互控制，是连接 UI 层与数据层的桥梁。  
  主要类：GameThread（游戏主线程，控制关卡加载、运行、结束流程）、GameListener（监听键盘 / 鼠标输入，将操作传递给主角等元素）。  
  功能举例：  
    管理游戏线程的生命周期（启动、中断、切换关卡）。  
    处理碰撞检测（ElementPK方法）、角色操作响应（如移动、攻击）。  
element包（元素层）  
  核心作用：定义游戏中所有可见元素的属性和行为，是游戏世界的 “实体” 集合。  
  主要类：  
    基础元素：ElementObj（所有元素的父类，定义位置、绘制、移动等通用方法）。  
    具体元素：Play（主角）、Enemy（步兵）、ArtilleryEnemy（炮兵）、Boos（Boss）、MapObj（地图）、PlayFile（主角子弹）等。  
  功能举例：  
    每个元素重写showElement方法实现自身绘制，重写move方法实现移动逻辑。  
    包含属性（血量、速度、状态等）和行为（攻击、死亡、受击等）的封装。  
game包（入口层）  
  核心作用：存放游戏的启动类，是程序的入口点。  
manager包（管理层）  
  核心作用：负责游戏资源（图片、配置）的加载、元素的统一管理，是游戏的数据中枢。  
  主要类：  
    ElementManager（单例模式，管理所有游戏元素的添加、删除、查询，供绘制和碰撞检测使用）。   
    GameLoad（加载图片、地图、角色配置等资源，解析text包中的配置文件）。  
    GameElement（枚举类，定义元素类型：如PLAY、ENEMY、MAPS，用于分类管理元素）。  
  功能举例：  
    GameLoad.loadImg()加载所有角色图片到imgMaps集合，供绘制时调用。  
    ElementManager通过getElementsByKey方法提供某类元素（如所有敌人）的列表，方便碰撞检测和绘制。  
  show包（展示层 / UI 层）  
  核心作用：负责游戏界面的渲染和用户交互界面的展示，是玩家可见的视觉层。  
  主要类：  
    GameJFrame（主窗口，管理面板切换）。  
    GameMainJPanel（游戏主面板，重写paint方法绘制所有元素）。  
    SelectLevelPanel（选关面板）、VictoryPanel（胜利面板）、DefeatPanel（失败面板）。  
  功能举例：  
    GameMainJPanel通过遍历ElementManager中的元素，调用其showElement方法完成画面绘制。  
    面板间的切换（如从选关到游戏、从游戏到胜利界面）由GameJFrame控制。  
text包（配置层）  
  核心作用：存储游戏的配置文件（文本形式），用于解耦代码与数据，方便修改游戏参数。  
  主要文件：  
    map（地图配置，记录各关卡地图路径）。    
    GameData.pro（图片资源配置，记录角色动画帧的路径）。  
    EnemyData.pro（敌人动画配置，区分步兵、炮兵的图片路径）。  
    obj.pro（类映射配置，记录元素类的全路径，用于动态加载）。  
    
游戏画面展示  
<img width="1024" height="765" alt="image" src="https://github.com/user-attachments/assets/9a948fbb-4757-4ae8-94a6-7237c00c1633" />
<img width="1317" height="986" alt="image" src="https://github.com/user-attachments/assets/128e4fc8-e640-4246-8877-e9574fae9c96" />
<img width="1318" height="978" alt="image" src="https://github.com/user-attachments/assets/965842c4-4a30-45d3-bb74-bb19935a9c4d" />
<img width="848" height="629" alt="image" src="https://github.com/user-attachments/assets/d204e9db-fbfb-428f-b534-853e80e59c1c" />
<img width="1316" height="971" alt="image" src="https://github.com/user-attachments/assets/16a33396-6acb-40ad-abf2-67cfbf787dba" />

<img width="907" height="659" alt="image" src="https://github.com/user-attachments/assets/b7ee9d62-a9c1-4b4c-a98e-5975f9471469" />  

用4个键位对应4种方向，有8种不同的情况，移动灵活，还可以跳跃  
使用鼠标点击进行攻击，子弹可以根据鼠标的位置来移动  
增加了游戏背景音乐，开枪音效，敌人死亡音效  
主角的上下半身各自根据状态设置了不同的动画  
敌人和boss拥有自动索敌机制  
角色和敌人都有状态显示  
