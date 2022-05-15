package server;

import real.skill.NClass;
import real.skill.SkillTemplate;
import server.io.Message;
import server.io.Session;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import real.clan.ClanManager;
import real.clan.ClanService;
import real.item.Item;
import real.item.ItemDAO;
import real.item.ItemSell;
import real.map.Map;
import real.map.Zone;
import real.map.Mob;
import real.map.Npc;
import real.map.WayPoint;
import real.player.Player;
import real.player.PlayerDAO;
import real.player.PlayerManger;
import real.player.UseSkill;
import real.player.User;

import javax.sound.midi.Soundbank;

public class Controller {

    private static Controller instance;
    Server server = Server.gI();

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public void onMessage(Session _session, Message m) {
        try {
            Player player = PlayerManger.gI().getPlayerByUserID(_session.userId);
            byte cmd = m.command;
            Util.log("CMD"+cmd);
            switch (cmd) {
                case -127:
                    byte typequay = m.reader().readByte();
                    byte soluong = m.reader().readByte();
                    server.menu.LuckyRound(player,typequay,soluong);
                    break;
                case -113:
                    UseSkill.useSkill(player, m);
                    break;
                case -84:
                    Service.gI().CHAGE_MOD_BODY(player);
                    break;
                case -107:
                    Service.gI().sendMessage(_session, -107, "detu");
                    break;
                case -101:
                    login2(_session);
                    break;

                case -87:
                    Service.gI().updateData(_session);
                    Manager.sendData(player);
                    break;
                case -81:
                    byte actionn = m.reader().readByte();
                    byte indexUI = m.reader().readByte();
                    Service.gI().upgrade(player, actionn, indexUI);
                    break;
                case -80:
                    Service.gI().sendMessage(_session, -80, "1630679754715_-80_r");
                    break;
                case  -111:

                    break;
                case -74:
                    byte type = m.reader().readByte();
                    Util.log("type "+type);
                    if (type == 1) {
                        Service.gI().sizeImageSource(_session);
                    } else if (type == 2) {
                        Service.gI().imageSource(_session);
                    }

                    break;
                case -71:
                    boolean isCanChat = false;
                    String chat2 = m.reader().readUTF();
                    if (player.ngocKhoa >= 5) {
                        player.ngocKhoa = player.ngocKhoa - 5;
                        isCanChat = true;
                    } else if (player.ngoc >= 5) {
                        player.ngoc = player.ngoc - 5;
                        isCanChat = true;
                    } else {
                        isCanChat = true;
                    }
                    if(isCanChat){
                        Service.gI().ChatGolbaL(player, chat2);
                        Service.gI().buyDone(player);
                    }
                    break;
                case -70:
                    String chat3 = m.reader().readUTF();
                    server.menu.ChatTG(player, player.PartHead(), chat3, (byte) 1);
                    break;
                case -77:
                case -67:
                    GameScr.reciveImage(player, m);
                    break;
                case -66:
                    int effId = m.reader().readShort();
                    Service.gI().effData(_session, effId);
//                    Service.gI().loadeffData(_session, effId);
                    break;
                case -63:
                    // id image logo clan
                    Service.gI().sendMessage(_session, -63, "1630679755147_-63_r");
                    break;
                case -55:
                    //leaveClan
                    Service.gI().serverMessage(_session, "leaveClan");
                    Util.log("leaveClan");
                    break;
                case -50:
                    int clanId = m.reader().readInt();
                    ClanService.gI().clanMember(_session, clanId);
                    break;
                case -47:
                    String clanName = m.reader().readUTF();
                    ClanService.gI().searchClan(_session, clanName);
                    break;
                case -46:
                    byte action = m.reader().readByte();
                    Util.log("Clan action: " + action);
                    if (action == 4) {
                        m.reader().readByte();
                        Service.gI().updateSloganClan(_session, m.reader().readUTF());
                    }
                    break;
                case -43:
                    try {
                        short idItem = 0;
                        byte typeUse = m.reader().readByte();
                        byte where = m.reader().readByte();
                        byte index = m.reader().readByte();
                        if(index == -1){
                             idItem = m.reader().readShort();
                        }
                        UseItemHandler(_session,player, typeUse, where, index,idItem);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    break;


                case -41:
                    //UPDATE_CAPTION
//                    Service.gI().loadPlayer(_session,player);
                    Service.gI().sendMessage(_session, -41, "1630679754812_-41_r");
                    break;
                case -40:
                    ReadMessage.gI().getItem(_session, m);
                    break;
                case -39:
                    //finishLoadMap
                    player.zone.loadPlayers(player);
                    break;
                case -32:
                    int bgId = m.reader().readShort();
                    Service.gI().bgTemp(_session, bgId);
                    break;
                case -30:
                    messageSubCommand(_session, m);
                    break;

                case -29:
                    messageNotLogin(_session, m);
                    break;
                case -28:
                    messageNotMap(_session, m);
                    break;
                case -27:
                    _session.sendSessionKey();
                    Service.gI().sendMessage(_session, -29, "1630679748828_-29_2_r");
                    Service.gI().versionImageSource(_session);
                    Service.gI().sendMessage(_session, -111, "1630679748814_-111_r");


                    break;
                case -33:
                case -23:
                    WayPoint[] wp = player.map.template.wayPoints;
                    if (wp != null) {
                        for(int i = 0 ; i < wp.length;i++){
                            Map maptele = Manager.getMapid(wp[i].goMap);
                            player.map = maptele;
                        }
                        player.zone.VGo(player, m);
                    } else {
                        Service.gI().serverMessage(_session, "Không thể vào map");
                        Service.gI().resetPoint(_session, player.x - 50, player.y);
                    }
                    break;
                case -20:
                    try {
                        short itemMapId = m.reader().readShort();
                        player.zone.PickItemDrop(player, itemMapId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case -7:
                    byte b = m.reader().readByte();
                    player.x = m.reader().readShort();
                    player.y = m.reader().readShort();
//                    Util.log("player x-------------->"+b +"\n" + "player y------------>"+player.y);
//                    try {
//
//                    } catch (Exception e) {
//                    }
                    player.zone.playerMove(player);
//                    byte b = m.reader().readByte();
//                    if (b == 0) {
//                        player.move(m.reader().readShort(), player.y);
//                    } else {
//                        player.move(m.reader().readShort(), m.reader().readShort());
//                    }
//                    try {
//                        player.x = m.reader().readShort();
//                        player.y = m.reader().readShort();
//                    } catch (Exception e) {
//                    }
                    Util.log("player x-------------->"+player.x +"\n" + "player y------------>"+player.y);
//                    player.zone.playerMove(player);
                    break;
                case 6:
                    try {
                        byte typeBuy = m.reader().readByte();
                        short itemID = m.reader().readShort();
                        ItemSell itemBuy = ItemSell.getItemSell(itemID, typeBuy);
                        if (itemBuy == null) {
                            player.sendAddchatYellow("Item " + itemID + " chưa được mở bán");
                            return;
                        }
                        if (typeBuy != itemBuy.buyType) {
                            if (typeBuy == 0) {
                                player.sendAddchatYellow("Item không bán bằng vàng");
                            }
                            if (typeBuy == 1) {
                                player.sendAddchatYellow("Item không bán bằng ngọc");
                            }
                            return;
                        }
                        boolean isCanBuy = false;
                        if (itemBuy.buyType == 0) {
                            if (player.vang >= itemBuy.buyGold) {
                                player.vang -= itemBuy.buyGold;
                                isCanBuy = true;
                            } else {
                                player.sendAddchatYellow("Bạn không đủ tiền để mua vật phẩm");
                                isCanBuy = false;
                            }
                        } else if (itemBuy.buyType == 1) {
                            if (player.ngocKhoa >= itemBuy.buyCoin) {
                                player.ngocKhoa -= itemBuy.buyCoin;
                                isCanBuy = true;
                            } else if (player.ngoc >= itemBuy.buyCoin) {
                                player.ngoc -= itemBuy.buyCoin;
                                isCanBuy = true;
                            } else {
                                player.sendAddchatYellow("Bạn không đủ tiền để mua vật phẩm");
                                isCanBuy = false;
                            }
                        } else {
                            player.sendAddchatYellow("lỗi");
                            isCanBuy = false;
                        }
                        if(player.getBagNull() == 0){
                            player.sendAddchatYellow("Hành trang không đủ chỗ trống");
                            isCanBuy = false;
                        }
                        if (isCanBuy ) {
                            player.addItemToBag(itemBuy.item);
                            Service.gI().updateItemBag(player);
                            Service.gI().buyDone(player);
                            player.sendAddchatYellow("Mua thành công "+ itemBuy.item.template.name);
                        }
                    } catch (Exception e) {
                        player.sendAddchatYellow("Mua vật phẩm không thành công");
                        e.printStackTrace();
                    }
                    break;  
                case 7:
                    byte actionBuy = m.reader().readByte();
                    byte typeBuy = m.reader().readByte();
                    short indexBuy = m.reader().readShort();
                    Service.gI().SellItem(player , actionBuy,typeBuy,indexBuy);
                    break;
                case 11:
                    byte modId = m.reader().readByte();
                    Service.gI().requestModTemplate(player, modId);
                    break;
                case 21:
                    //Chon khu vuc
                    if (player != null) {
                        player.zone.selectUIZone(player, m);
                    }
                    break;

                case 22:
                    //Xử lý menu có option 
                    try{
                        server.menu.menuHandler(player, m);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 29:
                    if (player != null) {
                        player.zone.openUIZone(player);
                        break;
                    }
                case -15:
                    Util.log("ve nha");
                    Map mapteles = Manager.getMapid(23);
                    player.upHPGOhome(10);
                   teleportToMAP(player, mapteles);

                    break;
                case -16:
                    player.zone.wakeUpDieReturn(player);
                    break;
                case 32:
                    try{
                        server.menu.confirmMenu(player, m);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case 33:
                    try{
                        server.menu.openUINpc(player, m);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 34:
                    short selectSkill = m.reader().readShort();
                    Util.log("skill select---------?" +selectSkill);

                    player.selectSkill = player.nClass.getSkillTemplate(selectSkill).getSkills();

                    break;
                case 35:
                    break;




                case 44:
                    String text = m.reader().readUTF();
                    if (server.isDebug) {
                        if (text.contains("m ")) {
                            int mapId = Integer.parseInt(text.replace("m ", ""));

                            Map maptele = Manager.getMapid(mapId);
                            teleportToMAP(player, maptele);
                        } else if (text.contains("smtn ")) {
                            int amount = Integer.parseInt(text.replace("smtn ", ""));
                            player.UpdateSMTN((byte) 2, amount);
                        } else if (text.equals("die")) {
                            player.getPlace().LiveFromDead(player);
                        } else if (text.equals("check")) {
                            player.sendAddchatYellow("MAP " + player.x + " " + player.y);
                        }
                        else if (text.contains("u ")) {
                            short u = Short.parseShort(text.replace("u ", ""));
                            player.y += u;
                            player.zone.playerMove(player);
                        }
                    } else {
                        player.zone.chat(player, text);
                    }
                    break;
                 case -34:
                    Service.gI().MagicTree(_session, 0);
                    break;
                //tấn công quái    
                case 54:

                    player.getPlace().FightMob(player, m);
                    break;
                // nhap
                case -45:
                            player.useSkillNotForcus(player);
                    break;
                case 88:
                    Draw.Draw(player, m);
                    break;
                case -79:
                    int playerid = m.reader().readInt();
                    Player player2 = PlayerManger.gI().getPlayerByUserID(playerid);
                    Service.gI().PlayerMenu(player.session,player2);
                    break;
                default:
                    player.sendAddchatYellow("Tính năng " + cmd + " chưa mở");
                    break;
            }
        } catch (Exception e) {
        }
        if (m != null) {
            m.cleanup();
        }
    }

    public void UseItemHandler(Session s,Player p, byte typeUse, byte where, byte index , short idtemp) {
         try{
            if (where == 0) {
                if (typeUse == 0) {
                } else if (typeUse == 1) {
                    Service.gI().DropDone(p,typeUse,"Bạn có chắc chắn muốn hủy bỏ (mất luôn)\n" + p.ItemBag[index].template.name +" ?",index);
                }
                if (typeUse == 2) {
                    p.removeItemBody(index);
                    Service.gI().updateItemBody(p);
                }
            } else if (where == 1) {
                Util.log("type use --------------> "+typeUse);
                if (typeUse == 0) {
                    if(index == -1){
                        for(int i = 0 ; i < p.ItemBag.length ;i++){
                            if(p.ItemBag[i] != null&&idtemp == 595){
                                if(p.ItemBag[i].quantity > 0){
                                    p.hp += p.hp + p.ItemBag[i].getParamItemByID(48);
                                    p.mp += p.mp + p.ItemBag[i].getParamItemByID(48);
                                    p.ItemBag[i].quantity-=1;
                                    Service.gI().loadPoint(p.session,p);
                                    Service.gI().updateItemBag(p);
                                }
                            }
                        }
                        return;
                    }
                    Service.gI().UseItem(p, index,idtemp);
                } else if (typeUse == 1) {
                    Service.gI().DropDone(p,typeUse,"Bạn có chắc chắn muốn hủy bỏ (mất luôn)\n" + p.ItemBag[index].template.name +" ?",index);
                }
                if (typeUse == 2) {
                    p.removeItemBag(index);
                    Service.gI().updateItemBag(p);
                }
            }
         } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void teleportToMAP(Player p, Map map) {
        p.zone.leave(p);
        map.getPlayers().add(p);
        p.map = map;
        if(p.isdie){
            p.isdie= false;
        }
        for(WayPoint ww : p.map.template.wayPoints){
            for(Npc n : p.map.template.npcs){
                if(n != null){
                    p.x = (short) n.cx;
                    p.y = (short) n.cy;
                }else{
                    p.x = (short) (ww.maxX + ww.goX);
                    p.y = ww.maxY;
                }
            }
        }
        p.map.area[0].Enter(p);
    }

    public void messageNotLogin(Session session, Message msg) {
        if (msg != null) {
            try {
                byte cmd = msg.reader().readByte();
                Util.log("sss"+cmd);
                switch (cmd) {
                    case 0:
                        login(session, msg);
                        break;
                    case 2:
                        session.setClientType(msg);
                        break;
                    default:
                        Util.log("messageNotLogin: " + cmd);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void messageNotMap(Session _session, Message _msg) {
        if (_msg != null) {
            try {
                Player player = PlayerManger.gI().getPlayerByUserID(_session.userId);
                byte cmd = _msg.reader().readByte();
                Util.debug("-28 ------------------> " + cmd);
                switch (cmd) {
                    case 2:
                        createChar(_session, _msg);
                        break;
                    // send data map
                    case 6:
                        Manager.sendMap(player);
                        break;
                    // send data skill
                    case 7:
//                        if(player!=null){
                            Manager.sendSkill(player);
//                        }else {
//                            Util.log("create player");
//                        }

                        break;
                    // send data item
                    case 8:
                        Manager.sendItem(player);
                        break;
                    case 10:
                        player.zone.AddItemGa();
                        Service.gI().mapTemp(_session, player.map.getId());
                        break;
                    case 13:
                        player.update();
                        break;
                    default:
                        Util.log("messageNotMap: " + cmd);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void messageSubCommand(Session _session, Message _msg) {
        if (_msg != null) {
            try {
                Player player = PlayerManger.gI().getPlayerByUserID(_session.userId);
                byte command = _msg.reader().readByte();
                switch (command) {
                    case 5:

                    case 16:
                        byte type = _msg.reader().readByte();
                        short point = _msg.reader().readShort();
                        player.increasePoint(type, point);
                        break;
                    case 63:
                        Service.gI().GET_PLAYER_MENU(player);
                        break;
                    default:
                        Util.log("messageSubCommand: " + command);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void login(Session session, Message msg) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String user = msg.reader().readUTF();
            String pass = msg.reader().readUTF();
            msg.reader().readUTF();
            msg.reader().readByte();
            // -77 SMALLIMAGE_VERSION
            Service.gI().sendMessage(session, -77, "1630679752225_-77_r");
            // -93 BGITEM_VERSION
            Service.gI().sendMessage(session, -93, "1630679752231_-93_r");
            conn = DBService.gI().getConnection();
            pstmt = conn.prepareStatement("select * from account where username=? and password=? limit 1");
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                session.nhanvat = rs.getString("nhanvat").toLowerCase();
                session.taikhoan = rs.getString("username").toLowerCase();
                session.matkhau = rs.getString("password").toLowerCase();
                session.userId = rs.getInt("id");
                Player p = PlayerManger.gI().getPlayerByUserID(session.userId);
                if (rs.getBoolean("ban")) {
                    Service.gI().serverMessage(session, "Tài khoản đã bị khóa vui lòng liên hệ với ADMIN để biết thêm chi tiết");
                } else if (!Util.CheckString(user, "^[a-zA-Z0-9]+$") ||!Util.CheckString(pass, "^[a-zA-Z0-9]+$") ) {
                    Service.gI().serverMessage(session,"Có Cái Nịt Nè :3");
                } 
                else if (p != null) {
                    this.logout(p.session);
                    PlayerManger.gI().kick(p.session);
                    Service.gI().serverMessage(session, "Bạn đang đăng nhập trên thiết bị khác");
                } else {
                    pstmt = conn.prepareStatement("select * from player where account_id=? limit 1");
                    pstmt.setInt(1, session.userId);
                    msg.cleanup();
                    rs = pstmt.executeQuery();
                    if (rs.first()) {
                        Player player = Player.setup(session.userId);
                        PlayerManger.gI().getPlayers().add(player);
                        player.session = session;
                        Service.gI().updateVersion(session);
//                        Service.gI().updateVersion(session);
                        Service.gI().updateData(session);
                        Service.gI().updateBag(session);
                        Service.gI().updateBody(session);
                        Service.gI().updateMap(session);
                        Service.gI().updateSkill(session);
                        Service.gI().updateItem(session);
                        Service.gI().updateItemBox(player);
                        Service.gI().itemBg(session, 0);
                        sendInfo(session);
                        player.sendAddchatYellow("Chào mừng " + player.name +" đến với NRO BASIC");
                        PlayerManger.gI().put(session);
                        PlayerManger.gI().put(player);
                        session.player = player;
                        if(player.ItemBody[5] != null || player.NhapThe == 1){
                            Service.gI().LoadCaiTrang(player, 1, player.PartHead(), player.PartHead() + 1, player.PartHead() + 2);
                        }
                        else{
                            Service.gI().LoadCaiTrang(player, 1, player.PartHead(), player.PartBody(), player.Leg());
                        }
                    } else {
                        Service.gI().sendMessage(session, -28, "1630679754226_-28_4_r");
                        Service.gI().sendMessage(session, -31, "1631370772604_-31_r");
                        Service.gI().sendMessage(session, -82, "1631370772610_-82_r");
                        Service.gI().sendMessage(session, 2, "1631370772901_2_r");
                    }
                }
            } else {
                Service.gI().serverMessage(session, "Tài khoản mật khẩu không chính xác !");
            }
            conn.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void createChar(Session session, Message msg) {
        Connection conn = DBService.gI().getConnection();
        PreparedStatement pstmt = null;
        //String CREATE_NHANVAT = "UPDATE account SET nhanvat==? WHERE id=?)";
        try {
            String name = msg.reader().readUTF();
            int gender = msg.reader().readByte();
            int head = msg.reader().readByte();
            if (gender > 2 || gender < 0) {
                session.player.sendAddchatYellow("Hành tinh lựa chòn không hợp lệ !");
            }
            pstmt = conn.prepareStatement("SELECT * FROM `player` WHERE name=?");
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.first()) {
                if (PlayerDAO.create(session, name, gender, head)) {
                    session.player = Player.setup(session.userId);
                    PlayerManger.gI().getPlayers().add(session.player);
                    session.player.session = session;
                    Service.gI().updateVersion(session);
                    Service.gI().updateData(session);
                    Service.gI().updateBag(session);
                    Service.gI().updateBody(session);
                    Service.gI().updateMap(session);
                    Service.gI().updateSkill(session);
                    Service.gI().updateItem(session);
                    Service.gI().updateItemBox(session.player);
                    Service.gI().itemBg(session, 0);
                    sendInfo(session);
                    session.player.sendAddchatYellow("Chào mừng " + session.player.name +" đến với NRO BASIC");
                    PlayerManger.gI().put(session);
                    PlayerManger.gI().put(session.player);
                    session.player = session.player;

                }
            } else {
                Service.gI().serverMessage(session, "Tên đã tồn tại");
            }
            conn.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

    }

    public void login2(Session session) {
        String user = "User" + Util.nextInt(2222222, 8888888);
        Connection conn = DBService.gI().getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO account(username,password,nhanvat) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user);
            ps.setString(2, "");
            ps.setBoolean(3, true);
            if (ps.executeUpdate() == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.first()) {
                    Service.gI().login2(session, user);

                }
            } else {
                Service.gI().serverMessage(session, "Có lỗi vui lòng thử lại");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("lỗi server.Controller.login2()");
        }
    }

    public void sendInfo(Session session) {
        Player player = PlayerManger.gI().getPlayerByUserID(session.userId);
        Service.gI().tileSet(session, player.map.id);
        Service.gI().sendMessage(session, 112, "1630679754607_112_r");
        Service.gI().loadPoint(session, player);
        Service.gI().sendMessage(session, 40, "1630679754622_40_r");
        Service.gI().clearMap(session);
        Service.gI().loadPlayer(session, player);
        if (player.clan != null) {
          
        }
        Service.gI().sendMessage(session, -69, "1630679754701_-69_r");
        // -68 STAMINA
        Service.gI().sendMessage(session, -68, "1630679754708_-68_r");
        // -80 FRIEND
        Service.gI().sendMessage(session, -80, "1630679754715_-80_r");
        // -97 UPDATE_ACTIVEPOINT
        Service.gI().sendMessage(session, -97, "1630679754722_-97_r");
        // -107 PET_INFO
        Service.gI().sendMessage(session, -107, "1630679754733_-107_r");
        // -119 THELUC
        Service.gI().sendMessage(session, -119, "1630679754740_-119_r");
        // -113 CHANGE_ONSKILL
        Service.gI().sendMessage(session, -113, "1630679754747_-113_r");
        // 50 GAME_INFO
        for (Map map : server.maps) {
            if (map.id != player.map.id) {
                continue;
            }
            for (int i = 0; i < map.area.length; i++) {
                if (map.area[i].players.size() < map.template.maxplayers) {
                    map.area[i].Enter(player);
                    return;
                }
            }
        }
    }

    public void logout(Session session) {
        Player player = PlayerManger.gI().getPlayerByUserID(session.userId);
//        System.out.print("player "+player.tiemNang);
        if (player != null) {
            Map map = player.map;
            PlayerDAO.updateDB(player);
            session.player.zone.exitMap(player,map);
            session.player.zone.players.remove(player);
            session.player.map = null;
            PlayerManger.gI().getPlayers().remove(player);
        }
    }

}
