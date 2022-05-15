package real.skill;

import org.json.simple.JSONObject;

public class Skill {
	private SkillTemplate template;
	private long skillID;
	private long point;
	private long powRequire;
	private long coolDown;
	private long lastTimeUseThisSkill;
	private long dx;
	private long dy;
	private long maxFight;
	private long manaUse;
	private Object options;
	private boolean paintCanNotUseSkill;
	private long damage;
	private String moreInfo;
	private long price;


	public  static JSONObject ObjectSkill(Skill skill){
		JSONObject put= new JSONObject();
		put.put((Object) "id",(Object) skill.getSkillID());
		put.put((Object) "point",(Object) skill.getPoint());
		return put;
	}
	public long getSkillID() { return skillID; }
	public void setSkillID(long value) { this.skillID = value; }

	public long getPoint() { return point; }
	public void setPoint(long value) { this.point = value; }

	public long getPowRequire() { return powRequire; }
	public void setPowRequire(long value) { this.powRequire = value; }

	public long getCoolDown() { return coolDown; }
	public void setCoolDown(long value) { this.coolDown = value; }

	public long getLastTimeUseThisSkill() { return lastTimeUseThisSkill; }
	public void setLastTimeUseThisSkill(long value) { this.lastTimeUseThisSkill = value; }

	public long getDx() { return dx; }
	public void setDx(long value) { this.dx = value; }

	public long getDy() { return dy; }
	public void setDy(long value) { this.dy = value; }

	public long getMaxFight() { return maxFight; }
	public void setMaxFight(long value) { this.maxFight = value; }

	public long getManaUse() { return manaUse; }
	public void setManaUse(long value) { this.manaUse = value; }

	public Object getOptions() { return options; }
	public void setOptions(Object value) { this.options = value; }
	public SkillTemplate getSkillTemplate() { return template; }
	public void setSkillTemplate(SkillTemplate value) { this.template = value; }
	public boolean getPaintCanNotUseSkill() { return paintCanNotUseSkill; }
	public void setPaintCanNotUseSkill(boolean value) { this.paintCanNotUseSkill = value; }

	public long getDamage() { return damage; }
	public void setDamage(long value) { this.damage = value; }

	public String getMoreInfo() { return moreInfo; }
	public void setMoreInfo(String value) { this.moreInfo = value; }

	public long getPrice() { return price; }
	public void setPrice(long value) { this.price = value; }
        
}
