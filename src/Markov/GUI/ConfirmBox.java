package Markov.GUI;
/*
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {
   private static boolean accepted = false;
    public static boolean display(String title, String message){

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);
        Button close = new Button();
        close.setText("Close");
        close.setOnAction(e->{
            accepted=false;
            window.close();
        });

        Button accept = new Button("Accept");
        accept.setOnAction(e->{
            accepted=true;
            window.close();
        });


        VBox box = new VBox(10);
        box.getChildren().addAll(label,close,accept);
        box.setAlignment(Pos.CENTER);

        Scene scene = new Scene(box);
        window.setScene(scene);
        window.showAndWait();

        return accepted;
    }
}
*/