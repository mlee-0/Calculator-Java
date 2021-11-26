import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
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

    // Number of digits to which results are rounded to.
    static final int MAX_PRECISION = 15;

    // Layout properties.
    static final int NUMBER_ROWS = 6;
    static final int NUMBER_COLUMNS = 4;
    // Font sizes.
    static final int FONT_SIZE_DEFAULT = 25;
    static final int FONT_SIZE_OPERATORS = 25;
    static final int FONT_SIZE_DISPLAY = 50;

    // Operator symbols.
    static final String PLUS_SYMBOL = "+";
    static final String PLUS_SYMBOL_DISPLAY = Character.toString('\u002b');
    static final String MINUS_SYMBOL = "-";
    static final String MINUS_SYMBOL_DISPLAY = Character.toString('\u2212');
    static final String MULTIPLY_SYMBOL = "*";
    static final String MULTIPLY_SYMBOL_DISPLAY = Character.toString('\u00d7');
    static final String DIVIDE_SYMBOL = "/";
    static final String DIVIDE_SYMBOL_DISPLAY = Character.toString('\u00f7');
    static final String POWER_SYMBOL = "^";
    static final String POWER_SYMBOL_DISPLAY = "^";
    static final String DECIMAL_SYMBOL = ".";
    static final String INVERT_SYMBOL = Character.toString('\u00b1');
    static final String RANDOM_SYMBOL = "?";
    static final String SOLVE_SYMBOL = "=";
    static final String CLEAR_SYMBOL = "C";
    // Error messages.
    static final String OVERFLOW_MESSAGE = "Overflow";
    static final String UNDERFLOW_MESSAGE = "Underflow";

    @Override
    public void start(Stage stage) throws IOException {
        // Create layout.
        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);
        root.setPadding(new Insets(10, 10, 10, 10));
        for (int i = 0; i < NUMBER_ROWS; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100 / NUMBER_ROWS);
            root.getRowConstraints().add(row);
        }
        for (int i = 0; i < NUMBER_COLUMNS; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100 / NUMBER_COLUMNS);
            root.getColumnConstraints().add(column);
        }
        root.setPrefWidth(400);
        root.setPrefHeight(500);
        root.setStyle(String.format("-fx-font-size: %dpx", FONT_SIZE_DEFAULT));

        // Create text display.
        Label display = new Label(DEFAULT_DISPLAY_TEXT);
        display.setFont(Font.font("", FontWeight.NORMAL, FONT_SIZE_DISPLAY));
        display.setAlignment(Pos.BASELINE_RIGHT);
        display.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setConstraints(display, 0, 0, NUMBER_COLUMNS, 1);
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
                GridPane.setConstraints(button, 1, NUMBER_ROWS-1);
            } else {
                GridPane.setConstraints(button, (i-1)%3, (NUMBER_ROWS-2) - (i-1)/3);
            }
            root.getChildren().add(button);
        }

        // Create decimal point button.
        Button buttonDecimal = new Button(DECIMAL_SYMBOL);
        buttonDecimal.setOnAction(event -> {
            enter(buttonDecimal.getText());
            display.setText(getDisplayText());
        });
        buttonDecimal.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonDecimal.setFocusTraversable(false);
        GridPane.setConstraints(buttonDecimal, 2, NUMBER_ROWS-1);
        root.getChildren().add(buttonDecimal);

        // Create random number generator button.
        Button buttonRandom = new Button(RANDOM_SYMBOL);
        buttonRandom.setOnAction(event -> {
            random();
            display.setText(getDisplayText());
        });
        buttonRandom.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonRandom.setFocusTraversable(false);
        GridPane.setConstraints(buttonRandom, 0, NUMBER_ROWS-1);
        root.getChildren().add(buttonRandom);

        // Create solve button.
        Button buttonSolve = new Button(SOLVE_SYMBOL);
        buttonSolve.setOnAction(event -> {
            solve();
            display.setText(getDisplayText());
        });
        buttonSolve.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonSolve.setFocusTraversable(false);
        buttonSolve.setFont(Font.font("", FontWeight.NORMAL, FONT_SIZE_OPERATORS));
        GridPane.setConstraints(buttonSolve, 3, NUMBER_ROWS-1);
        root.getChildren().add(buttonSolve);

        // Create operator buttons.
        LinkedHashMap<String, String> OPERATOR_SYMBOLS = new LinkedHashMap<String, String>();
        OPERATOR_SYMBOLS.put(PLUS_SYMBOL, PLUS_SYMBOL_DISPLAY);
        OPERATOR_SYMBOLS.put(MINUS_SYMBOL, MINUS_SYMBOL_DISPLAY);
        OPERATOR_SYMBOLS.put(MULTIPLY_SYMBOL, MULTIPLY_SYMBOL_DISPLAY);
        OPERATOR_SYMBOLS.put(DIVIDE_SYMBOL, DIVIDE_SYMBOL_DISPLAY);
        OPERATOR_SYMBOLS.put(POWER_SYMBOL, POWER_SYMBOL_DISPLAY);
        for (int i = 0; i < OPERATOR_SYMBOLS.size(); i++) {
            String operator = (String)(OPERATOR_SYMBOLS.keySet().toArray()[i]);
            String name = OPERATOR_SYMBOLS.get(operator);
            Button buttonOperator = new Button(name);
            buttonOperator.setOnAction(event -> {
                operate(operator);
                display.setText(getDisplayText());
            });
            buttonOperator.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            buttonOperator.setFocusTraversable(false);
            buttonOperator.setFont(Font.font("", FontWeight.NORMAL, FONT_SIZE_OPERATORS));
            int row = (NUMBER_ROWS - 2) - i;
            int column = 3;
            GridPane.setConstraints(buttonOperator, Math.min(column - (1-row), column), Math.max(1, row));
            root.getChildren().add(buttonOperator);
        }

        // Create clear button.
        Button buttonClear = new Button(CLEAR_SYMBOL);
        buttonClear.setOnAction(event -> {
            clear();
            display.setText(getDisplayText());
        });
        buttonClear.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonClear.setFocusTraversable(false);
        GridPane.setConstraints(buttonClear, 0, 1);
        root.getChildren().add(buttonClear);

        // Create invert sign button.
        Button buttonInvert = new Button(INVERT_SYMBOL);
        buttonInvert.setOnAction(event -> {
            invert();
            display.setText(getDisplayText());
        });
        buttonInvert.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonInvert.setFocusTraversable(false);
        GridPane.setConstraints(buttonInvert, 1, 1);
        root.getChildren().add(buttonInvert);

        // Create scene.
        Scene scene = new Scene(root);

        // Define key-button mappings.
