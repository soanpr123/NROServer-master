/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import cache.Part;
import cache.PartImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import real.item.Item;
import real.item.ItemOption;
import real.item.ItemOptionTemplate;
import real.item.ItemSell;
import real.item.ItemTemplate;
import real.item.Shop;
import real.item.TabItemShop;
import real.map.Map;
import real.map.MapData;
import real.map.MapTemplate;
import real.map.Mob;
import real.map.MobTemplate;
import real.map.Npc;
import real.map.WayPoint;
import real.player.Player;
import static server.SQLManager.conn;

import real.skill.EffectTeamPlate;
import server.io.Message;
import server.GameScr;
public class Manager {

    public int port;
    public String host;
    public static String mysql_part;
    public static String backup_part;
    public static String mysql_host;
    public static int mysql_port;
    public static String mysql_database;
    public static String mysql_user;
    public static String mysql_pass;
    byte vsData;
    byte vsMap;
    byte vsSkill;
    byte vsItem;
    public static ArrayList<Part> parts;
    static Server server = Server.gI();
    public static ArrayList<ItemOptionTemplate> iOptionTemplates=new  ArrayList<ItemOptionTemplate>();
    public Manager() {

        this.loadConfigFile();
        this.loadDataBase();
    }
 
    private void loadConfigFile() {
        byte[] ab = GameScr.loadFile("ninja.conf").toByteArray();
        if (ab == null) {
            System.out.println("Config file not found!");
            System.exit(0);
        }

        String data = new String(ab);
        HashMap<String, String> configMap = new HashMap();
        StringBuilder sbd = new StringBuilder();
        boolean bo = false;

        for (int i = 0; i <= data.length(); ++i) {
            char es;
            if (i != data.length() && (es = data.charAt(i)) != '\n') {
                if (es == '#') {
                    bo = true;
                }

                if (!bo) {
                    sbd.append(es);
                }
            } else {
                bo = false;
                String sbf = sbd.toString().trim();
                if (sbf != null && !sbf.equals("") && sbf.charAt(0) != '#') {
                    int j = sbf.indexOf(58);
                    if (j > 0) {
                        String key = sbf.substring(0, j).trim();
                        String value = sbf.substring(j + 1).trim();
                        configMap.put(key, value);
                        System.out.println("config: " + key + "-" + value);
                    }
                }
                sbd.setLength(0);
            }
        }

        if (configMap.containsKey("host")) {
            this.host = (String) configMap.get("host");
        } else {
            this.host = "localhost";
        }

        if (configMap.containsKey("port")) {
            this.port = Integer.parseInt((String) configMap.get("port"));
        } else {
            this.port = 14445;
        }

        if (configMap.containsKey("mysql-host")) {
            this.mysql_host = (String) configMap.get("mysql-host");
        } else {
            this.mysql_host = "localhost";
        }

        if (configMap.containsKey("mysql-port")) {
            this.mysql_port = Integer.parseInt((String) configMap.get("mysql-port"));
        } else {
            this.mysql_port = 3306;
        }

        if (configMap.containsKey("mysql-user")) {
            this.mysql_user = (String) configMap.get("mysql-user");
        } else {
            this.mysql_user = "root";
        }

        if (configMap.containsKey("mysql-password")) {
            this.mysql_pass = (String) configMap.get("mysql-password");
        } else {
            this.mysql_pass = "";
        }

        if (configMap.containsKey("mysql-database")) {
            this.mysql_database = (String) configMap.get("mysql-database");
        } else {
            this.mysql_database = "data";
        }

        if (configMap.containsKey("version-Data")) {
            this.vsData = Byte.parseByte((String) configMap.get("version-Data"));
        } else {
            this.vsData = 98;
        }

        if (configMap.containsKey("version-Map")) {
            this.vsMap = Byte.parseByte((String) configMap.get("version-Map"));
        } else {
            this.vsMap = 23;
        }

        if (configMap.containsKey("version-Skill")) {
            this.vsSkill = Byte.parseByte((String) configMap.get("version-Skill"));
        } else {
            this.vsSkill = 2;
        }

        if (configMap.containsKey("version-Item")) {
            this.vsItem = Byte.parseByte((String) configMap.get("version-Item"));
        } else {
            this.vsItem = 6;
        }
    }

