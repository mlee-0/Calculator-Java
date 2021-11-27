import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Calculator extends Application {
    static final String PROGRAM_NAME = "Calculator";
    static final String FILENAME_LOGO = "logo.png";
    static final String FOLDER_RESOURCES = ".";

    // Default text displayed at the top.
    static final String DEFAULT_DISPLAY_TEXT = "0";
    // Default text for the stored operand.
    static final String DEFAULT_OPERAND_STORED = "";
    // Default text for the current operand.
    static final String DEFAULT_OPERAND_CURRENT = "";
    // Default text for the repeated operand.
    static final String DEFAULT_OPERAND_REPEATED = "";
    // Default operation value.
    static final String DEFAULT_OPERATION = "";

    // The stored operand to be operated on.
    static String operandStored = DEFAULT_OPERAND_STORED;
    // The current operand being entered by the user.
    static String operandCurrent = DEFAULT_OPERAND_CURRENT;
    // The operand used by repeated calculations when no explicitly operands are input.
    static String operandRepeated = DEFAULT_OPERAND_REPEATED;
    // The selected operation.
    static String operation = DEFAULT_OPERATION;
    // The previous operation, which is stored after a solve.
    static String operationRepeated = DEFAULT_OPERATION;

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
    public void start(Stage stage) {
        // Create layout.
        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);
        root.setPadding(new Insets(10, 10, 10, 10));
        for (int i = 0; i < NUMBER_ROWS; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / NUMBER_ROWS);
            root.getRowConstraints().add(row);
        }
        for (int i = 0; i < NUMBER_COLUMNS; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / NUMBER_COLUMNS);
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
        Button[] buttonsDigit = new Button[10];
        for (int i = 0; i < buttonsDigit.length; i++) {
            String nameButton = String.valueOf(i);
            Button button = new Button(nameButton);
            button.setOnAction(event -> {
                enter(nameButton);
                display.setText(getDisplayText());
            });
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.setFocusTraversable(false);
            buttonsDigit[i] = button;
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
        LinkedHashMap<String, String> OPERATOR_SYMBOLS = new LinkedHashMap<>();
        OPERATOR_SYMBOLS.put(PLUS_SYMBOL, PLUS_SYMBOL_DISPLAY);
        OPERATOR_SYMBOLS.put(MINUS_SYMBOL, MINUS_SYMBOL_DISPLAY);
        OPERATOR_SYMBOLS.put(MULTIPLY_SYMBOL, MULTIPLY_SYMBOL_DISPLAY);
        OPERATOR_SYMBOLS.put(DIVIDE_SYMBOL, DIVIDE_SYMBOL_DISPLAY);
        OPERATOR_SYMBOLS.put(POWER_SYMBOL, POWER_SYMBOL_DISPLAY);
        Button[] buttonsOperator = new Button[OPERATOR_SYMBOLS.size()];
        for (int i = 0; i < OPERATOR_SYMBOLS.size(); i++) {
            String operator = (String)(OPERATOR_SYMBOLS.keySet().toArray()[i]);
            String name = OPERATOR_SYMBOLS.get(operator);
            Button button = new Button(name);
            button.setOnAction(event -> {
                operate(operator);
                display.setText(getDisplayText());
            });
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.setFocusTraversable(false);
            button.setFont(Font.font("", FontWeight.NORMAL, FONT_SIZE_OPERATORS));
            buttonsOperator[i] = button;
            int row = (NUMBER_ROWS - 2) - i;
            int column = 3;
            GridPane.setConstraints(button, Math.min(column - (1-row), column), Math.max(1, row));
            root.getChildren().add(button);
        }

        // Create backspace button.
        Button buttonBackspace = new Button();
        buttonBackspace.setOnAction(event -> {
            backspace();
            display.setText(getDisplayText());
        });

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
        Scene scene = new Scene(root);// Define key-button mappings. Each value is either a Button or a 2-length array of Buttons, where the first Button is the default and the second Button is the button to use if Shift is used with the key.
        HashMap<KeyCode, Object> buttonMapping = new HashMap<>();
        buttonMapping.put(KeyCode.ENTER, buttonSolve);
        buttonMapping.put(KeyCode.ESCAPE, buttonClear);
        buttonMapping.put(KeyCode.BACK_SPACE, buttonBackspace);
        buttonMapping.put(KeyCode.DIGIT0, buttonsDigit[0]);
        buttonMapping.put(KeyCode.DIGIT1, buttonsDigit[1]);
        buttonMapping.put(KeyCode.DIGIT2, buttonsDigit[2]);
        buttonMapping.put(KeyCode.DIGIT3, buttonsDigit[3]);
        buttonMapping.put(KeyCode.DIGIT4, buttonsDigit[4]);
        buttonMapping.put(KeyCode.DIGIT5, buttonsDigit[5]);
        buttonMapping.put(KeyCode.DIGIT6, new Button[]{buttonsDigit[6], buttonsOperator[4]});
        buttonMapping.put(KeyCode.DIGIT7, buttonsDigit[7]);
        buttonMapping.put(KeyCode.DIGIT8, new Button[]{buttonsDigit[8], buttonsOperator[2]});
        buttonMapping.put(KeyCode.DIGIT9, buttonsDigit[9]);
        buttonMapping.put(KeyCode.NUMPAD0, buttonsDigit[0]);
        buttonMapping.put(KeyCode.NUMPAD1, buttonsDigit[1]);
        buttonMapping.put(KeyCode.NUMPAD2, buttonsDigit[2]);
        buttonMapping.put(KeyCode.NUMPAD3, buttonsDigit[3]);
        buttonMapping.put(KeyCode.NUMPAD4, buttonsDigit[4]);
        buttonMapping.put(KeyCode.NUMPAD5, buttonsDigit[5]);
        buttonMapping.put(KeyCode.NUMPAD6, buttonsDigit[6]);
        buttonMapping.put(KeyCode.NUMPAD7, buttonsDigit[7]);
        buttonMapping.put(KeyCode.NUMPAD8, buttonsDigit[8]);
        buttonMapping.put(KeyCode.NUMPAD9, buttonsDigit[9]);
        buttonMapping.put(KeyCode.EQUALS, new Button[]{buttonSolve, buttonsOperator[0]});
        buttonMapping.put(KeyCode.ADD, buttonsOperator[0]);
        buttonMapping.put(KeyCode.MINUS, buttonsOperator[1]);
        buttonMapping.put(KeyCode.SUBTRACT, buttonsOperator[1]);
        buttonMapping.put(KeyCode.MULTIPLY, buttonsOperator[2]);
        buttonMapping.put(KeyCode.SLASH, new Button[]{buttonsOperator[3], buttonRandom});
        buttonMapping.put(KeyCode.DIVIDE, buttonsOperator[3]);
        buttonMapping.put(KeyCode.PERIOD, buttonDecimal);
        buttonMapping.put(KeyCode.DECIMAL, buttonDecimal);

        // Create event handler for key events.
        EventHandler<KeyEvent> handlerKey = event -> {
            Object value = buttonMapping.get(event.getCode());
            if (value == null) {
                return;
            }
            // Get the button to use.
            Button button;
            if (value.getClass().isArray()){
                button = event.isShiftDown() ? ((Button[])value)[1] : ((Button[])value)[0];
            }
            else {
                if (event.isShiftDown()) {
                    return;
                }
                button = (Button)value;
            }
            // Highlight the button.
            if (event.getEventType() == KeyEvent.KEY_PRESSED) {
                button.arm();
            }
            // Perform the action and un-highlight the button.
            else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
                button.fire();
                button.disarm();
            }
        };

        // Set handler to key events..
        scene.setOnKeyPressed(handlerKey);
        scene.setOnKeyReleased(handlerKey);

        // Create window.
        stage.setScene(scene);
        stage.setTitle(PROGRAM_NAME);
        stage.getIcons().add(new Image(Paths.get(FOLDER_RESOURCES, FILENAME_LOGO).toString()));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    // Enter the specified character into the current operand.
    private void enter(String text) {
        // Only add the character if it is alphanumeric or is a decimal point.
        if (Character.isLetterOrDigit(text.charAt(0)) || (text.equals(DECIMAL_SYMBOL) && !operandCurrent.contains(DECIMAL_SYMBOL))) {
            // Do not add the character if it is a zero and the current operand is "0".
            if (!(text.equals("0") && operandCurrent.equals("0"))) {
                // Clear if this an operation was just completed and is not part of an intermediate operation.
                if (!operandStored.isEmpty() && operation.isEmpty()) {
                    clear();
                }
                // If the operand only contains "0", remove it.
                if (operandCurrent.equals("0")) {
                    operandCurrent = DEFAULT_OPERAND_CURRENT;
                }
                operandCurrent += text;
            }
        }
    }

    // Remove the last character from the current operand.
    private void backspace() {
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

    // Invert the sign of the current operand.
    private void invert() {
        // Copy the stored operand into the current operand in order to apply the inversion.
        if (operandCurrent.isEmpty()) {
            operandCurrent = operandStored;
            operandStored = DEFAULT_OPERAND_STORED;
        }
        // Invert sign.
        if (operandCurrent.contains(MINUS_SYMBOL)) {
            operandCurrent = operandCurrent.substring(1);
        } else {
            operandCurrent = MINUS_SYMBOL + operandCurrent;
        }
    }

    // Generate a random number and replace the current operand.
    private void random() {
        operandCurrent = String.valueOf((int)(Math.random() * 100));
    }

    // Get the string showing the current operand to be displayed.
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
        // Add a "0" at the beginning if the first character is a decimal point.
        if (text.charAt(0) == DECIMAL_SYMBOL.charAt(0)) {
            text = DEFAULT_DISPLAY_TEXT + text;
        }
        // Replace the hyphen, if exists, with a minus sign.
        text = text.replace(MINUS_SYMBOL, MINUS_SYMBOL_DISPLAY);
        return text;
    }

    // Solve the stored operation and operands and return the result as text.
    private String calculate() {
        // Convert the operands to doubles.
        double[] operands = new double[2];
        for (int i = 0; i < operands.length; i++) {
            try {
                String text = i == 0 ? operandStored : (!operandCurrent.equals(DEFAULT_OPERAND_CURRENT) ? operandCurrent : operandRepeated);
                operands[i] = Double.parseDouble(text);
            }
            catch (NumberFormatException e) {
                operands[i] = Double.parseDouble(DEFAULT_DISPLAY_TEXT);
            }
        }

        // Perform the calculation on the operands, using the stored operation if no current operation exists.
        double result;
        boolean eliminateRoundoffError = false;
        switch (!operation.equals(DEFAULT_OPERATION) ? operation : operationRepeated) {
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
                    result = Math.round(result * Math.pow(10,precision)) / Math.pow(10,precision);
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

    // Store the specified operation or perform an intermediate calculation with it.
    private void operate(String operator) {
        if (!operandCurrent.isEmpty()) {
            // Store the entered operand.
            if (operandStored.isEmpty()) {
                operandStored = new String(operandCurrent);
            }
            // Calculate the specified operation on the existing operands and store the result as the stored operand.
            else {
                operandStored = calculate();
            }
        }
        operandCurrent = DEFAULT_OPERAND_CURRENT;
        operation = operator;
    }

    // Solve the stored operation and operands and clear the stored operation.
    private void solve() {
        // Store the current operand and operation for repeated calculations.
        if (!operandCurrent.equals(DEFAULT_OPERAND_CURRENT)) {
            operandRepeated = operandCurrent;
        }
        if (!operation.equals(DEFAULT_OPERATION)) {
            if (!operandCurrent.equals(DEFAULT_OPERAND_CURRENT)) {
                operationRepeated = operation;
            }
            // Revert to the previously entered operation if a new operation was entered but no operand was entered.
            else {
                operation = operationRepeated;
            }
        }

        if (!operandStored.isEmpty() && (!operandCurrent.isEmpty() || !operandRepeated.isEmpty())) {
            operandStored = calculate();
            operandCurrent = DEFAULT_OPERAND_CURRENT;
            operation = DEFAULT_OPERATION;
        }
    }

    // Reset the display and any stored operands.
    private void clear() {
        operandCurrent = DEFAULT_OPERAND_CURRENT;
        operandStored = DEFAULT_OPERAND_STORED;
        operandRepeated = DEFAULT_OPERAND_REPEATED;
        operation = DEFAULT_OPERATION;
        operationRepeated = DEFAULT_OPERATION;
    }
}