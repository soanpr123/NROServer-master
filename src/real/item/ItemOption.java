package real.item;

import server.Manager;
import server.Util;

public class ItemOption {
    public int id;
    public int param;
    public boolean isExpires = false;
    public boolean isUpToUp;
    public ItemOptionTemplate optionTemplate;

    public ItemOption() {
    }

    public ItemOption(int tempId, int param) {
        this.id = tempId;
        this.optionTemplate = Manager.iOptionTemplates.get(tempId);
        this.param = param;
    }

    public String getOptionString() {
        return Util.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
    }
    

}
