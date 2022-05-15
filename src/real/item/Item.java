package real.item;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import real.player.Player;

public class Item {
    public int id;
    public ItemTemplate template;
    public String info;
    public String content;
    public int quantity;
    public boolean isExpires = false;
    public boolean isUpToUp;
    public int quantityTemp = 1;
    public short headTemp;
    public short bodyTemp;
    public short legTemp;
    public int idTemp;
    public ArrayList<ItemOption> itemOptions;
    public static ArrayList<Item> entrys = new ArrayList<Item>();
    public Item() {
        this.id = -1;
        this.quantity = 1;
        this.itemOptions = new ArrayList<ItemOption>();
    }
    public String getInfo() {
        String strInfo = "";
        for (ItemOption itemOption : itemOptions) {
            strInfo += itemOption.getOptionString();
        }
        return strInfo;
    }

    public int getParamItemByID(int id)
    {
        for (ItemOption itemOption : itemOptions) {
            if(itemOption.id == id){
                return itemOption.param;
            }
        }
        return 0;
    }
    public String getContent() {
        return "Yêu cầu sức mạnh " + this.template.strRequire + " trở lên";
    }

}
