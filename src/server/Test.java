package server;

import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.player.Player;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        ItemData.loadDataItem();
        Player pl = new Player();
        pl.id = 1;


        //--------itemBag---------
        Item[] itemsBag = pl.ItemBody;
//        System.out.println(itemsBag.length);
        for (Item item : itemsBag) {
            if (item.id == -1) {
                System.out.println(-1);
            } else {
                System.out.println(item.template.id);
                System.out.println(item.quantity);
                System.out.println(item.getInfo());
                System.out.println(item.getContent());
                ArrayList<ItemOption> itemOptions = item.itemOptions;
                System.out.println(itemOptions.size());
                for (ItemOption itemOption : itemOptions) {
                    System.out.println(itemOption.optionTemplate.id);
                    System.out.println(itemOption.param);
                }
            }

        }
    }

}
