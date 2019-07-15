
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.paint.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author redayoub
 */
public class test {
    public static void main(String[] args) {
//        Matcher matcher=Pattern.compile("hello").matcher("hello how are you hello hello hello");
//        int c=0;
//        while(matcher.find()){
//            c++;
//        }
//        System.out.println(c);

//        StringBuffer sb=new StringBuffer("Hello this is hello");
//       
//        System.out.println(sb.toString());

        Color color=Color.web("#f464ff");
        String selColor=color.toString();
        System.out.println("Color :"+selColor);
        selColor="#"+selColor.substring(2, selColor.length()-2);
        System.out.println("Color :"+ selColor);
    }
}
