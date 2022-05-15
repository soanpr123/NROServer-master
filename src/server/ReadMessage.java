package server;

import real.player.Player;
import real.player.PlayerManger;
import server.io.Message;
import server.io.Session;

public class ReadMessage {

    private static ReadMessage instance;

    public static ReadMessage gI() {
        if (instance == null) {
            instance = new ReadMessage();
        }
        return instance;
    }

    public void getItem(Session session, Message msg) {
        Player player = PlayerManger.gI().getPlayerByUserID(session.userId);
        try {
            byte type = msg.reader().readByte();
            byte index = msg.reader().readByte();
            switch (type){
                case 0:
                    player.itemBoxToBag(index);
                    break;
                case 1:
                    player.itemBagToBox(index);
                    break;
                case 4:
                    player.itemBagToBody(index);
                    break;
                case 5:
                    player.itemBodyToBag(index);
                    break;
                case 31:
                    Service.gI().statusDetu(player);
                    break;
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
