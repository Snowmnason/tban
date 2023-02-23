package sora.tban;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static sora.tban.AMFLController.currRoomId;
import static sora.tban.AMFLController.desBoxText;
import static sora.tban.AMFLController.mapViewImg;
import static sora.tban.AMFLController.bLowerX;
import static sora.tban.AMFLController.bLowerY;
import static sora.tban.AMFLController.BUpperX;
import static sora.tban.AMFLController.BUpperY;
import static sora.tban.AMFLController.interactRoom;
import static sora.tban.AMFLController.invyList;
import static sora.tban.AMFLController.ofInterest;
import static sora.tban.AMFLController.playPosition;
import static sora.tban.AMFLController.playerItems;
import static sora.tban.AMFLController.currFloor;
//import static sora.tban.AMFLController.buttonsText;

public class SqlManagement {

    private static Connection conn;
    static boolean hall = false;

    public static void SetRoom(String getFloorName, int getRoomId, int inRoom) {
        String sql = "SELECT * FROM " + getFloorName + " WHERE Room_Id =" + Math.abs(getRoomId);
        PreparedStatement p = null;
        ResultSet rs = null;
        try {
            p = conn.prepareStatement(sql);
            rs = p.executeQuery();
            rs.next();
            currFloor = getFloorName;
            int oldRoom = currRoomId;
            currRoomId = getRoomId;
            desBoxText = rs.getString("Room_Desciption");
            mapViewImg = rs.getString("image_link");

            int X = rs.getInt("Set_X");
            int Y = rs.getInt("Set_Y");
            int R = rs.getInt("Set_R");
            if ((Y != -1 && X != -1) && inRoom != -1) {
                if (rs.getString("Option_1") == null) {
                    playPosition.set(2, R);
                    playPosition.set(0, X);
                    playPosition.set(1, Y);
                } else if (!rs.getString("Option_1").equals("Hall")) {
                    playPosition.set(2, R);
                    playPosition.set(0, X);
                    playPosition.set(1, Y);
                } else {
                    String sqlTurn = "SELECT * FROM " + getFloorName + " WHERE Room_Id =" + Math.abs(oldRoom);
                    PreparedStatement pTurn = conn.prepareStatement(sqlTurn);
                    ResultSet rsTurn = pTurn.executeQuery();
                    rsTurn.next();
                    String OldRoom = rsTurn.getString("Option_1");
                    if (OldRoom.matches("New [nesw]Room")) {//checks if leaving a room in same hall
                        int RT = rsTurn.getInt("Set_R");
                        int XT = rsTurn.getInt("Set_X");
                        int YT = rsTurn.getInt("Set_Y");
                        if (OldRoom.matches("New [n]Room")) { //Checking if bottom or top sided room
                            playPosition.set(0, XT);
                            playPosition.set(1, YT + 41);
                        } else if (OldRoom.matches("New [s]Room")) {
                            playPosition.set(0, XT);
                            playPosition.set(1, YT - 41);
                        } else if (OldRoom.matches("New [e]Room")) {
                            playPosition.set(0, XT - 41);
                            playPosition.set(1, YT);
                        } else if (OldRoom.matches("New [w]Room")) {
                            playPosition.set(0, XT + 41);
                            playPosition.set(1, YT);
                        }
                        playPosition.set(2, Math.abs(RT - 180));
                    } else {//if new hall sent normally
                        playPosition.set(2, R);
                        playPosition.set(0, X);
                        playPosition.set(1, Y);
                    }

                }

            }
            String inter = rs.getString("Of_Interest");
            if (inter != null) {
                String[] interest = inter.split(","); //Y,X
                ofInterest = new int[interest.length];
                for (int i = 0; i < interest.length; i++) {
                    ofInterest[i] = Integer.parseInt(interest[i]);
                }
            }
            bLowerX = rs.getInt("B_LowerX");
            bLowerY = rs.getInt("B_LowerY");
            BUpperX = rs.getInt("B_UpperX");
            BUpperY = rs.getInt("B_UpperY");
        } catch (SQLException e) {
            //throw new RuntimeException(e);
        }
    }
    static String[] buttonsText;

    public static String[] SetInteration(String getFloorName, int interactRoomid) {
        buttonsText = new String[]{null, null, null, null};
        String sql = "SELECT * FROM " + getFloorName + " WHERE Room_Id =" + interactRoomid;
        PreparedStatement p = null;
        ResultSet rs = null;
        try {
            p = conn.prepareStatement(sql);
            rs = p.executeQuery();
            rs.next();
            currFloor = getFloorName;
            interactRoom = interactRoomid;
            desBoxText = rs.getString("Room_Desciption");
            mapViewImg = rs.getString("image_link");

            String inter = rs.getString("Of_Interest");
            if (inter != null) {
                String[] interest = inter.split(","); //Y,X
                ofInterest = new int[interest.length];
                for (int i = 0; i < interest.length; i++) {
                    ofInterest[i] = Integer.parseInt(interest[i]);
                }
            }

            buttonsText = new String[]{rs.getString("Option_1"), rs.getString("Option_2"), rs.getString("Option_3"), rs.getString("Option_4")};
            CheckUse(getFloorName, interactRoomid);
        } catch (SQLException e) {
            //throw new RuntimeException(e);
        }
        return buttonsText;
    }

