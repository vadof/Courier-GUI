package com.example.gui.courier;

import java.time.LocalDate;
import java.util.List;

import com.example.gui.HelloApplication;
import com.example.gui.database.AdminDataBaseHandler;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML
    private Button backButton;

    @FXML
    private ChoiceBox<String> signUpCity;

    @FXML
    private Label error;

    @FXML
    private DatePicker signUpDate;

    @FXML
    private RadioButton signUpFemale;

    @FXML
    private TextField signUpFirstName;

    @FXML
    private TextField signUpLastName;

    @FXML
    private RadioButton signUpMale;

    @FXML
    private PasswordField signUpPassword;

    @FXML
    private Button signUpRegisterBtn;

    @FXML
    private TextField signUpUsername;

    @FXML
    void initialize() {
        List<String> cities = AdminDataBaseHandler.INSTANCE.getAllCityNames();
        signUpCity.setItems(FXCollections.observableArrayList(cities));
        signUpCity.setValue("City");

        backButton.setOnAction(actionEvent -> HelloApplication.changeScreen(backButton, "hello-view.fxml"));

        setRegisterBtn();
        error.setVisible(false);
    }

    private void setRegisterBtn() {
        signUpRegisterBtn.setOnAction(actionEvent -> {
            String firstname = signUpFirstName.getText().trim();
            String lastname = signUpLastName.getText().trim();
            LocalDate dateOfBirth = signUpDate.getValue();
            String city = signUpCity.getValue();
            String username = signUpUsername.getText().trim();
            String password = signUpPassword.getText().trim();
            String gender = signUpFemale.isSelected() ? "Female" : "Male";

            if (firstname.length() > 0 && lastname.length() > 0 && dateOfBirth != null
                    && !city.equals("City") && username.length() > 0 && password.length() > 0) {

                if (!AdminDataBaseHandler.INSTANCE.usernameAlreadyExists(username)) {
                    AdminDataBaseHandler.INSTANCE.registerNewCourier(firstname, lastname, dateOfBirth,
                            city, username, password, gender);
                    HelloApplication.username = username;
                    HelloApplication.changeScreen(signUpRegisterBtn, "courier-menu.fxml");
                } else {
                    error.setVisible(true);
                }
            }
        });

    }

}
