package real.skill;

public class NClass {
    private long classID;
    private String name;
    private SkillTemplate[] skillTemplates;

    public long getClassID() { return classID; }
    public void setClassID(long value) { this.classID = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }
    public SkillTemplate[] getSkillTempLates() { return skillTemplates; }
    public SkillTemplate getSkillTemplate(int tempId){
        for (SkillTemplate skillTemplate : skillTemplates) {
            if (skillTemplate.getID() == tempId){
                return skillTemplate;
            }
        }
        return null;
    }
    public void setSkillTemplates(SkillTemplate[] value) { this.skillTemplates = value; }
}
