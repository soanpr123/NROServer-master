package real.player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import real.clan.Clan;
import real.clan.ClanManager;
import real.item.Item;

import static real.item.ItemDAO.loadOptionsItem;
import static real.skill.Skills.skills;

import real.item.ItemOption;
import real.item.ItemSell;
import real.item.ItemTemplate;
import real.item.ItemTemplates;

import real.item.useItem;
import real.map.Map;
import real.map.Zone;
import real.map.WayPoint;
import real.skill.*;
import server.*;
import server.io.Message;
import server.io.Session;

public class Player {

    public Zone zone = null;
    public Session session;
    public int id;
    public int account_id;
    public Map map;
    public int menuNPCID = -1;
    public int menuID = -1;
    public short x;
    public short y;
    public String name;
    public short taskId;
    public byte taskIndex;
    public byte gender;
    public short head;
    public long power;
    public int vang;
    public int ngocKhoa;
    public int ngoc;
    public int hpGoc;
    public int mpGoc;
    public int hp;
    public int mp;
    public int damGoc;
    public short defGoc;
    public byte critGoc;
    public byte typePk;
    public byte limitPower;
    public long tiemNang;
    public NClass nClass;
    public Clan clan;
    public Skill[] selectSkill;
    public byte[] KSkill = null;
    public byte[] OSkill = null;
    public short CSkill = -1;
    public byte maxluggage = 30;
    public byte maxBox = 30;
    public Item[] ItemBag = null;
    public Item[] ItemBox = null;
    public Item[] ItemBody = null;
    public ArrayList<Player> nearPlayers;
    public int mobAtk = -1;
    public boolean isdie = false;
    public byte cPk = 0;
    public ArrayList<Skill> skill;
    public long CSkilldelay = 0;
    public static short[][] infoId = {{281, 361, 351}, {512, 513, 536}, {514, 515, 537}};
    public Detu detu;
    public byte petfucus = 0;
    public int hpFull;
    public int mpFull;
    public int damFull;
    public byte critFull;
    public short defFull;
    public int NhapThe = 0;

    public Player() {
        this.ItemBag = null;
        this.ItemBody = null;
        this.ItemBox = null;
        this.nearPlayers = new ArrayList<>();
        this.skill = new ArrayList<>();
    }

    public Zone getPlace() {
        return zone;
    }

    public Skill getSkill(int id) {
        for (Skill skl : this.skill) {
            if ((int) skl.getSkillID() == id) {
                return skl;
            }
        }
        return null;
    }

    public short getDefaultBody() {
        if (this.gender == 0) {
            return 57;
        } else if (this.gender == 1) {
            return 59;
        } else if (this.gender == 2) {
            return 57;
        }
        return -1;
    }

    public synchronized void upHP(int hpup) {
        if (this.isdie) {
            return;
        }
        this.hp += hpup;
        if (this.hp > this.getHpFull()) {
            this.hp = this.getHpFull();
        }
        if (this.hp <= 0) {
            this.isdie = true;
            this.hp = 0;
        }
    }

    public int getLeverSkill(int id) {
        for (int i = 0; i < this.skill.size(); ++i) {
            Util.log("is----------------->" + this.skill.get(i).getSkillID());
            Util.log("is----------------->ID" + id);
            if (this.skill.get(i).getSkillID() ==id-1) {
                return (int) this.skill.get(i).getPoint();
            }else  {

            }
        }
        return -1;
    }

    public void removeList(int id) {
        for (int i = 0; i < this.skill.size(); ++i) {
            if (this.skill.get(i).getSkillID() == id-1) {
                this.skill.remove(i);
            }
        }

    }

    public void useSkillNotForcus(Player player) throws IOException {

        Util.log("skill--------Idsss--------> " + player.selectSkill[0].getSkillTemplate().getType());

        SkillTemplate template = player.selectSkill[0].getSkillTemplate();
        Util.log("skill--------Levesssr--------> " + player.selectSkill[0].getPoint());
        Message m;
        m = new Message(-30);
        m.writer().writeByte(14);
        m.writer().writeByte((int) player.selectSkill[0].getSkillID());
//                m.writer().writeShort(skill.point);/''

        m.writer().flush();

        this.session.sendMessage(m);
//        m = new Message(-128);
////        m.writer().writeByte(15);
//        m.writer().writeShort((int) player.selectSkill[0].getSkillID());
////                m.writer().writeShort(skill.point);
//
//        m.writer().flush();

//        this.session.sendMessage(m);
        switch ((int) template.getType()) {

            case 3:

                break;
            default:

                break;
        }
    }

