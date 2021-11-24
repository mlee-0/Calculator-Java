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
    // Default text for the stored operand.
    static final String DEFAULT_OPERAND_STORED = "";
    // Default text for the current operand.
    static final String DEFAULT_OPERAND_CURRENT = "";
    // Default operation value.
    static final String DEFAULT_OPERATION = "";

    // The stored operand to be operated on.
    static String operandStored = DEFAULT_OPERAND_STORED;
    // The current operand being entered by the user.
    static String operandCurrent = DEFAULT_OPERAND_CURRENT;
    // The current operation selected.
    static String operation = DEFAULT_OPERATION;

    @Override
    public void start(Stage stage) throws IOException {
        // Create layout.
        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);

        // Create text display.
        Label display = new Label(DEFAULT_DISPLAY_TEXT);
        display.setAlignment(Pos.BASELINE_RIGHT);
        GridPane.setConstraints(display, 0, 0, 4, 1);
        root.getChildren().add(display);

        // Create digit buttons.
        Button[] buttons = new Button[10];
        for (int i = 0; i < buttons.length; i++) {
            String nameButton = String.valueOf(i);
            Button buttonDigit = new Button(nameButton);
            buttonDigit.setOnAction(event -> {
                appendText(nameButton);
                display.setText(getDisplayText());
            });
            buttonDigit.setFocusTraversable(false);
            buttonDigit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
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
            if (!operandCurrent.contains(nameButtonDecimal)) {
                appendText(nameButtonDecimal);
                display.setText(getDisplayText());
            }
        });
        buttonDecimal.setFocusTraversable(false);
        GridPane.setConstraints(buttonDecimal, 2, 4);
        root.getChildren().add(buttonDecimal);

        // Create operator buttons.

        // Create scene.
        Scene scene = new Scene(root);

        // Define key typed and key pressed event handlers.
        scene.setOnKeyTyped(event -> {
            String text = event.getCharacter();
            switch (text) {
                case "+":
                case "-":
                case "*":
                case "/":
                    if (!operandCurrent.isEmpty()) {
                        // Store the entered operand.
                        if (operandStored.isEmpty()) {
                            operandStored = new String(operandCurrent);
                        // Calculate the stored operand using the specified operation.
                        } else {
                            operandStored = calculate();
                        }
                    }
                    operandCurrent = DEFAULT_OPERAND_CURRENT;
                    operation = text;
                    break;
                default:
                    // Clear if an operation was completed and this is not an intermediate operation.
                    if (!operandStored.isEmpty() && operation.isEmpty()) {
                        clear();
                    }
                    if (Character.isLetterOrDigit(text.charAt(0)) || text.equals('.')) {
                        appendText(text);
                    }
                    break;
            }
            display.setText(getDisplayText());
        });
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ENTER:
                    operandStored = calculate();
                    operandCurrent = DEFAULT_OPERAND_CURRENT;
                    operation = DEFAULT_OPERATION;
                    break;
                case ESCAPE:
                    clear();
                    break;
                case BACK_SPACE:
                    removeCharacter();
                    break;
            }
            display.setText(getDisplayText());
        });

        // Create window.
        stage.setScene(scene);
        stage.setTitle("Calculator");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    // Get the string to be displayed based on the current operand.
    private String getDisplayText() {
        String text;
        if (!operandCurrent.isEmpty()) {
            text = operandCurrent;
        } else {
            if (operandStored.isEmpty()) {
                text = DEFAULT_DISPLAY_TEXT;
            } else {
                text = operandStored;
            }
        }
        return text;
    }

    // Add text to the display.
    private void appendText(String text) {
        operandCurrent += text;
    }

    // Remove the last character from the display.
    private void removeCharacter() {
        switch (operandCurrent.length()) {
            case 0:
                break;
            case 1:
                operandCurrent = DEFAULT_OPERAND_CURRENT;
                break;
            default:
                operandCurrent = operandCurrent.substring(0, operandCurrent.length()-1);
                break;
        }
    }

    // Solve the stored operation with the stored and current operands and return the result as text.
    private String calculate() {
        double operand1 = stringToDouble(operandStored);
        double operand2 = stringToDouble(operandCurrent);
        double result = 0.0;
        switch (operation) {
            case "+":
                result = operand1 + operand2;
                break;
            case "-":
                result = operand1 - operand2;
                break;
            case "*":
                result = operand1 * operand2;
                break;
            case "/":
                result = operand1 / operand2;
                break;
        }
        return String.valueOf(result);
    }

    // Reset the display and any stored operands.
    private void clear() {
        operandStored = DEFAULT_OPERAND_STORED;
        operandCurrent = DEFAULT_OPERAND_CURRENT;
        operation = DEFAULT_OPERATION;
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