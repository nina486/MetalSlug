package element;

public interface DistanceAware {
    // 更新与主角的距离
    void updateDistance(int distance);
    
    // 获取当前状态
    String getState();
}