package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Utils {
    public static Stage currentStage(ActionEvent event){
        return (Stage)((Node)event.getSource()).getScene().getWindow();
    }

    public static Integer tryParsetoInt(String str){
        try {
            return Integer.parseInt(str);
        }catch (NumberFormatException e){
            return null;
        }

    }

}
