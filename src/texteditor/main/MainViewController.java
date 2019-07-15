
package texteditor.main;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import texteditor.resources.filesHandling;
import texteditor.settings.SettingsFXMLController;



/**
 *
 * @author redayoub
 */
public class MainViewController implements Initializable {

    @FXML
    private JFXTextArea textView;
    @FXML
    private MenuItem menuSave;
     @FXML
    private MenuItem openMenuItem;
     @FXML
    private MenuItem saveAsMenuItem;
     @FXML
    private BorderPane rootPane;
    @FXML
    private JFXTextField findWord;
    @FXML
    private JFXTextField replaceByWord;
    @FXML
    private Label msg;
    
    filesHandling fh;
    
    private AnchorPane findPane;
    private AnchorPane replacePane;
 
    private VBox bottomPane;
    static FindThread findTh;
    Matcher matcher;
    ArrayList<FindInfoHolder> findInfosList;
    ArrayList<FindInfoHolder> DeletedfindInfosList;
    int currentFound;
    int maxMatches;
    
    
    
    
    
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        textView.setFocusColor(null);
        textView.setUnFocusColor(null);
        filesHandling.textView=textView;
        filesHandling.textChanged=false;
        menuSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN,KeyCombination.SHIFT_DOWN));
        VBox  sBottomPane=(VBox)rootPane.getBottom();
        findPane=(AnchorPane) sBottomPane.getChildren().get(0);
        replacePane=(AnchorPane) sBottomPane.getChildren().get(1);
        bottomPane=new VBox();
        rootPane.setBottom(bottomPane);
        maxMatches=0;
        findWord.setOnKeyReleased((e)->{
            if(e.getCode().equals(KeyCode.ENTER)){
                nextFound(null);
            }
        });
        SettingsFXMLController.applyPref();
    }    

    @FXML
    private void openF(ActionEvent event) {
        FileChooser chooser=new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*"));
        File chosenFile=chooser.showOpenDialog(null);
        fh=new filesHandling(chosenFile);
        textView.setText(fh.getText());
        filesHandling.textChanged=false;
    }

    @FXML
    private void save(ActionEvent event) {
        if (filesHandling.textChanged) {
           filesHandling.save();
        }
        
    }

    @FXML
    private void saveAs(ActionEvent event) {
        filesHandling.file=null;
        filesHandling.save();
    }

    @FXML
    private void print(ActionEvent event) {
        ObservableSet<Printer> l=Printer.getAllPrinters();
        if (l.isEmpty()){
            Alert al=new Alert(Alert.AlertType.ERROR,"No printers avaialble",ButtonType.CLOSE);
            al.show();
            return;
        }
        
        TextFlow printArea = new TextFlow(new Text(textView.getText()));

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        
        if (printerJob != null && printerJob.showPrintDialog(textView.getScene().getWindow())) {
            PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();
            printArea.setMaxWidth(pageLayout.getPrintableWidth());

            if (printerJob.printPage(printArea)) {
                printerJob.endJob();
                // done printing
            } else {
                System.out.println("Failed to print");
            }
        } else {
            System.out.println("Canceled");
        }
        
    }
    // to make settings moveable
    double xOffset=0;
    double yOffset=0;
    
    @FXML
    private void settings(ActionEvent event) {
        try {
            
            
            Parent root = FXMLLoader.load(getClass().getResource("/texteditor/settings/SettingsFXML.fxml"));
            Scene scene = new Scene(root);
            Stage stage=new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.setTitle("Settings");
            // make it moveable
            root.setOnMousePressed((ev)->{
                xOffset=ev.getSceneX();
                yOffset=ev.getSceneY();
            });
            root.setOnMouseDragged((ev2)->{
                stage.setX(ev2.getScreenX()- xOffset);
                 stage.setY(ev2.getScreenY()- yOffset);
            });
            
            //show
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void find(ActionEvent event) {
        if (bottomPane.getChildren().isEmpty()){
            bottomPane.getChildren().add(findPane);
            bottomPane.setMaxHeight(1);
            msg.setText(null);
            findWord.setText("");
        }
        if(findTh==null){
            findTh=new FindThread();
        }else{
            findTh.StopThread();
            findTh=new FindThread();
            
        }
        
        findTh.start();
        
    }

    @FXML
    private void replace(ActionEvent event) {
        if (bottomPane.getChildren().isEmpty()){
            bottomPane.getChildren().add(findPane);
            msg.setText(null);
            findWord.setText("");
        }
        bottomPane.getChildren().add(replacePane);
        bottomPane.setMaxHeight(1);
       
        if (findTh==null){
            findTh=new FindThread();
            findTh.start();
        }
    }

    @FXML
    private void about(ActionEvent event) {
    }
    
    @FXML
    private void close(ActionEvent event) {
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
                }
            }
        }
    }

    

    @FXML
    private void textChanged(KeyEvent event) {
            filesHandling.textChanged=true;
            System.out.println("im working");
            textView.setOnKeyTyped(null);
    }

    @FXML
    private void closeFindView(MouseEvent event) {
        bottomPane.getChildren().clear();
        findTh.StopThread();
        findTh=null;
        matcher=null;
        findInfosList=null;
       
    }
    
    
    @FXML
    private void pervFound(ActionEvent event) {
 
        if(findInfosList!=null&&findInfosList.size()>0){
        currentFound--;
        if (currentFound==-1) {
            currentFound=findInfosList.size()-1;
        }
        textView.selectRange(findInfosList.get(currentFound).getStart(), findInfosList.get(currentFound).getEnd());
        msg.setStyle("-fx-text-fill:#000000");
        msg.setText((currentFound+1)+" of "+findInfosList.size()+" matches");
        
        }else{
            msg.setStyle("-fx-text-fill:#ff0000");
            msg.setText("No matches");
            
        }
    }

    @FXML
    private void nextFound(ActionEvent event) {

        
        if(findInfosList!=null&&findInfosList.size()>0){
            currentFound++;
            if (currentFound==findInfosList.size()) {
                currentFound=0;
            }
            textView.selectRange(findInfosList.get(currentFound).getStart(), findInfosList.get(currentFound).getEnd());
            msg.setStyle("-fx-text-fill:#000000");
           msg.setText((currentFound+1)+" of "+findInfosList.size()+" matches");
            
        }else{
            msg.setStyle("-fx-text-fill:#ff0000");
            msg.setText("No matches");
             
        }
        
    }

    @FXML
    private void replaceCurrent(ActionEvent event) {
        if(findInfosList!=null&&findInfosList.size()>0){
            try{
                
                String p1=textView.getText().substring(0, findInfosList.get(currentFound).getStart());
                String p2=textView.getText().substring(findInfosList.get(currentFound).getEnd());

                
                StringBuilder sb=new StringBuilder(p1);
                sb.append(replaceByWord.getText());
                sb.append(p2);
                textView.setText(sb.toString());
                int edit;
                if (replaceByWord.getText().length()>findWord.getText().length()){
                     edit=replaceByWord.getText().length()-findWord.getText().length();
                }else{
                    if (replaceByWord.getText().length()<findWord.getText().length()){
                        edit=replaceByWord.getText().length()-findWord.getText().length();
                    }else{  //replaceByWord.getText().length()==findWord.getText().length()
                        // do nothing
                        edit=0;
                    }   
                }
                for (int i = currentFound; i < findInfosList.size(); i++) {
                    findInfosList.get(i).start+=edit;
                    findInfosList.get(i).end+=edit;
                }
                DeletedfindInfosList.add(findInfosList.get(currentFound));
                findInfosList.remove(currentFound);
                currentFound-=1;
                
                if(currentFound<-1){
                    currentFound=-1;
                }
                
                nextFound(null);
            
            }catch(Exception ex){
                
            }
        }
        
    }

    @FXML
    private void repalceAll(ActionEvent event) {
        
        while(!findInfosList.isEmpty()) {
            int i=0;
            String p1=textView.getText().substring(0, findInfosList.get(i).getStart());
            String p2=textView.getText().substring(findInfosList.get(i).getEnd());
            StringBuilder sb=new StringBuilder(p1);
            sb.append(replaceByWord.getText());
            sb.append(p2);
            textView.setText(sb.toString());
            int edit;
            if (replaceByWord.getText().length()>findWord.getText().length()){
                edit=replaceByWord.getText().length()-findWord.getText().length();
            }else{
                if (replaceByWord.getText().length()<findWord.getText().length()){
                    edit=replaceByWord.getText().length()-findWord.getText().length();
                }else{  //replaceByWord.getText().length()==findWord.getText().length()
                    // do nothing
                    edit=0;
                }   
            }
            for (int j = i; j < findInfosList.size(); j++) {
                findInfosList.get(j).start+=edit;
                findInfosList.get(j).end+=edit;
            }
            DeletedfindInfosList.add(findInfosList.get(i));
            findInfosList.remove(i);
        }
        msg.setStyle("-fx-text-fill:#ff0000");
        msg.setText("No matches");
    }

    
    class FindThread extends Thread{
        private boolean working;
        @Override
        public void run() {
            working=true;
           int pervFindLen=findWord.getText().length();
           int pervTextLen=textView.getText().length();
                while(working){
                    if((findWord.getText().length()!=pervFindLen)){
                    // do serching here
                        
                        textView.selectRange(0,0);
                        matcher=Pattern.compile(findWord.getText()).matcher(textView.getText());

                        if(matcher.find()){
                            
                            findInfosList=new ArrayList<>();
                            DeletedfindInfosList=new ArrayList<>();
                            matcher.reset();
                            while(matcher.find()){
                                findInfosList.add(new FindInfoHolder(matcher.start(), matcher.end()));
                            }
                            textView.selectRange(findInfosList.get(0).getStart(), findInfosList.get(0).getEnd());
                            currentFound=0;
                            maxMatches=findInfosList.size();
                            Platform.runLater(() -> {
                                msg.setStyle("-fx-text-fill:#000000");
                                msg.setText("1 of "+maxMatches+" matches");
                            });
                            
                        }else{
                            Platform.runLater(() -> {
                                msg.setStyle("-fx-text-fill:#ff0000");
                                msg.setText("No matches");
                            });
                            
                        }
                        
                        pervFindLen=findWord.getText().length();
                        pervTextLen=textView.getText().length();
                    }
                    if (pervTextLen!=textView.getText().length()) {
                        matcher=Pattern.compile(findWord.getText()).matcher(textView.getText());
                        if(matcher.find()){
                            
                            findInfosList=new ArrayList<>();
                            matcher.reset();
                            while(matcher.find()){
                                
                                findInfosList.add(new FindInfoHolder(matcher.start(), matcher.end()));
                            }
                            findInfosList.removeAll(DeletedfindInfosList);
                            
                            maxMatches=findInfosList.size();
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        }
        
        public void StopThread(){
            working=false;
        }
        
    }
    
   static class FindInfoHolder{
        int start;
        int end;

        public FindInfoHolder(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
        
    }
    
    
    
}
