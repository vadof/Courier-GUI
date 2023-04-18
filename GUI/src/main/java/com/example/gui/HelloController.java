package com.example.gui;

import com.example.gui.database.AdminDataBaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class HelloController {
    @FXML
    private Button loginBtn;

    @FXML
    private Button adminButton;

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField loginUsername;

    @FXML
    private Button signUp;

    @FXML
    void initialize() {
        errorLabel.setVisible(false);

        signUp.setOnAction(actionEvent -> HelloApplication.changeScreen(signUp, "register.fxml"));
        loginBtn.setOnAction(actionEvent -> loginEvent(false));
        adminButton.setOnAction(actionEvent -> loginEvent(true));
    }

    private void loginEvent(boolean admin) {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText().trim();

        if (username.length() > 0 && password.length() > 0) {
            HelloApplication.username = username;
            login(username, password, admin);
        } else {
            setErrorLabelVisible("Fields are empty!");
        }
    }

    private void login(String username, String password, boolean admin) {
        boolean loginSuccess = AdminDataBaseHandler.INSTANCE.login(username, password, admin);
        if (loginSuccess) {
            if (admin) {
                HelloApplication.changeScreen(adminButton, "admin-menu.fxml");
            } else {
                HelloApplication.changeScreen(loginBtn, "courier-menu.fxml");
            }
        } else {
            setErrorLabelVisible("Error!");
        }
    }

    private void setErrorLabelVisible(String text) {
        errorLabel.setText(text);
        errorLabel.setVisible(true);
    }
}
