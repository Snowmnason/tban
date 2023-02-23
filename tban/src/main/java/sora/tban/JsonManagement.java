package sora.tban;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import static sora.tban.AMFLController.currFloor;
import static sora.tban.AMFLController.currRoomId;
import static sora.tban.AMFLController.playerItems;
import static sora.tban.AMFLController.invyList;
import static sora.tban.AMFLController.playPosition;

public class JsonManagement {

    public static void SavePlayer(ArrayList<String> saves) {
        Gson gson = new Gson();
        saves.add(playPosition.get(0) + "");
        saves.add(playPosition.get(1) + "");
        saves.add(playPosition.get(2) + "");
        saves.add(currFloor);
        saves.add(currRoomId + "");
        saves.add(App.startTime + "");
        
        try {
            File file = new File("src\\main\\java\\sora\\tban\\PlayerItems.json");
            FileWriter fw = new FileWriter(file);
            gson.toJson(playerItems, fw);
            fw.close();
            File file1 = new File("src\\main\\java\\sora\\tban\\PlayerPosition.json");
            FileWriter fw1 = new FileWriter(file1);
            gson.toJson(saves, fw1);
            fw1.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static  Long LoadPlayer() {//long
        String[] l = null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            File file = new File("src\\main\\java\\sora\\tban\\PlayerPosition.json");
            FileReader fr = new FileReader(file);
            l = gson.fromJson(fr, String[].class); //got from slides, I dunno if this is the smartest....e is an array, add everything from json to the array, and it works, def need to ask about this
            fr.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        playPosition.set(0, Integer.parseInt(l[0]));
        playPosition.set(1, Integer.parseInt(l[1]));
        playPosition.set(2, Integer.parseInt(l[2]));
        currFloor = l[3];
        currRoomId = Integer.parseInt(l[4]);
        //return l;
        return Long.parseLong(l[5]);
    }

    public static void SetInvy(String json) {
        PlayerItems[] p;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            File file = new File("src\\main\\java\\sora\\tban\\" + json);
            FileReader fr = new FileReader(file);
            p = gson.fromJson(fr, PlayerItems[].class); //got from slides, I dunno if this is the smartest....e is an array, add everything from json to the array, and it works, def need to ask about this
            fr.close();
            Collection<PlayerItems> coll = Arrays.asList(p);
            coll.stream().forEach(x -> playerItems.add(x));
            coll.stream().forEach(x -> invyList.add(x.getTotal() + ": " + x.getItem()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