    public synchronized void upHPGOhome(int hpup) {

        this.hp += hpup;

        if (this.hp > this.getHpFull()) {
            this.hp = this.getHpFull();

        }
        if (this.hp > 0) {
            this.isdie = false;
        }
        this.zone.liveFromDead(this);
    }

    public short getDefaultLeg() {
        if (this.gender == 0) {
            return 58;
        } else if (this.gender == 1) {
            return 60;
        } else if (this.gender == 2) {
            return 58;
        }
        return -1;
    }

    public short PartHead() {
        if (NhapThe == 1 && gender == 1) {
            return 391;
        }
        if (NhapThe == 1 && (gender == 0 || gender == 2)) {
            return 383;
        }
        if (this.ItemBody[5] != null && NhapThe == 0) {
            for (Item iad : ItemBody[5].entrys) {
                if (ItemBody[5].id == iad.idTemp) {
                    return (short) iad.headTemp;
                }
            }
        }
        return head;
    }

    public short PartBody() {
        if (NhapThe == 1 && (gender == 0 || gender == 2)) {
            return (short) (PartHead() + 1);
        }
        if (this.ItemBody[5] != null && NhapThe == 0) {
            return (short) (PartHead() + 1);
        }
        if (this.ItemBody[0] == null) {
            return getDefaultBody();
        }
        return ItemTemplate.ItemTemplateID(this.ItemBody[0].id).part;
    }

    public short Leg() {
        if (NhapThe == 1 && (gender == 0 || gender == 2)) {
            return (short) (PartHead() + 2);
        }
        if (this.ItemBody[5] != null && NhapThe == 0) {
            return (short) (PartHead() + 2);
        }
        if (this.ItemBody[1] == null) {
            return getDefaultLeg();
        }
        return ItemTemplate.ItemTemplateID(this.ItemBody[1].id).part;
    }

    public int getMount() {
        for (Item item : ItemBag) {
            if (item != null && item.id != -1) {
                if (item.template.type == 23 || item.template.type == 24) {
                    return item.template.id;
                }
            }
        }
        return -1;
    }

    //    public int getPramSkill(int id) {
//        try {
//            if (this == null) {
//                return 0;
//            }
//            int param = 0;
//            SkillTemplate data;
//            SkillOptionTemplate temp;
//            for (Skill sk : this.skill) {
//                data = SkillTemplate.Templates(sk.skillId);
//                if (data.type == 0 || data.type == 2 || sk.skillId == this.CSkill || data.type == 4 || data.type == 3) {
//                    temp = SkillTemplate.Templates((byte) sk.skillId, (byte) sk.point);
//                    for (Option op : temp.options) {
//                        if (op.id == id) {
//                            param += op.param;
//                            break;
//                        }
//                    }
//                }
//            }
//            return param;
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//    public int percentIce() {
//        return this.getPramSkill(69);
//    }
    public int getHpFull() {
        int hp = hpFull;
        if (ItemBody[1] != null) {
            hp += ItemBody[1].getParamItemByID(6);
        }
        if (ItemBody[5] != null) {
            hp += hp * ItemBody[5].getParamItemByID(77) / 100;
        }
        return hp;
    }

    public int getMpFull() {
        int mp = mpFull;
        if (ItemBody[3] != null) {
            mp += ItemBody[3].getParamItemByID(7);
        }
        if (ItemBody[5] != null) {
            mp += mp * ItemBody[5].getParamItemByID(103) / 100;
        }
        return mp;
    }

