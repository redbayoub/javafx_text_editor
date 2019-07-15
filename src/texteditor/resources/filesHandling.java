
package texteditor.resources;

import com.jfoenix.controls.JFXTextArea;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author redayoub
 */
public class filesHandling {

    
    public static File file;
    public static Stage stage;
    public static JFXTextArea textView;
    public static boolean textChanged;
    
    public filesHandling(File file) {
        this.file = file;
        
    }
    
    public String getText(){
       StringBuilder sb=new StringBuilder();
        try (FileReader fr=new FileReader(file);){
            int ch=fr.read();
            while(ch!=-1){
                sb.append((char) ch);
                ch=fr.read();
            }
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(filesHandling.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(filesHandling.class.getName()).log(Level.SEVERE, null, ex);
        }
        String filename=file.getName();
        String title=filename+" Text Editor";
        stage.setTitle(title);
        return sb.toString();

    }
    
    private boolean setText(String text,File newFile){
        if(newFile==null){
            newFile=file;
        }
        boolean test=false;
        try (FileWriter fw=new FileWriter(newFile);) {
            fw.write(text);
            test=true;
        } catch (IOException ex) {
            //Logger.getLogger(filesHandling.class.getName()).log(Level.SEVERE, null, ex);
            test=false;
        }
        String filename=newFile.getName();
        String title=filename+" Text Editor";
        stage.setTitle(title);
        return test;
    }
    
    
    public static void save(){
        
                filesHandling fh;
                if(file==null){
                    FileChooser chooser=new FileChooser();
                    chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
                    File ChosenFile=chooser.showSaveDialog(null);
                    if (ChosenFile==null)return;
                    File newFile=new File(ChosenFile.getAbsolutePath()+".txt");
                    fh=new filesHandling(newFile);        
                }else{
                    fh=new filesHandling(file);      
                }
                fh.setText(textView.getText(), null);
            
       
       filesHandling.textChanged=false;
       textView.setOnKeyTyped((KeyEvent event)->{
            filesHandling.textChanged=true;
            System.out.println("im working");
            textView.setOnKeyTyped(null);
       });
        
    }
    
}
    
