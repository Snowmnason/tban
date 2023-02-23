package sora.tban;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import static javafx.application.Platform.exit;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.YES;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import static sora.tban.SqlManagement.useable;

public class AMFLController implements Initializable {

    @FXML
    BorderPane mainScreen;
    @FXML
    ImageView mapView, pMini;
    @FXML
    TextArea desBox;
    @FXML
    ListView inventory;
    @FXML
    Button openInvy;
    @FXML
    Button option1, option2, option3, option4, north, west, east, south, interact;
    @FXML
    VBox MovementButtons;

    public static ObservableList<String> invyList;
    public static ArrayList<PlayerItems> playerItems = new ArrayList<PlayerItems>();
    static ArrayList<Integer> playPosition = new ArrayList<Integer>();
    static int[] ofInterest;
    static String currFloor;
    static int currRoomId, interactRoom, bLowerX, bLowerY, BUpperX, BUpperY;
    private long startTimes;
    static String desBoxText, mapViewImg;
    boolean interactFlip = true;

    void SetInteration(String getFloorName, int interactRoomid) {
        // Platform.runLater(() -> {        });

        String[] buttonsText = SqlManagement.SetInteration(getFloorName, interactRoomid);
        desBox.setText(desBoxText);
        mapView.setImage(new Image(new File(mapViewImg).toURI().toString()));
        SetButtons(buttonsText);

    }

    public void CheckIntreset() {
        for (int i = 0; i < ofInterest.length; i += 3) {
            int X = ofInterest[i] - playPosition.get(0);
            int Y = ofInterest[i + 1] - playPosition.get(1);
            interactRoom = ofInterest[i + 2];

            if (((X >= -5 && X <= 5) && (Y >= -5 && Y <= 5))) {
                if (interactRoom >= 0) {
                    interact.setDisable(false);
                    interact.setText("Interact");
                } else if (interactRoom < 0) {
                    interact.setText("Enter");
                    interact.setDisable(false);
                }
                break;
            } else {
                interact.setDisable(true);

            }
        }
    }

    public void MoreOptions(String reset, String add) {
        if (reset.equals("off")) {
            interact.setDisable(true);
        } else if (reset.equals("reset")) {
            SetRoom(currFloor, currRoomId, 0);
            CheckDisable("off");
            Interact();

        } else if (reset.equals("New Room")) {
//            System.out.println(currFloor + " " + currRoomId);
            SetRoom(currFloor, currRoomId, 0);
            CheckDisable("off");
            Interact();

        } else {
            interact.setDisable(false);
        }
        if (add.equals("yes")) {
            //System.out.println("Working add" + add);
            AddItem();
        }
    }

    @FXML
    public void Interact() {
        if (interact.getText().equals("Interact")) {
            SetInteration(currFloor, interactRoom);
            interact.setText("Exit");
            pMini.setVisible(false);
            interactFlip = false;
            ButtonFlip(1);
            openInvy.setText("Open Inventory");
            inventory.setVisible(false);
        } else if (interact.getText().equals("Enter")) {
            interact.setText("Interact");
            SetRoom(currFloor, interactRoom, 0);
            CheckDisable("off");
            CheckIntreset();
        } else {
            SetRoom(currFloor, currRoomId, -1);
            interact.setText("Interact");
            pMini.setVisible(true);
            String[] oops = {"death"};
            SetButtons(oops);
            interactFlip = true;
            openInvy.setText("Open Inventory");
            inventory.setVisible(false);
            ButtonFlip(0);
        }
    }

    public void SetButtons(String[] buttonsText) {
        if (buttonsText[0].equals("death") || buttonsText[0].equals("New Room")) {
            option1.setVisible(false);
            option2.setVisible(false);
            option3.setVisible(false);
            option4.setVisible(false);
        } else {
            if (buttonsText[0] == null) {
                option1.setVisible(false);
            } else {
                option1.setVisible(true);
                option1.setText(buttonsText[0]);
                option1.setDisable(useable[0]);
            }
            if (buttonsText[1] == null) {
                option2.setVisible(false);
            } else {
                option2.setVisible(true);
                option2.setText(buttonsText[1]);
                option2.setDisable(useable[1]);
            }
            if (buttonsText[2] == null) {
                option3.setVisible(false);
            } else {
                option3.setVisible(true);
                option3.setText(buttonsText[2]);
                option3.setDisable(useable[2]);
            }
            if (buttonsText[3] == null) {
                option4.setVisible(false);
            } else {
                option4.setVisible(true);
                option4.setText(buttonsText[3]);
                option4.setDisable(useable[3]);
            }
        }
    }

