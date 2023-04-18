package com.example.gui.admin;

import com.example.gui.courier.Courier;
import com.example.gui.HelloApplication;
import com.example.gui.database.AdminDataBaseHandler;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.util.*;

public class AdminCourierInfo {

    @FXML
    private Label age;

    @FXML
    private Button backButton;

    @FXML
    private Label currentCity;

    @FXML
    private Label firstname;

    @FXML
    private Label gender;

    @FXML
    private Label lastname;

    @FXML
    private Label courierCity;

    @FXML
    private Label parcels;

    @FXML
    private Button sendButton;

    @FXML
    private ChoiceBox<String> sendCity;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label weight;

    @FXML
    void initialize() {
        backButton.setOnAction(actionEvent -> HelloApplication.changeScreen(backButton, "admin-country-menu.fxml"));
        usernameLabel.setText(AdminCountryMenu.clickedCourier);

        fillCourierInfo();
        currentCity.setText(AdminDataBaseHandler.INSTANCE.getCourierCurrentCity(usernameLabel.getText()));
        parcels.setText(AdminDataBaseHandler.INSTANCE.getNumberOfParcels(usernameLabel.getText()) + "");
        weight.setText(AdminDataBaseHandler.INSTANCE.getWeightOfAllParcelsAtCourier(usernameLabel.getText()) + " kg");

        fillCityChoices();
        sendButton.setOnAction(actionEvent -> sendCourierEvent());
    }

    private void fillCityChoices() {
        if (currentCity.getText().startsWith("On the way") || currentCity.getText().startsWith("Arrived")) {
            sendCity.setVisible(false);
            sendButton.setVisible(false);
        } else {
            sendCity.setValue("Choose city");
            List<String> cities = AdminDataBaseHandler.INSTANCE.getCourierParcelDestinations(usernameLabel.getText());

            if (cities.size() > 0) {
                Map<String, Integer> countMap = new HashMap<>();
                for (String item : cities) {
                    countMap.put(item, countMap.getOrDefault(item, 0) + 1);
                }

                cities.sort((o1, o2) -> countMap.get(o2) - countMap.get(o1));

                Set<String> citySet = new LinkedHashSet<>(cities);

                sendCity.setItems(FXCollections.observableList(citySet.stream().toList()));
            }
        }
    }

    private void fillCourierInfo() {
        Courier courier = AdminDataBaseHandler.INSTANCE.getCourierInfo(usernameLabel.getText());
        if (courier != null) {
            firstname.setText(courier.getFirstname());
            lastname.setText(courier.getLastname());
            age.setText(courier.getAge() + " y.o.");
            courierCity.setText(courier.getCity());
            gender.setText(courier.getGender());
        } else {
            firstname.setText("Error");
            lastname.setText("Error");
            age.setText("Error");
            courierCity.setText("Error");
            gender.setText("Error");
        }
    }

    private void sendCourierEvent() {
        String city = sendCity.getValue();
        if (!city.equals("Choose city")) {
            AdminDataBaseHandler.INSTANCE.sendCourier(usernameLabel.getText(), city);
            HelloApplication.changeScreen(sendButton, "admin-country-menu.fxml");
            sendCity.setVisible(false);
            sendButton.setVisible(false);
        }
    }

}
