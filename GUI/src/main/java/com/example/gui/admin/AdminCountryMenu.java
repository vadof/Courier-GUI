package com.example.gui.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.gui.HelloApplication;
import com.example.gui.database.AdminDataBaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AdminCountryMenu {

    @FXML
    private TextField coordinateXText;

    @FXML
    private TextField coordinateYText;

    @FXML
    private Button courier1;

    @FXML
    private Button courier2;

    @FXML
    private Button courier3;

    @FXML
    private Button courier4;

    @FXML
    private Button courier5;

    @FXML
    private Button courier6;

    @FXML
    private Button courierCityButton;

    @FXML
    private TextField courierCityText;

    @FXML
    private Button courierUsernameButton;

    @FXML
    private TextField courierUsernameText;

    @FXML
    private Button createCityButton;

    @FXML
    private Button mapBtn;

    @FXML
    private Button city1;

    @FXML
    private Button city2;

    @FXML
    private Button city3;

    @FXML
    private Button city4;

    @FXML
    private Button city5;

    @FXML
    private Button city6;

    @FXML
    private Button cityNameButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField cityNameText;

    @FXML
    private TextField cityNameTextCreate;

    @FXML
    private Label countryMenuLabel;

    private String currentCountry;
    private List<Button> countryCityList;
    private List<Button> countryCourierList;

    public static String clickedCity = null;
    public static String clickedCourier = null;

    @FXML
    void initialize() {
        currentCountry = AdminAvailableCountries.clickedCountry;
        if (currentCountry != null) {
            countryMenuLabel.setText(currentCountry);
        }

        countryCityList = new ArrayList<>(List.of(city1, city2, city3, city4, city5, city6));
        countryCourierList = new ArrayList<>(List.of(courier1, courier2, courier3, courier4, courier5, courier6));

        displayAllCities();
        displayAllCouriers();

        cityNameButton.setOnAction(actionEvent -> findCityByNameEvent());
        createCityButton.setOnAction(actionEvent -> createCityEvent());

        courierCityButton.setOnAction(actionEvent -> findCouriersByLocation());
        courierUsernameButton.setOnAction(actionEvent -> findCourierByUsername());

        backButton.setOnAction(actionEvent -> HelloApplication.changeScreen(backButton, "admin-menu.fxml"));
        mapBtn.setOnAction(actionEvent -> HelloApplication.changeScreen(mapBtn, "admin-map.fxml"));
    }

    private void hideCouriers() {
        for (Button button : countryCourierList) {
            button.setVisible(false);
        }
    }

    private void hideCities() {
        for (Button button : countryCityList) {
            button.setVisible(false);
        }
    }

    private void displayAllCities() {
        hideCities();
        List<String> cities = AdminDataBaseHandler.INSTANCE.getCitiesInCountry(currentCountry);
        for (int i = 0; i < cities.size(); i++) {
            Button button = countryCityList.get(i);
            String location = cities.get(i);

            button.setText(location);
            button.setVisible(true);
            button.setOnAction(actionEvent -> cityEvent(button));
        }
    }

    private void displayAllCouriers() {
        hideCouriers();
        List<String> couriers = AdminDataBaseHandler.INSTANCE.getCouriersInCountry(currentCountry);
        for (int i = 0; i < couriers.size(); i++) {
            if (i == 5) break;
            Button button = countryCourierList.get(i);
            String username = couriers.get(i);

            button.setText(username);
            button.setOnAction(actionEvent -> courierEvent(button));
            button.setVisible(true);
        }
    }

    private void findCityByNameEvent() {
        String name = cityNameText.getText().trim();
        if (name.length() > 0) {
            cityNameText.clear();
            hideCities();
            Optional<String> location = AdminDataBaseHandler.INSTANCE.getCityByName(name, currentCountry);
            if (location.isPresent()) {
                city1.setText(location.get());
                city1.setVisible(true);
                city1.setOnAction(actionEvent -> cityEvent(city1));
            }
        }
    }

    private void createCityEvent() {
        String cityName = cityNameTextCreate.getText().trim();
        String x = coordinateXText.getText().trim();
        String y = coordinateYText.getText().trim();

        if (cityName.length() > 0 && x.length() > 0 && y.length() > 0) {
            cityNameTextCreate.clear();
            coordinateXText.clear();
            coordinateYText.clear();
            AdminDataBaseHandler.INSTANCE.addCityToCountry(currentCountry, cityName, Float.parseFloat(x), Float.parseFloat(y));
            displayAllCities();
        }
    }

    private void findCouriersByLocation() {
        String location = courierCityText.getText().trim();
        if (location.length() > 0) {
            courierCityText.clear();
            hideCouriers();
            List<String> couriers = AdminDataBaseHandler.INSTANCE.getCouriersByCity(location, currentCountry);
            for (int i = 0; i < couriers.size(); i++) {
                Button button = countryCourierList.get(i);
                String username = couriers.get(i);

                button.setText(username);
                button.setOnAction(actionEvent -> courierEvent(button));
                button.setVisible(true);
            }
        }
    }

    private void findCourierByUsername() {
        String username = courierUsernameText.getText().trim();
        if (username.length() > 0) {
            courierUsernameText.clear();
            hideCouriers();
            Optional<String> courier = AdminDataBaseHandler.INSTANCE.getCourierByUsername(username, currentCountry);
            if (courier.isPresent()) {
                courier1.setText(courier.get());
                courier1.setOnAction(actionEvent -> courierEvent(courier1));
                courier1.setVisible(true);
            }
        }
    }

    private void cityEvent(Button button) {
        clickedCity = button.getText();
        HelloApplication.changeScreen(button, "admin-city-menu.fxml");
    }

    private void courierEvent(Button button) {
        clickedCourier = button.getText();
        HelloApplication.changeScreen(button, "admin-courier-menu.fxml");
    }

}
