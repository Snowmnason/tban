package sora.tban;

import com.google.gson.annotations.SerializedName;
public class PlayerItems {
    @SerializedName("id")
    int id;
    @SerializedName("Item")
    String item;
    @SerializedName("description")
    String description;
    @SerializedName("total")
    int total;
    
    PlayerItems(){
        id = 0;        item = "";        description = "";        total = 0;
    }
    public  PlayerItems(int id, String item, String description, int total) {
        this.id = id;
        this.item = item;
        this.description = description;
        this.total = total;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    
}
