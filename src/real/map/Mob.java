package real.map;

import java.util.ArrayList;
import java.util.HashMap;

public class Mob {

    public int tempId;
    public int level;
    public boolean isFire;
    public byte sys;
    public long timeIce;
    public boolean isIce=false;
    public boolean isboss;
    public boolean isWind=false;
    public int hp;
    public long timeFire;
    public long timeFight;
    public boolean isDisable=false;
    public int maxHp;
    public short pointX;
    public short pointY;
    public long xpup;
    public byte status;
    public boolean isDie;
    public boolean isRefresh = true;
    public int dameFire;
    public long timeRefresh;
    public MobTemplate template;
    public Mob(int id,int idtemplate, int level) {
        this.tempId = id;
        this.template = MobTemplate.entrys.get(idtemplate);
        this.level = level;
        this.hp = maxHp = template.maxHp;
        this.xpup = 100000;
        this.isDie = false;
        this.dameFire = 0;
        this.isRefresh = true;
        this.timeFire=1L;
        this.level = level;
        this.xpup = 10000L;
        this.isDie = false;
        
    }
    public static MobTemplate getMob(int id) {
        for (MobTemplate mob : MobTemplate.entrys) {
            if (mob.tempId == id) {
                return mob;
            }
        }
        return null;
    }
    public void updateHP(int num) {
        hp += num;
        if (hp <= 0) {            
            hp = 0;
            status = 0;
            isDie = true;
            if (isRefresh) {
                timeRefresh = System.currentTimeMillis()+ 5000;
            }
        }
    }
    public boolean isDisable() {
        return this.isDisable;
    }
    public void update() {

    }
}
