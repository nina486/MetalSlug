//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import element.ElementObj;

public class ElementManager {
    private Map<GameElement, List<ElementObj>> gameElements;
    private static ElementManager EM = null;

    public Map<GameElement, List<ElementObj>> getGameElements() {
        return this.gameElements;
    }

    public void addElement(ElementObj obj, GameElement ge) {
        ((List)this.gameElements.get(ge)).add(obj);
    }

    public List<ElementObj> getElementsByKey(GameElement ge) {
        return (List)this.gameElements.get(ge);
    }

    public static synchronized ElementManager getManager() {
        if (EM == null) {
            EM = new ElementManager();
        }

        return EM;
    }

    private ElementManager() {
        this.init();
    }

    public void init() {
        this.gameElements = new HashMap();
        GameElement[] var4;
        int var3 = (var4 = GameElement.values()).length;

        for(int var2 = 0; var2 < var3; ++var2) {
            GameElement ge = var4[var2];
            this.gameElements.put(ge, new ArrayList());
        }

    }
}
