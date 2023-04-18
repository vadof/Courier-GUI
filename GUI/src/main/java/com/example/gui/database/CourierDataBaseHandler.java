package com.example.gui.database;

import com.example.gui.City;
import com.example.gui.courier.Coordinates;
import com.example.gui.database.config.Config;
import com.example.gui.database.config.tables.CourierTable;
import com.example.gui.database.config.tables.CityTable;
import com.example.gui.database.config.tables.ParcelsTable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourierDataBaseHandler {

    public static CourierDataBaseHandler INSTANCE = new CourierDataBaseHandler();

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        return DriverManager.getConnection(Config.url, Config.dbUser, Config.dbPass);
    }

    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM " + CityTable.tableName;

            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                cities.add(new City(resultSet.getString(CityTable.name),
                        resultSet.getFloat(CityTable.coordinateX),
                        resultSet.getFloat(CityTable.coordinateY)));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return cities;
    }

    public Coordinates getCourierCoordinates(String username) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM " + CourierTable.tableName + " WHERE " + CourierTable.username + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Coordinates(resultSet.getFloat(CourierTable.currentX),
                        resultSet.getFloat(CourierTable.currentY));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new Coordinates(0, 0);
    }

    public String getCourierDestination(String username) {
        try (Connection connection = getConnection()) {
            String query = "SELECT " + CourierTable.destination + " FROM " + CourierTable.tableName +
                    " WHERE " + CourierTable.username + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String destination = resultSet.getString(CourierTable.destination);
                return destination == null ? "None" : destination;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "None";
    }

    public Coordinates getCountryCoordinates(String country) {
        try (Connection connection = getConnection()) {
            String query = "SELECT " + CityTable.coordinateX + ", " + CityTable.coordinateY + " FROM " +
                    CityTable.tableName + " WHERE " + CityTable.name + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, country);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Coordinates(resultSet.getFloat(CityTable.coordinateX),
                        resultSet.getFloat(CityTable.coordinateY));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void updateCourierCoordinates(String username, Coordinates coordinates) {
        try (Connection connection = getConnection()) {
            String query = "UPDATE " + CourierTable.tableName
                    + " SET " + CourierTable.currentX + " = ?, " + CourierTable.currentY + " = ?" +
                    " WHERE " + CourierTable.username + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setFloat(1, coordinates.coordinateX());
            preparedStatement.setFloat(2, coordinates.coordinateY());
            preparedStatement.setString(3, username);

            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void unloadCourier(String username) {
        setNullDestinationToCourier(username);
        setPacketStatusToDelivered(username);
    }

    private void setNullDestinationToCourier(String username) {
        try (Connection connection = getConnection()) {
            String query = "UPDATE " + CourierTable.tableName + " SET " + CourierTable.destination + " = NULL" +
                    " WHERE " + CourierTable.username + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setPacketStatusToDelivered(String username) {
        try (Connection connection = getConnection()) {
            String query = "UPDATE " + ParcelsTable.tableName + " SET " + ParcelsTable.delivered + " = true, " +
                    ParcelsTable.courier + " = NULL" +
                    " WHERE " + ParcelsTable.courier + " = (SELECT " + CourierTable.id + " FROM " + CourierTable.tableName +
                    " WHERE " + CourierTable.username + " = ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
