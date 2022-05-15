package real.skill;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import real.item.Item;
import real.item.ItemOption;

import java.util.ArrayList;

public class SkillTemplate {

    public boolean isBuffToPlayer() {
        return this.type == 2;
    }

    public boolean isUseAlone() {
        return this.type == 3;
    }

    public boolean isAttackSkill() {
        return this.type == 1;
    }

    private long id;
    private long classID;
    private String name;
    private long maxPoint;
    private long manaUseType;
    private long type;
    private long iconID;
    private String[] description;
    private Skill[] skills;
    private String damInfo;

    public static JSONObject ObjectSkill(Skill skill) {
        JSONObject put = new JSONObject();
        put.put((Object)"id", (Object)skill.getSkillTemplate().getID());
        put.put((Object)"point", (Object)skill.getPoint());
        return put;
    }

    public long getID() { return id; }
    public void setID(long value) { this.id = value; }

    public long getClassID() { return classID; }
    public void setClassID(long value) { this.classID = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public long getMaxPoint() { return maxPoint; }
    public void setMaxPoint(long value) { this.maxPoint = value; }

    public long getManaUseType() { return manaUseType; }
    public void setManaUseType(long value) { this.manaUseType = value; }

    public long getType() { return type; }
    public void setType(long value) { this.type = value; }

    public long getIconID() { return iconID; }
    public void setIconID(long value) { this.iconID = value; }

    public String[] getDescription() { return description; }
    public void setDescription(String[] value) { this.description = value; }

    public Skill[] getSkills() { return skills; }
    public void setSkills(Skill[] value) { this.skills = value; }

    public String getDamInfo() { return damInfo; }
    public void setDamInfo(String value) { this.damInfo = value; }
}
