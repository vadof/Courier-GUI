package com.example.gui.database;

import com.example.gui.City;
import com.example.gui.courier.Courier;
import com.example.gui.Parcel;
import com.example.gui.database.config.Config;
import com.example.gui.database.config.tables.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminDataBaseHandler {

    public static AdminDataBaseHandler INSTANCE = new AdminDataBaseHandler();

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        return DriverManager.getConnection(Config.url, Config.dbUser, Config.dbPass);
    }

    public boolean usernameAlreadyExists(String username) {
        try (Connection connection = getConnection()) {
            String query = "SELECT username FROM " + CourierTable.tableName +
                    " WHERE " + CourierTable.username + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerNewCourier(String firstname, String lastname, LocalDate dateOfBirth,
                                   String city, String username, String password, String gender) {

        try (Connection connection = getConnection()) {
            String coordinateX = "(SELECT " + CityTable.coordinateX + " FROM " + CityTable.tableName + " WHERE " +
                    CityTable.name + " = '" + city + "')";

            String coordinateY = "(SELECT " + CityTable.coordinateY + " FROM " + CityTable.tableName + " WHERE " +
                    CityTable.name + " = '" + city + "')";

            String query = "INSERT INTO " + CourierTable.tableName + " (" +
                    CourierTable.firstname + ", " + CourierTable.lastname + ", " +
                    CourierTable.dateOfBirth + ", " + CourierTable.city + ", " +
                    CourierTable.username + ", " + CourierTable.password + ", " +
                    CourierTable.gender + ", " + CourierTable.currentX + ", " +
                    CourierTable.currentY + ") VALUES (?,?,?,?,?,?,?," + coordinateX + "," + coordinateY + ")";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, firstname);
            preparedStatement.setString(2, lastname);
            preparedStatement.setDate(3, Date.valueOf(dateOfBirth));
            preparedStatement.setString(4, city);
            preparedStatement.setString(5, username);
            preparedStatement.setString(6, password);
            preparedStatement.setString(7, gender);

            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error");
        }

    }

    public boolean login(String username, String password, boolean admin) {
        try (Connection connection = getConnection()){
            String query = "SELECT * FROM " + CourierTable.tableName + " WHERE " +
                    CourierTable.username + " = ? AND " + CourierTable.password + " = ?";

            if (admin) {
                query = "SELECT * FROM " + AdminTable.tableName + " WHERE " +
                        AdminTable.username + " = ? AND " + AdminTable.password + " = ?";
            }

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getCountries() {
        try (Connection connection = getConnection()){
            String query = "SELECT " + CountryTable.name + " FROM " + CountryTable.tableName + " LIMIT 8";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            List<String> countries = new ArrayList<>();
            while (resultSet.next()) {
                countries.add(resultSet.getString(1));
            }

            return countries;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<String> getCountryByName(String name) {
        try (Connection connection = getConnection()){
            String query = "SELECT " + CountryTable.name + " FROM " + CountryTable.tableName +
                    " WHERE " + CountryTable.name + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(resultSet.getString(1));
            }

            return Optional.empty();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addWorld(String name) {
        try (Connection connection = getConnection()){
            String query = "INSERT INTO " + CountryTable.tableName + " (" + CountryTable.name + ") VALUES (?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);

            preparedStatement.execute();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllCityNames() {
        List<String> cityNames = new ArrayList<>();
        try (Connection connection = getConnection()){

            String query = "SELECT " + CityTable.name + " FROM " + CityTable.tableName;

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                cityNames.add(resultSet.getString(CityTable.name));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return cityNames;
    }

    public List<String> getCitiesInCountry(String countryName) {
        List<String> locations = new ArrayList<>();

        try (Connection connection = getConnection()){

            String query = "SELECT " + CityTable.name + " FROM " + CityTable.tableName +
                    " WHERE " + CityTable.country + " IN (SELECT " + CountryTable.id + " FROM " +
                    CountryTable.tableName + " WHERE " + CountryTable.name + " = '" + countryName + "') LIMIT 6";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                locations.add(resultSet.getString(CityTable.name));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return locations;
    }

    public void addCityToCountry(String countryName, String cityName, float coordinateX, float coordinateY) {
        try (Connection connection = getConnection()){
            String query = "INSERT INTO " + CityTable.tableName
                    + " (" + CityTable.name + ", " + CityTable.coordinateX + ", " + CityTable.coordinateY
                    + ", " + CityTable.country + ") VALUES (?,?,?," + "(SELECT " + CountryTable.id + " FROM "
                    + CountryTable.tableName + " WHERE " + CountryTable.name + " = ?))";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, cityName);
                preparedStatement.setFloat(2, coordinateX);
                preparedStatement.setFloat(3, coordinateY);
                preparedStatement.setString(4, countryName);

                preparedStatement.execute();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
    }

    public Optional<String> getCityByName(String name, String country) {
        try (Connection connection = getConnection()) {
            String query = "SELECT " + CityTable.name + " FROM " + CityTable.tableName +
                    " WHERE " + CityTable.name + " = ? AND " + CityTable.country + " IN (SELECT " + CountryTable.id + " FROM "
                    + CountryTable.tableName + " WHERE " + CountryTable.name + " = ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, country);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(resultSet.getString(CityTable.name));
            }
            return Optional.empty();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getCouriersInCountry(String country) {
        List<String> couriers = new ArrayList<>();

        try (Connection connection = getConnection()) {
            String query = "SELECT " + CourierTable.tableName + "." + CourierTable.username + " FROM " + CourierTable.tableName
                    + " JOIN " + CityTable.tableName + " ON " + CourierTable.tableName + "." + CourierTable.city + " = " + CityTable.tableName + "." + CityTable.name
                    + " JOIN " + CountryTable.tableName + " ON " + CityTable.tableName + "." + CityTable.country + " = " + CountryTable.tableName + "." + CountryTable.id
                    + " WHERE " + CountryTable.tableName + "." + CountryTable.name + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, country);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                couriers.add(resultSet.getString(CourierTable.username));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return couriers;
    }

    public List<String> getCouriersByCity(String city, String country) {
        List<String> couriers = new ArrayList<>();

        try (Connection connection = getConnection()) {
            String query = "SELECT " + CourierTable.tableName + "." + CourierTable.username + " FROM " + CourierTable.tableName
                    + " JOIN " + CityTable.tableName + " ON " + CourierTable.tableName + "." + CourierTable.city + " = " + CityTable.tableName + "." + CityTable.name
                    + " JOIN " + CountryTable.tableName + " ON " + CityTable.tableName + "." + CityTable.country + " = " + CountryTable.tableName + "." + CountryTable.id
                    + " WHERE " + CountryTable.tableName + "." + CountryTable.name + " = ? AND "
                    + CityTable.tableName + "." + CityTable.name + " = ?" + " LIMIT 6";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, country);
            preparedStatement.setString(2, city);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                couriers.add(resultSet.getString(CourierTable.username));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return couriers;
    }

    public Optional<String> getCourierByUsername(String username, String country) {
        Optional<String> courier = Optional.empty();

        try (Connection connection = getConnection()) {
            String query = "SELECT " + CourierTable.tableName + "." + CourierTable.username + " FROM " + CourierTable.tableName
                    + " JOIN " + CityTable.tableName + " ON " + CourierTable.tableName + "." + CourierTable.city + " = " + CityTable.tableName + "." + CityTable.name
                    + " JOIN " + CountryTable.tableName + " ON " + CityTable.tableName + "." + CityTable.country + " = " + CountryTable.tableName + "." + CountryTable.id
                    + " WHERE " + CountryTable.tableName + "." + CountryTable.name + " = ? AND "
                    + CourierTable.tableName + "." + CourierTable.username + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, country);
            preparedStatement.setString(2, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                courier = Optional.of(resultSet.getString(CourierTable.username));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return courier;
    }

    public List<Parcel> getParcelsToBeSentInCity(String city) {
        List<Parcel> parcels = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM " + ParcelsTable.tableName + " JOIN " + CityTable.tableName + " ON " +
                    ParcelsTable.tableName + "." + ParcelsTable.from + " = " + CityTable.tableName + "." + CityTable.id +
                    " JOIN " + CityTable.tableName + " l1 " + " ON " + " l1." + CityTable.id + " = " + ParcelsTable.tableName + "." + ParcelsTable.to +
                    " WHERE " + CityTable.tableName + "." + CityTable.name + " = ? AND "
                    + ParcelsTable.tableName + "." + ParcelsTable.courier + " IS NULL AND " + ParcelsTable.tableName + "." + ParcelsTable.delivered + " = false";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, city);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(ParcelsTable.id);
                String name = resultSet.getString(ParcelsTable.name);
                String from = resultSet.getString(ParcelsTable.from);
                String to = resultSet.getString("l1." + CityTable.name);
                float weight = resultSet.getFloat(ParcelsTable.weight);
                parcels.add(new Parcel(id, name, from, to, weight));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return parcels;
    }

    public List<String> getIssuedParcelsInCity(String city) {
        List<String> parcels = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM " + ParcelsTable.tableName +
                    " JOIN " + CityTable.tableName + " ON " + ParcelsTable.tableName + "." + ParcelsTable.to + " = " + CityTable.tableName + "." + CityTable.id +
                    " WHERE " + CityTable.tableName + "." + CityTable.name + " = ? AND "
                    + ParcelsTable.tableName + "." + ParcelsTable.delivered + " = true";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, city);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                parcels.add(resultSet.getString(ParcelsTable.tableName + "." + ParcelsTable.name));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return parcels;
    }

    public boolean addNewParcel(Parcel parcel) {
        try (Connection connection = getConnection()) {
            String from = "(SELECT " + CityTable.id + " from " + CityTable.tableName +
                    " WHERE " + CityTable.tableName + "." + CityTable.name + " = '" + parcel.from() + "')";

            String to = "(SELECT " + CityTable.id + " from " + CityTable.tableName +
                    " WHERE " + CityTable.tableName + "." + CityTable.name + " = '" + parcel.to() + "')";

            String query = "INSERT INTO " + ParcelsTable.tableName + "(" + ParcelsTable.name + ", `" + ParcelsTable.from + "`, `" +
                    ParcelsTable.to + "`, " + ParcelsTable.weight + ") VALUES (?, " + from + ", " + to + ", ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, parcel.name());
            preparedStatement.setFloat(2, parcel.weight());

            preparedStatement.execute();
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<String> getParcelByCode(String city, String code) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM " + ParcelsTable.tableName +
                    " JOIN " + CityTable.tableName + " ON " + ParcelsTable.tableName + "." + ParcelsTable.to + " = " + CityTable.tableName + "." + CityTable.id +
                    " WHERE " + CityTable.tableName + "." + CityTable.name + " = ? AND "
                    + ParcelsTable.tableName + "." + ParcelsTable.delivered + " = true AND " +
                    ParcelsTable.tableName + "." + ParcelsTable.name + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(resultSet.getString(ParcelsTable.tableName + "." + ParcelsTable.name));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public void removeParcel(String parcelCode) {
        try (Connection connection = getConnection()) {
            String query = "DELETE FROM " + ParcelsTable.tableName + " WHERE " + ParcelsTable.name + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, parcelCode);

            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getCouriersAtCurrentCity(String city) {
        List<String> couriers = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String query = "SELECT c." + CourierTable.username + " FROM " + CourierTable.tableName + " c" +
                    " JOIN " + CityTable.tableName + " ci ON " + "c." + CourierTable.currentX + " = ci." + CityTable.coordinateX +
                    " AND c." + CourierTable.currentY + " = ci." + CityTable.coordinateY +
                    " WHERE ci." + CityTable.name + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, city);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                couriers.add(resultSet.getString(CourierTable.username));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return couriers;
    }

    public void attachParcelToCourier(String parcelCode, String courierUsername) {
        try (Connection connection = getConnection()) {
            String queryForCourierID = "(SELECT " + CourierTable.id + " FROM " + CourierTable.tableName + " WHERE " + CourierTable.username + " = ?)";

            String updateQuery = "UPDATE " + ParcelsTable.tableName + " SET " + ParcelsTable.courier + " = " + queryForCourierID +
                    " WHERE " + ParcelsTable.name + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, courierUsername);
            preparedStatement.setString(2, parcelCode);

            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Courier getCourierInfo(String username) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM " + CourierTable.tableName + " WHERE " + CourierTable.username + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new Courier(resultSet.getString(CourierTable.firstname),
                        resultSet.getString(CourierTable.lastname),
                        username,
                        resultSet.getDate(CourierTable.dateOfBirth),
                        resultSet.getString(CourierTable.city),
                        resultSet.getString(CourierTable.gender));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String getCourierCurrentCity(String username) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM " + CourierTable.tableName +
                    " JOIN " + CityTable.tableName + " ON " + CityTable.coordinateX + " = " + CourierTable.currentX + " AND " + CityTable.coordinateY + " = " + CourierTable.currentY +
                    " WHERE " + CourierTable.username + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String destination = resultSet.getString(CourierTable.destination);
                if (destination == null) {
                    return resultSet.getString(CityTable.name);
                } else {
                    if (destination.equals(resultSet.getString(CityTable.name))) {
                        return "Arrived in " + resultSet.getString(CityTable.name);
                    }
                }
            }

            String query1 = "SELECT * FROM " + CourierTable.tableName + " WHERE " + CourierTable.username + " = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
            preparedStatement1.setString(1, username);

            ResultSet resultSet1 = preparedStatement1.executeQuery();

            if (resultSet1.next()) {
                String destination = resultSet1.getString(CourierTable.destination);
                if (destination != null) {
                    return "On the way to " + destination;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "Error";
    }

    public int getNumberOfParcels(String username) {
        try (Connection connection = getConnection()) {
            String query = "SELECT COUNT(*) FROM " + ParcelsTable.tableName + " WHERE " + ParcelsTable.courier +
                    " = (SELECT " + CourierTable.id + " FROM " + CourierTable.tableName + " WHERE " + CourierTable.username + " = ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public float getWeightOfAllParcelsAtCourier(String username) {
        try (Connection connection = getConnection()) {
            String query = "SELECT SUM(" + ParcelsTable.weight + ") FROM " + ParcelsTable.tableName + " WHERE " + ParcelsTable.courier +
                    " = (SELECT " + CourierTable.id + " FROM " + CourierTable.tableName + " WHERE " + CourierTable.username + " = ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getFloat(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public List<String> getCourierParcelDestinations(String username) {
        List<String> cities = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM " + ParcelsTable.tableName + " p" +
                    " JOIN " + CityTable.tableName + " ci ON " + " p." + ParcelsTable.to + " = ci." + CityTable.id +
                    " JOIN " + CourierTable.tableName + " c ON " + " p." + ParcelsTable.courier + " = c." + CourierTable.id +
                    " WHERE c." + CourierTable.username +  " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cities.add(resultSet.getString("ci." + CityTable.name));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return cities;
    }

    public void sendCourier(String username, String country) {
        try (Connection connection = getConnection()) {
            String query = "UPDATE " + CourierTable.tableName + " SET " + CourierTable.destination + " = ?" +
                    " WHERE " + CourierTable.username + " = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, country);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<City> getCountryCities(String country) {
        List<City> cities = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM " + CityTable.tableName + " WHERE " + CityTable.country + " = (SELECT " +
                    CountryTable.id + " FROM " + CountryTable.tableName + " WHERE " + CountryTable.name + " = ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, country);

            ResultSet resultSet = preparedStatement.executeQuery();

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
}