//        HashMap<KeyCode, Button> buttonMapping = new HashMap<KeyCode, Button>();
//        buttonMapping.put(KeyCode.ENTER, buttonSolve);
//        buttonMapping.put(KeyCode.ESCAPE, buttonClear);
//        buttonMapping.put(KeyCode.DIGIT0, buttonsDigit[0]);
//        buttonMapping.put(KeyCode.DIGIT1, buttonsDigit[1]);
//        buttonMapping.put(KeyCode.DIGIT2, buttonsDigit[2]);
//        buttonMapping.put(KeyCode.DIGIT3, buttonsDigit[3]);
//        buttonMapping.put(KeyCode.DIGIT4, buttonsDigit[4]);
//        buttonMapping.put(KeyCode.DIGIT5, buttonsDigit[5]);
//        buttonMapping.put(KeyCode.DIGIT6, buttonsDigit[6]);
//        buttonMapping.put(KeyCode.DIGIT7, buttonsDigit[7]);
//        buttonMapping.put(KeyCode.DIGIT8, buttonsDigit[8]);
//        buttonMapping.put(KeyCode.DIGIT9, buttonsDigit[9]);
//        buttonMapping.put(KeyCode.PLUS, buttonsOperator[0]);
//        buttonMapping.put(KeyCode.MINUS, buttonsOperator[1]);
//        buttonMapping.put(KeyCode.ASTERISK, buttonsOperator[2]);
//        buttonMapping.put(KeyCode.SLASH, buttonsOperator[3]);
//        buttonMapping.put(KeyCode.CIRCUMFLEX, buttonsOperator[4]);

        // Define key event handlers.
        scene.setOnKeyTyped(event -> {
            String text = event.getCharacter();
            switch (text) {
                case PLUS_SYMBOL:
                case MINUS_SYMBOL:
                case MULTIPLY_SYMBOL:
                case DIVIDE_SYMBOL:
                case POWER_SYMBOL:
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
//                    Event.fireEvent(buttonSolve, new MouseEvent(
//                            MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, true, false, false, false, false, false, null
//                    ));
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

    // Enter the specified character into the current operand.
    private void enter(String text) {
        // Only add the character if it is alphanumeric or is a decimal point.
        if (Character.isLetterOrDigit(text.charAt(0)) || (text.equals(DECIMAL_SYMBOL) && !this.operandCurrent.contains(DECIMAL_SYMBOL))) {
            // If the character is a zero, only add it if the current operand is not empty.
            if (!(text.equals("0") && this.operandCurrent.equals("0"))) {
                // Clear if this an operation was just completed and is not part of an intermediate operation.
                if (!this.operandStored.isEmpty() && this.operation.isEmpty()) {
                    clear();
                }
                this.operandCurrent += text;
            }
        }
    }

    // Remove the last character from the current operand.
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
        // Copy the stored operand into the current operand in order to apply the inversion.
        if (this.operandCurrent.isEmpty()) {
            this.operandCurrent = this.operandStored;
            this.operandStored = DEFAULT_OPERAND_STORED;
        }
        // Invert sign.
        if (this.operandCurrent.contains(MINUS_SYMBOL)) {
            this.operandCurrent = this.operandCurrent.substring(1, this.operandCurrent.length());
        } else {
            this.operandCurrent = MINUS_SYMBOL + this.operandCurrent;
        }
    }

    // Generate a random number and replace the current operand.
    private void random() {
        this.operandCurrent = String.valueOf((int)(Math.random() * 100));
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
        if (text.charAt(0) == DECIMAL_SYMBOL.charAt(0)) {
            text = this.DEFAULT_DISPLAY_TEXT + text;
        }
        // Replace the hyphen, if exists, with a minus sign.
        text.replace(MINUS_SYMBOL, MINUS_SYMBOL_DISPLAY);
        return text;
    }

    // Solve the stored operation and operands and return the result as text.
    private String calculate() {
        double[] operands = new double[2];
        double result;
        // Convert the operands to doubles.
        for (int i = 0; i < operands.length; i++) {
            try {
                String text = i == 0 ? this.operandStored : this.operandCurrent;
                operands[i] = Double.parseDouble(text);
            }
            catch (NumberFormatException e) {
                operands[i] = Double.parseDouble(DEFAULT_DISPLAY_TEXT);
            }
        }
        // Perform the stored operation on the operands.
        boolean eliminateRoundoffError = false;
        switch (this.operation) {
            case PLUS_SYMBOL:
                result = operands[0] + operands[1];
                eliminateRoundoffError = true;
                break;
            case MINUS_SYMBOL:
                result = operands[0] - operands[1];
                eliminateRoundoffError = true;
                break;
            case MULTIPLY_SYMBOL:
                result = operands[0] * operands[1];
                break;
            case DIVIDE_SYMBOL:
                result = operands[0] / operands[1];
                break;
            case POWER_SYMBOL:
                result = Math.pow(operands[0], operands[1]);
                break;
            default:
                result = Double.parseDouble(DEFAULT_DISPLAY_TEXT);
                break;
        }
        // Convert the result to text.
        String text;
        if (Double.isFinite(result)) {
            if ((long)result == Long.MAX_VALUE) {
                text = OVERFLOW_MESSAGE;
            }
            else if ((long)result == Long.MIN_VALUE) {
                text = UNDERFLOW_MESSAGE;
            }
            else {
                // Round the result to a number of digits based on how many digits follow the decimal point.
                if (eliminateRoundoffError && result != 0.0) {
                    int precision = 0;
                    for (int i = 0; i < operands.length; i++) {
                        String operandText = String.valueOf(operands[0]);
                        int numberTrailingDigits = (operandText.contains(DECIMAL_SYMBOL) ? operandText.length() - operandText.indexOf(DECIMAL_SYMBOL) : 0);
                        if (numberTrailingDigits > precision && numberTrailingDigits <= MAX_PRECISION) {
                            precision = numberTrailingDigits;
                        }
                    }
                    result = (double) (Math.round(result * Math.pow(10,precision)) / Math.pow(10,precision));
                }
                // Remove decimal point and successive digits if number represents an integer.
                text = (result % 1) == 0 ? String.valueOf((long)result) : String.valueOf(result);
            }
        }
        else {
            text = String.valueOf(result);
        }
        return text;
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
}