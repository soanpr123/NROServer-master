package server;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import jdk.nashorn.internal.ir.BreakNode;
import real.item.Item;
import real.item.ItemOption;
import real.item.ItemSell;
import real.item.ItemTemplate;
import real.item.ItemTemplates;
import real.item.Shop;
import real.item.TabItemShop;
import real.map.Map;
import real.map.Npc;
import real.map.Zone;
import real.player.Player;
import real.player.PlayerManger;
import server.io.Message;
import server.io.Session;

public class Menu {
    Server server = Server.gI();
    public static void doMenuArray(Player p,int idnpc,String chat, String[] menu) {
        Message m = null;
        try {
            m = new Message(32);
            m.writer().writeShort(idnpc);
            m.writer().writeUTF(chat);
            m.writer().writeByte(menu.length);
            for (byte i = 0; i < menu.length; ++i) {
            m.writer().writeUTF(menu[i]);
            }
            m.writer().flush();
            p.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }

    }
    public static void doMenuArraySay(Player p,short id, String[] menu) {
        Message m = null;
        try {
            m = new Message(38);
            m.writer().writeShort(id);
            for(byte i = 0; i < menu.length; i++) {
                m.writer().writeUTF(menu[i]);
            }
            m.writer().flush();
            p.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }

    }
    public static void sendWrite(Player p, String title,short type) {
        Message m = null;
        try {
            m = new Message(88);
            m.writer().writeUTF(title);
            m.writer().writeShort(type);
            m.writer().flush();
            p.session.sendMessage(m);
            m.cleanup();
        } catch (IOException var5) {
            var5.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }

    }
    public void textBoxId(Session session,short menuId, String str) {
        Message msg;
        try {
            msg = new Message(88);
            msg.writer().writeInt(menuId);
            msg.writer().writeUTF(str);
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
     public void sendTB(Session session, Player title, String s) {
        Message m = null;
        try {
            m = new Message(94);
            m.writer().writeUTF(title.name);
            m.writer().writeUTF(s);
            m.writer().flush();
            PlayerManger.gI().SendMessageServer(m);
            session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }

    }
     public void ChatTG(Player p, int avatar, String chat3,byte cmd) {
        Message m = null;
        try {
            m = new Message(-70);
            m.writer().writeShort(avatar);
            m.writer().writeUTF(chat3);
            m.writer().writeByte(cmd);
            m.writer().flush();
            PlayerManger.gI().SendMessageServer(m);
            p.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
    }
     public void ChatTG(Player p,short avatar, String str) {
        Message m = null;
        try {
            m = new Message(94);
            m.writer().writeShort(avatar);
            m.writer().writeUTF(str);
            m.writer().flush();
            p.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
    }
     

     public void Test(Player p) {
        Message m = null;
        try {
//            Item[] shop = p.ItemBag;
//            
//            System.out.println("bag "+ p.ItemBag);
            m = new Message(-81);
            m.writer().writeByte(0);
            m.writer().writeUTF("Ngọc Rồng Z");
            m.writer().writeUTF("Chọn đồ nâng cấp");
            m.writer().flush();
            p.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
    }
     public void LuckyRound(Player p,byte type,byte soluong) throws IOException{
         Message m = null;
        try {
         if(type == 0){
         m = new Message(-127);
         m.writer().writeByte(type);
         short[] arId = new short[]{2280,2281,2282,2283,2284,2285,2286};
         m.writer().writeByte(7);
         for(short i = 0; i< arId.length ;i++){
         m.writer().writeShort(arId[i]); 
         }
         m.writer().writeByte(soluong);
         m.writer().writeInt(10000);
         m.writer().writeShort(0);
         m.writer().flush();
         p.session.sendMessage(m);
         }else if(type == 1){
         m = new Message(-127);
         m.writer().writeByte(soluong);
         short[] arId = new short[]{2,3,4,5,6,7,8};
         for(short i = 0; i< soluong ;i++){
         m.writer().writeShort(arId[i]); 
         }
         m.writer().flush();
         p.session.sendMessage(m);
         }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.cleanup();
            }
        }
     }
     public void confirmMenu(Player p, Message m) throws IOException, SQLException, InterruptedException {
        Short idNpc = m.reader().readShort();
        byte select = m.reader().readByte();
        switch (p.menuNPCID) {
        case 39:{
            if(select == 0){
                TabItemShop[] test = Shop.getTabShop(39, 2).toArray(new TabItemShop[0]);
                GameScr.UIshop(p, test);
                break;
            }
            break;
        }
        case 16:{
            if(select == 0){
                TabItemShop[] test = Shop.getTabShop(16, p.gender).toArray(new TabItemShop[0]);
                GameScr.UIshop(p, test);
                break;
            }
            break;
        }
            case 4:{
                Util.log("selsct"+select);
                Service.gI().MagicTree(p.session, 2);

                break;
            }
        case 9:{
            if(select == 0){
                TabItemShop[] test = Shop.getTabShop(9, 0).toArray(new TabItemShop[0]);
                GameScr.UIshop(p, test);
                break;
            }
            break;
        }
        case 7:
            if(select == 0){
            TabItemShop[] test = Shop.getTabShop(7, 1).toArray(new TabItemShop[0]);
            GameScr.UIshop(p, test);
            break;
            }
            break;
        case 8:
            if(select == 0){
            TabItemShop[] test = Shop.getTabShop(8, 2).toArray(new TabItemShop[0]);
            GameScr.UIshop(p, test);
            break;
            }
            break;
        case 38:
            if(select == 0){
                if(p.map.id!=102){
                    GotoMap(p,102);
                }else{
                    GotoMap(p,24+p.gender);
                }
            }
            if(select == 1){
                break;
            }
            break;
        case 25:{
            if(select == 0){
                if(p.clan == null){
                    p.sendAddchatYellow("Ngươi chưa có bang hội!");
                }
                else{
                    if(p.clan.members.size() < 5){
                        p.sendAddchatYellow("Bang hội của ngươi quá yếu hãy kết nạp đủ 5 thành viên để vào!");
                    }
                    else{
                        for(int i = 0 ; i < p.map.players.size();i++){
                            if(p.map.players.get(i).clan.id == p.clan.id){
                                GotoMap(p,53);
                            }else{
                                p.sendAddchatYellow("Phải có ít nhất 1 thành viên có mặt trong khu!");
                            }
                        }
                    }
                }
            }
            if(select == 1){
                break;
            }
            break;
        }
        case 13 :{
            if(p.menuID != -1)
            {
                if(p.menuID == 1 && select == 0)
                {
                    p.openBox();
                }
                if(p.menuID == 1 && select == 1)
                {
                    ExtendBag(p);
                }
                if(p.menuID == 1 && select == 2)
                {
                    ExtendBox(p);
                }
            }
            if(select == 1){
                doMenuArray(p,idNpc,"Ta có thể giúp gì cho con",new String[]{"Mở rương" , "Mở rộng\nHành trang" ,"Mở rộng\nRương đồ"});
                p.menuID = select;
            }
            break;
        }
        case 21 :{
            if(p.menuID != -1)
            {
                if(p.menuID == 1 && select == 0)
                {
                    TabItemShop[] test = Shop.getTabShop(21, 0).toArray(new TabItemShop[0]);
                    GameScr.UIshop(p, test);
                }
            }
            if(select == 1){
                doMenuArray(p,idNpc,"Bùa của ta rất lợi hại. Mua xong có tác dụng ngay nhé, nhớ tranh thủ sử dụng, thoát game phí lắm. Mua càng nhiều thời gian giá càng rẻ!",new String[]{"Bùa\n1 tháng"});
                p.menuID = select;
            }
            break;
        }
        case 1:
                if(select == 0){
                    p.vang += 500000000;
                    Service.gI().buyDone(p);
                    p.sendAddchatYellow("Nhận thành công 500 triệu vàng");
                    break;
                }
                if(select == 1){
                    if(p.ngoc < 10000){
                         p.ngoc = p.ngoc + 10000;
                        Service.gI().buyDone(p);
                        p.sendAddchatYellow("Nhận thành công 10k ngọc");
                    }
                    else{
                        p.sendAddchatYellow("Vui lòng sử dụng hết ngọc");
                    }
                    break;
                }
            case 0:
                if(select == 0){
                    p.vang += 500000000;
                    Service.gI().buyDone(p);
                    p.sendAddchatYellow("Nhận thành công 500 triệu vàng");
                    break;
                }
                if(select == 1){
                    if(p.ngoc < 10000){
                         p.ngoc = p.ngoc + 10000;
                        Service.gI().buyDone(p);
                        p.sendAddchatYellow("Nhận thành công 10k ngọc");
                    }
                    else{
                        p.sendAddchatYellow("Vui lòng sử dụng hết ngọc");
                    }
                    break;
                }
            case 2:
                if(select == 0){
                    p.vang += 500000000;
                    Service.gI().buyDone(p);
                    p.sendAddchatYellow( "Nhận thành công 500 triệu vàng");
                    break;
                }
                if(select == 1){
                    if(p.ngoc < 10000){
                        p.ngoc = p.ngoc + 10000;
                        Service.gI().buyDone(p);
                       p.sendAddchatYellow( "Nhận thành công 10k ngọc");
                    }
                    else{
                        p.sendAddchatYellow("Vui lòng sử dụng hết ngọc");
                    }
                    break;
                }
            case 12:
                if(p.map.id == 19){
                    if(select == 0){
                        GotoMap(p,109);
                    }
                    if(select == 1){
                        GotoMap(p,68);
                    }
                    if(select == 2){
                        break;
                    }
                }else if(p.map.id == 68){
                    if(select == 0){
                        GotoMap(p,19);
                    }
                    if(select == 1){
                        break;
                    }
                }
                else{
                    if(select == 0){
                        GotoMap(p,24);
                    }
                    if(select == 1){
                        GotoMap(p,25);
                    }
                    if(select == 2){
                        break;
                    }
                }
                break;
            case 11:
                if(select == 0){
                    GotoMap(p,24);
                }
                if(select == 1){
                    GotoMap(p,26);
                }
                if(select == 2){
                    break;
                }
                break;
            case 10:
                if(select == 0){
                    GotoMap(p,25);
                }
                if(select == 1){
                    GotoMap(p,26);
                }
                if(select == 2){
                    break;
                }
                break;
        default:{
            Service.gI().sendTB(p.session,0, "Chức Năng Đang Được Cập Nhật " + idNpc,0);
            break;
        }
        }
        m.cleanup();
     }
     public void ExtendBag(Player p){
         String CREATE_PLAYER_BAG = "UPDATE player SET maxluggage=? WHERE account_id=?";
         Connection conn = DBService.gI().getConnection();
         try {
            if(p.maxluggage >= 40){
                p.sendAddchatYellow( "Đã đạt giới hạn");
                return;
            }
            else{
                p.maxluggage += 10;
                PreparedStatement ps = conn.prepareStatement(CREATE_PLAYER_BAG);
                ps.setInt(1, p.maxluggage);
                ps.setInt(2, p.id);
                ps.executeUpdate();
                p.sendAddchatYellow("Mở rộng thành công 10 ô hành trang vui lòng đăng nhập lại game để có hiệu lực");
            }
            conn.close();
         }
         catch (Exception e) {
            e.printStackTrace();
        }
     }
     public void ExtendBox(Player p){
         String CREATE_PLAYER_BOX = "UPDATE player SET maxbox=? WHERE account_id=?";
         Connection conn = DBService.gI().getConnection();
         try {
            if(p.maxBox >= 50){
                p.sendAddchatYellow( "Đã đạt giới hạn");
                return;
            }
            else{
                p.maxBox += 10;
                PreparedStatement ps = conn.prepareStatement(CREATE_PLAYER_BOX);
                ps.setInt(1, p.maxBox);
                ps.setInt(2, p.id);
                ps.executeUpdate();
                
                p.sendAddchatYellow( "Mở rộng thành công 10 ô rương đồ vui lòng đăng nhập lại game để có hiệu lực");
            }
            conn.close();
         }
         catch (Exception e) {
            e.printStackTrace();
        }
     }
     public void GotoMap(Player p,int id){
        Map maptele = Manager.getMapid(id);
        Controller.getInstance().teleportToMAP(p, maptele);
     }
     public void menuHandler(Player p, Message m) throws IOException, SQLException, InterruptedException {
        byte idNPC = m.reader().readByte();// ID NPC
        byte menuID = m.reader().readByte();// Lớp nút 1
        byte select = m.reader().readByte();// Lớp nút 2
         System.out.println("menuID: "+ p.menuID);
         System.out.println("menuNPCID: "+ p.menuNPCID);
         System.out.println("select: "+ select);
        int tl;
        switch (p.menuNPCID) {
        
        case 13 :
            if(p.menuID == 1)
            {
                if(select == 0){
                p.openBox();
                }
            }
            break;
        
        default:{
            Service.gI().sendTB(p.session,0, "Chức Năng Đang Được Cập Nhật " + idNPC,0);
                break;
        }
       
         //   Service.getInstance().serverMessage(p.session,"ID NPC " + b1);
         }
        m.cleanup();
     }
     
     public  void openUINpc(Player p, Message m) throws IOException {
        short idnpc = m.reader().readShort();//idnpc
        int avatar;
        m.cleanup();
        p.menuID = -1;
        p.menuNPCID = idnpc;
        avatar = NpcAvatar(p, idnpc);
        m = new Message(33);
        if(p.menuNPCID == 21){
                doMenuArray(p,idnpc,"Ngươi tìm ta có việc gì?",new String[]{"Nói chuyện","Cửa hàng\nBùa","Nâng cấp\nVật phẩm","Nhập\nNgọc Rồng"});
                return;
            }
            if(p.menuNPCID == 10){
                doMenuArray(p,idnpc,"Tàu Vũ Trụ của tôi có thể đưa cậu đến hành tinh khác trong 3 giây. Cậu muốn đi đâu",new String[]{"Đến\nNamếc","Đến\nXayda","Từ Chối"});
                return;
            }
            if(p.menuNPCID == 11){
                doMenuArray(p,idnpc,"Tàu Vũ Trụ Namếc tuy cũ nhưng tốc độ không hề kém bất kỳ loại tầu nào khác. Cậu muốn đi đâu?",new String[]{"Đến\nTrái Đất","Đến\nXayda","Từ Chối"});
                return;
            }
         if(p.menuNPCID == 4){
             doMenuArray(p,idnpc,"Đậu thần cấp?",new String[]{"Thu hoạch","Nâng cấp","Từ Chối"});
             return;
         }
            if(p.menuNPCID == 12){
                if(p.map.id == 19){
                    doMenuArray(p,idnpc,"Đội quân Fide đang ở Thung Lũng Nappa, ta sẽ đưa ngươi đến đó",new String[]{"Đến\nCold","Đến\nNappa","Từ Chối"});
                }else if(p.map.id == 68){
                    doMenuArray(p,idnpc,"Ngươi muốn bỏ chạy ư?",new String[]{"Đồng ý","Từ Chối"});
                }else{
                    doMenuArray(p,idnpc,"Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được",new String[]{"Đến\nTrái Đất","Đến\nNamếc","Từ Chối"});
                }
                return;
            }
            if(p.menuNPCID == 38){
                if(p.map.id != 102){
                doMenuArray(p,idnpc,"Chào chú cháu có thể giúp gì?",new String[]{"Đi đến Tương lai","Từ Chối"});
                return;
                }else{
                    doMenuArray(p,idnpc,"Chào chú cháu có thể giúp gì?",new String[]{"Quay về\nQuá khứ","Từ Chối"});
                    return;
                }
            }
            if (p.menuNPCID == 16) {
                doMenuArray(p,idnpc,Text.get(0, 1),new String[]{"Cửa Hàng"});
                return;
            }
            if (p.menuNPCID == 25) {
            doMenuArray(p,idnpc,"Ngươi có chắc chắn muốn vào trại độc nhãn",new String[]{"Vào Doanh Trại","Từ Chối"});
            return;
            }
            if (p.menuNPCID == 1 || p.menuNPCID == 0 ||p.menuNPCID == 2) {
//                if(p.HavePet == 0){
//                    doMenuArray(p,idnpc,Text.get(0, 0),new String[]{"Nhận Vàng","Nhận Ngọc","Nhận Đệ Tử"});
//                }else{
//                    doMenuArray(p,idnpc,Text.get(0, 0),new String[]{"Nhận Vàng","Nhận Ngọc"});
//                }
                doMenuArray(p,idnpc,Text.get(0, 0),new String[]{"Nhận Vàng","Nhận Ngọc","Nhận Đệ Tử"});
                return;
            }
            if (p.menuNPCID == 39) 
            {
                doMenuArray(p,idnpc,Text.get(0, 1),new String[]{"Cửa Hàng"});
                return;
            }
            if (p.menuNPCID == 9) 
            {
                if(p.gender == 2){
                    doMenuArray(p,idnpc,Text.get(0, 1),new String[]{"Cửa Hàng"});
                }else{
                    Service.gI().sendTB(p.session,0, "Ta chỉ bán đồ cho hành tinh Xayda",0);
                }
                return;
            }
            if (p.menuNPCID == 7) {
                if(p.gender == 0){
                    doMenuArray(p,idnpc,Text.get(0, 1),new String[]{"Cửa Hàng"});
                }else{
                    Service.gI().sendTB(p.session,0, "Ta chỉ bán đồ cho hành tinh Trái đất",0);
                }
                return;
            }
            if (p.menuNPCID == 8) {
                if(p.gender == 1){
                    doMenuArray(p,idnpc,Text.get(0, 1),new String[]{"Cửa Hàng"});

                }else{
                    Service.gI().sendTB(p.session,0, "Ta chỉ bán đồ cho hành tinh Namếc",0);
                }
                return;
            }
            if (p.menuNPCID == 13) {
            doMenuArray(p,idnpc,"Ta có thể giúp gì cho con",new String[]{"Nói chuyện","Tính năng"});
            return;
            }
            if(p.menuNPCID == 3){
            p.openBox();
            return;
            }else{
                Service.gI().sendTB(p.session,0, "Chức Năng Đang Được Cập Nhật " + idnpc,0);
            }
            m.writer().flush();
            p.session.sendMessage(m);
            m.cleanup();
    }
    public int NpcAvatar(Player p,int npcID){
       
        for (int i = 0; i < p.getPlace().map.template.npcs.length; i++){
            if(p.getPlace().map.template.npcs[i].tempId == npcID)
            {
                return p.getPlace().map.template.npcs[i].avartar;
            }    

        }
        return -1;
    }
}
