package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {

    private Stage window;
    private Scene setupScene, gameScene;
    private TextField rangeMinInput, rangeMaxInput, attemptsInput, roundsInput, guessInput;
    private Label feedbackLabel, roundsWonLabel;
    private Button startButton, submitGuessButton;
    private Random random;
    private int targetNumber, attemptsLeft, roundsLeft, roundsWon, minRange, maxRange;

    private static final String CSS_PATH = "application.css";
    private static final int SCENE_WIDTH = 400;
    private static final int SCENE_HEIGHT = 200;

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Number Guessing Game");

        setupScene = createSetupScene();
        window.setScene(setupScene);
        window.show();
    }

    private Scene createSetupScene() {
        GridPane setupGrid = new GridPane();
        setupGrid.setPadding(new Insets(20, 20, 20, 20));
        setupGrid.setVgap(15);
        setupGrid.setHgap(10);
        setupGrid.getStyleClass().add("grid-pane");

        Label rangeLabel = new Label("Enter Range:");
        GridPane.setConstraints(rangeLabel, 0, 0);
        rangeLabel.getStyleClass().add("label");

        rangeMinInput = new TextField();
        rangeMinInput.setPromptText("Min");
        GridPane.setConstraints(rangeMinInput, 1, 0);
        rangeMinInput.getStyleClass().add("text-field");

        rangeMaxInput = new TextField();
        rangeMaxInput.setPromptText("Max");
        GridPane.setConstraints(rangeMaxInput, 2, 0);
        rangeMaxInput.getStyleClass().add("text-field");

        Label attemptsLabel = new Label("Number of Attempts:");
        GridPane.setConstraints(attemptsLabel, 0, 1);
        attemptsLabel.getStyleClass().add("label");

        attemptsInput = new TextField();
        attemptsInput.setPromptText("Attempts");
        GridPane.setConstraints(attemptsInput, 1, 1);
        attemptsInput.getStyleClass().add("text-field");

        Label roundsLabel = new Label("Number of Rounds:");
        GridPane.setConstraints(roundsLabel, 0, 2);
        roundsLabel.getStyleClass().add("label");

        roundsInput = new TextField();
        roundsInput.setPromptText("Rounds");
        GridPane.setConstraints(roundsInput, 1, 2);
        roundsInput.getStyleClass().add("text-field");

        startButton = new Button("Start Game");
        GridPane.setConstraints(startButton, 1, 3);
        startButton.getStyleClass().add("button");

        setupGrid.getChildren().addAll(rangeLabel, rangeMinInput, rangeMaxInput, attemptsLabel, attemptsInput, roundsLabel, roundsInput, startButton);
        startButton.setOnAction(e -> handleStartGame());

        setupScene = new Scene(setupGrid, SCENE_WIDTH, SCENE_HEIGHT);
        setupScene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());

        configureEnterKeyNavigation();
        return setupScene;
    }

    private void configureEnterKeyNavigation() {
        rangeMinInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                rangeMaxInput.requestFocus();
            }
        });

        rangeMaxInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                attemptsInput.requestFocus();
            }
        });

        attemptsInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                roundsInput.requestFocus();
            }
        });

        roundsInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (isSetupInputValid()) {
                    handleStartGame();
                } else {
                    showAlert("Invalid Input", "Please enter valid values for all fields.");
                }
            }
        });
    }

    private boolean isSetupInputValid() {
        return !rangeMinInput.getText().isEmpty() && !rangeMaxInput.getText().isEmpty() &&
                !attemptsInput.getText().isEmpty() && !roundsInput.getText().isEmpty();
    }

    private void handleStartGame() {
        String minInput = rangeMinInput.getText();
        String maxInput = rangeMaxInput.getText();
        String attemptsInputText = attemptsInput.getText();
        String roundsInputText = roundsInput.getText();

        if (minInput.isEmpty() || maxInput.isEmpty() || attemptsInputText.isEmpty() || roundsInputText.isEmpty()) {
            showAlert("Invalid Input", "Please enter values for range, attempts, and rounds.");
            return;
        }

        try {
            minRange = Integer.parseInt(minInput);
            maxRange = Integer.parseInt(maxInput);
            attemptsLeft = Integer.parseInt(attemptsInputText);
            roundsLeft = Integer.parseInt(roundsInputText);

            if (minRange >= maxRange || attemptsLeft <= 0 || roundsLeft <= 0) {
                showAlert("Invalid Input", "Ensure that min < max, and attempts/rounds > 0.");
                return;
            }

            random = new Random();
            roundsWon = 0;

            startNewRound();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers.");
        }
    }

    private void startNewRound() {
        targetNumber = random.nextInt(maxRange - minRange + 1) + minRange;
        attemptsLeft = Integer.parseInt(attemptsInput.getText());

        VBox gameLayout = new VBox(10);
        gameLayout.setPadding(new Insets(20, 20, 20, 20));
        gameLayout.getStyleClass().add("vbox");

        Label guessLabel = new Label("Enter your guess:");
        guessLabel.getStyleClass().add("label");

        guessInput = new TextField();
        guessInput.getStyleClass().add("text-field");

        submitGuessButton = new Button("Submit");
        submitGuessButton.getStyleClass().add("button");

        feedbackLabel = new Label();
        feedbackLabel.getStyleClass().add("feedback-label");

        roundsWonLabel = new Label("Rounds won: " + roundsWon);
        roundsWonLabel.getStyleClass().add("rounds-won-label");

        submitGuessButton.setOnAction(e -> handleGuess());

        gameLayout.getChildren().addAll(guessLabel, guessInput, submitGuessButton, feedbackLabel, roundsWonLabel);
        gameScene = new Scene(gameLayout, SCENE_WIDTH, SCENE_HEIGHT);
        gameScene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());

        guessInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                submitGuessButton.fire();
            }
        });

        window.setScene(gameScene);
    }

    private void handleGuess() {
        String guessText = guessInput.getText();
        if (guessText.isEmpty()) {
            showAlert("Invalid Input", "Please enter your guess.");
            return;
        }

        try {
            int guess = Integer.parseInt(guessText);
            attemptsLeft--;

            if (guess == targetNumber) {
                roundsWon++;
                feedbackLabel.setText("Correct! The number was " + targetNumber + ". Rounds won: " + roundsWon);
                roundsWonLabel.setText("Rounds won: " + roundsWon);
                roundsLeft--;
                if (roundsLeft > 0) {
                    startNewRound();
                } else {
                    displayScore();
                }
            } else {
                giveFeedbackOnGuess(guess);
            }

            if (attemptsLeft <= 0 && guess != targetNumber) {
                feedbackLabel.setText("You've run out of attempts! The number was " + targetNumber + ". Rounds won: " + roundsWon);
                roundsLeft--;
                if (roundsLeft > 0) {
                    startNewRound();
                } else {
                    displayScore();
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number.");
        }
    }

    private void giveFeedbackOnGuess(int guess) {
        if (guess < targetNumber) {
            feedbackLabel.setText(guess < targetNumber - (maxRange - minRange) / 2 ? "Too low! Try again." : "Low! Try again.");
        } else {
            feedbackLabel.setText(guess > targetNumber + (maxRange - minRange) / 2 ? "Too high! Try again." : "High! Try again.");
        }
    }

    private void displayScore() {
        Alert scoreAlert = new Alert(Alert.AlertType.INFORMATION);
        scoreAlert.setTitle("Game Over");
        scoreAlert.setHeaderText(null);
        scoreAlert.setContentText("Game Over! You won " + roundsWon + " out of " + roundsInput.getText() + " rounds.");
        scoreAlert.showAndWait();

        window.setScene(setupScene);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
