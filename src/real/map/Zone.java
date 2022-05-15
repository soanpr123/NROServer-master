package real.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import real.item.Item;
import real.item.ItemOption;
import real.item.ItemSell;
import real.item.ItemTemplate;
import real.player.Player;
import real.player.PlayerManger;
import real.skill.Skill;
import real.skill.SkillData;
import real.skill.SkillTemplate;
import real.skill.Skills;
import server.Controller;
import server.FileIO;
import server.Manager;
import server.Service;
import server.Util;
import server.io.Message;
import server.io.Session;

public class Zone {

    public ArrayList<Mob> mobs = new ArrayList();
    public ArrayList<ItemMap> itemsMap = new ArrayList();
    public Map map;
    public final ArrayList<Player> players = new ArrayList<>();
    public Object LOCK = new Object();
    public byte id;
    //public byte numplayers = 0;
    public void AddItemGa(){
        try {
            for (Player p : players) {
                if (p != null && p.session != null) {
                    if(p.map.id == 21+p.gender){
                        ItemMap itemMap = new ItemMap();
                        Item item = new Item();
                        if(p.gender == 2){
                            itemMap.x = 619;
                            itemMap.y = 325;
                        }else if(p.gender == 1){
                            itemMap.x = 43;
                            itemMap.y = 320;
                        }
                        else{
                            itemMap.x = 623;
                            itemMap.y = 320;
                        }
                        itemMap.playerId = p.id;
                        itemMap.itemTemplateID = 74;
                        itemMap.item = item;
                        Message m = new Message(68);
                        itemMap.itemMapID = 74;
                        itemsMap.add(itemMap);
                        sendMessage(m);
                        m.writer().writeShort(itemMap.itemMapID);
                        m.writer().writeShort(itemMap.itemTemplateID);
                        m.writer().writeShort(itemMap.x);
                        m.writer().writeShort(itemMap.y);
                        m.writer().writeInt(itemMap.playerId);
                    }
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void DelItemMap(Player p){
        ArrayList<ItemMap> itemss = new ArrayList<>();
        for(int i = 0 ; i < itemsMap.size();i++){
            if(p.id == itemsMap.get(i).playerId){
                itemss.add(itemsMap.get(i));
            }
        }
        if(itemss.size()>0){
            itemsMap.removeAll(itemss);
        }
    }
    public void updateItemMap() {
        int i;
        ItemMap itemMap;
        for(i = this.itemsMap.size() - 1; i>= 0; i--) {
            itemMap = this.itemsMap.get(i);
            if(itemMap != null) {
                if (System.currentTimeMillis() >= itemMap.removedelay) {
                    this.removeItemMapMessage(itemMap.itemMapID);
                    this.itemsMap.remove(i);
                    i--;
                } else if (itemMap.removedelay - System.currentTimeMillis() < 5000L && itemMap.playerId != -1) {
                    itemMap.playerId = -1;
                }
            }
        }
    }
     public void removeItemMapMessage(int itemmapid) {
        Message m = null;
        try {
            m = new Message(-21);
            m.writer().writeShort(itemmapid);
            m.writer().flush();
            this.sendMessage(m);
        } catch (IOException var5) {
            var5.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
    }
    public Zone(Map map, byte id) {
        this.map = map;
        this.id = id;
    }

    public void leave(Player p) {
        synchronized (this) {
            if (players.contains(p)) {
                players.remove(p);
                removeMessage(p.id);
                //numplayers--;
            }
        }
    }

    public void removeMessage(int id) {
        try {
            Message m = new Message(-6);
            m.writer().writeInt(id);

            sendMessage(m);
            m.writer().flush();
            m.cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //item Handler
    public boolean CheckItemIDExist(int itemMapId) {
        for (ItemMap itemMap : itemsMap) {
            if (itemMap.itemMapID == itemMapId) {
                return true;
            }
        }
        return false;
    }
    
    public void PickItemDrop(Player p, short itemMapId) throws IOException {
        for (int i = 0; i < itemsMap.size(); i++) {
            ItemMap itemPick = itemsMap.get(i);
            if(itemPick.itemMapID == 74){
                p.update();
                Message m = new Message(-20);
                m.writer().writeShort(itemMapId);
                m.writer().writeUTF("");
                p.session.sendMessage(m);
                m.cleanup();
                return;
            }
            if (itemPick.itemMapID == itemMapId) {
                if(itemPick.playerId == p.id || itemPick.playerId == -1){
                    if (p.addItemToBag(itemPick.item)) {
                        itemsMap.remove(i);
                        Message m = new Message(-20);
                        m.writer().writeShort(itemMapId);
                        m.writer().writeUTF("");
                        p.session.sendMessage(m);
                        m = new Message(-19);
                        m.writer().writeShort(itemPick.itemMapID);
                        m.writer().writeInt(p.id);
                        sendMyMessage(p, m);
                        m.cleanup();
                    }
                    p.updateItemBag();
                }else{
                    p.sendAddchatYellow("Không thể nhặt vật phẩm của người khác");
                }
            }
        }
    }

    public int getItemMapID() {
        for (int i = 0; i < itemsMap.size(); i++) {
            if (!CheckItemIDExist(i)) {
                return i;
            }
        }
        return -1;
    }

    public void PlayerDropItem(Player p, Item item) {
        try {
            ItemMap itemMap = new ItemMap();
            itemMap.x = p.x;
            itemMap.y = p.y;
            itemMap.playerId = p.id;
            itemMap.itemTemplateID = (short) item.template.id;
            itemMap.item = item;
            Message m = new Message(68);
            for (int i = 0; i < itemsMap.size() + 1; i++) {
                if (!CheckItemIDExist(i)) {
                    itemMap.itemMapID = i;
                    itemsMap.add(itemMap);
                    sendMessage(m);
                    break;
                }
            }
            m.writer().writeShort(itemMap.itemMapID);
            m.writer().writeShort(itemMap.itemTemplateID);
            m.writer().writeShort(itemMap.x);
            m.writer().writeShort(itemMap.y);
            m.writer().writeInt(itemMap.playerId);

        } catch (Exception e) {
        }
    }
    //---
    public void VGo(Player p, Message m) throws IOException {
        m.cleanup();
        for (byte i = 0; i < map.template.wayPoints.length; i++) {
            WayPoint vg = map.template.wayPoints[i];

            if (p.x + 100 >= vg.minX && p.x <= vg.maxX + 100 && p.y + 100 >= vg.minY && p.y <= vg.maxY + 100) {

                leave(p);
                int mapid;
                mapid = vg.goMap;
                Map ma = Manager.getMapid(mapid);

                for (byte j = 0; j < ma.template.wayPoints.length; j++) {
                    WayPoint vg2 = ma.template.wayPoints[j];
                    if (vg2.goMap == map.id) {
                        p.x = (short) (vg2.goX);
                        p.y = (short) (vg2.goY);
                    }
                }
                byte errornext = -1;
                if (errornext == -1) {
                    for (byte j = 0; j < ma.area.length; j++) {
                        if (ma.area[j].players.size() < ma.template.maxplayers) {
                            p.map.id = mapid;
                            p.x = vg.goX;
                            p.y = vg.goY;
                            ma.area[j].Enter(p);

                            return;
                        }
                        if (j == ma.area.length - 1) {
                            errornext = 0;
                        }
                    }
                }

                Enter(p);
                switch (errornext) {
                    case 0:
                        p.sendAddchatYellow("Bản đồ quá tải.");
                        return;
                    case 1:
                        p.sendAddchatYellow("Trang bị thú cưới đã hết hạn. Vui lòng tháo ra để di chuển");
                        return;
                    case 2:
                        p.sendAddchatYellow("Cửa " + ma.template.name + " vẫn chưa mở");
                        return;
                }
            }

        }
    }

    public void Enter(Player pl) {
        synchronized (this) {
            Message msg;
            try {
                players.add(pl);
                pl.zone = this;
                msg = new Message(-24);
                msg.writer().writeByte(map.id);
                msg.writer().writeByte(map.template.planetId);
                msg.writer().writeByte(map.template.tileId);
                msg.writer().writeByte(map.template.bgId);
                msg.writer().writeByte(map.template.type);
                msg.writer().writeUTF(map.template.name);
                msg.writer().writeByte(id);
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
                    msg.writer().writeBoolean(wp.isOffline);
                    msg.writer().writeUTF(wp.name);
                }
                // Load mob class Map Template
                msg.writer().writeByte(mobs.size());
                for (short i = 0; i < mobs.size(); i++) {
                    Mob mob = mobs.get(i);
                    msg.writer().writeBoolean(false);
                    msg.writer().writeBoolean(false);
                    msg.writer().writeBoolean(false);
                    msg.writer().writeBoolean(false);
                    msg.writer().writeBoolean(false);
                    msg.writer().writeByte(mob.template.tempId);
                    msg.writer().writeByte(0);
                    msg.writer().writeInt(mob.hp);
                    msg.writer().writeByte(mob.level);
                    msg.writer().writeInt(mob.maxHp);
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
                //load item Drop In Map
                msg.writer().writeByte(itemsMap.size());
                for (ItemMap itemMap : itemsMap) {
                    msg.writer().writeShort(itemMap.itemMapID);
                    msg.writer().writeShort(itemMap.itemTemplateID);
                    msg.writer().writeShort(itemMap.x);
                    msg.writer().writeShort(itemMap.y);
                    msg.writer().writeInt(itemMap.playerId);
                }

                // bg item
                byte[] bgItem = FileIO.readFile("data/map/bg/" + map.id);
                msg.writer().write(bgItem);

                // eff item
                byte[] effItem = FileIO.readFile("data/map/eff/" + map.id);
                msg.writer().write(effItem);

                msg.writer().writeByte(map.bgType);
                msg.writer().writeByte(0);
                msg.writer().writeByte(0);

                pl.session.sendMessage(msg);
                pl.zone.joinMap(pl);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void joinMap(Player p) {
        //Player player = PlayerManger.gI().getPlayerByUserID(p.userId);
        Message msg;
        try {
            for (Player pl : players) {
                if (p == pl) {
                    return;
                }
                loadInfoPlayer(pl.session, p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Mob getMob(int id) {
        short i;
        for (i = 0; i < this.mobs.size(); i++) {
            if (this.mobs.get(i) != null && (this.mobs.get(i)).tempId == id) {
                return this.mobs.get(i);
            }
        }
        return null;
    }

    public void sendMyMessage(Player p, Message m) {

        for (Player player : players) {
            if (p.id != player.id) {
                player.session.sendMessage(m);
            }
        }
//        for (int i = players.size() - 1; i >= 0; i--) {
//            if (p.id != players.get(i).id) {
//                players.get(i).session.sendMessage(m);
//            }
//        }
    }

    public void sendMessage(Message m) {
        try {
            for (Player player : players) {
                if (player != null && player.session != null) {
                    player.session.sendMessage(m);
                }
            }
//            for (int i = this.players.size() - 1; i >= 0; --i) {
//                if (this.players.get(i) != null && (this.players.get(i)).session != null) {
//                    (this.players.get(i)).session.sendMessage(m);
//                }
//            }
//            m.cleanup();
        } catch (Exception var3) {
            var3.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }

    }

    private void MobStartDie(Player p, int dame, Mob mob, boolean fatal, ArrayList<ItemMap> itemDrops) throws IOException {
        try {
            Message m = new Message(-12);
            m.writer().writeByte(mob.tempId);
            m.writer().writeInt(dame);
            m.writer().writeBoolean(fatal);
            m.writer().writeByte(itemDrops.size());
            for (ItemMap itemMap : itemDrops) {
                m.writer().writeShort(itemMap.itemMapID);
                m.writer().writeShort(itemMap.item.template.id);
                m.writer().writeShort(mob.pointX);
                m.writer().writeShort(mob.pointY);
                m.writer().writeInt(p.id);
            }
            sendMessage(m);
            m.writer().flush();
            m.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void attachedMob(int dame, int mobid, boolean fatal) throws IOException {
        Message m = new Message(-9);
        m.writer().writeByte(mobid);
        Mob mob = getMob(mobid);
        m.writer().writeInt(mob.hp);
        m.writer().writeInt(dame);
        m.writer().writeBoolean(fatal);//flag
        //eff boss
        //5 khói
        m.writer().writeByte(-1);
        m.writer().flush();
        sendMessage(m);
    }

    public void PlayerDead(Player p) throws IOException {
        if (p.isdie = true) {
            Message m = new Message(-17);
            m.writer().writeByte(p.cPk);
            m.writer().writeShort(p.x);
            m.writer().writeShort(p.y);
            m.writer().flush();
            p.session.sendMessage(m);
            m.cleanup();
        } else {
            Message m = new Message(-16);
            m.writer().flush();
            p.session.sendMessage(m);
            m.cleanup();
        }
    }

//    public void attackMob(Session session, Player pl, int id) {
//        Message msg;
//        try {
//            msg = new Message(54);
//            msg.writer().writeInt(pl.id);
//            msg.writer().writeByte(pl.selectSkill.skillId);
//            msg.writer().writeByte(id);
//            session.sendMessage(msg);
//            msg.cleanup();
//
//        } catch (Exception e) {
//        }
//    }

    public void MOB_MAX_HP(Player p, int mob, int hp) throws IOException {
        Message m;
        try {
            m = new Message(-9);
            m.writer().writeByte(mob);
            m.writer().writeInt(hp);
            p.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
        }
    }

    public short getItemMapNotId() {
        short itemmapid = 0;
        while(true) {
            boolean isset = false;
            for(int i = this.itemsMap.size() - 1; i >= 0; --i) {
                if (this.itemsMap.get(i) != null && (this.itemsMap.get(i)).itemMapID == itemmapid) {
                    isset = true;
                }
            }
            if (!isset) {
                return itemmapid;
            }
            itemmapid++;
        }
    }
    
    public void FightMob(Player p, Message m) throws IOException {
        int mobId = m.reader().readByte();
        int damage = 0;
        Mob mob = getMob(mobId);
        Mob[] arMob = new Mob[10];
        arMob[0] = mob;
        int miss = Util.nextInt(0,10);

        if(miss < 8){
            damage = Util.nextInt(p.getDamFull(), (int) (p.getDamFull() * 1.2));
        }
        long upSmTn = (long) (damage * 30L);
        p.tiemNang += upSmTn;
        p.power += upSmTn;
        p.UpdateSMTN((byte) 2, upSmTn);
        mob.updateHP(-damage);
        int fantashi = Util.nextInt(p.getCritFull() , 100);
        boolean fatal = 5 > fantashi;
        if (mob.isDie) {
            int id = Util.nextInt(0,500);
            ArrayList<ItemMap> itemDrop = new ArrayList<>();
            Item itemMap = ItemSell.getItem(id);
            ItemMap item = new ItemMap();
            item.playerId = p.id;
            item.x = mob.pointX;
            item.y = mob.pointY;
            item.itemMapID = id;
            item.itemTemplateID = (short) item.itemMapID;
            itemMap.template = ItemTemplate.ItemTemplateID(id);
            item.item = itemMap;
            itemDrop.add(item);
            itemsMap.addAll(itemDrop);
            MobStartDie(p, damage, mob, fatal, itemDrop);
        } else {
            attachedMob(damage, mob.tempId, fatal);
//            this.loadMobAttached(mob.tempId);
        }
        m = new Message(54);
        m.writer().writeInt(p.id);
        m.writer().writeByte((byte)p.selectSkill[0].getSkillID());
        for (byte i = 0; i < arMob.length; i++) {
            m.writer().writeByte(arMob[i].tempId);
        }
        sendMyMessage(p, m);
        m.writer().flush();
        m.cleanup();
    }
//    public  void loadMobAttached(int modID){
//        Player player ;
//        short i;
//        for(i = 0; i < this.players.size(); i++) {
//            player = this.players.get(i);
//            player.upHP(-23);
//            this.MobAtkMessage(modID, player, 23, 12, (short)-1, (byte)-1, (byte)-1);
//            if(!mob.isboss && mob.lvboss == 0) {
//                break;
//            }
//        }
//
//    }
public void loadMobAttached(int mobid) {
    synchronized(this) {
        try {

            int damage = 0;
            Mob mob = getMob(mobid);

            if (mob != null) {

                long tFight = System.currentTimeMillis() + 1200L;
                if (mob.isboss) {
                    tFight = System.currentTimeMillis() + 500L;
                }

                mob.timeFight = tFight;
                int dame=0;
                if (mob.isboss) {
                    dame = Util.nextInt(7500,10000);

//                    if(this.map.getXHD() != -1) {
//                        dame = Util.nextInt(6000,8000);
//                    }
//                    else if(this.map.mapBossTuanLoc()) {
//                        dame = Util.nextInt(5000,6500);
//                    }
                }else {
                    dame = Util.nextInt(mob.hp/5, (int) (mob.hp /2.5));;
                }
                Player player;
                short i;
                for(i = 0; i < this.players.size(); i++) {
                    player = this.players.get(i);
                    if(player!=null){
                        if(!player.isdie){
                            short dx = 80;
                            short dy = 2;
                            if(Math.abs(player.x-mob.pointX)<dx&&Math.abs(player.y-mob.pointY)<dy){
                                dame = Util.nextInt(dame * 90 / 100, dame);
                                if (dame <= 0) {
                                    dame = 1;
                                }
                                int miss = Util.nextInt(0,10);
                                if (miss > Util.nextInt(8000)) {
                                    dame = 0;
                                }
                                player.upHP(-dame);

                                this.MobAtkMessage(mob.tempId, player, dame,0, (short)-1, (byte)-1, (byte)-1);
                                if(!mob.isboss && mob.level == 0) {
                                    break;
                                }
                            }
                        }
                    }else {
                        continue;
                    }

                }
            }


        } catch (Exception var18) {
            var18.printStackTrace();
            return;
        }

    }
}

    private void IceMobMessage(int mobid, int type) {
        Message m = null;
        try {
            Mob mob = this.getMob(mobid);
            if(mob != null) {
                switch(type) {
                    case -1:
                        mob.isIce = false;
                        break;
                    case 0:
                        mob.isIce = true;
                        mob.timeIce = System.currentTimeMillis() + 1500L;
                        break;
                    case 1:
                        mob.isIce = true;
                        mob.timeIce = System.currentTimeMillis() + 4000L;
                        break;
                    case 2:
                        mob.isIce = true;
                        mob.timeIce = System.currentTimeMillis() + 2000L;
                }
                m = new Message(90);
                m.writer().writeByte(mobid);
                m.writer().writeBoolean(mob.isIce);
                m.writer().flush();
                this.sendMessage(m);
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }

    }

    public void loadPlayers(Player p) {
        try {
            for (Player pl : players) {
                if (p != pl) {
                    loadInfoPlayer(p.session, pl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  void wakeUpDieReturn(Player p) {
        if(p != null && p.session != null && p.isdie ) {

                if (p.ngoc < 1) {
                    p.sendAddchatYellow("bạn không có đủ ngọc");
                } else {
                    p.isdie = false;
                    boolean isCanChat = false;
//                    String chat2 = m.reader().readUTF();
                    if (p.ngocKhoa >= 5) {
                        p.ngocKhoa = p.ngocKhoa - 1;
                        isCanChat = true;
                    } else if (p.ngoc >= 5) {
                        p.ngoc = p.ngoc - 5;
                        isCanChat = true;
                    } else {
                        isCanChat = true;
                    }
                    if(isCanChat){
//                        Service.gI().ChatGolbaL(p, chat2);
                        Service.gI().buyDone(p);
                    }
                    Service.gI().buyDone(p);
                    p.hp = p.getHpFull();
                    p.mp = p.getMpFull();
                    this.liveFromDead(p);
//                    p.liveFromDead();
                }
            } else {
            p.sendAddchatYellow("bạn không có đủ ngọc");
            }

    }

    public  void liveFromDead(Player p) {
        Message m = null;
        try {

           p.hp = p.getHpFull();
            p.mp = p.getMpFull();
           p.isdie = false;
            if(p.session != null) {
                m = new Message(-16);
                m.writer().flush();
                p.session.sendMessage(m);
                m.cleanup();
            }
            m = new Message(84);
            m.writer().writeInt(p.id);
            m.writer().writeShort(p.x);
            m.writer().writeShort(p.y);
            m.writer().flush();
            this.sendMyMessage(p, m);
            m.cleanup();
        } catch (Exception var2) {
            var2.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
    }

    public void loadInfoPlayer(Session _session, Player _player) {
        Message msg;
        try {
            msg = new Message(-5);
            msg.writer().writeInt(_player.id);
            if (_player.clan != null) {
                msg.writer().writeInt(_player.clan.id);
            } else {
                msg.writer().writeInt(-1);
            }
            msg.writer().writeByte(10);
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(_player.typePk);
            msg.writer().writeByte(_player.gender);
            msg.writer().writeByte(_player.gender);
            msg.writer().writeShort(_player.PartHead());
            msg.writer().writeUTF(_player.name);
            msg.writer().writeInt(_player.hpGoc);
            msg.writer().writeInt(_player.getHpFull());
            msg.writer().writeShort(_player.PartBody());
            msg.writer().writeShort(_player.Leg());
            msg.writer().writeByte(8);
            msg.writer().writeByte(-1);
            msg.writer().writeShort(_player.x);
            msg.writer().writeShort(_player.y);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeShort(_player.getMount());
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            _session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public void DieReturn(Player p) {
//        Message m = null;
//        try {
//            if (p.c.isInDun && p.c.dunId != -1) {
//                p.restCave();
//            }
//            this.leave(p);
//            p.c.get().isDie = false;
//            Map ma = Manager.getMapid(p.c.mapLTD);
//
//            TileMap area;
//            int var5;
//            for( var5 = 0; var5 < ma.area.length; var5++) {
//                area = ma.area[var5];
//                if (area.numplayers < ma.template.maxplayers) {
//                    area.EnterMap0(p.c);
//                    p.c.get().hp = p.c.get().getMaxHP();
//                    p.c.get().mp = p.c.get().getMaxMP();
//                    m = new Message(-30);
//                    m.writer().writeByte(-123);
//                    m.writer().writeInt(p.c.xu);
//                    m.writer().writeInt(p.c.yen);
//                    m.writer().writeInt(p.luong);
//                    m.writer().writeInt(p.c.get().getMaxHP());
//                    m.writer().writeInt(p.c.get().getMaxMP());
//                    m.writer().writeByte(0);
//                    m.writer().flush();
//                    p.conn.sendMessage(m);
//                    m.cleanup();
//                    m = new Message(57);
//                    m.writer().flush();
//                    p.conn.sendMessage(m);
//                    m.cleanup();
//                    return;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(m != null) {
//                m.cleanup();
//            }
//        }
//    }

    public void exitMap(Player _player, Map _map) {
        Message msg;
        try {
            for (int i = 0; i < _map.getPlayers().size(); i++) {
                Player player = _map.getPlayers().get(i);
                if (_player != player) {
                    msg = new Message(-6);
                    msg.writer().writeInt(_player.id);
                    player.session.sendMessage(msg);
                    msg.cleanup();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void MobAtkMessage(int mobid, Player n, int dame, int mpdown, short idskill_atk, byte typeatk, byte typetool) {
        Message m = null;
        try {
            m = new Message(-11);
            m.writer().writeByte(mobid);
            m.writer().writeInt(dame);
            m.writer().writeInt(mpdown);
            m.writer().writeShort(idskill_atk);
            m.writer().writeByte(typeatk);
            m.writer().writeByte(typetool);
            m.writer().flush();
            n.session.sendMessage(m);
            m.cleanup();
            m = new Message(-2);
            m.writer().writeByte(mobid);
            m.writer().writeInt(n.id);
            m.writer().writeInt(n.hp);
            m.writer().writeInt(mpdown);
            m.writer().writeShort(idskill_atk);
            m.writer().writeByte(typeatk);
            m.writer().writeByte(typetool);
            m.writer().flush();
            this.sendMyMessage(n, m);
            if (n.isdie ) {
                this.PlayerDead(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }

    }
    public void playerMove(Player _player) {
        Map map = _player.map;
        Message msg;
        try {
            for (Player player : players) {
                if (player != _player) {
                    msg = new Message(-7);
                    msg.writer().writeInt(_player.id);
                    msg.writer().writeShort(_player.x);
                    msg.writer().writeShort(_player.y);
                    player.session.sendMessage(msg);
                    msg.cleanup();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chat(Player player, String _text) {
        Message msg;
        try {
            for (Player pl : players) {
                msg = new Message(44);
                msg.writer().writeInt(player.id);
                msg.writer().writeUTF(_text);
                pl.session.sendMessage(msg);
                msg.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chatTG(Session _session, String _text) {
        Player player = PlayerManger.gI().getPlayerByUserID(_session.userId);
        Map map = player.map;
        Message msg;
        try {
            for (Player pl : players) {
                msg = new Message(92);
                msg.writer().writeInt(player.id);
                msg.writer().writeUTF(_text);
                pl.session.sendMessage(msg);
                msg.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void MagicTree(Session _session, byte _text) {
//        Player player = PlayerManger.gI().getPlayerByUserID(_session.userId);
//        Map map = player.map;
        Message msg;
        try {
            for (Player pl : players) {
                msg = new Message(-34);
                msg.writer().writeByte(_text);
                pl.session.sendMessage(msg);
                msg.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectUIZone(Player p, Message m) throws IOException {
        byte zoneid = m.reader().readByte();
//        if (zoneid == id) {
//            p.sendAddchatYellow("Bạn đã ở khu vực: " + zoneid);
//            return;
//        }
        leave(p);
        map.area[zoneid].Enter(p);
        m = new Message(21);
        p.session.sendMessage(m);
        m.writer().flush();
        m.cleanup();
    }

    public void openUIZone(Player p) throws IOException {
        if (p == null) {
            return;
        }
        final Message m = new Message(29);
        m.writer().writeByte(this.map.area.length);
        for (id = 0; id < this.map.area.length; id++) {
            m.writer().writeByte(id);
            m.writer().writeByte(0);
            m.writer().writeByte(this.map.area[id].getNumplayers());
            m.writer().writeByte(this.map.template.maxplayers);
            m.writer().writeByte(0);
        }
        m.writer().flush();
        p.session.sendMessage(m);
        m.cleanup();
    }

    public byte getNumplayers() {
        return (byte) this.players.size();
    }

    public void LiveFromDead(Player p) {
        Message m = null;
        try {
            m = new Message(-16);
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

    public void GET_PLAYER_MENU(Player p, byte bit) throws IOException {
        Message m = new Message(63);
        m.writer().writeByte(bit);
        p.session.sendMessage(m);
        m.cleanup();
    }
     public void refreshMob(int mobid) {
        try {
            synchronized (this) {
                Mob mob = getMob(mobid);
                mob.hp = mob.maxHp = mob.template.maxHp;
                mob.status = 5;
                mob.isDie = false;
                mob.timeRefresh = 0;
                Message m = new Message(-13);
                m.writer().writeByte(mob.tempId);
                m.writer().writeByte((byte)Util.nextInt(0,3));
                m.writer().writeByte(0);
                m.writer().writeInt(mob.hp);
                m.writer().flush();
                sendMessage(m);
                m.cleanup();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        synchronized (this) {
            int i;
            Mob mob;
            try {
            for ( i = mobs.size() - 1; i >= 0; i--) {
                     mob= mobs.get(i);
                    if (mob.timeRefresh > 0 && System.currentTimeMillis() >= mob.timeRefresh && mob.isRefresh) {
                        refreshMob(mob.tempId);
                    }

                    if (!mob.isDie&&mob.status != 0 && mob.level != 1 && System.currentTimeMillis() >= mob.timeFight) {
                        this.loadMobAttached(mob.tempId);
                    }
            }
            } catch (Exception var14) {
                var14.printStackTrace();
            }

        }
    }

    public void close() {
    }
}
