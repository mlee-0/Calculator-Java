import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
    // The selected operation.
    static String operation = DEFAULT_OPERATION;

    static final String MINUS_SIGN = "-";

    @Override
    public void start(Stage stage) throws IOException {
        // Create layout.
        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);
        for (int i = 0; i < 6; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            root.getRowConstraints().add(row);
        }
        for (int i = 0; i < 4; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            root.getColumnConstraints().add(column);
        }
        root.setPrefWidth(400);
        root.setPrefHeight(500);
        root.setStyle("-fx-font-size: 20pt");

        // Create text display.
        Label display = new Label(DEFAULT_DISPLAY_TEXT);
        display.setFont(Font.font("System", FontWeight.NORMAL, 50));
        display.setAlignment(Pos.BASELINE_RIGHT);
        display.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setConstraints(display, 0, 0, 4, 1);
        root.getChildren().add(display);

        // Create digit buttons.
        Button[] buttons = new Button[10];
        for (int i = 0; i < buttons.length; i++) {
            String nameButton = String.valueOf(i);
            Button button = new Button(nameButton);
            button.setOnAction(event -> {
                enter(nameButton);
                display.setText(getDisplayText());
            });
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.setFocusTraversable(false);
            buttons[i] = button;
            if (i == 0) {
                GridPane.setConstraints(button, 1, 5);
            } else {
                GridPane.setConstraints(button, (i-1)%3, 4 - (i-1)/3);
            }
            root.getChildren().add(button);
        }

        // Create decimal point button.
        Button buttonDecimal = new Button(".");
        buttonDecimal.setOnAction(event -> {
            enter(buttonDecimal.getText());
            display.setText(getDisplayText());
        });
        buttonDecimal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonDecimal.setFocusTraversable(false);
        GridPane.setConstraints(buttonDecimal, 2, 5);
        root.getChildren().add(buttonDecimal);

        // Create invert sign button.
        Button buttonInvert = new Button("+/-");
        buttonInvert.setOnAction(event -> {
            invert();
            display.setText(getDisplayText());
        });
        buttonInvert.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonInvert.setFocusTraversable(false);
        GridPane.setConstraints(buttonInvert, 0, 5);
        root.getChildren().add(buttonInvert);

        // Create solve button.
        Button buttonSolve = new Button("=");
        buttonSolve.setOnAction(event -> {
            solve();
            display.setText(getDisplayText());
        });
        buttonSolve.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonSolve.setFocusTraversable(false);
        GridPane.setConstraints(buttonSolve, 3, 5);
        root.getChildren().add(buttonSolve);

        // Create operator buttons.
        String[] operators = {"+", "-", "*", "/"};
        for (int i = 0; i < operators.length; i++) {
            String operator = operators[i];
            Button buttonOperator = new Button(operator);
            buttonOperator.setOnAction(event -> {
                operate(operator);
                display.setText(getDisplayText());
            });
            buttonOperator.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            buttonOperator.setFocusTraversable(false);
            GridPane.setConstraints(buttonOperator, 3, 4-i);
            root.getChildren().add(buttonOperator);
        }

        // Create clear button.
        Button buttonClear = new Button("C");
        buttonClear.setOnAction(event -> {
            clear();
            display.setText(getDisplayText());
        });
        buttonClear.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonClear.setFocusTraversable(false);
        GridPane.setConstraints(buttonClear, 0, 1);
        root.getChildren().add(buttonClear);

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
                    operate(text);
                    break;
                default:
                    enter(text);
                    break;
            }
            display.setText(getDisplayText());
        });
        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ENTER:
                    solve();
                    break;
                case ESCAPE:
                    clear();
                    break;
                case BACK_SPACE:
                    backspace();
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

    // Get the string showing the current operand to be displayed.
    private String getDisplayText() {
        String text;
        if (!this.operandCurrent.isEmpty()) {
            text = this.operandCurrent;
        } else {
            if (this.operandStored.isEmpty()) {
                text = this.DEFAULT_DISPLAY_TEXT;
            } else {
                text = this.operandStored;
            }
        }
        // Add a "0" at the beginning if the first character is a decimal point.
        if (text.charAt(0) == '.') {
            text = this.DEFAULT_DISPLAY_TEXT + text;
        }
        return text;
    }

    // Enter the specified number into the current operand.
    private void enter(String text) {
        // Only add the character if it is alphanumeric or is a decimal point.
        if (Character.isLetterOrDigit(text.charAt(0)) || (text.equals(".") && !this.operandCurrent.contains("."))) {
            // If the character is a zero, only add it if the current operand is not empty.
            if (!(text.equals("0") && this.operandCurrent.equals(DEFAULT_OPERAND_CURRENT))) {
                // Clear if this an operation was just completed and is not part of an intermediate operation.
                if (!this.operandStored.isEmpty() && this.operation.isEmpty()) {
                    clear();
                }
                this.operandCurrent += text;
            }
        }
    }

    // Remove the last character from the display.
    private void backspace() {
        switch (this.operandCurrent.length()) {
            case 0:
                break;
            case 1:
                this.operandCurrent = this.DEFAULT_OPERAND_CURRENT;
                break;
            default:
                this.operandCurrent = this.operandCurrent.substring(0, this.operandCurrent.length()-1);
                break;
        }
    }

    // Invert the sign of the current operand.
    private void invert() {
        if (!this.operandCurrent.isEmpty()) {
            if (this.operandCurrent.contains(MINUS_SIGN)) {
                this.operandCurrent = this.operandCurrent.substring(1, this.operandCurrent.length());
            } else {
                this.operandCurrent = MINUS_SIGN + this.operandCurrent;
            }
        }
    }

    // Apply the specified operation.
    private void operate(String operation) {
        if (!this.operandCurrent.isEmpty()) {
            if (this.operandStored.isEmpty()) {
                // Store the entered operand.
                this.operandStored = new String(this.operandCurrent);
            } else {
                // Calculate the stored operand using the specified operation.
                this.operandStored = calculate();
            }
        }
        this.operandCurrent = this.DEFAULT_OPERAND_CURRENT;
        this.operation = operation;
    }

    // Solve the stored operation and operands and return the result as text.
    private String calculate() {
        double operand1 = stringToDouble(this.operandStored.isEmpty() ? DEFAULT_DISPLAY_TEXT : this.operandStored);
        double operand2 = stringToDouble(this.operandCurrent.isEmpty() ? DEFAULT_DISPLAY_TEXT : this.operandCurrent);
        double result;
        switch (this.operation) {
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
            default:
                result = 0.0;
        }
        // Remove decimal point and successive digits if number is an integer.
        String text = (result % 1) == 0 ? String.valueOf((int)result): String.valueOf(result);
        return text;
    }

    // Solve the stored operation and operands and clear the stored operation.
    private void solve() {
        if (!this.operandStored.isEmpty() && !this.operandCurrent.isEmpty()) {
            this.operandStored = calculate();
            this.operandCurrent = this.DEFAULT_OPERAND_CURRENT;
            this.operation = this.DEFAULT_OPERATION;
        }
    }

    // Reset the display and any stored operands.
    private void clear() {
        this.operandStored = this.DEFAULT_OPERAND_STORED;
        this.operandCurrent = this.DEFAULT_OPERAND_CURRENT;
        this.operation = this.DEFAULT_OPERATION;
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