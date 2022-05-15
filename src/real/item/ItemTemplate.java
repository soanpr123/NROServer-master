package real.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class ItemTemplate {

    public int id;
    public int skill;
    public byte type;

    public byte gender;

    public String name;

    public String description;
    
    public byte level;
    
    public long expires = 0;
    
    public int iconID;

    public short part;

    public boolean isUpToUp;

    public int strRequire;
    public ArrayList<ItemOption> itemoption = new ArrayList<>();
    public static ArrayList<ItemTemplate> entrys = new ArrayList<ItemTemplate>();
    private static HashMap<Integer, ItemOptionTemplate> options = new HashMap<Integer, ItemOptionTemplate>();
    public static void put(int id, ItemOptionTemplate option) {
        ItemTemplate.options.put(id, option);
    }
    public static Collection<ItemOptionTemplate> getOptions() {
        return ItemTemplate.options.values();
    }
    public static JSONObject ObjectItem(Item item, int index) {
        JSONObject put = new JSONObject();
        put.put((Object)"index", (Object)index);
        put.put((Object)"id", (Object)item.id);
        put.put((Object)"quantity", (Object)item.quantity);
        JSONArray option = new JSONArray();
        for (ItemOption Option : item.itemOptions) {
            JSONObject pa = new JSONObject();
            pa.put((Object)"id", (Object)Option.id);
            pa.put((Object)"param", (Object)Option.param);
            option.add((Object)pa);
        }
        put.put((Object)"option", (Object)option);
        return put;
    }
    
    public static Item parseItem(String str) {
        Item item = new Item();
        JSONObject job = (JSONObject) JSONValue.parse(str);
        item.id = Short.parseShort(job.get((Object)"id").toString());
        item.quantity = Short.parseShort(job.get((Object)"quantity").toString());
        item.template = ItemTemplate.ItemTemplateID(item.id);
        JSONArray Option = (JSONArray)JSONValue.parse(job.get((Object)"option").toString());
        for (Object Option2 : Option) {
            JSONObject job2 = (JSONObject)Option2;
            ItemOption option = new ItemOption(Integer.parseInt(job2.get((Object)"id").toString()), Integer.parseInt(job2.get((Object)"param").toString()));
            item.itemOptions.add(option);
        }
        return item;
    }
    public static ItemTemplate ItemTemplateID(int id) {
        for (ItemTemplate entry : entrys) {
            if(entry.id == id)
            {
                return entry;
            }
        }
        return null;
    }
  
}