    //Option one always take, enter, or fight Room_Id + 5 * 11
    //Option two always inspect, run, wait Room_Id + 5 * 22
    //Option three Room_Id + 5 * 33
    //Option four Room_Id + 5 * 44
    void ConfrimInteraction(String getFloorName, int interactRoom) {
        String[] buttonsText = SqlManagement.ConfrimInteraction(getFloorName, interactRoom);
        desBox.setText(desBoxText);
        mapView.setImage(new Image(new File(mapViewImg).toURI().toString()));
        SetButtons(buttonsText);
        MoreOptions(buttonsText[4], buttonsText[5]);
    }

    @FXML
    void handlesOptionOne() {
        int newRoomId = (interactRoom + 5) * 11;
        System.out.println("Option 1: interactRoom= " + interactRoom + " newRoomId= " + newRoomId);
        ConfrimInteraction(currFloor, newRoomId);
    }

    @FXML
    void handlesOptionTwo() {
        int newRoomId = (interactRoom + 5) * 22;
        System.out.println("Option 2: interactRoom= " + interactRoom + " newRoomId= " + newRoomId);
        ConfrimInteraction(currFloor, newRoomId);
    }

    @FXML
    void handlesOptionThree() {
        int newRoomId = (interactRoom + 5) * 33;
        System.out.println("Option 3: interactRoom= " + interactRoom + " newRoomId= " + newRoomId);
        ConfrimInteraction(currFloor, newRoomId);

    }

    @FXML
    void handlesOptionFour() {
        int newRoomId = (interactRoom + 5) * 44;
        ConfrimInteraction(currFloor, newRoomId);
        System.out.println("Option 1: interactRoom= " + interactRoom + " newRoomId= " + newRoomId);

    }

    public void SetRoom(String getFloorName, int getRoomId, int inRoom) {
        //Add lab thing here
        SqlManagement.SetRoom(getFloorName, getRoomId, inRoom);
        desBox.setText(desBoxText);
        mapView.setImage(new Image(new File(mapViewImg).toURI().toString()));
        if (inRoom != -1) {
            pMini.setX(playPosition.get(0));
            pMini.setY(playPosition.get(1));
            pMini.setRotate(playPosition.get(2));
        }
    }

    @FXML
    public void ControlMovement() { //Make it so if you spam button it doesn't mess up the movement
        Timeline timeline = new Timeline();
        KeyFrame end = null;
        if (north.isArmed() && bLowerY <= (playPosition.get(1) - 41)) {
            playPosition.set(1, playPosition.get(1) - 41);
            end = new KeyFrame(Duration.millis(500), new KeyValue(pMini.yProperty(), playPosition.get(1)));
            pMini.setRotate(0);
        }
        if (east.isArmed() && BUpperX >= (playPosition.get(0) + 41)) {
            playPosition.set(0, playPosition.get(0) + 41);
            end = new KeyFrame(Duration.millis(500), new KeyValue(pMini.xProperty(), playPosition.get(0)));
            pMini.setRotate(90);
        }
        if (south.isArmed() && BUpperY >= (playPosition.get(1) + 41)) {
            playPosition.set(1, playPosition.get(1) + 41);
            end = new KeyFrame(Duration.millis(500), new KeyValue(pMini.yProperty(), playPosition.get(1)));
            pMini.setRotate(180);
        }
        if (west.isArmed() && bLowerX <= (playPosition.get(0) - 41)) {
            playPosition.set(0, playPosition.get(0) - 41);
            end = new KeyFrame(Duration.millis(500), new KeyValue(pMini.xProperty(), playPosition.get(0)));
            pMini.setRotate(270);
        }
        MovementButtons.setVisible(false); //Bad sulotion
        timeline.getKeyFrames().add(end);
        timeline.play();
        timeline.setOnFinished((e) -> {
            MovementButtons.setVisible(true);
        });
        CheckIntreset();
        CheckDisable("q");
    }

    void CheckDisable(String check) {
        if (bLowerY >= (playPosition.get(1) - 41)) {
            north.setDisable(true);
        } else {
            north.setDisable(false);
        }
        if (BUpperX <= (playPosition.get(0) + 41)) {
            east.setDisable(true);
        } else {
            east.setDisable(false);
        }
        if (BUpperY <= (playPosition.get(1) + 41)) {
            south.setDisable(true);
        } else {
            south.setDisable(false);
        }
        if (bLowerX >= (playPosition.get(0) - 41)) {
            west.setDisable(true);
        } else {
            west.setDisable(false);
        }
        if (check.equals("off")) {
            interact.setDisable(true);
        }
    }

