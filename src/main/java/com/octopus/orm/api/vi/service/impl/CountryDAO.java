package com.octopus.orm.api.vi.service.impl;

import com.octopus.orm.api.vi.pojo.ApiV1Country;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class CountryDAO {
    private final String URL;
    private final String USER;
    private final String PASSWORD;

    public CountryDAO() {
        Properties properties = new Properties();
        // Load database properties
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            properties.load(input);
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");
            createCountryTableIfNotExists(); // Ensure the table exists
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void createCountryTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS countries (" +
                "country_id SERIAL PRIMARY KEY, " +
                "country_code VARCHAR(10) NOT NULL UNIQUE, " +
                "country_name VARCHAR(100) NOT NULL" +
                ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Countries table is ready.");
        } catch (SQLException e) {
            System.err.println("Error while creating countries table: " + e.getMessage());
        }
    }

    public ApiV1Country addCountry(ApiV1Country country) {
        String sql = "INSERT INTO countries (country_code, country_name) VALUES (?, ?) RETURNING country_id;";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, country.getCountryCode());
            pstmt.setString(2, country.getCountryName());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                country.setCountryId(rs.getInt("country_id")); // Set the generated ID
            }
        } catch (SQLException e) {
            System.err.println("Error adding country: " + e.getMessage());
        }
        return country;
    }

    public ArrayList<ApiV1Country> getAllCountries() {
        ArrayList<ApiV1Country> countries = new ArrayList<>();
        String sql = "SELECT * FROM countries";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ApiV1Country country = new ApiV1Country();
                country.setCountryId(rs.getInt("country_id"));
                country.setCountryCode(rs.getString("country_code"));
                country.setCountryName(rs.getString("country_name"));
                countries.add(country);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching countries: " + e.getMessage());
        }
        return countries;
    }

    public ApiV1Country updateCountry(ApiV1Country country) {
        String sql = "UPDATE countries SET country_code = ?, country_name = ? WHERE country_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, country.getCountryCode());
            pstmt.setString(2, country.getCountryName());
            pstmt.setInt(3, country.getCountryId());

            pstmt.executeUpdate(); // Update doesn't return anything
        } catch (SQLException e) {
            System.err.println("Error updating country: " + e.getMessage());
        }
        return country;
    }

    public ApiV1Country getCountryByName(String name) {
        String sql = "SELECT * FROM countries WHERE country_name = ?";
        ApiV1Country country = null;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                country = new ApiV1Country();
                country.setCountryId(rs.getInt("country_id"));
                country.setCountryCode(rs.getString("country_code"));
                country.setCountryName(rs.getString("country_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching country by name: " + e.getMessage());
        }
        return country;
    }

    public ApiV1Country getCountryByCode(String code) {
        String sql = "SELECT * FROM countries WHERE country_code = ?";
        ApiV1Country country = null;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                country = new ApiV1Country();
                country.setCountryId(rs.getInt("country_id"));
                country.setCountryCode(rs.getString("country_code"));
                country.setCountryName(rs.getString("country_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching country by code: " + e.getMessage());
        }
        return country;
    }
}