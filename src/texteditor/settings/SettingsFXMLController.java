
package texteditor.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXTextArea;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import org.controlsfx.dialog.FontSelectorDialog;
import texteditor.resources.filesHandling;

/**
 * FXML Controller class
 *
 * @author redayoub
 */
public class SettingsFXMLController implements Initializable {

    @FXML
    private JFXColorPicker fontColorPicker;
    @FXML
    private JFXCheckBox textWarpSett;
    
    private FontSelectorDialog fsd;
    private static final File mSettings=new File("app_settings.xml");
    private Preferences preferences;
    
    // prfrence keys
    private static final String FONT_COLOR_KEY="font_color";
    
    private static final String FONT_SIZE_KEY="font_size";
    private static final String FONT_NAME_KEY="font_name";
    
    private static final String WARP_TEXT_KEY="warp_text";
    
    // defaul values
    private static final String DEFAULT_FONT_COLOR="#000000";
    
    private static final double DEFAULT_FONT_SIZE=Font.getDefault().getSize();
    private static final String DEFAULT_FONT_NAME=Font.getDefault().getName();
    
    private static final boolean DEFAULT_WRAP_TEXT=false;
    @FXML
    private JFXButton fontPicker;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        preferences=getPreferences(mSettings);
        Font cuurentFont=new Font(
                    preferences.get(FONT_NAME_KEY, DEFAULT_FONT_NAME),
                    preferences.getDouble(FONT_SIZE_KEY, DEFAULT_FONT_SIZE)
                );
        fontPicker.setText(cuurentFont.getName()+" "+cuurentFont.getSize());
        fsd=new FontSelectorDialog(cuurentFont);
        
        
        Color font_color=Color.web(preferences.get(FONT_COLOR_KEY, DEFAULT_FONT_COLOR));
        
        fontColorPicker.setValue(font_color);
        textWarpSett.setSelected(preferences.getBoolean(WARP_TEXT_KEY, DEFAULT_WRAP_TEXT));
        
        
        
    }    

    @FXML
    private void pickFont(ActionEvent event) {
        fsd.showAndWait();
        Font res=fsd.getResult();
        if(res==null)return;
        preferences.put(FONT_NAME_KEY, res.getName());
        preferences.putDouble(FONT_SIZE_KEY, res.getSize());
    }
    
    @FXML
    private void changeFontColor(ActionEvent event) {
        String selColor=fontColorPicker.getValue().toString();
        selColor="#"+selColor.substring(2, selColor.length()-2);
        preferences.put(FONT_COLOR_KEY,selColor);
    }

    
    
    @FXML
    private void wrapText(ActionEvent event) {
        preferences.putBoolean(WARP_TEXT_KEY, textWarpSett.isSelected());
    }

    @FXML
    private void saveAction(ActionEvent event) {
       
        try {
            // save
            preferences.exportNode(new FileOutputStream(mSettings));
            
        } catch (IOException ex) {
            Logger.getLogger(SettingsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BackingStoreException ex) {
            Logger.getLogger(SettingsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //apply
        
        applyPref();
        
        //exit
        close();
              
    }

    @FXML
    private void cancelAction(ActionEvent event) {
        close();
    }
    
    private void close(){
        fontColorPicker.getScene().getWindow().hide();
    }

    
    
    public static void applyPref(){
        
        Preferences preferences=getPreferences(mSettings);
        
        JFXTextArea textView=filesHandling.textView;
        Font myFont=new Font(
                    preferences.get(FONT_NAME_KEY, DEFAULT_FONT_NAME),
                    preferences.getDouble(FONT_SIZE_KEY, DEFAULT_FONT_SIZE)
                );
        textView.setFont(myFont);
        StringBuilder sb=new StringBuilder();
        sb.append("-fx-text-fill:").append(preferences.get(FONT_COLOR_KEY, DEFAULT_FONT_COLOR)).append(";");
        
        textView.setStyle(sb.toString());
        
        textView.setWrapText(preferences.getBoolean(WARP_TEXT_KEY, DEFAULT_WRAP_TEXT));
       
        
    }
    
    public static Preferences getPreferences(File file){
        Preferences preferences=Preferences.userRoot();
        if (file.exists()){
            try {
                Preferences.importPreferences(new FileInputStream(file));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SettingsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SettingsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidPreferencesFormatException ex) {
                Logger.getLogger(SettingsFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
           
            preferences.put(FONT_COLOR_KEY, DEFAULT_FONT_COLOR);
            preferences.put(FONT_NAME_KEY,DEFAULT_FONT_NAME);
            preferences.putDouble(FONT_SIZE_KEY, DEFAULT_FONT_SIZE);
            preferences.putBoolean(WARP_TEXT_KEY, DEFAULT_WRAP_TEXT);
        }
        return preferences;
    }

    


}
