package server;

import cache.Part;
import cache.PartImage;

import java.io.*;
import java.nio.CharBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Scanner;
import org.joda.time.DateTime;
import real.clan.Clan;
import real.clan.Member;
import real.clan.ClanManager;
import real.item.Item;
import real.item.ItemOption;
import real.item.ItemTemplate;
import real.item.useItem;
import real.map.Map;
import real.map.Mob;
import real.map.MobTemplate;
import real.map.Npc;
import real.map.WayPoint;
import real.player.Player;
import real.player.PlayerManger;
import real.skill.EffectTeamPlate;
import server.io.Session;
import real.skill.Skill;
import static server.GameScr.saveFile;
import server.io.Message;

public class Service {

    private static Service instance;
    
    public void LoadDeTu(Player p){
        Message m = null;
        try {
            m = new Message(-90);
            m.writer().writeByte(1);
            m.writer().writeShort(p.head);
            m.writer().writeByte(p.ItemBody.length);
            for (Item item : p.ItemBody) {
                if (item == null) {
                    m.writer().writeShort(-1);
                } else {
                    m.writer().writeShort(item.template.id);
                    m.writer().writeInt(item.quantity);
                    m.writer().writeUTF(item.getInfo());
                    m.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    m.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        m.writer().writeByte(itemOption.optionTemplate.id);
                        m.writer().writeShort(itemOption.param);
                    }

                }
            }
            m.writer().writeByte(p.hp);
            m.writer().writeByte(p.getHpFull());
            m.writer().writeByte(p.mp);
            m.writer().writeByte(p.getMpFull());
            m.writer().writeByte(p.getDamFull());
            m.writer().writeUTF(p.name);
            m.writer().writeUTF("Giới Vương");
            m.writer().writeLong(p.power);
            m.writer().writeLong(p.tiemNang);
            m.writer().writeByte(0);
            m.writer().writeShort(1000);
            m.writer().writeShort(1000);
            m.writer().writeByte(p.getCritFull());
            m.writer().writeShort(p.getDefFull());
            m.writer().writeByte(p.skill.size());
            for (Skill skill : p.skill) {
                m.writer().writeShort((short)skill.getSkillID());
            }
            m.writer().flush();
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void LoadCaiTrang(Player p , int type , int head , int body , int leg){
        Message m = null;
        try {
            m = new Message(-90);
            m.writer().writeByte(type);
            m.writer().writeInt(p.id);
            m.writer().writeShort(head);
            m.writer().writeShort(body);
            m.writer().writeShort(leg);
            m.writer().writeShort(0);
            p.session.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
    }

    public void sendTB(Session p, int avatar, String s , int typechat) {
        Message m = null;
        try {
            m = new Message(-70);
            m.writer().writeShort(avatar);
            m.writer().writeUTF(s);
            m.writer().writeByte(typechat);
            p.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
    }
    public void MagicTree(Session p, int type) {
        Message m = null;
        try {
            m = new Message(-34);
            m.writer().writeByte(type);
            if(type == 0){
                m.writer().writeShort(84);
                m.writer().writeUTF("Đậu Thần Cấp 2");
                m.writer().writeShort(278);
                m.writer().writeShort(336);
                m.writer().writeByte(2);
                m.writer().writeShort(5);
                m.writer().writeShort(15);
                m.writer().writeUTF("Đang Kết Hạt");
                m.writer().writeInt(50);
                m.writer().writeByte(0);
                m.writer().writeBoolean(true);
            }else  if(type==2){
                m.writer().writeShort(84);
                m.writer().writeUTF("Đậu Thần Cấp 1");
                m.writer().writeShort(278);
                m.writer().writeShort(336);
                m.writer().writeByte(2);
                m.writer().writeShort(5);
                m.writer().writeShort(10);
                m.writer().writeUTF("Đang nâng cấp");
                m.writer().writeInt(1800);
                m.writer().writeByte(0);
                m.writer().writeBoolean(true);

            }
            m.writer().flush();
            p.sendMessage(m);
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
    }
    public void SellItem(Player player , byte actionBuy , byte typeBuy ,short indexBuy){
        try{
            Item item = player.getIndexBag(indexBuy);
            if (item != null){
                if(actionBuy == 0){
                    Service.gI().SaleDone(player, typeBuy, "Bạn có muốn bán\n"+"x"+player.ItemBag[indexBuy].quantity +" "+player.ItemBag[indexBuy].template.name +"\nVới giá 1 vàng không?" , indexBuy);
                }
                else if(actionBuy == 1){
                    player.removeItemBag(indexBuy);
                    Service.gI().updateItemBag(player);
                    player.vang +=1;
                    Service.gI().buyDone(player);
                }else{
                    player.sendAddchatYellow("Bán vật phẩm không thành công");
                }
            }
        }
        catch (Exception e) {
            player.sendAddchatYellow("Bán vật phẩm không thành công");
            e.printStackTrace();
        }
    }
    public void SaleDone(Player p , short type ,String info ,short id) {
        Message m;
        try {
            m = new Message(7);
            m.writer().writeByte(type);
            m.writer().writeShort(id);
            m.writer().writeUTF(info);
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     public void UseItem(Player p , int index , short id){
        try {
            Item item = p.getIndexBag(index);
            if (item != null) {
                useItem.uesItem(p, item, index ,id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void DropDone(Player p , short type ,String info ,short id) {
        Message m;
        try {
            m = new Message(-43);
            m.writer().writeByte(type);
            m.writer().writeByte(1);
            m.writer().writeByte(id);
            m.writer().writeUTF(info);
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Service gI() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }
    public void PlayerMenu(Session s,Player p) {
        Message m;
        try {
            m = new Message(-79);
            m.writer().writeInt(-1);
            m.writer().writeLong(p.power);
            m.writer().writeUTF("");
            s.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void requestItemInfo(Player p, int typeUI, int indexUI) {
        Message message = null;
        try {
            message = new Message(35);
            message.writer().writeByte(typeUI);
            message.writer().writeByte(indexUI);
            p.session.sendMessage(message);
        } catch (Exception ex) {

        } finally {
            message.cleanup();
        }
    }

    public static void createCachePart() {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bas);
            ByteArrayOutputStream parts = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(parts);
            ds = new DataOutputStream(parts);
            ds.writeShort(Manager.parts.size());
            for (Part p : Manager.parts) {
                ds.writeByte(p.type);
                for (PartImage pi : p.pi) {
                    ds.writeShort(pi.id);
                    ds.writeByte(pi.dx);
                    ds.writeByte(pi.dy);
                }
            }
            ds.flush();
            dos.writeInt(parts.toByteArray().length);
            dos.write(parts.toByteArray());
            byte[] ab = bas.toByteArray();
            saveFile("res/cache/data/NR_part", ab);
            ds.close();
            parts.close();
            dos.close();
            bas.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void serverMessage(Session session, String text) {
        Message msg;
        try {
            msg = new Message(-26);
            msg.writer().writeUTF(text);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void chatNPC(Player p, Short idnpc, String chat) {
        Message m = null;
        try {
            m = new Message(38);
            m.writer().writeShort(idnpc);
            m.writer().writeUTF(chat);
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

    public void loginDe(Session session, short second) {
        Message msg;
        try {
            msg = new Message(122);
            msg.writer().writeShort(second);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void versionImageSource(Session session) {
        Message msg;
        try {
            msg = new Message(-74);
            msg.writer().writeByte(0);
            msg.writer().writeInt(5714013);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sizeImageSource(Session session) {
        Message msg;
        try {
            msg = new Message(-74);
            msg.writer().writeByte(1);
            msg.writer().writeShort(958);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void imageSource(Session session) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg;
                try {
                    
                    File file = new File("data/res/5714013.txt");


                    Scanner scaner = new Scanner(file);
                    String[] data2 = new String[958];
                    int i = 0;
                    while (scaner.hasNextLine()) {
                        String data = scaner.nextLine();
                        data2[i] = data;

                        byte[] res = FileIO.readFile("data/res/5714013" + data2[i]);
//                        System.out.println("File created: " +  "data/res/5714013" + data2[i]);
                        msg = new Message(-74);
                        msg.writer().writeByte(2);
                        msg.writer().writeUTF(data2[i]);
                        msg.writer().writeInt(res.length);
                        msg.writer().write(res);
                        session.sendMessage(msg);
                        msg.cleanup();
                       i++;
                    }
                    scaner.close();

                    msg = new Message(-74);
                    msg.writer().writeByte(3);
                    msg.writer().writeInt(5714013);
                    session.sendMessage(msg);
                    msg.cleanup();

                     
                } catch (Exception e) {
                }
            }
        }).start();
    }

    public void itemBg(Session session, int id) {
        Message msg;
        try {
            byte[] item_bg = FileIO.readFile("data/map/item_bg/" + id);
            msg = new Message(-31);
            msg.writer().write(item_bg);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void bgTemp(Session session, int id) {
        Message msg;
        try {
            byte[] bg_temp = FileIO.readFile("data/bg_temp/" + id);
            msg = new Message(-32);
            msg.writer().write(bg_temp);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void effData(Session session, int id) {
        Message msg;
        try {
            byte[] eff_data = FileIO.readFile("data/eff_data/" + id);
            msg = new Message(-66);
            msg.writer().write(eff_data);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
//        Connection conn = DBService.gI().getConnection();
//        try {
//            PreparedStatement ps=null;
//            ps = conn.prepareStatement("INSERT INTO effect(id, type, name,iconId) VALUES(?,?,?,?)");
//            int i;
//            for(i=0;i<eff_data)
//        }catch (Exception e){
//
//        }
    }

    public void loadeffData() {
//        Connection conn = DBService.gI().getConnection();
//        try {
//            ByteArrayInputStream is = new ByteArrayInputStream(FileIO.readFile("res/cache/data/NR_effect"));
//            DataInputStream dis = new DataInputStream(is);
//            dis.readByte();
//            dis.readByte();
//            dis.readByte();
//            PreparedStatement ps=null;
//            ps = conn.prepareStatement("INSERT INTO effect(id, type, name,iconId) VALUES(?,?,?,?)");
//            int num = (int) dis.readShort();
//            for (int j = 0; j < num; j++) {
//                ps.setInt(1,j);
//                ps.setInt(2,dis.readByte());
//                ps.setString(3,dis.readUTF());
//                ps.setInt(4,dis.readShort());
//                EffectTeamPlate.EffectTemplate it = new EffectTeamPlate.EffectTemplate();
//                it.id = j;
//                it.type = dis.readByte();
//
//                it.name = dis.readUTF();
//
//                it.iconId = dis.readShort();
//
//                Util.debug("idEfffff " + it.id + " name" + it.name );
//
//                EffectTeamPlate.EffectTemplate.entrys.add(it);
//
//            }
//            ps.close();
//            conn.close();
//        }catch (Exception e){
//
//        }
    }

    public void requestModTemplate(Player p, int id) {
        Message msg;
        try {
            byte[] mob = FileIO.readFile("data/mob/x"+p.session.zoomLevel + "/" + id);
            msg = new Message(11);
//            msg.writer().writeInt(id);
//            msg.writer().writeInt(mob.length);
            msg.writer().write(mob);
            p.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendMessage(Session session, int cmd, String filename) {
        Message msg;
        try {
            msg = new Message(cmd);
            msg.writer().write(FileIO.readFile("data/msg/" + filename));
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void updateVersion(Session session) {
        Message msg;
        try {
            msg = messageNotMap((byte) 4);
            msg.writer().writeByte(Server.manager.vsData);
            msg.writer().writeByte(Server.manager.vsMap);
            msg.writer().writeByte(Server.manager.vsSkill);
            msg.writer().writeByte(Server.manager.vsItem);
            msg.writer().writeByte(0);
            msg.writer().write(FileIO.readFile("data/NRexp"));
            session.sendMessage(msg);
            msg.cleanup();
//            msg = new Message(-28);
//            msg.writer().write(FileIO.readFile("data/1632811838304_-28_4_r"));
//            session.sendMessage(msg);
//            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void updateData(Session session) {
        Message msg;
        try {
            msg = new Message(-87);
//            msg.writer().write(FileIO.readFile("data/NRdata_v47"));
            msg.writer().write(FileIO.readFile("data/1632811838531_-87_r"));
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        System.out.println("update data");
    }

    public void updateMap(Session session) {
        Message msg;
        try {
            //msg = messageNotMap((byte) 6);
            msg = new Message(-28);
//            msg.writer().write(FileIO.readFile("data/NRmap_v25"));
            msg.writer().write(FileIO.readFile("data/1632811838538_-28_6_r"));
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        System.out.println("update map");
    }

    public void updateSkill(Session session) {
        Message msg;
        try {
            //msg = messageNotMap((byte) 7);
            msg = new Message(-28);
//            msg.writer().write(FileIO.readFile("data/NRskill_v5"));
            msg.writer().write(FileIO.readFile("data/1632811838545_-28_7_r"));
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        System.out.println("update skill");
    }

    public void updateItem(Session session) {
        Message msg;
        try {
            //msg = messageNotMap((byte) 8);
            msg = new Message(-28);
            //msg.writer().write(FileIO.readFile("data/NRitem_v90_0"));
            msg.writer().write(FileIO.readFile("data/1632811838554_-28_8_r"));
            session.doSendMessage(msg);
            msg.cleanup();

            msg = new Message(-28);
            //msg.writer().write(FileIO.readFile("data/NRitem_v90_1"));
            msg.writer().write(FileIO.readFile("data/1632811838561_-28_8_r"));
            session.doSendMessage(msg);
            msg.cleanup();

            msg = new Message(-28);
            //msg.writer().write(FileIO.readFile("data/NRitem_v90_2"));
            msg.writer().write(FileIO.readFile("data/1632811838570_-28_8_r"));
            session.doSendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
        System.out.println("update item");
    }

    public void tileSet(Session session, int id) {
        Message msg;
        try {
            //msg = messageNotMap((byte) 6);
            msg = new Message(-82);
            msg.writer().write(FileIO.readFile("data/map/tile_set/" + id));
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void mapInfo(Session session, Player pl) {
        Message msg;
        try {
            Map map = pl.map;
            msg = new Message(-24);
            msg.writer().writeByte(map.id);
            msg.writer().writeByte(map.template.planetId);
            msg.writer().writeByte(map.template.tileId);
            msg.writer().writeByte(map.template.bgId);
            msg.writer().writeByte(map.template.type);
            msg.writer().writeUTF(map.template.name);
            msg.writer().writeByte(0);
            msg.writer().writeShort(pl.x);
            msg.writer().writeShort(pl.y);

            // Load WayPoint class Map Template
            //    ArrayList<WayPoint> wayPoints = map.wayPoints;
            msg.writer().writeByte(map.template.wayPoints.length);
            for (WayPoint wp : map.template.wayPoints) {
                msg.writer().writeShort(wp.minX);
                msg.writer().writeShort(wp.minY);
                msg.writer().writeShort(wp.maxX);
                msg.writer().writeShort(wp.maxY);
                msg.writer().writeBoolean(wp.isEnter);
                msg.writer().writeBoolean(false);
                msg.writer().writeUTF(map.template.name);
            }
            // Load mob class Map Template
            msg.writer().writeByte(map.template.mobs.length);
            for (Mob mob : map.template.mobs) {
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeBoolean(false);
                msg.writer().writeByte(mob.tempId);
                msg.writer().writeByte(0);
                msg.writer().writeInt(mob.hp);
                msg.writer().writeByte(mob.level);
                msg.writer().writeInt((mob.maxHp));
                msg.writer().writeShort(mob.pointX);
                msg.writer().writeShort(mob.pointY);
                msg.writer().writeByte(mob.status);
                msg.writer().writeByte(0);
                msg.writer().writeBoolean(false);
            }

            msg.writer().writeByte(0);

            // Load NPC class Map Template
            msg.writer().writeByte(map.template.npcs.length);
            for (Npc npc : map.template.npcs) {
                msg.writer().writeByte(npc.status);
                msg.writer().writeShort(npc.cx);
                msg.writer().writeShort(npc.cy);
                msg.writer().writeByte(npc.tempId);
                msg.writer().writeShort(npc.avartar);
            }

            msg.writer().writeByte(0);

            // bg item
            byte[] bgItem = FileIO.readFile("data/map/bg/" + map.id);
            msg.writer().write(bgItem);

            // eff item
            byte[] effItem = FileIO.readFile("data/map/eff/" + map.id);
            msg.writer().write(effItem);

            msg.writer().writeByte(map.bgType);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void PlayerAttack(Player p, Mob[] mob) {
        Message msg = null;
        try {
            msg = new Message(45);
            msg.writer().writeInt(p.id);
            msg.writer().writeByte(p.CSkill);
            for (byte i = 0; i < mob.length; i++) {
                if (mob[i] != null) {
                    msg.writer().writeByte(mob[i].tempId);
                }
            }
            msg.writer().flush();
            p.session.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void addPlayer(Session session, Player player) {
        Message msg;
        try {
            msg = new Message(-5);
            msg.writer().writeInt(player.id);
            msg.writer().writeInt(-1);
            msg.writer().writeByte(10);
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(0);
            msg.writer().writeByte(2);
            msg.writer().writeByte(2);
            msg.writer().writeShort(27);
            msg.writer().writeUTF(player.name);
            msg.writer().writeInt(54760);
            msg.writer().writeInt(54760);
            msg.writer().writeShort(player.PartBody());
            msg.writer().writeShort(player.Leg());
            msg.writer().writeByte(8);
            msg.writer().writeByte(-1);
            msg.writer().writeShort(player.x);
            msg.writer().writeShort(player.y);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeShort(348);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removePlayer(Session session, Player player) {
        Message msg;
        try {
            msg = new Message(-6);
            msg.writer().writeInt(player.id);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void resetPoint(Session session, int x, int y) {
        Message msg;
        try {
            msg = new Message(46);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            session.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {
        }
    }

    public void login2(Session session, String user) {
        Message msg;
        try {
            msg = new Message(-101);
            msg.writer().writeUTF(user);
            session.sendMessage(msg);
            msg.cleanup();

        } catch (Exception e) {
        }
    }

    public void mapTemp(Session session, int id) {
        Message msg;
        try {
            //msg = messageNotMap((byte) 6);
            msg = new Message(-28);
            msg.writer().write(FileIO.readFile("data/map/temp/" + id));
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void updateBag(Session session) {
        Message msg;
        try {
            msg = new Message(-64);
            msg.writer().writeInt(0);// id char
            msg.writer().writeByte(0);// id bag
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void updateBody(Session session) {
        Message msg;
        try {
            msg = new Message(-90);
            msg.writer().writeByte(1);
            msg.writer().writeInt(1);
            msg.writer().writeShort(177);
            msg.writer().writeShort(178);
            msg.writer().writeShort(179);
            msg.writer().writeByte(0);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void updateSloganClan(Session session, String text) {
//        ClanDB.updateSlogan(session.player.clanId, text);
//        Message msg;
//        try {
//            msg = new Message(-46);
//            msg.writer().writeByte(4);
//            msg.writer().writeByte(0);
//            msg.writer().writeUTF(text);
//            session.sendMessage(msg);
//            msg.cleanup();
//        } catch (Exception e) {
//        }
    }

    public void clearMap(Session session) {
        Message msg;
        try {
            msg = new Message(-22);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void messagePlayerMenu(Session session, int charId) {
        Message message = null;
        try {
            message = new Message(-30);
            message.writer().writeByte(63);
            message.writer().writeInt(charId);
            session.sendMessage(message);
        } catch (Exception ex) {
        }
    }

    public void playerMove(Session session, Player pl) {
        Message msg;
        try {
            msg = new Message(-7);
            msg.writer().writeInt(pl.id);
            msg.writer().writeShort(pl.x);
            msg.writer().writeShort(pl.y);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void stamina(Session session) {
        Message msg;
        try {
            msg = new Message(-68);
            msg.writer().writeShort(10000);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void maxStamina(Session session) {
        Message msg;
        try {
            msg = new Message(-69);
            msg.writer().writeShort(10000);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void activePoint(Session session) {
        Message msg;
        try {
            msg = new Message(-97);
            msg.writer().writeInt(1000);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void loadPoint(Session session, Player pl) {
        Message msg;
        try {
            msg = new Message(-42);
            msg.writer().writeInt(pl.hpGoc);
            msg.writer().writeInt(pl.mpGoc);
            msg.writer().writeInt(pl.damGoc);
            msg.writer().writeInt(pl.getHpFull());// hp full
            msg.writer().writeInt(pl.getMpFull());// mp full
            msg.writer().writeInt(pl.hp);// hp
            msg.writer().writeInt(pl.mp);// mp
            msg.writer().writeByte(pl.getSpeed());// speed
            msg.writer().writeByte(20);
            msg.writer().writeByte(20);
            msg.writer().writeByte(1);
            msg.writer().writeInt(pl.getDamFull());// dam full
            msg.writer().writeInt(pl.getDefFull());// def full
            msg.writer().writeByte(pl.getCritFull());// crit full
            msg.writer().writeLong(pl.tiemNang);
            msg.writer().writeShort(100);
            msg.writer().writeShort(pl.defGoc);
            msg.writer().writeByte(pl.critGoc);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buyDone(Player p) {
        Message m;
        try {
            m = new Message(6);
            m.writer().writeInt(p.vang);
            m.writer().writeInt(p.ngoc);
            m.writer().writeInt(p.ngocKhoa);
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
        }
    }

    public void loadPlayer(Session session, Player pl) {
        Message msg;
        try {
            msg = messageSubCommand((byte) 0);
            msg.writer().writeInt(pl.id);
            msg.writer().writeByte(pl.taskId);
            msg.writer().writeByte(pl.gender);
            msg.writer().writeShort(pl.PartHead());
            msg.writer().writeUTF(pl.name);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeLong(pl.power);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0);
            msg.writer().writeByte(pl.gender);
            //--------skill---------
            ArrayList<Skill> skills = pl.skill;
            msg.writer().writeByte(skills.size());
            for (Skill skill : skills) {
                msg.writer().writeShort((short)skill.getSkillID());
            }
            //---vang---luong--luongKhoa
            msg.writer().writeInt(pl.vang);
            msg.writer().writeInt(pl.ngocKhoa);
            msg.writer().writeInt(pl.ngoc);

            //--------itemBody---------
            Item[] itemsBody = pl.ItemBody;
            msg.writer().writeByte(itemsBody.length);
            for (Item item : itemsBody) {
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }

            }

            //--------itemBag---------
            Item[] itemsBag = pl.ItemBag;
            msg.writer().writeByte(itemsBag.length);
            for (int i = 0; i < itemsBag.length; i++) {
                Item item = itemsBag[i];
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }

            }
            //--------itemBox---------
            Item[] itemsBox = pl.ItemBox;
            msg.writer().writeByte(itemsBox.length);
            for (int i = 0; i < itemsBox.length; i++) {
                Item item = itemsBox[i];
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }
            }
            //-----------------

     //       msg.writer().writeShort(252);
            msg.writer().write(FileIO.readFile("res/cache/head"));
//            msg.writer().writeShort(992);
//            msg.writer().writeShort(8928);
            //-----------------
            msg.writer().writeShort(514);
            msg.writer().writeShort(515);
            msg.writer().writeShort(537);
            msg.writer().writeByte(pl.NhapThe);
            msg.writer().writeInt(1632811835);
            msg.writer().writeByte(0);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateItemBody(Player player) {
        Message msg;
        try {
            Item[] itemsBody = player.ItemBody;
            msg = new Message(-37);
            msg.writer().writeByte(0);
            msg.writer().writeShort(player.PartHead());
            msg.writer().writeByte(itemsBody.length);
            for (Item item : itemsBody) {
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }

                }
            }
            player.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            System.out.println("-37 " + e.toString());
        }
    }

    public void updateItemBag(Player player) {
        Message msg;
        try {
            Item[] itemsBody = player.ItemBag;
            msg = new Message(-36);
            msg.writer().writeByte(0);
            msg.writer().writeByte(itemsBody.length);
            for (Item item : itemsBody) {
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }

                }
            }
            player.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateItemBox(Player player) {
        Message msg;
        try {
            Item[] itemsBox = player.ItemBox;
            msg = new Message(-35);
            msg.writer().writeByte(0);
            msg.writer().writeByte(itemsBox.length);
            for (Item item : itemsBox) {
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }

                }
            }
            player.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     public void upgrade(Player player,byte action,byte indexUI){
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(5);
            msg.writer().writeShort(indexUI);
            player.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            System.out.println("-107 " + e.toString());
        }
    }
    public void statusDetu(Player player){
        Message msg;
        try {
            msg = new Message(31);
            msg.writer().writeInt(player.id);
            msg.writer().writeByte(1);
            msg.writer().writeShort(6);
            msg.writer().writeByte(1);
            msg.writer().writeByte(1);
            player.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            System.out.println("-107 " + e.toString());
        }
    }
    public void CHAGE_MOD_BODY(Player player){
       Message msg;
        try {
            msg = new Message(-84); 
            msg.writer().writeByte(player.ItemBody.length);
            msg.writer().writeInt(player.hp);
            player.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        }
    }
    public void deTu(Player player) {
        Message msg;
        try {
            msg = new Message(-107);
            Item[] itemsBody = new Item[7];
            msg.writer().writeByte(2);
            msg.writer().writeShort(player.PartHead());
            msg.writer().writeByte(itemsBody.length);
            for (Item item : itemsBody) {
                if (item == null) {
                    msg.writer().writeShort(-1);
                } else {
                    msg.writer().writeShort(item.template.id);
                    msg.writer().writeInt(item.quantity);
                    msg.writer().writeUTF(item.getInfo());
                    msg.writer().writeUTF(item.getContent());
                    ArrayList<ItemOption> itemOptions = item.itemOptions;
                    msg.writer().writeByte(itemOptions.size());
                    for (ItemOption itemOption : itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }

                }
            }
            msg.writer().writeByte(player.hp);
            msg.writer().writeByte(player.getHpFull());
            msg.writer().writeByte(player.mp);
            msg.writer().writeByte(player.getMpFull());
            msg.writer().writeByte(player.getDamFull());
            msg.writer().writeUTF(player.name);
            msg.writer().writeUTF("10000");
            msg.writer().writeLong(player.power);
            msg.writer().writeLong(player.tiemNang);
            msg.writer().writeByte(0);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0);
            msg.writer().writeByte(player.getCritFull());
            msg.writer().writeShort(player.getDefFull());
            Skill[] skills = new Skill[4];
            msg.writer().writeByte(4);
            for (int i = 0; i < 4; i++) {
                if(skills[i] != null){
                msg.writer().writeShort((short)skills[i].getSkillID());
                }else{
                msg.writer().writeShort(-1);
                msg.writer().writeUTF("Yêu cầu sức mạnh đạt");
                }
            }
            player.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            System.out.println("-107 " + e.toString());
        }
    }
    public void GET_PLAYER_MENU(Player player){
        Message msg;
        try {
            msg = new Message(63);
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Chủ Thân");
            msg.writer().writeUTF("Đệ tử");
            msg.writer().writeShort(1);
            player.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            System.out.println("-63 " + e.toString());
        }
    }
    public void chat(Session session, int playerId, String text) {
        Message msg;
        try {
            msg = new Message(44);
            msg.writer().writeInt(playerId);
            msg.writer().writeUTF(text);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendTB(Player p, String title, String s) {
        Message m = null;
        try {
            m = new Message(92);
            m.writer().writeUTF(title);
            m.writer().writeUTF(s);
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

    public void serverTB(Player p, String str) {
        Message m = null;
        try {
            m = new Message(93);
            m.writer().writeUTF(str);
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

    //Client chát server
    public void ChatGolbaL(Player p, String str) {
        Message m = null;
        try {
            m = new Message(92);
            m.writer().writeUTF(p.name);
            m.writer().writeUTF("|5|" + str);
            m.writer().writeInt(1);
            m.writer().writeShort(p.PartHead());
            m.writer().writeShort(p.PartBody());
            m.writer().writeShort(-1);
            m.writer().writeShort(p.Leg());
            m.writer().writeByte(0);
            PlayerManger.gI().SendMessageServer(m);
            m.writer().flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }
    }
    
    private Message messageNotLogin(byte command) throws IOException {
        Message ms = new Message(-29);
        ms.writer().writeByte(command);
        return ms;
    }

    private Message messageNotMap(byte command) throws IOException {
        Message ms = new Message(-28);
        ms.writer().writeByte(command);
        return ms;
    }

    private Message messageSubCommand(byte command) throws IOException {
        Message ms = new Message(-30);
        ms.writer().writeByte(command);
        return ms;
    }
    //Rồng Namek
//     public void RongNamek(Session p) {
//        Message m = null;
//        try {
//            m = new Message(-83);
//            m.writer().writeByte(0);
//            m.writer().writeShort(p.player.map.id);
//            m.writer().writeShort(66);
//            m.writer().writeInt(0);
//	    m.writer().writeInt(1);
//	    m.writer().writeUTF("Ahuhu");
//	    m.writer().writeShort(p.player.x);
//	    m.writer().writeShort(p.player.y);
//	    m.writer().writeByte(1);
//            m.writer().flush();
//            p.sendMessage(m);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(m != null) {
//                m.cleanup();
//            }
//        }
//    }

}
