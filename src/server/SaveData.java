/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.Calendar;
import real.item.Item;
import real.player.Player;
import real.player.PlayerDAO;
import real.player.PlayerManger;

/**
 *
 * @author DuyProMax
 */
public class SaveData extends Thread{
     public static SaveData gI() {
        if (instance == null) {
            instance = new SaveData();
        }
        return instance;
    }
    public boolean check = true;
    private static SaveData instance;
    @Override
    public void run(){
        try{
            while (true) {
                Calendar calendar = Calendar.getInstance();
                int sec = calendar.get(13);
                if((sec %1 == 0 || sec == 0)) {
                    for (int i=0; i< PlayerManger.gI().conns.size(); i++) {
                        if(PlayerManger.gI().conns.get(i) != null && PlayerManger.gI().conns.get(i).player != null) {
                            Player player = PlayerManger.gI().conns.get(i).player;
                            if(player!=null){
                                PlayerDAO.updateDB(player);
                                if(player.map.MapCold()){
                                    if(check){ 
                                        player.sendAddchatYellow("Sức tấn công và HP của bạn sẽ bị giảm 50% vì lạnh");
                                        player.hpFull -= player.getHpFull() * 50 / 100;
                                        player.damFull -= player.getDamFull() * 50 / 100;
                                        server.Service.gI().loadPoint(player.session, player);
                                        check = false;
                                    }
                                }else if(!check){
                                    player.hpFull = player.hpGoc;
                                    player.damFull = player.damGoc;
                                    server.Service.gI().loadPoint(player.session, player);
                                    player.sendAddchatYellow( "Bạn đã rời khỏi hành tinh băng tấn công và HP được hồi phục");
                                    check = true;
                                }
                            }
                        }
                    }
                }
                Thread.sleep(1000L);
            }
        }catch(Exception e) {
        }
    }
}