    @FXML
    public void HandleUseItem() {
        int selectedIndex = inventory.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < invyList.size()) {
            Alert alert = new Alert(AlertType.NONE, "Confrimation", YES, CANCEL);
            alert.setTitle("Use Item");
            String s = "Are you sure you would like to use your " + playerItems.get(selectedIndex).getItem();
            alert.setContentText(s);
            Optional<ButtonType> result = alert.showAndWait();
            if ((result.isPresent()) && (result.get() == ButtonType.YES)) {
                int total = playerItems.get(selectedIndex).getTotal();
                if ((total - 1) == 0) {
                    invyList.remove(selectedIndex);
                    playerItems.remove(selectedIndex);
                } else {
                    PlayerItems x = playerItems.get(selectedIndex);
                    x.setTotal(x.getTotal() - 1);
                    playerItems.set(selectedIndex, x);
                    invyList.set(selectedIndex, playerItems.get(selectedIndex).getTotal() + ": " + playerItems.get(selectedIndex).getItem());
                }
            }
        }
    }

    @FXML
    public void HandlesOpenInvey() {
        if (openInvy.getText().equals("Close Inventory")) {
            openInvy.setText("Open Inventory");
            inventory.setVisible(false);
            if (interactFlip) {
                ButtonFlip(0);
            }
        } else {
            openInvy.setText("Close Inventory");
            inventory.setVisible(true);
            //interact.setDisable(true);
            if (interactFlip) {
                ButtonFlip(1);
            }
        }
    }

    public void AddItem() {
        PlayerItems addItem = playerItems.get(playerItems.size() - 1);
        invyList.add(addItem.getTotal() + ": " + addItem.getItem());
    }

    void ButtonFlip(int i) {
        boolean[] flip = new boolean[]{false, true};
        north.setDisable(flip[i]);
        east.setDisable(flip[i]);
        south.setDisable(flip[i]);
        west.setDisable(flip[i]);
        if (!flip[i]) {
            CheckDisable("q");
        }
    }

    @FXML
    public void HandlesSave() {
        ArrayList<String> saves = new ArrayList<String>();
        JsonManagement.SavePlayer(saves);
    }

    @FXML
    public void HandlesLoad() {
        invyList.clear();
        JsonManagement.SetInvy("PlayerItems.json");
        startTimes = JsonManagement.LoadPlayer();
        pMini.setX(playPosition.get(0));
        pMini.setY(playPosition.get(1));
        pMini.setRotate(playPosition.get(2));
        SetRoom(currFloor, currRoomId, -1);

    }

    @FXML
    public void HandlesClose() {
        exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        startTimes = App.startTime;
        playPosition.add(0);
        playPosition.add(0);
        playPosition.add(0);
        SqlManagement.RunDB("AMFL.accdb"); //quick test only
        invyList = inventory.getItems();
        inventory.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new addDesc();
            }
        });
        currFloor = "Outside"; //Set to Outside
        currRoomId = 0; //Sett to 0
        JsonManagement.SetInvy("StartingPlayerItems.json"); //sets starting inventory
        SetRoom(currFloor, currRoomId, 0); //Sets starting Room
        CheckDisable("q");

    }

    @FXML
    public void HandlesHelp() {
        Alert alert = new Alert(AlertType.NONE, "Confrimation", YES, CANCEL);
        alert.setTitle("Quick Game tip");
        String s = "This is a slow paced game, move in any direction but make sure to check the interact button, it will become clickable if there is something of interest, I tried my best to name the objects of interest so you can decide where to go"
                + "there might be easter eggs if you look walk around and it becomes clickable";
        alert.setContentText(s);
        Optional<ButtonType> result = alert.showAndWait();
    }

    class addDesc extends ListCell<String> {

        final Tooltip tip = new Tooltip();

        @Override
        public void updateItem(String Item, boolean empty) {
            super.updateItem(Item, empty);
            if (Item != null) {
                for (int i = 0; i < playerItems.size(); i++) {
                    String check = playerItems.get(i).getTotal() + ": " + playerItems.get(i).getItem();
                    if (check.equals(Item)) {
                        tip.setText(playerItems.get(i).getDescription());
                    }
                }
                setText(Item);
                setTooltip(tip);
            } else {
                setText("");
                setTooltip(null);
            }
        }
    }
}
//double convert = TimeUnit.SECONDS.convert((System.nanoTime() - startTimes), TimeUnit.NANOSECONDS);
