package real.skill;

import real.player.Option;

import java.util.ArrayList;

public class SkillOptionTemplate {

    public int id;

    public String name;
    public byte point;
    public int level;
    public short manaUse;
    public int coolDown;
    public short dx;
    public short dy;
    public byte maxFight;
    public ArrayList<Option> options;
    public SkillOptionTemplate() {
        this.options = new ArrayList<Option>();
    }
}
