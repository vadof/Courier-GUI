package com.example.gui.courier;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;

public class Courier {

    private final String firstname;
    private final String lastname;
    private final String username;
    private final int age;
    private final String city;
    private final String gender;

    public Courier(String firstname, String lastname, String username, Date date, String city, String gender) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.age = calculateAge(date);
        this.city = city;
        this.gender = gender;
    }

    private int calculateAge(Date date) {
        LocalDate birthDate = date.toLocalDate();
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthDate, currentDate);
        return period.getYears();
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUsername() {
        return username;
    }

    public int getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }

    public String getGender() {
        return gender;
    }
}