    //load item database
    public void loadDataBase() {
        SQLManager.create(this.mysql_host, this.mysql_port, this.mysql_database, this.mysql_user, this.mysql_pass);
        Connection conn = DBService.gI().getConnection();
        PreparedStatement ps=null;
        ResultSet res=null;
        int i;
        try {
             ps = conn.prepareStatement("SELECT * FROM mob");
             res = ps.executeQuery();
            JSONArray Option;
            i = 0;
            while (res.next()) {
                MobTemplate md = new MobTemplate();
                md.tempId = Integer.parseInt(res.getString("id"));
                md.name = res.getString("name");
                md.level = Byte.parseByte(res.getString("level"));
                md.maxHp = Integer.parseInt(res.getString("hp"));
                md.rangeMove = Byte.parseByte(res.getString("rangeMove"));
                md.speed = Byte.parseByte(res.getString("speed"));
                MobTemplate.entrys.add(md);
            }
            res.close();
            ps.close();
            //load MAP
            ps = conn.prepareStatement("SELECT * FROM map");
            res = ps.executeQuery();
            i = 0;
            byte j;

            if (res.last()) {
                MapTemplate.arrTemplate = new MapTemplate[res.getRow()];
                res.beforeFirst();
            }

            while (res.next()) {
                MapTemplate mapTemplate = new MapTemplate();
                mapTemplate.id = res.getInt("id");
                mapTemplate.name = res.getString("name");
                mapTemplate.type = res.getByte("type");
                mapTemplate.planetId = res.getByte("planet_id");
                mapTemplate.tileId = res.getByte("tile_id");
                mapTemplate.bgId = res.getByte("bg_id");
                mapTemplate.bgType = res.getByte("bg_type");
                mapTemplate.maxplayers = res.getByte("maxplayer");
                mapTemplate.numarea = res.getByte("numzone");
                mapTemplate.wayPoints = MapData.loadListWayPoint(mapTemplate.id).toArray(new WayPoint[0]);
                JSONArray jar2;
                Option = (JSONArray) JSONValue.parse(res.getString("Mob"));
                mapTemplate.arMobid = new short[Option.size()];
                mapTemplate.arrMoblevel = new int[Option.size()];
                mapTemplate.arrMaxhp = new int[Option.size()];
                mapTemplate.arrMobx = new short[Option.size()];
                mapTemplate.arrMoby = new short[Option.size()];
                short l;
                for (l = 0; l < Option.size(); ++l) {
                    jar2 = (JSONArray) Option.get(l);
                    mapTemplate.arMobid[l] = Short.parseShort(jar2.get(0).toString());
                    mapTemplate.arrMoblevel[l] = Integer.parseInt(jar2.get(1).toString());
                    mapTemplate.arrMaxhp[l] = Integer.parseInt(jar2.get(2).toString());
                    mapTemplate.arrMobx[l] = Short.parseShort(jar2.get(3).toString());
                    mapTemplate.arrMoby[l] = Short.parseShort(jar2.get(4).toString());
                }
                Option = (JSONArray) JSONValue.parse(res.getString("Npc"));
                mapTemplate.npcs = new Npc[Option.size()];
                for (j = 0; j < Option.size(); ++j) {
                    mapTemplate.npcs[j] = new Npc();
                    jar2 = (JSONArray) JSONValue.parse(Option.get(j).toString());
                    Npc npc = mapTemplate.npcs[j];
                    npc.status = Byte.parseByte(jar2.get(0).toString());
                    npc.cx = Short.parseShort(jar2.get(1).toString());
                    npc.cy = Short.parseShort(jar2.get(2).toString());
                    npc.tempId = Integer.parseInt(jar2.get(3).toString());
                    npc.avartar = Integer.parseInt(jar2.get(4).toString());
                }
                MapTemplate.arrTemplate[i] = mapTemplate;
                i++;
            }

            res.close();
            ps.close();
            //load MAP
            ps = conn.prepareStatement("SELECT * FROM optionitem");
            res = ps.executeQuery();


            while (res.next()) {
                ItemOptionTemplate iOptionTemplate = new ItemOptionTemplate();

                iOptionTemplate.id = res.getInt("id");
                iOptionTemplate.name =res.getString("name");
                iOptionTemplate.type = res.getInt("type");

                iOptionTemplates.add(iOptionTemplate);
                for(int s=0;s<iOptionTemplates.size();++s){
//                    Util.debug("id name optionss "+  iOptionTemplates.get(s).name);
                }
//


            }
            res.close();
            ps.close();
            //load MAP
            ps = conn.prepareStatement("SELECT * FROM item");
            res = ps.executeQuery();
            i = 0;
            JSONObject job;
            while (res.next()) {
                
                ItemTemplate item = new ItemTemplate();
                item.id = Short.parseShort(res.getString("id"));
                item.type = Byte.parseByte(res.getString("type"));
                item.gender = Byte.parseByte(res.getString("gender"));
                item.name = res.getString("name");
                item.skill=res.getInt("skillid");
                item.description = res.getString("description");
                item.level = Byte.parseByte(res.getString("level"));
                item.strRequire = Integer.parseInt(res.getString("strRequire"));
                item.iconID = Integer.parseInt(res.getString("IconID"));
                item.part = Short.parseShort(res.getString("part"));
                item.isUpToUp = Boolean.parseBoolean(res.getString("isUpToUp"));
                Option = (JSONArray) JSONValue.parse(res.getString("ItemOption"));
                if (Option.size() > 0) {
                    for (int k = 0; k < Option.size(); ++k) {
                        job = (JSONObject) Option.get(k);
                        item.itemoption.add(new ItemOption(Integer.parseInt(job.get("id").toString()), Integer.parseInt(job.get("param").toString())));
                    }
                } else {
                    item.itemoption.add(new ItemOption(73, 0));
                }

                ItemTemplate.entrys.add(item);
            }

            res.close();
            ps.close();
            //load MAP
            ps = conn.prepareStatement("SELECT * FROM ItemSell");
            res = ps.executeQuery();
            //load itemShell
            i = 0;

            while (res.next()) {
                ItemSell sell = new ItemSell();
                sell.id = Integer.parseInt(res.getString("item_id"));
                sell.buyCoin = Integer.parseInt(res.getString("buyCoin"));
                sell.buyGold = Integer.parseInt(res.getString("buyGold"));
                sell.buyType = Byte.parseByte(res.getString("buyType"));
                sell.isNew = res.getBoolean("isNew");
                Item item = new Item();
                item.id = sell.id;
                item.template = ItemTemplate.ItemTemplateID(item.id);
                item.quantity = Integer.parseInt(res.getString("quantity"));
                item.quantityTemp = Integer.parseInt(res.getString("quantity"));
                item.isExpires = Boolean.parseBoolean(res.getString("isExpires"));
                Option = (JSONArray) JSONValue.parse(res.getString("optionItem"));
                if (Option.size() > 0) {
                    for (int l = 0; l < Option.size(); l++) {
                        JSONObject job2 = (JSONObject) Option.get(l);
                        item.itemOptions.add(new ItemOption(Integer.parseInt(job2.get("id").toString()), Integer.parseInt(job2.get("param").toString())));
                    }
                }
                else
                {
                     item.itemOptions.add(new ItemOption(73, 0));
                }
                sell.item = item;
                ItemSell.items.add(item);
                ItemSell.itemCanSell.add(sell);
                i++;
            }


            //load Shops
            res.close();
            ps.close();

            ps = conn.prepareStatement("SELECT * FROM shop");
            res = ps.executeQuery();
            i = 0;

            while (res.next()) {
                Shop shop = new Shop();
                shop.npcID = Integer.parseInt(res.getString("npcID"));
                shop.idTabShop = Integer.parseInt(res.getString("idTabShop"));
                JSONArray tabs = (JSONArray) JSONValue.parse(res.getString("itemSell"));
                for (int k = 0; k < tabs.size(); k++) {
                    TabItemShop tabItemShop = new TabItemShop();
                    JSONObject tabItem = (JSONObject) JSONValue.parse(tabs.get(k).toString());
                    tabItemShop.tabName = tabItem.get("tabName").toString();
                    JSONArray items = (JSONArray) JSONValue.parse(tabItem.get("items").toString());
                    for (int l = 0; l < items.size(); l++) {
                        int itemSellID = Integer.parseInt(items.get(l).toString());
                        tabItemShop.itemsSell.add(ItemSell.getItemSellByID(itemSellID));
                    }
                    shop.tabShops.add(tabItemShop);
                }
                Shop.shops.add(shop);
                i++;
            }
            res.close();
            ps.close();

            ps = conn.prepareStatement("SELECT * FROM itemtemp");
            res = ps.executeQuery();
            i = 0;

            while (res.next()) {
                Item item2 = new Item();
                item2.idTemp = res.getInt("id");
                item2.headTemp = res.getShort("head");
                item2.bodyTemp = res.getShort("body");
                item2.legTemp = res.getShort("leg");
                item2.entrys.add(item2);  
                i++;
            }
            res.close();
            ps.close();
            res.close();

        } catch (Exception var14) {
            var14.printStackTrace();
            System.exit(0);
        }
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        SQLManager.close();
        SQLManager.create(this.mysql_host, this.mysql_port, this.mysql_database, this.mysql_user, this.mysql_pass);
    }

