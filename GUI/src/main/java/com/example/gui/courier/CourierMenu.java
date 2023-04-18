package com.example.gui.courier;

import java.util.ArrayList;
import java.util.List;

import com.example.gui.City;
import com.example.gui.HelloApplication;
import com.example.gui.database.CourierDataBaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CourierMenu {

    @FXML
    private Label destination;

    @FXML
    private Button drive;

    @FXML
    private Label infoLabel;

    @FXML
    private Pane map;

    @FXML
    private Button unload;

    private String username;
    private final int mapScaleX = 10;
    private final int mapScaleY = 5;

    private final Circle courierCircle = new Circle(10, Color.RED);
    private final Text courierText = new Text("You");

    @FXML
    void initialize() {
        username = HelloApplication.username;
        addCityToTheMap();
        addCourierToTheMap();
        setInfoPanel();

        drive.setOnAction(actionEvent -> driveEvent());
        unload.setOnAction(actionEvent -> unloadEvent());
    }

    private void setInfoPanel() {
        destination.setText(CourierDataBaseHandler.INSTANCE.getCourierDestination(username));
        infoLabel.setVisible(false);
    }

    private void addCourierToTheMap() {
        Coordinates coordinates = CourierDataBaseHandler.INSTANCE.getCourierCoordinates(username);

        courierCircle.setCenterX(coordinates.coordinateX() * mapScaleX);
        courierCircle.setCenterY(coordinates.coordinateY() * mapScaleY);
        map.getChildren().add(courierCircle);

        courierText.setFill(Color.RED);
        courierText.setFont(new Font(20));
        courierText.setX(coordinates.coordinateX() * mapScaleX + 15);
        courierText.setY(coordinates.coordinateY() * mapScaleY + 9);
        map.getChildren().add(courierText);
    }

    private void addCityToTheMap() {
        List<City> cities = CourierDataBaseHandler.INSTANCE.getAllCities();
        for (City city : cities) {
            Circle cityRect = new Circle(15, Color.GREEN);
            cityRect.setCenterX(city.coordinateX() * mapScaleX);
            cityRect.setCenterY(city.coordinateY() * mapScaleY);
            map.getChildren().add(cityRect);

            Text text = new Text(city.name());
            text.setFill(Color.WHITE);
            text.setFont(new Font(30));
            text.setX(city.coordinateX() * mapScaleX - 15);
            text.setY(city.coordinateY() * mapScaleY - 9);
            map.getChildren().add(text);
        }
    }

    private void updateCourierOnTheMap() {
        Coordinates coordinates = CourierDataBaseHandler.INSTANCE.getCourierCoordinates(username);

        courierCircle.setCenterX(coordinates.coordinateX() * mapScaleX);
        courierCircle.setCenterY(coordinates.coordinateY() * mapScaleY);

        courierText.setX(coordinates.coordinateX() * mapScaleX + 15);
        courierText.setY(coordinates.coordinateY() * mapScaleY + 9);
    }

    private void driveEvent() {
        if (destination.getText().equals("None")) {
            infoLabel.setText("You haven't been assigned a destination yet!");
            infoLabel.setVisible(true);
        } else if (courierArrived()) {
            infoLabel.setText("You have arrived at your destination!");
        } else {
            CourierDataBaseHandler.INSTANCE.updateCourierCoordinates(username, calculateNewCoordinates());
            updateCourierOnTheMap();
            infoLabel.setVisible(false);
        }
    }

    private Coordinates calculateNewCoordinates() {
        Coordinates destinationCoordinates = CourierDataBaseHandler.INSTANCE.getCountryCoordinates(destination.getText());
        Coordinates courierCoordinates = CourierDataBaseHandler.INSTANCE.getCourierCoordinates(username);

        float x1 = courierCoordinates.coordinateX(), y1 = courierCoordinates.coordinateY();
        float x2 = destinationCoordinates.coordinateX(), y2 = destinationCoordinates.coordinateY();

        float dx = x2 - x1;
        float dy = y2 - y1;

        float distance = (float) Math.sqrt(dx*dx + dy*dy);

        if (distance < 1) {
            return destinationCoordinates;
        }

        float stepSize = 1 / distance;

        List<Coordinates> coordinates = new ArrayList<>();
        float x, y;
        int count = 0;
        for (float t = 0; t <= 1; t += stepSize) {
            if (count == 3) break;
            x = x1 + t * dx;
            y = y1 + t * dy;
            coordinates.add(new Coordinates(x, y));
            count++;
        }

        return coordinates.get(coordinates.size() - 1);
    }

    private boolean courierArrived() {
        Coordinates destinationCoordinates = CourierDataBaseHandler.INSTANCE.getCountryCoordinates(destination.getText());
        Coordinates courierCoordinates = CourierDataBaseHandler.INSTANCE.getCourierCoordinates(username);

        if (destinationCoordinates == null || courierCoordinates == null) {
            return false;
        }

        return courierCoordinates.coordinateX() == destinationCoordinates.coordinateX()
                && courierCoordinates.coordinateY() == destinationCoordinates.coordinateY();
    }

    private void unloadEvent() {
        if (courierArrived()) {
            CourierDataBaseHandler.INSTANCE.unloadCourier(username);
            destination.setText(CourierDataBaseHandler.INSTANCE.getCourierDestination(username));
        }
    }
}
