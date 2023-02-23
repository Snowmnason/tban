package sora.tban;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import static javafx.application.Application.launch;

public class App extends Application {
    public static Scene scene;
    public static Stage stage1;
    public static ArrayList<Image> iconsList = new ArrayList<Image>();

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("ChooseYourAdventure"));
        //scene = new Scene(loadFXML("AMFLController")); //TESTING code
        stage.setTitle("Choose your Adventure");

        stage.setResizable(false);
        stage.setScene(scene);
        stage1 = stage;
        String css = this.getClass().getResource("styleSheet.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.getIcons().setAll(iconsList);
        stage.show();
    }
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));

        return fxmlLoader.load();

    }
    static long startTime;
    public static void main(String[] args) {
        iconsList.add(new Image(App.class.getResourceAsStream("Logo.png")));//0
        iconsList.add(new Image(App.class.getResourceAsStream("AMFLogo.png")));//1
       startTime = System.nanoTime();
        launch();
    }
}