import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Calculator extends Application {
    // Default text displayed at the top.
    static final String DEFAULT_DISPLAY_TEXT = "0";
    // Default operand value.
    static final double DEFAULT_OPERAND = 0.0;
    // Default operation value.
    static final String DEFUALT_OPERATION = "";

    // The text displayed at the top.
    static String displayText = DEFAULT_DISPLAY_TEXT;
    // The operand on the left side of the entered operator.
    static double operand = Double.parseDouble(displayText);
    // The current operation selected.
    static String operation = DEFUALT_OPERATION;

    @Override
    public void start(Stage stage) throws IOException {
        // Create layout.
        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);

        // Create text display.
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

        // Create scene.
        Scene scene = new Scene(root);
        // Define key typed and key pressed event handlers.
        scene.setOnKeyTyped(event -> {
            char character = event.getCharacter().charAt(0);
            switch (character) {
                case '+':
                case '-':
                case '*':
                case '/':
                    operand = stringToDouble(displayText);
                    operation = event.getText();
                    break;
                default:
                    if (Character.isLetterOrDigit(character)) {
                        appendCharacter(character);
                    }
                    break;
            }
            display.setText(displayText);
        });
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ENTER:
                    break;
                case ESCAPE:
                    clear();
                case BACK_SPACE:
                    removeCharacter();
                    break;
            }
            display.setText(displayText);
        });

        // Create window.
        stage.setScene(scene);
        stage.setTitle("Calculator");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    // Add a character to the display.
    private void appendCharacter(char character) {
        String text = Character.toString(character);
        if (displayText.equals("0")) {
            displayText = text;
        } else {
            displayText += text;
        }
    }

    // Remove the last character from the display.
    private void removeCharacter() {
        if (!displayText.equals(DEFAULT_DISPLAY_TEXT)) {
            int length = displayText.length();
            if (length > 1) {
                displayText = displayText.substring(0, length-1);
            } else {
                displayText = DEFAULT_DISPLAY_TEXT;
            }
        }
    }

    // Reset the display and any stored operands.
    private void clear() {
        displayText = DEFAULT_DISPLAY_TEXT;
        operand = DEFAULT_OPERAND;
        operation = DEFUALT_OPERATION;
    }

    // Convert a string to a double.
    private static double stringToDouble(String text) {
        double number = Double.parseDouble(text);
        return number;
//        catch (Exception e) {
//            System.out.println(e);
//        }
    }
}