    public static String[] ConfrimInteraction(String getFloorName, int getRoomId) {
        buttonsText = new String[]{"death", null, null, null, "on", "no"};
        String onORoff = "on";
        String add = "no";
        String sql = "SELECT * FROM " + getFloorName + " WHERE Room_Id =" + getRoomId;
        PreparedStatement p = null;
        ResultSet rs = null;
        try {
            p = conn.prepareStatement(sql);
            rs = p.executeQuery();
            rs.next();

            String newRoom = rs.getString("Option_1");
            if (newRoom != null) {
                if (newRoom.matches("New [nesw]Room") || newRoom.equals("Hall")) {
                    currFloor = rs.getString("Room_Desciption");
                    currRoomId = Integer.parseInt(rs.getString("image_link"));
                    onORoff = "New Room";
                }
                if (rs.getInt("Expanded") != -1 && rs.getInt("Expanded") != -2) {
                    interactRoom = rs.getInt("Expanded");
                    onORoff = "off";
                } else if (rs.getInt("Expanded") == -2) {
                    onORoff = "reset";
                }
                add = CheckUse(getFloorName, getRoomId);
                buttonsText = new String[]{rs.getString("Option_1"), rs.getString("Option_2"), rs.getString("Option_3"), rs.getString("Option_4"), onORoff, add};

            }//-1 more options

            desBoxText = rs.getString("Room_Desciption");
            if (rs.getString("image_link") != null) {
                mapViewImg = rs.getString("image_link");
            }
        } catch (SQLException e) {

        }
        return buttonsText;
    }

    static boolean[] useable;

    public static String CheckUse(String getFloorName, int getRoomId) {
        String add = "no";
        useable = new boolean[]{false, false, false, false};
        String sql = "SELECT * FROM " + getFloorName + " WHERE Room_Id =" + getRoomId;
        PreparedStatement p = null;
        ResultSet rs = null;
        try {
            p = conn.prepareStatement(sql);
            rs = p.executeQuery();
            rs.next();
            int item = rs.getInt("Item_Num");
            if (item != 0) {
                add = "yes";
                int itemBTN = item % 10;
                useable[itemBTN - 1] = true;
                item = item / 10;
                UpdateInvy(item, interactRoom);
                String sqlUp = null;

                if (useable[0]) {
                    sqlUp = "UPDATE " + getFloorName + "  set Use_One = true WHERE Room_Id =" + interactRoom;
                }
                if (useable[1]) {
                    sqlUp = "UPDATE " + getFloorName + "  set Use_Two = true WHERE Room_Id =" + interactRoom;
                }
                if (useable[2]) {
                    sqlUp = "UPDATE " + getFloorName + "  set Use_Three = true WHERE Room_Id =" + interactRoom;
                }
                if (useable[3]) {
                    sqlUp = "UPDATE " + getFloorName + " set Use_Four = true WHERE Room_Id =" + interactRoom;
                }
                p = conn.prepareStatement(sqlUp);

                useable = new boolean[]{rs.getBoolean("Use_One"), rs.getBoolean("Use_Two"), rs.getBoolean("Use_Three"), rs.getBoolean("Use_Four")};

                p.execute();
                return "yes";
            } else {
                useable = new boolean[]{rs.getBoolean("Use_One"), rs.getBoolean("Use_Two"), rs.getBoolean("Use_Three"), rs.getBoolean("Use_Four")};
            }

        } catch (SQLException e) {

        }
        return add;
    }

    public static int UpdateInvy(int itemId, int current_Room) {
        String sql = "SELECT * FROM Item_List WHERE Item_Id =" + itemId + " and Room_Id = " + current_Room;
        PreparedStatement p = null;
        ResultSet rs = null;
        try {
            p = conn.prepareStatement(sql);
            rs = p.executeQuery();
            rs.next();
            for (int i = 0; i < playerItems.size(); i++) {
                if (playerItems.get(i).id == itemId) {
                    playerItems.get(i).setTotal(playerItems.get(i).getTotal() + rs.getInt("Item_Total"));
                    PlayerItems x = playerItems.get(i);
                    playerItems.remove(i);
                    playerItems.add(x);
                    invyList.remove(i);
                    //invyList.add(x.getTotal() + ": " + x.getItem());
                    return 0;
                }
            }
            PlayerItems addItem = new PlayerItems(itemId, rs.getString("Item_Name"), rs.getString("Item_Desc"), rs.getInt("Item_Total"));
            playerItems.add(addItem);
        } catch (SQLException e) {
        }
        return 0;
    }

    public static void RunDB(String dbName) {
        String databaseURL = "";
        conn = null;
        try {
            databaseURL = "jdbc:ucanaccess://src\\main\\resources\\sora\\tban\\" + dbName;
            conn = DriverManager.getConnection(databaseURL);
        } catch (SQLException ex) {
            //Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void WritetoDB(int itemBTN) {

    }
}
