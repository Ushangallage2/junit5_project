package com.octopus.orm.api.vi.pojo;

import com.octopus.orm.api.vi.pojo.CountryServiceImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/1.0/country", new CountryHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8000");
    }

    // The CountryHandler class that interfaces with the CountryServiceImpl
    static class CountryHandler implements HttpHandler {
        private final CountryServiceImpl countryService;
        private final Gson gson;

        public CountryHandler() {
            this.countryService = new CountryServiceImpl(); // Initialize the service implementation
            this.gson = new Gson(); // Initialize Gson for JSON processing
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            int responseCode = 200;

            switch (exchange.getRequestMethod()) {
                case "POST":
                    if (exchange.getRequestURI().getPath().endsWith("/addCountry")) {
                        // Handle adding a country
                        ApiV1Country country = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), ApiV1Country.class);
                        response = countryService.addCountryRecord(country).toString();
                    } else if (exchange.getRequestURI().getPath().endsWith("/editCountry")) {
                        // Handle editing a country
                        ApiV1Country country = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), ApiV1Country.class);
                        response = countryService.editCountryRecord(country).toString();
                    } else {
                        responseCode = 400; // Bad Request
                        response = "Invalid POST request";
                    }
                    break;

                case "GET":
                    if (exchange.getRequestURI().getPath().endsWith("/countryName")) {
                        // Handle getting a country by name
                        String name = exchange.getRequestURI().getQuery().split("=")[1];
                        response = countryService.getCountryByName(name).toString();
                    } else if (exchange.getRequestURI().getPath().endsWith("/countryCode")) {
                        // Handle getting a country by code
                        String code = exchange.getRequestURI().getQuery().split("=")[1];
                        response = countryService.getCountryByCode(code).toString();
                    } else {
                        responseCode = 400; // Bad Request
                        response = "Invalid GET request";
                    }
                    break;

                default:
                    responseCode = 405; // Method Not Allowed
                    response = "Unsupported request method";
                    break;
            }

            // Send the response
            exchange.sendResponseHeaders(responseCode, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}