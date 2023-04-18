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

public class AdminAvailableCountries {

    private List<Button> countryButtons;
    public static String clickedCountry = null;

    @FXML
    private Button createButton;

    @FXML
    private TextField createCountryName;

    @FXML
    private Label error;

    @FXML
    private Button findButton;

    @FXML
    private TextField findCountryName;

    @FXML
    private Button country1;

    @FXML
    private Button country2;

    @FXML
    private Button country3;

    @FXML
    private Button country4;

    @FXML
    private Button country5;

    @FXML
    private Button country6;

    @FXML
    private Button country7;

    @FXML
    private Button country8;

    @FXML
    void initialize() {
        countryButtons = new ArrayList<>(List.of(country1, country2, country3, country4, country5, country6, country7, country8));
        error.setVisible(false);

        hideWorldButtons();
        displayWorlds();

        setFindButton();
        setCreateButton();
    }

    private void setCreateButton() {
        createButton.setOnAction(actionEvent -> {
            String worldName = createCountryName.getText().trim();

            if (worldName.length() > 0) {

                if (AdminDataBaseHandler.INSTANCE.addWorld(worldName)) {
                    displayWorlds();
                } else {
                    error.setVisible(true);
                }
            }
        });
    }

    private void setFindButton() {
        findButton.setOnAction(actionEvent -> {
            String worldName = findCountryName.getText().trim();

            if (worldName.length() > 0) {
                hideWorldButtons();
                Optional<String> world = AdminDataBaseHandler.INSTANCE.getCountryByName(worldName);

                world.ifPresent(this::displayWorld1);
            }
        });
    }

    private void displayWorld1(String name) {
        country1.setText(name);
        country1.setVisible(true);
        country1.setOnAction(actionEvent -> worldEvent(country1, "admin-country-menu.fxml"));
    }

    private void hideWorldButtons() {
        for (Button button : countryButtons) {
            button.setVisible(false);
        }
    }

    private void displayWorlds() {
        List<String> worlds = AdminDataBaseHandler.INSTANCE.getCountries();
        for (int i = 0; i < worlds.size(); i++) {
            Button button = countryButtons.get(i);
            button.setText(worlds.get(i));
            button.setVisible(true);
            button.setOnAction(actionEvent -> worldEvent(button, "admin-country-menu.fxml"));
        }
    }

    public void worldEvent(Button button, String path) {
        clickedCountry = button.getText();
        HelloApplication.changeScreen(button, path);
    }
}
