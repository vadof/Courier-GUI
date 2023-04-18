package com.example.gui.admin;

import com.example.gui.City;
import com.example.gui.HelloApplication;
import com.example.gui.courier.Coordinates;
import com.example.gui.database.AdminDataBaseHandler;
import com.example.gui.database.CourierDataBaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;

public class AdminMap {

    @FXML
    private Button backBtn;

    @FXML
    private Pane map;

    private final int mapScaleX = 10;
    private final int mapScaleY = 5;

    @FXML
    void initialize() {
        backBtn.setOnAction(actionEvent -> HelloApplication.changeScreen(backBtn, "admin-city-menu.fxml"));
        showCities();
        showCouriers();
    }

    private void showCities() {
        List<City> cities = AdminDataBaseHandler.INSTANCE.getCountryCities(AdminAvailableCountries.clickedCountry);
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

    private void showCouriers() {
        List<String> couriers = AdminDataBaseHandler.INSTANCE.getCouriersInCountry(AdminAvailableCountries.clickedCountry);

        for (String courier : couriers) {
            Coordinates coordinates = CourierDataBaseHandler.INSTANCE.getCourierCoordinates(courier);

            Circle courierCircle = new Circle(10, Color.RED);
            courierCircle.setCenterX(coordinates.coordinateX() * mapScaleX);
            courierCircle.setCenterY(coordinates.coordinateY() * mapScaleY);
            map.getChildren().add(courierCircle);

            Text courierText = new Text(courier);
            courierText.setFill(Color.RED);
            courierText.setFont(new Font(20));
            courierText.setX(coordinates.coordinateX() * mapScaleX + 15);
            courierText.setY(coordinates.coordinateY() * mapScaleY + 9);
            map.getChildren().add(courierText);
        }
    }
}
