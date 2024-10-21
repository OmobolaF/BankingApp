package org.openjfx.bankingAppfx;

import java.io.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LogInDetails extends Application {

    private Label messageLabel;
    private String loggedInUsername;
    private double accountBalance = 0.0;  // Default balance

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        switchToCreateBankAccount(primaryStage);  // Start with account creation screen
    }

    private void switchToCreateBankAccount(Stage stage) {
        // Create a new scene for account creation
        Group createBankAccountRoot = new Group();
        Scene createBankAccountScene = new Scene(createBankAccountRoot, Color.LIGHTYELLOW);

        // Create account form components
        Label newUsernameLabel = new Label("New Username:");
        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("Enter new username");

        Label newPasswordLabel = new Label("New Password:");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");

        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");

        // Message to instruct the user on the correct account parameters
        Label instructionsLabel = new Label("Please use 'reindeer' as the username and 'Pepper4567' as the password.");

        // Create account submit button
        Button submitCreateButton = new Button("Submit");

        // Message label for account creation status
        Label createBankAccountMessage = new Label();

        // Action on submit button click
        submitCreateButton.setOnAction(e -> {
            String newUsername = newUsernameField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (newUsername.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                createBankAccountMessage.setText("All fields must be filled.");
            } else if (!newPassword.equals(confirmPassword)) {
                createBankAccountMessage.setText("Passwords do not match.");
            } else if (!newUsername.equals("reindeer") || !newPassword.equals("Pepper4567")) {
                createBankAccountMessage.setText("Incorrect username or password. Please use 'reindeer' as the username and 'Pepper4567' as the password.");
            } else {
                // Account creation success logic (store in a file with initial balance 0.0)
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("accounts.txt", true));
                    writer.write(newUsername + "," + newPassword + ",0.0");  // Store balance as 0.0 initially
                    writer.newLine();
                    writer.close();
                    createBankAccountMessage.setText("Account created successfully!");

                    // Switch to login after account creation
                    switchToLogin(stage);
                } catch (IOException ex) {
                    createBankAccountMessage.setText("Error saving account.");
                    ex.printStackTrace();
                }
            }
        });

        // Arrange the components for account creation
        HBox newUsernameBox = new HBox(10, newUsernameLabel, newUsernameField);
        HBox newPasswordBox = new HBox(10, newPasswordLabel, newPasswordField);
        HBox confirmPasswordBox = new HBox(10, confirmPasswordLabel, confirmPasswordField);

        // Arrange all components vertically for account creation layout
        VBox createAccountLayout = new VBox(15, instructionsLabel, newUsernameBox, newPasswordBox, confirmPasswordBox, submitCreateButton, createBankAccountMessage);
        createBankAccountRoot.getChildren().add(createAccountLayout);

        // Switch to the account creation scene
        stage.setScene(createBankAccountScene);
        stage.show();
    }

    private void switchToLogin(Stage stage) {
        // Create login screen
        Group root = new Group();
        Scene scene = new Scene(root, Color.BEIGE);

        // Create username and password fields and labels
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        // Create a login button
        Button loginButton = new Button("Log In");

        // Create a label to display messages (for validation or login success)
        messageLabel = new Label();

        // Action on login button click
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Validate against accounts.txt
            if (validateLogin(username, password)) {
                loggedInUsername = username;
                messageLabel.setText("Login successful! Welcome to the bank.");
                switchToAccountOptions(stage);
            } else {
                messageLabel.setText("Incorrect username or password. Please try again.");
            }
        });

        // Arrange username label and field horizontally
        HBox usernameBox = new HBox(10);  // 10px spacing between label and field
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Arrange password label and field horizontally
        HBox passwordBox = new HBox(10);  // 10px spacing between label and field
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Arrange all components in a vertical layout
        VBox layout = new VBox(15);  // 15px spacing between components
        layout.getChildren().addAll(usernameBox, passwordBox, loginButton, messageLabel);

        // Add the layout to the root
        root.getChildren().add(layout);

        stage.setTitle("A Bank For Your Convenience");
        stage.setWidth(500);
        stage.setHeight(500);

        stage.setScene(scene);
        stage.show();
    }

    private boolean validateLogin(String username, String password) {
        // Check accounts.txt for matching username and password
        try (BufferedReader reader = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] account = line.split(",");
                if (account.length == 3) {
                    String storedUsername = account[0].trim();
                    String storedPassword = account[1].trim();
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        accountBalance = Double.parseDouble(account[2]);  // Get stored balance
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void switchToAccountOptions(Stage stage) {
        // Create the account options screen
        Group accountOptionsRoot = new Group();
        Scene accountOptionsScene = new Scene(accountOptionsRoot, Color.LIGHTGRAY);

        // Create buttons for account options
        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        Button checkBalanceButton = new Button("Check Balance");

        // Create a label to display account balance
        Label accountMessage = new Label();

        // Deposit action
        depositButton.setOnAction(e -> switchToDeposit(stage, accountMessage));

        // Withdraw action
        withdrawButton.setOnAction(e -> switchToWithdraw(stage, accountMessage));

        // Check balance action
        checkBalanceButton.setOnAction(e -> accountMessage.setText("Your current balance is: $" + accountBalance));

        // Arrange the buttons and message label
        VBox layout = new VBox(15, depositButton, withdrawButton, checkBalanceButton, accountMessage);

        accountOptionsRoot.getChildren().add(layout);

        // Switch to account options scene
        stage.setScene(accountOptionsScene);
        stage.show();
    }

    private void switchToDeposit(Stage stage, Label accountMessage) {
        // Deposit screen
        Group depositRoot = new Group();
        Scene depositScene = new Scene(depositRoot, Color.LIGHTGREEN);

        Label depositLabel = new Label("Enter amount to deposit:");
        TextField depositAmountField = new TextField();
        Button submitDepositButton = new Button("Submit");

        submitDepositButton.setOnAction(e -> {
            double amount = Double.parseDouble(depositAmountField.getText());
            accountBalance += amount;
            updateAccountBalance();
            accountMessage.setText("Successfully deposited $" + amount + ". New balance: $" + accountBalance);
            switchToAccountOptions(stage);  // Go back to account options
        });

        VBox layout = new VBox(15, depositLabel, depositAmountField, submitDepositButton);
        depositRoot.getChildren().add(layout);

        stage.setScene(depositScene);
        stage.show();
    }

    private void switchToWithdraw(Stage stage, Label accountMessage) {
        // Withdraw screen
        Group withdrawRoot = new Group();
        Scene withdrawScene = new Scene(withdrawRoot, Color.LIGHTPINK);

        Label withdrawLabel = new Label("Enter amount to withdraw:");
        TextField withdrawAmountField = new TextField();
        Button submitWithdrawButton = new Button("Submit");

        submitWithdrawButton.setOnAction(e -> {
            double amount = Double.parseDouble(withdrawAmountField.getText());
            if (amount <= accountBalance) {
                accountBalance -= amount;
                updateAccountBalance();
                accountMessage.setText("Successfully withdrew $" + amount + ". New balance: $" + accountBalance);
            } else {
                accountMessage.setText("Insufficient funds.");
            }
            switchToAccountOptions(stage);  // Go back to account options
        });

        VBox layout = new VBox(15, withdrawLabel, withdrawAmountField, submitWithdrawButton);
        withdrawRoot.getChildren().add(layout);

        stage.setScene(withdrawScene);
        stage.show();
    }

    private void updateAccountBalance() {
        // Update the account balance in accounts.txt
        try {
            BufferedReader reader = new BufferedReader(new FileReader("accounts.txt"));
            StringBuilder updatedContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] account = line.split(",");
                if (account[0].equals(loggedInUsername)) {
                    // Update the balance for the logged-in user
                    line = loggedInUsername + "," + account[1] + "," + accountBalance;
                }
                updatedContent.append(line).append("\n");
            }
            reader.close();

            // Write updated content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter("accounts.txt"));
            writer.write(updatedContent.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}