package com.example.gui.admin;

import java.util.*;

import com.example.gui.HelloApplication;
import com.example.gui.Parcel;
import com.example.gui.database.AdminDataBaseHandler;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminCityInfo {

    @FXML
    private Button addButton;

    @FXML
    private ChoiceBox<String> addToSelection;

    @FXML
    private TextField addWeightText;

    @FXML
    private ChoiceBox<String> availableCouriers;

    @FXML
    private Button backButton;

    @FXML
    private Button findParcelButton;

    @FXML
    private TextField findParcelText;

    @FXML
    private Button issuedParcelButton1;

    @FXML
    private Button issuedParcelButton10;

    @FXML
    private Button issuedParcelButton2;

    @FXML
    private Button issuedParcelButton3;

    @FXML
    private Button issuedParcelButton4;

    @FXML
    private Button issuedParcelButton5;

    @FXML
    private Button issuedParcelButton6;

    @FXML
    private Button issuedParcelButton7;

    @FXML
    private Button issuedParcelButton8;

    @FXML
    private Button issuedParcelButton9;

    @FXML
    private Label issuedParcelName1;

    @FXML
    private Label issuedParcelName10;

    @FXML
    private Label issuedParcelName2;

    @FXML
    private Label issuedParcelName3;

    @FXML
    private Label issuedParcelName4;

    @FXML
    private Label issuedParcelName5;

    @FXML
    private Label issuedParcelName6;

    @FXML
    private Label issuedParcelName7;

    @FXML
    private Label issuedParcelName8;

    @FXML
    private Label issuedParcelName9;

    @FXML
    private Label issuedTotal;

    @FXML
    private ToggleGroup parcelSelect;

    @FXML
    private RadioButton radio1;

    @FXML
    private RadioButton radio2;

    @FXML
    private RadioButton radio3;

    @FXML
    private RadioButton radio4;

    @FXML
    private RadioButton radio5;

    @FXML
    private Button sendParcel;

    @FXML
    private Label sentLocation1;

    @FXML
    private Label sentLocation2;

    @FXML
    private Label sentLocation3;

    @FXML
    private Label sentLocation4;

    @FXML
    private Label sentLocation5;

    @FXML
    private Label sentName1;

    @FXML
    private Label sentName2;

    @FXML
    private Label sentName3;

    @FXML
    private Label sentName4;

    @FXML
    private Label sentName5;

    @FXML
    private Label sentTotal;

    @FXML
    private Label sentWeight1;

    @FXML
    private Label sentWeight2;

    @FXML
    private Label sentWeight3;

    @FXML
    private Label sentWeight4;

    @FXML
    private Label sentWeight5;

    @FXML
    private Label cityMenuLabel;

    private List<RadioButton> radioButtons;

    private List<Label> sentNameLabels;
    private List<Label> sentToLabels;
    private List<Label> sentWeightsLabels;

    private List<Label> issuedParcelLabels;
    private List<Button> issuedParcelButtons;

    @FXML
    void initialize() {
        cityMenuLabel.setText(AdminCountryMenu.clickedCity);

        radioButtons = new ArrayList<>(List.of(radio1, radio2, radio3, radio4, radio5));

        sentNameLabels = new ArrayList<>(List.of(sentName1, sentName2, sentName3, sentName4, sentName5));
        sentToLabels = new ArrayList<>(List.of(sentLocation1, sentLocation2, sentLocation3, sentLocation4, sentLocation5));
        sentWeightsLabels = new ArrayList<>(List.of(sentWeight1, sentWeight2, sentWeight3, sentWeight4, sentWeight5));

        issuedParcelLabels = new ArrayList<>(List.of(issuedParcelName1, issuedParcelName2, issuedParcelName3, issuedParcelName4, issuedParcelName5, issuedParcelName6, issuedParcelName7, issuedParcelName8, issuedParcelName9, issuedParcelName10));
        issuedParcelButtons = new ArrayList<>(List.of(issuedParcelButton1, issuedParcelButton2, issuedParcelButton3, issuedParcelButton4, issuedParcelButton5, issuedParcelButton6, issuedParcelButton7, issuedParcelButton8, issuedParcelButton9, issuedParcelButton10));

        backButton.setOnAction(actionEvent -> HelloApplication.changeScreen(backButton, "admin-country-menu.fxml"));
        addButton.setOnAction(actionEvent -> addParcelEvent());
        findParcelButton.setOnAction(actionEvent -> findParcelEvent());

        availableCouriers.setValue("Choose courier");
        availableCouriers.setItems(FXCollections.observableList(AdminDataBaseHandler.INSTANCE.getCouriersAtCurrentCity(AdminCountryMenu.clickedCity)));

        sendParcel.setOnAction(actionEvent -> sendButtonEvent());

        fillCityChoices();

        showSentBlock();
        showIssuedBlock();
    }

    private void hideIssuedBlock() {
        for (int i = 0; i < issuedParcelLabels.size(); i++) {
            issuedParcelLabels.get(i).setVisible(false);
            issuedParcelButtons.get(i).setVisible(false);
        }
    }

    private void hideSentBlock() {
        for (int i = 0; i < sentNameLabels.size(); i++) {
            radioButtons.get(i).setVisible(false);
            sentNameLabels.get(i).setVisible(false);
            sentToLabels.get(i).setVisible(false);
            sentWeightsLabels.get(i).setVisible(false);
        }
        setSendVisible(false);
    }

    private void showSentBlock() {
        hideSentBlock();
        List<Parcel> parcels = AdminDataBaseHandler.INSTANCE.getParcelsToBeSentInCity(AdminCountryMenu.clickedCity);
        sentTotal.setText("Total: " + parcels.size());

        for (int i = 0; i < parcels.size(); i++) {
            if (i == 5) break;

            sentNameLabels.get(i).setText(parcels.get(i).name());
            sentNameLabels.get(i).setVisible(true);

            sentToLabels.get(i).setText(parcels.get(i).to());
            sentToLabels.get(i).setVisible(true);

            sentWeightsLabels.get(i).setText(parcels.get(i).weight() + "kg");
            sentWeightsLabels.get(i).setVisible(true);

            radioButtons.get(i).setVisible(true);
        }

        if (parcels.size() > 0) setSendVisible(true);
    }

    private void showIssuedBlock() {
        hideIssuedBlock();
        List<String> parcelNames = AdminDataBaseHandler.INSTANCE.getIssuedParcelsInCity(AdminCountryMenu.clickedCity);
        issuedTotal.setText("Total: " + parcelNames.size());

        for (int i = 0; i < parcelNames.size(); i++) {
            if (i == 9) break;

            issuedParcelLabels.get(i).setText(parcelNames.get(i));
            issuedParcelLabels.get(i).setVisible(true);

            Button button = issuedParcelButtons.get(i);
            button.setVisible(true);
            button.setOnAction(actionEvent ->  issueAParcelEvent(button));
        }
    }

    private void fillCityChoices() {
        addToSelection.setValue("Choose city");
        addToSelection.setItems(FXCollections.observableList(AdminDataBaseHandler.INSTANCE.getAllCityNames()));
    }

    private void addParcelEvent() {
        String weight = addWeightText.getText().trim();
        String to = addToSelection.getValue();

        if (weight.length() > 0 && !to.equals("Choose city")) {
            float weightF;
            try {
                weightF = Float.parseFloat(weight);
                String parcelName = generateRandomParcelName();

                boolean added = AdminDataBaseHandler.INSTANCE.addNewParcel(new Parcel(0, parcelName, AdminCountryMenu.clickedCity, to, weightF));
                if (added) {
                    showSentBlock();
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private String generateRandomParcelName() {
        Random rand = new Random();
        String prefix = "CE";
        StringBuilder sb = new StringBuilder(prefix);

        for (int i = 0; i < 13; i++) {
            int digit = rand.nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }

    private void findParcelEvent() {
        String code = findParcelText.getText().trim();
        if (code.length() > 0) {
            Optional<String> foundParcelCode = AdminDataBaseHandler.INSTANCE.getParcelByCode(AdminCountryMenu.clickedCity, code);
            if (foundParcelCode.isPresent()) {
                hideIssuedBlock();
                issuedTotal.setText("Total: " + 1);

                issuedParcelButton1.setVisible(true);
                issuedParcelButton1.setOnAction(actionEvent -> issueAParcelEvent(issuedParcelButton1));

                issuedParcelName1.setText(foundParcelCode.get());
                issuedParcelName1.setVisible(true);
            }
        }
    }

    private void issueAParcelEvent(Button button) {
        int id = issuedParcelButtons.indexOf(button);
        AdminDataBaseHandler.INSTANCE.removeParcel(issuedParcelLabels.get(id).getText());
        showIssuedBlock();
    }

    private void sendButtonEvent() {
        String courier = availableCouriers.getValue();
        if (!courier.equals("Choose courier")) {
            int buttonId = Integer.parseInt(((RadioButton) parcelSelect.getSelectedToggle()).getText());
            AdminDataBaseHandler.INSTANCE.attachParcelToCourier(sentNameLabels.get(buttonId).getText(), courier);
            showSentBlock();
            radio1.setSelected(true);
        }
    }

    private void setSendVisible(boolean b) {
        availableCouriers.setVisible(b);
        sendParcel.setVisible(b);
    }
}
