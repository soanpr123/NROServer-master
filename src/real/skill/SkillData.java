package real.skill;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import server.FileIO;
import server.Util;

public class SkillData {

    public static NClass[] nClasss;

    private static SkillOptionTemplate[] sOptionTemplates;

    public static void createSkill() {
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(FileIO.readFile("data/NRskill_v5"));
            DataInputStream dis = new DataInputStream(is);
            dis.readByte();
            dis.readByte();
            sOptionTemplates = new SkillOptionTemplate[(int) dis.readByte()];
            for (int i = 0; i < sOptionTemplates.length; i++) {
                sOptionTemplates[i] = new SkillOptionTemplate();
                sOptionTemplates[i].id = i;
                sOptionTemplates[i].name = dis.readUTF();
            }
            nClasss = new NClass[dis.readByte()];
            for (int j = 0; j < nClasss.length; j++) {
                nClasss[j] = new NClass();
                nClasss[j].setClassID(j);
                nClasss[j].setName(dis.readUTF());
                nClasss[j].setSkillTemplates(new SkillTemplate[(int) dis.readByte()]);
                for (int k = 0; k < nClasss[j].getSkillTempLates().length; k++) {
                    nClasss[j].getSkillTempLates()[k] = new SkillTemplate();
                    nClasss[j].getSkillTempLates()[k].setID( dis.readByte());
                    nClasss[j].getSkillTempLates()[k].setName(dis.readUTF());
                    nClasss[j].getSkillTempLates()[k].setMaxPoint( (int) dis.readByte());
                    nClasss[j].getSkillTempLates()[k].setManaUseType((int) dis.readByte());
                    nClasss[j].getSkillTempLates()[k].setType((int) dis.readByte());
                    nClasss[j].getSkillTempLates()[k].setIconID((int)  dis.readShort());
                    nClasss[j].getSkillTempLates()[k].setDamInfo(dis.readUTF());
                    /*nClasss[j].skillTemplates[k].description = */dis.readUTF();
                    nClasss[j].getSkillTempLates()[k].setSkills(new Skill[(int) dis.readByte()]);
                    for (int l = 0; l < nClasss[j].getSkillTempLates()[k].getSkills().length; l++) {
                        nClasss[j].getSkillTempLates()[k].getSkills()[l] = new Skill();
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setSkillID(dis.readShort());

                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setPoint( (int) dis.readByte());
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setPowRequire( dis.readLong());
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setManaUse((int) dis.readShort());
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setCoolDown( dis.readInt());
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setDx((int) dis.readShort());
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setDy( (int) dis.readShort());
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setMaxFight((int) dis.readByte());
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setDamage( dis.readShort());
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setPrice( dis.readShort());
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setMoreInfo(dis.readUTF());
                        nClasss[j].getSkillTempLates()[k].getSkills()[l].setSkillTemplate(nClasss[j].getSkillTempLates()[k]);
                        Util.log("sadasdasdasdasd "+ nClasss[j].getSkillTempLates()[k].getSkills()[l].getSkillTemplate().getType());
//                        Skills.add(nClasss[j].getSkillTempLates()[k].getSkills()[l]);
                    }
                }
                Util.log("finish createSkill->>>>>>>>>>>" +dis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
