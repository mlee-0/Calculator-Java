import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Calculator extends Application {
    String displayText = "0";

    @Override
    public void start(Stage stage) throws IOException {
        // Create layout.
        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);

        // Create number display.
        Label display = new Label(displayText);
        display.setAlignment(Pos.BASELINE_RIGHT);
        GridPane.setConstraints(display, 0, 0, 4, 1);
        root.getChildren().add(display);

        // Create digit buttons.
        Button[] buttons = new Button[10];
        for (int i = 0; i < buttons.length; i++) {
            String nameButton = String.valueOf(i);
            Button buttonDigit = new Button(nameButton);
            buttonDigit.setOnAction(event -> {
                if (displayText.equals("0")) {
                    displayText = nameButton;
                } else {
                    displayText += nameButton;
                }
                display.setText(displayText);
            });
            buttons[i] = buttonDigit;
            if (i == 0) {
                GridPane.setConstraints(buttonDigit, 0, 4, 2, 1);
            } else {
                GridPane.setConstraints(buttonDigit, (i-1)%3, 3 - (i-1)/3);
            }
            root.getChildren().add(buttonDigit);
        }
        // Create decimal point button.
        String nameButtonDecimal = ".";
        Button buttonDecimal = new Button(nameButtonDecimal);
        buttonDecimal.setOnAction(event -> {
            if (!displayText.contains(nameButtonDecimal)) {
                displayText += nameButtonDecimal;
                display.setText(displayText);
            }
        });
        GridPane.setConstraints(buttonDecimal, 2, 4);
        root.getChildren().add(buttonDecimal);

        // Create operator buttons.
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Calculator");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

//    private static void updateDisplay() {
//        display.setText(displayText);
//    }
}