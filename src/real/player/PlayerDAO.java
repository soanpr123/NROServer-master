package real.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import real.clan.ClanManager;
import real.item.Item;
import real.item.ItemDAO;
import real.item.ItemTemplate;
import real.map.Map;
import real.skill.Skill;
import real.skill.SkillData;
import real.skill.SkillTemplate;
import server.DBService;
import server.Manager;
import server.Util;
import server.io.Session;

public class PlayerDAO {

    public static boolean create(Session userId, String name, int gender, int head) {
        String CREATE_PLAYER = "INSERT INTO player(account_id,name,power,vang,luong,luong_khoa,gender,head,where_id,where_x,where_y,limit_power,hp_goc,mp_goc,dame_goc,def_goc,crit_goc,tiem_nang,maxluggage,maxbox,skill,itembody,itembag,itembox,nhapthe) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        boolean check = false;
        JSONArray jarr = new JSONArray();
        JSONObject put = new JSONObject();
        Connection conn = DBService.gI().getConnection();
        ResultSet rss = null;
        int playerid = userId.userId;
        try {
            PreparedStatement ps = null;
            ps = conn.prepareStatement(CREATE_PLAYER, Statement.RETURN_GENERATED_KEYS);
            conn.setAutoCommit(false);
            ps.setInt(1, playerid);
            ps.setString(2, name);
            //sức mạnh
            ps.setLong(3, 1200);
            //vàng
            ps.setInt(4, 20000);
            //ngọc
            ps.setInt(5, 20);
            //ngọc tím
            ps.setInt(6, 0);
            ps.setInt(7, gender);
            ps.setInt(8, head);
            ps.setInt(9, gender + 39);
            ps.setInt(10, 180);
            ps.setInt(11, 384);
            ps.setInt(12, 1);
            switch (gender) {
                case 0:
                    ps.setInt(13, 500);
                    ps.setInt(14, 100);
                    ps.setInt(15, 12);
                    break;
                case 1:
                    ps.setInt(13, 500);
                    ps.setInt(14, 200);
                    ps.setInt(15, 12);
                    break;
                case 2:
                    ps.setInt(13, 500);
                    ps.setInt(14, 500);
                    ps.setInt(15, 15);
                    break;
            }
            ps.setInt(16, 0);
            ps.setInt(17, 0);
            ps.setLong(18, 1200);
            ps.setLong(19, 20);
            ps.setLong(20, 20);
            switch (gender) {
                case 0:
                    put.put((Object)"id", (Object)0);
                    put.put((Object)"point", (Object)1);
                    jarr.add(put);
                    ps.setString(21, jarr.toJSONString());
                    break;
                case 1:
                    put.put((Object)"id", (Object)2);
                    put.put((Object)"point", (Object)1);
                    jarr.add(put);
                    ps.setString(21, jarr.toJSONString());
                    break;
                case 2:
                    put.put((Object)"id", (Object)4);
                    put.put((Object)"point", (Object)1);
                    jarr.add(put);
                    ps.setString(21, jarr.toJSONString());
                    break;
            }
            jarr.clear();
            ps.setString(22, "[]");
            ps.setString(23, "[]");
            ps.setString(24, "[]");
            ps.setInt(25, 0);
            if(ps.executeUpdate() == 1){
                check = true;
            }
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check;
    }


    public static void updateDB(Player player) {
        String UPDATE_PLAYER = "UPDATE player SET power=?,vang=?,luong=?,luong_khoa=?,clan_id=?,task_id=?,head=?,where_id=?,where_x=?,where_y=?,maxluggage=?,maxbox=?,hp_goc=?,mp_goc=?,dame_goc=?,def_goc=?,crit_goc=?,tiem_nang=?,skill=?,itembody=?,itembag=?,itembox=?,nhapthe=? WHERE account_id=?";
        Connection conn;
        JSONArray jarr = new JSONArray();
        PreparedStatement ps;
        try {
            conn = DBService.gI().getConnection();
            ps = conn.prepareStatement(UPDATE_PLAYER);
            conn.setAutoCommit(false);
            ps.setLong(1, player.power);
            ps.setInt(2, player.vang);
            ps.setInt(3, player.ngoc);
            ps.setInt(4, player.ngocKhoa);
            if (player.clan != null) {
                ps.setInt(5, player.clan.id);
            } else {
                ps.setInt(5, -1);
            }
            ps.setInt(6, player.taskId);
            ps.setInt(7, player.head);
            ps.setInt(8, player.map.id);
            ps.setInt(9, player.x);
            ps.setInt(10, player.y);
            ps.setInt(11, player.maxluggage);
            ps.setInt(12, player.maxBox);
            ps.setInt(13, player.hpGoc);
            ps.setInt(14, player.mpGoc);
            ps.setInt(15, player.damGoc);
            ps.setInt(16, player.defGoc);
            ps.setInt(17, player.critGoc);
            ps.setLong(18, player.tiemNang);
            byte j;
            for(j=0;j<player.skill.size();++j){
                jarr.add(SkillTemplate.ObjectSkill(player.skill.get(j)));
            }
            ps.setString(19, jarr.toJSONString());
            jarr.clear();
            for(j = 0; j < player.ItemBody.length; ++j) {
                if (player.ItemBody[j] != null && player.ItemBody[j].id != -1) {
                    jarr.add(ItemTemplate.ObjectItem(player.ItemBody[j], j));
                }
            }
            ps.setString(20, jarr.toJSONString());
            jarr.clear();
            for(j = 0; j < player.ItemBag.length; ++j) {
                if (player.ItemBag[j] != null && player.ItemBag[j].id != -1) {
                    jarr.add(ItemTemplate.ObjectItem(player.ItemBag[j], j));
                }
            }
            ps.setString(21, jarr.toJSONString());
            jarr.clear();
            for(j = 0; j < player.ItemBox.length; ++j) {
                if (player.ItemBox[j] != null && player.ItemBox[j].id != -1) {
                    jarr.add(ItemTemplate.ObjectItem(player.ItemBox[j], j));
                }
            }
            ps.setString(22, jarr.toJSONString());
            ps.setInt(23, player.NhapThe);
            ps.setInt(24, player.account_id);
            if (ps.executeUpdate() == 1) {
            }
            conn.commit();
            conn.close();
//            System.out.println("player updated  " + player.id);
        } catch (Exception e) {
        }
    }
}