    public static void sendData(Player p) {
        Message m = null;
        try {
            m = new Message(-87);
            m.writer().write(Server.cache[0].toByteArray());
            m.writer().flush();
            p.session.sendMessage(m);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void sendMap(Player p) {
        Message m = null;
        try {
            m = new Message(-28);
            m.writer().write(Server.cache[1].toByteArray());
            m.writer().flush();
            p.session.sendMessage(m);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void sendSkill(Player p) {
        Message m = null;
        try {
            m = new Message(-28);
            m.writer().write(Server.cache[2].toByteArray());
            m.writer().flush();
            p.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static void sendItem(Player p) {
        Message m = null;
        try {
            m = new Message(-28);
            m.writer().write(Server.cache[3].toByteArray());
            p.session.doSendMessage(m);
            m.cleanup();

            m = new Message(-28);
            m.writer().write(Server.cache[4].toByteArray());
            p.session.doSendMessage(m);
            m.cleanup();

            m = new Message(-28);
            m.writer().write(Server.cache[5].toByteArray());
            p.session.doSendMessage(m);
            m.cleanup();
            
            m = new Message(-28);
            m.writer().write(Server.cache[6].toByteArray());
            p.session.doSendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

    public static Map getMapid(int id) {
        synchronized (server.maps) {

            for (Map map : server.maps) {
                if (map != null && map.template.id == id) {
                    return map;
                }
            }
            return null;

//            for (short i = 0; i < server.maps.length; i++) {
//                Map map = server.maps[i];
//                if (map != null && map.template.id == id) {
//                    return map;
//                }
//            }
        }
    }

    public static void reciveImageMOB(Player player, Message m) {
        try {
            if (player != null && player.session != null && m != null && m.reader().available() > 0) {
                GameScr.reciveImageMOB(player, m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }

}
