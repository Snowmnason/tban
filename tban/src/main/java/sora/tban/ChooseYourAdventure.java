package sora.tban;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.util.Duration;

public class ChooseYourAdventure {

    @FXML
    ImageView LogoPane, logoGif;
    @FXML
    Pane ButtonPane;

    @FXML
    protected void MindFlayerAdv() {
        ButtonPane.setVisible(false);
        logoGif.setImage(new Image(App.class.getResourceAsStream("AMFgif.gif")));

        PauseTransition pt = new PauseTransition(Duration.millis(5500));
        pt.play();
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                SqlManagement.RunDB("AMFL.accdb");
            }
        });
        task.run();
        
        pt.setOnFinished((e) -> {
            try {
                task.join();
                playMFA("AMFLController", "Adventure into the Mind Flayer's Lair");
            } catch (IOException ex) {
                Logger.getLogger(ChooseYourAdventure.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(ChooseYourAdventure.class.getName()).log(Level.SEVERE, null, ex);
            } 
        });
    }
    private void playMFA(String game, String Title) throws IOException {
        App.setRoot(game);
        Stage primStage = App.stage1;
        primStage.setTitle(Title);
        primStage.getIcons().set(0, App.iconsList.get(1));
        primStage.sizeToScene();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primStage.setX((screenBounds.getWidth() - primStage.getWidth()) / 2);
        primStage.setY((screenBounds.getHeight() - primStage.getHeight()) / 2);

    }
}