    public void ChatBunma() {
        try {
            while (true) {
                Calendar calendar = Calendar.getInstance();
                int sec = calendar.get(13);
                if ((sec % 20 == 0 || sec == 0)) {
                    for (int i = 0; i < PlayerManger.gI().conns.size(); i++) {
                        if (PlayerManger.gI().conns.get(i) != null && PlayerManger.gI().conns.get(i).player != null) {
                            Player player = PlayerManger.gI().conns.get(i).player;
                            if (player != null && player.map == map) {
                                player.zone.chat(player, "Bunma đẹp troaiiiii=))");
                            }
                        }
                    }
                }
                Thread.sleep(1000L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDamFull() {
        int dam = damFull;
        if (ItemBody[2] != null) {
            dam += ItemBody[2].getParamItemByID(0);
        }
        if (ItemBody[5] != null) {
            dam += dam * ItemBody[5].getParamItemByID(50) / 100;
        }
        return dam;
    }

    public short getDefFull() {
        short def = defFull;
        if (ItemBody[0] != null) {
            def += ItemBody[0].getParamItemByID(47);
        }
        return def;
    }

    public byte getCritFull() {
        byte crit = critFull;
        if (ItemBody[4] != null) {
            crit += ItemBody[4].getParamItemByID(14);
        }
        if (ItemBody[5] != null) {
            crit += crit * ItemBody[5].getParamItemByID(14) / 100;
        }
        return crit;
    }

    public byte getSpeed() {
        return 7;
    }

    public void updateVangNgocHPMP() {
        Message m;
        try {
            m = new Message(-30);
            m.writer().writeByte(4);
            m.writer().writeInt(this.vang);
            m.writer().writeInt(this.ngoc);
            m.writer().writeByte(this.hp);
            m.writer().writeByte(this.mp);
            m.writer().writeInt(this.ngocKhoa);
            this.session.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
        }

    }

    public void updateItemBag() {
        Message msg;
        try {
            Item[] itemsBody = this.ItemBag;
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
                    msg.writer().writeByte(item.itemOptions.size());
                    for (ItemOption itemOption : item.itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }

            }
            this.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateItemBox() {
        Message msg;
        try {
            Item[] itemsBody = this.ItemBox;
            msg = new Message(-35);
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
                    msg.writer().writeByte(item.itemOptions.size());
                    for (ItemOption itemOption : item.itemOptions) {
                        msg.writer().writeByte(itemOption.optionTemplate.id);
                        msg.writer().writeShort(itemOption.param);
                    }
                }

            }
            this.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte getBoxNull() {
        byte num = 0;
        for (byte i = 0; i < this.ItemBox.length; ++i) {
            if (this.ItemBox[i] == null) {
                num++;
            }
        }
        return num;
    }

    public byte getBagNull() {
        byte num = 0;
        for (byte i = 0; i < this.ItemBag.length; ++i) {
            if (this.ItemBag[i] == null) {
                num++;
            }
        }
        return num;
    }

    public Item getIndexBag(final int index) {
        if (index < this.ItemBag.length && index >= 0) {
            return this.ItemBag[index];
        }
        return null;
    }

    public Item getIndexBox(final int index) {
        if (index < this.ItemBox.length && index >= 0) {
            return this.ItemBox[index];
        }
        return null;
    }

    protected Item getItemIdBag(final int id) {
        for (int i = 0; i < this.ItemBag.length; ++i) {
            final Item item = this.ItemBag[i];
            if (item != null && item.id == id) {
                return item;
            }
        }
        return null;
    }

    public byte getIndexBagid(int id) {
        byte i;
        Item item;
        for (i = 0; i < this.ItemBag.length; ++i) {
            item = this.ItemBag[i];
            if (item != null && item.id == id) {
                return i;
            }
        }
        return -1;
    }

    public byte getIndexBoxid(final int id) {
        for (byte i = 0; i < this.ItemBox.length; ++i) {
            final Item item = this.ItemBox[i];
            if (item != null && item.id == id) {
                return i;
            }
        }
        return -1;
    }

    protected int getIndexBagItem(final int id) {
        for (int i = 0; i < this.ItemBag.length; ++i) {
            final Item item = this.ItemBag[i];
            if (item != null && item.id == id) {
                return i;
            }
        }
        return -1;
    }

    public byte getIndexBagNotItem() {
        byte i;
        Item item;
        for (i = 0; i < this.ItemBag.length; ++i) {
            item = this.ItemBag[i];
            if (item == null) {
                return i;
            }
        }

        return -1;
    }

    protected byte getIndexBoxNotItem() {
        for (byte i = 0; i < this.ItemBox.length; ++i) {
            final Item item = this.ItemBox[i];
            if (item == null) {
                return i;
            }
        }
        return -1;
    }

    protected byte getIndexBody() {
        for (byte i = 0; i < this.ItemBody.length; ++i) {
            final Item item = this.ItemBody[i];
            if (item == null) {
                return i;
            }
        }
        return -1;
    }

    public byte getAvailableBag() {
        byte num = 0;
        for (int i = 0; i < this.ItemBag.length; ++i) {
            if (this.ItemBag[i] == null) {
                ++num;
            }
        }
        return num;
    }

    public Boolean addItemToBag(Item item) {
        try {
            byte index = this.getIndexBagid(item.id);
            if (index != -1 && (item.id == 595 || item.id == 194)) {
                Item item2 = this.ItemBag[index];
                item.quantity = item2.quantity + item.quantityTemp;
                this.ItemBag[index] = item;
                return true;
            } else {
                index = this.getIndexBagNotItem();
                if (getBagNull() == 0) {
                    this.sendAddchatYellow("Hành trang không đủ chỗ trống!");
                    return false;
                } else {
                    this.ItemBag[index] = item;
                    return true;
                }
            }
        } catch (Exception var6) {
            var6.printStackTrace();
            return false;
        }

    }

    public void itemBagToBox(int index) {
        Item item = getIndexBag(index);
        if (item != null) {
            byte indexBox = getIndexBoxid(item.id);
            if (indexBox != -1 && (item.id == 595 || item.id == 194)) {
                removeItemBag(index);
                Item item2 = ItemBox[indexBox];
                item2.quantity += item.quantity;
            } else {
                if (getBoxNull() <= 0) {
                    this.sendAddchatYellow("Rương đồ không đủ chỗ trống");
                    return;
                }
                indexBox = getIndexBoxNotItem();
                removeItemBag(index);
                this.ItemBox[indexBox] = item;
                item.quantity = item.quantityTemp;
            }
            Service.gI().updateItemBox(this);
            Service.gI().updateItemBag(this);
            item.quantity = item.quantityTemp;
        }
    }

    public void itemBoxToBag(int index) {
        Item item = getIndexBox(index);
        if (item != null) {
            byte indexBag = getIndexBagid(item.id);
            if (indexBag != -1 && (item.id == 595 || item.id == 194)) {
                removeItemBox(index);
                Item item2 = ItemBag[indexBag];
                item2.quantity += item.quantity;
            } else {
                if (getBagNull() <= 0) {
                    this.sendAddchatYellow("Hành trang không đủ chỗ trống");
                    return;
                }
                indexBag = getIndexBagNotItem();
                removeItemBox(index);
                ItemBag[indexBag] = item;
            }
            Service.gI().updateItemBag(this);
            Service.gI().updateItemBox(this);
        }
    }

    public void useItemBody(Item item, short indexItemBag) {
        int index = -1;
        if (item != null && item.id != -1) {
            if (item.template.gender == this.gender || item.template.type == 5 || item.template.gender == 3) {
                if (item.template.strRequire <= this.power) {
                    if (item.template.type >= 0 && item.template.type <= 11) {
                        index = item.template.type;
                    }
                    if (item.template.type == 32) {
                        index = 6;
                    }
                } else {
                    Service.gI().serverMessage(this.session, "Sức mạnh không đủ yêu cầu");
                }
            } else {
                Service.gI().serverMessage(this.session, "Sai hành tinh");
            }
        }
        if (index != -1) {
            if (this.ItemBody[index] != null) {
                this.ItemBag[indexItemBag] = this.ItemBody[index];
            } else {
                this.ItemBag[indexItemBag] = null;
            }
            this.ItemBody[index] = item;
        }
    }

    public void itemBodyToBag(int index) {
        if (getBagNull() == 0) {
            sendAddchatYellow("Hành trang không đủ chỗ trống");
            return;
        }
        Item _item = this.ItemBody[index];
        if (_item != null) {
            byte indexBag = getIndexBagNotItem();
            removeItemBody(index);
            this.ItemBag[indexBag] = _item;
            Service.gI().updateItemBody(this);
            Service.gI().updateItemBag(this);
            Service.gI().loadPoint(session, this);
            if (ItemBody[5] != null || NhapThe == 1) {
                Service.gI().LoadCaiTrang(this, 1, PartHead(), PartHead() + 1, PartHead() + 2);
            } else {
                Service.gI().LoadCaiTrang(this, 1, PartHead(), PartBody(), Leg());
            }
        }
    }

    public void itemBagToBody(byte index) throws IOException {
        Item item = this.ItemBag[index];
        useItemBody(item, index);
        Service.gI().updateItemBag(this);
        Service.gI().updateItemBody(this);
        Service.gI().loadPoint(session, this);
        if (ItemBody[5] != null || NhapThe == 1) {
            Service.gI().LoadCaiTrang(this, 1, PartHead(), PartHead() + 1, PartHead() + 2);
        } else {
            Service.gI().LoadCaiTrang(this, 1, PartHead(), PartBody(), Leg());
        }
    }

    public void removeItemBag(int index) {
        this.ItemBag[index] = null;
    }

    public void loadBox() throws IOException {
        Message m;
        m = new Message(-35);
        m.writer().writeByte(0);
        m.writer().writeByte(this.ItemBox.length);
        for (int i = 0; i < this.ItemBox.length; i++) {
            m.writer().writeShort(this.ItemBox[i].id);
            m.writer().writeInt(this.ItemBox[i].quantity);
            m.writer().writeUTF(this.ItemBox[i].getInfo());
            m.writer().writeUTF(this.ItemBox[i].getContent());
            m.writer().writeByte(this.ItemBox[i].itemOptions.size());
            for (int j = 0; j < this.ItemBox[i].itemOptions.size(); j++) {
                m.writer().writeByte(0);
                m.writer().writeShort(0);
            }
        }
        this.session.sendMessage(m);
        m.cleanup();
    }


    public void UpdateSMTN(byte type, long amount) {
        Message msg;
        try {
            msg = new Message(-3);
            msg.writer().writeByte(type);
            msg.writer().writeInt((int) (amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : amount));
            this.session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void openBox() throws IOException {
        Message m;
        m = new Message(-35);
        m.writer().writeByte(1);
        this.session.sendMessage(m);
        m.cleanup();
    }

    public synchronized void removeItemBag(byte index, int quantity) {
        Item item = getIndexBag(index);
        try {
            item.quantity -= quantity;
            Message m = new Message(69);
            m.writer().writeByte(index);
            m.writer().writeShort(quantity);
            m.writer().flush();
            this.session.sendMessage(m);
            m.cleanup();
            if (item.quantity <= 0) {
                this.ItemBag[index] = null;
            }
        } catch (IOException iOException) {
        }
    }

    public void removeItemBody(int index) {
        this.ItemBody[index] = null;
    }
//    

    public void removeItemBox(int index) {
        this.ItemBox[index] = null;
    }

    public void increasePoint(byte type, short point) {
        if (point <= 0) {
            return;
        }
        long tiemNangUse = 0;
        if (type == 0) {
            int pointHp = point * 20;
            tiemNangUse = point * (2 * (this.hpGoc + 1000) + pointHp - 20) / 2;
            if ((this.hpGoc + pointHp) <= getHpMpLimit()) {
                if (useTiemNang(tiemNangUse)) {
                    hpGoc += pointHp;
                    hpFull = hpGoc;
                }
            } else {
                Service.gI().serverMessage(this.session, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 1) {
            int pointMp = point * 20;
            tiemNangUse = point * (2 * (this.mpGoc + 1000) + pointMp - 20) / 2;
            if ((this.mpGoc + pointMp) <= getHpMpLimit()) {
                if (useTiemNang(tiemNangUse)) {
                    mpGoc += pointMp;
                    mpFull = mpGoc;
                }
            } else {
                Service.gI().serverMessage(this.session, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 2) {
            tiemNangUse = point * (2 * this.damGoc + point - 1) / 2 * 100;
            if ((this.damGoc + point) <= getDamLimit()) {
                if (useTiemNang(tiemNangUse)) {
                    damGoc += point;
                    damFull = damGoc;
                }
            } else {
                Service.gI().serverMessage(this.session, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 3) {
            tiemNangUse = 2 * (this.defGoc + 5) / 2 * 100000;
            if ((this.defGoc + point) <= getDefLimit()) {
                if (useTiemNang(tiemNangUse)) {
                    defGoc += point;
                    defFull = defGoc;
                }
            } else {
                Service.gI().serverMessage(this.session, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 4) {
            tiemNangUse = 50000000L;
            for (int i = 0; i < this.critGoc; i++) {
                tiemNangUse *= 5L;
            }
            if ((this.critGoc + point) <= getCrifLimit()) {
                if (useTiemNang(tiemNangUse)) {
                    critGoc += point;
                    critFull = critGoc;
                }
            } else {
                Service.gI().serverMessage(this.session, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        Service.gI().loadPoint(this.session, this);
    }

    public boolean useTiemNang(long tiemNang) {
        if (this.tiemNang < tiemNang) {
            Service.gI().serverMessage(this.session, "Bạn không đủ tiềm năng");
            return false;
        }
        if (this.tiemNang >= tiemNang) {
            this.tiemNang -= tiemNang;
            return true;
        }
        return false;
    }

    public int getHpMpLimit() {
        if (limitPower == 0) {
            return 220000;
        }
        if (limitPower == 1) {
            return 240000;
        }
        if (limitPower == 2) {
            return 300000;
        }
        if (limitPower == 3) {
            return 350000;
        }
        if (limitPower == 4) {
            return 400000;
        }
        if (limitPower == 5) {
            return 450000;
        }
        return 0;
    }

    public int getDamLimit() {
        if (limitPower == 0) {
            return 11000;
        }
        if (limitPower == 1) {
            return 12000;
        }
        if (limitPower == 2) {
            return 15000;
        }
        if (limitPower == 3) {
            return 18000;
        }
        if (limitPower == 4) {
            return 20000;
        }
        if (limitPower == 5) {
            return 22000;
        }
        return 0;
    }

    public short getDefLimit() {
        if (limitPower == 0) {
            return 550;
        }
        if (limitPower == 1) {
            return 600;
        }
        if (limitPower == 2) {
            return 700;
        }
        if (limitPower == 3) {
            return 800;
        }
        if (limitPower == 4) {
            return 100;
        }
        if (limitPower == 5) {
            return 22000;
        }
        return 0;
    }

    public byte getCrifLimit() {
        if (limitPower == 0) {
            return 5;
        }
        if (limitPower == 1) {
            return 6;
        }
        if (limitPower == 2) {
            return 7;
        }
        if (limitPower == 3) {
            return 8;
        }
        if (limitPower == 4) {
            return 9;
        }
        if (limitPower == 5) {
            return 10;
        }
        return 0;
    }

    public void move(short _toX, short _toY) {
        if (_toX != this.x) {
            this.x = _toX;
        }
        if (_toY != this.y) {
            this.y = _toY;
        }
        this.zone.playerMove(this);
    }

    public void update() {
        this.hp = this.getHpFull();
        this.mp = this.getMpFull();
        Service.gI().loadPoint(this.session, this);
    }

    public void sortBag() throws IOException {
        try {
            int i;
            for (i = 0; i < ItemBag.length; i = (i + 1)) {
                if (ItemBag[i] != null && !(ItemBag[i]).isExpires && (ItemTemplate.ItemTemplateID(ItemBag[i].id)).isUpToUp) {
                    for (int j = (i + 1); j < ItemBag.length; j = (j + 1)) {
                        if (ItemBag[j] != null && !(ItemBag[i]).isExpires && (ItemBag[j]).id == (ItemBag[i]).id) {
                            (ItemBag[i]).quantity += (ItemBag[j]).quantity;
                            ItemBag[j] = null;
                        }
                    }
                }
            }

            for (i = 0; i < ItemBag.length; i = (i + 1)) {
                if (ItemBag[i] == null) {
                    for (int j = (i + 1); j < ItemBag.length; j = j + 1) {
                        if (ItemBag[j] != null) {
                            ItemBag[i] = ItemBag[j];
                            ItemBag[j] = null;
                            break;
                        }
                    }
                }
            }
        } catch (Exception exception) {
        }
        final Message m = new Message(-30);
        m.writer().writeByte(18);
        m.writer().flush();
        this.session.sendMessage(m);
        m.cleanup();
    }

    public void sortBox() throws IOException {
        for (byte i = 0; i < this.ItemBox.length; ++i) {
            if (this.ItemBox[i] != null && !this.ItemBox[i].isExpires && ItemTemplate.ItemTemplateID(this.ItemBox[i].id).isUpToUp) {
                for (byte j = (byte) (i + 1); j < this.ItemBox.length; ++j) {
                    if (this.ItemBox[j] != null && !this.ItemBox[i].isExpires && this.ItemBox[j].id == this.ItemBox[i].id) {
                        final Item item = this.ItemBox[i];
                        item.quantity += this.ItemBox[j].quantity;
                        this.ItemBox[j] = null;
                    }
                }
            }
        }
        for (byte i = 0; i < this.ItemBox.length; ++i) {
            if (this.ItemBox[i] == null) {
                for (byte j = (byte) (i + 1); j < this.ItemBox.length; ++j) {
                    if (this.ItemBox[j] != null) {
                        this.ItemBox[i] = this.ItemBox[j];
                        this.ItemBox[j] = null;
                        break;
                    }
                }
            }
        }
        final Message m = new Message(-30);
        m.writer().writeByte(19);
        m.writer().flush();
        this.session.sendMessage(m);
        m.cleanup();
    }

    //chim thông báo
    public void sendAddchatYellow(String str) {
        try {
            Message m = new Message(-25);
//            m.writer().writeUTF(this.name);
            m.writer().writeUTF(str);
            m.writer().flush();
            session.sendMessage(m);
            m.cleanup();
        } catch (IOException ex) {
        }
    }

    public void requestItem(int typeUI) throws IOException {
        Message m = new Message(23);
        m.writer().writeByte(typeUI);
        m.writer().flush();
        session.sendMessage(m);
        m.cleanup();
    }

    public static Player setup(int account_id) {
        try {
            synchronized (Server.LOCK_MYSQL) {
                Connection conn = DBService.gI().getConnection();
//        try {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM `player` WHERE `account_id`LIKE " + account_id);
                ResultSet rs = ps.executeQuery();
//                 rs = SQLManager.stat.executeQuery("SELECT * FROM `player` WHERE `account_id`LIKE'" + account_id + "';");
                if (rs != null && rs.first()) {
                    Player player = new Player();
                    player.account_id = rs.getInt("account_id");
                    player.id = rs.getInt("id");
                    player.taskId = rs.getByte("task_id");
                    player.name = rs.getString("name");
                    player.head = rs.getShort("head");
                    player.gender = rs.getByte("gender");
                    player.nClass = SkillData.nClasss[player.gender];
                    player.power = rs.getLong("power");
                    player.vang = rs.getInt("vang");
                    player.ngoc = rs.getInt("luong");
                    player.ngocKhoa = rs.getInt("luong_khoa");
                    player.x = rs.getShort("where_x");
                    player.y = rs.getShort("where_y");
                    if (rs.getInt("clan_id") != -1) {
                        player.clan = ClanManager.gI().getClanById(rs.getInt("clan_id"));
                    }
                    Map map = Manager.getMapid(rs.getByte("where_id"));
                    map.getPlayers().add(player);
                    player.map = map;
                    player.hpGoc = rs.getInt("hp_goc");
                    player.mpGoc = rs.getInt("mp_goc");
                    player.hp = player.hpGoc;
                    player.mp = player.mpGoc;
                    player.damGoc = rs.getInt("dame_goc");
                    player.defGoc = rs.getShort("def_goc");
                    player.critGoc = rs.getByte("crit_goc");
                    player.tiemNang = rs.getLong("tiem_nang");
                    player.limitPower = rs.getByte("limit_power");
                    player.hpFull = rs.getInt("hp_goc");
                    player.mpFull = rs.getInt("mp_goc");
                    player.damFull = rs.getInt("dame_goc");
                    player.defFull = rs.getShort("def_goc");
                    player.NhapThe = rs.getInt("nhapthe");
                    player.critFull = rs.getByte("crit_goc");
                    JSONArray jar = (JSONArray) JSONValue.parse(rs.getString("skill"));
                    Util.log("skiii-----------------------------------------> " + jar);
                    JSONObject job;
                    int index;
                    if (jar != null) {
                        job = null;
                        for (index = 0; index < jar.size(); index++) {
                            Skill skill = new Skill();
                            job = (JSONObject) JSONValue.parse(jar.get(index).toString());
                            int id = Integer.parseInt(job.get("id").toString());
                            int level = Integer.parseInt(job.get("point").toString());
                            skill.setSkillID(id);
                            skill.setPoint(level);
                            skill = player.nClass.getSkillTemplate((int)skill.getSkillID()).getSkills()[(int)skill.getPoint() - 1];
                            player.skill.add(skill);
                            job.clear();
                        }
                    }
                    player.maxluggage = rs.getByte("maxluggage");
                    player.maxBox = rs.getByte("maxbox");
                    JSONObject job2 = null;
                    player.ItemBag = new Item[player.maxluggage];
                    jar = (JSONArray) JSONValue.parse(rs.getString("itembag"));
                    int j;
                    if (jar != null) {
                        for (j = 0; j < jar.size(); ++j) {
                            job2 = (JSONObject) jar.get(j);
                            index = Byte.parseByte(job2.get("index").toString());
                            player.ItemBag[index] = ItemTemplate.parseItem(jar.get(j).toString());
                            job2.clear();
                        }
                    }

                    player.ItemBox = new Item[player.maxBox];
                    jar = (JSONArray) JSONValue.parse(rs.getString("itembox"));
                    if (jar != null) {
                        for (j = 0; j < jar.size(); ++j) {
                            job2 = (JSONObject) jar.get(j);
                            index = Byte.parseByte(job2.get("index").toString());
                            player.ItemBox[index] = ItemTemplate.parseItem(jar.get(j).toString());
                            job2.clear();
                        }
                    }

                    player.ItemBody = new Item[7];
                    jar = (JSONArray) JSONValue.parse(rs.getString("itembody"));
                    if (jar != null) {
                        for (j = 0; j < jar.size(); ++j) {
                            job2 = (JSONObject) jar.get(j);
                            index = Byte.parseByte(job2.get("index").toString());
                            player.ItemBody[index] = ItemTemplate.parseItem(jar.get(j).toString());
                            job2.clear();
                        }
                    }
                    rs.close();
                    return player;
                } else {
                    return null;
                }
            }
        } catch (Exception var23) {
            var23.printStackTrace();
            return null;
        }
    }

    public void luongMessage(long luongup) {
        Message m = null;
        try {
            if (this.session != null) {
                this.upluong(luongup);
                m = new Message(-30);
                m.writer().writeByte(-72);
                m.writer().writeInt(this.ngoc);
                m.writer().flush();
                this.session.sendMessage(m);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }

    }
    public boolean ItemKhien(Item item){
        return item.id == 434 || item.id == 435||item.id == 436||item.id == 437||item.id == 438||item.id == 439 || item.id == 440;
    }
    public int getLevelSkill(int ID){
        for(int i = 0 ; i < skill.size() ;i ++){
            if(skill.get(i).getSkillTemplate().getID() == ID){
                return (int)skill.get(i).getPoint();
            }
        }
        return -1;
    }

    public void RemoveList(int ID){
        for(int i = 0 ; i < skill.size() ;i ++){
            if(skill.get(i).getSkillTemplate().getID() == ID){
                skill.remove(i);
            }
        }
    }
    public synchronized int upluong(long x) {
        long luongnew = (long) this.ngoc + x;
        if (luongnew > 2000000000L) {
            x = 2000000000 - this.ngoc;
        } else if (luongnew < -2000000000L) {
            x = -2000000000 - this.ngoc;
        }
        this.ngoc += (int) x;
        return (int) x;
    }

    public void openBookSkill(int  index, int sid) {
        if(getLevelSkill(sid) == 7){
            sendAddchatYellow("Đã Đạt Cấp Tối Đa");
            return;
        }
        try {
            int level = Integer.parseInt(ItemBag[index].template.name.split("lv")[1]);
            String name  = ItemBag[index].template.name;
            if (ItemBag[index].template.gender == this.gender || ItemKhien(ItemBag[index])) {
                if (ItemBag[index].template.strRequire <= this.power) {
                    if(level == (int)getLevelSkill(sid) + 1 || level == 1){
                        RemoveList(sid);
                        this.ItemBag[index] = null;
                        Skill skill = new Skill();
                        skill.setSkillID(sid);
                        skill.setPoint(level);
                        skill = this.nClass.getSkillTemplate((int)skill.getSkillID()).getSkills()[(int)skill.getPoint() - 1];
                        this.skill.add(skill);
                        Service.gI().loadPlayer(this.session, this);
                        Service.gI().updateItemBag(this);
                        if(ItemBody[5] != null || NhapThe == 1){
                            Service.gI().LoadCaiTrang(this, 1, PartHead(), PartHead() + 1, PartHead() + 2);
                        }
                        else{
                            Service.gI().LoadCaiTrang(this, 1, PartHead(), PartBody(), Leg());
                        }
                        sendAddchatYellow("Học Thành Công Kỹ Năng " + name);
                    }
                    else{
                        sendAddchatYellow("Vui Lòng Học Cấp Độ " + (level - 1) +" Trước");
                    }
                }
                else {
                    Service.gI().serverMessage(this.session, "Sức mạnh không đủ yêu cầu");
                }
            } else {
                Service.gI().serverMessage(this.session, "Sai hành tinh");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSkill() {
        Message m = null;
        try {

            m = new Message(-30);

            m.writer().writeByte(2);

//            m.writer().writeByte(this.getSpeed());
            m.writer().writeInt(this.getMpFull());
            m.writer().writeInt(this.getHpFull());

            m.writer().writeByte(this.skill.size());
            for (Skill skill : this.skill) {
                m.writer().writeShort((short) skill.getSkillID());
//                m.writer().writeShort(skill.point);
                Util.log("skill--------Idsss--------> " + skill.getSkillID());
                Util.log("skill--------Levesssr--------> " + skill.getPoint());
            }
            m.writer().flush();
            this.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (m != null) {
                m.cleanup();
            }
        }

    }
}
