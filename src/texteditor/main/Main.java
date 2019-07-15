/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texteditor.main;

import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import static texteditor.main.MainViewController.findTh;
import texteditor.resources.filesHandling;

/**
 *
 * @author redayoub
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        filesHandling.stage=stage;
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Text Editor by Red Ayoub");
        stage.show();
        stage.setOnCloseRequest((event)->{            
            if (filesHandling.textChanged==false)return;
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Saving file");
            alert.setContentText("Save changes to document before closing?");
            ButtonType bt1=new ButtonType("Yes",ButtonBar.ButtonData.YES);
            ButtonType bt2=new ButtonType("No",ButtonBar.ButtonData.NO);
            ButtonType bt3=new ButtonType("Cancel",ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(bt1,bt2,bt3);
            Optional<ButtonType> res=alert.showAndWait();
            if (res.get().equals(bt1)){
                System.out.println("Yes");
                filesHandling.save();
            }else{
                if (res.get().equals(bt2)){
                    System.out.println("No");
                    if (findTh!=null) {
                        findTh.StopThread();
                    }
                }else{
                    if (res.get().equals(bt3)){
                       System.out.println("Cancel"); 
                       event.consume();
                    }
                }
            }
        });
        
       
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
